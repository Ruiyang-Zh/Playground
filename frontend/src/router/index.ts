import { createRouter, createWebHistory } from 'vue-router'
import { setupRouterGuards } from './guards'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      name: 'Home',
      component: () => import('@/views/Home.vue')
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
      component: () => import('@/views/activity/ActivityDetail.vue'),
      meta: { title: '活动详情', layout: 'activity' }
    },
    {
      path: '/activities/create',
      name: 'ActivityCreate',
      component: () => import('@/views/activity/ActivityCreate.vue'),
      meta: { title: '发布活动', requiresAuth: true }
    },
    {
      path: '/activities/:id/edit',
      name: 'ActivityEdit',
      component: () => import('@/views/activity/ActivityEdit.vue'),
      meta: { title: '编辑活动', requiresAuth: true }
    },
    {
      path: '/users/:userId',
      name: 'Profile',
      component: () => import('@/views/user/Profile.vue'),
      meta: { title: '用户主页' }
    },
    {
      path: '/:pathMatch(.*)*',
      name: 'NotFound',
      component: () => import('@/views/error/404.vue'),
      meta: { title: 'Not Found' }
    }
  ]
})

// 设置路由守卫
setupRouterGuards(router)

export default router
