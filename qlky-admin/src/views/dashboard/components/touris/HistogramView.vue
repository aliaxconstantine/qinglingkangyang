<template>
	<div class="chart-container">
		<dv-loading v-if="loading">加载中...</dv-loading>
		<div ref="chartEl" class="chart-wrapper" :style="chartStyle"></div>
	</div>
</template>

<script setup>
import { ref, watch, onMounted, onUnmounted, computed } from 'vue';
import * as echarts from 'echarts';
import { ElMessage } from 'element-plus';
import { getTourismAll } from '/@/apis/qlky/tourist';
import { useUserInfo } from '/@/stores/userInfo';

// 响应式状态
const store = useUserInfo();
const chartEl = ref(null);
const loading = ref(false);
const chartData = ref([]);
let chartInstance = null;

// 新增响应式样式计算
const chartStyle = computed(() => ({
	width: 'calc(100% - 20px)', // 左右各留10px
	height: 'calc(100% - 20px)', // 上下各留10px
	padding: '10px',
}));

// 城市区县配置
const cityCountyMap = {
	shangluo: ['商州区', '洛南县', '丹凤县', '商南县', '山阳县', '镇安县', '柞水县'],
	hanzhong: ['汉台区', '勉县', '城固县', '洋县', '西乡县', '佛坪县', '留坝县', '镇巴县', '宁强县', '南郑区'],
	ankang: ['宁陕县', '石泉县', '汉阴县', '汉滨区', '紫阳县', '平利县', '镇坪县', '白河县', '旬阳市', '岚皋县'],
	baoji: ['渭滨区', '金台区', '陈仓区', '凤翔区', '岐山县', '扶风县', '眉县', '陇县', '千阳县', '麟游县', '凤县', '太白县'],
	weinan: ['临渭区', '华州区', '潼关县', '大荔县', '合阳县', '澄城县', '蒲城县', '白水县', '富平县', '韩城市', '华阴市'],
	xian: ['新城区', '碑林区', '莲湖区', '灞桥区', '未央区', '雁塔区', '阎良区', '临潼区', '长安区', '高陵区', '鄠邑区', '蓝田县', '周至县'],
};

// 计算属性
const currentCity = computed(() => store.userInfos?.city || 'shangluo');

// 数据获取
const fetchData = async (city) => {
	loading.value = true;
	const response = await getTourismAll();
	const rows = (response?.data?.records || []).slice(0, 10);
	const series = [
		rows.map(() => 1),
		rows.map((row) => Number(row.review_count) || 0),
		rows.map((row) => Number(row.strategy_count) || Number(row.ranking) || 0),
	];
	series.names = rows.map((row) => row.spot_name || row.location || '-');
	chartData.value = series;
	loading.value = false;
	initChart(city);
};

// 图表初始化
const initChart = (city) => {
	if (!chartEl.value) return;

	// 清理旧实例
	if (chartInstance) {
		chartInstance.dispose();
	}

	// 创建实例
	chartInstance = echarts.init(chartEl.value);

	// 图表配置
	const option = {
		backgroundColor: 'rgba(23, 32, 58, 0.7)',
		title: {
			text: '数量/个',
			textStyle: {
				fontSize: '1.3vw',
				fontWeight: 900,
				color: '#8fd5f3',
			},
			top: '6%',
			left: '3.5%',
		},
		tooltip: {
			trigger: 'axis',
			axisPointer: { type: 'shadow' },
		},
		legend: {
			show: true,
			orient: 'horizontal', // 明确指定水平布局
			top: '8%', // 增加顶部间距
			right: '3%', // 增加右侧间距
			itemGap: 15, // 使用固定像素间距
			itemWidth: 25, // 固定图例标记宽度
			itemHeight: 14, // 固定图例标记高度
			textStyle: {
				color: '#fff',
				fontSize: 12, // 固定字号代替 vw
				fontWeight: 600,
				padding: [0, 0, 0, 5], // 文字左边距
			},
			// 添加滚动功能（当图例过多时）
			type: 'scroll',
			pageIconColor: '#00f2fe',
			pageTextStyle: {
				color: 'rgba(255,255,255,0.8)',
			},
		},
		grid: {
			top: '15%',
			left: '8%',
			right: '8%',
			bottom: '15%',
			containLabel: true,
		},
		xAxis: {
			type: 'category',
			data: chartData.value.names || cityCountyMap[city] || [],
			axisLine: { lineStyle: { color: '#394458' } },
			axisLabel: {
				interval: 0,
				margin: 10,
				color: '#05D5FF',
				fontSize: '1.1vw',
				fontWeight: 900,
			},
		},
		yAxis: {
			type: 'value',
			axisLabel: {
				color: 'rgba(95, 187, 235, 1)',
				fontSize: '1.1vw',
				fontWeight: 900,
			},
			axisLine: { lineStyle: { color: '#394458' } },
			splitLine: { lineStyle: { color: '#394458' } },
		},
		series: [
			createBarSeries('产品数', chartData.value[0], 'rgba(255, 204, 0,0.3)', '#ffcc00'),
			createBarSeries('点评数', chartData.value[1], 'rgba(0, 255, 255, 0.3)', '#00ffff'),
			createBarSeries('攻略数', chartData.value[2], 'rgba(255, 0, 119, 0.3)', '#ff00bf'),
		],
	};

	chartInstance.setOption(option);
};

// 创建柱状图系列配置
const createBarSeries = (name, data, color, borderColor) => ({
	name,
	type: 'bar',
	barWidth: '13',
	data,
	itemStyle: {
		color,
		borderColor,
		borderWidth: 1,
	},
});

// 监听城市变化
watch(currentCity, (newVal) => {
	fetchData(newVal);
});

// 自适应处理
const handleResize = () => {
	if (chartEl.value?.parentElement) {
		chartInstance?.resize({
			width: chartEl.value.parentElement.offsetWidth - 20,
			height: chartEl.value.parentElement.offsetHeight - 20,
		});
	}
};

// 生命周期
onMounted(() => {
	fetchData(currentCity.value);
	window.addEventListener('resize', handleResize);
	setTimeout(handleResize, 100); // 延迟确保容器尺寸计算准确
});

onUnmounted(() => {
	window.removeEventListener('resize', handleResize);
	chartInstance?.dispose();
});
</script>

<style scoped>
/* 容器样式 */
.chart-container {
	position: relative;
	width: 100%;
	height: 100%;
	padding: 10px;
	background: rgba(23, 32, 58, 0.7);
	border-radius: 8px;
	box-sizing: border-box;
}

.chart-wrapper {
	width: 100%;
	height: 100%;
	min-width: 300px;
	min-height: 200px;
}

/* 响应式调整 */
@media (max-width: 768px) {
	:deep(.echarts-legend) {
		top: '8%' !important;
	}

	:deep(.echarts-grid) {
		left: '10%' !important;
		right: '10%' !important;
	}
}
</style>
