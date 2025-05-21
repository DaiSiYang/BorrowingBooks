<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import { Document, Reading, User, Warning, Refresh, Download } from '@element-plus/icons-vue'

// 页面加载状态
const loading = ref(true)

// 统计数据
const statisticsData = ref({
  totalBooks: 5243,
  totalBorrowings: 18624,
  activeUsers: 3542,
  overdueBooks: 127
})

// 图表实例引用
const borrowTrendChart = ref(null)
const bookCategoryChart = ref(null)
const userActivityChart = ref(null)

// 热门图书数据
const hotBooks = ref([
  { rank: 1, title: '活着', author: '余华', category: '小说', borrowCount: 342 },
  { rank: 2, title: '三体', author: '刘慈欣', category: '科幻', borrowCount: 315 },
  { rank: 3, title: '人类简史', author: '尤瓦尔·赫拉利', category: '历史', borrowCount: 287 },
  { rank: 4, title: '平凡的世界', author: '路遥', category: '小说', borrowCount: 264 },
  { rank: 5, title: '解忧杂货店', author: '东野圭吾', category: '小说', borrowCount: 253 },
  { rank: 6, title: '自控力', author: '凯利·麦格尼格尔', category: '心理学', borrowCount: 238 },
  { rank: 7, title: '围城', author: '钱钟书', category: '小说', borrowCount: 226 },
  { rank: 8, title: '明朝那些事儿', author: '当年明月', category: '历史', borrowCount: 215 }
])

// 借阅趋势数据
const borrowTrendData = ref({
  months: ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月'],
  borrowings: [156, 172, 143, 187, 192, 214, 253, 268, 231, 264, 287, 301],
  returns: [142, 165, 138, 174, 187, 201, 240, 252, 223, 248, 261, 279]
})

// 图书分类数据
const bookCategoryData = ref([
  { value: 1856, name: '文学小说' },
  { value: 1243, name: '社科历史' },
  { value: 853, name: '经管励志' },
  { value: 642, name: '科学技术' },
  { value: 382, name: '生活艺术' },
  { value: 267, name: '其他类别' }
])

// 用户活跃度数据
const userActivityData = ref({
  days: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'],
  borrowings: [68, 73, 82, 76, 91, 142, 156],
  activeUsers: [42, 46, 53, 49, 62, 87, 96]
})

// 时间范围
const dateRange = ref([
  new Date(new Date().getTime() - 30 * 24 * 60 * 60 * 1000),
  new Date()
])

// 初始化借阅趋势图表
const initBorrowTrendChart = () => {
  const chartDom = borrowTrendChart.value
  if (!chartDom) return
  
  const myChart = echarts.init(chartDom)
  const option = {
    title: {
      text: '月度借阅趋势',
      left: 'center',
      textStyle: {
        color: '#3d2c29'
      }
    },
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    legend: {
      data: ['借出', '归还'],
      bottom: '0%'
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '10%',
      top: '15%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: borrowTrendData.value.months,
      axisTick: {
        alignWithLabel: true
      }
    },
    yAxis: {
      type: 'value',
      name: '数量'
    },
    series: [
      {
        name: '借出',
        type: 'bar',
        barWidth: '40%',
        data: borrowTrendData.value.borrowings,
        itemStyle: {
          color: '#8a5f41'
        }
      },
      {
        name: '归还',
        type: 'line',
        smooth: true,
        data: borrowTrendData.value.returns,
        itemStyle: {
          color: '#e36049'
        },
        lineStyle: {
          width: 3
        }
      }
    ]
  }
  
  myChart.setOption(option)
  
  window.addEventListener('resize', () => {
    myChart.resize()
  })
}

// 初始化图书分类图表
const initBookCategoryChart = () => {
  const chartDom = bookCategoryChart.value
  if (!chartDom) return
  
  const myChart = echarts.init(chartDom)
  const option = {
    title: {
      text: '馆藏图书分类',
      left: 'center',
      textStyle: {
        color: '#3d2c29'
      }
    },
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 'left',
      data: bookCategoryData.value.map(item => item.name)
    },
    series: [
      {
        name: '图书分类',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: false,
          position: 'center'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: '14',
            fontWeight: 'bold'
          }
        },
        labelLine: {
          show: false
        },
        data: bookCategoryData.value,
        color: ['#8a5f41', '#e36049', '#4c8dae', '#e89b0c', '#637d21', '#9d7cd8']
      }
    ]
  }
  
  myChart.setOption(option)
  
  window.addEventListener('resize', () => {
    myChart.resize()
  })
}

// 初始化用户活跃度图表
const initUserActivityChart = () => {
  const chartDom = userActivityChart.value
  if (!chartDom) return
  
  const myChart = echarts.init(chartDom)
  const option = {
    title: {
      text: '周用户活跃度',
      left: 'center',
      textStyle: {
        color: '#3d2c29'
      }
    },
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'cross',
        label: {
          backgroundColor: '#6a7985'
        }
      }
    },
    legend: {
      data: ['活跃用户', '借阅数量'],
      bottom: 0
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '10%',
      top: '15%',
      containLabel: true
    },
    xAxis: [
      {
        type: 'category',
        boundaryGap: false,
        data: userActivityData.value.days
      }
    ],
    yAxis: [
      {
        type: 'value',
        name: '数量',
        position: 'left'
      }
    ],
    series: [
      {
        name: '活跃用户',
        type: 'line',
        stack: 'Total',
        areaStyle: {
          opacity: 0.3
        },
        emphasis: {
          focus: 'series'
        },
        data: userActivityData.value.activeUsers,
        smooth: true,
        itemStyle: {
          color: '#4c8dae'
        }
      },
      {
        name: '借阅数量',
        type: 'line',
        stack: 'Total',
        areaStyle: {
          opacity: 0.3
        },
        emphasis: {
          focus: 'series'
        },
        data: userActivityData.value.borrowings,
        smooth: true,
        itemStyle: {
          color: '#e36049'
        }
      }
    ]
  }
  
  myChart.setOption(option)
  
  window.addEventListener('resize', () => {
    myChart.resize()
  })
}

// 更新数据
const updateStatistics = () => {
  loading.value = true
  
  // 模拟异步数据获取
  setTimeout(() => {
    loading.value = false
    ElMessage({
      message: '统计数据已更新',
      type: 'success'
    })
  }, 800)
}

// 导出数据
const exportData = (type) => {
  ElMessage({
    message: `${type}数据导出成功`,
    type: 'success'
  })
}

// 页面初始化
onMounted(() => {
  setTimeout(() => {
    loading.value = false
    initBorrowTrendChart()
    initBookCategoryChart()
    initUserActivityChart()
  }, 800)
})
</script>

<template>
  <div class="statistics-page">
    <el-card class="page-header">
      <div class="header-content">
        <div class="header-title">
          <h2>统计分析</h2>
          <p>查看系统运营数据和借阅统计</p>
        </div>
        <div class="header-actions">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            size="default"
          />
          <el-button type="primary" @click="updateStatistics">
            <el-icon class="el-icon--left"><Refresh /></el-icon>更新数据
          </el-button>
        </div>
      </div>
    </el-card>

    <!-- 数据概览 -->
    <div class="stat-card-container">
      <el-card class="stat-card" shadow="hover">
        <div class="stat-icon book-icon">
          <el-icon><Document /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ statisticsData.totalBooks }}</div>
          <div class="stat-label">馆藏总量</div>
        </div>
        <div class="stat-trend positive">
          <span>+24</span>
          <span>较上月</span>
        </div>
      </el-card>
      
      <el-card class="stat-card" shadow="hover">
        <div class="stat-icon borrow-icon">
          <el-icon><Reading /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ statisticsData.totalBorrowings }}</div>
          <div class="stat-label">总借阅量</div>
        </div>
        <div class="stat-trend positive">
          <span>+315</span>
          <span>较上月</span>
        </div>
      </el-card>
      
      <el-card class="stat-card" shadow="hover">
        <div class="stat-icon user-icon">
          <el-icon><User /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ statisticsData.activeUsers }}</div>
          <div class="stat-label">活跃用户</div>
        </div>
        <div class="stat-trend positive">
          <span>+86</span>
          <span>较上月</span>
        </div>
      </el-card>
      
      <el-card class="stat-card" shadow="hover">
        <div class="stat-icon overdue-icon">
          <el-icon><Warning /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ statisticsData.overdueBooks }}</div>
          <div class="stat-label">逾期图书</div>
        </div>
        <div class="stat-trend negative">
          <span>+12</span>
          <span>较上月</span>
        </div>
      </el-card>
    </div>

    <!-- 图表统计 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :xs="24" :lg="16">
        <el-card shadow="hover" class="chart-card">
          <div class="chart-header">
            <h3>借阅趋势</h3>
            <el-button text @click="exportData('借阅趋势')">
              <el-icon><Download /></el-icon> 导出
            </el-button>
          </div>
          <div ref="borrowTrendChart" class="chart-container"></div>
        </el-card>
      </el-col>
      
      <el-col :xs="24" :lg="8">
        <el-card shadow="hover" class="chart-card">
          <div class="chart-header">
            <h3>馆藏分布</h3>
            <el-button text @click="exportData('馆藏分布')">
              <el-icon><Download /></el-icon> 导出
            </el-button>
          </div>
          <div ref="bookCategoryChart" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="chart-row">
      <el-col :xs="24" :lg="12">
        <el-card shadow="hover" class="chart-card">
          <div class="chart-header">
            <h3>用户活跃度</h3>
            <el-button text @click="exportData('用户活跃度')">
              <el-icon><Download /></el-icon> 导出
            </el-button>
          </div>
          <div ref="userActivityChart" class="chart-container"></div>
        </el-card>
      </el-col>
      
      <el-col :xs="24" :lg="12">
        <el-card shadow="hover" class="chart-card">
          <div class="chart-header">
            <h3>热门借阅榜</h3>
            <el-button text @click="exportData('热门借阅')">
              <el-icon><Download /></el-icon> 导出
            </el-button>
          </div>
          <el-table :data="hotBooks" style="width: 100%" stripe>
            <el-table-column prop="rank" label="排名" width="70" align="center">
              <template #default="scope">
                <el-tag 
                  :type="scope.row.rank <= 3 ? ['danger', 'warning', 'success'][scope.row.rank - 1] : 'info'"
                  effect="plain"
                  size="small"
                >
                  {{ scope.row.rank }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="title" label="书名" show-overflow-tooltip />
            <el-table-column prop="author" label="作者" width="120" />
            <el-table-column prop="category" label="分类" width="90" />
            <el-table-column prop="borrowCount" label="借阅次数" width="90" align="center" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <el-backtop :right="20" :bottom="20" />
  </div>
</template>

<style scoped lang="scss">
.statistics-page {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100%;

  .page-header {
    margin-bottom: 20px;
    
    .header-content {
      display: flex;
      justify-content: space-between;
      align-items: center;
      flex-wrap: wrap;
      gap: 20px;
      
      .header-title {
        h2 {
          font-size: 22px;
          color: #3d2c29;
          margin: 0 0 5px 0;
        }
        
        p {
          font-size: 14px;
          color: #8a5f41;
          margin: 0;
        }
      }
      
      .header-actions {
        display: flex;
        gap: 15px;
        flex-wrap: wrap;
      }
    }
  }
  
  .stat-card-container {
    display: flex;
    gap: 20px;
    margin-bottom: 20px;
    flex-wrap: wrap;
    
    .stat-card {
      flex: 1;
      min-width: 200px;
      display: flex;
      align-items: center;
      padding: 10px 20px;
      
      .stat-icon {
        width: 60px;
        height: 60px;
        border-radius: 12px;
        display: flex;
        align-items: center;
        justify-content: center;
        margin-right: 15px;
        
        .el-icon {
          font-size: 30px;
          color: #fff;
        }
        
        &.book-icon {
          background-color: #8a5f41;
        }
        
        &.borrow-icon {
          background-color: #4c8dae;
        }
        
        &.user-icon {
          background-color: #637d21;
        }
        
        &.overdue-icon {
          background-color: #e36049;
        }
      }
      
      .stat-content {
        flex: 1;
        
        .stat-value {
          font-size: 24px;
          font-weight: 600;
          color: #3d2c29;
        }
        
        .stat-label {
          font-size: 14px;
          color: #606266;
        }
      }
      
      .stat-trend {
        display: flex;
        flex-direction: column;
        align-items: flex-end;
        font-size: 12px;
        
        &.positive {
          color: #67c23a;
        }
        
        &.negative {
          color: #f56c6c;
        }
      }
    }
  }
  
  .chart-row {
    margin-bottom: 20px;
  }
  
  .chart-card {
    height: 100%;
    margin-bottom: 20px;
    
    .chart-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding-bottom: 15px;
      border-bottom: 1px solid #ebeef5;
      margin-bottom: 15px;
      
      h3 {
        font-size: 16px;
        color: #3d2c29;
        margin: 0;
      }
    }
    
    .chart-container {
      height: 320px;
    }
  }
}

@media (max-width: 768px) {
  .statistics-page {
    padding: 10px;
    
    .stat-card-container {
      flex-direction: column;
      
      .stat-card {
        width: 100%;
      }
    }
  }
}
</style> 