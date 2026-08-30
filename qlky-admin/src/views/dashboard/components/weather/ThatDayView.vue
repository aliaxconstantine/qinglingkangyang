<template>
	<div ref="chartEl" class="weather-chart-container"></div>
</template>

<script setup lang="ts">
import { onBeforeUnmount, onMounted, ref } from 'vue';
import * as echarts from 'echarts';
import { getLiveWeather } from '/@/apis/qlky/weather';

const chartEl = ref<HTMLElement | null>(null);
let chartInstance: echarts.ECharts | null = null;
let refreshTimer: number | null = null;

async function loadChart() {
	if (!chartEl.value) return;
	const { observed } = await getLiveWeather();
	chartInstance?.dispose();
	chartInstance = echarts.init(chartEl.value);
	chartInstance.setOption({
		backgroundColor: 'transparent',
		title: { text: '商洛 24 小时天气实况', left: 'center', textStyle: { color: '#d7f2ff', fontSize: 16, fontWeight: 600 } },
		tooltip: {
			trigger: 'axis',
			formatter: (params: any[]) => {
				const item = observed[params[0].dataIndex];
				return `${item.time}<br/>温度：${item.temperature}℃<br/>降水：${item.precipitation} mm<br/>湿度：${item.humidity}%<br/>风向：${item.windDirection} ${item.windPower}级`;
			},
		},
		legend: { bottom: 4, textStyle: { color: '#a8ccda' } },
		grid: { top: 58, right: 38, bottom: 48, left: 42 },
		xAxis: {
			type: 'category',
			data: observed.map((item) => item.time),
			axisLine: { lineStyle: { color: '#4b85a8' } },
			axisLabel: { color: '#a8ccda', interval: 2 },
		},
		yAxis: [
			{ type: 'value', name: '温度', axisLabel: { color: '#ff9b91', formatter: '{value}℃' }, nameTextStyle: { color: '#ff9b91' }, splitLine: { lineStyle: { color: 'rgba(104, 161, 194, .15)' } } },
			{ type: 'value', name: '湿度', min: 0, max: 100, axisLabel: { color: '#86d7ff', formatter: '{value}%' }, nameTextStyle: { color: '#86d7ff' }, splitLine: { show: false } },
		],
		series: [
			{ name: '温度', type: 'line', smooth: true, data: observed.map((item) => item.temperature), itemStyle: { color: '#ff796d' }, areaStyle: { color: 'rgba(255, 121, 109, .14)' } },
			{ name: '湿度', type: 'line', smooth: true, yAxisIndex: 1, data: observed.map((item) => item.humidity), itemStyle: { color: '#55c7f3' } },
		],
	});
}

function resizeChart() {
	chartInstance?.resize();
}

onMounted(async () => {
	try {
		await loadChart();
	} catch (error) {
		console.error('Failed to load live weather observations', error);
	}
	window.addEventListener('resize', resizeChart);
	refreshTimer = window.setInterval(loadChart, 10 * 60 * 1000);
});

onBeforeUnmount(() => {
	window.removeEventListener('resize', resizeChart);
	if (refreshTimer) window.clearInterval(refreshTimer);
	chartInstance?.dispose();
});
</script>

<style scoped>
.weather-chart-container {
	width: 100%;
	height: 100%;
	min-height: 300px;
	padding: 10px;
	box-sizing: border-box;
}
</style>
