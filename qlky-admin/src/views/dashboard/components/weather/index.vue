<template>
	<div class="weather-dashboard">
		<section class="weather-panel current-weather"><ThatDayView /></section>
		<section class="weather-panel forecast-weather"><SevenView /></section>
	</div>
</template>

<script setup>
import ThatDayView from './ThatDayView.vue';
import SevenView from './SevenView.vue';
</script>

<style scoped>
.weather-dashboard {
	display: grid;
	grid-template-columns: minmax(0, 1.3fr) minmax(320px, 0.7fr);
	gap: 16px;
	width: min(1500px, calc(100vw - 88px));
	height: calc(100vh - 110px);
	padding: 16px;
	box-sizing: border-box;
}

.weather-panel {
	min-width: 0;
	min-height: 0;
	overflow: hidden;
	background: rgba(12, 31, 67, 0.72);
	border: 1px solid rgba(72, 196, 255, 0.38);
}

.current-weather :deep(.weather-chart-container) {
	width: 100%;
	height: 100%;
	box-sizing: border-box;
}

.forecast-weather :deep(.weather-container) {
	max-width: none;
	height: 100%;
	margin: 0;
	border-radius: 0;
	box-sizing: border-box;
}

@media (max-width: 900px) {
	.weather-dashboard {
		grid-template-columns: 1fr;
		overflow-y: auto;
		width: 100%;
	}
}
</style>
