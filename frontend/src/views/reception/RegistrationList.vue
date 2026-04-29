<template>
  <el-card class="page-card">
    <div class="page-toolbar">
      <el-date-picker v-model="filter.date" type="date" value-format="YYYY-MM-DD" placeholder="挂号日期" />
      <el-select v-model="filter.status" placeholder="状态" clearable style="width: 140px">
        <el-option v-for="s in statusOptions" :key="s.value" :label="s.label" :value="s.value" />
      </el-select>
      <el-button type="primary" @click="load">查询</el-button>
    </div>

    <el-table :data="records" border stripe>
      <el-table-column prop="regNo" label="挂号号" width="160" />
      <el-table-column prop="patientName" label="患者" width="120" />
      <el-table-column prop="patientGender" label="性别" width="60" />
      <el-table-column prop="deptName" label="科室" width="100" />
      <el-table-column prop="doctorName" label="医生" width="120" />
      <el-table-column prop="regDate" label="日期" width="120" />
      <el-table-column prop="regFee" label="挂号费" width="100">
        <template #default="{ row }">¥{{ row.regFee }}</template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="statusType(row.status)">{{ statusLabel(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120" fixed="right">
        <template #default="{ row }">
          <el-popconfirm
            v-if="row.status === 'WAITING'"
            title="确认退号？"
            @confirm="onCancel(row)">
            <template #reference><el-button link type="danger">退号</el-button></template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>
  </el-card>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import dayjs from 'dayjs'
import { listRegistrations, cancelRegistration } from '@/api/registration'

const records = ref([])
const filter = reactive({ date: dayjs().format('YYYY-MM-DD'), status: '' })

const statusOptions = [
  { value: 'WAITING', label: '待诊' },
  { value: 'VISITING', label: '就诊中' },
  { value: 'DONE', label: '已就诊' },
  { value: 'CANCELLED', label: '已退号' }
]

function statusLabel(s) { return (statusOptions.find(o => o.value === s) || {}).label || s }
function statusType(s) {
  return ({ WAITING: 'warning', VISITING: 'primary', DONE: 'success', CANCELLED: 'info' })[s]
}

async function load() {
  records.value = await listRegistrations({ date: filter.date, status: filter.status || undefined })
}

async function onCancel(row) {
  await cancelRegistration(row.id)
  ElMessage.success('已退号')
  load()
}

onMounted(load)
</script>
