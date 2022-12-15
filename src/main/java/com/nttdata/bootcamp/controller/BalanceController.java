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
	@GetMapping("/")
	public Flux<Balance> finAllBalance() {
		Flux<Balance> movementsFlux = balanceService.findAll();
		LOGGER.info("Registered balance: " + movementsFlux);
		return movementsFlux;
	}

	//Balance by AccountNumber
	@CircuitBreaker(name = CIRCUIT_NAME, fallbackMethod = "fallBackGetBalance")
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
	@PostMapping(value = "/saveOpeningBalance")
	public Mono<Balance> saveOpeningBalance(@RequestBody BalanceDto dataBalance){
		Balance balance= new Balance();
		Mono.just(balance).doOnNext(t -> {
					t.setBalance(dataBalance.getBalance());
					t.setDni(dataBalance.getDni());
					t.setAccountNumber(dataBalance.getAccountNumber());
					t.setBalance(dataBalance.getBalance());
					t.setCreationDate(new Date());
					t.setModificationDate(new Date());

				}).onErrorReturn(balance).onErrorResume(e -> Mono.just(balance))
				.onErrorMap(f -> new InterruptedException(f.getMessage())).subscribe(x -> LOGGER.info(x.toString()));

		Mono<Balance> movementsMono = balanceService.saveBalance(balance);
		return movementsMono;
	}

	//Update balance
	@PutMapping("/updateBalanceCredit")
	public Mono<Balance> updateBalanceCredit(@Valid @RequestBody BalanceDto dataBalance) {

		Balance dataCurrentAccount = new Balance();
		Double balance = dataBalance.getBalance();

		Mono.just(dataCurrentAccount).doOnNext(t -> {
					t.setAccountNumber(dataBalance.getAccountNumber());
					t.setBalance(dataBalance.getBalance());
					t.setModificationDate(new Date());
				}).onErrorReturn(dataCurrentAccount).onErrorResume(e -> Mono.just(dataCurrentAccount))
				.onErrorMap(f -> new InterruptedException(f.getMessage())).subscribe(x -> LOGGER.info(x.toString()));

		Mono<Balance> updateBalance = balanceService.updateBalance(dataCurrentAccount,"CREDIT");
		return updateBalance;

	}
	@PutMapping("/updateBalanceDebit")
	public Mono<Balance> updateBalanceDebit(@Valid @RequestBody BalanceDto dataBalance) {

		Balance dataCurrentAccount = new Balance();
		Double balance = dataBalance.getBalance();

		Mono.just(dataCurrentAccount).doOnNext(t -> {
					t.setAccountNumber(dataBalance.getAccountNumber());
					t.setBalance(dataBalance.getBalance());
					t.setModificationDate(new Date());
				}).onErrorReturn(dataCurrentAccount).onErrorResume(e -> Mono.just(dataCurrentAccount))
				.onErrorMap(f -> new InterruptedException(f.getMessage())).subscribe(x -> LOGGER.info(x.toString()));

		Mono<Balance> updateBalance = balanceService.updateBalance(dataCurrentAccount,"DEBIT");
		return updateBalance;

	}


	private Mono<Balance> fallBackGetBalance(@PathVariable("accountNumber") String accountNumber,Exception e){
		Balance balance= new Balance();
		balance.setBalance(-10.00);
		return Mono.just(balance);
		//return  Mono.<Balance>error(new Error("El cuenta bancaria " + accountNumber+ " no existe"));

	}




}
