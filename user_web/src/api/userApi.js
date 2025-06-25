import http from '@/utils/request.js'

/**
 * 用户API服务
 */
export const userApi = {
  /**
   * 分页查询用户列表
   * @param {Object} params 查询参数
   * @returns {Promise}
   */
  getUserListByPage(params) {
    return http.get('/users/doUserListByPage', params)
  },

  /**
   * 根据ID查询用户信息
   * @param {String|Number} id 用户ID
   * @returns {Promise}
   */
  getUserById(id) {
    return http.get(`/users/doFindUserById/${id}`)
  },

  /**
   * 添加用户
   * @param {Object} userData 用户数据
   * @returns {Promise}
   */
  addUser(userData) {
    return http.post('/users/doUserAddSave', userData)
  },

  /**
   * 更新用户
   * @param {Object} userData 用户数据
   * @returns {Promise}
   */
  updateUser(userData) {
    return http.put('/users/doUserEditSave', userData)
  },

  /**
   * 删除用户
   * @param {String|Number} id 用户ID
   * @returns {Promise}
   */
  deleteUser(id) {
    return http.delete(`/users/doUserDelete/${id}`)
  },

  /**
   * 批量删除用户
   * @param {Array} ids 用户ID数组
   * @returns {Promise}
   */
  batchDeleteUsers(ids) {
    return http.delete('/users/doBatchDelete', { ids })
  },

  /**
   * 更新用户状态
   * @param {String|Number} id 用户ID
   * @param {Number} status 状态值
   * @returns {Promise}
   */
  updateUserStatus(id, status) {
    return http.put(`/users/doUpdateStatus/${id}`, { status })
  },

  /**
   * 重置用户密码
   * @param {String|Number} id 用户ID
   * @param {String} newPassword 新密码
   * @returns {Promise}
   */
  resetPassword(id, newPassword) {
    return http.put(`/users/doResetPassword/${id}`, { password: newPassword })
  },

  /**
   * 上传头像
   * @param {FormData} formData 文件数据
   * @returns {Promise}
   */
  uploadAvatar(formData) {
    return http.upload('/upload/avatar', formData)
  }
}

export default userApi