<template>
  <el-container style="height: 100vh">
    <el-aside width="220px" style="background: #001529;">
      <div class="brand">门诊 HIS</div>
      <el-menu
        :default-active="$route.path"
        :router="true"
        background-color="#001529"
        text-color="#bfcbd9"
        active-text-color="#fff"
        unique-opened>
        <template v-for="group in groupedMenus" :key="group.title">
          <el-sub-menu :index="group.title">
            <template #title>
              <el-icon><component :is="group.icon" /></el-icon>
              <span>{{ group.title }}</span>
            </template>
            <el-menu-item
              v-for="m in group.items"
              :key="m.path"
              :index="'/' + m.path">
              <el-icon v-if="m.meta.icon"><component :is="m.meta.icon" /></el-icon>
              <span>{{ m.meta.title }}</span>
            </el-menu-item>
          </el-sub-menu>
        </template>
        <el-menu-item v-for="m in topMenus" :key="m.path" :index="'/' + m.path">
          <el-icon v-if="m.meta.icon"><component :is="m.meta.icon" /></el-icon>
          <span>{{ m.meta.title }}</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="topbar">
        <div class="topbar-title">{{ $route.meta?.title || '' }}</div>
        <div class="topbar-right">
          <span class="role-tag">{{ roleLabel }}</span>
          <span class="user-name">{{ user?.realName }}</span>
          <el-button type="text" @click="onLogout">退出</el-button>
        </div>
      </el-header>
      <el-main>
        <router-view v-slot="{ Component }">
          <component :is="Component" />
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const user = computed(() => userStore.user)

const roleLabel = computed(() => ({
  ADMIN: '管理员', RECEPTION: '挂号员', DOCTOR: '医生'
}[userStore.role] || ''))

const allChildren = router.options.routes.find(r => r.path === '/').children

const accessible = computed(() => allChildren.filter(r =>
  !r.meta?.hidden && (!r.meta?.roles || r.meta.roles.includes(userStore.role))
))

const topMenus = computed(() => accessible.value.filter(r => !r.meta?.group))

const groupedMenus = computed(() => {
  const groups = {}
  accessible.value.forEach(r => {
    if (!r.meta?.group) return
    const g = r.meta.group
    if (!groups[g]) groups[g] = { title: g, icon: groupIcon(g), items: [] }
    groups[g].items.push(r)
  })
  return Object.values(groups)
})

function groupIcon(g) {
  return ({ '挂号台': 'EditPen', '医生工作站': 'Stethoscope', '基础信息': 'Files', '系统管理': 'Setting' }[g]) || 'Folder'
}

async function onLogout() {
  await userStore.logout()
  ElMessage.success('已退出')
  router.replace('/login')
}
</script>

<style scoped>
.brand {
  color: #fff;
  font-size: 18px;
  font-weight: 600;
  text-align: center;
  height: 60px;
  line-height: 60px;
  letter-spacing: 4px;
  background: rgba(255,255,255,0.05);
}
.topbar {
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid #ebeef5;
}
.topbar-title { font-size: 18px; font-weight: 500; }
.topbar-right { display: flex; align-items: center; gap: 12px; }
.role-tag {
  background: #ecf5ff;
  color: #409eff;
  padding: 2px 10px;
  border-radius: 10px;
  font-size: 12px;
}
.user-name { color: #606266; }
:deep(.el-aside)::-webkit-scrollbar { display: none; }
</style>
