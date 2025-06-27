import http from '@/utils/request'

// 图片识别相关 API
const marineBiologyImageApi = {
  // 上传图片进行识别
  uploadImage(imageFile) {
    const formData = new FormData()
    formData.append('imageFile', imageFile)
    return http.upload('/marine/api/upload', formData)
  },

  // 根据识别后的 ID 获取物种详情
  getSpeciesById(id) {
    return http.get(`/marine/api/detail/${id}`)
  }
}

export default marineBiologyImageApi
