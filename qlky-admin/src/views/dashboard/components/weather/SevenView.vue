<template>
	<div class="weather-container">
		<header>
			<h3>商洛未来天气</h3>
			<span>{{ updatedAt }}</span>
		</header>
		<div v-if="loading" class="state">正在获取实时预报...</div>
		<div v-else-if="error" class="state error">{{ error }}</div>
		<div v-else class="weather-list">
			<article v-for="day in forecastData" :key="day.date" class="weather-day">
				<div class="date">{{ day.date }}</div>
				<div class="condition">{{ day.weather }}</div>
				<div class="temperature">{{ day.tempHigh }}<small>℃</small><span>/</span>{{ day.tempLow }}<small>℃</small></div>
				<div class="wind">{{ day.windDirection }} {{ day.windPower }}</div>
			</article>
		</div>
	</div>
</template>

<script setup lang="ts">
import { onBeforeUnmount, onMounted, ref } from 'vue';
import { getLiveWeather } from '/@/apis/qlky/weather';

const loading = ref(true);
const error = ref('');
const updatedAt = ref('');
const forecastData = ref<any[]>([]);
let refreshTimer: number | null = null;

async function loadWeather() {
	loading.value = true;
	error.value = '';
	try {
		forecastData.value = (await getLiveWeather()).forecast;
		updatedAt.value = `更新于 ${new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })}`;
	} catch {
		error.value = '天气服务暂时不可用';
	} finally {
		loading.value = false;
	}
}

onMounted(async () => {
	await loadWeather();
	refreshTimer = window.setInterval(loadWeather, 10 * 60 * 1000);
});

onBeforeUnmount(() => {
	if (refreshTimer) window.clearInterval(refreshTimer);
});
</script>

<style scoped>
.weather-container { width: 100%; height: 100%; padding: 16px; box-sizing: border-box; color: #eefaff; }
header { display: flex; justify-content: space-between; align-items: baseline; border-bottom: 1px solid rgba(113, 207, 244, .28); padding-bottom: 10px; }
h3 { margin: 0; font-size: 16px; font-weight: 600; }
header span { color: #89b7ca; font-size: 12px; }
.weather-list { display: grid; gap: 8px; padding-top: 12px; }
.weather-day { display: grid; grid-template-columns: 62px minmax(70px, 1fr) auto minmax(96px, 1fr); align-items: center; gap: 8px; padding: 11px 8px; background: rgba(27, 93, 137, .2); border-left: 2px solid #40c6e7; }
.date { color: #7fe3ff; font-weight: 600; }
.condition { color: #dceff7; }
.temperature { color: #ffb44d; font-size: 18px; font-weight: 600; white-space: nowrap; }
.temperature small { font-size: 11px; font-weight: 400; }
.temperature span { padding: 0 3px; color: #9cb9c8; }
.wind { color: #a8ccda; font-size: 12px; text-align: right; }
.state { display: grid; place-items: center; height: calc(100% - 50px); color: #a8ccda; }
.state.error { color: #ff9d92; }
@media (max-width: 600px) { .weather-day { grid-template-columns: 58px 1fr auto; } .wind { display: none; } }
</style>
