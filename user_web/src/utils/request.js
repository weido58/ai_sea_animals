import axios from 'axios'
import { ElMessage } from 'element-plus'

// 创建axios实例
const request = axios.create({
  baseURL: '/api', // 基础URL
  timeout: 10000,  // 请求超时时间
  headers: {
    'Content-Type': 'application/json;charset=UTF-8'
  }
})

// 请求拦截器
request.interceptors.request.use(
  config => {
    // 在发送请求之前做些什么
    // 可以在这里添加token等认证信息
    // const token = getToken()
    // if (token) {
    //   config.headers.Authorization = `Bearer ${token}`
    // }
    return config
  },
  error => {
    // 对请求错误做些什么
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  response => {
    // 对响应数据做点什么
    const res = response.data

    // 根据业务逻辑处理响应
    if (res.processResult === 'SUCCESS') {
      return res
    } else if (res.processResult === 'FAILED') {
      ElMessage.error(res.errorMessage || '请求失败')
      return Promise.reject(new Error(res.errorMessage || '请求失败'))
    }

    return res
  },
  error => {
    // 对响应错误做点什么
    console.error('响应错误:', error)

    // 处理HTTP状态码错误
    let message = '请求失败'
    if (error.response) {
      switch (error.response.status) {
        case 401:
          message = '未授权，请重新登录'
          break
        case 403:
          message = '拒绝访问'
          break
        case 404:
          message = '请求地址出错'
          break
        case 408:
          message = '请求超时'
          break
        case 500:
          message = '服务器内部错误'
          break
        case 501:
          message = '服务未实现'
          break
        case 502:
          message = '网关错误'
          break
        case 503:
          message = '服务不可用'
          break
        case 504:
          message = '网关超时'
          break
        case 505:
          message = 'HTTP版本不受支持'
          break
        default:
          message = `连接错误${error.response.status}`
      }
    } else if (error.code === 'ECONNABORTED') {
      message = '请求超时'
    } else if (error.message.includes('Network Error')) {
      message = '网络连接异常'
    }

    ElMessage.error(message)
    return Promise.reject(error)
  }
)

// 封装常用的请求方法
const http = {
  get(url, params = {}) {
    return request({
      method: 'get',
      url,
      params
    })
  },

  post(url, data = {}) {
    return request({
      method: 'post',
      url,
      data
    })
  },

  put(url, data = {}) {
    return request({
      method: 'put',
      url,
      data
    })
  },

  delete(url, params = {}) {
    return request({
      method: 'delete',
      url,
      params
    })
  },

  // 文件上传
  upload(url, formData) {
    return request({
      method: 'post',
      url,
      data: formData,
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  }
}

export default http