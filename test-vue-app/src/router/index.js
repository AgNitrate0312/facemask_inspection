import { createRouter, createWebHistory } from 'vue-router'
import PageOne from '../components/PageOne.vue'
import PageTwo from '../components/PageTwo.vue'
import PageThree from '../components/PageThree.vue'
import PageFour from '../components/PageFour.vue'
import PageFive from '../components/PageFive.vue'

const routes = [
  { path: '/', redirect: '/page-one' },
  { path: '/page-one', component: PageOne },
  { path: '/page-two', component: PageTwo },
  { path: '/page-three', component: PageThree },
  { path: '/page-four',  component: PageFour },
  { path: '/page-five',  component: PageFive },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

export default router
