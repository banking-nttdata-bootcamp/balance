package com.nttdata.bootcamp.controller;

import com.nttdata.bootcamp.entity.Balance;
import com.nttdata.bootcamp.service.BalanceService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = "/balance")
public class ReportController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BalanceController.class);
    @Autowired
    private BalanceService balanceService;

    @CircuitBreaker(name = "balance", fallbackMethod = "fallBackGetBalance")
    @GetMapping("/findBalanceOfMainAccount/{accountNumber}")
    public Mono<Double> findBalanceByAccount(@PathVariable("accountNumber") String accountNumber) {
        Mono<Balance> balanceMono = balanceService.findByAccountNumber(accountNumber);
        return Mono.just(balanceMono.block().getBalance());
    }

    private Mono<Balance> fallBackGetBalance(String accountNumber, RuntimeException e){
        Balance balance = new Balance();
        balance.setBalance(-10.00);
        Mono<Balance> movementsMono= Mono.just(balance);
        return movementsMono;
    }
}
