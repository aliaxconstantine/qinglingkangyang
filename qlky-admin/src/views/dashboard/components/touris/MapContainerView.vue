<template>
  <div class="map-container">
    <div ref="mapContainer" class="map-content"></div>
    <div ref="panel" class="route-panel"></div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted, onUnmounted } from 'vue'
import AMapLoader from '@amap/amap-jsapi-loader'

const props = defineProps({
  sendData: {
    type: Object,
    default: () => ({ la: 0, ln: 0 })
  }
})

const mapContainer = ref(null)
const panel = ref(null)
const mapInstance = ref(null)
const geolocation = ref(null)
const driving = ref(null)
const currentPosition = ref({ lng: 0, lat: 0 })

// 安全配置
window._AMapSecurityConfig = {
  securityJsCode: "51d3c19c4f3d32b95c3b1765bdca779d"
}

// 初始化地图
const initMap = async () => {
  try {
    const AMap = await AMapLoader.load({
      key: "d9593cdfb2108bd5232f77a4ef0c545c",
      version: "2.0",
      plugins: ["AMap.Scale", 'AMap.Geolocation', 'AMap.Driving']
    })

    // 创建地图实例
    mapInstance.value = new AMap.Map(mapContainer.value, {
      viewMode: "3D",
      resizeEnable: true,
      zoom: 13,
      center: [props.sendData.la, props.sendData.ln]
    })

    // 初始化路径规划
    driving.value = new AMap.Driving({
      policy: 0,
      map: mapInstance.value,
      panel: panel.value
    })

    // 初始化定位服务
    initGeolocation(AMap)
  } catch (error) {
    console.error('地图初始化失败:', error)
  }
}

// 初始化定位
const initGeolocation = (AMap) => {
  geolocation.value = new AMap.Geolocation({
    enableHighAccuracy: true,
    offset: [10, 20],
    zoomToAccuracy: true
  })

  geolocation.value.getCurrentPosition((status, result) => {
    if (status === 'complete') {
      currentPosition.value = result.position
      updateRoute()
    } else {
      console.error('定位失败:', status)
    }
  })
}

// 更新路线规划
const updateRoute = () => {
  if (!props.sendData || !currentPosition.value) return
  
  const start = [currentPosition.value.lng, currentPosition.value.lat]
  const end = [props.sendData.la, props.sendData.ln]
  
  driving.value?.clear()
  driving.value?.search(start, end)
}

// 清理资源
const destroyMap = () => {
  driving.value?.clear()
  geolocation.value = null
  if (mapInstance.value) {
    mapInstance.value.destroy()
    mapInstance.value = null
  }
}

// 监听数据变化
watch(() => props.sendData, (newVal) => {
  if (newVal.la && newVal.ln) {
    updateRoute()
  }
})

// 生命周期
onMounted(initMap)
onUnmounted(destroyMap)
</script>

<style scoped>
.map-container {
  position: relative;
  width: 100%;
  height: 400px;
}

.map-content {
  width: 100%;
  height: 100%;
}

.route-panel {
  position: absolute;
  top: 20px;
  right: 20px;
  width: 250px;
  max-height: 360px;
  background: #fff;
  box-shadow: 0 2px 6px rgba(0,0,0,0.1);
  border-radius: 4px;
  overflow-y: auto;
  z-index: 999;
}

/* 优化高德地图控件样式 */
:deep(.amap-geo) {
  bottom: 20px !important;
  right: 20px !important;
}

:deep(.amap-driving-route-container) {
  padding: 12px;
  font-size: 14px;
}
</style>