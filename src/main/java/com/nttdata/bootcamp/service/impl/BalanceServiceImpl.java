package com.nttdata.bootcamp.service.impl;

import com.nttdata.bootcamp.entity.Balance;
import com.nttdata.bootcamp.repository.BalanceRepository;
import com.nttdata.bootcamp.service.BalanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.Date;

//Service implementation
@Service
public class BalanceServiceImpl implements BalanceService {
    @Autowired
    private BalanceRepository balanceRepository;

    @Override
    public Flux<Balance> findAll() {
        Flux<Balance> balanceFlux = balanceRepository.findAll();
        return balanceFlux;
    }

    @Override
    public Flux<Balance> findByAccountNumber(String accountNumber) {
        Flux<Balance> balanceFlux = balanceRepository
                .findAll()
                .filter(x -> x.getAccountNumber().equals(accountNumber));
        return balanceFlux;
    }

    @Override
    public Mono<Balance> findLastBalanceByAccountNumber(String accountNumber) {
        Mono<Balance> balanceMono = balanceRepository
                .findAll()
                .filter(x -> x.getAccountNumber().equals(accountNumber)  )
                .sort((y,x) -> x.getCreationDate().compareTo(y.getCreationDate())).next();

        return balanceMono;
    }

    @Override
    public Mono<Balance> saveBalance(Balance dataBalance) {

        return balanceRepository.save(dataBalance);
    }



    @Override
    public Mono<Void> deleteBalance(String Number) {
        Mono<Balance> movementsMono = findLastBalanceByAccountNumber(Number);
        try {
            Balance balance = movementsMono.block();
            return balanceRepository.delete(balance);
        }
        catch (Exception e){
            return Mono.<Void>error(new Error("The balance of the account" + Number+ " do not exists"));
        }
    }


}
