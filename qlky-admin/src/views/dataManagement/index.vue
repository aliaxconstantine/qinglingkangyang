<template>
	<tableFormComponents
		:formatter="formatter"
		v-model="tableFormData"
		@getData="onGetData"
		@updateData="onUpdateData"
		@deleteData="onDeleteData"
		@createData="onAddData"
		@exportData="exportData"
		:export="true"
	>
	</tableFormComponents>
</template>
<script lang="ts" setup>
import { onMounted, reactive, ref } from 'vue';
import tableFormComponents from '/@/components/tableForm/index.vue';
import { addOrUpdateCrawlerData, deleteCrawlerData, getCrawlerDataByCrawlerId } from '/@/apis/qlky/crawlerData';
import { ElMessage, ElMessageBox } from 'element-plus';
import { useRouter, useRoute } from 'vue-router';
const router = useRouter();
const route = useRoute();
const tableFormData = reactive<any>({
	title: '数据',
	isLoading: false,
	formFlag: 1,
	formVisiable: false,
	addBtn: true,
	tableHead: [],
	searchHead: [],
	pagging: false,
	page: {
		currentPage: 1,
		pageSize: 10,
		total: 0,
	},
	tableData: [],
	formData: [],
	searchForm: [],
});
const formatter = (value: any, datatype: string) => {
	switch (datatype) {
		case 'datetime':
			return value ? new Date(value).toLocaleString() : '-';
		case 'time':
			return value ? new Date(value).toLocaleTimeString() : '-';
		case 'number':
			return value || 0;
		default:
			return value || '-';
	}
};
function getData() {
	tableFormData.isLoading = true;
	//从路由信息上获取爬虫ID
	const crawlerId = route.meta.crawlerId;
	getCrawlerDataByCrawlerId({
		crawlerId: crawlerId,
		...tableFormData.page,
	})
		.then((res: any) => {
			if (res.code === 8888) {
				const pages = { ...tableFormData.page };
				Object.assign(tableFormData, res.data.tableData);
				tableFormData.page = pages;
				tableFormData.tableData = res.data.crawlerData.list;
				tableFormData.page.total = res.data.crawlerData.total;
			}
		})
		.finally(() => {
			tableFormData.isLoading = false;
		});
}

function onGetData() {
	getData();
}

function onUpdateData() {
	const crawlerId = route.meta.crawlerId;
	addOrUpdateCrawlerData({
		...tableFormData.formData,
		crawlerId: crawlerId,
	}).then((res) => {
		if (res.code === 8888) {
			ElMessage.success('保存成功');
			getData();
		}
	});
}
function onDeleteData(row: any) {
	deleteCrawlerData({
		crawlerId: route.meta.crawlerId,
		groupId: row.group_id,
	}).then((res) => {
		if (res.code === 8888) {
			ElMessage.success('删除成功');
			getData();
		}
	});
}

function onAddData() {
	const crawlerId = route.meta.crawlerId;
	addOrUpdateCrawlerData({
		...tableFormData.formData,
		crawlerId: crawlerId,
	}).then((res) => {
		if (res.code === 8888) {
			ElMessage.success('保存成功');
			getData();
		}
	});
}

function exportData(fun: (data: any) => {}) {
	tableFormData.isLoading = true;
	//从路由信息上获取爬虫ID
	const crawlerId = route.meta.crawlerId;
	getCrawlerDataByCrawlerId({
		crawlerId: crawlerId,
		currentPage: 1,
		pageSize: 1000000,
	})
		.then((res: any) => {
			if (res.code === 8888) {
				fun(res.data.crawlerData.list);
			}
		})
		.finally(() => {
			tableFormData.isLoading = false;
		});
}

onMounted(() => {
	getData();
});
</script>
<style lang="scss" scoped></style>
