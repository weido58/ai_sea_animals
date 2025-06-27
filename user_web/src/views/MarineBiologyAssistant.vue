<template>
  <div class="app">
    <!-- 海洋气泡背景 -->
    <div
        v-for="bubble in bubbles"
        :key="bubble.id"
        class="bubble"
        :style="bubble.style"
    ></div>

    <div class="chat-container">
      <!-- 聊天头部 -->
      <div class="chat-header">
        <div
            class="status-indicator"
            :class="{ 'status-error': systemStatus !== 'UP' }"
            :title="systemStatus === 'UP' ? '系统运行正常' : '系统异常'"
        ></div>
        <h1>🐠 海洋生物智能助手</h1>
        <p>探索神秘的海洋世界，了解各种海洋生物的奥秘</p>
      </div>

      <!-- 聊天消息区域 -->
      <div class="chat-messages" ref="messagesContainer">
        <!-- 欢迎消息 -->
        <div v-if="messages.length === 0" class="welcome-message">
          <h3>🌊 欢迎来到海洋世界！</h3>
          <p>我是您的海洋生物专家助手，可以为您介绍各种海洋生物的习性、特征、生态环境等知识</p>
        </div>

        <!-- 快速问题 -->
        <div v-if="messages.length === 0" class="quick-questions">
          <div
              v-for="question in quickQuestions"
              :key="question"
              class="quick-question"
              @click="sendQuickQuestion(question)"
          >
            {{ question }}
          </div>
        </div>

        <!-- 消息列表 -->
        <div
            v-for="message in messages"
            :key="message.id"
            class="message"
            :class="message.type"
        >
          <div
              class="message-content"
              :class="{ 'error-message': message.isError }"
              v-html="message.content.replace(/\n/g, '<br>')"
          ></div>
          <div class="message-time">{{ message.time }}</div>
        </div>

        <!-- 输入指示器 -->
        <div v-if="isTyping" class="typing-indicator">
          <span>海洋助手正在思考</span>
          <div class="typing-dots">
            <div class="typing-dot"></div>
            <div class="typing-dot"></div>
            <div class="typing-dot"></div>
          </div>
        </div>
      </div>

      <!-- 输入区域 -->
      <div class="chat-input-container">
        <div class="chat-input-wrapper">
          <textarea
              v-model="inputMessage"
              ref="chatInput"
              class="chat-input"
              placeholder="请问我关于海洋生物的问题，如：鲨鱼有多少种类？"
              rows="1"
              :disabled="isLoading"
              @keydown="handleKeydown"
              @input="autoResizeTextarea"
          ></textarea>
          <button
              class="send-button"
              :disabled="isLoading || !inputMessage.trim()"
              @click="sendMessage"
          >
            <div v-if="isLoading" class="loading">⟳</div>
            <span v-else>➤</span>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick, watch } from 'vue'
import { marineBiologyApi } from '@/api/marineBiology.js'

// 响应式数据
const messages = ref([])
const inputMessage = ref('')
const isLoading = ref(false)
const isTyping = ref(false)
const systemStatus = ref('UP')
const bubbles = ref([])
const messagesContainer = ref(null)
const chatInput = ref(null)

// 快速问题列表
const quickQuestions = [
  '🐋 鲸鱼是如何呼吸的？',
  '🐬 海豚有多聪明？',
  '🪸 珊瑚礁生态系统',
  '🦑 深海生物有哪些？',
  '🌊 海洋污染的影响'
]

// 消息ID计数器
let messageIdCounter = 0
let bubbleIdCounter = 0

// 生命周期
onMounted(() => {
  createBubbles()
  // 每3秒创建新气泡
  setInterval(createBubbles, 3000)
})

// 监听消息变化，自动滚动到底部
watch(messages, () => {
  nextTick(() => {
    scrollToBottom()
  })
}, { deep: true })

// 检查系统状态
const checkSystemStatus = async () => {
  try {
    const response = await marineBiologyApi.getSystemHealth()
    systemStatus.value = response.status
  } catch (error) {
    console.error('系统状态检查失败:', error)
    systemStatus.value = 'ERROR'
  }
}

// 创建海洋气泡效果
const createBubbles = () => {
  const bubble = {
    id: bubbleIdCounter++,
    style: {
      width: Math.random() * 20 + 10 + 'px',
      height: Math.random() * 20 + 10 + 'px',
      left: Math.random() * 100 + '%',
      animationDuration: (Math.random() * 3 + 4) + 's'
    }
  }

  bubbles.value.push(bubble)

  // 7秒后移除气泡
  setTimeout(() => {
    const index = bubbles.value.findIndex(b => b.id === bubble.id)
    if (index > -1) {
      bubbles.value.splice(index, 1)
    }
  }, 7000)
}

// 自动调整文本框高度
const autoResizeTextarea = () => {
  nextTick(() => {
    if (chatInput.value) {
      chatInput.value.style.height = 'auto'
      chatInput.value.style.height = Math.min(chatInput.value.scrollHeight, 120) + 'px'
    }
  })
}

// 处理键盘事件
const handleKeydown = (e) => {
  if (e.key === 'Enter' && !e.shiftKey) {
    e.preventDefault()
    sendMessage()
  }
}

// 发送消息
const sendMessage = async () => {
  const message = inputMessage.value.trim()
  if (!message || isLoading.value) return

  // 添加用户消息
  addMessage(message, 'user')
  inputMessage.value = ''
  autoResizeTextarea()

  // 设置加载状态
  isLoading.value = true
  isTyping.value = true

  try {
    const response = await marineBiologyApi.query(message)

    isTyping.value = false

    if (response.processResult==='SUCCESS') {
      addMessage(response.queryResultData.response, 'bot')
    } else {
      addMessage(`抱歉，处理您的请求时出现问题：${response.error || response.message}`, 'bot', true)
    }
  } catch (error) {
    console.error('发送消息失败:', error)
    isTyping.value = false
    addMessage('抱歉，网络连接出现问题，请稍后重试。', 'bot', true)
  } finally {
    isLoading.value = false
  }
}

// 快速问题点击
const sendQuickQuestion = (question) => {
  if (isLoading.value) return

  // 移除表情符号，只保留问题文本
  const cleanQuestion = question.replace(/^[🐋🐬🪸🦑🌊]\s*/, '')
  inputMessage.value = cleanQuestion
  sendMessage()
}

// 添加消息
const addMessage = (content, type, isError = false) => {
  const now = new Date()
  const timeStr = now.toLocaleTimeString('zh-CN', {
    hour: '2-digit',
    minute: '2-digit'
  })

  messages.value.push({
    id: messageIdCounter++,
    content,
    type,
    isError,
    time: timeStr
  })
}

// 滚动到底部
const scrollToBottom = () => {
  if (messagesContainer.value) {
    setTimeout(() => {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
    }, 100)
  }
}
</script>

<style scoped>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.app {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Hiragino Sans GB', sans-serif;
  background: linear-gradient(180deg, #87CEEB 0%, #20B2AA 50%, #191970 100%);
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
  position: relative;
  overflow: hidden;
}

/* 海洋动画背景 */
.app::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 20"><path d="M0 10 Q25 0 50 10 T100 10 V20 H0 Z" fill="rgba(255,255,255,0.1)"/></svg>') repeat-x;
  animation: wave 10s linear infinite;
  z-index: 1;
}

@keyframes wave {
  0% { transform: translateX(0); }
  100% { transform: translateX(-100px); }
}

.chat-container {
  width: 100%;
  max-width: 800px;
  height: 80vh;
  max-height: 600px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(15px);
  border-radius: 25px;
  box-shadow: 0 25px 50px rgba(0, 100, 200, 0.2);
  display: flex;
  flex-direction: column;
  overflow: hidden;
  border: 2px solid rgba(32, 178, 170, 0.3);
  position: relative;
  z-index: 10;
}

.chat-header {
  background: linear-gradient(135deg, #20B2AA 0%, #48CAE4 50%, #00B4D8 100%);
  color: white;
  padding: 25px;
  text-align: center;
  position: relative;
  box-shadow: 0 4px 20px rgba(32, 178, 170, 0.3);
}

.chat-header h1 {
  font-size: 28px;
  font-weight: 700;
  margin-bottom: 8px;
  text-shadow: 0 2px 4px rgba(0,0,0,0.2);
}

.chat-header p {
  font-size: 15px;
  opacity: 0.95;
  font-weight: 400;
}

.status-indicator {
  position: absolute;
  top: 25px;
  right: 25px;
  width: 14px;
  height: 14px;
  background: #4ade80;
  border-radius: 50%;
  animation: pulse 2s infinite;
  box-shadow: 0 0 10px rgba(74, 222, 128, 0.5);
}

.status-indicator.status-error {
  background: #f87171;
  box-shadow: 0 0 10px rgba(248, 113, 113, 0.5);
}

@keyframes pulse {
  0%, 100% { opacity: 1; transform: scale(1); }
  50% { opacity: 0.7; transform: scale(1.1); }
}

.chat-messages {
  flex: 1;
  padding: 25px;
  overflow-y: auto;
  background: linear-gradient(180deg, #f0f9ff 0%, #e0f2fe 100%);
}

.message {
  margin-bottom: 25px;
  display: flex;
  animation: slideUp 0.4s ease-out;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.message.user {
  justify-content: flex-end;
}

.message.bot {
  justify-content: flex-start;
}

.message-content {
  max-width: 75%;
  padding: 18px 24px;
  border-radius: 25px;
  position: relative;
  word-wrap: break-word;
  line-height: 1.6;
  font-size: 15px;
}

.message.user .message-content {
  background: linear-gradient(135deg, #20B2AA 0%, #48CAE4 100%);
  color: white;
  border-bottom-right-radius: 8px;
  box-shadow: 0 4px 15px rgba(32, 178, 170, 0.3);
}

.message.bot .message-content {
  background: white;
  color: #1e293b;
  border: 2px solid #e1f5fe;
  border-bottom-left-radius: 8px;
  box-shadow: 0 4px 20px rgba(0, 150, 200, 0.1);
}

.message-time {
  font-size: 12px;
  opacity: 0.7;
  margin-top: 8px;
  text-align: right;
  font-weight: 500;
}

.message.bot .message-time {
  text-align: left;
}

.chat-input-container {
  padding: 25px;
  background: white;
  border-top: 2px solid #e1f5fe;
}

.chat-input-wrapper {
  display: flex;
  gap: 12px;
  align-items: flex-end;
}

.chat-input {
  flex: 1;
  min-height: 50px;
  max-height: 120px;
  padding: 15px 20px;
  border: 2px solid #b3e5fc;
  border-radius: 25px;
  font-size: 15px;
  font-family: inherit;
  resize: none;
  outline: none;
  transition: all 0.3s ease;
  background: #f8fdff;
}

.chat-input:focus {
  border-color: #20B2AA;
  box-shadow: 0 0 0 4px rgba(32, 178, 170, 0.15);
  background: white;
}

.send-button {
  width: 50px;
  height: 50px;
  background: linear-gradient(135deg, #20B2AA 0%, #48CAE4 100%);
  color: white;
  border: none;
  border-radius: 50%;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  font-size: 20px;
  box-shadow: 0 4px 15px rgba(32, 178, 170, 0.3);
}

.send-button:hover:not(:disabled) {
  transform: scale(1.05) translateY(-2px);
  box-shadow: 0 8px 25px rgba(32, 178, 170, 0.4);
}

.send-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.quick-questions {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 20px;
}

.quick-question {
  background: linear-gradient(135deg, #e1f5fe 0%, #b3e5fc 100%);
  color: #0277bd;
  border: 2px solid #81d4fa;
  padding: 10px 16px;
  border-radius: 20px;
  cursor: pointer;
  font-size: 13px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.quick-question:hover {
  background: linear-gradient(135deg, #20B2AA 0%, #48CAE4 100%);
  color: white;
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(32, 178, 170, 0.3);
}

.typing-indicator {
  display: flex;
  align-items: center;
  gap: 10px;
  color: #0277bd;
  margin-bottom: 20px;
  font-weight: 500;
}

.typing-dots {
  display: flex;
  gap: 5px;
}

.typing-dot {
  width: 10px;
  height: 10px;
  background: #48CAE4;
  border-radius: 50%;
  animation: typing 1.4s infinite ease-in-out;
}

.typing-dot:nth-child(1) { animation-delay: -0.32s; }
.typing-dot:nth-child(2) { animation-delay: -0.16s; }

@keyframes typing {
  0%, 80%, 100% {
    transform: scale(0.8);
    opacity: 0.5;
  }
  40% {
    transform: scale(1.2);
    opacity: 1;
  }
}

.welcome-message {
  text-align: center;
  color: #0277bd;
  margin: 50px 0;
}

.welcome-message h3 {
  margin-bottom: 15px;
  color: #01579b;
  font-size: 22px;
  font-weight: 600;
}

.welcome-message p {
  font-size: 16px;
  line-height: 1.6;
}

.error-message {
  background: linear-gradient(135deg, #ffebee 0%, #ffcdd2 100%) !important;
  color: #c62828 !important;
  border-left: 4px solid #f44336 !important;
}

.loading {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

/* 海洋气泡效果 */
.bubble {
  position: absolute;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 50%;
  animation: bubble-rise 6s infinite linear;
  z-index: 1;
  pointer-events: none;
}

@keyframes bubble-rise {
  0% {
    bottom: -50px;
    opacity: 0;
  }
  10% {
    opacity: 1;
  }
  90% {
    opacity: 1;
  }
  100% {
    bottom: 100vh;
    opacity: 0;
  }
}

@media (max-width: 768px) {
  .app {
    padding: 10px;
  }

  .chat-container {
    height: 90vh;
    border-radius: 20px;
  }

  .chat-header {
    padding: 20px;
  }

  .chat-header h1 {
    font-size: 24px;
  }

  .message-content {
    max-width: 85%;
  }

  .chat-messages {
    padding: 20px;
  }

  .chat-input-container {
    padding: 20px;
  }
}
</style>