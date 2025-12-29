<template>
    <div class="page-five">
        <h2 style="margin-bottom: 16px;">人体抓拍记录</h2>

        <el-alert v-if="loading" title="加载中…" type="info" :closable="false" />
        <el-alert v-else-if="error" :title="error" type="error" :closable="false" />

        <!-- 表格展示 :data标签配置数据源为tableData数组 表格组件会自动遍历数组的每一个数据项tableData[i],并把它渲染成表格中的每一行-->
        <el-table v-else :data="tableData" style="width: 100%">
            <!-- prop标签 配置表格行需要展示哪些字段-->
            <!--例如：<el-table-column/>表示创建一个表格列，prop="person_gender"表示当前列与tableData[i].person_gender字段相关联。 label="性别"，网页将这	一列显示为性别-->
            <el-table-column prop="person_gender" label="性别" width="180" />
            <el-table-column prop="st_age" label="年龄" width="180" />
            <el-table-column prop="coat_color" label="外套颜色" />
        </el-table>
    </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import axios from 'axios'

// 定义一个元数据接口 表示表格每一行的数据属性
interface SnapPhoto {
    person_gender: string
    st_age: string
    coat_color: string
}

const loading = ref(true)
const error = ref('')
// 声明一个数组，来保存后端返回的数据，这是Vue3的TS语法，ref<SnapPhoto[]>表示创建一个响应式变量，类型是SnapPhoto数组
// ([]) 表示初始化为空数组
const tableData = ref<SnapPhoto[]>([])

const fetchData = async () => {
    // 向指定url发送get请求
    // axios.get('http://127.0.0.1:8000/getAnatomySnapPhoto')  // django 后端地址 运行django项目使用这个地址
     axios.get('http://127.0.0.1:8080/getAnatomySnapPhoto')  // springboot 后端地址 运行springboot项目使用这个地址
        .then(response => {
            // 如果请求成功则会执行then方法 其中response.data表示拿到的数据对象
            // 将数据赋值给表格数据源tableData
            tableData.value = response.data;
            // 停止加载图标
            loading.value = false;
        })
        .catch(error => {
            // 如果请求错误，则会执行catch方法，error是错误对象
            console.error(error); // 处理错误
            error.value = error.message
        });
}
// 在浏览器加载这个页面时，首先调用fetchData方法获取数据，然后在渲染
onMounted(fetchData)
</script>

<style scoped>
.page-five {
    padding: 20px;
    background: #f5f7fa;
    min-height: 100%;
}
</style>