import request from '@/utils/request'

// 用户注册（贴合论文5.3.1）
export const userRegister = (data) => {
  return request({
    url: '/user/register',
    method: 'post',
    data
  })
}

// 用户登录（贴合论文5.3.1）
export const userLogin = (data) => {
  return request({
    url: '/user/login',
    method: 'post',
    data
  })
}

// 头像上传（贴合论文5.3.3）
export const uploadAvatar = (file) => {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/user/upload/avatar',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 获取用户信息
export const getUserInfo = () => {
  return request({
    url: '/user/info',
    method: 'get'
  })
}

// 修改用户信息（紧急联系人/密码）
export const updateUserInfo = (data) => {
  return request({
    url: '/user/info',
    method: 'put',
    data
  })
}
