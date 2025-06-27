import http from '@/utils/request.js'

/**
 * 海洋生物智能助手 API 服务
 */
export const marineBiologyApi = {
    /**
     * 发送查询消息
     * @param {string} query 查询内容
     * @returns {Promise}
     */
    query(query) {
        return http.post('/marineBiology/query', query)
    }
}

export default marineBiologyApi