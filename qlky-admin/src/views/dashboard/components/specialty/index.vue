<template>
	<div class="specialty-dashboard">
		<div class="dashboard-row specialty-overview">
			<section class="panel specialty-map"><SpecialtyMapView /></section>
			<section class="panel specialty-sector"><SectorDiagramView /></section>
			<section class="panel specialty-proportion"><SpecialtyProportionView /></section>
		</div>
		<div class="dashboard-row specialty-detail">
			<section class="panel specialty-table"><SpecialtyTableView /></section>
			<section class="panel specialty-ranking"><SpecoaltyTop10View /></section>
		</div>
	</div>
</template>

<script setup>
import SectorDiagramView from './SectorDiagramView.vue';
import SpecialtyMapView from './SpecialtyMapView.vue';
import SpecialtyProportionView from './SpecialtyProportionView.vue';
import SpecialtyTableView from './SpecialtyTableView.vue';
import SpecoaltyTop10View from './SpecoaltyTop10View.vue';
</script>

<style scoped>
.specialty-dashboard {
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

.specialty-overview {
	grid-template-columns: minmax(0, 2fr) minmax(0, 1fr) minmax(0, 1fr);
}

.specialty-detail {
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

@media (max-width: 1100px) {
	.specialty-dashboard {
		grid-template-rows: minmax(0, 1fr) minmax(0, 1fr);
	}
	.specialty-overview,
	.specialty-detail {
		grid-template-columns: repeat(2, minmax(0, 1fr));
	}
}
</style>
