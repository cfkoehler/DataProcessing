package DpFeeder.ProcessingFeeder.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import DpFeeder.ProcessingFeeder.model.SessionMessage;
import DpFeeder.ProcessingFeeder.service.RabbitMQSender;

@RestController
@RequestMapping(value = "/DpFeeder-rabbitmq/")
public class RabbitMQWebController {

    @Autowired
    RabbitMQSender rabbitMQSender;

    @GetMapping(value = "/producer")
    public String producer(@RequestParam("filePath") String filepath,@RequestParam("offset") String offset) {

        SessionMessage sessionMessage = new SessionMessage();
        sessionMessage.setFilePath(filepath);
        sessionMessage.setOffset(offset);
        rabbitMQSender.send(sessionMessage);

        return "Message sent to the RabbitMQ DpFeeder Successfully";
    }

}
