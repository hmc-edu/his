<template>
  <el-card class="page-card">
    <el-row :gutter="16">
      <el-col :span="10">
        <h3>1. 选择患者</h3>
        <el-input v-model="keyword" placeholder="输入姓名/手机/身份证" clearable @keyup.enter="searchPatients" />
        <el-button type="primary" style="margin-top: 8px" @click="searchPatients">搜索患者</el-button>
        <el-button type="success" style="margin-top: 8px" @click="openCreate">新建患者</el-button>

        <el-table
          :data="patientRecords"
          border
          highlight-current-row
          @current-change="onSelectPatient"
          style="margin-top: 12px">
          <el-table-column prop="name" label="姓名" width="100" />
          <el-table-column prop="gender" label="性别" width="60" />
          <el-table-column prop="phone" label="电话" width="130" />
          <el-table-column prop="idCard" label="身份证" />
        </el-table>
      </el-col>

      <el-col :span="14">
        <h3>2. 选择医生并提交</h3>
        <el-form label-width="100px" :model="form">
          <el-form-item label="已选患者">
            <el-tag v-if="form.patient">{{ form.patient.name }} / {{ form.patient.gender }}</el-tag>
            <span v-else style="color: #909399">请先在左侧选择患者</span>
          </el-form-item>
          <el-form-item label="科室">
            <el-select v-model="form.deptId" placeholder="选择科室" @change="onDeptChange" style="width: 220px">
              <el-option v-for="d in departments" :key="d.id" :label="d.name" :value="d.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="医生">
            <el-select v-model="form.doctorId" placeholder="选择医生" style="width: 220px">
              <el-option
                v-for="d in doctors"
                :key="d.id"
                :label="`${d.name} ${d.title || ''} - ¥${d.regFee}`"
                :value="d.id" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :disabled="!canSubmit" @click="onSubmit">确认挂号</el-button>
          </el-form-item>
        </el-form>

        <el-result
          v-if="result"
          icon="success"
          :title="`挂号成功：${result.regNo}`"
          :sub-title="`科室 ${result.deptName} / 医生 ${result.doctorName} / 挂号费 ¥${result.regFee}`">
          <template #extra>
            <el-button type="primary" @click="reset">继续挂号</el-button>
          </template>
        </el-result>
      </el-col>
    </el-row>

    <el-dialog v-model="dialogVisible" title="新建患者" width="520px">
      <el-form :model="newPatient" label-width="80px">
        <el-form-item label="姓名" required>
          <el-input v-model="newPatient.name" />
        </el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="newPatient.gender">
            <el-radio value="男">男</el-radio>
            <el-radio value="女">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="手机">
          <el-input v-model="newPatient.phone" />
        </el-form-item>
        <el-form-item label="身份证">
          <el-input v-model="newPatient.idCard" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="onCreatePatient">保存</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { pagePatients, createPatient, getPatient } from '@/api/patient'
import { listDepartments } from '@/api/department'
import { listDoctors } from '@/api/doctor'
import { createRegistration } from '@/api/registration'

const keyword = ref('')
const patientRecords = ref([])
const departments = ref([])
const doctors = ref([])

const form = reactive({ patient: null, deptId: null, doctorId: null })
const result = ref(null)

const dialogVisible = ref(false)
const newPatient = reactive({ name: '', gender: '男', phone: '', idCard: '' })

const canSubmit = computed(() => form.patient && form.doctorId)

async function searchPatients() {
  const res = await pagePatients({ keyword: keyword.value, page: 1, size: 10 })
  patientRecords.value = res.records
}

function onSelectPatient(row) {
  form.patient = row
}

function openCreate() {
  Object.assign(newPatient, { name: '', gender: '男', phone: '', idCard: '' })
  dialogVisible.value = true
}

async function onCreatePatient() {
  if (!newPatient.name) return ElMessage.warning('请填写姓名')
  const id = await createPatient(newPatient)
  const created = await getPatient(id)
  patientRecords.value = [created, ...patientRecords.value]
  form.patient = created
  ElMessage.success('已新建患者')
  dialogVisible.value = false
}

async function onDeptChange(val) {
  form.doctorId = null
  doctors.value = await listDoctors({ deptId: val })
}

async function onSubmit() {
  const reg = await createRegistration({ patientId: form.patient.id, doctorId: form.doctorId })
  result.value = reg
  ElMessage.success('挂号成功')
}

function reset() {
  form.patient = null
  form.deptId = null
  form.doctorId = null
  doctors.value = []
  result.value = null
  patientRecords.value = []
  keyword.value = ''
}

onMounted(async () => {
  departments.value = await listDepartments()
})
</script>
