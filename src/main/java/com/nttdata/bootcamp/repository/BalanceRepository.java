package com.nttdata.bootcamp.repository;

import com.nttdata.bootcamp.entity.Balance;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

//Mongodb Repository
public interface BalanceRepository extends ReactiveCrudRepository<Balance, String> {
}
