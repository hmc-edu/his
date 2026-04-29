<template>
  <div class="login-page">
    <el-card class="login-card">
      <template #header>
        <div class="login-header">
          <div class="login-title">杭州医学院门诊 HIS 系统</div>
          <div class="login-subtitle">Hangzhou Medical College · Outpatient HIS</div>
        </div>
      </template>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px" @submit.prevent="onLogin">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="admin / reception / doctor" autofocus />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" show-password placeholder="123456" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" style="width: 100%" @click="onLogin">登录</el-button>
        </el-form-item>
      </el-form>
      <div class="hint">默认账号：<code>admin / reception / doctor</code>，密码均为 <code>123456</code></div>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const formRef = ref()
const loading = ref(false)

const form = reactive({ username: '', password: '' })

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function onLogin() {
  await formRef.value?.validate()
  loading.value = true
  try {
    await userStore.login({ ...form })
    ElMessage.success('登录成功')
    const redirect = route.query.redirect || '/dashboard'
    router.replace(redirect)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #1890ff 0%, #36cfc9 100%);
}
.login-card {
  width: 420px;
}
.login-header {
  text-align: center;
}
.login-title {
  font-size: 20px;
  font-weight: 600;
  letter-spacing: 1px;
}
.login-subtitle {
  margin-top: 4px;
  font-size: 12px;
  color: #909399;
  letter-spacing: 1px;
}
.hint {
  color: #909399;
  font-size: 12px;
  text-align: center;
}
</style>
