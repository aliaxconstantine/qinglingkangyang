<template>
	<div class="captcha-container">
		<!-- 验证区：添加缺口覆盖层 -->
		<div class="canvas-wrapper">
			<canvas ref="canvas" :width="width" :height="height"></canvas>

			<!-- 新增可移动的滑块层 -->
			<div
				class="slider-overlay"
				:style="{
					left: overlayPos.x + 'px',
					top: overlayPos.y + 'px',
				}"
			></div>

			<!-- 目标缺口标记 -->
			<div class="target-marker" :style="targetStyle" :class="{ active: isDragging }"></div>
		</div>

		<!-- 滑动控制 -->
		<div class="slider-track">
			<div class="slider-progress" :style="{ width: sliderPos + 40 + 'px' }"></div>
			<div class="slider-handle" :style="{ left: sliderPos + 'px' }" @mousedown="startDrag" @touchstart.prevent="startDrag"></div>
		</div>

		<div class="status-indicator" :class="{ success: isVerified }">
			{{ statusText }}
		</div>
	</div>
</template>

<script setup lang="ts">
import { ref, computed, watch, defineEmits, onMounted } from 'vue';
const emits = defineEmits(['verifySuccess']);
const props = defineProps({
	width: { type: Number, default: 320 },
	height: { type: Number, default: 180 },
	tolerance: { type: Number, default: 8 },
});
const statusText = computed(() => (isVerified.value ? '验证成功 ✅' : '拖动滑块对齐缺口'));

const targetStyle = computed(() => ({
	left: `${target.value.x - HOLE_RADIUS}px`,
	top: `${target.value.y - HOLE_RADIUS}px`,
}));

//** 新增覆盖层坐标计算 **
const overlayPos = ref({ x: 0, y: props.height * 0.7 });
const sliderPos = ref(0);
const isDragging = ref(false);
const isMobile = ref(1.5);
const startX = ref(0);
const isVerified = ref(false);
const canvas = ref<HTMLCanvasElement | null>(null);
const target = ref({ x: 0, y: 0 });
const HOLE_RADIUS = 24;

//** 动态绑定覆盖层位置 **
watch(sliderPos, (newVal) => {
	overlayPos.value.x = newVal - HOLE_RADIUS;
});

// 初始化画布
const initCanvas = () => {
	const ctx = canvas.value?.getContext('2d');
	if (!ctx) return;

	// 生成随机缺口坐标
	target.value = {
		x: Math.random() * (props.width - HOLE_RADIUS * 2) + HOLE_RADIUS,
		y: props.height * 0.7,
	};

	overlayPos.value = {
		x: overlayPos.value.x,
		y: target.value.y - HOLE_RADIUS,
	};

	// 绘制渐变背景
	const gradient = ctx.createLinearGradient(0, 0, props.width, props.height);
	gradient.addColorStop(0, '#f0f3f5');
	gradient.addColorStop(1, '#e1e6eb');
	ctx.fillStyle = gradient;
	ctx.fillRect(0, 0, props.width, props.height);

	// 绘制缺口（网页7的合成模式应用）
	ctx.save();
	ctx.globalCompositeOperation = 'destination-out';
	ctx.beginPath();
	ctx.arc(target.value.x, target.value.y, HOLE_RADIUS, 0, Math.PI * 2);
	ctx.fill();
	ctx.restore();
};

//** 重写拖拽逻辑（网页2的事件处理优化）**
const startDrag = (e: MouseEvent | TouchEvent) => {
	if (isVerified.value) return;
	isDragging.value = true;
	startX.value = e instanceof TouchEvent ? e.touches[0].clientX : e.clientX;

	document.addEventListener('mousemove', drag);
	document.addEventListener('mouseup', endDrag);
	document.addEventListener('touchmove', drag);
	document.addEventListener('touchend', endDrag);
};

const drag = (e: MouseEvent | TouchEvent) => {
	if (!isDragging.value) return;

	const currentX = e instanceof TouchEvent ? e.touches[0].clientX : e.clientX;
	const maxPos = props.width - 40;

	sliderPos.value = Math.max(0, Math.min(currentX - startX.value, maxPos));
};

//** 验证逻辑优化**
const endDrag = () => {
	isDragging.value = false;
	document.removeEventListener('mousemove', drag);
	document.removeEventListener('mouseup', endDrag);
	document.removeEventListener('touchmove', drag);
	document.removeEventListener('touchend', endDrag);

	// 修正坐标偏移
	const overlayCenterX = overlayPos.value.x + HOLE_RADIUS; // 覆盖层中心X
	const overlayCenterY = overlayPos.value.y + HOLE_RADIUS; // 覆盖层中心Y
	const targetCenterX = target.value.x;
	const targetCenterY = target.value.y;

	// 分轴计算误差
	const xError = Math.abs(overlayCenterX - targetCenterX);
	const yError = Math.abs(overlayCenterY - targetCenterY);
	const maxError = Math.max(xError, yError); // 取最大轴误差

	// 动态容差控制
	const dynamicTolerance = props.tolerance * (isMobile.value ? 1.5 : 1);

	if (maxError <= dynamicTolerance) {
		// 双轴独立验证
		isVerified.value = true;
		setTimeout(() => {
			emits('verifySuccess');
		}, 1000);
	} else {
		handleVerificationFail();
	}
};

const handleVerificationFail = () => {
	sliderPos.value = 0;
	overlayPos.value.x = -HOLE_RADIUS * 2; // 视觉复位
	setTimeout(() => {
		initCanvas();
	}, 500);
};

onMounted(() => {
	initCanvas();
});

</script>

<style scoped>
.captcha-container {
	padding: 24px;
	background: white;
	border-radius: 12px;
	box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
	max-width: 360px;
}

.canvas-wrapper {
	position: relative;
	border-radius: 8px;
	overflow: hidden;
	background: #f8f9fa;
}

canvas {
	display: block;
	border-radius: 8px;
}

.target-marker {
	position: absolute;
	width: 48px;
	height: 48px;
	border: 2px dashed #4e73df;
	border-radius: 50%;
	pointer-events: none;
	animation: pulse 1.5s infinite;
	position: absolute;
	z-index: 1000;
}

.slider-track {
	height: 44px;
	background: #f1f3f5;
	border-radius: 22px;
	margin-top: 20px;
	position: relative;
}

.slider-handle {
	width: 44px;
	height: 44px;
	background: white;
	border-radius: 50%;
	position: absolute;
	top: -2px;
	cursor: grab;
	box-shadow: 0 4px 12px rgba(78, 115, 223, 0.2);
	transition: transform 0.2s, box-shadow 0.2s;
	z-index: 2;
}

.slider-handle:active {
	cursor: grabbing;
	transform: scale(1.1);
	box-shadow: 0 6px 16px rgba(78, 115, 223, 0.3);
}

.handle-core {
	width: 16px;
	height: 16px;
	background: #4e73df;
	border-radius: 50%;
	margin: auto;
}

.slider-progress {
	height: 100%;
	background: rgba(78, 115, 223, 0.1);
	border-radius: 22px;
	transition: width 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.status-indicator {
	text-align: center;
	margin-top: 16px;
	font-size: 14px;
	color: #6b7280;
	transition: all 0.3s;
}

.status-indicator.success {
	color: #10b981;
	font-weight: 500;
}

@keyframes pulse {
	0% {
		opacity: 0.9;
		transform: scale(1);
	}
	50% {
		opacity: 0.6;
		transform: scale(1.05);
	}
	100% {
		opacity: 0.9;
		transform: scale(1);
	}
}
.slider-overlay {
	position: absolute;
	width: 48px;
	height: 48px;
	background: rgba(78, 115, 223, 0.2);
	border: 2px solid #4e73df;
	border-radius: 50%;
	pointer-events: none;
	transition: left 0.15s ease-out;
	z-index: 999;
}

/* 目标缺口动效优化（网页3的动画参考） */
.target-marker.active {
	animation: pulse 0.8s infinite, highlight 1.2s ease-out;
}

@keyframes highlight {
	0% {
		box-shadow: 0 0 0 0 rgba(78, 115, 223, 0.3);
	}
	50% {
		box-shadow: 0 0 0 12px rgba(78, 115, 223, 0);
	}
	100% {
		box-shadow: 0 0 0 0 rgba(78, 115, 223, 0);
	}
}
</style>
