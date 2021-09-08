/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.geektimes.event.distributed;

import java.util.ArrayList;
import java.util.List;
import java.util.EventObject;
import java.util.Scanner;

/**
 * Distributed {@link EventObject} Subscriber
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class DistributedEventSubscriber {
    @Override
    public String toString() {
        return "DistributedEventSubscriber{}";
    }



    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        List<Object> objects = new ArrayList<>();
        DistributedEventPublisher eventPublisher = new DistributedEventPublisher("redis://127.0.0.1:6379");

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
