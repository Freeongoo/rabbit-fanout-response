package com.prod.rabbit.controller;

import com.prod.rabbit.dto.PriceExchange;
import com.prod.rabbit.service.ProducerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Slf4j
@RestController
public class SendController {

    @Value("${app.name}")
    private String appName;

    @Autowired
    private ProducerService producerService;

    @RequestMapping(value = "/send", method = GET, produces = APPLICATION_JSON_VALUE)
    public void send() {
        PriceExchange price = new PriceExchange(appName, "BTC", "USDT", new BigDecimal("1110.2"));
        log.info(" !!! Being send message from controller: {}", price);
        producerService.broadcastMessageASync(price);
    }
}
