// src/api/alarm.js
import request from '@/utils/request'

// 触发SOS报警
export function triggerSOSApi() {
  return request({
    url: '/alarm/sos',
    method: 'post'
  })
}

// 获取报警列表
export function getAlarmListApi(params) {
  return request({
    url: '/alarm/list',
    method: 'get',
    params
  })
}

// 处理报警
export function handleAlarmApi(data) {
  return request({
    url: '/alarm/handle',
    method: 'post',
    data
  })
}

// 获取报警详情
export function getAlarmDetailApi(id) {
  return request({
    url: `/alarm/${id}`,
    method: 'get'
  })
}
