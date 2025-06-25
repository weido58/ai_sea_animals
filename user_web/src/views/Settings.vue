<template>
  <div class="settings-page">
    <h1>系统设置</h1>

    <div class="settings-content">
      <!-- 基本设置 -->
      <el-card class="setting-card">
        <template #header>
          <span>基本设置</span>
        </template>

        <el-form :model="basicSettings" label-width="120px">
          <el-form-item label="系统名称">
            <el-input v-model="basicSettings.systemName" />
          </el-form-item>
          <el-form-item label="系统版本">
            <el-input v-model="basicSettings.version" disabled />
          </el-form-item>
          <el-form-item label="维护模式">
            <el-switch v-model="basicSettings.maintenanceMode" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="saveBasicSettings">保存设置</el-button>
          </el-form-item>
        </el-form>
      </el-card>

      <!-- 安全设置 -->
      <el-card class="setting-card">
        <template #header>
          <span>安全设置</span>
        </template>

        <el-form :model="securitySettings" label-width="120px">
          <el-form-item label="密码强度">
            <el-select v-model="securitySettings.passwordStrength">
              <el-option label="低" value="low" />
              <el-option label="中" value="medium" />
              <el-option label="高" value="high" />
            </el-select>
          </el-form-item>
          <el-form-item label="会话超时">
            <el-input-number
              v-model="securitySettings.sessionTimeout"
              :min="10"
              :max="1440"
            />
            <span style="margin-left: 10px;">分钟</span>
          </el-form-item>
          <el-form-item label="双因子认证">
            <el-switch v-model="securitySettings.twoFactorAuth" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="saveSecuritySettings">保存设置</el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { reactive } from 'vue'
import { ElMessage } from 'element-plus'

// 基本设置数据
const basicSettings = reactive({
  systemName: '管理系统',
  version: 'v1.0.0',
  maintenanceMode: false
})

// 安全设置数据
const securitySettings = reactive({
  passwordStrength: 'medium',
  sessionTimeout: 30,
  twoFactorAuth: false
})

// 保存基本设置方法
// 作用：保存系统基本配置信息
const saveBasicSettings = async () => {
  try {
    // 模拟保存操作
    await new Promise(resolve => setTimeout(resolve, 500))
    ElMessage.success('基本设置保存成功')
  } catch (error) {
    ElMessage.error('保存失败')
  }
}

// 保存安全设置方法
// 作用：保存系统安全相关配置
const saveSecuritySettings = async () => {
  try {
    // 模拟保存操作
    await new Promise(resolve => setTimeout(resolve, 500))
    ElMessage.success('安全设置保存成功')
  } catch (error) {
    ElMessage.error('保存失败')
  }
}
</script>

<style scoped>
.settings-page {
  padding: 20px;
}

.settings-page h1 {
  margin-bottom: 20px;
  color: #333;
}

.settings-content {
  display: grid;
  gap: 20px;
}

.setting-card {
  width: 100%;
  max-width: 600px;
}
</style>