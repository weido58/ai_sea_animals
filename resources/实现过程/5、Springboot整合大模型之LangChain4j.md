#  Springboot整合大模型之LangChain4j

## LangChain4j概述

> LangChain是⼀个⼤模型的开发框架，使⽤ LangChain 框架，程序员可以更好的利⽤⼤模型的能⼒，⼤⼤提⾼编程效率。如果你是⼀个 Java 程序员，那么对 LangChain 最简单直观的理解就是， LangChain 是⼤模型领域的Spring 。 LangChain 不光提供了⼀整套快速接⼊各种开放⼤模型的⼯具，更重要的是，他集成了当今程序员使⽤⼤模型能⼒最好的⽅案。
>      LangChain是⼀个基于 Python 开发的框架，⽽ LangChain4j 则是 LangChain 的 Java 版本。将⼤模型的强⼤能⼒和Java 编程语⾔相结合，这就是 LangChain4j 所做的。实际上， LangChain4j 不光包含了 LangChain 的功能，同时还加⼊了很多⾃⼰的创新。在⽬前阶段， LangChain4j 也是与⼤模型结合最好最成熟的框架。

LangChain4j 的目标是简化与 Java 应用程序 集成大模型。

在课程开始之前，由于LangChain4j是采用JDK17写的，所以大家需要将JDK安装成17版本的。

官网：https://docs.langchain4j.dev/get-started

![image-20250603183809905](assets/image-20250603183809905.png)

## Spring Boot整合LangChain4j代码实现

### 项目目标

学习如何在Spring Boot项目中整合LangChain4j框架，实现与大模型的交互功能。

### 技术栈

- Spring Boot 3.2.12
- LangChain4j 1.0.0-beta1
- JDK 17+
- Maven 构建工具

### 项目结构

```
mrsp/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── example/
│       │           └── langchain4j/
│       │               ├── LangChain4Application.java
│       │               ├── controller/
│       │               │   └── ChatController.java
│       │               └── service/
│       │                   └── ChatService.java
│       └── resources/
│           └── application.yml
└── pom.xml
```

### Maven依赖配置（pom.xml）

xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.gec</groupId>
    <artifactId>mrsp</artifactId>
    <version>1.0-SNAPSHOT</version>

    <!-- 指定spring boot父工程 -->
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.6</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>

    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <druid.version>1.2.9</druid.version>
        <mybatis-spring-boot-starter.version>3.0.3</mybatis-spring-boot-starter.version>
        <mysql-connector.version>8.0.29</mysql-connector.version>
        <mybatis-plus.version>3.5.6</mybatis-plus.version>
        <mybatis.version>3.5.16</mybatis.version>
        <!-- LangChain4j版本号，使用变量便于统一管理 -->
        <langchain4j.verion>1.0.0-beta1</langchain4j.verion>
    </properties>


    <dependencies>

        <!-- Spring Boot Web Starter -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>org.mybatis</groupId>
            <artifactId>mybatis</artifactId>
            <version>${mybatis.version}</version>
        </dependency>

        <!--集成druid连接池-->
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>druid-spring-boot-starter</artifactId>
            <version>${druid.version}</version>
        </dependency>
        <!--Mysql数据库驱动-->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>${mysql-connector.version}</version>
        </dependency>

        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
            <version>${mybatis-plus.version}</version>
        </dependency>

        <!-- MyBatis-Plus -->
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus</artifactId>
            <version>${mybatis-plus.version}</version>
        </dependency>

        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-webflux</artifactId>
        </dependency>

        <!-- LangChain4j核心库：提供基础的AI模型交互功能 -->
        <dependency>
            <groupId>dev.langchain4j</groupId>
            <artifactId>langchain4j</artifactId>
            <version>${langchain4j.verion}</version>
        </dependency>

        <!-- LangChain4j OpenAI集成：提供OpenAI模型的具体实现 -->
        <dependency>
            <groupId>dev.langchain4j</groupId>
            <artifactId>langchain4j-open-ai</artifactId>
            <version>${langchain4j.verion}</version>
        </dependency>

    </dependencies>




</project>
```

**配置要点说明：**

1. **JDK版本**：Spring Boot 3.x要求JDK 17+
2. **版本管理**：使用变量统一管理LangChain4j版本
3. **依赖选择**：选择合适的starter和AI模型集成库
4. **构建工具**：使用Spring Boot Maven插件简化构建过程

### 聊天服务类（ChatService.java）

java

```java
package com.gec.marine.service;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

    /**
     * 聊天语言模型接口
     * LangChain4j提供的统一聊天模型抽象
     */
    private final ChatLanguageModel chatModel;

    /**
     * 构造函数：初始化聊天模型
     * 采用构造器模式配置OpenAI模型
     */
    public ChatService() {
        // 使用Builder模式创建OpenAI聊天模型实例
        this.chatModel = OpenAiChatModel.builder()
                // 设置API基础URL（这里使用演示地址）
                .baseUrl("https://api.deepseek.com")
                // 设置API密钥（生产环境应从配置文件读取）
                .apiKey("sk-xxxx")
                // 指定使用的模型名称
                .modelName("deepseek-chat")
                // 构建模型实例
                .build();
    }

    /**
     * 聊天方法：与AI模型进行对话
     *
     * @param message 用户输入的消息
     * @return AI模型的回复
     */
    public String chat(String message) {
        // 调用模型的chat方法，传入用户消息并返回AI回复
        return chatModel.chat(message);
    }
}

```

**关键技术点：**

1. **@Service注解**：标记为Spring服务组件
2. **ChatLanguageModel接口**：LangChain4j的核心抽象
3. **Builder模式**：OpenAI模型的配置方式
4. **封装性**：将AI交互逻辑封装在服务层

###  控制器层实现（ChatController.java）

java

```java
package com.gec.marine.controller;

import com.gec.marine.config.AssistantConfig;
import com.gec.marine.service.ChatService;
import dev.langchain4j.service.TokenStream;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

/**
 * 聊天控制器
 * 提供REST API接口，处理HTTP请求
 *
 * @author Your Name
 * @version 1.0
 */
@RestController  // Spring注解：标记为REST控制器，自动序列化返回值为JSON
@RequestMapping("/chat")  // 设置控制器的基础路径
public class ChatController {

    /**
     * 自动注入聊天服务
     * Spring会自动找到ChatService的实例并注入
     */
    @Autowired
    private ChatService chatService;


    /**
     * POST接口：接收用户消息并返回AI回复
     *
     * @param message 请求体中的用户消息
     * @return AI模型的回复
     */
    @GetMapping("/sendmessage")
    public String chat(@RequestParam("message") String message) {
        // 调用服务层方法处理聊天逻辑
        return chatService.chat(message);
    }



    /**
     * GET接口：测试用的hello接口
     * 用于快速验证系统是否正常工作
     *
     * @return AI对"你好，你是谁？"的回复
     */
    @GetMapping("/hello")  // 映射GET请求到/api/chat/hello
    public String hello() {
        // 发送固定的问候消息给AI
        return chatService.chat("你好，你是谁？");
    }


}
```

**API接口说明：**

1. **POST /api/chat**：主要聊天接口
2. **GET /api/chat/hello**：测试接口
3. **@RequestBody**：自动解析HTTP请求体
4. **依赖注入**：使用@Autowired注入服务层

### 应用启动类

### 主启动类（LangChain4Application.java）

java

```java
package com.gec.marine;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.gec.marine.mapper")
public class MarineRecognitionApplication {

    public static void main(String[] args) {
        SpringApplication.run(MarineRecognitionApplication.class,args);
    }
}

```

**启动类要点：**

1. **@SpringBootApplication**：Spring Boot的核心注解
2. **包扫描**：自动扫描当前包及子包的组件
3. **自动配置**：根据类路径自动配置Spring Bean



## 项目运行与测试

### 启动应用

输入地址：

```bash
http://127.0.0.1:8080/mrsp_server/chat/hello
```

![image-20250603184656984](assets/image-20250603184656984.png)

# 引入DeepSeek大模型

如果需要接入DeepSeek首先需要去申请DeepSeek开放平台申请密钥：

![image-20250603203411683](assets/image-20250603203411683.png)

微信扫码登录即可，登录以后找到API keys 注册自己的密钥：

![image-20250603203423694](assets/image-20250603203423694.png)

选择创建自己的API key：

![image-20250603203433982](assets/image-20250603203433982.png)

然后随便起个名字创建即可：

![image-20250603203444710](assets/image-20250603203444710.png)

然后就会产生一个密钥，这个密钥可以用来调用DeepSeek的各种功能：

![image-20250603203455623](assets/image-20250603203455623.png)

DeepSeek API 使用与 OpenAI 兼容的 API 格式，通过修改配置，您可以使用 OpenAI SDK 来访问 DeepSeek API，或使用与 OpenAI API 兼容的软件。

| PARAM      | VALUE                                                        |
| ---------- | ------------------------------------------------------------ |
| base_url * | `https://api.deepseek.com`                                   |
| api_key    | apply for an [API key](https://platform.deepseek.com/api_keys) |

\* 出于与 OpenAI 兼容考虑，您也可以将 `base_url` 设置为 `https://api.deepseek.com/v1` 来使用，但注意，此处 `v1` 与模型版本无关。

\* **`deepseek-chat` 模型已全面升级为 DeepSeek-V3，接口不变。** 通过指定 `model='deepseek-chat'` 即可调用 DeepSeek-V3。

\* **`deepseek-reasoner` 是 DeepSeek 最新推出的[推理模型](https://api-docs.deepseek.com/zh-cn/guides/reasoning_model) DeepSeek-R1**。通过指定 `model='deepseek-reasoner'`，即可调用 DeepSeek-R1。

使用Java调用DeepSeek的APi：

```
    public ChatService() {
        // 使用Builder模式创建OpenAI聊天模型实例
        this.chatModel = OpenAiChatModel.builder()
                // 设置API基础URL（这里使用演示地址）
                .baseUrl("https://api.deepseek.com")
                // 设置API密钥（生产环境应从配置文件读取）
                .apiKey("sk-xxxx")
                // 指定使用的模型名称
                .modelName("deepseek-chat")
                // 构建模型实例
                .build();
    }
```

如图所示代码没有多大的变化， 前面我们说因为 Deepseek 的 API 设计与 OpenAI 的 API 兼容，所以使用OpenAI的那一套就行。只不过deepseek好像没有默认的apikey,所以只能填入你自己的apikey了，具体可以去deepseek官网查看。

结果：

![image-20250603203519343](assets/image-20250603203519343.png)

调用不了报错：

![image-20250603203530990](assets/image-20250603203530990.png)

需要去充值：

![image-20250603203543098](assets/image-20250603203543098.png)