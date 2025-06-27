import http from '@/utils/request'

const marineSpeciesApi = {
  getSpeciesDetail(id) {
    return http.get(`/marine/api/detail/${id}`)  // 注意接口路径要跟后端对上
  }
}

export default marineSpeciesApi
