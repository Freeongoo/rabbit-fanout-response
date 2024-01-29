package com.prod.rabbit.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prod.rabbit.config.ExchangeTopic;
import com.prod.rabbit.dto.PriceExchange;
import com.prod.rabbit.dto.ResponseDto;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class ProducerService {

    @Autowired
    private RabbitTemplate template;

    @Autowired
    private ConnectionFactory connectionFactory;

    @Autowired
    private RabbitListenerEndpointRegistry rabbitListenerEndpointRegistry;

    private static final Map<String, MessageListenerContainer> REG_CUSTOM = new ConcurrentHashMap<>();

    @Autowired
    private RabbitAdmin rabbitAdmin;

    public void broadcastMessageSync(PriceExchange priceExchange) {
        //this.template.convertAndSend(ExchangeTopic.TOPIC, "", priceExchange);  // broadcasts string message to each my-queue-* via my-exchange
        ResponseDto responseDto = this.template.convertSendAndReceiveAsType(ExchangeTopic.TOPIC, "", priceExchange, new ParameterizedTypeReference<>() {});
        log.info("Response: {}", responseDto);
    }

    public void broadcastMessageASync(PriceExchange priceExchange) {
        //String replayQ = "replies";
        // создание некоего уникального идентификатора по отправляемым данным
        String replayQ = priceExchange.getBaseCoin() + "_" + priceExchange.getPostCoin() + "_" + priceExchange.getPrice();
        rabbitAdmin.declareQueue(new Queue(replayQ, false));

        MessageListenerContainer listenerContainer = REG_CUSTOM.get(replayQ);
        if (listenerContainer == null) {

            SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
            container.setListenerId(replayQ);
            container.setConnectionFactory(connectionFactory);
            container.setQueueNames(replayQ);
            container.setMessageListener(new MessageListener() {
                @SneakyThrows
                @Override
                public void onMessage(Message message) {
                    byte[] body = message.getBody();
                    ObjectMapper objectMapper = new ObjectMapper();
                    ResponseDto responseDto = objectMapper.readValue(body, ResponseDto.class);
                    log.info("On reply handle income: " + responseDto);
                }
            });
            container.start();
            REG_CUSTOM.put(replayQ, container);
        }

        //TODO:
        // 1. нужно же после обработки грохать созданные container
        // 2. как понять что все ожидаемые были обработаны

        this.template.convertAndSend(ExchangeTopic.TOPIC, "", priceExchange, message -> {
            message.getMessageProperties().setReplyTo(replayQ);
            return message;
        });
    }

//    @Bean
//    public Queue replies() {
//        return new Queue("replies", false);
//    }
//
//    @RabbitListener(queues = "replies")
//    public void replyHandler(ResponseDto responseDto) {
//        log.info("On reply handle income: " + responseDto);
//    }
}
