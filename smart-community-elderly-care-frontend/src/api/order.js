// src/api/order.js
import request from '@/utils/request'

// ========== 服务相关 ==========

// 获取所有服务列表（后端路径: GET /service）
export function getServiceListApi(params) {
  return request({
    url: '/service',
    method: 'get',
    params
  })
}

// 获取可用服务列表（后端路径: GET /service/available）
export function getAvailableServicesApi() {
  return request({
    url: '/service/available',
    method: 'get'
  })
}

// 获取服务详情（后端路径: GET /service/{id}）
export function getServiceDetailApi(id) {
  return request({
    url: `/service/${id}`,
    method: 'get'
  })
}

// 按类别获取服务（后端路径: GET /service/category/{category}）
export function getServicesByCategoryApi(category) {
  return request({
    url: `/service/category/${category}`,
    method: 'get'
  })
}

// ========== 订单相关 ==========

// 创建订单（后端路径: POST /order）
export function createOrderApi(data) {
  return request({
    url: '/order',
    method: 'post',
    data
  })
}

// 获取用户的所有订单（后端路径: GET /order/user/{userId}）
export function getMyOrdersApi(userId) {
  return request({
    url: `/order/user/${userId}`,
    method: 'get'
  })
}

// 获取订单详情（后端路径: GET /order/{orderId}）
export function getOrderDetailApi(orderId) {
  return request({
    url: `/order/${orderId}`,
    method: 'get'
  })
}

// 更新订单状态（后端路径: PUT /order/{orderId}/status）
export function updateOrderStatusApi(orderId, status) {
  return request({
    url: `/order/${orderId}/status`,
    method: 'put',
    data: { status }
  })
}

// 取消订单（后端路径: PUT /order/{orderId}/cancel）
export function cancelOrderApi(orderId) {
  return request({
    url: `/order/${orderId}/cancel`,
    method: 'put'
  })
}

// 提交评价（后端路径: POST /order/{orderId}/review）
export function reviewOrderApi(orderId, data) {
  return request({
    url: `/order/${orderId}/review`,
    method: 'post',
    data
  })
}

// 获取所有订单（管理员用）（后端路径: GET /order/all）
export function getAllOrdersApi() {
  return request({
    url: '/order/all',
    method: 'get'
  })
}

// 获取订单统计（后端路径: GET /order/statistics）
export function getOrderStatisticsApi(startDate, endDate) {
  return request({
    url: '/order/statistics',
    method: 'get',
    params: { startDate, endDate }
  })
}

// 获取服务分布统计（后端路径: GET /order/service-distribution）
export function getServiceDistributionApi() {
  return request({
    url: '/order/service-distribution',
    method: 'get'
  })
}
