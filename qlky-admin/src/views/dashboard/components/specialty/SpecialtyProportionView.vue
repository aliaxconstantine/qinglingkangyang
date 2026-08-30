<template><div ref="chartEl" class="chart"></div></template>

<script setup>
import { onBeforeUnmount, onMounted, ref } from 'vue';
import * as echarts from 'echarts';
import { getSpecialtyAll } from '/@/apis/qlky/tourist';

const chartEl = ref(null);
let chartInstance = null;

async function renderChart() {
	const response = await getSpecialtyAll();
	const counts = new Map();
	(response?.data?.records || []).forEach((row) => {
		const type = row.product_type || '未分类';
		counts.set(type, (counts.get(type) || 0) + 1);
	});
	const data = [...counts.entries()].map(([name, value]) => ({ name, value }));
	chartInstance?.dispose();
	chartInstance = echarts.init(chartEl.value);
	chartInstance.setOption({
		backgroundColor: 'transparent',
		title: { text: '特产分类占比', left: 'center', top: 10, textStyle: { color: '#d7f2ff', fontSize: 15 } },
		tooltip: { trigger: 'item' },
		legend: { bottom: 8, type: 'scroll', textStyle: { color: '#d7f2ff' } },
		series: [{ type: 'pie', radius: ['35%', '62%'], center: ['50%', '48%'], label: { color: '#e9fbff' }, data }],
	});
}

function resizeChart() { chartInstance?.resize(); }
onMounted(() => { renderChart(); window.addEventListener('resize', resizeChart); });
onBeforeUnmount(() => { window.removeEventListener('resize', resizeChart); chartInstance?.dispose(); });
</script>

<style scoped>.chart { width: 100%; height: 100%; min-height: 280px; }</style>
