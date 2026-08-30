<template>
  <div ref="chartContainer" class="map-chart"></div>
</template>

<script setup>
import { ref, watch, onMounted, onUnmounted, computed } from 'vue'
import * as echarts from 'echarts'
import { useUserInfo } from '/@/stores/userInfo'

const store = useUserInfo()
const chartContainer = ref(null)
let chartInstance = null

// 城市地理数据配置
const cityConfig = computed(() => ({
  shangluo: {
    geoPath: '611000_full.json',
    name: '商洛市'
  },
  hanzhong: {
    geoPath: '610700_full.json',
    name: '汉中市'
  },
  ankang: {
    geoPath: '610900_full.json',
    name: '安康市'
  },
  weinan: {
    geoPath: '610500_full.json',
    name: '渭南市'
  },
  baoji: {
    geoPath: '610300_full.json',
    name: '宝鸡市'
  },
  xian: {
    geoPath: '610100_full.json',
    name: '西安市'
  }
}))

// 固定散点数据
const scatterPoints = [
  { value: [116.4, 39.9], color: '#f58414' },
  { value: [115.9, 39.96], color: '#e7ab0b' },
  { value: [115.98, 39.69], color: '#1DE9B6' }
]

// 初始化图表
const initChart = async (cityKey) => {
  if (!chartContainer.value) return

  // 清理旧实例
  if (chartInstance) {
    chartInstance.dispose()
  }

  // 获取城市配置
  const { geoPath, name } = cityConfig.value[cityKey] || cityConfig.value.shangluo
  
  try {
    // 加载地理数据
    const response = await fetch(`/geo-proxy/${geoPath}`)
    if (!response.ok) throw new Error(`HTTP ${response.status}`)
    const geoJson = await response.json()
    
    // 注册地图
    echarts.registerMap(name, geoJson)

    // 创建实例
    chartInstance = echarts.init(chartContainer.value)
    
    // 配置项
    const option = {
      title: {
        text: `${name}各县区示意图`,
        left: 'center',
        textStyle: { color: '#ffffff' }
      },
      geo: {
        show: false,
        map: name,
        roam: true,
        itemStyle: {
          normal: {
            areaColor: 'transparent',
            borderColor: '#3fdaff',
            borderWidth: 2,
            shadowColor: 'rgba(63, 218, 255, 0.5)',
            shadowBlur: 30
          },
          emphasis: { areaColor: '#2B91B7' }
        }
      },
      series: [
        {
          type: 'map',
          map: name,
          roam: false,
          label: {
            normal: {
              show: true,
              textStyle: { color: '#fff', fontSize: 15, fontWeight: 500 }
            },
            emphasis: { textStyle: { color: 'rgb(183,185,14)' } }
          },
          itemStyle: {
            normal: {
              areaColor: '#003669',
              borderColor: '#3fdaff',
              borderWidth: 2,
              shadowColor: 'rgba(63, 218, 255,0.6)',
              shadowBlur: 35
            },
            emphasis: { areaColor: '#2B91B7' }
          },
          zoom: 1.1
        },
        {
          type: 'effectScatter',
          coordinateSystem: 'geo',
          zlevel: 1,
          rippleEffect: { period: 15, scale: 4, brushType: 'fill' },
          label: {
            normal: {
              formatter: '{b}',
              position: 'right',
              offset: [15, 0],
              color: '#1DE9B6',
              show: true
            }
          },
          itemStyle: { normal: { color: '#1DE9B6', shadowBlur: 20 } },
          symbolSize: 12,
          data: scatterPoints.map(p => ({
            ...p,
            itemStyle: { color: p.color }
          }))
        }
      ]
    }

    chartInstance.setOption(option)
  } catch (error) {
    console.error('地图加载失败:', error)
  }
}

// 监听城市变化
watch(
  () => store.userInfos.city,
  (newCity) => {
    console.log('城市变更:', newCity)
    initChart(newCity)
  },
  { immediate: true }
)

// 生命周期管理
onMounted(() => {
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  chartInstance?.dispose()
})

// 响应式调整
const handleResize = () => {
  chartInstance?.resize()
}
</script>

<style>
.map-chart {
  width: 100%;
  height: 100%;
  min-width: 0;
  min-height: 0;
  padding: 10px;
  box-sizing: border-box;
  background-color: #091c3d;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.15);
}
</style>
