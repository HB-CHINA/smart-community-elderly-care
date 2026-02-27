<template>
  <div class="register-container">
    <el-card class="register-card" shadow="hover">
      <h2 class="register-title">用户注册</h2>
      <el-form
        ref="registerFormRef"
        :model="registerForm"
        :rules="registerRules"
        label-width="80px"
        class="register-form"
      >
        <el-form-item label="用户名" prop="username">
          <el-input v-model="registerForm.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="registerForm.password"
            type="password"
            placeholder="请输入密码"
            show-password
          />
        </el-form-item>
        <el-form-item label="确认密码" prop="checkPassword">
          <el-input
            v-model="registerForm.checkPassword"
            type="password"
            placeholder="请确认密码"
            show-password
          />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="registerForm.role" placeholder="请选择角色">
            <el-option label="老人" value="elder" />
            <el-option label="家属" value="family" />
          </el-select>
        </el-form-item>
        <el-form-item label="紧急联系人" prop="emergencyContact">
          <el-input v-model="registerForm.emergencyContact" placeholder="请输入紧急联系人电话" />
        </el-form-item>
        <el-form-item class="register-btn">
          <el-button type="primary" @click="handleRegister" class="btn">注册</el-button>
          <el-button type="text" @click="goToLogin" class="login-btn">返回登录</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { userRegister } from '@/api/user'

const router = useRouter()
const registerFormRef = ref(null)

// 注册表单
const registerForm = reactive({
  username: '',
  password: '',
  checkPassword: '',
  role: '',
  emergencyContact: ''
})

// 校验规则
const registerRules = reactive({
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  checkPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== registerForm.password) {
          callback(new Error('两次输入的密码不一致！'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }],
  emergencyContact: [{ required: true, message: '请输入紧急联系人电话', trigger: 'blur' }]
})

// 注册操作
const handleRegister = async () => {
  try {
    await registerFormRef.value.validate()
    // 调用注册接口（测试阶段可先模拟）
    await userRegister(registerForm)
    ElMessage.success('注册成功！请返回登录')
    router.push('/login')
  } catch (error) {
    ElMessage.error('注册失败：' + (error.message || '请检查表单信息'))
  }
}

// 返回登录页
const goToLogin = () => {
  router.push('/login')
}
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f5f7fa;
}

.register-card {
  width: 420px;
  padding: 20px;
}

.register-title {
  text-align: center;
  margin-bottom: 20px;
  font-size: 20px;
  color: #1989fa;
}

.register-form {
  margin-top: 20px;
}

.register-btn {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.btn {
  width: 180px;
  height: 44px;
  font-size: 16px;
}

.login-btn {
  font-size: 14px;
  color: #1989fa;
}
</style>
