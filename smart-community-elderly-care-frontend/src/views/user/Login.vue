<template>
  <div class="login-container">
    <el-card class="login-card" shadow="hover">
      <h2 class="login-title">社区智慧养老系统</h2>
      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        label-width="80px"
        class="login-form"
      >
        <el-form-item label="用户名" prop="username">
          <el-input v-model="loginForm.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            show-password
          />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="loginForm.role" placeholder="请选择角色">
            <el-option label="老人" value="elder" />
            <el-option label="家属" value="family" />
            <el-option label="管理员" value="admin" />
          </el-select>
        </el-form-item>
        <el-form-item class="login-btn">
          <el-button type="primary" @click="handleLogin" class="btn">登录</el-button>
          <el-button type="text" @click="goToRegister" class="register-btn">注册账号</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/pinia/userStore'
import { userLogin } from '@/api/user'

const router = useRouter()
const userStore = useUserStore()
const loginFormRef = ref(null)

// 登录表单
const loginForm = reactive({
  username: '',
  password: '',
  role: ''
})

// 校验规则
const loginRules = reactive({
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }]
})

// 登录操作
const handleLogin = async () => {
  try {
    await loginFormRef.value.validate()
    // 调用登录接口（测试阶段可先模拟返回）
    const res = await userLogin(loginForm)
    // 实际项目中替换为接口返回的token和userInfo
    const mockToken = 'mock-token-' + Date.now()
    const mockUserInfo = {
      userId: 1001,
      username: loginForm.username,
      role: loginForm.role
    }
    userStore.login(mockToken, mockUserInfo)
    ElMessage.success('登录成功！')
    // 根据角色跳转对应首页
    if (loginForm.role === 'elder') {
      router.push('/elder/home')
    } else if (loginForm.role === 'family') {
      router.push('/family/home')
    } else if (loginForm.role === 'admin') {
      router.push('/admin/home')
    }
  } catch (error) {
    ElMessage.error('登录失败：' + (error.message || '用户名或密码错误'))
  }
}

// 跳转到注册页
const goToRegister = () => {
  router.push('/register')
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f5f7fa;
}

.login-card {
  width: 420px;
  padding: 20px;
}

.login-title {
  text-align: center;
  margin-bottom: 20px;
  font-size: 20px;
  color: #1989fa;
}

.login-form {
  margin-top: 20px;
}

.login-btn {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.btn {
  width: 180px;
  height: 44px;
  font-size: 16px;
}

.register-btn {
  font-size: 14px;
  color: #1989fa;
}
</style>
