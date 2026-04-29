import { createRouter, createWebHashHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/login',
    name: 'login',
    component: () => import('@/views/login/Login.vue'),
    meta: { public: true, title: '登录' }
  },
  {
    path: '/',
    component: () => import('@/layouts/DefaultLayout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'dashboard',
        component: () => import('@/views/dashboard/Index.vue'),
        meta: { title: '工作台', icon: 'HomeFilled', roles: ['ADMIN', 'RECEPTION', 'DOCTOR'] }
      },

      // 挂号台
      {
        path: 'reception/patients',
        name: 'reception-patients',
        component: () => import('@/views/reception/PatientList.vue'),
        meta: { title: '患者管理', icon: 'User', roles: ['ADMIN', 'RECEPTION'], group: '挂号台' }
      },
      {
        path: 'reception/register',
        name: 'reception-register',
        component: () => import('@/views/reception/Register.vue'),
        meta: { title: '挂号', icon: 'EditPen', roles: ['ADMIN', 'RECEPTION'], group: '挂号台' }
      },
      {
        path: 'reception/registrations',
        name: 'reception-registrations',
        component: () => import('@/views/reception/RegistrationList.vue'),
        meta: { title: '挂号查询', icon: 'List', roles: ['ADMIN', 'RECEPTION'], group: '挂号台' }
      },

      // 医生工作站
      {
        path: 'doctor/queue',
        name: 'doctor-queue',
        component: () => import('@/views/doctor/Queue.vue'),
        meta: { title: '待诊队列', icon: 'Bell', roles: ['ADMIN', 'DOCTOR'], group: '医生工作站' }
      },
      {
        path: 'doctor/visit/:id',
        name: 'doctor-visit',
        component: () => import('@/views/doctor/Visit.vue'),
        meta: { title: '就诊', roles: ['ADMIN', 'DOCTOR'], hidden: true }
      },

      // 基础信息
      {
        path: 'base/department',
        name: 'base-department',
        component: () => import('@/views/base/Department.vue'),
        meta: { title: '科室', icon: 'OfficeBuilding', roles: ['ADMIN'], group: '基础信息' }
      },
      {
        path: 'base/doctor',
        name: 'base-doctor',
        component: () => import('@/views/base/Doctor.vue'),
        meta: { title: '医生', icon: 'Avatar', roles: ['ADMIN'], group: '基础信息' }
      },
      {
        path: 'base/drug',
        name: 'base-drug',
        component: () => import('@/views/base/Drug.vue'),
        meta: { title: '药品', icon: 'FirstAidKit', roles: ['ADMIN'], group: '基础信息' }
      },

      // 系统
      {
        path: 'system/user',
        name: 'system-user',
        component: () => import('@/views/system/User.vue'),
        meta: { title: '用户', icon: 'UserFilled', roles: ['ADMIN'], group: '系统管理' }
      }
    ]
  },
  { path: '/:pathMatch(.*)*', redirect: '/dashboard' }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

router.beforeEach((to) => {
  const userStore = useUserStore()
  if (to.meta?.public) return true
  if (!userStore.isLogin) return { path: '/login', query: { redirect: to.fullPath } }
  if (to.meta?.roles && !to.meta.roles.includes(userStore.role)) {
    return { path: '/dashboard' }
  }
  return true
})

export default router
