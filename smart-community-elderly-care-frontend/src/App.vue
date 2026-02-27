<template>
  <router-view />
</template>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB',
  'Microsoft YaHei', '微软雅黑', Arial, sans-serif;
  background-color: #f5f7fa;
}

/* 适老化设计 - 大字体模式 */
.elderly-mode {
  font-size: 18px !important;
}

.elderly-mode .el-button {
  font-size: 18px !important;
  padding: 12px 24px !important;
}

.elderly-mode .el-input__inner {
  font-size: 18px !important;
  height: 48px !important;
}

.elderly-mode .el-menu-item {
  font-size: 18px !important;
}

/* 高对比度模式 */
.high-contrast {
  --el-color-primary: #0066cc;
  --el-color-success: #009900;
  --el-color-warning: #cc6600;
  --el-color-danger: #cc0000;
}

.high-contrast .el-button--primary {
  background-color: #0066cc !important;
  border-color: #0066cc !important;
}
</style>
