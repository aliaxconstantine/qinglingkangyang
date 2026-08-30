<template>
	<transition name="fade">
	  <div 
		v-if="imageViewVisible"
		class="image-preview-mask"
		@click.self="handleClose"
	  >
		<div class="image-preview-container">
		  <!-- 图片容器 -->
		  <div 
			class="image-wrapper"
			:style="imageTransform"
			@wheel.prevent="handleWheel"
			@dblclick="resetZoom"
		  >
			<img 
			  :src="vmProps.imageUrl" 
			  class="preview-image"
			/>
		  </div>
  
		  <!-- 底部控制栏 -->
		  <div class="controls-container">
			<div class="control-buttons">
			  <button class="control-btn" @click="zoomOut">
				<el-icon><ZoomOut /></el-icon>
			  </button>
			  <div class="scale-display">{{ (scale * 100).toFixed(0) }}%</div>
			  <button class="control-btn" @click="zoomIn">
				<el-icon><ZoomIn /></el-icon>
			  </button>
			</div>
		  </div>
		</div>
	  </div>
	</transition>
  </template>
  
  <script lang="ts" setup>
  import { ref, watch, computed } from 'vue'
  import { ZoomIn, ZoomOut } from '@element-plus/icons-vue'
  
  const scale = ref(1)
  const offset = ref({ x: 0, y: 0 })
  
  const imageViewVisible = defineModel<boolean>()
  const vmProps = defineProps({
	imageUrl: {
	  type: String,
	  default: '',
	},
  })
  
  // 图片变换样式
  const imageTransform = computed(() => ({
	transform: `scale(${scale.value}) translate(${offset.value.x}px, ${offset.value.y}px)`,
	transition: 'transform 0.2s ease'
  }))
  
  // 鼠标滚轮事件
  const handleWheel = (e: WheelEvent) => {
	e.deltaY > 0 ? zoomOut() : zoomIn()
  }
  
  // 缩放控制
  const zoomOut = () => scale.value = Math.max(0.5, scale.value - 0.2)
  const zoomIn = () => scale.value = Math.min(4, scale.value + 0.2)
  const resetZoom = () => {
	scale.value = 1
	offset.value = { x: 0, y: 0 }
  }
  
  // 关闭处理
  const handleClose = () => {
	imageViewVisible.value = false
	resetZoom()
  }
  
  // 图片变化时重置
  watch(() => vmProps.imageUrl, () => resetZoom())
  </script>
  
  <style scoped>
  .fade-enter-active,
  .fade-leave-active {
	transition: opacity 0.3s ease;
  }
  
  .fade-enter-from,
  .fade-leave-to {
	opacity: 0;
  }
  
  .image-preview-mask {
	position: fixed;
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	background: rgba(0, 0, 0, 0.95);
	backdrop-filter: blur(8px);
	display: flex;
	justify-content: center;
	align-items: center;
	z-index: 2000;
  }
  
  .image-preview-container {
	width: 80vw;
	height: 80vh;
	max-width: 1440px;
	max-height: 90vh;
	display: flex;
	flex-direction: column;
	position: relative;
  }
  
  .image-wrapper {
	flex: 1;
	display: flex;
	align-items: center;
	justify-content: center;
	overflow: hidden;
	cursor: grab;
  }
  
  .preview-image {
	max-width: 100%;
	max-height: 100%;
	object-fit: contain;
	pointer-events: none;
  }
  
  .controls-container {
	padding: 20px;
	background: rgba(0, 0, 0, 0.4);
	backdrop-filter: blur(4px);
	border-radius: 8px;
	margin-top: 20px;
  }
  
  .control-buttons {
	display: flex;
	justify-content: center;
	align-items: center;
	gap: 30px;
  }
  
  .control-btn {
	width: 40px;
	height: 40px;
	border-radius: 50%;
	background: rgba(255, 255, 255, 0.1);
	border: 1px solid rgba(255, 255, 255, 0.2);
	color: white;
	cursor: pointer;
	transition: all 0.3s ease;
	display: flex;
	align-items: center;
	justify-content: center;
  
	&:hover {
	  background: rgba(255, 255, 255, 0.2);
	  transform: scale(1.1);
	}
  }
  
  .scale-display {
	color: rgba(255, 255, 255, 0.8);
	font-size: 16px;
	min-width: 60px;
	text-align: center;
	font-family: monospace;
  }
  </style>