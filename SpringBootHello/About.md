# JAVA基础框架概述

## Springboot框架应用

### Springboot概述

#### 1.1 什么是Springboot

![image-20250623232114437](https://gitee.com/weido34/typora-image/raw/master/img/image-20250623232114437.png)

Spring Boot 是一个开源的 Java 框架，旨在让开发者能够轻松创建独立的、生产级的基于 Spring 的应用程序，做到“开箱即用”。 我们对 Spring 平台和第三方库采取了有主见的观点，使您可以以最小的麻烦开始。大多数 Spring Boot 应用程序只需要最少的 Spring 配置。

#### 1.2 功能特点

**主要特性：**

1. **创建独立的 Spring 应用程序：** Spring Boot 可以以 jar 包的形式独立运行，无需外部应用服务器。 
2. **嵌入式 Servlet 容器：** Spring Boot 内置了 Tomcat、Jetty 或 Undertow，无需将应用程序打包为 WAR 文件进行部署。 
3. **简化的 Maven 配置：** Spring Boot 提供了推荐的基础 POM 文件，简化了 Maven 的配置。 
4. **自动配置：** Spring Boot 会根据项目依赖自动配置 Spring 框架，极大地减少了手动配置的工作量。 
5. **生产就绪的功能：** Spring Boot 提供了可直接在生产环境中使用的功能，如性能指标、应用信息和健康检查。 
6. **无代码生成和 XML 配置：** Spring Boot 不生成代码，也不需要任何 XML 配置即可实现 Spring 的所有配置。 

#### **1.3 核心理念**

Spring Boot 强调“**约定优于配置**”，即提供合理的默认配置，开发者在默认配置满足需求时无需额外配置，只有在需要改变默认行为时才进行配置。 

**优势：**

- **简化开发：** 通过自动配置和起步依赖，开发者可以专注于业务逻辑，而无需处理繁琐的配置。
- **快速启动：** 内嵌的服务器和独立运行的特性使应用程序的启动和部署更加快捷。
- **易于监控：** 提供的生产就绪功能，如 Actuator，使得应用的监控和管理更加方便。

总的来说，Spring Boot 通过简化配置和提供一系列开箱即用的功能，极大地提升了开发效率，使开发者能够更专注于业务逻辑的实现。

#### 1.4 springboot版本

- **当前使用版本**：3.2.12

- **其他版本**：包括 3.5.0-M1、3.5.0-SNAPSHOT、3.4.3-SNAPSHOT、3.3.9-SNAPSHOT、3.3.8、3.2.12、3.1.12、3.0.13、2.7.18 等。

  ![image-20250623232203879](../../../海洋生物ai/day01/1、项目基础框架搭建/1、项目基础框架搭建/教案/assets/image-20250623232203879.png)

### 编写Springboot的Hello程序

在 IntelliJ IDEA 2023 和 Maven 3.9.6 环境下编写一个简单的 Spring Boot 3.2.12 Hello World 程序涉及以下步骤：配置 Maven 项目、编写 Spring Boot 程序、运行和测试程序。下面是详细的实现过程和代码解释。

![image-20250624094428014](../../../海洋生物ai/day01/1、项目基础框架搭建/1、项目基础框架搭建/教案/assets/image-20250624094428014.png)

#### 步骤一：创建 Maven 项目

1. 在 IDEA 中创建一个新的 Maven 项目：
   - 打开 IntelliJ IDEA，点击 `File` -> `New` -> `Project`。
   - 选择 `Maven`，点击 `Next`。
   - 输入项目的 `GroupId` 和 `ArtifactId`（比如：`com.gec` 和 `springboot-hello`）。
   - 选择项目的 JDK 版本，Spring Boot 3.x 需要 JDK 17 或更高版本。
   - 点击 `Finish`。

#### 步骤二：配置 `pom.xml`

在 `pom.xml` 中配置 Spring Boot 依赖和插件，以便能够构建和运行 Spring Boot 应用。

~~~ xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.gec</groupId>
    <artifactId>springboot-hello</artifactId>
    <version>1.0-SNAPSHOT</version>
    <packaging>jar</packaging>

    <properties>
        <java.version>17</java.version> <!-- Spring Boot 3.x 需要 JDK 17 及以上版本 -->
    </properties>

    <!--    所有springboot项目都必须继承自 spring-boot-starter-parent -->
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.12</version>
        <!-- <relativePath /> -->  <!-- 根据情况添加 -->
    </parent>

    <dependencies>
        <!-- Spring Boot Starter Web -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <!-- Spring Boot Starter Test (用于单元测试) -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <!-- Spring Boot Maven 插件，用于打包和运行 Spring Boot 应用 -->
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>


</project>
~~~



**解释**：

- **`<parent>`**：定义了该项目的父 POM 配置。所有的 Spring Boot 项目都应该继承自 `spring-boot-starter-parent`，它提供了许多有用的默认配置和版本管理，从而避免了在项目中手动设置这些配置。
- `spring-boot-starter-web`：这是 Spring Boot 提供的 Web 开发起步依赖，包括了 Spring MVC 和 Tomcat（内嵌的）。
- `spring-boot-maven-plugin`：这个插件用于构建 Spring Boot 应用，可以直接打包为 JAR 文件，或者通过 `mvn spring-boot:run` 启动应用。
- `java.version`：指定 Java 版本，Spring Boot 3.x 要求至少使用 JDK 17。

#### 步骤三：编写 Spring Boot 应用代码

1. **创建主程序类 `SpringBootHelloApplication.java`**：

   在 `src/main/java/com/jzm/springboothello` 目录下创建主程序类。

```
package com.gec.springboothello;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class SpringBootHelloApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootHelloApplication.class, args);
    }
}

@RestController
class HelloController {

    @GetMapping("/hello")
    public String sayHello() {
        return "Hello, Spring Boot!";
    }
}
```

**解释**：

- `@SpringBootApplication`：这是一个组合注解，包含了 `@Configuration`、`@EnableAutoConfiguration` 和 `@ComponentScan`。它标志着这是一个 Spring Boot 应用。
- `SpringApplication.run()`：启动 Spring Boot 应用。
- `@RestController`：该注解表明该类处理 HTTP 请求，`@RestController` 自动结合了 `@Controller` 和 `@ResponseBody`，即返回的对象会直接被转换为 JSON 或文本。
- `@GetMapping("/hello")`：处理 GET 请求，访问 `/hello` URL 时，返回 "Hello, Spring Boot!" 字符串。

#### 步骤四：配置应用属性

可以在 `src/main/resources` 目录下创建 `application.properties` 或 `application.yml` 来配置一些应用参数（如端口、日志等）。

**示例：`application.properties`**

```
server.port=8080  # 设置应用运行的端口
```

#### 步骤五：运行应用

在 IntelliJ IDEA 中：

1. 右键点击 `SpringBootHelloApplication.java` 类，选择 `Run 'SpringBootHelloApplication'`。
2. 也可以通过 Maven 命令运行：打开终端，执行 `mvn spring-boot:run`。

#### 步骤六：测试应用

- 启动应用后，打开浏览器，访问 `http://localhost:8080/hello`，你应该能够看到返回的结果：

  ![image-20250623232232354](https://gitee.com/weido34/typora-image/raw/master/img/image-20250623232232354.png)

- Hello, Spring Boot!

#### 步骤七：打包和部署

1. 打包为 JAR 文件：
   - 运行 `mvn clean package` 命令。
   - 在 `target` 目录下会生成一个 `.jar` 文件，文件名为 `springboot-hello-1.0-SNAPSHOT.jar`。
2. 运行 JAR 文件：
   - 使用命令 `java -jar target/springboot-hello-1.0-SNAPSHOT.jar` 启动应用。