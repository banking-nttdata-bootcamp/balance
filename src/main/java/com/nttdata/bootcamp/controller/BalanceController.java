package com.nttdata.bootcamp.controller;

import com.nttdata.bootcamp.entity.Balance;
import com.nttdata.bootcamp.entity.dto.BalanceDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.nttdata.bootcamp.service.BalanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.CoreSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.Date;
import javax.validation.Valid;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = "/balance")
public class BalanceController {

	private static final Logger LOGGER = LoggerFactory.getLogger(BalanceController.class);
	private static final String CIRCUIT_NAME="balance";
	@Autowired
	private BalanceService balanceService;


	//Balance search
	@GetMapping("/findBalanceByAccount/{accountNumber}")
	public Flux<Balance> finAllBalanceByAccount(@PathVariable("accountNumber") String accountNumber) {
		Flux<Balance> movementsFlux = balanceService.findByAccountNumber(accountNumber);
		LOGGER.info("Registered balance: " + movementsFlux);
		return movementsFlux;
	}

	//Balance by AccountNumber
	@CircuitBreaker(name = CIRCUIT_NAME, fallbackMethod = "fallBackGetBalance")
	@GetMapping("/findLastBalanceByAccount/{accountNumber}")
	public Mono<Balance> findBalanceByAccount(@PathVariable("accountNumber") String accountNumber) {
		Mono<Balance> balanceMono = balanceService.findLastBalanceByAccountNumber(accountNumber);
		LOGGER.info("Registered last balance of account number: "+accountNumber +"-" + balanceMono.block().getBalance());
		return balanceMono;
	}

	//Balance by customer

	//Save balance
	@PostMapping(value = "/saveBalance")
	public Mono<Balance> saveBalance(@RequestBody BalanceDto dataBalance){
		Balance balance= new Balance();
		Mono.just(balance).doOnNext(t -> {
					t.setBalance(dataBalance.getBalance());
					t.setDni(dataBalance.getDni());
					t.setAccountNumber(dataBalance.getAccountNumber());
					t.setCreationDate(new Date());
					t.setModificationDate(new Date());

				}).onErrorReturn(balance).onErrorResume(e -> Mono.just(balance))
				.onErrorMap(f -> new InterruptedException(f.getMessage())).subscribe(x -> LOGGER.info(x.toString()));

		Mono<Balance> movementsMono = balanceService.saveBalance(balance);
		return movementsMono;
	}

	//Update balance

	private Mono<Balance> fallBackGetBalance(@PathVariable("accountNumber") String accountNumber,Exception e){
		Balance balance= new Balance();
		balance.setBalance(-10.00);
		return Mono.just(balance);
		//return  Mono.<Balance>error(new Error("El cuenta bancaria " + accountNumber+ " no existe"));

	}




}
