<template>
	<div class="tourism-dashboard">
		<div class="dashboard-row tourism-overview">
			<section class="panel compact"><PieView /></section>
			<section class="panel histogram"><HistogramView /></section>
			<section class="panel compact"><PercentageOfRatingsView /></section>
			<section class="panel compact"><RatingRatioChartView /></section>
		</div>
		<div class="dashboard-row tourism-detail">
			<section class="panel attraction-list"><CarouselTableView /></section>
			<section class="panel visits"><VisitedRatesView /></section>
		</div>
	</div>
</template>

<script setup>
import CarouselTableView from './CarouselTableView.vue';
import HistogramView from './HistogramView.vue';
import PercentageOfRatingsView from './PercentageOfRatingsView.vue';
import PieView from './PieView.vue';
import RatingRatioChartView from './RatingRatioChartView.vue';
import VisitedRatesView from './VisitedRatesView.vue';
</script>

<style scoped>
.tourism-dashboard {
	width: 100%;
	height: 100%;
	padding: 8px;
	box-sizing: border-box;
	display: grid;
	min-width: 0;
	min-height: 0;
	grid-template-rows: minmax(0, 0.9fr) minmax(0, 1.1fr);
	gap: 8px;
}

.dashboard-row {
	display: grid;
	gap: 8px;
	min-width: 0;
	min-height: 0;
}

.tourism-overview {
	grid-template-columns: minmax(0, 1fr) minmax(0, 2fr) minmax(0, 1fr) minmax(0, 1fr);
}

.tourism-detail {
	grid-template-columns: minmax(0, 1fr) minmax(0, 1fr);
}

.panel {
	min-width: 0;
	min-height: 0;
	overflow: hidden;
	background: rgba(12, 31, 67, 0.72);
	border: 1px solid rgba(72, 196, 255, 0.38);
	box-sizing: border-box;
}

.panel > :deep(*) {
	width: 100%;
	height: 100%;
	min-width: 0 !important;
	min-height: 0 !important;
	box-sizing: border-box;
}

@media (max-width: 1280px) {
	.tourism-dashboard {
		grid-template-rows: minmax(0, 1fr) minmax(0, 1fr);
	}
	.tourism-overview,
	.tourism-detail {
		grid-template-columns: repeat(2, minmax(0, 1fr));
	}
}
</style>
