<template>
  <el-card class="page-card">
    <div class="page-toolbar">
      <span>当前医生：<el-tag>{{ doctorTag }}</el-tag></span>
      <el-button type="primary" @click="load">刷新</el-button>
    </div>

    <el-empty v-if="!records.length" description="暂无待诊患者" />

    <el-table v-else :data="records" border stripe>
      <el-table-column prop="regNo" label="挂号号" width="160" />
      <el-table-column prop="patientName" label="患者" width="120" />
      <el-table-column prop="patientGender" label="性别" width="60" />
      <el-table-column prop="deptName" label="科室" width="100" />
      <el-table-column prop="regDate" label="日期" width="120" />
      <el-table-column label="操作" width="160" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" @click="onStart(row)">开始接诊</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-card>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { queueVisits, startVisit } from '@/api/visit'
import { listDoctors } from '@/api/doctor'

const router = useRouter()
const userStore = useUserStore()
const records = ref([])
const myDoctor = ref(null)

const doctorTag = computed(() =>
  myDoctor.value ? `${myDoctor.value.name} (${myDoctor.value.deptName || ''})` : (userStore.user?.realName || '未关联医生')
)

async function load() {
  // 教学简化：管理员看全部医生队列；DOCTOR 角色看自己（按 user_id 关联医生记录）
  let doctorId = null
  if (userStore.role === 'DOCTOR') {
    if (!myDoctor.value) {
      const doctors = await listDoctors()
      myDoctor.value = doctors.find(d => d.userId === userStore.user.id)
    }
    if (!myDoctor.value) {
      ElMessage.warning('当前账号未关联医生记录，无法接诊')
      records.value = []
      return
    }
    doctorId = myDoctor.value.id
  }
  records.value = await queueVisits(doctorId ? { doctorId } : {})
}

async function onStart(row) {
  const visit = await startVisit({ registrationId: row.id })
  router.push(`/doctor/visit/${visit.id}`)
}

onMounted(load)
</script>
