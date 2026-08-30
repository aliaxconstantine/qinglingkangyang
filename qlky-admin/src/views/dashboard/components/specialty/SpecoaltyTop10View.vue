<template>
	<div class="ranking-panel">
		<div class="panel-title">实时特产目录</div>
		<ScrollBoard v-if="config.data.length" :config="config" class="ranking-board" />
		<div v-else class="empty-state">暂无实时特产数据</div>
	</div>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import { ScrollBoard } from '@kjgl77/datav-vue3';
import { getSpecialtyAll } from '/@/apis/qlky/tourist';

const config = ref({
	header: ['排名', '特产名称', '产地', '信息完整度'],
	columnWidth: [58, 180, 105, 100],
	rowNum: 6,
	headerBGC: '#0f5d8c',
	oddRowBGC: 'rgba(17, 72, 111, 0.34)',
	evenRowBGC: 'rgba(17, 72, 111, 0.16)',
	data: [],
});

onMounted(async () => {
	const response = await getSpecialtyAll();
	const rows = response?.data?.records || [];
	config.value.data = rows.slice(0, 10).map((row, index) => [
		index + 1,
		row.product_name || '-',
		row.origin_place || '-',
		String(row.description || '').trim() ? '完整' : '待补充',
	]);
});
</script>

<style scoped>
.ranking-panel { width: 100%; height: 100%; min-height: 280px; padding: 12px; box-sizing: border-box; }
.panel-title { color: #d7f2ff; text-align: center; font-weight: 600; margin-bottom: 10px; }
.ranking-board { width: 100%; height: calc(100% - 42px); }
.empty-state { color: #9bd9ed; text-align: center; padding-top: 40px; }
</style>
