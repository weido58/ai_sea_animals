<template>
  <div class="dashboard">
    <h1>仪表板</h1>
    <div class="dashboard-content">
      <!-- 统计卡片 -->
      <div class="stats-grid">
        <div class="stat-card" v-for="stat in stats" :key="stat.title">
          <div class="stat-icon" :style="{ backgroundColor: stat.color }">
            <el-icon><component :is="stat.icon" /></el-icon>
          </div>
          <div class="stat-info">
            <h3>{{ stat.value }}</h3>
            <p>{{ stat.title }}</p>
          </div>
        </div>
      </div>

      <!-- 欢迎信息 -->
      <el-card class="welcome-card">
        <h2>欢迎回来，{{ userStore.username }}！</h2>
        <p>今天是 {{ currentDate }}，祝您工作愉快！</p>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useUserStore } from '../stores/user'

const userStore = useUserStore()

// 统计数据
const stats = ref([
  { title: '总用户数', value: '1,234', icon: 'User', color: '#409eff' },
  { title: '今日访问', value: '856', icon: 'View', color: '#67c23a' },
  { title: '系统消息', value: '12', icon: 'Message', color: '#e6a23c' },
  { title: '待处理', value: '5', icon: 'Warning', color: '#f56c6c' }
])

// 当前日期
const currentDate = computed(() => {
  return new Date().toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    weekday: 'long'
  })
})
</script>

<style scoped>
.dashboard {
  padding: 20px;
}

.dashboard h1 {
  margin-bottom: 20px;
  color: #333;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.stat-card {
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  display: flex;
  align-items: center;
}

.stat-icon {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  margin-right: 15px;
}

.stat-info h3 {
  margin: 0;
  font-size: 24px;
  color: #333;
}

.stat-info p {
  margin: 5px 0 0 0;
  color: #666;
}

.welcome-card {
  margin-top: 20px;
}

.welcome-card h2 {
  margin: 0 0 10px 0;
  color: #333;
}

.welcome-card p {
  margin: 0;
  color: #666;
}
</style>