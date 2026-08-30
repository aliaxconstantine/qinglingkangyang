<template>
	<div class="chart-container">
		<!-- 加载状态改造 -->
		<div v-if="loading" class="custom-loading">
			<BorderBox7 :dur="1.5" backgroundColor="rgba(25, 45, 77, 0.8)">
				<div class="loading-text">数据加载中...</div>
			</BorderBox7>
		</div>

		<template v-if="!loading">
			<div ref="chartEl" class="chart-content"></div>
		</template>
	</div>
</template>

<script setup>
import { ref, watch, onMounted, onUnmounted, computed, nextTick } from 'vue';
import { BorderBox7, Decoration7 } from '@kjgl77/datav-vue3';
import * as echarts from 'echarts';
import { ElMessage } from 'element-plus';
import { useUserInfo } from '/@/stores/userInfo';
import { getTourismAll } from '/@/apis/qlky/tourist';

// 响应式状态
const store = useUserInfo();
const chartEl = ref(null);
const loading = ref(false);
const ratingData = ref([]);
let chartInstance = null;

// 颜色配置
const colorPalette = ['#A0CE3A', '#31C5C0', '#1E9BD1', '#0F347B', '#585247', '#7F6AAD', '#009D85', 'rgba(250,250,250,0.3)'];

// 评分区间配置
const ratingCategories = [
	{ name: '评分为0分', range: [0, 0] },
	{ name: '评分为1-59分', range: [1, 59] },
	{ name: '评分为60-79分', range: [60, 79] },
	{ name: '评分为80-89分', range: [80, 89] },
	{ name: '评分为90-100分', range: [90, 100] },
];

// 计算属性
const currentCity = computed(() => store.userInfos?.city || 'shangluo');

// 数据获取
const fetchData = async () => {
	try {
		loading.value = true;
		const response = await getTourismAll();
		const counts = [0, 0, 0, 0, 0];
		(response?.data?.records || []).forEach((row) => {
			const rawScore = Number.parseFloat(String(row.spot_rating));
			const score = rawScore <= 5 ? rawScore * 20 : rawScore;
			if (!score) counts[0] += 1;
			else if (score < 60) counts[1] += 1;
			else if (score < 80) counts[2] += 1;
			else if (score < 90) counts[3] += 1;
			else counts[4] += 1;
		});
		ratingData.value = counts;
	} catch (error) {
		ElMessage.error(`数据错误: ${error.message}`);
	} finally {
		loading.value = false;
		await nextTick();
		initChart();
	}
};

// 图表初始化
const initChart = () => {
	if (!chartEl.value) return;

	// 清理旧实例
	if (chartInstance) {
		chartInstance.dispose();
	}

	// 创建实例
	chartInstance = echarts.init(chartEl.value);

	// 图表配置
	const option = {
		color: colorPalette,
		tooltip: {
			trigger: 'item',
			formatter: ({ name, value, percent }) => `
        ${name}<br/>
        数量: ${value}个<br/>
        占比: ${percent}%
      `,
		},
		legend: {
			orient: 'vertical',
			top: 'middle',
			right: '5%',
			textStyle: {
				color: '#f3f2f2',
				fontWeight: 900,
			},
			icon: 'roundRect',
			data: ratingCategories.map((item) => item.name),
		},
		series: [
			{
				name: '评分分布',
				type: 'pie',
				radius: [30, 55],
				center: ['35%', '50%'],
				roseType: 'area',
				label: {
					show: true,
					formatter: (item) => (item.value ? `${item.name}\n${item.value} 个` : ''),
					color: '#f3f2f2',
					fontWeight: 600,
					fontSize: 10,
					position: 'outside',
				},
				data: ratingCategories.map((category, index) => ({
					value: ratingData.value[index] || 0,
					name: category.name,
				})),
			},
		],
	};

	chartInstance.setOption(option);
};

// 监听城市变化
watch(currentCity, (newVal) => {
	fetchData();
});

// 生命周期管理
onMounted(() => {
	fetchData();
	window.addEventListener('resize', handleResize);
});

onUnmounted(() => {
	window.removeEventListener('resize', handleResize);
	chartInstance?.dispose();
});

// 响应式调整
const handleResize = () => {
	chartInstance?.resize();
};
</script>

<style scoped>
.chart-container {
	position: relative;
	width: 100%;
	height: 100%;
	min-height: 220px;
}

.chart-title {
	display: block;
	width: 100%;
	height: 14px;
	font-weight: 600;
	margin-bottom: 4px;
}

.chart-content {
	width: 100%;
	height: calc(100% - 22px);
	min-height: 180px;
	box-sizing: border-box;
	padding: 5px;
	background: rgba(23, 32, 58, 0.7);
	border-radius: 8px;
}
</style>
