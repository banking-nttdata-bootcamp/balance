package com.nttdata.bootcamp.controller;

import com.nttdata.bootcamp.entity.Balance;
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
	@PutMapping("/updateBalance/{numberAccount}/{mount}/{type}")
	public Mono<Balance> updateBalance(@PathVariable("numberAccount") String numberAccount,
									   @PathVariable("balance") Double balance,
									   @PathVariable("mount") Double mount,
									   @PathVariable("flagType") String flagType) {
		Balance balanceMono= findBalanceByAccount(numberAccount).block();
		if (flagType.equals("Debit")){
			balance=balance-mount;
		}
		if (flagType.equals("credit")){
			balance=balance+mount;
		}
		balanceMono.setBalance(balance);
		balanceMono.setModificationDate(new Date());
		Mono<Balance> updateTransfer = balanceService.updateBalance(balanceMono);
		return updateTransfer;

	}


	private Mono<Balance> fallBackGetBalance(@PathVariable("accountNumber") String accountNumber,Exception e){
		Balance balance= new Balance();
		balance.setBalance(-10.00);
		return Mono.just(balance);
		//return  Mono.<Balance>error(new Error("El cuenta bancaria " + accountNumber+ " no existe"));

	}




}
