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
import { deleteTask, getTaskList, saveTask, executeCrawlerTask,cancelCrawlerTask, deleteCrawlerData } from '/@/apis/qlky/crawlerTask';
import { ElMessage, ElMessageBox } from 'element-plus';

const formatter = (value: any, datatype: string) => {
	switch (datatype) {
		case 'datetime':
			return value ? new Date(value).toLocaleString() : '-';
		case 'time':
			return value ? new Date(value).toLocaleTimeString() : '-';
		case 'number':
			return value || 0;
		case 'status':
			return formatStatus(value);
		default:
			return value || '-';
	}
};

const formatStatus = (status: string) => {
	const statusMap: Record<string, string> = {
		pending: '待执行',
		running: '执行中',
		completed: '已完成',
		failed: '失败',
	};
	return statusMap[status] || status;
};

const tableFormData = reactive({
	title: '任务管理',
	isLoading: false,
	formFlag: 1,
	formVisiable: false,
	addBtn: true,
	tableHead: [
		{
			label: '任务ID',
			prop: 'taskId',
			datatype: 'id',
			tableShow: false,
			formShow: false,
		},
		{
			label: '任务名称',
			prop: 'taskName',
			datatype: 'text',
			tableShow: true,
			formShow: true,
			required: true,
		},
		{
			label: '关联爬虫ID',
			prop: 'crawlerId',
			datatype: 'select',
			tableShow: true,
			formShow: true,
			required: true,
		},
		{
			label: '任务状态',
			prop: 'taskStatus',
			datatype: 'select',
			tableShow: true,
			formShow: false,
		},
		{
			label: '开始时间',
			prop: 'startTime',
			datatype: 'datetime',
			tableShow: true,
			formShow: true,
		},
		{
			label: '结束时间',
			prop: 'endTime',
			datatype: 'datetime',
			tableShow: true,
			formShow: true,
		},
		{
			label: '时间表达式',
			prop: 'cron',
			datatype: 'text',
			tableShow: true,
			formShow: true,
		},
		{
			label: '间隔秒数',
			prop: 'intervalSeconds',
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
		{
			label: '操作',
			prop: 'morebutton',
			datatype: 'morebutton',
			tableShow: true,
			formShow: false,
			width: 240,
			children: [
				{
					label: '手动执行',
					color: 'success',
					datatype: 'textbutton',
					fc: (row: any) => handleExecute(row),
					ifelse: (row: any) => {
						return row.taskStatus === '0';
					},
				},
				{
					label: '取消任务',
					color: 'success',
					datatype: 'textbutton',
					fc: (row: any) => handleNoExecute(row),
					ifelse: (row: any) => {
						return row.taskStatus === '1';
					},
				},
				{
					label: '清空数据集',
					color: 'primary',
					datatype: 'textbutton',
					fc: (row: any) => handleDelete(row),
					ifelse: (row: any) => {
						return row.taskStatus === '0';
					},
				},
			],
		},
	],
	page: {
		currentPage: 1,
		pageSize: 10,
		total: 0,
	},
	searchHead: [
		{
			label: '任务名称',
			prop: 'taskName',
			datatype: 'text',
		},
		{
			label: '任务状态',
			prop: 'taskStatus',
			datatype: 'select',
		},
	],
	searchForm: {},
	tableData: [],
	formData: {},
	crawlerId: [],
	formRules: {
		cron: [
			{
				message: '请输入cron表达式',
				trigger: 'blur',
			},
		],
	},
	optionsName: {
		crawlerId: 'crawlerName',
		taskStatus: 'name',
	},
	optionsValue: {
		crawlerId: 'crawlerId',
		taskStatus: 'id',
	},
	taskStatus: [
		{
			id: '0',
			name: '待执行',
		},
		{
			id: '1',
			name: '执行中',
		},
	],
});

function onGetData() {
	tableFormData.isLoading = true;
	getTaskList({
		...tableFormData.searchForm,
		page: tableFormData.page.currentPage,
		pageSize: tableFormData.page.pageSize,
	})
		.then((res: any) => {
			if (res.code === 8888) {
				tableFormData.tableData = res.data.list.list;
				tableFormData.page.total = res.data.total;
				tableFormData.crawlerId = res.data.list.crawlerId;
			}
		})
		.finally(() => {
			tableFormData.isLoading = false;
		});
}

function onUpdateData() {
	saveTask({
		...tableFormData.formData,
	}).then((res: any) => {
		if (res.code === 8888) {
			ElMessage.success('任务更新成功');
			onGetData();
		}
	});
}

function onDeleteData(row: any) {
	deleteTask({
		taskId: row.taskId,
	}).then((res: any) => {
		if (res.code === 8888) {
			ElMessage.success('任务删除成功');
			onGetData();
		}
	});
}

function onAddData() {
	saveTask({
		...tableFormData.formData,
	}).then((res: any) => {
		if (res.code === 8888) {
			ElMessage.success('任务创建成功');
			onGetData();
		}
	});
}

function handleExecute(row: any) {
	// 这里添加执行任务的逻辑
	ElMessage.info(`开始执行任务: ${row.taskName}`);
	row.taskStatus = '1';
	// 调用执行任务的API
	executeCrawlerTask(row.crawlerId, row.taskId).then((res: any) => {
		if (res.code === 8888) {
			ElMessage.success(`任务执行成功: ${row.taskName}`);
		}
	});
}

function handleNoExecute(row: any){
	ElMessage.info(`取消任务: ${row.taskName}`);
	row.taskStatus = '0';
	cancelCrawlerTask(row.crawlerId, row.taskId).then((res: any) => {
		if (res.code === 8888) {
			ElMessage.success(`任务取消成功: ${row.taskName}`);
		}
	});
}

function handleDelete(row: any) {
	ElMessageBox.confirm('确定清空数据集吗?', '提示', {
		confirmButtonText: '确定',
		cancelButtonText: '取消',
		type: 'warning',
	}).then(() => {
		deleteCrawlerData(row.taskId).then((res: any) => {
			if (res.code === 8888) {
				ElMessage.success('清空成功');
			}
		})
	})
}

onMounted(() => {
	onGetData();
});
</script>

<style lang="scss" scoped>
/* 可以添加自定义样式 */
.status-badge {
	display: inline-block;
	padding: 2px 8px;
	border-radius: 4px;
	font-size: 12px;

	&.pending {
		background-color: #f0f0f0;
		color: #666;
	}
	&.running {
		background-color: #e6f7ff;
		color: #1890ff;
	}
	&.completed {
		background-color: #f6ffed;
		color: #52c41a;
	}
	&.failed {
		background-color: #fff2f0;
		color: #f5222d;
	}
}
</style>
