// src/api/user.js
import request from '@/utils/request'

// 用户登录（后端期望 phone 和 password 字段）
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

// 获取用户信息
export function getUserInfoApi(id) {
  return request({
    url: `/user/${id}`,
    method: 'get'
  })
}

// 获取所有用户
export function getAllUsersApi() {
  return request({
    url: '/user/all',
    method: 'get'
  })
}

// 根据角色获取用户
export function getUsersByRoleApi(role) {
  return request({
    url: `/user/role/${role}`,
    method: 'get'
  })
}

// 上传头像（后端路径: /user/upload/avatar）
export function uploadAvatarApi(formData) {
  return request({
    url: '/user/upload/avatar',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}
