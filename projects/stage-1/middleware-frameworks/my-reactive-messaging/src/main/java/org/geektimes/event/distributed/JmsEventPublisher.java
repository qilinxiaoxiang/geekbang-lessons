package org.geektimes.event.distributed;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.geektimes.event.EventListener;
import org.geektimes.event.reactive.stream.ListenerSubscriberAdapter;
import org.geektimes.reactive.streams.SimplePublisher;

import javax.jms.*;
import java.util.Date;
import java.util.EventObject;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author: 项峥
 * @Date: 2021/9/1 23:50
 */
public class JmsEventPublisher {
    private final SimplePublisher<EventObject> simplePublisher;

    private final Connection connection;

    private final ExecutorService executorService;

    public JmsEventPublisher(String uri) throws JMSException {
        simplePublisher = new SimplePublisher();
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(ActiveMQConnectionFactory.DEFAULT_USER,
                ActiveMQConnectionFactory.DEFAULT_PASSWORD, uri);
        connection = connectionFactory.createConnection();
        connection.start();
        Session session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
        Destination destination = session.createQueue("TestQueue");
        MessageProducer producer = session.createProducer(destination);
        producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
        MessageConsumer consumer = session.createConsumer(destination);

        // Build-in listener
        addEventListener(event -> {
            if (event instanceof DistributedEventObject) {
                // Event -> Pub/Sub
                producer.send(session.createTextMessage((String) event.getSource()));
                session.commit();
            }
        });

        this.executorService = Executors.newSingleThreadExecutor();

        executorService.execute(() -> {
            try {
                Message message = consumer.receive();
                publish(new EventObject(message));
            } catch (JMSException e) {
                e.printStackTrace();
            }
        });
    }

    public void publish(Object event) {
//        simplePublisher.publish(new EventObject(event));
        simplePublisher.publish(new DistributedEventObject(event));
    }

    private void publish(EventObject event) {
//        simplePublisher.publish(new EventObject(event));
        simplePublisher.publish(event);
    }

    public void addEventListener(EventListener eventListener) {
        simplePublisher.subscribe(new ListenerSubscriberAdapter(eventListener));
    }

    public void close() {
        try {
            connection.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }
        executorService.shutdown();
    }

    public static void main(String[] args) throws JMSException {
        JmsEventPublisher eventPublisher = new JmsEventPublisher("tcp://localhost:61616");

        // Publish Event
        eventPublisher.publish(String.valueOf(new Date()));

        eventPublisher.close();
    }
}
