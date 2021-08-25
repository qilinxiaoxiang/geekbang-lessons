# 作业
## 上周作业
### 1. 描述Spring校验注解org.springframework.validation.annotation.Validated 的⼯作原理，它与 Spring Validator 以及 JSR-303 Bean Validation@javax.validation.Valid 之间的关系

#### 1.1 @Validated工作原理
1. 在Spring MVC中
    1. Spring MVC中HandlerMethodArgumentResolver是主要的方法参数解析器，@RequestBody注解对应的参数解析器（RequestResponseBodyMethodProcessor）是它的实现之一.
    2. 解析用@RequestBody注释的方法参数并处理返回，通过读写@ResponseBody注释的方法中的值使用HttpMessageConverter返回请求或响应的主体。
    3. 走到org.springframework.web.servlet.mvc.method.annotation.AbstractMessageConverterMethodArgumentResolver#validateIfApplicable
        - 遍历当前参数methodParam所有的注解，如果注解是@Validated或注解的名字以‘Valid’开头，则使用WebDataBinder对象执行校验逻辑
    4. isBindExceptionRequired方法，说的通俗一点，就是要不要抛出异常。怎么判断呢？如果当前参数后面还有一个参数并且参数类型是Errors（BindingResult继承Errors）则不抛出异常。
    5. BindingResult的结果被封装在ModelAndViewContainer.
2. 对于任意Bean
    - 在org.springframework.validation.beanvalidation.MethodValidationPostProcessor 后置处理器中，调用afterPropertiesSet时 会对所有标注了org.springframework.validation.annotation.Validated的方法，创建一个切面Pointcut，并使用该Pointcut创建一个Advisor，Advisor创建 则调用createMethodValidationAdvice方法，在该方法中，为方法创建了一个org.springframework.validation.beanvalidation.MethodValidationInterceptor 方法拦截器。在该方法调用时，会使用代理的方式，调用Intercepotr中的invoke方法。invoke方法中，通过Validator获取方法的入参和出参，并做校验。

#### 1.2 Spring Validator工作原理
1. 接口职责
    - Spring内部校验器接口, 通过编程的方式校验目标对象
2. 核心方法
    - supports(Class): 判断目标类能否校验
    - validate(Object, Errors): 校验目标对象, 并将校验失败的内容输出至Errors对象
3. 配套组件
    - 错误收集器: org.springframework.validation.Errors
    - Validator工具类: org.springframework.validation.ValidationUtils
4. 使用场景
    - 编程式校验, 已经被淘汰. 比不上Bean Validation等声明式校验.

#### 1.3 三者之间的观察
1. @Validated是Spring's JSR-303规范(标准JSR-303规范的一个变种)的实现, 支持分组校验
2. @Valid是标准JSR-303规范的实现, 不支持分组校验
3. 被@Validated标注的类，在校验时，最终会调用org.springframework.validation.Validator接口中的validate方法进行校验。​ 而Validator接口的创建，则是由org.springframework.validation.beanvalidation.SpringValidatorAdapter工厂类创建，​ 同时作为适配器类，SpringValidatorAdapter同时实现了​ org.springframework.validation.Validator和javax.validation.Validator，用于适配不同的实现。​ 用户也可以指定Validator的实现，在spring-mvc中的org.springframework.validation.DataBinder#setValidator​ 以及org.springframework.validation.beanvalidation.MethodValidationPostProcessor#setValidator


## 这周作业
### 2. 利用Reactor Mono API配合Reactive Streams Publisher实现, 让Subscriber实现能够获取到数据.

将Mono.from()改成Mono.fromDirect()即可.

```java
SimplePublisher publisher = new SimplePublisher();
Mono.fromDirect(publisher).subscribe(new BusinessSubscriber(5));
for (int i = 0; i < 5; i++) {
    publisher.publish(i);
}
```

看API文档, 可以知道from方法, 会确保Publisher只会发布0个或1个消息.

    Expose the specified Publisher with the Mono API, and ensure it will emit 0 or 1 item. The source emitter will be cancelled on the first `onNext`.

而fromDirect方法不会去检查Publisher.

    Convert a Publisher to a Mono without any cardinality check (ie this method doesn't cancel the source past the first element). Conversion transparently returns Mono sources without wrapping and otherwise supports Fuseable sources. Note this is an advanced interoperability operator that implies you know the Publisher you are converting follows the Mono semantics and only ever emits one element.