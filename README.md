# 第十周作业
## 1. 引入Bulkhead
```xml
<dependency>
    <groupId>io.github.resilience4j</groupId>
    <artifactId>resilience4j-bulkhead</artifactId>
    <version>1.7.1</version>
</dependency>
```

## 2. 在provider项目中写Filter
```java
package org.apache.dubbo.demo.filter;

import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadConfig;
import io.github.resilience4j.bulkhead.BulkheadRegistry;
import io.vavr.CheckedFunction0;
import io.vavr.control.Try;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;

import java.time.Duration;

import static org.apache.dubbo.common.constants.CommonConstants.PROVIDER;

/**
 * @Author: 项峥
 * @Date: 2021/9/14 0:48
 */
@Activate(group = PROVIDER)
public class BulkheadFilter implements Filter {
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        BulkheadConfig config = BulkheadConfig.custom()
                .maxConcurrentCalls(1)
                .maxWaitDuration(Duration.ofSeconds(2)).build();
        Bulkhead bulkhead = Bulkhead.of("name", config);
        CheckedFunction0<Result> resultCheckedFunction0 = Bulkhead.decorateCheckedSupplier(bulkhead, () -> invoker.invoke(invocation));
        Try<Result> tryResult = Try.of(resultCheckedFunction0);
        System.out.println("========== bulkhead filtered =======");
        return tryResult.get();
    }
}

```

## 3. spi扩展
在resource下, 新建文件META-INF/dubbo/org.apache.dubbo.rpc.Filter, 内容为
```
bulkhead=org.apache.dubbo.demo.filter.BulkheadFilter
```

## 4. 修改dubbo.provider.xml
在dubbo:provider中添加属性`filter="bulkhead"`.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!--
  Licensed to the Apache Software Foundation (ASF) under one or more
  contributor license agreements.  See the NOTICE file distributed with
  this work for additional information regarding copyright ownership.
  The ASF licenses this file to You under the Apache License, Version 2.0
  (the "License"); you may not use this file except in compliance with
  the License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
  -->
<beans xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:dubbo="http://dubbo.apache.org/schema/dubbo"
       xmlns="http://www.springframework.org/schema/beans"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-4.3.xsd
       http://dubbo.apache.org/schema/dubbo http://dubbo.apache.org/schema/dubbo/dubbo.xsd">

    <dubbo:application name="demo-provider"/>

    <dubbo:provider token="true" filter="bulkhead"/>

    <dubbo:registry address="zookeeper://${zookeeper.address:127.0.0.1}:2181"/>

    <dubbo:protocol name="dubbo"/>

    <bean id="demoServiceImpl" class="org.apache.dubbo.demo.provider.DemoServiceImpl"/>

    <dubbo:service serialization="protobuf" interface="org.apache.dubbo.demo.DemoService"
                   ref="demoServiceImpl"/>

</beans>

```

# 第十一周作业
## Configuration类
要求在非web应用才装配, 所以加上`@ConditionalOnNotWebApplication`注解.

```java
package com.wsbo.activemqtest.config;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnNotWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: 项峥
 * @Date: 2021/9/15 16:33
 */
@ConditionalOnNotWebApplication
@Configuration
public class ApplicationRunnerConfig {
    @Bean
    public ApplicationRunner applicationRunner() {
        System.out.println("build application runner");
        return args -> System.out.println("Hello world!");
    }
}

```

## 自动配置
### 1. 配置文件
在resource/META-INF下新建文件spring.factories

```
org.springframework.boot.autoconfigure.EnableAutoConfiguration=com.wsbo.activemqtest.config.ApplicationRunnerConfig
```

### 2. 启动类上加上注解
1. 启动类加上注解`@SpringBootApplication`
2. 如果需要的话, 配置`@ComponentScan`