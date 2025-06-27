<template>
  <div class="page-wrapper">
    <div class="container">
      <h1>🐠 海洋生物识别系统</h1>
      <p class="subtitle">上传海洋生物图片，AI智能识别物种信息</p>

      <!-- 上传区域 -->
      <div
        class="upload-area"
        @click="triggerFileSelect"
        @dragover.prevent="onDragOver"
        @dragleave.prevent="onDragLeave"
        @drop.prevent="onDrop"
        :class="{ dragover: isDragOver }"
      >
        <input
          type="file"
          ref="fileInput"
          accept="image/*"
          @change="handleFileChange"
          class="file-input"
        />
        <div v-if="!previewUrl" class="upload-icon">📸</div>
        <div v-if="!previewUrl" class="upload-text">点击选择图片或拖拽图片到此处</div>
        <div v-if="!previewUrl" class="upload-hint">支持 JPG、PNG、GIF 格式，最大 10MB</div>
        <img v-if="previewUrl" :src="previewUrl" class="preview-image" alt="预览图片" />
      </div>

      <!-- 进度条 -->
      <div class="progress-bar" v-show="loading">
        <div class="progress-fill" :style="{ width: progress + '%' }"></div>
      </div>

      <!-- 加载状态 -->
      <div class="loading" v-if="loading">
        <div class="spinner"></div>
        <p>AI正在识别中，请稍候...</p>
      </div>

      <!-- 错误提示 -->
      <div v-if="errorMsg" class="alert alert-error">
        {{ errorMsg }}
      </div>

      <!-- 按钮 -->
      <button
        class="upload-btn"
        :disabled="!selectedFile || loading"
        @click="uploadImage"
      >
        🚀 开始识别
      </button>

      <!-- 导航按钮 -->
      <div class="nav-buttons">
        <a href="/marine/list" class="nav-btn">📋 查看识别记录</a>
        <a href="/marine/batch" class="nav-btn">📦 批量上传</a>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import marineBiologyImageApi from '@/api/marineBiologyImageApi'

const fileInput = ref(null)
const selectedFile = ref(null)
const previewUrl = ref('')
const loading = ref(false)
const errorMsg = ref('')
const progress = ref(0)
const isDragOver = ref(false)

const triggerFileSelect = () => {
  fileInput.value.click()
}

const handleFileChange = (e) => {
  const file = e.target.files[0]
  if (!file) return

  if (!file.type.startsWith('image/')) {
    alert('请选择图片文件！')
    return
  }
  if (file.size > 10 * 1024 * 1024) {
    alert('图片文件大小不能超过10MB！')
    return
  }

  selectedFile.value = file
  previewUrl.value = URL.createObjectURL(file)
  errorMsg.value = ''
}

const onDragOver = () => {
  isDragOver.value = true
}
const onDragLeave = () => {
  isDragOver.value = false
}
const onDrop = (e) => {
  isDragOver.value = false
  const files = e.dataTransfer.files
  if (files.length > 0) {
    const file = files[0]
    if (!file.type.startsWith('image/')) {
      alert('请选择图片文件！')
      return
    }
    if (file.size > 10 * 1024 * 1024) {
      alert('图片文件大小不能超过10MB！')
      return
    }
    selectedFile.value = file
    previewUrl.value = URL.createObjectURL(file)
    errorMsg.value = ''
  }
}

const uploadImage = async () => {
  if (!selectedFile.value) return

  loading.value = true
  errorMsg.value = ''
  progress.value = 0

  // 模拟进度条
  const progressInterval = setInterval(() => {
    progress.value += Math.random() * 15
    if (progress.value > 90) {
      progress.value = 90
      clearInterval(progressInterval)
    }
  }, 200)

  try {
    const res = await marineBiologyImageApi.uploadImage(selectedFile.value)
    clearInterval(progressInterval)
    progress.value = 100

    setTimeout(() => {
      loading.value = false
      if (res.success) {
        window.location.href = `/marine/detail/${res.data.id}`
      } else {
        errorMsg.value = '识别失败：' + res.message
        progress.value = 0
      }
    }, 500)
  } catch (e) {
    clearInterval(progressInterval)
    loading.value = false
    progress.value = 0
    errorMsg.value = '上传失败，请稍后重试'
    console.error(e)
  }
}
</script>

<style scoped>
/* 页面全局背景 */
.page-wrapper {
  min-height: 100vh;
  padding: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  justify-content: center;
  align-items: center;
}

/* 白色半透明容器 */
.container {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
  padding: 40px;
  max-width: 600px;
  width: 100%;
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10px);
  text-align: center;
}

h1 {
  color: #333;
  font-size: 2.5rem;
  font-weight: 600;
  margin-bottom: 10px;
}

.subtitle {
  color: #666;
  font-size: 1.1rem;
  margin-bottom: 30px;
}

.upload-area {
  border: 3px dashed #4f46e5;
  border-radius: 15px;
  padding: 40px 20px;
  margin: 30px 0;
  cursor: pointer;
  transition: all 0.3s ease;
  background: linear-gradient(135deg, #f8fafc 0%, #e0e7ff 100%);
  position: relative;
  overflow: hidden;
  user-select: none;
}

.upload-area.dragover {
  border-color: #1e40af;
  background: linear-gradient(135deg, #dbeafe 0%, #bfdbfe 100%);
  transform: scale(1.02);
}

.upload-area:hover {
  border-color: #3730a3;
  background: linear-gradient(135deg, #e0e7ff 0%, #c7d2fe 100%);
  transform: translateY(-2px);
}

.upload-icon {
  font-size: 3rem;
  color: #4f46e5;
  margin-bottom: 15px;
}

.upload-text {
  color: #4b5563;
  font-size: 1.1rem;
  margin-bottom: 10px;
}

.upload-hint {
  color: #9ca3af;
  font-size: 0.9rem;
}

.file-input {
  display: none;
}

.preview-image {
  max-width: 100%;
  max-height: 300px;
  border-radius: 10px;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
  margin-top: 10px;
}

/* 进度条 */
.progress-bar {
  width: 100%;
  height: 4px;
  background: #e5e7eb;
  border-radius: 2px;
  overflow: hidden;
  margin: 20px 0;
  transition: opacity 0.3s ease;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #4f46e5, #7c3aed);
  width: 0%;
  transition: width 0.3s ease;
}

/* 加载中 */
.loading {
  margin: 20px 0;
  color: #4f46e5;
  font-weight: 500;
  user-select: none;
}

.spinner {
  border: 4px solid #f3f4f6;
  border-top: 4px solid #4f46e5;
  border-radius: 50%;
  width: 40px;
  height: 40px;
  animation: spin 1s linear infinite;
  margin: 0 auto 10px auto;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* 错误提示 */
.alert-error {
  background: linear-gradient(135deg, #fee2e2 0%, #fecaca 100%);
  color: #991b1b;
  border-left: 4px solid #ef4444;
  padding: 15px 20px;
  border-radius: 10px;
  margin: 20px 0;
  font-weight: 500;
}

/* 按钮 */
.upload-btn {
  background: linear-gradient(135deg, #4f46e5 0%, #7c3aed 100%);
  color: white;
  border: none;
  padding: 12px 30px;
  border-radius: 25px;
  font-size: 1.1rem;
  cursor: pointer;
  transition: all 0.3s ease;
  margin: 20px 10px 0 10px;
  font-weight: 500;
}

.upload-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.upload-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(79, 70, 229, 0.4);
}

/* 导航按钮 */
.nav-buttons {
  margin-top: 30px;
}

.nav-btn {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  color: white;
  text-decoration: none;
  padding: 10px 20px;
  border-radius: 20px;
  margin: 10px 5px 0 5px;
  display: inline-block;
  transition: all 0.3s ease;
}

.nav-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(16, 185, 129, 0.4);
  text-decoration: none;
  color: white;
}

@media (max-width: 768px) {
  .container {
    padding: 20px;
    margin: 10px;
  }
  h1 {
    font-size: 2rem;
  }
  .upload-area {
    padding: 30px 15px;
  }
}
</style>
