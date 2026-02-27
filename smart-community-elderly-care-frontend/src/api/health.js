import request from '@/utils/request'

// 健康数据录入（贴合论文5.4.1）
export const addHealthRecord = (userId, data) => {
  return request({
    url: `/health/${userId}`,
    method: 'post',
    data
  })
}

// 查询健康历史数据（贴合论文5.4.1）
export const getHealthHistory = (userId) => {
  return request({
    url: `/health/${userId}/history`,
    method: 'get'
  })
}

// 健康数据Excel导出（贴合论文5.4.1）
export const exportHealthExcel = (params) => {
  return request({
    url: '/health/export',
    method: 'get',
    params,
    responseType: 'blob' // 二进制流（Excel文件）
  })
}

// 查询健康异常报警
export const getHealthAlarm = (userId) => {
  return request({
    url: `/health/${userId}/alarm`,
    method: 'get'
  })
}
