<template>
  <div class="page-four-mock">
    <h2 style="margin-bottom: 16px">口罩佩戴识别记录 (Mock演示)</h2>

    <el-alert title="当前为模拟数据展示模式，仅用于效果预览" type="success" :closable="false" style="margin-bottom: 16px" />

    <el-row :gutter="16">
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
import { Picture as IconPicture } from '@element-plus/icons-vue'

/**
 * 接口数据定义
 */
interface MaskRecord {
  camera_name: string         
  channel: number             
  trigger: string             
  mask_status: string         
  person_gender: string       
  st_age_value: string        
  snap_img: string            
}

const list = ref<MaskRecord[]>([])

onMounted(() => {
  // 生成模拟数据
  list.value = [
    {
      camera_name: '大门入口',
      channel: 1,
      trigger: '2025-01-23 12:07:53',
      mask_status: '未佩戴',
      person_gender: '女',
      st_age_value: '26.000000',
      snap_img: '' // 暂无图片，显示默认占位符
    },
    {
      camera_name: '办公区域',
      channel: 2,
      trigger: '2025-01-23 12:08:10',
      mask_status: '佩戴',
      person_gender: '男',
      st_age_value: '30.500000',
      snap_img: ''
    },
    {
      camera_name: '电梯口',
      channel: 3,
      trigger: '2025-01-23 12:09:22',
      mask_status: '佩戴不完全',
      person_gender: '男',
      st_age_value: '45.000000',
      snap_img: ''
    },
    {
      camera_name: '前台大厅',
      channel: 4,
      trigger: '2025-01-23 12:15:30',
      mask_status: '佩戴',
      person_gender: '女',
      st_age_value: '22.000000',
      snap_img: ''
    },
    {
      camera_name: '后门出口',
      channel: 5,
      trigger: '2025-01-23 12:20:45',
      mask_status: '未佩戴',
      person_gender: '男',
      st_age_value: '35.000000',
      snap_img: ''
    }
  ]
})

function getStatusType(status: string) {
  switch (status) {
    case '未佩戴': return 'danger'       // 红色
    case '佩戴不完全': return 'warning'   // 黄色
    case '佩戴': return 'success'        // 绿色
    default: return 'info'               // 灰色
  }
}

function formatTime(timeStr: string) {
  if (!timeStr) return '--'
  return timeStr.replace('T', ' ')
}
</script>

<style scoped>
.page-four-mock {
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
