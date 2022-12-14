package com.nttdata.bootcamp.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BalanceDto {
    private String dni;
    private String accountNumber;
    private Double balance;
}
