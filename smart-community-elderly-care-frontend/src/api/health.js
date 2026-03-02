// src/api/health.js
import request from '@/utils/request'

// 提交健康数据
export function submitHealthDataApi(data) {
  return request({
    url: '/health/submit',
    method: 'post',
    data
  })
}

// 获取健康历史
export function getHealthHistoryApi(params) {
  return request({
    url: '/health/history',
    method: 'get',
    params
  })
}

// 获取健康趋势
export function getHealthTrendApi(params) {
  return request({
    url: '/health/trend',
    method: 'get',
    params
  })
}

// 获取老人健康数据（家属端用）
export function getElderHealthApi(elderId) {
  return request({
    url: `/health/elder/${elderId}`,
    method: 'get'
  })
}
