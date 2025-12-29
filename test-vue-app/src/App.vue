<template>
  <el-container style="height: 100vh;">
    <!-- 左侧菜单 -->
    <el-aside width="200px" style="background-color: #f5f5f5;">
      <el-menu :default-active="activeIndex" class="el-menu-vertical-demo" @select="handleSelect">
        <el-menu-item index="1">人脸比对</el-menu-item>
        <el-menu-item index="2">人脸属性</el-menu-item>
        <el-menu-item index="3">人体属性</el-menu-item>
        <el-menu-item index="4">页面四</el-menu-item>
        <el-menu-item index="5">页面五</el-menu-item>
      </el-menu>
    </el-aside>

    <!-- 右侧 -->
    <el-container direction="vertical"> <!-- 关键：列方向 flex -->
      <!-- 1. 顶栏 -->
      <TopBar />

      <!-- 2. 页面内容 -->
      <el-main style="padding: 0; overflow: auto;">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>
<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import TopBar from './components/TopBar.vue'

const router = useRouter()
const activeIndex = ref('1')

const handleSelect = (key: string) => {
  const map: Record<string, string> = {
    '1': '/page-one',
    '2': '/page-two',
    '3': '/page-three',
    '4': '/page-four',
    '5': '/page-five',
  }
  activeIndex.value = key
  const path = map[key]
  if (path) router.push(path)
}
</script>
