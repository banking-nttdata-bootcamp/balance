package com.nttdata.bootcamp.service;

import com.nttdata.bootcamp.entity.Balance;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

//Interface Service
public interface BalanceService {

    public Flux<Balance> findAll();
    public Flux<Balance> findByAccountNumber(String accountNumber);
    public Mono<Balance> findLastBalanceByAccountNumber(String accountNumber);
    public Mono<Balance> saveBalance(Balance balance);
    public Mono<Void> deleteBalance(String accountNumber);




}
