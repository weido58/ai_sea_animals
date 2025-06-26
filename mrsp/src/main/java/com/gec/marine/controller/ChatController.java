package com.gec.marine.controller;

import com.gec.marine.config.AssistantConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
//    @Autowired
//    private ChatService chatService;
    @Autowired
    private AssistantConfig.Assistant  assistant;

    /**
     * POST接口：接收用户消息并返回AI回复
     *
     * @param message 请求体中的用户消息
     * @return AI模型的回复
     */
//    @GetMapping("/sendmessage")
//    public String chat(@RequestParam("message") String message) {
//        // 调用服务层方法处理聊天逻辑
//        return chatService.chat(message);
//    }

    @GetMapping("/sendmessageByAssistant")
    public String sendmessageByAssistant(@RequestParam("message") String message) {
        // 调用服务层方法处理聊天逻辑
        return assistant.chat(message);
    }

    /**
     * GET接口：测试用的hello接口
     * 用于快速验证系统是否正常工作
     *
     * @return AI对"你好，你是谁？"的回复
     */
//    @GetMapping("/hello")  // 映射GET请求到/api/chat/hello
//    public String hello() {
//        // 发送固定的问候消息给AI
//        return chatService.chat("你好，你是谁？");
//    }
}