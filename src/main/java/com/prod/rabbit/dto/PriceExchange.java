package com.prod.rabbit.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class PriceExchange implements Serializable {
    String app;
    String baseCoin;
    String postCoin;
    BigDecimal price;
}
