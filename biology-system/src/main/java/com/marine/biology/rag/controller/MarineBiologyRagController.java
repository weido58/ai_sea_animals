package com.marine.biology.rag.controller;

import com.marine.biology.rag.service.MarineBiologyRagService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 海洋生物RAG智能客服系统的RESTful API控制器
 *
 * 【控制器概述】
 * 这是系统的Web API入口层，负责：
 * 1. 接收和处理来自前端、移动端、第三方系统的HTTP请求
 * 2. 参数验证和格式化处理
 * 3. 调用业务服务层(FlightRagService)处理具体逻辑
 * 4. 统一封装响应格式并返回JSON数据
 * 5. 异常处理和错误信息标准化
 *
 * 【API设计原则】
 * - RESTful风格：使用标准HTTP方法和状态码
 * - 统一响应格式：所有接口返回结构化的JSON响应
 * - 向后兼容：保留旧版本接口以支持现有客户端
 * - 跨域支持：允许前端跨域访问
 * - 完整错误处理：提供详细的错误信息和日志记录
 *
 * 【支持的客户端】
 * - Web前端(React/Vue等SPA应用)
 * - 移动应用(iOS/Android)
 * - 第三方集成系统
 * - API测试工具(Postman等)
 *
 * @author System
 * @version 1.0
 * @since 2024-01-01
 */
@RestController                                    // 标识为REST控制器，自动进行JSON序列化
@RequestMapping("/marineBiology")                         // 统一路由前缀，所有接口都以/flight开头
@CrossOrigin(origins = "*", maxAge = 3600)        // 跨域配置：允许所有域名访问，缓存预检请求1小时
public class MarineBiologyRagController {

    // 日志记录器，用于记录请求处理过程和错误信息
    private static final Logger logger = LoggerFactory.getLogger(MarineBiologyRagController.class);

    /**
     * 注入FlightRagService业务服务
     * 通过依赖注入获取RAG服务实例，实现控制器与业务逻辑的解耦
     */
    @Autowired
    private MarineBiologyRagService marineBiologyRagService;

    /**
     * 主要的智能查询接口 - POST方式
     *
     * 【接口设计】
     * - 方法：POST（因为查询内容可能较长，且符合RESTful语义）
     * - 路径：/flight/query
     * - 请求体：纯文本字符串（用户查询内容）
     * - 响应：标准化JSON格式
     *
     * 【使用场景】
     * - 前端智能客服聊天界面
     * - 移动端语音转文字查询
     * - 第三方系统集成调用
     *
     * 【响应格式】
     * {
     *   "success": true,
     *   "timestamp": "2024-01-01T12:00:00",
     *   "query": "用户输入的查询",
     *   "response": "AI生成的回答",
     *   "message": "处理状态描述"
     * }
     *
     * @param query 用户查询内容，使用@NotBlank验证非空
     * @return ResponseEntity包装的标准化JSON响应
     */
    @PostMapping("/query")
    public ResponseEntity<Map<String, Object>> query(
            @RequestBody @NotBlank(message = "查询内容不能为空") String query) {

        logger.info("收到查询请求: {}", query);

        try {
            // 调用RAG服务处理查询，trim()去除首尾空格
            String response = marineBiologyRagService.processQuery(query.trim());

            // 构建成功响应的标准格式
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);                    // 处理成功标识
            result.put("timestamp", LocalDateTime.now());   // 处理时间戳
            result.put("query", query);                     // 原始查询内容
            result.put("response", response);               // AI生成的回答
            result.put("message", "查询成功");               // 处理状态描述

            // 返回HTTP 200状态码和响应数据
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            // 记录错误日志，包含完整的异常堆栈信息
            logger.error("处理查询请求失败: {}", e.getMessage(), e);

            // 构建错误响应的标准格式
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);                          // 处理失败标识
            errorResult.put("timestamp", LocalDateTime.now());          // 错误发生时间
            errorResult.put("query", query);                           // 导致错误的查询内容
            errorResult.put("error", "系统处理异常，请稍后重试");          // 用户友好的错误信息
            errorResult.put("message", e.getMessage());                // 详细的技术错误信息

            // 返回HTTP 500状态码和错误信息
            return ResponseEntity.internalServerError().body(errorResult);
        }
    }

    // 1. 修改Controller方法，添加正确的编码设置
    @GetMapping(value = "/stream", produces = "text/event-stream;charset=UTF-8")
    public Flux<String> chatStream(@RequestParam String message, HttpServletResponse response) {
        // 设置响应头确保UTF-8编码
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/event-stream;charset=UTF-8");
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Connection", "keep-alive");

        return marineBiologyRagService.processQueryStream(message);
    }


}