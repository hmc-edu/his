<template>
  <el-card class="page-card">
    <div class="page-toolbar">
      <el-input v-model="keyword" placeholder="姓名/手机/身份证" clearable style="width: 240px" @keyup.enter="load(1)" />
      <el-button type="primary" @click="load(1)">搜索</el-button>
      <el-button type="success" @click="openCreate">新建患者</el-button>
    </div>

    <el-table :data="records" border stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="姓名" width="120" />
      <el-table-column prop="gender" label="性别" width="80" />
      <el-table-column prop="birthday" label="出生日期" width="130" />
      <el-table-column prop="phone" label="电话" width="140" />
      <el-table-column prop="idCard" label="身份证" width="200" />
      <el-table-column prop="address" label="地址" />
      <el-table-column label="操作" width="160" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
          <el-popconfirm title="确认删除？" @confirm="onDelete(row)">
            <template #reference><el-button link type="danger">删除</el-button></template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      style="margin-top: 12px; justify-content: flex-end; display: flex"
      v-model:current-page="page"
      v-model:page-size="size"
      :total="total"
      layout="total, prev, pager, next"
      @current-change="load(page)"
    />

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑患者' : '新建患者'" width="520px">
      <el-form :model="form" label-width="80px" ref="formRef">
        <el-form-item label="姓名" required>
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="form.gender">
            <el-radio value="男">男</el-radio>
            <el-radio value="女">女</el-radio>
            <el-radio value="未知">未知</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="出生日期">
          <el-date-picker v-model="form.birthday" type="date" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="手机">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="身份证">
          <el-input v-model="form.idCard" />
        </el-form-item>
        <el-form-item label="地址">
          <el-input v-model="form.address" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="onSave">保存</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { pagePatients, createPatient, updatePatient, deletePatient } from '@/api/patient'

const records = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(10)
const keyword = ref('')

const dialogVisible = ref(false)
const form = reactive({ id: null, name: '', gender: '男', birthday: '', phone: '', idCard: '', address: '' })

async function load(p) {
  page.value = p
  const res = await pagePatients({ keyword: keyword.value, page: p, size: size.value })
  records.value = res.records
  total.value = res.total
}

function openCreate() {
  Object.assign(form, { id: null, name: '', gender: '男', birthday: '', phone: '', idCard: '', address: '' })
  dialogVisible.value = true
}

function openEdit(row) {
  Object.assign(form, row)
  dialogVisible.value = true
}

async function onSave() {
  if (!form.name) return ElMessage.warning('请填写姓名')
  if (form.id) await updatePatient(form.id, form)
  else await createPatient(form)
  ElMessage.success('已保存')
  dialogVisible.value = false
  load(page.value)
}

async function onDelete(row) {
  await deletePatient(row.id)
  ElMessage.success('已删除')
  load(page.value)
}

onMounted(() => load(1))
</script>
