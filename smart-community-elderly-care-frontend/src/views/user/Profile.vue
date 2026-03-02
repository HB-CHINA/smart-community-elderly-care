<!-- src/views/user/Profile.vue -->
<template>
  <div class="profile-container">
    <el-card class="profile-card">
      <template #header>
        <div class="card-header">
          <span class="title">个人中心</span>
        </div>
      </template>

      <div class="profile-content">
        <!-- 头像区域 -->
        <div class="avatar-section">
          <el-avatar :size="120" :src="userInfo.avatar || defaultAvatar" />
          <el-upload
            class="avatar-upload"
            :show-file-list="false"
            :before-upload="beforeAvatarUpload"
            :http-request="handleAvatarUpload"
          >
            <el-button type="primary" size="small">更换头像</el-button>
          </el-upload>
        </div>

        <!-- 基本信息表单 -->
        <el-form :model="profileForm" :rules="rules" ref="profileFormRef" label-width="100px">
          <el-form-item label="用户名">
            <el-input v-model="userInfo.username" disabled />
          </el-form-item>
          <el-form-item label="真实姓名" prop="realName">
            <el-input v-model="profileForm.realName" placeholder="请输入真实姓名" />
          </el-form-item>
          <el-form-item label="手机号" prop="phone">
            <el-input v-model="profileForm.phone" placeholder="请输入手机号" />
          </el-form-item>
          <el-form-item label="紧急联系人" prop="emergencyContact">
            <el-input v-model="profileForm.emergencyContact" placeholder="请输入紧急联系人" />
          </el-form-item>
          <el-form-item label="紧急电话" prop="emergencyPhone">
            <el-input v-model="profileForm.emergencyPhone" placeholder="请输入紧急联系电话" />
          </el-form-item>
          <el-form-item label="家庭住址" prop="address">
            <el-input v-model="profileForm.address" type="textarea" :rows="2" placeholder="请输入家庭住址" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleUpdate" :loading="loading">保存修改</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/pinia/userStore'
import { updateUserInfoApi, uploadAvatarApi } from '@/api/user'

const userStore = useUserStore()
const profileFormRef = ref(null)
const loading = ref(false)

const userInfo = computed(() => userStore.userInfo)
const defaultAvatar = 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'

const profileForm = reactive({
  realName: '',
  phone: '',
  emergencyContact: '',
  emergencyPhone: '',
  address: ''
})

const rules = {
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  emergencyPhone: [
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ]
}

onMounted(() => {
  // 初始化表单数据
  Object.assign(profileForm, {
    realName: userInfo.value.realName || '',
    phone: userInfo.value.phone || '',
    emergencyContact: userInfo.value.emergencyContact || '',
    emergencyPhone: userInfo.value.emergencyPhone || '',
    address: userInfo.value.address || ''
  })
})

const handleUpdate = async () => {
  if (!profileFormRef.value) return

  await profileFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        await updateUserInfoApi(profileForm)
        userStore.updateUser(profileForm)
        ElMessage.success('修改成功')
      } catch (error) {
        ElMessage.error(error.message || '修改失败')
      } finally {
        loading.value = false
      }
    }
  })
}

const beforeAvatarUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('只能上传图片文件')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB')
    return false
  }
  return true
}

const handleAvatarUpload = async (options) => {
  try {
    const formData = new FormData()
    formData.append('file', options.file)
    const avatarUrl = await uploadAvatarApi(formData)
    userStore.updateUser({ avatar: avatarUrl })
    ElMessage.success('头像上传成功')
  } catch (error) {
    ElMessage.error(error.message || '头像上传失败')
  }
}
</script>

<style scoped>
.profile-container {
  padding: 20px;
}

.profile-card {
  max-width: 800px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.title {
  font-size: 20px;
  font-weight: 600;
}

.profile-content {
  padding: 20px;
}

.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 30px;
}

.avatar-upload {
  margin-top: 15px;
}
</style>
