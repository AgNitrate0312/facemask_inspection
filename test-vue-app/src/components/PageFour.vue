<template>
  <div class="page-four">
    <div class="header">
      <h2 style="margin-bottom: 0">口罩佩戴识别记录</h2>
      <!-- 右侧：查询表单（放在抽屉或直接展开，这里直接展开） -->
      <div class="filter-bar">
        <!-- 简单的一行查询条件 -->
        <el-form :inline="true" :model="queryForm" size="small" class="search-form">
          <el-form-item label="通道号">
            <el-input v-model.number="queryForm.channel" placeholder="如 1" style="width: 80px" />
          </el-form-item>
          <el-form-item label="摄像机">
            <el-input v-model="queryForm.cameraName" placeholder="名称关键字" style="width: 120px" />
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="queryForm.maskStatus" placeholder="全部" clearable style="width: 100px">
              <el-option label="佩戴" value="佩戴" />
              <el-option label="未佩戴" value="未佩戴" />
              <el-option label="佩戴不完全" value="佩戴不完全" />
            </el-select>
          </el-form-item>
          <el-form-item label="性别">
            <el-select v-model="queryForm.personGender" placeholder="全部" clearable style="width: 80px">
              <el-option label="男" value="男" />
              <el-option label="女" value="女" />
            </el-select>
          </el-form-item>
          <el-form-item label="年龄">
             <div style="display: flex; width: 140px;">
               <el-input v-model.number="queryForm.minAge" placeholder="最小" style="width: 60px" />
               <span style="padding: 0 4px">-</span>
               <el-input v-model.number="queryForm.maxAge" placeholder="最大" style="width: 60px" />
             </div>
          </el-form-item>
          <el-form-item label="时间">
             <el-date-picker
                v-model="queryForm.dateRange"
                type="datetimerange"
                range-separator="至"
                start-placeholder="开始"
                end-placeholder="结束"
                value-format="YYYY-MM-DD HH:mm:ss"
                style="width: 320px"
             />
          </el-form-item>
          
          <el-form-item>
            <el-button type="primary" @click="handleSearch">查询</el-button>
            <el-button @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>

    <el-alert v-if="loading" title="数据加载中…" type="info" :closable="false" />
    <el-alert v-else-if="error" :title="error" type="error" :closable="false" />

    <!-- 列表区域 -->
    <div v-else class="content-area">
      <el-row :gutter="16">
        <el-col v-for="(item, idx) in list" :key="idx" :xs="24" :sm="12" :md="8" :lg="6" style="margin-bottom: 16px">
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
                    <span class="value" :title="item.trigger">{{ formatTime(item.trigger) }}</span>
                  </div>
                  <div class="info-row">
                    <span class="label">位置：</span>
                    <span class="value" :title="item.camera_name">{{ item.camera_name }} (通道{{ item.channel }})</span>
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

      <!-- 分页控件 -->
      <div class="pagination-bar">
        <el-pagination
          v-model:current-page="queryForm.page"
          v-model:page-size="queryForm.size"
          :page-sizes="[8, 12, 24, 48]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSearch"
          @current-change="handleSearch"
        />
      </div>
    </div>

    <!-- 处理弹窗 -->
    <el-dialog v-model="dialogVisible" title="处理记录" width="400px">
      <el-form :model="editForm" label-width="80px">
        <el-form-item label="口罩状态">
          <el-select v-model="editForm.maskStatus" placeholder="修改识别结果">
            <el-option label="佩戴" value="佩戴" />
            <el-option label="未佩戴" value="未佩戴" />
            <el-option label="佩戴不完全" value="佩戴不完全" />
          </el-select>
        </el-form-item>
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
 */
interface MaskRecord {
  id: number
  camera_name: string
  channel: number
  trigger: string
  mask_status: string
  person_gender: string
  st_age_value: string
  snap_img: string
  handle_status: number
  remark: string
}

interface QueryForm {
  channel?: number
  cameraName?: string
  maskStatus?: string
  personGender?: string
  minAge?: number
  maxAge?: number
  dateRange: string[] // [startTime, endTime]
  page: number
  size: number
}

// 后端接口地址
const baseURL = 'http://127.0.0.1:8080'
const apiPath = '/searchMaskDetection'

const list = ref<MaskRecord[]>([])
const total = ref(0)
const loading = ref(false)
const error = ref('')

// 查询表单
const queryForm = reactive<QueryForm>({
  channel: undefined,
  cameraName: '',
  maskStatus: '',
  personGender: '',
  minAge: undefined,
  maxAge: undefined,
  dateRange: [],
  page: 1,
  size: 12
})

// 弹窗相关
const dialogVisible = ref(false)
const editForm = reactive({
  id: 0,
  handleStatus: 0,
  remark: '',
  maskStatus: '' // 新增：允许修改口罩状态
})

onMounted(() => {
  handleSearch()
})

const handleSearch = async () => {
  loading.value = true
  try {
    // 构造请求参数
    const params: any = {
      channel: queryForm.channel,
      cameraName: queryForm.cameraName,
      maskStatus: queryForm.maskStatus,
      personGender: queryForm.personGender,
      minAge: queryForm.minAge,
      maxAge: queryForm.maxAge,
      page: queryForm.page,
      size: queryForm.size
    }
    
    if (queryForm.dateRange && queryForm.dateRange.length === 2) {
      params.startTime = queryForm.dateRange[0]
      params.endTime = queryForm.dateRange[1]
    }

    const { data } = await axios.post(`${baseURL}${apiPath}`, params)
    
    if (data) {
      list.value = data.records
      total.value = data.total
    }
    error.value = ''
  } catch (e: any) {
    error.value = e.message || '请求失败，请确认后端接口已启动'
  } finally {
    loading.value = false
  }
}

const resetQuery = () => {
  queryForm.channel = undefined
  queryForm.cameraName = ''
  queryForm.maskStatus = ''
  queryForm.personGender = ''
  queryForm.minAge = undefined
  queryForm.maxAge = undefined
  queryForm.dateRange = []
  queryForm.page = 1
  handleSearch()
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
  // 只显示月-日 时:分，节省空间
  return timeStr.replace('T', ' ').substring(5, 16)
}

// 打开编辑弹窗
const openEditDialog = (item: MaskRecord) => {
  editForm.id = item.id
  editForm.handleStatus = item.handle_status || 0
  editForm.remark = item.remark || ''
  editForm.maskStatus = item.mask_status || '' // 初始化口罩状态
  dialogVisible.value = true
}

// 提交编辑
const submitEdit = async () => {
  try {
    const formData = new FormData()
    formData.append('id', editForm.id.toString())
    formData.append('handleStatus', editForm.handleStatus.toString())
    formData.append('remark', editForm.remark)
    formData.append('maskStatus', editForm.maskStatus) // 提交修改后的口罩状态

    const res = await axios.post(`${baseURL}/updateMaskRecord`, formData)
    if (res.data && (res.data === '更新成功' || res.status === 200)) {
      ElMessage.success('处理成功')
      dialogVisible.value = false
      handleSearch() // 刷新当前页
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
      handleSearch() // 刷新当前页
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
  display: flex;
  flex-direction: column;
}

.header {
  background: #fff;
  padding: 16px;
  border-radius: 8px;
  margin-bottom: 16px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.05);
}

.filter-bar {
  margin-top: 16px;
}

.search-form {
  margin-bottom: -18px; /* 抵消 el-form-item 的默认下边距 */
}

.content-area {
  flex: 1;
  display: flex;
  flex-direction: column;
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
  height: 160px;
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
  width: 45px;
  flex-shrink: 0;
}

.status-tag {
  font-weight: bold;
}

.pagination-bar {
  margin-top: 20px;
  display: flex;
  justify-content: center;
  background: #fff;
  padding: 12px;
  border-radius: 8px;
}
</style>
