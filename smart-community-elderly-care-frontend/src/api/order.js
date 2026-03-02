// src/api/order.js
import request from '@/utils/request'

// 获取服务列表
export function getServiceListApi(params) {
  return request({
    url: '/service/list',
    method: 'get',
    params
  })
}

// 创建订单
export function createOrderApi(data) {
  return request({
    url: '/order/create',
    method: 'post',
    data
  })
}

// 获取我的订单
export function getMyOrdersApi(params) {
  return request({
    url: '/order/my',
    method: 'get',
    params
  })
}

// 取消订单
export function cancelOrderApi(id) {
  return request({
    url: `/order/cancel/${id}`,
    method: 'post'
  })
}

// 评价订单
export function reviewOrderApi(data) {
  return request({
    url: '/order/review',
    method: 'post',
    data
  })
}

// 获取订单详情
export function getOrderDetailApi(id) {
  return request({
    url: `/order/${id}`,
    method: 'get'
  })
}
