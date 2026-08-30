<template><div ref="chartEl" class="chart"></div></template>

<script setup>
import { onBeforeUnmount, onMounted, ref } from 'vue';
import * as echarts from 'echarts';
import { getTourismAll } from '/@/apis/qlky/tourist';

const chartEl = ref(null);
let chartInstance = null;

async function renderChart() {
	const response = await getTourismAll();
	const values = [0, 0, 0, 0, 0];
	(response?.data?.records || []).forEach((row) => {
		const score = Number(row.spot_rating) || 0;
		if (!score) values[0] += 1;
		else if (score < 3) values[1] += 1;
		else if (score < 4) values[2] += 1;
		else if (score < 4.5) values[3] += 1;
		else values[4] += 1;
	});
	chartInstance?.dispose();
	chartInstance = echarts.init(chartEl.value);
	chartInstance.setOption({
		backgroundColor: 'transparent',
		title: { text: '景点评分比例', left: 'center', top: 10, textStyle: { color: '#d7f2ff', fontSize: 15 } },
		tooltip: { trigger: 'item' },
		legend: { bottom: 6, type: 'scroll', textStyle: { color: '#d7f2ff' } },
		series: [{
			type: 'pie', radius: ['30%', '58%'], center: ['50%', '48%'], label: { color: '#ffffff', fontSize: 11 },
			data: ['暂无', '0-2.9', '3.0-3.9', '4.0-4.4', '4.5+'].map((name, index) => ({ name, value: values[index] })),
		}],
	});
}

function resizeChart() { chartInstance?.resize(); }
onMounted(() => { renderChart(); window.addEventListener('resize', resizeChart); });
onBeforeUnmount(() => { window.removeEventListener('resize', resizeChart); chartInstance?.dispose(); });
</script>

<style scoped>.chart { width: 100%; height: 100%; min-height: 220px; }</style>
