package DpFeeder.ProcessingFeeder.service;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import DpFeeder.ProcessingFeeder.model.SessionMessage;

@Service
public class RabbitMQSender {

    @Autowired
    private AmqpTemplate rabbitTemplate;

    @Value("${javainuse.rabbitmq.exchange}")
    private String exchange;

    @Value("${javainuse.rabbitmq.routingkey}")
    private String routingkey;

    public void send(SessionMessage sessionMessage) {
        rabbitTemplate.convertAndSend(exchange, routingkey, sessionMessage);
        System.out.println("Send msg = " + sessionMessage);

    }
}