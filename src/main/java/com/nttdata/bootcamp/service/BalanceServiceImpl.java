package com.nttdata.bootcamp.service;

import com.nttdata.bootcamp.entity.Balance;
import com.nttdata.bootcamp.repository.BalanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
    public Mono<Balance> findByAccountNumber(String accountNumber) {
        Mono<Balance> balanceMono = balanceRepository
                .findAll()
                .filter(x -> x.getAccountNumber().equals(accountNumber)).next();
        return balanceMono;
    }

    @Override
    public Flux<Balance> findByBalanceByCustomer(String customer) {
        Flux<Balance> balanceFlux = balanceRepository
                .findAll()
                .filter(x -> x.getDni().equals(customer));
        return balanceFlux;
    }

    @Override
    public Mono<Balance> saveBalance(Balance dataBalance) {
        return balanceRepository.save(dataBalance);
    }


    @Override
    public Mono<Balance> updateBalance(Balance dataBalance) {

        Mono<Balance> transactionMono = findByAccountNumber(dataBalance.getAccountNumber());
        try {
            dataBalance.setDni(transactionMono.block().getDni());
            dataBalance.setBalance(transactionMono.block().getBalance());
            dataBalance.setAccountNumber(transactionMono.block().getAccountNumber());
            dataBalance.setCreationDate(transactionMono.block().getCreationDate());
            return balanceRepository.save(dataBalance);
        }catch (Exception e){
            return Mono.<Balance>error(new Error("The balance of the account " + dataBalance.getAccountNumber() + " do not exists"));
        }
    }

    @Override
    public Mono<Void> deleteBalance(String Number) {
        Mono<Balance> movementsMono = findByAccountNumber(Number);
        try {
            Balance balance = movementsMono.block();
            return balanceRepository.delete(balance);
        }
        catch (Exception e){
            return Mono.<Void>error(new Error("The balance of the account" + Number+ " do not exists"));
        }
    }


}
