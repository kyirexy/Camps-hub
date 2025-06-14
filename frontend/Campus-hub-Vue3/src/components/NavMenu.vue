<template>
  <nav class="main-nav">
    <el-menu
      mode="horizontal"
      :ellipsis="false"
      class="nav-menu"
      :router="true"
    >
      <el-menu-item index="/">
        <el-icon><HomeFilled /></el-icon>
        首页
      </el-menu-item>
      <el-menu-item index="/market">
        <el-icon><Shop /></el-icon>
        二手市场
      </el-menu-item>
      <el-menu-item index="/course">
        <el-icon><Reading /></el-icon>
        课程助手
      </el-menu-item>
      <el-menu-item index="/classroom">
        <el-icon><School /></el-icon>
        空教室
      </el-menu-item>
      <el-menu-item index="/activities">
        <el-icon><Calendar /></el-icon>
        校园活动
      </el-menu-item>
    </el-menu>
  </nav>
</template>

<style scoped>
.main-nav {
  margin: 0 auto;
  max-width: 1200px;
}
</style>