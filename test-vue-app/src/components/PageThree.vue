<template>
  <div class="page-three">
    <h2 style="margin-bottom: 16px">人体抓拍记录</h2>

    <el-alert v-if="loading" title="加载中…" type="info" :closable="false" />
    <el-alert v-else-if="error" :title="error" type="error" :closable="false" />

    <!-- 一行两框 -->
    <el-row v-else :gutter="16">
      <el-col v-for="(item, idx) in list" :key="idx" :xs="24" :sm="12" style="margin-bottom: 16px">
        <el-card shadow="hover" class="anatomy-card">
          <el-row :gutter="10">
            <!-- 左边：大图 -->
            <el-col :span="10" class="left-img">
              <el-image :src="`data:image/jpeg;base64,${item.snap_img}`" fit="contain" class="snap-img" />
            </el-col>

            <!-- 竖线 -->
            <el-col :span="1" class="divider-col">
              <div class="vertical-line" />
            </el-col>

            <!-- 右边：信息 -->
            <el-col :span="13" class="right-info">
              <div class="block">
                <div class="info-line">通道：{{ item.channel }}</div>
                <div class="info-line">相机：{{ item.camera_name }}</div>
                <div class="info-line">时间：{{ item.trigger }}</div>
              </div>

              <el-divider style="margin: 8px 0" />

              <div class="block">
                <div class="info-line">年龄：{{ ageMap[item.st_age] }}</div>
                <div class="info-line">性别：{{ item.person_gender }}</div>
                <div class="info-line">帽子：{{ item.cap_style }}</div>
                <div class="info-line">口罩：{{ maskMap[item.st_respirator_v2] }}</div>
                <div class="info-line">外套颜色：{{ colorMap[item.coat_color] || item.coat_color }}</div>
                <div class="info-line">吸烟：{{ item.st_smoking }}</div>
                <div class="info-line">反光衣：{{ reflectMap[item.st_reflective_clothes] }}</div>
              </div>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import axios from 'axios'

interface AnatomyItem {
  channel: number
  camera_name: string
  trigger: string
  snap_img: string
  st_age: string
  person_gender: string
  cap_style: string
  coat_color: string
  st_smoking: string
  st_respirator_v2: string
  st_reflective_clothes: string
}

// django 用这个地址
// const baseURL = 'http://127.0.0.1:8000/'

// springboot 用这个地址
const baseURL = 'http://127.0.0.1:8080/'

/* 中文映射 */
const ageMap: Record<string, string> = {
  st_child: '儿童',
  st_young: '青少年',
  st_adult: '成年人',
  st_old: '老年人'
}
const maskMap: Record<string, string> = {
  无: '无',
  yes: '有',
  st_respirator_v2_yes: '有'
}
const reflectMap: Record<string, string> = {
  无: '无',
  yes: '有'
}
const colorMap: Record<string, string> = {
  black: '黑',
  white: '白',
  red: '红',
  blue: '蓝',
  gray: '灰'
}

const list = ref<AnatomyItem[]>([])
const loading = ref(true)
const error = ref('')

onMounted(async () => {
  try {
    const { data } = await axios.get<AnatomyItem[]>(`${baseURL}getAnatomySnapPhoto/`)
    // list.value 就是从后端拿到的返回值，至于这些数据大家如何展示，如何设计出漂亮的UI界面，大家自行制作
    list.value = data
  } catch (e: any) {
    error.value = e.message || '请求失败'
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.page-three {
  padding: 20px;
  background: #f5f7fa;
  min-height: 100%
}

.anatomy-card {
  border-radius: 8px
}

.left-img {
  display: flex;
  align-items: center
}

.snap-img {
  width: 100%;
  height: 240px;
  border-radius: 4px;
  border: 1px solid #e4e7ed
}

.divider-col {
  display: flex;
  align-items: center
}

.vertical-line {
  width: 1px;
  height: 240px;
  background: #dcdfe6
}

.right-info .block {
  display: flex;
  flex-direction: column;
  gap: 4px
}

.info-line {
  font-size: 13px;
  color: #606266
}
</style>