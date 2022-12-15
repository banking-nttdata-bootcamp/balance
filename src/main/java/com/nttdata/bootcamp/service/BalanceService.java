package com.nttdata.bootcamp.service;

import com.nttdata.bootcamp.entity.Balance;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

//Interface Service
public interface BalanceService {

    public Flux<Balance> findAll();
    public Mono<Balance> findByAccountNumber(String accountNumber);
    public Flux<Balance> findByBalanceByCustomer(String customer);
    public Mono<Balance> saveBalance(Balance balance);
    public Mono<Balance> updateBalance(Balance balance,String type);
    public Mono<Void> deleteBalance(String accountNumber);




}
