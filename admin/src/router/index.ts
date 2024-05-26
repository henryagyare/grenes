import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '@/pages/index.vue'
import { getCurrentUser } from 'vuefire'

// const DEV_MODE = false
const DEV_MODE = import.meta.env.MODE === 'development'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
      meta: {
        requiresAuth: !DEV_MODE
      }
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('@/pages/LoginView.vue')
    },
    {
      path: '/challenges',
      name: 'challenges',
      component: () => import('@/pages/Challenges.vue'),
      meta: {
        requiresAuth: !DEV_MODE
      }
    },
    {
      path: '/about',
      name: 'about', // route level code-splitting
      // this generates a separate chunk (About.[hash].js) for this route
      // which is lazy-loaded when the route is visited.
      component: () => import('@/pages/AboutView.vue')
    }
  ]
})

router.beforeEach(async (to, from) => {
  const currentUser = await getCurrentUser()

  if (to.meta.requiresAuth && to.name !== 'login') {
    // if the user is not logged in, redirect to the login page
    if (!currentUser) {
      return {
        path: '/login',
        query: {
          redirect: to.fullPath
        }
      }
    }
  }
})

export default router
