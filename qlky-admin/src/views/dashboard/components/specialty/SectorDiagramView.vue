<template>
	<div class="sector-chart">
		<div class="chart-title">特产内容完整度</div>
		<div ref="chartEl" class="chart-canvas"></div>
	</div>
</template>

<script setup>
import { onBeforeUnmount, onMounted, ref } from 'vue';
import * as echarts from 'echarts';
import { getSpecialtyAll } from '/@/apis/qlky/tourist';

const chartEl = ref(null);
let chartInstance = null;

async function renderChart() {
	const response = await getSpecialtyAll();
	const rows = response?.data?.records || [];
	const described = rows.filter((row) => String(row.description || '').trim()).length;
	const data = [
		{ value: described, name: '含详情' },
		{ value: Math.max(rows.length - described, 0), name: '待补充' },
	];
	chartInstance?.dispose();
	chartInstance = echarts.init(chartEl.value);
	chartInstance.setOption({
		backgroundColor: 'transparent',
		tooltip: { trigger: 'item' },
		title: {
			text: String(rows.length),
			subtext: '实时特产',
			left: 'center',
			top: '42%',
			textStyle: { color: '#ffffff', fontSize: 24 },
			subtextStyle: { color: '#9bd9ed' },
		},
		legend: { bottom: 8, textStyle: { color: '#d7f2ff' } },
		series: [{
			type: 'pie',
			radius: ['48%', '68%'],
			center: ['50%', '48%'],
			label: { color: '#ffffff', formatter: '{b}: {c}' },
			data,
			color: ['#44d7b6', '#2b75c9'],
		}],
	});
}

function resizeChart() {
	chartInstance?.resize();
}

onMounted(() => {
	renderChart();
	window.addEventListener('resize', resizeChart);
});

onBeforeUnmount(() => {
	window.removeEventListener('resize', resizeChart);
	chartInstance?.dispose();
});
</script>

<style scoped>
.sector-chart { width: 100%; height: 100%; min-height: 280px; }
.chart-title { padding: 12px; color: #d7f2ff; font-weight: 600; text-align: center; }
.chart-canvas { width: 100%; height: calc(100% - 48px); }
</style>
