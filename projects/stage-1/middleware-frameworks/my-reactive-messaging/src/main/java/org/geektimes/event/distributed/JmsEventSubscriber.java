package org.geektimes.event.distributed;

import javax.jms.JMSException;

/**
 * @Author: 项峥
 * @Date: 2021/9/2 0:07
 */
public class JmsEventSubscriber {
    public static void main(String[] args) throws JMSException {
        JmsEventPublisher eventPublisher = new JmsEventPublisher("tcp://localhost:61616");

        // Customized Listener
        eventPublisher.addEventListener(event -> {
            if (!(event instanceof DistributedEventObject)) {
                System.out.printf("[Thread : %s] Handles %s[Source : %s]\n",
                        Thread.currentThread().getName(),
                        event.getClass().getSimpleName(),
                        event.getSource());
            }
        });

        eventPublisher.close();
    }
}
