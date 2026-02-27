<template>
  <div class="profile-container">
    <el-row :gutter="20">
      <el-col :xs="24" :md="8">
        <el-card class="avatar-card">
          <div class="avatar-wrapper">
            <!-- 使用计算属性拼接完整头像URL -->
            <el-avatar :size="120" :src="avatarUrl" />
            <el-upload
              class="avatar-uploader"
              action="/api/user/upload/avatar"
              :headers="uploadHeaders"
              :show-file-list="false"
              :before-upload="beforeAvatarUpload"
              :on-success="handleAvatarSuccess"
              :on-error="handleAvatarError"
            >
              <el-button type="primary" size="small">
                <el-icon><Upload /></el-icon>
                更换头像
              </el-button>
            </el-upload>
          </div>
          <h3>{{ userInfo.name }}</h3>
          <p>{{ roleText }}</p>
        </el-card>
      </el-col>

      <el-col :xs="24" :md="16">
        <el-card>
          <template #header>
            <span>基本信息</span>
          </template>

          <el-form :model="form" label-width="120px" size="large">
            <el-form-item label="手机号">
              <el-input v-model="form.phone" disabled />
            </el-form-item>
            <el-form-item label="姓名">
              <el-input v-model="form.name" />
            </el-form-item>
            <el-form-item label="紧急联系人">
              <el-input v-model="form.emergencyContact" placeholder="请输入紧急联系人电话" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="saveProfile">保存修改</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import request from '@/utils/request'

const userStore = useUserStore()
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

const userInfo = computed(() => userStore.userInfo)

// 计算头像完整URL
const avatarUrl = computed(() => {
  const avatar = userInfo.value.avatar
  if (!avatar) return defaultAvatar
  // 如果已经是完整URL则直接返回
  if (avatar.startsWith('http')) return avatar
  // 拼接后端地址（确保这里是后端实际地址和端口）
  return `http://localhost:8080${avatar}`
})

const roleText = computed(() => {
  const roles = { 'elder': '老人用户', 'family': '家属用户', 'admin': '管理员' }
  return roles[userInfo.value.role] || userInfo.value.role
})

const form = ref({
  phone: '',
  name: '',
  emergencyContact: ''
})

// 初始化表单数据
const initForm = () => {
  form.value = {
    phone: userInfo.value.phone || '',
    name: userInfo.value.name || '',
    emergencyContact: userInfo.value.emergencyContact || ''
  }
}

const uploadHeaders = computed(() => ({
  Authorization: `Bearer ${userStore.token}`
}))

const beforeAvatarUpload = (file) => {
  const isJPG = file.type === 'image/jpeg'
  const isPNG = file.type === 'image/png'
  const isJPEG = file.type === 'image/jpeg'
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isJPG && !isPNG && !isJPEG) {
    ElMessage.error('只支持 JPG/PNG/JPEG 格式!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB!')
    return false
  }
  return true
}

const handleAvatarSuccess = (response) => {
  // 注意：element-plus的on-success返回的是整个响应，不是res.data
  if (response.code === 200) {
    ElMessage.success('头像上传成功')
    // 更新store中的用户信息
    userStore.setUserInfo({ ...userInfo.value, avatar: response.data })
  } else {
    ElMessage.error(response.message || '上传失败')
  }
}

const handleAvatarError = (error) => {
  console.error('上传错误:', error)
  ElMessage.error('头像上传失败，请检查网络或联系管理员')
}

const saveProfile = async () => {
  try {
    // 这里可以调用更新用户信息接口
    // await request.put('/user/update', form.value)
    ElMessage.success('保存成功')
  } catch (error) {
    console.error('保存失败:', error)
    ElMessage.error('保存失败')
  }
}

// 组件挂载时初始化
onMounted(() => {
  initForm()
})
</script>

<style scoped lang="scss">
.profile-container {
  .avatar-card {
    text-align: center;

    .avatar-wrapper {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 15px;
      margin-bottom: 20px;
    }

    h3 {
      margin: 10px 0 5px;
    }

    p {
      color: #909399;
    }
  }
}
</style>
