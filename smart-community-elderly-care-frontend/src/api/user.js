// src/api/user.js
import request from '@/utils/request'

// 用户登录
export function loginApi(data) {
  return request({
    url: '/user/login',
    method: 'post',
    data
  })
}

// 用户注册
export function registerApi(data) {
  return request({
    url: '/user/register',
    method: 'post',
    data
  })
}

// 更新用户信息
export function updateUserInfoApi(data) {
  return request({
    url: '/user/update',
    method: 'put',
    data
  })
}

// 上传头像
export function uploadAvatarApi(data) {
  return request({
    url: '/user/avatar/upload',
    method: 'post',
    data
  })
}

// 获取用户信息
export function getUserInfoApi(id) {
  return request({
    url: `/user/${id}`,
    method: 'get'
  })
}
