<template><div ref="chartEl" class="chart"></div></template>

<script setup>
import { onBeforeUnmount, onMounted, ref } from 'vue';
import * as echarts from 'echarts';
import { getTourismAll } from '/@/apis/qlky/tourist';

const chartEl = ref(null);
let chartInstance = null;

async function renderChart() {
	const response = await getTourismAll();
	const values = [0, 0, 0];
	(response?.data?.records || []).forEach((row) => {
		const count = Number(row.review_count) || 0;
		if (!count) values[0] += 1;
		else if (count <= 100) values[1] += 1;
		else values[2] += 1;
	});
	chartInstance?.dispose();
	chartInstance = echarts.init(chartEl.value);
	chartInstance.setOption({
		backgroundColor: 'transparent',
		title: { text: '点评热度比例', left: 'center', top: 10, textStyle: { color: '#d7f2ff', fontSize: 15 } },
		tooltip: { trigger: 'item' },
		legend: { bottom: 6, textStyle: { color: '#d7f2ff' } },
		series: [{
			type: 'pie', roseType: 'area', radius: [26, 54], center: ['50%', '48%'], label: { color: '#ffffff', fontSize: 11 },
			data: ['无点评', '1-100条', '100条以上'].map((name, index) => ({ name, value: values[index] })),
			color: ['#4e75a3', '#34c6ba', '#f3a743'],
		}],
	});
}

function resizeChart() { chartInstance?.resize(); }
onMounted(() => { renderChart(); window.addEventListener('resize', resizeChart); });
onBeforeUnmount(() => { window.removeEventListener('resize', resizeChart); chartInstance?.dispose(); });
</script>

<style scoped>.chart { width: 100%; height: 100%; min-height: 220px; }</style>
