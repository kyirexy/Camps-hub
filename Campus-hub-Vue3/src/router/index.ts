import { createRouter, createWebHistory } from 'vue-router'
import Home from '@/views/Home.vue'
import ProfileView from '@/views/ProfileView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: Home
    },
    {
      path: '/market',
      name: 'market',
      component: () => import('@/views/Market.vue')
    },
    {
      path: '/course',
      name: 'course',
      component: () => import('@/views/Course.vue')
    },
    {
      path: '/classroom',
      name: 'classroom',
      component: () => import('@/views/Classroom.vue')
    },
    {
      path: '/activity',
      name: 'activity',
      component: () => import('@/views/Activity.vue')
    },
    {
      path: '/about',
      name: 'about',
      // route level code-splitting
      // this generates a separate chunk (About.[hash].js) for this route
      // which is lazy-loaded when the route is visited.
      component: () => import('../views/AboutView.vue'),
    },
    {
      path: '/profile',
      name: 'profile',
      component: ProfileView,
      meta: { requiresAuth: true }
    }
  ],
})

export default router
