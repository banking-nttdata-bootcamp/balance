package com.nttdata.bootcamp.service.impl;

import com.nttdata.bootcamp.entity.Balance;
import com.nttdata.bootcamp.entity.dto.MovementKafkaDto;
import com.nttdata.bootcamp.events.EventKafka;
import com.nttdata.bootcamp.events.MovementCreatedEventKafka;
import com.nttdata.bootcamp.repository.BalanceRepository;
import com.nttdata.bootcamp.service.BalanceService;
import com.nttdata.bootcamp.service.KafkaService;
import com.nttdata.bootcamp.util.Constant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
public class KafkaServiceImpl implements KafkaService {

    @Autowired
    private BalanceRepository balanceRepository;
    @Autowired
    private BalanceService balanceService;

    @KafkaListener(
            topics = "${topic.customer.name:topic_movement}",
            containerFactory = "kafkaListenerContainerFactory",
            groupId = "grupo1")
    public void consumerBalanceSave(EventKafka<?> eventKafka) {
        if (eventKafka.getClass().isAssignableFrom(MovementCreatedEventKafka.class)) {
            MovementCreatedEventKafka customerCreatedEvent = (MovementCreatedEventKafka) eventKafka;
            log.info("Received Data created event .... with Id={}, data={}",
                    customerCreatedEvent.getId(),
                    customerCreatedEvent.getData().toString());
            MovementKafkaDto KafkaDto = ((MovementCreatedEventKafka) eventKafka).getData();
            Double lastbalance=balanceService.findLastBalanceByAccountNumber(KafkaDto.getAccountNumber()).block().getBalance();
            Balance balance = new Balance();

            balance.setDni(KafkaDto.getDni());
            balance.setAccountNumber(KafkaDto.getAccountNumber());
            balance.setBalance(lastbalance+KafkaDto.getAmount());
            balance.setCreationDate(new Date());
            balance.setModificationDate(new Date());

            this.balanceRepository.save(balance).subscribe();
        }
    }


}
