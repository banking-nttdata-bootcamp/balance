package com.nttdata.bootcamp.controller;

import com.nttdata.bootcamp.entity.Balance;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.nttdata.bootcamp.service.BalanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.Date;
import javax.validation.Valid;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = "/balance")
public class BalanceController {

	private static final Logger LOGGER = LoggerFactory.getLogger(BalanceController.class);
	@Autowired
	private BalanceService balanceService;


	//Balance search
	@GetMapping("/")
	public Flux<Balance> finAllBalance() {
		Flux<Balance> movementsFlux = balanceService.findAll();
		LOGGER.info("Registered balance: " + movementsFlux);
		return movementsFlux;
	}

	//Balance by AccountNumber
	@GetMapping("/findBalanceByAccount/{accountNumber}")
	public Mono<Balance> findBalanceByAccount(@PathVariable("accountNumber") String accountNumber) {
		Mono<Balance> balanceMono = balanceService.findByAccountNumber(accountNumber);
		LOGGER.info("Registered balance of account number: "+accountNumber +"-" + balanceMono);
		return balanceMono;
	}

	//Balance by customer
	@GetMapping("/findAllBalanceOfCustomer/{customer}")
	public Flux<Balance> findAllBalanceOfCustomer(@PathVariable("customer") String customer) {
		Flux<Balance> balanceFlux = balanceService.findByBalanceByCustomer(customer);
		LOGGER.info("List balances of account by customer: "+customer +"-" + balanceFlux);
		return balanceFlux;
	}

	//Save balance
	@CircuitBreaker(name = "balance", fallbackMethod = "fallBackGetBalance")
	@PostMapping(value = "/saveBalance")
	public Mono<Balance> saveBalance(@RequestBody Balance dataBalance){
		Mono.just(dataBalance).doOnNext(t -> {

					t.setCreationDate(new Date());
					t.setModificationDate(new Date());

				}).onErrorReturn(dataBalance).onErrorResume(e -> Mono.just(dataBalance))
				.onErrorMap(f -> new InterruptedException(f.getMessage())).subscribe(x -> LOGGER.info(x.toString()));

		Mono<Balance> movementsMono = balanceService.saveBalance(dataBalance);
		return movementsMono;
	}

	//Update balance
	@CircuitBreaker(name = "balance", fallbackMethod = "fallBackGetBalance")
	@PutMapping("/updateBalance/{numberAccount}/{mount}")
	public Mono<Balance> updateBalance(@PathVariable("numberAccount") String numberAccount,
									   @PathVariable("mount") Double mount) {
		Balance balanceMono= findBalanceByAccount(numberAccount).block();
		balanceMono.setBalance(mount);
		balanceMono.setModificationDate(new Date());
		Mono<Balance> updateTransfer = balanceService.updateBalance(balanceMono);
		return updateTransfer;

	}


	private Mono<Balance> fallBackGetMovement(Exception e){
		Balance balance = new Balance();
		balance.setBalance(-10.00);
		Mono<Balance> movementsMono= Mono.just(balance);
		return movementsMono;
	}




}
