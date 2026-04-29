<template>
  <div class="dashboard">
    <el-row :gutter="16">
      <el-col :span="12">
        <el-card>
          <h2>杭州医学院门诊 HIS 系统</h2>
          <p>当前角色：<el-tag>{{ roleLabel }}</el-tag></p>
          <p>主线业务流程：<strong>挂号 → 就诊 → 处方</strong></p>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <h3>快速入口</h3>
          <el-space wrap>
            <el-button v-for="link in links" :key="link.to" type="primary" plain @click="$router.push(link.to)">
              {{ link.label }}
            </el-button>
          </el-space>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const roleLabel = computed(() => ({ ADMIN: '管理员', RECEPTION: '挂号员', DOCTOR: '医生' }[userStore.role] || '-'))

const allLinks = [
  { label: '患者管理', to: '/reception/patients', roles: ['ADMIN', 'RECEPTION'] },
  { label: '挂号', to: '/reception/register', roles: ['ADMIN', 'RECEPTION'] },
  { label: '挂号查询', to: '/reception/registrations', roles: ['ADMIN', 'RECEPTION'] },
  { label: '待诊队列', to: '/doctor/queue', roles: ['ADMIN', 'DOCTOR'] },
  { label: '科室', to: '/base/department', roles: ['ADMIN'] },
  { label: '药品', to: '/base/drug', roles: ['ADMIN'] }
]
const links = computed(() => allLinks.filter(l => l.roles.includes(userStore.role)))
</script>

<style scoped>
.dashboard { padding: 8px; }
.dashboard h2 { margin-top: 0; }
</style>
