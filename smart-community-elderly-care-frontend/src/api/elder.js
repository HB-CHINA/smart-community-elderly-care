// src/api/elder.js 修正版本
import request from '@/utils/request'

/**
 * 获取养老人员列表（适配后端MyBatis-Plus分页）
 * @param {Object} params - 分页+查询参数 {page: 1, pageSize: 10, name: '张三'}
 * @returns {Promise}
 */
export function getElderList(params) {
  // 适配后端分页参数（page→pageNum，统一格式）
  const submitParams = {
    pageNum: params.page || 1,
    pageSize: params.pageSize || 10,
    ...params
  }
  delete submitParams.page // 删除多余参数，避免后端接收异常
  return request({
    url: '/api/elderly/list', // 标准后端接口路径（可根据你的实际路径修改）
    method: 'GET', // 列表查询统一用GET，匹配后端
    params: submitParams // GET请求用params传递
  })
}

/**
 * 添加养老人员（适配后端参数格式）
 * @param {Object} data - 养老人员信息 {name, birthDate, phone, status}
 * @returns {Promise}
 */
export function addElder(data) {
  // 格式转换：匹配后端接收的参数类型
  const submitData = {
    ...data,
    // 日期转换：前端日期选择器→后端时间戳
    birthDate: data.birthDate ? new Date(data.birthDate).getTime() : null,
    // 枚举值转换：前端文字→后端数字（适配常见养老项目状态码）
    status: data.status === '正常' ? 1 : data.status === '禁用' ? 0 : 1,
    // 手机号格式化：去除空格，匹配后端校验规则
    phone: data.phone ? data.phone.replace(/\s+/g, '') : ''
  }
  return request({
    url: '/api/elderly/add', // 标准后端接口路径
    method: 'POST', // 新增用POST，匹配后端
    data: submitData // POST请求用data传递
  })
}

/**
 * 修改养老人员信息
 * @param {Object} data - 养老人员信息 {id, name, birthDate, phone, status}
 * @returns {Promise}
 */
export function updateElder(data) {
  const submitData = {
    ...data,
    birthDate: data.birthDate ? new Date(data.birthDate).getTime() : null,
    status: data.status === '正常' ? 1 : 0,
    phone: data.phone ? data.phone.replace(/\s+/g, '') : ''
  }
  return request({
    url: '/api/elderly/update',
    method: 'PUT', // 修改用PUT，匹配后端RESTful规范
    data: submitData
  })
}

/**
 * 删除养老人员
 * @param {Number|String} id - 养老人员ID
 * @returns {Promise}
 */
export function deleteElder(id) {
  return request({
    url: `/api/elderly/delete/${id}`, // 路径传参，匹配后端
    method: 'DELETE' // 删除用DELETE，匹配后端
  })
}

/**
 * 获取养老人员健康数据
 * @param {Number|String} elderId - 养老人员ID
 * @returns {Promise}
 */
export function getElderHealth(elderId) {
  return request({
    url: `/api/elderly/health/${elderId}`,
    method: 'GET'
  })
}
