<template>
  <el-card class="page-card">
    <div class="page-toolbar">
      <el-input v-model="keyword" placeholder="编码/名称" clearable style="width: 240px" @keyup.enter="load" />
      <el-button type="primary" @click="load">搜索</el-button>
      <el-button type="success" @click="openCreate">新增药品</el-button>
    </div>

    <el-table :data="records" border stripe>
      <el-table-column prop="code" label="编码" width="120" />
      <el-table-column prop="name" label="名称" />
      <el-table-column prop="spec" label="规格" width="160" />
      <el-table-column prop="unit" label="单位" width="80" />
      <el-table-column prop="price" label="单价" width="100">
        <template #default="{ row }">¥{{ row.price }}</template>
      </el-table-column>
      <el-table-column prop="stock" label="库存" width="100" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
          <el-popconfirm title="确认删除？" @confirm="onDelete(row)">
            <template #reference><el-button link type="danger">删除</el-button></template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑药品' : '新增药品'" width="480px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="编码"><el-input v-model="form.code" /></el-form-item>
        <el-form-item label="名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="规格"><el-input v-model="form.spec" /></el-form-item>
        <el-form-item label="单位"><el-input v-model="form.unit" placeholder="盒/瓶/支" /></el-form-item>
        <el-form-item label="单价"><el-input-number v-model="form.price" :min="0" :precision="2" /></el-form-item>
        <el-form-item label="库存"><el-input-number v-model="form.stock" :min="0" /></el-form-item>
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
import { listDrugs, createDrug, updateDrug, deleteDrug } from '@/api/drug'

const records = ref([])
const keyword = ref('')
const dialogVisible = ref(false)
const form = reactive({ id: null, code: '', name: '', spec: '', unit: '盒', price: 0, stock: 0 })

async function load() {
  records.value = await listDrugs(keyword.value ? { keyword: keyword.value } : {})
}
function openCreate() {
  Object.assign(form, { id: null, code: '', name: '', spec: '', unit: '盒', price: 0, stock: 0 })
  dialogVisible.value = true
}
function openEdit(row) { Object.assign(form, row); dialogVisible.value = true }
async function onSave() {
  if (!form.code || !form.name) return ElMessage.warning('编码和名称必填')
  if (form.id) await updateDrug(form.id, form)
  else await createDrug(form)
  ElMessage.success('已保存'); dialogVisible.value = false; load()
}
async function onDelete(row) { await deleteDrug(row.id); ElMessage.success('已删除'); load() }
onMounted(load)
</script>
