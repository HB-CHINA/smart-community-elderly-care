// src/api/notice.js
import request from '@/utils/request'

// 获取公告列表
export function getNoticeListApi(params) {
  return request({
    url: '/notice/list',
    method: 'get',
    params
  })
}

// 获取公告详情
export function getNoticeDetailApi(id) {
  return request({
    url: `/notice/${id}`,
    method: 'get'
  })
}

// 发布公告（管理员）
export function publishNoticeApi(data) {
  return request({
    url: '/notice/publish',
    method: 'post',
    data
  })
}

// 删除公告
export function deleteNoticeApi(id) {
  return request({
    url: `/notice/${id}`,
    method: 'delete'
  })
}
