<template>
  <el-card class="page-card">
    <div class="page-toolbar">
      <el-button type="primary" @click="openCreate">新增科室</el-button>
    </div>

    <el-table :data="records" border stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="code" label="编码" width="160" />
      <el-table-column prop="name" label="名称" />
      <el-table-column prop="sort" label="排序" width="100" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
          <el-popconfirm title="确认删除？" @confirm="onDelete(row)">
            <template #reference><el-button link type="danger">删除</el-button></template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑科室' : '新增科室'" width="420px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="编码"><el-input v-model="form.code" /></el-form-item>
        <el-form-item label="名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="排序"><el-input-number v-model="form.sort" :min="0" /></el-form-item>
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
import { listDepartments, createDepartment, updateDepartment, deleteDepartment } from '@/api/department'

const records = ref([])
const dialogVisible = ref(false)
const form = reactive({ id: null, code: '', name: '', sort: 0 })

async function load() {
  records.value = await listDepartments()
}
function openCreate() { Object.assign(form, { id: null, code: '', name: '', sort: 0 }); dialogVisible.value = true }
function openEdit(row) { Object.assign(form, row); dialogVisible.value = true }
async function onSave() {
  if (!form.code || !form.name) return ElMessage.warning('编码和名称必填')
  if (form.id) await updateDepartment(form.id, form)
  else await createDepartment(form)
  ElMessage.success('已保存'); dialogVisible.value = false; load()
}
async function onDelete(row) { await deleteDepartment(row.id); ElMessage.success('已删除'); load() }
onMounted(load)
</script>
