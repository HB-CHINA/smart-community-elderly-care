// src/api/notice.js
import request from '@/utils/request'

// 创建/发布公告（后端路径: POST /notices）
export function publishNoticeApi(data) {
  return request({
    url: '/notices',
    method: 'post',
    data
  })
}

// 更新公告（后端路径: PUT /notices/{id}）
export function updateNoticeApi(id, data) {
  return request({
    url: `/notices/${id}`,
    method: 'put',
    data
  })
}

// 删除公告（后端路径: DELETE /notices/{id}）
export function deleteNoticeApi(id) {
  return request({
    url: `/notices/${id}`,
    method: 'delete'
  })
}

// 获取公告详情（后端路径: GET /notices/{id}）
export function getNoticeDetailApi(id) {
  return request({
    url: `/notices/${id}`,
    method: 'get'
  })
}

// 获取已发布公告列表（后端路径: GET /notices/published）
export function getPublishedNoticesApi() {
  return request({
    url: '/notices/published',
    method: 'get'
  })
}

// 获取所有公告（后端路径: GET /notices/all）
export function getNoticeListApi(params) {
  return request({
    url: '/notices/all',
    method: 'get',
    params
  })
}
