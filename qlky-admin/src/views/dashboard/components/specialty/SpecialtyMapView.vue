<template>
	<div class="map-container">
		<dv-loading v-if="loading">地图加载中...</dv-loading>
		<el-alert v-if="loadError" type="error" show-icon :closable="false"> 地图加载失败，请<a @click="initChart">重试</a> </el-alert>
		<span v-show="!loading && !loadError" class="map-gesture-hint">滚轮缩放 · 拖动平移</span>

		<div v-show="!loading && !loadError">
			<el-select v-model="selectedCity" placeholder="选择城市" class="city-selector" @change="initChart">
				<el-option v-for="city in cityOptions" :key="city.value" :label="city.label" :value="city.value" />
			</el-select>

			<div ref="chartEl" class="chart-content"></div>
		</div>
	</div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, nextTick, computed } from 'vue';
import * as echarts from 'echarts';
import { ElMessage } from 'element-plus';
import { getSpecialtyAll } from '/@/apis/qlky/tourist';
import { useUserInfo } from '/@/stores/userInfo';

const cityConfig = {
	xian: {
		// 省会，古称长安[3](@ref)
		name: '西安市',
		geoPath: '610100_full.json',
		districts: ['新城区', '碑林区', '莲湖区', '灞桥区', '未央区', '雁塔区', '阎良区', '临潼区', '长安区', '高陵区', '鄠邑区', '蓝田县', '周至县'],
	},
	tongchuan: {
		// 红色革命老区[4](@ref)
		name: '铜川市',
		geoPath: '610200_full.json',
		districts: ['王益区', '印台区', '耀州区', '宜君县'],
	},
	baoji: {
		// 青铜器之乡[4](@ref)
		name: '宝鸡市',
		geoPath: '610300_full.json',
		districts: ['渭滨区', '金台区', '陈仓区', '凤翔区', '岐山县', '扶风县', '眉县', '陇县', '千阳县', '麟游县', '凤县', '太白县'],
	},
	xianyang: {
		// 中国第一帝都[3](@ref)
		name: '咸阳市',
		geoPath: '610400_full.json',
		districts: [
			'秦都区',
			'渭城区',
			'杨陵区',
			'兴平市',
			'三原县',
			'泾阳县',
			'乾县',
			'礼泉县',
			'永寿县',
			'彬州市',
			'长武县',
			'旬邑县',
			'淳化县',
			'武功县',
		],
	},
	weinan: {
		// 关中粮仓[4](@ref)
		name: '渭南市',
		geoPath: '610500_full.json',
		districts: ['临渭区', '华州区', '韩城市', '华阴市', '潼关县', '大荔县', '合阳县', '澄城县', '蒲城县', '白水县', '富平县'],
	},
	yanan: {
		// 革命圣地[3](@ref)
		name: '延安市',
		geoPath: '610600_full.json',
		districts: ['宝塔区', '安塞区', '子长市', '延长县', '延川县', '志丹县', '吴起县', '甘泉县', '富县', '洛川县', '宜川县', '黄龙县', '黄陵县'],
	},
	yulin: {
		// 能源之都[4](@ref)
		name: '榆林市',
		geoPath: '610800_full.json',
		districts: ['榆阳区', '横山区', '神木市', '府谷县', '靖边县', '定边县', '绥德县', '米脂县', '佳县', '吴堡县', '清涧县', '子洲县'],
	},
	hanzhong: {
		// 汉文化发祥地[3](@ref)
		name: '汉中市',
		geoPath: '610700_full.json',
		districts: ['汉台区', '南郑区', '城固县', '洋县', '西乡县', '勉县', '宁强县', '略阳县', '镇巴县', '留坝县', '佛坪县'],
	},
	ankang: {
		// 南水北调水源地[4](@ref)
		name: '安康市',
		geoPath: '610900_full.json',
		districts: ['汉滨区', '旬阳市', '汉阴县', '石泉县', '宁陕县', '紫阳县', '岚皋县', '平利县', '镇坪县', '白河县'],
	},
	shangluo: {
		// 秦岭腹地[3](@ref)
		name: '商洛市',
		geoPath: '611000_full.json',
		districts: ['商州区', '洛南县', '丹凤县', '商南县', '山阳县', '镇安县', '柞水县'],
	},
	yangling: {
		// 农业高新技术示范区[3](@ref)
		name: '杨凌示范区',
		geoPath: '611100_full.json',
		districts: ['杨陵街道', '李台街道', '大寨街道', '五泉镇'],
	},
};

const cityOptions = [
	{ value: 'xian', label: '西安市' },
	{ value: 'baoji', label: '宝鸡市' },
	{ value: 'xianyang', label: '咸阳市' },
	{ value: 'tongchuan', label: '铜川市' },
	{ value: 'weinan', label: '渭南市' },
	{ value: 'yanan', label: '延安市' },
	{ value: 'yulin', label: '榆林市' },
	{ value: 'hanzhong', label: '汉中市' },
	{ value: 'ankang', label: '安康市' },
	{ value: 'shangluo', label: '商洛市' },
];

// 响应式状态
const loading = ref(true);
const loadError = ref(false);
const selectedCity = ref('shangluo');
const chartEl = ref(null);
const chartInstance = ref(null);
let resizeHandler = null;
let resizeObserver = null;
let removeMapInteractions = null;

const districtKeywords = {
	shangluo: [
		{ district: '商州区', keywords: ['商州', 'shangzhou'] },
		{ district: '洛南县', keywords: ['洛南', '洛源', 'luonan', 'luoyuan'] },
		{ district: '丹凤县', keywords: ['丹凤', 'danfeng'] },
		{ district: '商南县', keywords: ['商南', 'shangnan'] },
		{ district: '山阳县', keywords: ['山阳', '天竺山', 'shanyang', 'tianzhushan'] },
		{ district: '镇安县', keywords: ['镇安', '云盖寺', 'zhenan', 'yungaisi'] },
		{ district: '柞水县', keywords: ['柞水', 'zhashui'] },
	],
};

const state = useUserInfo();

const city = computed(() => state.userInfos?.city || 'shangluo');

// 获取地图数据（修改后）
const fetchGeoJson = async (city) => {
	try {
		const config = cityConfig[city] || cityConfig.shangluo;
		const res = await fetch(`/geo-proxy/${config.geoPath}`);
		if (!res.ok) throw new Error(`HTTP错误! 状态码: ${res.status}`);
		return await res.json();
	} catch (error) {
		ElMessage.error(`地理数据加载失败: ${error.message}`);
		throw error;
	}
};

// 初始化图表（完整实现）
function resolveDistrict(record, cityKey, districts) {
	const origin = String(record.origin_place || '');
	const directMatch = districts.find((district) => origin.includes(district) || origin.includes(district.replace(/[区县]$/, '')));
	if (directMatch) return directMatch;

	const searchableText = [record.product_name, record.origin_place, record.description, record.detail_address]
		.filter(Boolean)
		.join(' ')
		.toLowerCase();
	const keywordMatch = (districtKeywords[cityKey] || []).find(({ keywords }) => keywords.some((keyword) => searchableText.includes(keyword)));
	if (keywordMatch) return keywordMatch.district;

	return cityKey === 'shangluo' && searchableText.includes('商洛') ? '商州区' : null;
}

const initChart = async () => {
	try {
		loading.value = true;
		loadError.value = false;
		await nextTick();
		// 检查DOM有效性
		if (!chartEl.value) {
			throw new Error('图表容器未找到或未渲染');
		}

		// 销毁旧实例（增强版）
		if (chartInstance.value) {
			echarts.dispose(chartInstance.value);
			chartInstance.value = null;
		}

		// 获取配置数据
		const config = cityConfig[selectedCity.value] || cityConfig.shangluo;
		const response = await getSpecialtyAll();
		const records = response?.data?.records || [];
		const summaryData = { sum: records.length };
		const districtCounts = new Map(config.districts.map((district) => [district, 0]));
		records.forEach((record) => {
			const district = resolveDistrict(record, selectedCity.value, config.districts);
			if (district) districtCounts.set(district, (districtCounts.get(district) || 0) + 1);
		});
		const districtData = config.districts.map((name) => ({ name, value: districtCounts.get(name) || 0 }));

		// 加载地理JSON（增加容错）
		const geoJson = await fetchGeoJson(selectedCity.value);
		if (!geoJson) throw new Error('地理数据加载失败');

		echarts.registerMap(config.name, geoJson);
		loading.value = false;
		await nextTick();

		// 初始化实例（带容错）
		chartInstance.value = echarts.init(chartEl.value, null, { renderer: 'canvas' });
		// 配置选项
		const option = {
			backgroundColor: '#091c3d',
			title: {
				text: `${config.name}特产分布图`,
				subtext: `数据总量: ${summaryData.sum}种`,
				left: 'center',
				textStyle: { color: '#fff' },
			},
			tooltip: { trigger: 'item', formatter: '{a}<br/>{b}: {c}个' },
			visualMap: {
				min: 0,
				max: 20,
				left: 'left',
				top: 'bottom',
				text: ['高', '低'],
				calculable: true,
				inRange: {
					color: ['#1a4ca4', '#133776', '#091c3d'],
				},
				textStyle: {
					color: '#fff',
				},
			},
			series: [
				{
					name: '特产分布',
					type: 'map',
					map: config.name,
					roam: false,
					zoom: 1,
					scaleLimit: {
						min: 1,
						max: 4,
					},
					data: districtData,
					label: {
						show: true,
						color: '#fff',
						fontSize: 12,
					},
					itemStyle: {
						areaColor: '#1D346F',
						borderColor: '#D79D3D',
					},
				},
			],
		};

		// 初始化图表
		chartInstance.value.setOption(option);
		bindMapInteractions();
		// 窗口自适应
		if (resizeHandler) window.removeEventListener('resize', resizeHandler);
		resizeHandler = resizeChart;
		window.addEventListener('resize', resizeHandler);
		requestAnimationFrame(resizeChart);
	} catch (error) {
		loadError.value = true;
		console.error('地图初始化失败:', error);
	} finally {
		loading.value = false;
	}
};

function resizeChart() {
	chartInstance.value?.resize();
}

function bindMapInteractions() {
	removeMapInteractions?.();
	const chart = chartInstance.value;
	const target = chartEl.value;
	if (!chart || !target) return;

	let pointer = null;
	const pan = (deltaX, deltaY) => {
		chart.dispatchAction({
			type: 'geoRoam',
			seriesIndex: 0,
			dx: deltaX,
			dy: deltaY,
			animation: { duration: 0 },
		});
	};
	const onPointerDown = (event) => {
		if (event.button !== 0) return;
		pointer = { x: event.clientX, y: event.clientY, id: event.pointerId };
		target.setPointerCapture?.(event.pointerId);
		event.preventDefault();
	};
	const onPointerMove = (event) => {
		if (!pointer || event.pointerId !== pointer.id) return;
		pan(event.clientX - pointer.x, event.clientY - pointer.y);
		pointer.x = event.clientX;
		pointer.y = event.clientY;
	};
	const onPointerUp = (event) => {
		if (!pointer || event.pointerId !== pointer.id) return;
		target.releasePointerCapture?.(event.pointerId);
		pointer = null;
	};
	const onWheel = (event) => {
		event.preventDefault();
		const bounds = target.getBoundingClientRect();
		chart.dispatchAction({
			type: 'geoRoam',
			seriesIndex: 0,
			zoom: event.deltaY < 0 ? 1.15 : 0.87,
			originX: event.clientX - bounds.left,
			originY: event.clientY - bounds.top,
			animation: { duration: 0 },
		});
	};

	target.addEventListener('pointerdown', onPointerDown);
	target.addEventListener('pointermove', onPointerMove);
	target.addEventListener('pointerup', onPointerUp);
	target.addEventListener('pointercancel', onPointerUp);
	target.addEventListener('wheel', onWheel, { passive: false });
	removeMapInteractions = () => {
		target.removeEventListener('pointerdown', onPointerDown);
		target.removeEventListener('pointermove', onPointerMove);
		target.removeEventListener('pointerup', onPointerUp);
		target.removeEventListener('pointercancel', onPointerUp);
		target.removeEventListener('wheel', onWheel);
	};
}

// 生命周期
onMounted(() => {
	resizeObserver = new ResizeObserver(resizeChart);
	resizeObserver.observe(chartEl.value);
	initChart();
});
onBeforeUnmount(() => {
	if (resizeHandler) window.removeEventListener('resize', resizeHandler);
	resizeObserver?.disconnect();
	removeMapInteractions?.();
	chartInstance.value?.dispose();
});
</script>

<style scoped>
.map-container {
	position: relative;
	width: 100%;
	height: 100%;
	min-height: 300px;
}

.chart-content {
	width: 100%;
	height: 100%;
	min-height: 300px;
	cursor: grab;
}

.chart-content:active {
	cursor: grabbing;
}

.map-gesture-hint {
	position: absolute;
	right: 10px;
	bottom: 8px;
	z-index: 2;
	padding: 3px 6px;
	border-radius: 3px;
	background: rgba(9, 28, 61, 0.78);
	color: #bdeeff;
	font-size: 12px;
	pointer-events: none;
}

:deep(.el-select__wrapper) {
	background: rgba(9, 28, 61, 0.9);
	border: 1px solid #1ca498;
	color: #fff;
}

:deep(.el-select__placeholder) {
	color: #87a3c2 !important;
}
</style>
