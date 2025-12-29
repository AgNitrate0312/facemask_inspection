<template>
  <div class="page-two">
    <h2 style="margin-bottom: 16px">抓拍记录</h2>

    <el-alert v-if="loading" title="加载中…" type="info" :closable="false" />
    <el-alert v-else-if="error" :title="error" type="error" :closable="false" />

    <!-- 一行两框 -->
    <el-row v-else :gutter="16">
      <el-col v-for="(item, idx) in list" :key="idx" :xs="24" :sm="12" style="margin-bottom: 16px">
        <el-card shadow="hover" class="snap-card">
          <el-row :gutter="10">
            <!-- 左边：图片 -->
            <el-col :span="10" class="left-img">
              <el-image :src="`data:image/jpeg;base64,${item.snap_img}`" fit="contain" class="snap-img" />
            </el-col>

            <!-- 中间竖线 -->
            <el-col :span="1" class="divider-col">
              <div class="vertical-line" />
            </el-col>

            <!-- 右边：信息 -->
            <el-col :span="13" class="right-info">
              <div class="block">
                <!-- <div>基础信息<el-divider style="margin: 8px 0" /></div> -->
                <div class="info-line">通道：{{ item.channel }}</div>
                <div class="info-line">相机：{{ item.camera_name }}</div>
                <div class="info-line">时间：{{ item.trigger }}</div>
              </div>
              <el-divider style="margin: 8px 0" />

              <div class="block">
                <!-- <div>属性信息<el-divider style="margin: 8px 0" /></div> -->
                <div class="info-line">年龄：{{ Number(item.st_age_value).toFixed(0) }} 岁</div>
                <div class="info-line">性别：{{ item.person_gender }}</div>
                <div class="info-line">胡子：{{ item.mustache_style }}</div>
                <div class="info-line">眼镜：{{ glassText(item.glass_style) }}</div>
                <div class="info-line">帽子：{{ item.cap_style }}</div>
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

interface SnapItem {
  channel: number
  camera_name: string
  trigger: string
  snap_img: string
  st_age_value: string
  person_gender: string
  mustache_style: string
  glass_style: string
  cap_style: string
}
// django 用这个地址
// const baseURL = 'http://127.0.0.1:8000/'

// springboot 用这个地址
const baseURL = 'http://127.0.0.1:8080/'

const list = ref<SnapItem[]>([])
const loading = ref(true)
const error = ref('')

onMounted(async () => {
  try {
    const { data } = await axios.get<SnapItem[]>(`${baseURL}getSnapPhoto/`)
    // list.value 就是从后端拿到的返回值，至于这些数据大家如何展示，如何设计出漂亮的UI界面，大家自行制作
    list.value = data
  } catch (e: any) {
    error.value = e.message || '请求失败'
  } finally {
    loading.value = false
  }
})

function glassText(style: string): string {
  switch (style) {
    case 'glasses_style_type_none':   return '无眼镜'
    case 'transparent_glasses':       return '透明眼镜'
    case 'sunglasses':                return '太阳镜'
    case 'st_ordinary_glasses':       return '普通眼镜'
    default:                          return style        // 兜底
  }
}
</script>

<style scoped>
.page-two {
  padding: 20px;
  background: #f5f7fa;
  min-height: 100%
}

.snap-card {
  border-radius: 8px
}

.left-img {
  display: flex;
  align-items: center
}

.snap-img {
  width: 100%;
  height: 200px;
  border-radius: 4px;
  border: 1px solid #e4e7ed
}

.divider-col {
  display: flex;
  align-items: center
}

.vertical-line {
  width: 1px;
  height: 200px;
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