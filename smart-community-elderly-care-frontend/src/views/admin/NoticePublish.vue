<!-- src/views/admin/NoticePublish.vue -->
<template>
  <div class="notice-publish">
    <el-card>
      <template #header>
        <span class="card-title">{{ isEdit ? '编辑公告' : '发布公告' }}</span>
      </template>

      <el-form :model="noticeForm" :rules="rules" ref="noticeFormRef" label-width="100px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="noticeForm.title" placeholder="请输入公告标题" />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input
            v-model="noticeForm.content"
            type="textarea"
            :rows="8"
            placeholder="请输入公告内容"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="loading">
            {{ isEdit ? '保存修改' : '发布' }}
          </el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { useRoute, useRouter } from 'vue-router'
import { publishNoticeApi, getNoticeDetailApi } from '@/api/notice'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const noticeFormRef = ref(null)

const isEdit = computed(() => route.query.id)

const noticeForm = reactive({
  title: '',
  content: ''
})

const rules = {
  title: [{ required: true, message: '请输入公告标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入公告内容', trigger: 'blur' }]
}

onMounted(async () => {
  if (isEdit.value) {
    try {
      const res = await getNoticeDetailApi(isEdit.value)
      Object.assign(noticeForm, res)
    } catch (error) {
      ElMessage.error(error.message || '加载公告详情失败')
      router.push('/admin/notice')
    }
  }
})

const handleSubmit = async () => {
  if (!noticeFormRef.value) return

  await noticeFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const data = {
          ...noticeForm,
          id: isEdit.value || undefined
        }

        await publishNoticeApi(data)
        ElMessage.success(isEdit.value ? '修改成功' : '发布成功')
        router.push('/admin/notice')
      } catch (error) {
        ElMessage.error(error.message || '操作失败')
      } finally {
        loading.value = false
      }
    }
  })
}

const handleReset = () => {
  if (noticeFormRef.value) {
    noticeFormRef.value.resetFields()
  }
}
</script>

<style scoped>
.notice-publish {
  padding: 20px;
}

.card-title {
  font-size: 20px;
  font-weight: 600;
}
</style>
