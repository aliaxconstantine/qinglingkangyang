<template>
	<div>
		<tableFormComponents
			:formatter="formatter"
			v-model="tableFormData"
			@getData="onGetData"
			@updateData="onUpdateData"
			@deleteData="onDeleteData"
			@createData="onAddData"
		>
			<template #crawlerProgramPathform>
				<el-input v-model="tableFormData.formData.crawlerProgramPath" style="margin-bottom: 20rpx" placeholder="请输入文件路径"></el-input>
				<FileUploader v-model="tableFormData.formData.file" accept=".py," tip="请上传python文件" style="margin-top: 20px" />
			</template>
			<template #crawlerProgramPath="{ row }">
				<div>{{ row.crawlerProgramPath }}</div>
			</template>
		</tableFormComponents>
		<el-dialog v-model="dialogVisible" title="编辑字段" style="height: 95vh; width: 80vw;">
			<crawlerFieldManagement :crawlerId="currentCrawler.crawlerId"></crawlerFieldManagement>
		</el-dialog>
	</div>
</template>

<script lang="ts" setup>
import { onMounted, reactive, ref } from 'vue';
import tableFormComponents from '/@/components/tableForm/index.vue';
import { deleteCrawler, getCrawlerList, saveCrawler, updateCrawlerPath, updateCrawlerInfo } from '/@/apis/qlky/crawler.ts';
import { ElMessage, ElMessageBox } from 'element-plus';
import FileUploader from '/@/components/fileupdate/index.vue';
import crawlerFieldManagement from '/@/views/crawlerFieldManagement/index.vue';
const dialogVisible = ref(false);
const currentCrawler = ref({});
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
	title: '爬虫管理',
	isLoading: false,
	formFlag: 1,
	formVisiable: false,
	addBtn: true,
	tableHead: [
		{
			label: '爬虫ID',
			prop: 'crawlerId',
			datatype: 'id',
			tableShow: false,
			formShow: false,
		},
		{
			label: '爬虫名称',
			prop: 'crawlerName',
			datatype: 'text',
			tableShow: true,
			formShow: true,
			required: true,
		},
		{
			label: '爬虫描述',
			prop: 'crawlerDescription',
			datatype: 'text',
			tableShow: true,
			formShow: true,
		},
		{
			label: '程序路径',
			prop: 'crawlerProgramPath',
			datatype: 'html',
			tableShow: true,
			formShow: true,
			required: true,
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
			label: '编辑字段',
			prop: 'field',
			datatype: 'button',
			tableShow: true,
			formShow: false,
			fc: (row) => {
				currentCrawler.value = { ...row };
				dialogVisible.value = true;
			},
			width: 150,
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
	searchHead: [
		{
			label: '爬虫名称',
			prop: 'crawlerName',
			datatype: 'text',
		},
		{
			label: '程序路径',
			prop: 'crawlerProgramPath',
			datatype: 'text',
		},
	],
	searchForm: {},
	tableData: [],
	formData: {},
});

// 获取爬虫列表
function onGetData() {
	tableFormData.isLoading = true;
	getCrawlerList({
		...tableFormData.searchForm,
		page: tableFormData.page.currentPage,
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

// 更新爬虫信息
function onUpdateData() {
	saveCrawler(tableFormData.formData.file, tableFormData.formData).then((res: any) => {
		if (res.code === 8888) {
			ElMessage.success('更新成功');
			onGetData();
		}
	});
}

// 删除爬虫
function onDeleteData(row: any) {
	deleteCrawler(row.crawlerId, row.crawlerProgramPath).then((res: any) => {
		if (res.code === 8888) {
			ElMessage.success('删除成功');
			onGetData();
		}
	});
}

// 新增爬虫
function onAddData() {
	saveCrawler(tableFormData.formData.file, tableFormData.formData).then((res: any) => {
		if (res.code === 8888) {
			ElMessage.success('新增成功');
			onGetData();
		}
	});
}

onMounted(() => {
	onGetData();
});
</script>

<style lang="scss" scoped>
/* 可以添加一些自定义样式 */
.action-buttons {
	display: flex;
	gap: 8px;
}
</style>
