<template>
  <div class="page-four">
    <h2 style="margin-bottom: 16px">口罩佩戴识别记录</h2>

    <el-alert v-if="loading" title="数据加载中…" type="info" :closable="false" />
    <el-alert v-else-if="error" :title="error" type="error" :closable="false" />

    <el-row v-else :gutter="16">
      <el-col v-for="(item, idx) in list" :key="idx" :xs="24" :sm="12" :md="8" style="margin-bottom: 16px">
        <el-card shadow="hover" class="mask-card" :body-style="{ padding: '10px' }">
          <el-row :gutter="10">
            <!-- 左侧：抓拍图片 -->
            <el-col :span="10" class="left-img">
              <el-image 
                :src="item.snap_img.startsWith('data:') ? item.snap_img : `data:image/jpeg;base64,${item.snap_img}`" 
                fit="contain" 
                class="snap-img" 
              >
                <template #error>
                  <div class="image-slot">
                    <el-icon><icon-picture /></el-icon>
                  </div>
                </template>
              </el-image>
            </el-col>

            <!-- 右侧：信息展示 -->
            <el-col :span="14" class="right-info">
              <div class="info-block">
                <div class="info-row">
                  <span class="label">时间：</span>
                  <span class="value">{{ formatTime(item.trigger) }}</span>
                </div>
                <div class="info-row">
                  <span class="label">位置：</span>
                  <span class="value">{{ item.camera_name }} (通道{{ item.channel }})</span>
                </div>
                <div class="info-row">
                  <span class="label">属性：</span>
                  <span class="value">{{ item.person_gender }} / {{ Number(item.st_age_value).toFixed(0) }}岁</span>
                </div>
                
                <el-divider style="margin: 8px 0" />
                
                <div class="status-box">
                  <span class="label">佩戴状态：</span>
                  <!-- 根据状态显示不同颜色的标签/文字 -->
                  <el-tag :type="getStatusType(item.mask_status)" effect="dark" class="status-tag">
                    {{ item.mask_status }}
                  </el-tag>
                </div>
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
import { Picture as IconPicture } from '@element-plus/icons-vue'

/**
 * 接口数据定义
 * 对应后端 /getMaskDetection 返回结构
 */
interface MaskRecord {
  camera_name: string         // 抓拍相机名称
  channel: number             // 通道号
  trigger: string             // 抓拍时间
  mask_status: string         // 口罩状态: "佩戴" | "未佩戴" | "佩戴不完全" | "未知"
  person_gender: string       // 性别
  st_age_value: string        // 预估年龄
  snap_img: string            // Base64 (不含前缀)
}

// 后端接口地址 (根据现有项目惯例)
// springboot 用这个地址
const baseURL = 'http://127.0.0.1:8080'
// 接口路径
const apiPath = '/getMaskDetection'

const list = ref<MaskRecord[]>([])
const loading = ref(true)
const error = ref('')

onMounted(async () => {
  try {
    const { data } = await axios.get<MaskRecord[]>(`${baseURL}${apiPath}`)
    list.value = data
  } catch (e: any) {
    error.value = e.message || '请求失败，请确认后端接口已启动'
  } finally {
    loading.value = false
  }
})

// 状态映射逻辑
// "未佩戴" -> Red
// "佩戴不完全" -> Yellow
// "佩戴" -> Green
function getStatusType(status: string) {
  switch (status) {
    case '未佩戴': return 'danger'       // 红色
    case '佩戴不完全': return 'warning'   // 黄色
    case '佩戴': return 'success'        // 绿色
    default: return 'info'               // 灰色 (未知或其他)
  }
}

// 既然后端直接返回中文状态，前端直接显示即可，无需转换函数
// function getStatusText(status: string) { ... }

function formatTime(timeStr: string) {
  // 简单处理时间格式
  if (!timeStr) return '--'
  return timeStr.replace('T', ' ')
}
</script>

<style scoped>
.page-four {
  padding: 20px;
  background: #f5f7fa;
  min-height: 100%;
}

.mask-card {
  border-radius: 8px;
  transition: all 0.3s;
}

.mask-card:hover {
  transform: translateY(-2px);
}

.left-img {
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f0f2f5;
  border-radius: 4px;
  height: 140px;
  overflow: hidden;
}

.snap-img {
  width: 100%;
  height: 100%;
}

.image-slot {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  background: #f5f7fa;
  color: #909399;
  font-size: 24px;
}

.right-info {
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.info-block {
  padding: 4px 0;
}

.info-row {
  margin-bottom: 6px;
  font-size: 13px;
  line-height: 1.4;
  display: flex;
}

.info-row .label {
  color: #909399;
  width: 45px;
  flex-shrink: 0;
}

.info-row .value {
  color: #606266;
  font-weight: 500;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.status-box {
  display: flex;
  align-items: center;
  margin-top: 8px;
}

.status-box .label {
  font-size: 13px;
  color: #909399;
  margin-right: 8px;
}

.status-tag {
  font-weight: bold;
}
</style>
