<template>
	<div class="chart-switch-container">
		<div class="chart-header">
			<dv-decoration-7 class="title-decoration">
				<el-dropdown @command="handleChartSwitch">
					<h3 class="title-text">{{ activeChartTitle }}</h3>
					<template #dropdown>
						<el-dropdown-menu>
							<el-dropdown-item command="visitRate">康养体验到访率TOP10康养项目</el-dropdown-item>
							<el-dropdown-item command="price">价格TOP10康养项目</el-dropdown-item>
						</el-dropdown-menu>
					</template>
				</el-dropdown>
			</dv-decoration-7>
		</div>

		<div class="chart-content">
			<div v-show="activeChart === 'visitRate'" ref="visitRateChart" class="chart-box"></div>
			<div v-show="activeChart === 'price'" ref="priceChart" class="chart-box"></div>
		</div>
	</div>
</template>

<script setup>
import { ref, watch, onMounted, onUnmounted, computed, nextTick } from 'vue';
import * as echarts from 'echarts';
import { ElMessage } from 'element-plus';
import { getTourismAll } from '/@/apis/qlky/tourist';
import { useUserInfo } from '/@/stores/userInfo';

// 响应式状态
const store = useUserInfo();
const loading = ref(false);
const activeChart = ref('visitRate');
const visitRateData = ref({ names: [], rates: [] });
const priceData = ref({ names: [], prices: [] });

// 图表实例
const visitRateChart = ref(null);
const priceChart = ref(null);
let visitRateChartInstance = null;
let priceChartInstance = null;

// 计算属性
const currentCity = computed(() => store.userInfos?.city || 'shangluo');
const activeChartTitle = computed(() => (activeChart.value === 'visitRate' ? '康养体验到访率TOP10康养项目' : '价格TOP10康养项目'));

// 数据获取
const fetchVisitRateData = async (city) => {
	try {
		loading.value = true;
		const response = await getTourismAll();
		const rows = [...(response?.data?.records || [])]
			.sort((left, right) => Number(right.visitor_rate || right.spot_rating) - Number(left.visitor_rate || left.spot_rating))
			.slice(0, 10);
		visitRateData.value = {
			names: rows.map((row) => row.spot_name || '-'),
			rates: rows.map((row) => Number(row.visitor_rate) || (Number(row.spot_rating) || 0) * 20),
		};
		await nextTick();
		initVisitRateChart();
	} finally {
		loading.value = false;
	}
};

const fetchPriceData = async (city) => {
	try {
		loading.value = true;
		const response = await getTourismAll();
		const rows = [...(response?.data?.records || [])]
			.sort((left, right) => Number(right.ticket_price) - Number(left.ticket_price))
			.slice(0, 10);
		priceData.value = {
			names: rows.map((row) => row.spot_name || '-'),
			prices: rows.map((row) => Number(row.ticket_price) || 0),
		};
		await nextTick();
		initPriceChart();
	} catch (error) {
		ElMessage.error(error.message);
	} finally {
		loading.value = false;
	}
};

// 图表配置生成器
const createBaseOptions = (title, yName) => ({
	backgroundColor: 'rgba(23, 32, 58, 0.7)',
	title: {
		text: title,
		left: 'center',
		textStyle: {
			color: '#ffffff',
			fontSize: '1.2em',
		},
	},
	grid: {
		top: '22%',
		left: '5%',
		right: '5%',
		bottom: '15%',
		containLabel: true,
	},
	tooltip: {
		trigger: 'axis',
		axisPointer: { type: 'shadow' },
	},
	xAxis: {
		type: 'category',
		axisLabel: {
			color: '#ffffff',
			fontSize: '0.9em',
			formatter: (val) => (val.length > 4 ? `${val.slice(0, 4)}...` : val),
		},
	},
	yAxis: {
		type: 'value',
		name: yName,
		nameTextStyle: {
			color: '#ffffff',
			fontSize: '1em',
		},
		axisLabel: {
			color: '#ffffff',
			fontSize: '0.9em',
		},
	},
});

// 图表初始化
const initVisitRateChart = () => {
	if (!visitRateChart.value) return;

	visitRateChartInstance?.dispose();
	visitRateChartInstance = echarts.init(visitRateChart.value);

	const options = {
		...createBaseOptions('康养体验到访率TOP10康养项目', '百分比/%'),
		xAxis: { data: visitRateData.value.names },
		series: [
			{
				type: 'line',
				smooth: true,
				data: visitRateData.value.rates,
				areaStyle: { color: createGradient('#71bff3') },
				lineStyle: { color: '#22a9dd' },
			},
		],
	};

	visitRateChartInstance.setOption(options);
};

const initPriceChart = () => {
	if (!priceChart.value) return;

	priceChartInstance?.dispose();
	priceChartInstance = echarts.init(priceChart.value);

	const options = {
		...createBaseOptions('价格TOP10康养项目', '价格/元'),
		xAxis: { data: priceData.value.names },
		series: [
			{
				type: 'line',
				smooth: true,
				data: priceData.value.prices,
				areaStyle: { color: createGradient('#71bff3') },
				lineStyle: { color: '#22a9dd' },
			},
		],
	};

	priceChartInstance.setOption(options);
};

// 工具函数
const createGradient = (color) =>
	new echarts.graphic.LinearGradient(0, 0, 0, 1, [
		{ offset: 0, color },
		{ offset: 1, color: 'transparent' },
	]);

// 事件处理
const handleChartSwitch = (command) => {
	activeChart.value = command;
	if (command === 'visitRate') fetchVisitRateData(currentCity.value);
	if (command === 'price') fetchPriceData(currentCity.value);
};

// 响应式处理
const handleResize = () => {
	visitRateChartInstance?.resize();
	priceChartInstance?.resize();
};

// 生命周期
onMounted(() => {
	fetchVisitRateData(currentCity.value);
	window.addEventListener('resize', handleResize);
});

onUnmounted(() => {
	window.removeEventListener('resize', handleResize);
	visitRateChartInstance?.dispose();
	priceChartInstance?.dispose();
});

// 监听城市变化
watch(currentCity, (newVal) => {
	if (activeChart.value === 'visitRate') fetchVisitRateData(newVal);
	else fetchPriceData(newVal);
});
</script>

<style scoped>
.chart-switch-container {
	position: relative;
	width: 100%;
	height: 100%;
	min-width: 300px;
	min-height: 280px;
	box-sizing: border-box;
}

.chart-header {
	position: absolute;
	top: -8%;
	left: 50%;
	transform: translateX(-50%);
	width: 80%;
	z-index: 1;
}

.title-decoration {
	width: 100%;
	height: 0.5em;
}

.title-text {
	color: white;
	margin: 0;
	padding: 0 5%;
	font-size: 1.2em;
	cursor: pointer;
	transition: opacity 0.3s;
	white-space: nowrap;
	text-align: center;
}

.title-text:hover {
	opacity: 0.8;
}

.chart-content {
	width: 100%;
	height: calc(100% - 10%);
	margin-top: 8%;
	position: relative;
}

.chart-box {
	width: 100%;
	height: 100%;
}

@media (max-width: 768px) {
	.title-text {
		font-size: 1em;
	}

	.chart-content {
		height: calc(100% - 15%);
		margin-top: 12%;
	}

	:deep(.echarts-axis-label) {
		font-size: 0.8em !important;
	}
}

:deep(.el-dropdown-menu) {
	min-width: 180px !important;
}
</style>
