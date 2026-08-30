<template>
	<div class="specialty-list">
		<div class="panel-title">特产实时详情</div>
		<div ref="cardsRef" class="cards" @mouseenter="pause" @mouseleave="resume">
			<article v-for="(item, index) in rows" :key="item.group_id || index" class="specialty-card">
				<span class="rank">{{ index + 1 }}</span>
				<el-image :src="item.image_url" fit="cover" class="product-image" />
				<div class="product-info">
					<strong>{{ item.product_name }}</strong>
					<span>{{ item.product_type || '秦岭特产' }} · {{ item.origin_place || '秦岭地区' }}</span>
					<p>{{ summarize(item.description) }}</p>
				</div>
				<a v-if="item.detail_address" :href="item.detail_address" target="_blank" rel="noreferrer">详情</a>
			</article>
		</div>
	</div>
</template>

<script setup>
import { nextTick, onBeforeUnmount, onMounted, ref } from 'vue';
import { getSpecialtyAll } from '/@/apis/qlky/tourist';

const rows = ref([]);
const cardsRef = ref(null);
let refreshTimer = null;
let scrollTimer = null;
let scrollDirection = 1;

function summarize(value) {
	const text = String(value || '暂无详情');
	return text.length > 70 ? `${text.slice(0, 70)}...` : text;
}

async function load() {
	const response = await getSpecialtyAll();
	rows.value = response?.data?.records || [];
	await nextTick();
	startAutoScroll();
}

function stopAutoScroll() {
	clearInterval(scrollTimer);
	scrollTimer = null;
}

function startAutoScroll() {
	stopAutoScroll();
	const cards = cardsRef.value;
	if (!cards || cards.scrollHeight <= cards.clientHeight) return;

	scrollTimer = setInterval(() => {
		const maxScrollTop = cards.scrollHeight - cards.clientHeight;
		if (cards.scrollTop >= maxScrollTop) scrollDirection = -1;
		if (cards.scrollTop <= 0) scrollDirection = 1;
		cards.scrollTop += scrollDirection;
	}, 35);
}

function pause() {
	clearInterval(refreshTimer);
	refreshTimer = null;
	stopAutoScroll();
}

function resume() {
	startAutoScroll();
	if (!refreshTimer) refreshTimer = setInterval(load, 120000);
}

onMounted(async () => { await load(); resume(); });
onBeforeUnmount(pause);
</script>

<style scoped>
.specialty-list { width: 100%; height: 100%; padding: 12px; box-sizing: border-box; color: #eefaff; }
.panel-title { text-align: center; color: #d7f2ff; font-weight: 600; margin-bottom: 10px; }
.cards { height: calc(100% - 40px); overflow-x: hidden; overflow-y: auto; display: grid; gap: 8px; scrollbar-width: none; -ms-overflow-style: none; }
.cards::-webkit-scrollbar { display: none; }
.specialty-card { display: grid; grid-template-columns: 30px 70px minmax(0, 1fr) auto; gap: 10px; align-items: center; min-height: 82px; padding: 8px; background: rgba(30, 96, 142, 0.24); border-left: 2px solid #3bc6e7; }
.rank { color: #66dcff; font-weight: 700; text-align: center; }
.product-image { width: 70px; height: 60px; }
.product-info { min-width: 0; display: grid; gap: 4px; }
.product-info strong, .product-info p { overflow: hidden; text-overflow: ellipsis; white-space: nowrap; margin: 0; }
.product-info span, .product-info p { color: #a8ccda; font-size: 12px; }
.specialty-card a { color: #75dfff; font-size: 12px; }
</style>
