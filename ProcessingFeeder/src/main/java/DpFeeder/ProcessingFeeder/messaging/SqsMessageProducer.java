package DpFeeder.ProcessingFeeder.messaging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class SqsMessageProducer {

    private final QueueMessagingTemplate queueMessagingTemplate;

    @Value("${objects.queue.name")
    private String objectsQueue;

    @Autowired
    public SqsMessageProducer(QueueMessagingTemplate queueMessagingTemplate) {
        this.queueMessagingTemplate = queueMessagingTemplate;
    }

    public <T> void send(T message, Map<String, Object> headers) {
        if (message == null) {
            log.warn("SQS Producer can't produce 'null' value");
            return;
        }

        log.debug("Message {}", message);
        log.debug("Queue name {}", objectsQueue);

        queueMessagingTemplate.convertAndSend(objectsQueue, message, headers);
    }

}
