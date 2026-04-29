<template>
  <el-card class="page-card">
    <div class="page-toolbar">
      <el-select v-model="filterDeptId" placeholder="科室筛选" clearable style="width: 200px" @change="load">
        <el-option v-for="d in departments" :key="d.id" :label="d.name" :value="d.id" />
      </el-select>
      <el-button type="primary" @click="openCreate">新增医生</el-button>
    </div>

    <el-table :data="records" border stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="姓名" width="120" />
      <el-table-column prop="title" label="职称" width="120" />
      <el-table-column prop="deptName" label="科室" width="120" />
      <el-table-column prop="regFee" label="挂号费" width="100">
        <template #default="{ row }">¥{{ row.regFee }}</template>
      </el-table-column>
      <el-table-column prop="userId" label="关联用户ID" width="120" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
          <el-popconfirm title="确认删除？" @confirm="onDelete(row)">
            <template #reference><el-button link type="danger">删除</el-button></template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑医生' : '新增医生'" width="420px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="姓名"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="职称"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="科室">
          <el-select v-model="form.deptId" placeholder="选择科室">
            <el-option v-for="d in departments" :key="d.id" :label="d.name" :value="d.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="挂号费">
          <el-input-number v-model="form.regFee" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="关联用户ID">
          <el-input-number v-model="form.userId" :min="0" />
          <div style="color: #909399; font-size: 12px">填写已存在的 sys_user.id 即可让该用户登录后看到自己的待诊队列</div>
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
import { listDepartments } from '@/api/department'
import { listDoctors, createDoctor, updateDoctor, deleteDoctor } from '@/api/doctor'

const departments = ref([])
const records = ref([])
const filterDeptId = ref(null)

const dialogVisible = ref(false)
const form = reactive({ id: null, name: '', title: '', deptId: null, regFee: 10, userId: null })

async function load() {
  records.value = await listDoctors(filterDeptId.value ? { deptId: filterDeptId.value } : {})
}
function openCreate() {
  Object.assign(form, { id: null, name: '', title: '', deptId: null, regFee: 10, userId: null })
  dialogVisible.value = true
}
function openEdit(row) { Object.assign(form, row); dialogVisible.value = true }
async function onSave() {
  if (!form.name || !form.deptId) return ElMessage.warning('姓名和科室必填')
  if (form.id) await updateDoctor(form.id, form)
  else await createDoctor(form)
  ElMessage.success('已保存'); dialogVisible.value = false; load()
}
async function onDelete(row) { await deleteDoctor(row.id); ElMessage.success('已删除'); load() }

onMounted(async () => { departments.value = await listDepartments(); await load() })
</script>
