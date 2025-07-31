import { createRouter, createWebHistory } from 'vue-router'
import { setupRouterGuards } from './guards'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      name: 'Home',
      component: () => import('@/views/Home.vue'),
      meta: { title: '首页' }
    },
    {
      path: '/auth/login',
      name: 'Login',
      component: () => import('@/views/auth/Login.vue'),
      meta: { title: '登录' }
    },
    {
      path: '/auth/register',
      name: 'Register',
      component: () => import('@/views/auth/Register.vue'),
      meta: { title: '注册' }
    },
    {
      path: '/activities/:id',
      name: 'ActivityDetail',
      component: () => import('@/views/activity/Detail.vue'),
      meta: { title: '活动详情' }
    },
    {
      path: '/activities/create',
      name: 'CreateActivity',
      component: () => import('@/views/activity/Create.vue'),
      meta: { title: '创建活动', requiresAuth: true }
    },
    {
      path: '/profile',
      name: 'Profile',
      component: () => import('@/views/user/Profile.vue'),
      meta: { title: '个人中心', requiresAuth: true }
    },
    {
      path: '/users/:id',
      name: 'PublicProfile',
      component: () => import('@/views/user/PublicProfile.vue'),
      meta: { title: '用户主页' }
    },
    {
      path: '/:pathMatch(.*)*',
      name: 'NotFound',
      component: () => import('@/views/error/404.vue'),
      meta: { title: '页面未找到' }
    }
  ]
})

// 设置路由守卫
setupRouterGuards(router)

export default router
