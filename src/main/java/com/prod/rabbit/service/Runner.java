package com.prod.rabbit.service;

import com.prod.rabbit.dto.PriceExchange;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Lazy(false)
@Component
public class Runner {

    @Value("${app.name}")
    private String appName;

    @Autowired
    private ProducerService producerService;

    @Scheduled(fixedDelay = 10000, initialDelay = 4000)
    public void run() {
        PriceExchange price = new PriceExchange(appName, "BTC", "USDT", new BigDecimal("1110.2"));
        log.info(" !!! Being send message: {}", price);
        producerService.broadcastMessageASync(price);
    }
}
