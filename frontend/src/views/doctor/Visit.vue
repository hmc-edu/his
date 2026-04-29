<template>
  <el-card class="page-card" v-if="visit">
    <template #header>
      <div class="visit-header">
        <span>就诊单 #{{ visit.id }}</span>
        <el-tag>{{ visit.registration?.patientName }} / {{ visit.registration?.patientGender }}</el-tag>
        <el-tag type="info">挂号号 {{ visit.registration?.regNo }}</el-tag>
        <el-tag type="success">{{ visit.registration?.deptName }} - {{ visit.registration?.doctorName }}</el-tag>
      </div>
    </template>

    <h3>电子病历</h3>
    <el-form :model="record" label-width="80px">
      <el-form-item label="主诉">
        <el-input v-model="record.chiefComplaint" type="textarea" :rows="2" placeholder="如：发热3天" />
      </el-form-item>
      <el-form-item label="现病史">
        <el-input v-model="record.presentIllness" type="textarea" :rows="3" />
      </el-form-item>
      <el-form-item label="诊断">
        <el-input v-model="record.diagnosis" placeholder="如：上呼吸道感染" />
      </el-form-item>
      <el-form-item label="医嘱">
        <el-input v-model="record.doctorAdvice" type="textarea" :rows="2" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="onSaveRecord">保存病历</el-button>
      </el-form-item>
    </el-form>

    <el-divider />

    <h3>处方</h3>
    <el-table :data="prescriptionItems" border>
      <el-table-column label="药品" min-width="200">
        <template #default="{ row, $index }">
          <el-select
            v-model="row.drugId"
            placeholder="选择药品"
            filterable
            @change="(v) => onDrugChange(v, $index)">
            <el-option v-for="d in drugs" :key="d.id" :label="`${d.name} ${d.spec || ''}`" :value="d.id" />
          </el-select>
        </template>
      </el-table-column>
      <el-table-column label="规格" width="120">
        <template #default="{ row }">{{ row._drug?.spec || '-' }}</template>
      </el-table-column>
      <el-table-column label="单价" width="80">
        <template #default="{ row }">{{ row.unitPrice ?? '-' }}</template>
      </el-table-column>
      <el-table-column label="数量" width="100">
        <template #default="{ row }">
          <el-input-number v-model="row.qty" :min="1" controls-position="right" />
        </template>
      </el-table-column>
      <el-table-column label="天数" width="100">
        <template #default="{ row }">
          <el-input-number v-model="row.days" :min="1" controls-position="right" />
        </template>
      </el-table-column>
      <el-table-column label="用法" width="160">
        <template #default="{ row }"><el-input v-model="row.dosage" /></template>
      </el-table-column>
      <el-table-column label="频次" width="160">
        <template #default="{ row }"><el-input v-model="row.frequency" /></template>
      </el-table-column>
      <el-table-column label="小计" width="100">
        <template #default="{ row }">¥{{ subtotal(row).toFixed(2) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="80">
        <template #default="{ $index }">
          <el-button link type="danger" @click="removeItem($index)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="rx-toolbar">
      <el-button @click="addItem">添加药品</el-button>
      <span class="rx-total">合计：¥{{ totalAmount.toFixed(2) }}</span>
    </div>

    <el-divider />

    <div v-if="visit.prescriptions?.length" class="history">
      <h3>已开具处方</h3>
      <el-card v-for="p in visit.prescriptions" :key="p.id" style="margin-bottom: 8px">
        <div>处方 #{{ p.id }}（合计 ¥{{ p.totalAmount }}）</div>
        <el-table :data="p.items" border size="small">
          <el-table-column prop="drugName" label="药品" />
          <el-table-column prop="drugSpec" label="规格" width="140" />
          <el-table-column prop="dosage" label="用法" width="140" />
          <el-table-column prop="frequency" label="频次" width="140" />
          <el-table-column prop="qty" label="数量" width="80" />
          <el-table-column prop="days" label="天数" width="80" />
          <el-table-column prop="subtotal" label="小计" width="100" />
        </el-table>
      </el-card>
    </div>

    <div class="bottom-bar">
      <el-button type="primary" :disabled="!prescriptionItems.length" @click="onSubmitPrescription">提交处方</el-button>
      <el-button type="success" @click="onFinish">完成就诊</el-button>
      <el-button @click="$router.push('/doctor/queue')">返回队列</el-button>
    </div>
  </el-card>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getVisit, updateVisit, finishVisit, prescribe } from '@/api/visit'
import { listDrugs } from '@/api/drug'

const route = useRoute()
const router = useRouter()
const visitId = Number(route.params.id)

const visit = ref(null)
const drugs = ref([])
const record = reactive({ chiefComplaint: '', presentIllness: '', diagnosis: '', doctorAdvice: '' })
const prescriptionItems = ref([])

function makeItem() {
  return { drugId: null, _drug: null, unitPrice: null, qty: 1, days: 3, dosage: '口服', frequency: '一日三次' }
}

function addItem() {
  prescriptionItems.value.push(makeItem())
}

function removeItem(i) {
  prescriptionItems.value.splice(i, 1)
}

function onDrugChange(drugId, index) {
  const drug = drugs.value.find(d => d.id === drugId)
  prescriptionItems.value[index]._drug = drug
  prescriptionItems.value[index].unitPrice = drug?.price
}

function subtotal(row) {
  if (!row._drug) return 0
  return Number(row._drug.price || 0) * Number(row.qty || 0)
}

const totalAmount = computed(() => prescriptionItems.value.reduce((s, r) => s + subtotal(r), 0))

async function load() {
  visit.value = await getVisit(visitId)
  Object.assign(record, {
    chiefComplaint: visit.value.chiefComplaint || '',
    presentIllness: visit.value.presentIllness || '',
    diagnosis: visit.value.diagnosis || '',
    doctorAdvice: visit.value.doctorAdvice || ''
  })
}

async function onSaveRecord() {
  await updateVisit(visitId, record)
  ElMessage.success('病历已保存')
  load()
}

async function onSubmitPrescription() {
  if (prescriptionItems.value.some(i => !i.drugId)) {
    return ElMessage.warning('请为每行选择药品')
  }
  const items = prescriptionItems.value.map(i => ({
    drugId: i.drugId, qty: i.qty, days: i.days, dosage: i.dosage, frequency: i.frequency
  }))
  await prescribe(visitId, { items })
  ElMessage.success('处方已提交')
  prescriptionItems.value = []
  load()
}

async function onFinish() {
  await onSaveRecord()
  await finishVisit(visitId)
  ElMessage.success('就诊已完成')
  router.push('/doctor/queue')
}

onMounted(async () => {
  drugs.value = await listDrugs()
  await load()
})
</script>

<style scoped>
.visit-header { display: flex; gap: 8px; align-items: center; }
.rx-toolbar { display: flex; justify-content: space-between; align-items: center; margin-top: 12px; }
.rx-total { font-size: 16px; font-weight: 600; color: #f56c6c; }
.bottom-bar { margin-top: 16px; display: flex; gap: 8px; justify-content: flex-end; }
.history { margin-top: 12px; }
</style>
