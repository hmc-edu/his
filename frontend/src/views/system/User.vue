<template>
  <el-card class="page-card">
    <div class="page-toolbar">
      <el-input v-model="keyword" placeholder="用户名/姓名" clearable style="width: 240px" @keyup.enter="load(1)" />
      <el-button type="primary" @click="load(1)">搜索</el-button>
      <el-button type="success" @click="openCreate">新增用户</el-button>
    </div>

    <el-table :data="records" border stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="username" label="登录名" width="160" />
      <el-table-column prop="realName" label="真实姓名" width="140" />
      <el-table-column label="角色" width="120">
        <template #default="{ row }">
          <el-tag>{{ roleLabel(row.role) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.enabled ? 'success' : 'info'">{{ row.enabled ? '启用' : '禁用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="创建时间" width="180" />
      <el-table-column label="操作" width="200" fixed="right">
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

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑用户' : '新增用户'" width="480px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="登录名">
          <el-input v-model="form.username" :disabled="!!form.id" />
        </el-form-item>
        <el-form-item label="真实姓名">
          <el-input v-model="form.realName" />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="form.role">
            <el-option label="管理员 ADMIN" value="ADMIN" />
            <el-option label="挂号员 RECEPTION" value="RECEPTION" />
            <el-option label="医生 DOCTOR" value="DOCTOR" />
          </el-select>
        </el-form-item>
        <el-form-item label="启用">
          <el-switch v-model="form.enabled" />
        </el-form-item>
        <el-form-item :label="form.id ? '重置密码' : '初始密码'">
          <el-input v-model="form.password" :placeholder="form.id ? '留空则不修改' : '必填'" type="password" show-password />
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
import { pageUsers, createUser, updateUser, deleteUser } from '@/api/user'

const records = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(10)
const keyword = ref('')

const dialogVisible = ref(false)
const form = reactive({ id: null, username: '', password: '', realName: '', role: 'RECEPTION', enabled: true })

function roleLabel(r) { return ({ ADMIN: '管理员', RECEPTION: '挂号员', DOCTOR: '医生' })[r] || r }

async function load(p) {
  page.value = p
  const res = await pageUsers({ keyword: keyword.value, page: p, size: size.value })
  records.value = res.records
  total.value = res.total
}

function openCreate() {
  Object.assign(form, { id: null, username: '', password: '', realName: '', role: 'RECEPTION', enabled: true })
  dialogVisible.value = true
}
function openEdit(row) {
  Object.assign(form, { ...row, password: '' })
  dialogVisible.value = true
}
async function onSave() {
  if (!form.username || !form.realName || !form.role) return ElMessage.warning('用户名/姓名/角色必填')
  if (!form.id && !form.password) return ElMessage.warning('请设置初始密码')
  if (form.id) await updateUser(form.id, form)
  else await createUser(form)
  ElMessage.success('已保存')
  dialogVisible.value = false
  load(page.value)
}
async function onDelete(row) { await deleteUser(row.id); ElMessage.success('已删除'); load(page.value) }

onMounted(() => load(1))
</script>
