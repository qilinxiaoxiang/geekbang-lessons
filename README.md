# 第六周作业
1. 增加一个注解名为 @ConfigSources，使其能够关联多个@ConfigSource，并且在 @ConfigSource 使用Repeatable，可以对比参考 Spring 中 @PropertySources 与@PropertySource，并且文字说明 Java 8 @Repeatable 实现原理。
2. 可选作业，根据 URL 与 URLStreamHandler 的关系，扩展一个自定义协议，可参考`sun.net.www.protocol.classpath.Handler`.

## 作业1
### 1. @Repeatable实现原理
- @Repeatable注解实际上是一个语法糖. 
- 以@PropertySources和@PropertySource为例.

```java
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PropertySources {
    PropertySource[] value();
}
```

```java
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(PropertySources.class)
public @interface PropertySource {
    String name() default "";

    String[] value();

    boolean ignoreResourceNotFound() default false;

    String encoding() default "";

    Class<? extends PropertySourceFactory> factory() default PropertySourceFactory.class;
}

```

- 给一个类增加多个@PropertySource注解, 编译以后反编译, 就能看到其实被编译成@PropertySources.

``` java

@PropertySource("classpath:a.properties")
@PropertySource("classpath:b.properties")
public class RepeatableTest {
}

```

等价于

```java
@PropertySources(value = {
@PropertySource("classpath:a.properties")
@PropertySource("classpath:b.properties")
})
public class RepeatableTest {
}
```

### 2. 增加@ConfigSources注解
见master分支的最新提交. https://github.com/qilinxiaoxiang/geekbang-lessons/commits/master

## 作业2