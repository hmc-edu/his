<template>
  <el-card class="page-card">
    <div class="page-toolbar">
      <el-select v-model="filter.status" placeholder="状态" clearable style="width: 140px">
        <el-option v-for="s in statusOptions" :key="s.value" :label="s.label" :value="s.value" />
      </el-select>
      <el-button type="primary" @click="load">查询</el-button>
    </div>

    <el-table :data="records" border stripe>
      <el-table-column prop="billNo" label="账单编号" width="160" />
      <el-table-column prop="patientName" label="患者" width="120" />
      <el-table-column prop="patientGender" label="性别" width="60" />
      <el-table-column prop="doctorName" label="医生" width="120" />
      <el-table-column prop="regNo" label="挂号号" width="160" />
      <el-table-column label="金额" width="120">
        <template #default="{ row }">¥{{ row.totalAmount }}</template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="statusType(row.status)">{{ statusLabel(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="创建时间" width="180" />
      <el-table-column label="操作" width="160" fixed="right">
        <template #default="{ row }">
          <el-popconfirm
            v-if="row.status === 'PENDING'"
            title="确认收费？"
            @confirm="onCharge(row)">
            <template #reference><el-button link type="primary">收费</el-button></template>
          </el-popconfirm>
          <el-popconfirm
            v-if="row.status === 'PAID'"
            title="确认退费？"
            @confirm="onRefund(row)">
            <template #reference><el-button link type="danger">退费</el-button></template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>
  </el-card>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { listBills, chargeBill, refundBill } from '@/api/bill'

const records = ref([])
const filter = reactive({ status: '' })

const statusOptions = [
  { value: 'PENDING', label: '待收费' },
  { value: 'PAID', label: '已收费' },
  { value: 'REFUNDED', label: '已退费' }
]

function statusLabel(s) { return (statusOptions.find(o => o.value === s) || {}).label || s }
function statusType(s) {
  return ({ PENDING: 'warning', PAID: 'success', REFUNDED: 'info' })[s]
}

async function load() {
  records.value = await listBills({ status: filter.status || undefined })
}

async function onCharge(row) {
  await chargeBill(row.id)
  ElMessage.success('收费成功')
  load()
}

async function onRefund(row) {
  await refundBill(row.id)
  ElMessage.success('已退费')
  load()
}

onMounted(load)
</script>
