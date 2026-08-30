<template>
	<div>
		<tableFormComponents
			:formatter="formatter"
			v-model="tableFormData"
			@getData="onGetData"
			@updateData="onUpdateData"
			@deleteData="onDeleteData"
			@createData="onAddData"
			export
		>
		</tableFormComponents>
	</div>
</template>
<script lang="ts" setup>
import { onMounted, reactive, watch, defineProps } from 'vue';
import tableFormComponents from '/@/components/tableForm/index.vue';
import { saveCrawlerField, deleteCrawlerField, getCrawlerFieldList } from '/@/apis/qlky/crawlerField.ts'; // 需替换为实际API接口
import { DB_FIELD_TYPES } from '/@/constant/crawlerConstant.ts';
import { ElMessage, ElMessageBox } from 'element-plus';
const props = defineProps({
	// 父组件传递的参数
	crawlerId: {
		type: Number,
		default: 0,
	},
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

const tableFormData = reactive({
	title: '字段管理',
	isLoading: false,
	formFlag: 1,
	formVisiable: false,
	addBtn: true,
	tableHead: [
		{
			label: '字段ID',
			prop: 'fieldId',
			datatype: 'id',
			tableShow: false,
			formShow: false,
		},
		{
			label: '字段名称',
			prop: 'fieldName',
			datatype: 'text',
			tableShow: true,
			formShow: true,
			required: true,
		},
		{
			label: '字段类型',
			prop: 'fieldType',
			datatype: 'select',
			tableShow: true,
			formShow: true,
		},
		{
			label: '字段描述',
			prop: 'fieldDescription',
			datatype: 'text',
			tableShow: true,
			formShow: true,
		},
		{
			label: '排序ID',
			prop: 'sortid',
			datatype: 'number',
			tableShow: true,
			formShow: true,
		},
		{
			label: '创建时间',
			prop: 'createdAt',
			datatype: 'datetime',
			tableShow: true,
			formShow: false,
		},
		{
			label: '更新时间',
			prop: 'updatedAt',
			datatype: 'datetime',
			tableShow: true,
			formShow: false,
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
	page: {
		currentPage: 1,
		pageSize: 10,
		total: 0,
	},
	searchHead: [],
	searchForm: {},
	tableData: [],
	formData: {},
	fieldType: DB_FIELD_TYPES,
	crawlerId: [],
	optionsName: {
		crawlerId: 'crawlerName',
		fieldType: 'name',
	},
	optionsValue: {
		crawlerId: 'crawlerId',
		fieldType: 'id',
	},
});
function onGetData() {
	tableFormData.isLoading = true;
	getCrawlerFieldList({
		crawlerId: props.crawlerId,
		...tableFormData.searchForm,
		currentPage: tableFormData.page.currentPage,
		pageSize: tableFormData.page.pageSize,
	})
		.then((res: any) => {
			if (res.code === 8888) {
				tableFormData.tableData = res.data.list;
				tableFormData.page.total = res.data.total;
			}
		})
		.finally(() => {
			tableFormData.isLoading = false;
		});
}

watch(
	() => props.crawlerId,
	async (newValue, oldValue) => {
		if (newValue !== oldValue) {
			onGetData();
		}
	}
);

function onUpdateData(row: any) {
	saveCrawlerField({
		...tableFormData.formData,
		crawlerId: props.crawlerId,
	}).then((res: any) => {
		if (res.code === 8888) {
			ElMessage.success('保存成功');
			onGetData();
		}
	});
}

function onDeleteData(row: any) {
	deleteCrawlerField(row.fieldId).then((res: any) => {
		if (res.code === 8888) {
			ElMessage.success('删除成功');
			onGetData();
		}
	});
}

function onAddData(row: any) {
	saveCrawlerField({
		...tableFormData.formData,
		crawlerId: props.crawlerId,
	}).then((res: any) => {
		if (res.code === 8888) {
			ElMessage.success('保存成功');
			onGetData();
		}
	});
}

onMounted(() => {
	onGetData();
});
</script>
