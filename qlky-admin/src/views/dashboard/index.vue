<template>
	<div class="dashboard-shell">
		<BorderBox11 title="秦岭康养数据可视化" class="dashboard-frame">
			<nav class="dashboard-tabs" aria-label="数据大屏切换">
				<button
					v-for="page in pages"
					:key="page.name"
					:class="{ active: currentPage === page.name }"
					@click="switchPage(page.name)"
				>
					{{ page.label }}
				</button>
			</nav>
			<component :is="currentComponent" class="dashboard-content" />
		</BorderBox11>
	</div>
</template>

<script setup lang="ts">
import { computed, defineAsyncComponent, nextTick, ref } from 'vue';
import { BorderBox11 } from '@kjgl77/datav-vue3';

const pages = [
	{ name: 'tourism', label: '康养服务数据', comp: defineAsyncComponent(() => import('./components/touris/index.vue')) },
	{ name: 'specialty', label: '特产数据', comp: defineAsyncComponent(() => import('./components/specialty/index.vue')) },
	{ name: 'weather', label: '天气数据', comp: defineAsyncComponent(() => import('./components/weather/index.vue')) },
];

const currentPage = ref('tourism');
const currentComponent = computed(() => pages.find((page) => page.name === currentPage.value)?.comp);

function switchPage(name: string) {
	currentPage.value = name;
	nextTick(() => window.dispatchEvent(new Event('resize')));
}
</script>

<style scoped>
.dashboard-shell {
	position: fixed;
	inset: 0;
	overflow: hidden;
	background: #091c3d;
}

.dashboard-frame {
	width: 100%;
	height: 100%;
	position: relative;
	box-sizing: border-box;
	overflow: hidden;
}

.dashboard-tabs {
	position: absolute;
	top: 15px;
	right: 48px;
	z-index: 2;
	display: flex;
	gap: 8px;
}

.dashboard-tabs button {
	padding: 7px 14px;
	border: 1px solid rgba(83, 204, 255, 0.5);
	border-radius: 2px;
	background: rgba(6, 34, 72, 0.82);
	color: #d4f3ff;
	cursor: pointer;
}

.dashboard-tabs button.active {
	background: #0d86bb;
	border-color: #76e7ff;
	color: #fff;
}

.dashboard-content {
	position: absolute;
	top: 52px;
	left: 12px;
	max-width: min(100vw, calc(100% - 44px), 1560px);
	max-height: min(108vh, calc(100% - 75px), 820px);
	box-sizing: border-box;
	min-width: 0;
	min-height: 0;
	overflow: hidden;
}

@media (max-width: 700px) {
	.dashboard-tabs {
		right: 16px;
	}
	.dashboard-tabs button {
		padding: 6px 8px;
		font-size: 12px;
	}
	.dashboard-content {
		top: 58px;
		left: 16px;
		width: calc(100% - 32px);
		height: calc(100% - 74px);
	}
}
</style>
