// src/api/alarm.js
import request from '@/utils/request'

// 获取所有报警（后端路径: GET /alarms）
export function getAlarmListApi(params) {
  return request({
    url: '/alarms',
    method: 'get',
    params
  })
}

// 获取待处理报警（后端路径: GET /alarms/pending）
export function getPendingAlarmsApi() {
  return request({
    url: '/alarms/pending',
    method: 'get'
  })
}

// 获取老人的报警记录（后端路径: GET /alarms/elder/{elderId}）
export function getAlarmsByElderApi(elderId) {
  return request({
    url: `/alarms/elder/${elderId}`,
    method: 'get'
  })
}

// 手动触发报警/SOS（后端路径: POST /alarms/trigger）
export function triggerSOSApi(data) {
  return request({
    url: '/alarms/trigger',
    method: 'post',
    data
  })
}

// 处理报警（后端路径: POST /alarms/{alarmId}/resolve）
export function handleAlarmApi(alarmId, data) {
  return request({
    url: `/alarms/${alarmId}/resolve`,
    method: 'post',
    data
  })
}

// 统计待处理报警数量（后端路径: GET /alarms/count/pending）
export function countPendingAlarmsApi() {
  return request({
    url: '/alarms/count/pending',
    method: 'get'
  })
}

// 按类型获取报警（后端路径: GET /alarms/type/{alarmType}）
export function getAlarmsByTypeApi(alarmType) {
  return request({
    url: `/alarms/type/${alarmType}`,
    method: 'get'
  })
}

// 按时间范围获取报警（后端路径: GET /alarms/time-range）
export function getAlarmsByTimeRangeApi(start, end) {
  return request({
    url: '/alarms/time-range',
    method: 'get',
    params: { start, end }
  })
}

// 触发签到缺失预警（后端路径: POST /alarms/missed-checkin/{elderId}）
export function triggerMissedCheckinApi(elderId) {
  return request({
    url: `/alarms/missed-checkin/${elderId}`,
    method: 'post'
  })
}

// 获取报警详情（调试接口）
export function getAlarmDetailApi(alarmId) {
  return request({
    url: `/alarms/debug/${alarmId}`,
    method: 'get'
  })
}

// 别名函数，兼容旧代码
export const triggerSosApi = triggerSOSApi
