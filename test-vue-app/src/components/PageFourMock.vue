<template>
  <div class="page-container">
    <div class="header-section">
      <div class="title-bar">
        <h2>😷 口罩佩戴识别监控 (Mock演示)</h2>
        <el-tag type="warning" effect="dark">测试模式</el-tag>
      </div>

      <!-- 统计看板 -->
      <div class="dashboard-section">
        <el-row :gutter="20">
          <el-col :span="16">
            <div class="stat-cards">
              <el-card shadow="hover" class="stat-card" :body-style="{ padding: '15px' }">
                <div class="stat-content">
                  <div class="stat-value">{{ stats.total_today }}</div>
                  <div class="stat-label">今日抓拍</div>
                </div>
              </el-card>
              <el-card shadow="hover" class="stat-card" :body-style="{ padding: '15px' }">
                <div class="stat-content">
                  <div class="stat-value" :class="getRateColor(stats.wearing_rate)">{{ stats.wearing_rate }}%</div>
                  <div class="stat-label">佩戴率</div>
                </div>
              </el-card>
              <el-card shadow="hover" class="stat-card" :body-style="{ padding: '15px' }">
                <div class="stat-content">
                  <div class="stat-value text-danger">{{ stats.no_mask_count }}</div>
                  <div class="stat-label">未佩戴</div>
                </div>
              </el-card>
              <el-card shadow="hover" class="stat-card" :body-style="{ padding: '15px' }">
                 <div class="stat-content">
                   <div class="stat-value text-warning">{{ stats.pending_handle_count }}</div>
                   <div class="stat-label">待处理</div>
                 </div>
              </el-card>
            </div>
          </el-col>
          <el-col :span="8">
             <div class="chart-container" ref="chartRef"></div>
          </el-col>
        </el-row>
      </div>
      
      <!-- 筛选栏 -->
      <div class="filter-card">
        <el-form :inline="true" :model="queryForm" size="default" class="search-form">
          <el-form-item label="通道">
            <el-input v-model.number="queryForm.channel" placeholder="例如: 1" style="width: 100px">
              <template #prefix><el-icon><Monitor /></el-icon></template>
            </el-input>
          </el-form-item>
          <el-form-item label="摄像机">
            <el-input v-model="queryForm.cameraName" placeholder="输入名称" style="width: 140px">
              <template #prefix><el-icon><VideoCamera /></el-icon></template>
            </el-input>
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="queryForm.maskStatus" placeholder="全部状态" clearable style="width: 120px">
              <el-option label="✅ 佩戴" value="佩戴" />
              <el-option label="❌ 未佩戴" value="未佩戴" />
              <el-option label="⚠️ 佩戴不全" value="佩戴不完全" />
            </el-select>
          </el-form-item>
          <el-form-item label="性别">
            <el-select v-model="queryForm.personGender" placeholder="全部" clearable style="width: 100px">
              <el-option label="男" value="男" />
              <el-option label="女" value="女" />
            </el-select>
          </el-form-item>
          <el-form-item label="年龄">
             <div class="age-range">
               <el-input v-model.number="queryForm.minAge" placeholder="Min" style="width: 60px" />
               <span class="separator">-</span>
               <el-input v-model.number="queryForm.maxAge" placeholder="Max" style="width: 60px" />
             </div>
          </el-form-item>
          
          <el-form-item class="action-buttons">
            <el-button type="primary" @click="handleSearch" :icon="Search">查询</el-button>
            <el-button @click="resetQuery" :icon="Refresh">重置</el-button>
            <el-button type="success" @click="handleExport" :icon="Download">导出</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>

    <!-- 列表区域 -->
    <div class="content-area" v-loading="loading">
      <el-empty v-if="list.length === 0 && !loading" description="暂无符合条件的记录" />
      
      <el-row :gutter="20" v-else>
        <el-col v-for="(item, idx) in list" :key="item.id" :xs="24" :sm="12" :md="8" :lg="6" style="margin-bottom: 20px">
          <div class="record-card">
            <!-- 图片区域 -->
            <div class="card-image-wrapper">
              <el-image 
                :src="item.snap_img" 
                fit="cover" 
                class="card-img" 
                loading="lazy"
                :preview-src-list="[item.snap_img]"
              >
                <template #error>
                  <div class="image-error">
                    <el-icon :size="32"><Picture /></el-icon>
                    <span>暂无图片</span>
                  </div>
                </template>
              </el-image>
              
              <!-- 悬浮状态标签 -->
              <div class="card-status-badge">
                <el-tag :type="getStatusType(item.mask_status)" effect="dark" round>
                  {{ item.mask_status }}
                </el-tag>
              </div>
            </div>

            <!-- 信息区域 -->
            <div class="card-content">
              <div class="card-header">
                <div class="camera-info">
                  <el-icon><VideoCamera /></el-icon>
                  <span class="camera-name" :title="item.camera_name">{{ item.camera_name }}</span>
                </div>
                <span class="channel-badge">CH{{ item.channel }}</span>
              </div>

              <div class="info-grid">
                <div class="info-item">
                  <span class="label">时间</span>
                  <span class="value">{{ formatTime(item.trigger) }}</span>
                </div>
                <div class="info-item">
                  <span class="label">属性</span>
                  <span class="value">
                    <el-icon v-if="item.person_gender==='男'" color="#409EFF"><Male /></el-icon>
                    <el-icon v-else color="#F56C6C"><Female /></el-icon>
                    {{ item.st_age_value }}岁
                  </span>
                </div>
              </div>

              <!-- 处理状态条 -->
              <div class="handle-bar" :class="getHandleClass(item.handle_status)">
                <div class="handle-status">
                   <el-icon><component :is="getHandleIcon(item.handle_status)" /></el-icon>
                   <span>{{ getHandleStatusText(item.handle_status) }}</span>
                </div>
                <div class="actions">
                   <el-button type="primary" link size="small" @click="openEditDialog(item)">处理</el-button>
                   <el-popconfirm title="确定删除?" @confirm="handleDelete(item.id)">
                      <template #reference>
                        <el-button type="danger" link size="small">删除</el-button>
                      </template>
                   </el-popconfirm>
                </div>
              </div>
              
              <div v-if="item.remark" class="remark-box">
                <el-icon><Comment /></el-icon>
                <span class="remark-text">{{ item.remark }}</span>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>

      <!-- 分页控件 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="queryForm.page"
          v-model:page-size="queryForm.size"
          :page-sizes="[8, 12, 24]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSearch"
          @current-change="handleSearch"
          background
        />
      </div>
    </div>

    <!-- 处理弹窗 -->
    <el-dialog v-model="dialogVisible" title="📝 记录处理" width="450px" destroy-on-close align-center>
      <el-form :model="editForm" label-width="80px" class="edit-form">
        <el-form-item label="识别修正">
          <el-radio-group v-model="editForm.maskStatus" size="small">
            <el-radio-button label="佩戴" />
            <el-radio-button label="未佩戴" />
            <el-radio-button label="佩戴不完全" />
          </el-radio-group>
        </el-form-item>
        <el-form-item label="处理状态">
           <el-select v-model="editForm.handleStatus" style="width: 100%">
            <el-option label="⏳ 未处理" :value="0" />
            <el-option label="✅ 已处理" :value="1" />
            <el-option label="🚫 忽略" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注说明">
          <el-input 
            v-model="editForm.remark" 
            type="textarea" 
            :rows="3" 
            placeholder="请填写处理意见或情况说明..." 
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitEdit">确认提交</el-button>
        </div>
      </template>
    </el-dialog>

  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, reactive, nextTick } from 'vue'
import * as echarts from 'echarts'
import { 
  Monitor, VideoCamera, Search, Refresh, Picture, Download,
  Male, Female, CircleCheck, Warning, CircleClose, Comment 
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

// --- 假数据生成器 ---
const generateMockData = (count: number) => {
  const statuses = ['佩戴', '未佩戴', '佩戴不完全']
  const genders = ['男', '女']
  const remarks = ['', '已教育', '误报', '', '']
  
  return Array.from({ length: count }).map((_, i) => ({
    id: i + 1,
    camera_name: `教学楼${['东','西','南','北'][Math.floor(Math.random()*4)]}门摄像头`,
    channel: Math.floor(Math.random() * 4) + 1,
    trigger: new Date(Date.now() - Math.random() * 1000000000).toISOString().replace('Z', ''),
    mask_status: statuses[Math.floor(Math.random() * statuses.length)],
    person_gender: genders[Math.floor(Math.random() * genders.length)],
    st_age_value: Math.floor(Math.random() * 40 + 18).toString(),
    snap_img: `https://picsum.photos/300/200?random=${i}`, // 使用随机占位图
    handle_status: Math.floor(Math.random() * 3), // 0, 1, 2
    remark: remarks[Math.floor(Math.random() * remarks.length)]
  }))
}

// 全部假数据
const allMockData = generateMockData(50)

// --- 状态定义 ---
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

const list = ref<MaskRecord[]>([])
const total = ref(0)
const loading = ref(false)
const dialogVisible = ref(false)
const chartRef = ref<HTMLElement>()

const queryForm = reactive({
  channel: undefined as number | undefined,
  cameraName: '',
  maskStatus: '',
  personGender: '',
  minAge: undefined as number | undefined,
  maxAge: undefined as number | undefined,
  page: 1,
  size: 12
})

const stats = reactive({
  total_today: 0,
  mask_wearing_count: 0,
  no_mask_count: 0,
  partial_mask_count: 0,
  pending_handle_count: 0,
  wearing_rate: '0.0'
})

const editForm = reactive({
  id: 0,
  handleStatus: 0,
  remark: '',
  maskStatus: ''
})

// --- 模拟查询逻辑 ---
const handleSearch = () => {
  loading.value = true
  setTimeout(() => {
    // 1. 过滤
    let filtered = allMockData.filter(item => {
      if (queryForm.channel && item.channel !== queryForm.channel) return false
      if (queryForm.cameraName && !item.camera_name.includes(queryForm.cameraName)) return false
      if (queryForm.maskStatus && item.mask_status !== queryForm.maskStatus) return false
      if (queryForm.personGender && item.person_gender !== queryForm.personGender) return false
      const age = parseInt(item.st_age_value)
      if (queryForm.minAge && age < queryForm.minAge) return false
      if (queryForm.maxAge && age > queryForm.maxAge) return false
      return true
    })
    
    // 2. 排序 (时间倒序)
    filtered.sort((a, b) => new Date(b.trigger).getTime() - new Date(a.trigger).getTime())

    // 3. 分页
    total.value = filtered.length
    const start = (queryForm.page - 1) * queryForm.size
    list.value = filtered.slice(start, start + queryForm.size)
    
    loading.value = false
    ElMessage.success('数据已刷新 (Mock)')
    
    // 刷新统计
    updateMockStatistics()
  }, 500)
}

const updateMockStatistics = () => {
  const total = allMockData.length
  const wearing = allMockData.filter(i => i.mask_status === '佩戴').length
  const notWearing = allMockData.filter(i => i.mask_status === '未佩戴').length
  const partial = allMockData.filter(i => i.mask_status === '佩戴不完全').length
  const pending = allMockData.filter(i => i.handle_status === 0).length
  
  stats.total_today = total
  stats.mask_wearing_count = wearing
  stats.no_mask_count = notWearing
  stats.partial_mask_count = partial
  stats.pending_handle_count = pending
  stats.wearing_rate = total > 0 ? (wearing / total * 100).toFixed(1) : '0.0'
  
  initChart()
}

const initChart = () => {
  nextTick(() => {
    if (!chartRef.value) return
    
    // 销毁旧实例（防止 resize 报错）
    const existingChart = echarts.getInstanceByDom(chartRef.value)
    if (existingChart) existingChart.dispose()

    const myChart = echarts.init(chartRef.value)
    const option = {
      tooltip: { trigger: 'item' },
      legend: { 
          orient: 'vertical',
          right: 10,
          top: 'center',
          itemWidth: 10,
          itemHeight: 10,
          textStyle: { fontSize: 12 }
      },
      series: [
        {
          name: '今日识别',
          type: 'pie',
          radius: ['45%', '75%'],
          center: ['35%', '50%'],
          avoidLabelOverlap: false,
          itemStyle: {
            borderRadius: 5,
            borderColor: '#fff',
            borderWidth: 2
          },
          label: { show: false, position: 'center' },
          emphasis: {
            label: { show: true, fontSize: 16, fontWeight: 'bold' }
          },
          labelLine: { show: false },
          data: [
            { value: stats.mask_wearing_count, name: '佩戴', itemStyle: { color: '#67c23a' } },
            { value: stats.no_mask_count, name: '未佩戴', itemStyle: { color: '#f56c6c' } },
            { value: stats.partial_mask_count, name: '不规范', itemStyle: { color: '#e6a23c' } }
          ]
        }
      ]
    }
    myChart.setOption(option)
    
    // 监听窗口变化
    window.addEventListener('resize', () => {
        myChart.resize()
    })
  })
}

const handleExport = () => {
  ElMessage.success('正在导出 Excel (Mock演示)...')
  setTimeout(() => {
    ElMessage.success('导出成功!')
  }, 1000)
}

const resetQuery = () => {
  queryForm.channel = undefined
  queryForm.cameraName = ''
  queryForm.maskStatus = ''
  queryForm.personGender = ''
  queryForm.minAge = undefined
  queryForm.maxAge = undefined
  queryForm.page = 1
  handleSearch()
}

// --- 辅助函数 ---
const getStatusType = (status: string) => {
  switch (status) {
    case '未佩戴': return 'danger'
    case '佩戴不完全': return 'warning'
    case '佩戴': return 'success'
    default: return 'info'
  }
}

const getHandleStatusText = (status: number) => {
  switch (status) {
    case 0: return '待处理'
    case 1: return '已处理'
    case 2: return '已忽略'
    default: return '未知'
  }
}

const getHandleClass = (status: number) => {
  switch (status) {
    case 0: return 'status-pending'
    case 1: return 'status-done'
    case 2: return 'status-ignored'
    default: return ''
  }
}

const getHandleIcon = (status: number) => {
   switch (status) {
    case 0: return 'Warning'
    case 1: return 'CircleCheck'
    case 2: return 'CircleClose'
    default: return 'Warning'
  }
}

const formatTime = (timeStr: string) => {
  if (!timeStr) return '--'
  return timeStr.replace('T', ' ').substring(5, 16)
}

const getRateColor = (rate: string) => {
    const val = parseFloat(rate)
    if (val >= 90) return 'text-success'
    if (val >= 70) return 'text-warning'
    return 'text-danger'
}

// --- 编辑逻辑 (Mock) ---
const openEditDialog = (item: MaskRecord) => {
  editForm.id = item.id
  editForm.handleStatus = item.handle_status
  editForm.remark = item.remark
  editForm.maskStatus = item.mask_status
  dialogVisible.value = true
}

const submitEdit = () => {
  // 模拟更新
  const target = allMockData.find(i => i.id === editForm.id)
  if (target) {
    target.handle_status = editForm.handleStatus
    target.remark = editForm.remark
    target.mask_status = editForm.maskStatus
    ElMessage.success('更新成功 (Mock)')
    dialogVisible.value = false
    handleSearch() // 刷新视图
  }
}

const handleDelete = (id: number) => {
  const idx = allMockData.findIndex(i => i.id === id)
  if (idx !== -1) {
    allMockData.splice(idx, 1)
    ElMessage.success('删除成功 (Mock)')
    handleSearch()
  }
}

onMounted(() => {
  handleSearch()
})
</script>

<style scoped>
.page-container {
  padding: 24px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.header-section {
  margin-bottom: 24px;
}

.title-bar {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.title-bar h2 {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
  margin: 0;
}

.dashboard-section {
    margin-bottom: 24px;
}

.stat-cards {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 16px;
    height: 100%;
}

.stat-card {
    height: 100%;
    display: flex;
    flex-direction: column;
    justify-content: center;
    border-radius: 8px;
}

.stat-content {
    text-align: center;
}

.stat-value {
    font-size: 28px;
    font-weight: bold;
    color: #303133;
    margin-bottom: 8px;
    font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

.stat-label {
    font-size: 14px;
    color: #909399;
}

.text-danger { color: #f56c6c; }
.text-warning { color: #e6a23c; }
.text-success { color: #67c23a; }

.chart-container {
    height: 160px;
    background: white;
    border-radius: 8px;
    padding: 10px;
    box-shadow: 0 1px 4px rgba(0,0,0,0.05);
    border: 1px solid #ebeef5;
}

.filter-card {
  background: white;
  padding: 20px 20px 0 20px;
  border-radius: 8px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.05);
}

.search-form {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.age-range {
  display: flex;
  align-items: center;
}

.separator {
  margin: 0 8px;
  color: #909399;
}

.content-area {
  min-height: 400px;
}

/* 卡片样式优化 */
.record-card {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid #ebeef5;
  display: flex;
  flex-direction: column;
}

.record-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.08);
  border-color: transparent;
}

.card-image-wrapper {
  position: relative;
  height: 180px;
  overflow: hidden;
  background: #f5f7fa;
}

.card-img {
  width: 100%;
  height: 100%;
  transition: transform 0.5s ease;
}

.record-card:hover .card-img {
  transform: scale(1.05);
}

.image-error {
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  color: #c0c4cc;
  font-size: 14px;
  gap: 8px;
}

.card-status-badge {
  position: absolute;
  top: 12px;
  right: 12px;
}

.card-content {
  padding: 16px;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.camera-info {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #606266;
  font-weight: 500;
  font-size: 14px;
  flex: 1;
  overflow: hidden;
}

.camera-name {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.channel-badge {
  background: #f0f2f5;
  color: #909399;
  font-size: 12px;
  padding: 2px 6px;
  border-radius: 4px;
  font-family: monospace;
}

.info-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
  margin-bottom: 16px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.info-item .label {
  font-size: 12px;
  color: #909399;
}

.info-item .value {
  font-size: 14px;
  color: #303133;
  display: flex;
  align-items: center;
  gap: 4px;
  font-weight: 500;
}

.handle-bar {
  margin-top: auto;
  padding-top: 12px;
  border-top: 1px solid #ebeef5;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.handle-status {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  font-weight: 500;
}

/* 状态颜色 */
.status-pending { color: #e6a23c; }
.status-done { color: #67c23a; }
.status-ignored { color: #909399; }

.remark-box {
  margin-top: 12px;
  background: #fdf6ec;
  padding: 8px;
  border-radius: 4px;
  font-size: 12px;
  color: #e6a23c;
  display: flex;
  align-items: flex-start;
  gap: 6px;
}

.remark-text {
  line-height: 1.4;
}

.pagination-wrapper {
  margin-top: 32px;
  display: flex;
  justify-content: center;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}
</style>
