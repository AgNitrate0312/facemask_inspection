<template>
  <div class="page-one">
    <h2 style="margin-bottom: 16px">预警人脸</h2>

    <el-alert v-if="loading" title="加载中…" type="info" :closable="false" />
    <el-alert v-else-if="error" :title="error" type="error" :closable="false" />

    <!-- 关键：el-row 每行 3 列 -->
    <el-row v-else :gutter="16">
      <el-col v-for="(item, idx) in list" :key="idx" :xs="24" :sm="12" :md="8" style="margin-bottom: 16px">
        <el-card shadow="hover" class="warn-card">
          <el-row :gutter="10">
            <!-- 左 -->
            <el-col :span="8" class="left-col">
              <el-image :src="`data:image/jpeg;base64,${item.snap_img}`" fit="contain" class="snap-img" />
              <div class="info-line">通道：{{ item.channel }}</div>
              <div class="info-line">相机：{{ item.camera_name }}</div>
              <div class="info-line">时间：{{ timeOnly(item.create_time) }}</div>
            </el-col>

            <!-- 中 -->
            <el-col :span="4" class="mid-col">
              <div class="similarity-box">
                <div class="label">相似度</div>
                <div class="score">{{ item.similarity }}%</div>
              </div>
            </el-col>

            <!-- 右 -->
            <el-col :span="12" class="right-col">
              <el-image :src="`data:image/jpeg;base64,${item.img}`" fit="contain" class="face-img" />
              <div class="info-line">姓名：{{ item.person_name }}</div>
              <div class="info-line">性别：{{ item.person_gender }}</div>
              <div class="info-line">库名：{{ item.lib_name }}</div>
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

export interface WarnFace {
  camera_name: string
  channel: number
  create_time: string
  img: string
  snap_img: string
  lib_name: string
  person_name: string
  person_gender: string
  similarity: number
}

// django 用这个地址
// const baseURL = 'http://127.0.0.1:8000'

// springboot 用这个地址
const baseURL = 'http://127.0.0.1:8080'

const list = ref<WarnFace[]>([])
const loading = ref(true)
const error = ref('')

onMounted(async () => {
  try {
    const { data } = await axios.get<WarnFace[]>(`${baseURL}/getWarnFaces/`)
    // list.value 就是从后端拿到的返回值，至于这些数据大家如何展示，如何设计出漂亮的UI界面，大家自行制作
    list.value = data
  } catch (e: any) {
    error.value = e.message || '请求失败'
  } finally {
    loading.value = false
  }
})

function timeOnly(datetime: string): string {
  return datetime.split(' ')[1] ?? datetime
}
</script>

<style scoped>
.page-one {
  padding: 20px;
  background: #f5f7fa;
  min-height: 100%
}

.warn-card {
  border-radius: 8px
}

.left-col,
.right-col {
  display: flex;
  flex-direction: column;
  gap: 6px
}

.snap-img,
.face-img {
  width: 100%;
  height: 100px;
  /* ← 原来是 140px，改小 */
  border-radius: 4px;
  border: 1px solid #e4e7ed;
}

.info-line {
  font-size: 13px;
  color: #606266
}

.mid-col {
  display: flex;
  align-items: center;
  justify-content: center
}

.similarity-box {
  text-align: center
}

.similarity-box .label {
  font-size: 12px;
  color: #909399
}

.similarity-box .score {
  font-size: 24px;
  font-weight: bold;
  color: #409eff
}
</style>