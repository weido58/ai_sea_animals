<template>
  <div class="container my-5" v-if="species">
    <div class="species-card mx-auto shadow p-0 rounded-4 overflow-hidden" style="max-width: 900px;">
      <!-- 头部 -->
      <div class="species-header text-white text-center p-4">
        <h1 class="species-title mb-2">{{ species.chineseName || '未知物种' }}</h1>
        <p class="species-subtitle fst-italic mb-1">{{ species.scientificName }}</p>
        <p class="species-subtitle fst-italic" v-if="species.commonName">俗名: {{ species.commonName }}</p>
      </div>

      <!-- 详细信息 -->
      <div class="info-section p-4 bg-white">
        <div class="row g-3">
          <!-- 学名 -->
          <div class="col-md-6" v-if="species.scientificName">
            <div class="info-item p-3 rounded-3 border-start border-4 border-primary bg-light">
              <div class="info-label d-flex align-items-center mb-2">
                <i class="fas fa-microscope me-2 text-primary"></i>
                学名 (Scientific Name)
              </div>
              <div class="info-value text-secondary">{{ species.scientificName }}</div>
            </div>
          </div>

          <!-- 俗名 -->
          <div class="col-md-6" v-if="species.commonName">
            <div class="info-item p-3 rounded-3 border-start border-4 border-primary bg-light">
              <div class="info-label d-flex align-items-center mb-2">
                <i class="fas fa-comment me-2 text-primary"></i>
                俗名 (Common Name)
              </div>
              <div class="info-value text-secondary">{{ species.commonName }}</div>
            </div>
          </div>

          <!-- 保护状态 -->
          <div class="col-12" v-if="species.conservationStatus">
            <div class="info-item p-3 rounded-3 border-start border-4 border-primary bg-light">
              <div class="info-label d-flex align-items-center mb-2">
                <i class="fas fa-shield-alt me-2 text-primary"></i>
                保护状态 (Conservation Status)
              </div>
              <div>
                <span
                  class="conservation-status px-3 py-1 rounded-pill fw-semibold"
                  :class="conservationStatusClass(species.conservationStatus)"
                >
                  {{ species.conservationStatus }}
                </span>
              </div>
            </div>
          </div>

          <!-- 分类信息 -->
          <div class="col-12" v-if="species.classification">
            <div class="info-item p-3 rounded-3 border-start border-4 border-primary bg-light">
              <div class="info-label d-flex align-items-center mb-2">
                <i class="fas fa-sitemap me-2 text-primary"></i>
                分类信息 (Classification)
              </div>
              <pre class="classification-tree bg-white p-3 rounded" style="max-height: 200px; overflow:auto;">
{{ formattedClassification }}
              </pre>
            </div>
          </div>

          <!-- 形态特征 -->
          <div class="col-md-6" v-if="species.characteristics">
            <div class="info-item p-3 rounded-3 border-start border-4 border-primary bg-light">
              <div class="info-label d-flex align-items-center mb-2">
                <i class="fas fa-eye me-2 text-primary"></i>
                形态特征 (Characteristics)
              </div>
              <div class="info-value text-secondary">{{ species.characteristics }}</div>
            </div>
          </div>

          <!-- 体型范围 -->
          <div class="col-md-6" v-if="species.sizeRange">
            <div class="info-item p-3 rounded-3 border-start border-4 border-primary bg-light">
              <div class="info-label d-flex align-items-center mb-2">
                <i class="fas fa-ruler me-2 text-primary"></i>
                体型范围 (Size Range)
              </div>
              <div class="info-value text-secondary">{{ species.sizeRange }}</div>
            </div>
          </div>

          <!-- 栖息地 -->
          <div class="col-md-6" v-if="species.habitat">
            <div class="info-item p-3 rounded-3 border-start border-4 border-primary bg-light">
              <div class="info-label d-flex align-items-center mb-2">
                <i class="fas fa-home me-2 text-primary"></i>
                栖息地 (Habitat)
              </div>
              <div class="info-value text-secondary">{{ species.habitat }}</div>
            </div>
          </div>

          <!-- 分布区域 -->
          <div class="col-md-6" v-if="species.distribution">
            <div class="info-item p-3 rounded-3 border-start border-4 border-primary bg-light">
              <div class="info-label d-flex align-items-center mb-2">
                <i class="fas fa-globe me-2 text-primary"></i>
                分布区域 (Distribution)
              </div>
              <div class="info-value text-secondary">{{ species.distribution }}</div>
            </div>
          </div>

          <!-- 食性 -->
          <div class="col-12" v-if="species.diet">
            <div class="info-item p-3 rounded-3 border-start border-4 border-primary bg-light">
              <div class="info-label d-flex align-items-center mb-2">
                <i class="fas fa-utensils me-2 text-primary"></i>
                食性 (Diet)
              </div>
              <div class="info-value text-secondary">{{ species.diet }}</div>
            </div>
          </div>

          <!-- 详细描述 -->
          <div class="col-12" v-if="species.description">
            <div class="info-item p-3 rounded-3 border-start border-4 border-primary bg-light">
              <div class="info-label d-flex align-items-center mb-2">
                <i class="fas fa-info-circle me-2 text-primary"></i>
                详细描述 (Description)
              </div>
              <div
                class="info-value text-secondary"
                v-html="formattedDescription"
              ></div>
            </div>
          </div>

          <!-- 图片展示 -->
          <div class="col-12" v-if="species.imageUrls && imageList.length">
            <div class="info-item p-3 rounded-3 border-start border-4 border-primary bg-light">
              <div class="info-label d-flex align-items-center mb-2">
                <i class="fas fa-images me-2 text-primary"></i>
                参考图片 (Reference Images)
              </div>
              <div class="image-gallery d-grid gap-3" style="grid-template-columns: repeat(auto-fill,minmax(200px,1fr));">
                <img
                  v-for="(url, index) in imageList"
                  :key="index"
                  :src="fullImageUrl(url)"
                  class="species-image rounded"
                  @click="showModal(url)"
                  style="cursor: pointer;"
                  alt="物种图片"
                />
              </div>
            </div>
          </div>

          <!-- 时间信息 -->
          <div class="col-md-6" v-if="species.createdTime">
            <div class="info-item p-3 rounded-3 border-start border-4 border-primary bg-light">
              <div class="info-label d-flex align-items-center mb-2">
                <i class="fas fa-calendar-plus me-2 text-primary"></i>
                创建时间
              </div>
              <div class="info-value text-secondary">{{ formatTime(species.createdTime) }}</div>
            </div>
          </div>

          <div class="col-md-6" v-if="species.updatedTime">
            <div class="info-item p-3 rounded-3 border-start border-4 border-primary bg-light">
              <div class="info-label d-flex align-items-center mb-2">
                <i class="fas fa-calendar-check me-2 text-primary"></i>
                更新时间
              </div>
              <div class="info-value text-secondary">{{ formatTime(species.updatedTime) }}</div>
            </div>
          </div>
        </div>

        <!-- 操作按钮 -->
        <div class="text-center mt-4">
          <button class="btn btn-primary me-3" @click="goBack">
            <i class="fas fa-arrow-left"></i> 返回上一页
          </button>
          <router-link to="/species/list" class="btn btn-primary">
            <i class="fas fa-list"></i> 返回列表
          </router-link>
        </div>
      </div>
    </div>

    <!-- 图片模态框 -->
    <b-modal v-model="showImageModal" size="lg" centered title="物种图片" @hide="modalImageUrl = ''">
      <img :src="modalImageUrl" class="w-100 rounded" alt="物种图片" />
    </b-modal>
  </div>

  <div v-else class="text-center text-white py-5">
    <p>加载中...</p>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import marineBiologyImageApi from '@/api/marineBiologyImageApi'

const route = useRoute()
const router = useRouter()
const species = ref(null)
const imageList = ref([])

const showImageModal = ref(false)
const modalImageUrl = ref('')

const goBack = () => {
  router.back()
}

const showModal = (url) => {
  modalImageUrl.value = url
  showImageModal.value = true
}

onMounted(async () => {
  const id = route.params.id
  try {
    const res = await marineBiologyImageApi.getSpeciesById(id)
    if (res.success) {
      species.value = res.data
      imageList.value = JSON.parse(res.data.imageUrls || '[]')
    } else {
      console.error('接口返回异常', res)
    }
  } catch (e) {
    console.error('获取详情失败', e)
  }
})

const formattedClassification = computed(() => {
  if (!species.value || !species.value.classification) return ''
  try {
    const obj = JSON.parse(species.value.classification)
    return JSON.stringify(obj, null, 2)
  } catch {
    return species.value.classification
  }
})

const formattedDescription = computed(() => {
  if (!species.value || !species.value.description) return ''
  // 支持换行转 <br>
  return species.value.description.replace(/\n/g, '<br>')
})

const formatTime = (isoStr) => {
  return new Date(isoStr).toLocaleString()
}

const fullImageUrl = (relativePath) => {
  if (relativePath.startsWith('http')) return relativePath
  return '/' + relativePath
}

const conservationStatusClass = (status) => {
  const map = {
    '低风险': 'status-low-risk bg-success text-white',
    '近危': 'status-near-threatened bg-warning text-dark',
    '易危': 'status-vulnerable bg-danger text-white',
    '濒危': 'status-endangered bg-danger text-white',
    '极危': 'status-critically-endangered bg-danger text-white',
    '灭绝': 'status-extinct bg-secondary text-white',
  }
  // 返回对应class，默认用 bg-secondary
  return map[status] || 'bg-secondary text-white'
}
</script>

<style scoped>
        body {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        }
        .species-card {
            background: rgba(255, 255, 255, 0.95);
            backdrop-filter: blur(10px);
            border-radius: 20px;
            box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
              margin: 2rem auto; /* ❗将 margin 设置为 auto 实现水平居中 */
              max-width: 900px;   /* ❗加一个最大宽度，让它不要太宽 */
            overflow: hidden;
        }
        .species-header {
            background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
            color: white;
            padding: 2rem;
            text-align: center;
        }
        .species-title {
            font-size: 2.5rem;
            font-weight: 700;
            margin-bottom: 0.5rem;
            text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.3);
        }
        .species-subtitle {
            font-size: 1.2rem;
            opacity: 0.9;
            font-style: italic;
        }
        .info-section {
            padding: 2rem;
        }
        .info-item {
            background: rgba(79, 172, 254, 0.1);
            border-left: 4px solid #4facfe;
            padding: 1rem;
            margin-bottom: 1rem;
            border-radius: 0 10px 10px 0;
            transition: all 0.3s ease;
        }
        .info-item:hover {
            background: rgba(79, 172, 254, 0.2);
            transform: translateX(5px);
        }
        .info-label {
            font-weight: 600;
            color: #2c3e50;
            margin-bottom: 0.5rem;
            display: flex;
            align-items: center;
        }
        .info-label i {
            margin-right: 0.5rem;
            color: #4facfe;
        }
        .info-value {
            color: #555;
            line-height: 1.6;
        }
        .image-gallery {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
            gap: 1rem;
            margin-top: 1rem;
        }
        .species-image {
            width: 100%;
            height: 200px;
            object-fit: cover;
            border-radius: 10px;
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.2);
            transition: transform 0.3s ease;
            cursor: pointer;
        }
        .species-image:hover {
            transform: scale(1.05);
        }
        .conservation-status {
            display: inline-block;
            padding: 0.5rem 1rem;
            border-radius: 20px;
            font-weight: 600;
            text-transform: uppercase;
            font-size: 0.9rem;
        }
        .status-low-risk { background: #d4edda; color: #155724; }
        .status-near-threatened { background: #fff3cd; color: #856404; }
        .status-vulnerable { background: #f8d7da; color: #721c24; }
        .status-endangered { background: #f5c6cb; color: #721c24; }
        .status-critically-endangered { background: #f1b0b7; color: #721c24; }
        .status-extinct { background: #d6d8db; color: #383d41; }
        .btn-back {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border: none;
            padding: 0.75rem 2rem;
            border-radius: 25px;
            color: white;
            font-weight: 600;
            transition: all 0.3s ease;
        }
        .btn-back:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3);
            color: white;
        }
        .error-container {
            text-align: center;
            padding: 3rem;
            color: white;
        }
        .classification-tree {
            background: rgba(79, 172, 254, 0.05);
            border-radius: 10px;
            padding: 1rem;
            font-family: 'Courier New', monospace;
        }
        .modal-content {
            border-radius: 15px;
            border: none;
        }
        .modal-body {
            padding: 0;
        }
        .modal-image {
            width: 100%;
            height: auto;
            border-radius: 15px;
        }
</style>
