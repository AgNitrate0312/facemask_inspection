<template>
  <div class="top-bar">
    <div class="user-info">
      <el-avatar
        size="small"
        src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png"
      />
      <span class="user-name">Admin</span>
    </div>
  </div>
</template>

<script setup lang="ts">
// 后续 axios 拿到真实用户信息后，把头像/昵称换成动态数据即可
</script>

<style scoped>
.top-bar {
  height: 52px;              /* 顶栏高度 */
  background-color: #fff;
  border-bottom: 1px solid #e4e7ed;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  padding: 0 16px;
}
.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
}
.user-name {
  font-size: 14px;
  color: #606266;
}
</style>