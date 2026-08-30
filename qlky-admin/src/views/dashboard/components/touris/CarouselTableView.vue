<template>
	<div class="top-attractions" style="color: white">
		<!-- 自动滚动容器 -->
		<div class="card-container" ref="scrollContainer" @mouseenter="stopScroll" @mouseleave="startScroll">
			<!-- 卡片列表 -->
			<div v-for="(item, index) in cardData" :key="index" class="attraction-card" :class="index % 2 === 0 ? 'even-card' : 'odd-card'">
				<div class="card-header">
					<span class="ranking-badge">TOP {{ index + 1 }}</span>
					<h3 class="attraction-name">{{ item.name }}</h3>
				</div>

				<div class="card-body">
					<el-image :src="item.image" class="card-image" fit="cover" />
					<div class="info-panel">
						<div class="info-item">
							<span class="label" style="margin-right: 10px">季节推荐</span>
							<el-tag type="warning" size="small">{{ item.season }}</el-tag>
						</div>
						<div class="info-item">
							<span class="label" style="margin-right: 10px">评分</span>
							<el-rate v-model="item.rating" disabled :colors="['#00f2fe', '#00f2fe', '#00f2fe']" />
						</div>
						<div class="price-tag">¥{{ item.price }}</div>
						<div class="info-item description">
							{{ item.desc }}
						</div>
					</div>
				</div>

				<div style="position: absolute; bottom: 10px; right: 10px">
					<el-button type="primary" link @click="showWordCloud(item)"> 词云分析 </el-button>
				</div>
			</div>
		</div>

		<!-- 词云弹窗 -->
		<el-dialog v-model="wordCloudVisible" title="词云分析" width="800">
			<!-- 添加 v-if 确保 DOM 已挂载 -->
			<div v-if="wordCloudVisible" ref="wordcloudEl" class="wordcloud-container"></div>
		</el-dialog>
	</div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue';
import * as echarts from 'echarts';
import 'echarts-wordcloud';
import { getTourismAll } from '/@/apis/qlky/tourist';
import { ElMessage } from 'element-plus';

// 卡片数据
const cardData = ref([]);

const fetchData = async () => {
	const response = await getTourismAll();
	const rows = response?.data?.records || [];
	cardData.value = rows.map((row) => ({
		name: row.spot_name,
		image: row.image_url,
		price: Number(row.ticket_price) || 0,
		season: row.best_season || 'All year',
		reviews: Number(row.review_count) || 0,
		rating: Number(row.spot_rating) || 0,
		detailUrl: row.detail_url,
		keywords: [row.spot_name, row.location, row.best_season, row.ranking].filter(Boolean),
	}));
};

// 自动滚动逻辑（优化版）
const scrollContainer = ref(null);
let scrollInterval = null;
const scrollStep = 2; // 调整滚动速度[7](@ref)

const startScroll = () => {
	const container = scrollContainer.value;
	if (!container) return;

	container.scrollTop = 0;
	const maxScroll = container.scrollHeight - container.clientHeight;

	scrollInterval = setInterval(() => {
		if (container.scrollTop >= maxScroll) {
			container.scrollTop = 0;
		} else {
			container.scrollTop += scrollStep;
		}
	}, 40);
};

const stopScroll = () => {
	clearInterval(scrollInterval);
};

// 词云功能（增强配置）
const wordCloudVisible = ref(false);
const wordcloudEl = ref(null);
const chartInstance = ref(null);
const showWordCloud = async (item) => {
	try {
		// 数据校验
		if (!item?.keywords?.filter(Boolean).length) {
			throw new Error('关键词数据为空');
		}

		wordCloudVisible.value = true;

		// 等待 DOM 完全渲染
		await nextTick();
		await nextTick();

		// 容器存在性检查
		if (!wordcloudEl.value) {
			throw new Error('容器元素未找到');
		}

		// 尺寸强制设置
		Object.assign(wordcloudEl.value.style, {
			width: '100%',
			height: '500px',
			minHeight: '500px',
			background: '#fff',
		});

		// 销毁旧实例
		if (chartInstance.value) {
			echarts.dispose(chartInstance.value);
		}

		// 初始化实例
		chartInstance.value = echarts.init(wordcloudEl.value, null, {
			renderer: 'canvas',
			useDirtyRect: false,
		});

		// 设置配置项
		chartInstance.value.setOption(getWordCloudOption(item));

		// 窗口 resize 监听
		const resizeHandler = () => chartInstance.value.resize();
		window.addEventListener('resize', resizeHandler);

		// 清理监听
		onBeforeUnmount(() => {
			window.removeEventListener('resize', resizeHandler);
		});
	} catch (error) {
		ElMessage.error(`词云渲染失败: ${error.message}`);
		wordCloudVisible.value = false;
	}
};

// 单独提取配置生成函数
const getWordCloudOption = (item) => ({
	backgroundColor: {
		type: 'linear',
		x: 0,
		y: 0,
		x2: 1,
		y2: 1,
		colorStops: [
			{ offset: 0, color: '#00152a' },
			{ offset: 1, color: '#000a1a' },
		],
	},
	series: [
		{
			type: 'wordCloud',
			shape: 'circle',
			sizeRange: [20, 80],
			gridSize: 8,
			textStyle: {
				color: () => ['#00f2fe', '#00ff87', '#4e7fff', '#20c997', '#ff6b6b'][Math.floor(Math.random() * ['#00f2fe', '#00ff87', '#4e7fff', '#20c997', '#ff6b6b'].length)],
				shadowBlur: 10,
				shadowColor: 'rgba(0, 242, 254, 0.5)',
			},
			data:item.keywords.map((word) => ({
				name: word,
				value: Math.floor(Math.random() * 100) + 50,
			})),
		},
	],
});
// 生命周期
onMounted(async () => {
	await fetchData();
	startScroll();
	window.addEventListener('resize', startScroll);
});

onBeforeUnmount(() => {
	stopScroll();
	window.removeEventListener('resize', startScroll);
	chartInstance.value?.dispose();
});
</script>

<style scoped>
/* 基础容器 */
.top-attractions {
	width: 100%;
	height: 100%; /* 视窗高度自适应 */
	margin: 0 auto;
	position: relative;
}

.card-container {
	height: calc(100% - 40px);
	overflow-y: hidden;
	padding: 8px 0;
	scroll-behavior: smooth; /* 平滑滚动[6](@ref) */
}

/* 卡片固定高度布局 */
.attraction-card {
	width: 95%;
	height: 160px !important; /* 强制固定高度 */
	margin: 8px auto;
	padding: 12px;
	background: rgba(16, 46, 89, 0.8);
	border-radius: 6px;
	display: grid;
	grid-template-rows: auto 1fr auto;
	transition: transform 0.3s ease;
	box-sizing: border-box;
}

.card-body {
	display: grid;
	grid-template-columns: 120px 1fr;
	gap: 12px;
	height: 100px; /* 固定内容区域高度 */
}

.card-image {
	width: 100%;
	height: 80px !important; /* 强制图片高度 */
	border-radius: 4px;
	object-fit: cover;
}

/* 信息面板优化 */
.info-panel {
	display: flex;
	flex-direction: column;
	justify-content: space-between;
	height: 100%;
}

.info-item {
	margin: 2px 0;

	&.description {
		font-size: 12px;
		overflow: hidden;
		display: -webkit-box;
		-webkit-box-orient: vertical;
	}
}

.price-tag {
	font-size: 18px;
	color: #00f2fe;
	margin: 4px 0;
}

/* 响应式调整 */
@media (max-width: 768px) {
	.card-body {
		grid-template-columns: 100px 1fr;
	}

	.card-image {
		height: 60px !important;
	}

	.attraction-card {
		height: 130px !important;
		padding: 8px;
	}
}

/* 交互优化 */
.attraction-card:hover {
	transform: translateY(-3px);
	box-shadow: 0 4px 15px rgba(0, 242, 254, 0.25);
	z-index: 1;
}

.even-card {
	background: rgba(16, 46, 89, 0.6);
}
.odd-card {
	background: rgba(16, 46, 89, 0.8);
}

/* 深度样式覆盖 */
:deep(.el-image__inner) {
	border: none !important;
	box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
}

:deep(.el-button) {
	padding: 6px 10px;
	font-size: 12px;

	&::after {
		border: none !important;
	}
}

/* 添加科技感边框动画 */
.wordcloud-container {
	position: relative;
	overflow: hidden;

	&::after {
		content: '';
		position: absolute;
		top: -50%;
		left: -50%;
		width: 200%;
		height: 200%;
		background: linear-gradient(45deg, transparent 25%, rgba(0, 242, 254, 0.2) 50%, transparent 75%);
		animation: techFlow 8s linear infinite;
	}
}

@keyframes techFlow {
	0% {
		transform: rotate(0deg) translate(-50%, -50%);
	}
	100% {
		transform: rotate(360deg) translate(-50%, -50%);
	}
}

/* 修复 Element UI 弹窗层级 */
.el-dialog {
	z-index: 10000 !important;
}
</style>
