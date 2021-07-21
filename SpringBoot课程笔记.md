####Spring Boot HelloWorld入门

8. 根据官方文档引入spring-boot-starter-parent依赖以及spring-boot-starter-web依赖

2. 创建主应用类HelloWorldMainApplication，并使用@SpringBootApplication修饰

3. 创建业务逻辑组件(@Controller等)

4. SpringApplication.run(HelloWorldMainApplication.class, args)开启服务

5. 可以在pom文件中添加spring-boot-maven-plugin插件，将项目打包成jar，可以通过java -jar的方式直接开启服务

---

####Spring Boot 依赖管理

1. Spring Boot pom文件中包含父项目spring-boot-starters-parent，该项目又包含父项目spring-boot-dependencies，dependencies项目用来管理spring boot的依赖版本。

2. Spring Boot将相关功能模块的依赖封装为对应的spring-boot-starters-xx依赖，比如web功能模块对应依赖spring-boot-starters-web，也称为**场景启动器**。

---

####@SpringBootApplication注解详解

#### test

1. @SpringBootApplication是一个组合注解，首先，其包含@SpringBootConfiguration，表示被该注解修饰的类一个配置类(@Configuation其实也是一个@Component)

```java
...
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(
    excludeFilters = {@Filter(
    type = FilterType.CUSTOM,
    classes = {TypeExcludeFilter.class}
), @Filter(
    type = FilterType.CUSTOM,
    classes = {AutoConfigurationExcludeFilter.class}
)}
)
```

2. @SpringBootApplication又包含@EnableAutoConfiguration注解，该注解用于开启自动配置，其包含@AutoConfigurationPackage和@Import({EnableAutoConfigurationImportSelector.class})

  ```java
  ...
  @AutoConfigurationPackage
  @Import({EnableAutoConfigurationImportSelector.class})
  ```

  + @AutoConfigurationPackage注解包含@Import({Registrar.class})注解，即向容器中注册了一个ImportBeanDefinitionRegistrar对象，该对象根据注解元信息AnnotationMetadata**向容器中注册被@SpringBootApplication修饰的类所在的包下的所有组件**

  ```java
  ...
  @Import({Registrar.class})
  ...
      
  static class Registrar implements ImportBeanDefinitionRegistrar, DeterminableImports {
  ...
  	public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
  		AutoConfigurationPackages.register(registry, (new AutoConfigurationPackages.PackageImport(metadata)).getPackageName());
          }
  ...
      }
  ```
  + 通过@Import注解导入EnableAutoConfigurationImportSelector对象，该对象配根据META-INF/spring.factories的定义导入非常多的自动配置类，例如springmvc中的视图解析器等。

  【总结】@EnableAutoConfiguration有两部分功能

	+ 免去配置@ComponentScan（@AutoConfigurationPackage功能）
	+ 免去人工配置类似视图解析器等组件（EnableAutoConfigurationImportSelector功能）

---

####使用spring initializer快速创建spring-boot项目

可以在idea中使用spring initializer，通过联网方式将项目初始模板下载到本地

---

####Springboot 组件属性注入方式

1. 在springboot中，可以使用.properties文件作为配置文件，也可以使用.yml文件作为配置文件

2. yaml语法

  + 以key: value的形式进行配置，层级关系使用左对齐空格进行控制，且大小写敏感
    **【注意】冒号后一定要有一个空格！**
  + 字符串默认不需要加""或''
    + ""包含下会将特殊字符进行转义，例如"zhangsan \n lisi"会输出换行
    + ''不会进行转义例如"zhangsan \n lisi"会输出"zhangsan \n lisi"
  + 对象表示方式

  ```yaml
  #key-value形式
  friends:
      name: zhangsan
      age: 22
  #行内形式
  friends: {name: zhangsan,age: 22}
  ```

  + 数组表示方式

  ```yaml
  #key-value形式
  pets:
   - cat
   - dog
   - pig
  #行内形式
  pets: [cat,dog,pig]
  ```

  【总结】对于行内形式，对象用{}，数组用[]


    3. @ConfigurationProperties注解用于组件赋值

+ 该注解默认使用application.yml或application.properties作为配置文件
+ 该注解用于标注待配置属性的类，对该对象进行初始化赋值操作【注意】**该对象必须在容器中！**
+ 导入配置文件处理器依赖，重新编译后，可以在写配置文件时获得自动补全提示(properties文件与yaml都适用)

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-configuration-processor</artifactId>
    <optional>true</optional>
</dependency>
```

4. @ConfigurationProperties与@Value的区别

| `@ConfigurationProperties`                                   | @Value                  |
| ------------------------------------------------------------ | ----------------------- |
| 批量赋值                                                     | 分别赋值                |
| 支持松散绑定（可以用短横线-(person.last-name)，下划线_（person.last_name）以及全部大写用下划线PERSON_LAST_NAME） | 不支持                  |
| 不支持                                                       | 支持spEL#{}             |
| 支持JSR303校验（@Validated标注对象，@Email标注相应字段）     | 不支持                  |
| 支持复杂类型                                                 | 不支持复杂类型封装(Map) |

5. @PropertySource用于指定加载配置文件

   ```java
   @PropertySource(value = {"classpath:person.properties"})
   ```

   + 【注意】**仅支持properties文件，不支持yml！**

6. @ImportResource导入springboot配置文件（仅导入xml文件）

7. 可以直接使用@Configuration添加组件



