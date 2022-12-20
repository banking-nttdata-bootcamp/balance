package com.nttdata.bootcamp.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovementKafkaDto {

        private String dni;
        private String accountNumber;
        private String movementNumber;
        private Double amount;
}
