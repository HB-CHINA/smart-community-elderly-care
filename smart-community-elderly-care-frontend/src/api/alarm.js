import request from '@/utils/request'

// 触发SOS紧急求助
export const triggerSos = (userId) => {
  return request({
    url: `/alarm/sos/${userId}`,
    method: 'post'
  })
}

// 查询报警记录
export const getAlarmRecord = (userId) => {
  return request({
    url: `/alarm/record/${userId}`,
    method: 'get'
  })
}
