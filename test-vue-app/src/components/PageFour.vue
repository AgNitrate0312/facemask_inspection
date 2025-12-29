<template>
  <div class="page-four">
    <div class="header">
      <h2 style="margin-bottom: 16px">口罩佩戴识别记录</h2>
      <el-button type="primary" size="small" @click="fetchData">刷新列表</el-button>
    </div>

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
                :preview-src-list="[item.snap_img.startsWith('data:') ? item.snap_img : `data:image/jpeg;base64,${item.snap_img}`]"
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
                  <span class="label">状态：</span>
                  <el-tag :type="getStatusType(item.mask_status)" effect="dark" size="small" class="status-tag">
                    {{ item.mask_status }}
                  </el-tag>
                </div>

                <div class="status-box" style="margin-top: 4px;">
                   <span class="label">处理：</span>
                   <el-tag :type="item.handle_status === 1 ? 'success' : (item.handle_status === 2 ? 'info' : 'warning')" size="small" effect="plain">
                     {{ getHandleStatusText(item.handle_status) }}
                   </el-tag>
                </div>
                
                <div class="info-row" v-if="item.remark" style="margin-top: 4px; color: #909399;">
                   <span class="label">备注：</span>
                   <span class="value" :title="item.remark">{{ item.remark }}</span>
                </div>

                <!-- 操作按钮 -->
                <div class="action-box" style="margin-top: 10px; display: flex; justify-content: flex-end; gap: 8px;">
                  <el-button type="primary" link size="small" @click="openEditDialog(item)">处理</el-button>
                  <el-popconfirm title="确定要删除这条记录吗？" @confirm="handleDelete(item.id)">
                    <template #reference>
                      <el-button type="danger" link size="small">删除</el-button>
                    </template>
                  </el-popconfirm>
                </div>

              </div>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
    </el-row>

    <!-- 处理弹窗 -->
    <el-dialog v-model="dialogVisible" title="处理记录" width="400px">
      <el-form :model="editForm" label-width="80px">
        <el-form-item label="处理状态">
          <el-select v-model="editForm.handleStatus" placeholder="请选择状态">
            <el-option label="未处理" :value="0" />
            <el-option label="已处理" :value="1" />
            <el-option label="忽略" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注信息">
          <el-input v-model="editForm.remark" type="textarea" placeholder="请输入备注，如：已警告" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitEdit">确定</el-button>
        </span>
      </template>
    </el-dialog>

  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, reactive } from 'vue'
import axios from 'axios'
import { Picture as IconPicture } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

/**
 * 接口数据定义
 * 对应后端 /getMaskDetection 返回结构
 */
interface MaskRecord {
  id: number
  camera_name: string         // 抓拍相机名称
  channel: number             // 通道号
  trigger: string             // 抓拍时间
  mask_status: string         // 口罩状态
  person_gender: string       // 性别
  st_age_value: string        // 预估年龄
  snap_img: string            // Base64
  handle_status: number       // 处理状态 0-未处理 1-已处理 2-忽略
  remark: string              // 备注
}

// 后端接口地址
const baseURL = 'http://127.0.0.1:8080'
const apiPath = '/getMaskDetection'

const list = ref<MaskRecord[]>([])
const loading = ref(true)
const error = ref('')

// 弹窗相关
const dialogVisible = ref(false)
const editForm = reactive({
  id: 0,
  handleStatus: 0,
  remark: ''
})

onMounted(() => {
  fetchData()
})

const fetchData = async () => {
  loading.value = true
  try {
    const { data } = await axios.get<MaskRecord[]>(`${baseURL}${apiPath}`)
    list.value = data
    error.value = ''
  } catch (e: any) {
    error.value = e.message || '请求失败，请确认后端接口已启动'
  } finally {
    loading.value = false
  }
}

// 状态映射逻辑
function getStatusType(status: string) {
  switch (status) {
    case '未佩戴': return 'danger'       // 红色
    case '佩戴不完全': return 'warning'   // 黄色
    case '佩戴': return 'success'        // 绿色
    default: return 'info'               // 灰色
  }
}

function getHandleStatusText(status: number) {
  switch (status) {
    case 0: return '未处理'
    case 1: return '已处理'
    case 2: return '忽略'
    default: return '未知'
  }
}

function formatTime(timeStr: string) {
  if (!timeStr) return '--'
  return timeStr.replace('T', ' ')
}

// 打开编辑弹窗
const openEditDialog = (item: MaskRecord) => {
  editForm.id = item.id
  editForm.handleStatus = item.handle_status || 0
  editForm.remark = item.remark || ''
  dialogVisible.value = true
}

// 提交编辑
const submitEdit = async () => {
  try {
    // 构造 FormData
    const formData = new FormData()
    formData.append('id', editForm.id.toString())
    formData.append('handleStatus', editForm.handleStatus.toString())
    formData.append('remark', editForm.remark)

    const res = await axios.post(`${baseURL}/updateMaskRecord`, formData)
    if (res.data && (res.data === '更新成功' || res.status === 200)) {
      ElMessage.success('处理成功')
      dialogVisible.value = false
      fetchData() // 刷新列表
    } else {
      ElMessage.error('操作失败')
    }
  } catch (e) {
    ElMessage.error('网络请求异常')
  }
}

// 删除记录
const handleDelete = async (id: number) => {
  try {
    const formData = new FormData()
    formData.append('id', id.toString())
    
    const res = await axios.post(`${baseURL}/deleteMaskRecord`, formData)
    if (res.data && (res.data === '删除成功' || res.status === 200)) {
      ElMessage.success('删除成功')
      fetchData() // 刷新列表
    } else {
      ElMessage.error('删除失败')
    }
  } catch (e) {
    ElMessage.error('网络请求异常')
  }
}

</script>

<style scoped>
.page-four {
  padding: 20px;
  background: #f5f7fa;
  min-height: 100%;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.mask-card {
  border-radius: 8px;
  transition: all 0.3s;
}

.mask-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

.left-img {
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f0f2f5;
  border-radius: 4px;
  height: 160px; /* 稍微调高一点 */
  overflow: hidden;
  cursor: pointer;
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
}

.status-box .label {
  font-size: 13px;
  color: #909399;
  margin-right: 8px;
  width: 45px; /* 对齐 */
  flex-shrink: 0;
}

.status-tag {
  font-weight: bold;
}
</style>
