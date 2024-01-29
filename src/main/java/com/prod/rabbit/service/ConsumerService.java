package com.prod.rabbit.service;

import com.prod.rabbit.dto.PriceExchange;
import com.prod.rabbit.dto.ResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ConsumerService {

    @Autowired
    private RabbitTemplate template;

    @Bean
    public MessageListenerAdapter listenerAdapter(ConsumerService consumerService, MessageConverter messageConverter) {
        MessageListenerAdapter listenerAdapter = new MessageListenerAdapter(consumerService, messageConverter);
        listenerAdapter.setDefaultListenerMethod("handleEndResponse");     // see below
        return listenerAdapter;
    }

    public ResponseDto handleEndResponse(PriceExchange message) {
        log.info("Income message: " + message);
        //if (true) throw new RuntimeException("throw");
        return new ResponseDto(200, message.toString());
    }

    public void handle(PriceExchange message) {
        log.info("Income message: " + message);
    }
}
