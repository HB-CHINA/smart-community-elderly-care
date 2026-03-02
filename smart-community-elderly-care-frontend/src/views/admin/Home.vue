<!-- src/views/admin/Home.vue -->
<template>
  <div class="admin-home">
    <!-- 数据统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :xs="24" :sm="12" :md="6" v-for="stat in statsData" :key="stat.title">
        <el-card class="stat-card" :body-style="{ padding: '20px' }">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-value">{{ stat.value }}</div>
              <div class="stat-title">{{ stat.title }}</div>
            </div>
            <el-icon class="stat-icon" :size="50" :style="{ color: stat.color }">
              <component :is="stat.icon" />
            </el-icon>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="charts-row">
      <el-col :xs="24" :md="12">
        <el-card>
          <template #header>
            <span class="card-title">健康数据趋势</span>
          </template>
          <div ref="healthChart" style="height: 300px;"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :md="12">
        <el-card>
          <template #header>
            <span class="card-title">服务订单统计</span>
          </template>
          <div ref="orderChart" style="height: 300px;"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 最近报警 -->
    <el-row :gutter="20" class="alarm-row">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="card-header">
              <span class="card-title">最近报警</span>
              <el-link type="primary" @click="$router.push('/admin/alarm/handle')">查看全部</el-link>
            </div>
          </template>
          <el-table :data="recentAlarms" style="width: 100%">
            <el-table-column prop="elderName" label="报警老人" width="120" />
            <el-table-column prop="type" label="报警类型" width="120">
              <template #default="{ row }">
                <el-tag :type="row.type === 'SOS' ? 'danger' : 'warning'">
                  {{ row.type }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="content" label="报警内容" />
            <el-table-column prop="time" label="报警时间" width="180" />
            <el-table-column prop="status" label="状态" width="100
