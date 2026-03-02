// src/api/admin.js
import request from '@/utils/request'

// 获取统计数据
export function getStatisticsApi() {
  return request({
    url: '/admin/statistics',
    method: 'get'
  })
}

// 获取最近报警
export function getRecentAlarmsApi(params) {
  return request({
    url: '/admin/alarms/recent',
    method: 'get',
    params
  })
}

// 获取用户列表
export function getUserListApi(params) {
  return request({
    url: '/admin/users',
    method: 'get',
    params
  })
}

// 添加用户
export function addUserApi(data) {
  return request({
    url: '/admin/user/add',
    method: 'post',
    data
  })
}

// 更新用户
export function updateUserApi(data) {
  return request({
    url: '/admin/user/update',
    method: 'put',
    data
  })
}

// 切换用户状态
export function toggleUserStatusApi(data) {
  return request({
    url: '/admin/user/toggle-status',
    method: 'post',
    data
  })
}

// 获取订单列表
export function getOrderListApi(params) {
  return request({
    url: '/admin/orders',
    method: 'get',
    params
  })
}

// 导出数据
export function exportDataApi(params) {
  return request({
    url: '/admin/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}
