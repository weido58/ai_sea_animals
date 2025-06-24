## MyBatis-Plus框架

### 1. MyBatis-Plus概述

**MyBatis-Plus**（简称MP）是基于MyBatis的增强工具，它简化了MyBatis的开发，减少了大量的代码和SQL配置，使得开发者能够专注于业务逻辑的实现。它为MyBatis提供了一些常用的功能增强，如自动化的CRUD操作、代码生成器、条件构造器等，使得开发过程更加高效和简洁。

MyBatis-Plus依然保留了MyBatis的核心优势，开发者可以手动编写SQL，同时也能通过MyBatis-Plus的自动化功能提升开发效率。它不需要改变MyBatis的原有架构，只是对MyBatis进行了增强，因此它与MyBatis具有高度兼容性。

官网：https://www.baomidou.com/

![image-20250623232259950](../../../海洋生物ai/day01/1、项目基础框架搭建/1、项目基础框架搭建/教案/assets/image-20250623232259950.png)

### 2. MyBatis-Plus的特点

1. **简化CRUD操作**
   MyBatis-Plus为常见的CRUD操作（如增、删、改、查）提供了通用方法。通过继承`BaseMapper`接口，开发者不需要写任何SQL语句就可以实现基本的数据库操作，如：
   - `insert()`
   - `delete()`
   - `update()`
   - `selectById()` 等
2. **自动化分页查询**
   MyBatis-Plus提供了内置的分页功能。开发者只需要调用`Page`对象配合`selectPage()`方法，就能够轻松实现分页查询，无需手动编写分页SQL。
3. **条件构造器**
   MyBatis-Plus提供了强大的条件构造器（`Wrapper`）来帮助开发者构建动态SQL查询。`QueryWrapper`、`UpdateWrapper`等对象可以实现灵活的查询条件、排序、分页、联合查询等操作。
4. **代码生成器**
   MyBatis-Plus内置代码生成器，可以根据数据库表自动生成实体类、Mapper接口、XML映射文件等。通过简单的配置，可以快速生成符合开发规范的代码，极大提高了开发效率。
5. **乐观锁插件**
   MyBatis-Plus支持乐观锁机制，能够在并发环境下避免数据冲突。在更新数据时，通过在数据库表中添加版本字段来进行版本控制。它会自动检测版本号，防止因并发修改数据而造成的数据不一致问题。
6. **SQL性能分析**
   MyBatis-Plus提供了SQL性能分析插件，可以帮助开发者分析和优化SQL的执行效率。通过日志输出的方式，开发者可以清楚地看到执行的SQL语句及其性能指标。
7. **逻辑删除功能**
   MyBatis-Plus内置逻辑删除支持。在删除记录时，并不会直接从数据库中删除数据，而是通过设置标志字段来实现“删除”效果。开发者只需在实体类中配置`@TableLogic`注解即可。
8. **自动填充功能**
   MyBatis-Plus提供了自动填充的功能。可以在插入或更新数据时，自动填充一些字段（如创建时间、修改时间、操作人等），避免手动处理这些字段。
9. **多种数据库支持**
   MyBatis-Plus支持主流数据库，如MySQL、PostgreSQL、Oracle、SQL Server等，且具有良好的兼容性。
10. **插件机制**
     MyBatis-Plus提供了插件机制，开发者可以根据自己的需求自定义插件。例如，开发者可以编写自己的SQL拦截器、性能分析插件等。

# 项目基础框架搭建

## 配置pom文件

~~~ xml
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

    </dependencies>


</project>
~~~

## 实体类 `SysUser`（`entity` 包）

~~~ java
package com.gec.marine.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_users")
public class SysUser {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String username;
    private String email;
    private String password;
    private String nickname;
    private String avatarUrl;
    private String role;
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;
}

~~~



解释：

- **`@TableName`**：用于指定数据库表名。
- **`@TableId`**：表示该字段是主键，MyBatis-Plus 会自动处理主键的值（比如自增）。

## Mapper 接口 `SysUserMapper`（`mapper` 包）

```
package com.gec.marine.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gec.marine.model.SysUser;

public interface SysUserMapper extends BaseMapper<SysUser> {
    // MyBatis-Plus 会自动实现常见的 CRUD 操作（insert, delete, update, select）
}
```

解释：

- **`BaseMapper<T>`**：MyBatis-Plus 提供的基础接口，自动实现了常见的 CRUD 操作。

## Service 接口与实现（`service` 包）

### 1. **Service 接口**

```
package com.gec.marine.service;

import com.gec.marine.entity.SysUser;
import com.baomidou.mybatisplus.extension.service.IService;

public interface SysUserService extends IService<SysUser> {
    // 可以根据需求添加业务方法，默认提供了常见的 CRUD 操作
}
```

解释：

- **`IService<T>`**：MyBatis-Plus 提供的服务层基础接口，默认实现了常见的 CRUD 操作。

### 2. **Service 实现类**

```
package com.gec.marine.service.impl;

import com.gec.marine.mapper.SysUserMapper;
import com.gec.marine.model.SysUser;
import com.gec.marine.service.SysUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {
    // 可以在此添加自定义的业务逻辑
}
```

解释：

- **`ServiceImpl`**：MyBatis-Plus 提供的实现类，已经实现了常见的 CRUD 功能。我们只需要继承它，并且指定 Mapper 和实体类。

## Controller 层（`controller` 包）

```
package com.gec.marine.controller;

import com.gec.marine.model.SysUser;
import com.gec.marine.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private SysUserService sysUserService;

    // 查询所有用户
    @GetMapping("/list")
    public List<SysUser> getAllUsers() {
        return sysUserService.list(); // MyBatis-Plus 会自动处理
    }

    // 根据 ID 查询用户
    @GetMapping("/{id}")
    public SysUser getUserById(@PathVariable Long id) {
        return sysUserService.getById(id); // MyBatis-Plus 提供的查询方法
    }

    // 添加用户
    @PostMapping("/add")
    public boolean addUser(@RequestBody SysUser SysUser) {
        return sysUserService.save(SysUser); // MyBatis-Plus 提供的保存方法
    }

    // 更新用户
    @PutMapping("/update")
    public boolean updateUser(@RequestBody SysUser SysUser) {
        return sysUserService.updateById(SysUser); // MyBatis-Plus 提供的更新方法
    }

    // 删除用户
    @DeleteMapping("/delete/{id}")
    public boolean deleteUser(@PathVariable Long id) {
        return sysUserService.removeById(id); // MyBatis-Plus 提供的删除方法
    }
}
```

解释：

- **`@RestController`**：标识这是一个控制器类，自动返回 JSON 响应。

- **`@RequestMapping`**：设置请求的路径前缀。

- **`@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping`**：分别对应 GET、POST、PUT 和 DELETE 请求。

- `SysUserService` 调用了 MyBatis-Plus 提供的 CRUD 方法，比如 `save()`, `getById()`, `updateById()`, `removeById()`。

  

## 全局配置文件

application.yml

~~~ yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/ocean_db?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai&useSSL=false
    username: root
    password: 1111
    # Druid 数据源配置
    druid:
      initial-size: 5  # 初始化连接数
      min-idle: 5      # 最小空闲连接数
      max-active: 20   # 最大连接数
      max-wait: 60000  # 获取连接时最大等待时间
      validation-query: SELECT 1 FROM DUAL  # 校验连接的SQL
mybatis-plus:
  # 不支持多包, 如有需要可在注解配置 或 提升扫包等级
  # 例如 com.**.**.mapper
  mapperPackage: com.gec.marine.mapper
  # 对应的 XML 文件位置
  mapperLocations: classpath*:mapper/**/*Mapper.xml
  # 实体扫描，多个package用逗号或者分号分隔
  typeAliasesPackage: com.gec.marine.entity
  # 启动时是否检查 MyBatis XML 文件的存在，默认不检查
  checkConfigLocation: false
  configuration:
    # 自动驼峰命名规则（camel case）映射
    mapUnderscoreToCamelCase: true
    # MyBatis 自动映射策略
    # NONE：不启用 PARTIAL：只对非嵌套 resultMap 自动映射 FULL：对所有 resultMap 自动映射
    autoMappingBehavior: FULL
    # MyBatis 自动映射时未知列或未知属性处理策
    # NONE：不做处理 WARNING：打印相关警告 FAILING：抛出异常和详细信息
    autoMappingUnknownColumnBehavior: NONE
    # 更详细的日志输出 会有性能损耗 org.apache.ibatis.logging.stdout.StdOutImpl
    # 默认日志输出 org.apache.ibatis.logging.slf4j.Slf4jImpl
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl

server:
  port: 8080

~~~



## Handler处理器

- MyMetaObjectHandler类

~~~ java
package com.marine.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        LocalDateTime now = LocalDateTime.now();
        this.strictInsertFill(metaObject, "createdTime", LocalDateTime.class, now);
        this.strictInsertFill(metaObject, "updatedTime", LocalDateTime.class, now);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updatedTime", LocalDateTime.class, LocalDateTime.now());
    }
}
~~~

 配置说明

- `@Component`: 保证被 Spring 扫描到。
- `strictInsertFill`: 在插入时填充字段（仅当字段为空时填充）。
- `strictUpdateFill`: 在更新时填充字段。



## 启动类MarineRecognitionApplication

~~~ java
package com.gec.marine;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.gec.marine.mapper")
public class MarineRecognitionApplication {

    public static void main(String[] args) {
        SpringApplication.run(MarineRecognitionApplication.class, args);

    }
}

~~~



