<template>
	<tableFormComponents
		:formatter="formatter"
		v-model="tableFormData"
		@getData="onGetData"
		@updateData="onUpdateData"
		@deleteData="onDeleteData"
		@createData="onAddData"
	/>
</template>
<script lang="ts" setup>
import { onMounted, reactive } from 'vue';
import tableFormComponents from '/@/components/tableForm/index.vue';
import { deleteSystem, getSystemList, saveSystem } from '/@/apis/qlky/crawlerset';
import { ElMessage } from 'element-plus';

const formatter = (value: any, datatype: string) => {
	switch (datatype) {
		case 'datetime':
			return value.replace(/T/g, ' ').replace(/\.000Z/g, '');
		case 'number':
			return value;
		default:
			return value;
	}
};

const tableFormData = reactive({
	title: '爬虫设置',
	isLoading: false,
	formFlag: 1,
	formVisiable: false,
	addBtn: true,
	tableHead: [
		{
			label: '关联爬虫ID',
			prop: 'systemId',
			datatype: 'id',
			tableShow: false, // 关联ID一般不显示在表格中
			formShow: false, // 关联ID一般不在表单中编辑
		},
		{
			label: '爬虫名称',
			prop: 'crawlerName',
			datatype: 'text',
			tableShow: true,
			formShow: false,
		},
		{
			label: '代理设置',
			prop: 'proxy',
			datatype: 'text',
			tableShow: true,
			formShow: true,
		},
		{
			label: 'User-Agent',
			prop: 'userAgent',
			datatype: 'text',
			tableShow: true,
			formShow: true,
		},
		{
			label: '最大重试次数',
			prop: 'maxRetry',
			datatype: 'number',
			tableShow: true,
			formShow: true,
		},
		{
			label: '超时时间（秒）',
			prop: 'timeout',
			datatype: 'number',
			tableShow: true,
			formShow: true,
		},
		{
			label: '配置开始时间',
			prop: 'startTime',
			datatype: 'time',
			tableShow: true,
			formShow: true,
		},
		{
			label: '配置结束时间',
			prop: 'endTime',
			datatype: 'time',
			tableShow: true,
			formShow: true,
		},
		{
			label: '创建时间',
			prop: 'createdAt',
			datatype: 'datetime',
			tableShow: true,
			formShow: false, // 根据实际需求决定是否在表单中显示
		},
		{
			label: '更新时间',
			prop: 'updatedAt',
			datatype: 'datetime',
			tableShow: true,
			formShow: false, // 根据实际需求决定是否在表单中显示
		},
		{
			label: '系统配置ID',
			prop: 'systemId',
			datatype: 'number',
			tableShow: false, // 主键一般不显示在表格中
			formShow: false, // 主键一般不在表单中编辑
		},
		{
			label: '关联爬虫ID',
			prop: 'crawlerId',
			datatype: 'crawlerId',
			tableShow: false, // 关联ID一般不显示在表格中
			formShow: false, // 关联ID一般不在表单中编辑
		},
		{
			label: '修改',
			prop: 'update',
			datatype: 'button',
			tableShow: true,
			formShow: false,
		},
		{
			label: '删除',
			prop: 'delete',
			datatype: 'button',
			tableShow: true,
			formShow: false,
		},
	],
	crawlerId: [],
	page: {
		currentPage: 1,
		pageSize: 10,
		total: 0,
	},
	searchHead: [
		{
			label: '爬虫名称',
			prop: 'crawlerName',
			datatype: 'text',
		},
	],
	searchForm: {},
	tableData: [],
	formData: [],
});

function onGetData() {
	tableFormData.isLoading = true;
	getSystemList({
		...tableFormData.searchForm,
		page: tableFormData.page.currentPage,
		pageSize: tableFormData.page.pageSize,
	}).then((res: any) => {
		if (res.code === 8888) {
			tableFormData.tableData = res.data.list;
			tableFormData.page.total = res.data.total;
			tableFormData.isLoading = false;
		}
	});
}

function onUpdateData() {
	saveSystem({
		...tableFormData.formData,
	}).then((res: any) => {
		if (res.code === 8888) {
			ElMessage.success('保存成功');
			onGetData();
		}
	});
}

function onDeleteData(row: any) {
	deleteSystem({
		systemId: row.systemId,
	}).then((res: any) => {
		if (res.code === 8888) {
			ElMessage.success('删除成功');
			onGetData();
		}
	});
}

function onAddData() {
	saveSystem({
		...tableFormData.formData,
	}).then((res: any) => {
		if (res.code === 8888) {
			ElMessage.success('保存成功');
			onGetData();
		}
	});
}

onMounted(() => [onGetData()]);
</script>
<style lang="scss" scoped></style>
