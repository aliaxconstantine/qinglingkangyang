<template>
	<div>
		<div v-loading="tableFormData.isLoading">
			<div style="padding: 10px; display: flex">
				<div style="display: flex; margin-right: 20px; margin-bottom: 20px">
					<el-button type="primary" @click="onClickAdd" v-if="tableFormData.addBtn">{{ `新增${tableFormData.title}` }}</el-button>
					<el-button
						type="primary"
						@click="
							onGetData();
							tableFormData.isLoading = true;
						"
						>重新查询</el-button
					>
					<el-button type="primary" @click="onClickExport" v-if="export">导出数据</el-button>
				</div>
				<tableSearch v-if="tableFormData.searchHead.length > 0" v-model="tableFormData" :vmProps="vmProps" @getData="vmEmits('getData')" />
			</div>
			<div style="padding: 10px; margin-top: -20px; z-index: -1;">
				<el-table :data="tableFormData.tableData" height="70vh" :border="true" :row-key="'id'">
					<el-table-column type="index" width="80" label="序号"></el-table-column>
					<template v-for="item in tableFormData.tableHead" :key="item">
						<el-table-column
							:prop="item.datatype"
							:label="item.label"
							:width="
								item.datatype === 'button'
									? item.width
										? item.width
										: 100
									: item.width || getTableColumnWidths(tableFormData.tableHead, tableFormData.tableData, item.prop, 25)
							"
							:fixed="item.datatype === 'button' || item.datatype === 'morebutton' ? 'right' : null"
							v-if="!['password', 'id'].includes(item.datatype) && item.tableShow"
						>
							<template #default="scope">
								<div v-if="item.datatype === 'text'">
									{{ formatter(scope.row[item.prop], item.datatype) }}
								</div>
								<div v-if="item.datatype === 'select'">
									{{ formatDataOptions(scope.row[item.prop], item.prop) }}
								</div>
								<div v-if="item.datatype === 'image'">
									<el-image
										style="width: 100px; height: 100px"
										:src="scope.row[item.prop]"
										@click.stop="onClickImage(scope.row[item.prop])"
									></el-image>
								</div>
								<div v-if="item.datatype === 'button'">
									<div v-if="item.prop === 'update'">
										<el-button type="primary" @click="onClickUpdate(scope.row)">修改</el-button>
									</div>
									<div v-else-if="item.prop === 'delete'">
										<el-button type="primary" @click="onClickDelete(scope.row)">删除</el-button>
									</div>
									<div v-else>
										<el-button type="primary" @click="item.fc(scope.row)">{{ item.label }}</el-button>
									</div>
								</div>
								<div v-if="item.datatype === 'morebutton'" style="display: flex">
									<div v-for="(v, k) in item.children" :key="k" :style="{ 'margin-right': item.buttonSpace }">
										<div v-if="(v.datatype === 'iconbutton' && !v.ifelse) || (v.datatype === 'iconbutton' && v.ifelse(scope.row))"  style="margin-right: 10px">
											<el-button type="primary" @click="v.fc(scope.row)" :icon="v.icon" />
										</div>
										<div v-if="(v.datatype === 'textbutton' && !v.ifelse) || (v.datatype === 'textbutton' && v.ifelse(scope.row))"  style="margin-right: 10px">
											<el-button type="primary" @click="v.fc(scope.row)">{{ v.label }}</el-button>
										</div>
									</div>
								</div>
								<div v-if="item.datatype === 'html'">
									<slot :name="item.prop" :row="scope.row"></slot>
								</div>
								<div v-if="item.datatype === 'tag'">
									<el-tag :type="item.color" :dark="item.dark">{{ formatter(scope.row[item.prop], item.datatype) }}</el-tag>
								</div>
								<div v-if="item.datatype === 'datetime'">
									{{ formatDatetimeStr(scope.row[item.prop]) }}
								</div>
								<div v-if="item.datatype === 'date'">
									{{ formatDateStr(scope.row[item.prop]) }}
								</div>
								<div v-if="item.datatype === 'time'">
									{{ formatTimeStr(scope.row[item.prop]) }}
								</div>
								<div v-if="item.datatype === 'number'">
									{{ scope.row[item.prop] }}
								</div>
							</template>
							<template #empty>
								<el-empty description="暂无数据" />
							</template>
						</el-table-column>
					</template>
				</el-table>
				<div style="margin: 10px">
					<el-pagination
						v-model:current-page="tableFormData.page.currentPage"
						:page-size="tableFormData.page.pageSize"
						:total="tableFormData.page.total"
						:page-sizes="[10, 20, 30, 50]"
						layout="total, sizes, prev, pager, next"
						@size-change="handleSizeChange"
						@current-change="handleCurrentChange"
					/>
				</div>
			</div>
		</div>
		

		<el-dialog
			v-model="tableFormData.formVisible"
			:title="`${tableFormData.formFlag === 1 ? '添加' : '修改'}${tableFormData.title}`"
			style="margin-top: 10px; padding: 10px"
		>
			<el-form
				:model="tableFormData.formData"
				:rules="tableFormData.formRules"
				ref="formRef"
				:label-width="getMaxHeadLabelWidths(tableFormData.tableHead, 16)"
				label-position="right"
			>
				<template v-for="item in tableFormData.tableHead" :key="item">
					<el-form-item :label="item.label" :prop="item.prop" v-if="!['button', 'morebutton', 'id'].includes(item.datatype) && item.formShow">
						<div v-if="item.datatype === 'text'">
							<el-input v-model="tableFormData.formData[item.prop]" :placeholder="`请输入${item.label}`" clearable></el-input>
						</div>
						<div v-if="item.datatype === 'select'">
							<el-select v-model="tableFormData.formData[item.prop]" :placeholder="`请选择${item.label}`" clearable>
								<el-option
									v-for="(v, k) in tableFormData[item.prop]"
									:key="v"
									:label="formatOption(v, 1, item.prop)"
									:value="formatOption(v, 2, item.prop)"
								></el-option>
							</el-select>
						</div>
						<div v-if="item.datatype === 'time'">
							<el-time-picker v-model="tableFormData.formData[item.prop]" type="date" :placeholder="`请选择${item.label}`" clearable></el-time-picker>
						</div>
						<div v-if="item.datatype === 'datetime'">
							<el-date-picker
								v-model="tableFormData.formData[item.prop]"
								type="datetime"
								:placeholder="`请选择${item.label}`"
								clearable
							></el-date-picker>
						</div>
						<div v-if="item.datatype === 'date'">
							<el-date-picker v-model="tableFormData.formData[item.prop]" :placeholder="`请选择${item.label}`" clearable></el-date-picker>
						</div>
						<div v-if="item.datatype === 'number'">
							<el-input-number v-model.number="tableFormData.formData[item.prop]" :placeholder="`请输入${item.label}`" clearable></el-input-number>
						</div>
						<div v-if="item.datatype === 'password'">
							<el-input v-model="tableFormData.formData[item.prop]" :placeholder="`请输入${item.label}`" clearable type="password"></el-input>
						</div>
						<div v-if="item.datatype === 'html'">
							<slot :name="`${item.prop}form`" :row="tableFormData.formData"></slot>
						</div>
						<div v-if="item.datatype === 'image'">
							<FileUploader mode="url" v-model="tableFormData.formData[item.prop]" accept=".png,.jpg" tip="请上传图片文件" style="margin-top: 20px" />
						</div>
					</el-form-item>
				</template>
			</el-form>
			<div style="margin: 10px; width: 100%; display: flex; justify-content: center">
				<div>
					<el-button type="primary" @click="onClickSubmitForm">确定</el-button>
					<el-button @click="onClickCancelForm">取消</el-button>
				</div>
			</div>
		</el-dialog>
		<viewImage v-model="imageViewVisiable" :imageUrl="currentImage" v-if="imageViewVisiable" style="z-index: 100000;"></viewImage>
	</div>
</template>

<script setup lang="ts">
import { defineModel, onBeforeUnmount, onMounted, ref } from 'vue';
import tableSearch from '/@/components/tableForm/components/search/index.vue';
import viewImage from '/@/components/tableForm/components/viewImage/index.vue';
import { table } from 'console';
import FileUploader from '/@/components/fileupdate/index.vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { formatDateStr, formatDatetimeStr, formatTimeStr } from '/@/utils/formatTime';
import { emit } from 'process';
import { da } from 'element-plus/es/locale';
const tableFormData = defineModel<any>();
const vmProps = defineProps(['formatter','export']);
const vmEmits = defineEmits(['updateData', 'createData', 'deleteData', 'getData', 'beforeUpdateData', 'beforeCreateData', 'exportData']);
const formRef = ref<any>();
const imageViewVisiable = ref(false);
const currentImage = ref('');
//获取表头宽度
function getTableColumnWidths(tableHead: any[], tableData: any[], prop: string, font: number = 16): number {
	//如果类型是图片返回150
	let column = tableHead.find((item) => item.prop === prop);
	if (column.datatype === 'image') {
		return 200;
	}
	const label = column.label;
	// 获取列标题的宽度
	let maxWidth = getTextWidthUsingDiv(label, font);

	// 获取每行对应列的最大宽度
	tableData.forEach((row) => {
		const cellText = row[prop] as string;
		const cellWidth = getTextWidthUsingDiv(cellText, font);
		maxWidth = Math.max(maxWidth, cellWidth);
	});
	return maxWidth;
}

function getMaxHeadLabelWidths(tableHead: any[], font: number = 14): number {
	let maxWidths = tableHead.map((column) => {
		const label = column.label;
		const maxWidth = getTextWidthUsingDiv(label, font);
		return maxWidth;
	});
	return Math.max(...maxWidths) + 20;
}

function getTextWidthUsingDiv(text: string, font: number) {
	// 创建一个隐藏的 div 元素
	var div = document.createElement('div');
	// 将 div 元素添加到 body 中，隐藏它
	div.style.position = 'absolute';
	div.style.visibility = 'hidden';
	div.style.whiteSpace = 'nowrap'; // 防止换行
	div.style.font = `${font}px Arial`; // 设置字体
	div.style.display = 'inline-block'; // 让它作为文本块显示
	// 设置 div 的文本
	div.textContent = text;
	// 将 div 添加到 body
	document.body.appendChild(div);
	// 获取文本宽度
	var width = div.offsetWidth;
	// 移除 div 元素
	document.body.removeChild(div);
	return width;
}

//1是显示label，2是显示value
function formatOption(data: any, type: number, prop: string) {
	const formData = tableFormData.value;
	if (!formData[prop] || !formData.optionsName || !formData.optionsValue) {
		return '-';
	}
	if (type === 1) {
		const optionNameKey = formData.optionsName[prop];
		return optionNameKey ? data[optionNameKey] : '-';
	} else {
		const optionValueKey = formData.optionsValue[prop];
		return optionValueKey ? data[optionValueKey] : '-';
	}
}

function formatDataOptions(data: any, prop: string) {
	const formData = tableFormData.value;
	let idname = formData.optionsValue[prop] || 'id';
	let valuename = formData.optionsName[prop] || 'label';
	return formData[prop].find((item: any) => `${item[idname]}` === `${data}`)[valuename] || '-';
}

function onClickUpdate(row: any) {
	tableFormData.value.formData = { ...row };
	vmEmits('beforeUpdateData', tableFormData.value.formData);
	tableFormData.value.formFlag = 2;
	tableFormData.value.formVisible = true;
}
function onClickAdd() {
	vmEmits('beforeCreateData', tableFormData.value.formData);
	tableFormData.value.formFlag = 1;
	tableFormData.value.formVisible = true;
	tableFormData.value.formData = {};
}

function onClickDelete(row: any) {
	ElMessageBox.confirm('确定删除吗？', '提示', {
		confirmButtonText: '确定',
		cancelButtonText: '取消',
		type: 'warning',
	}).then(() => {
		vmEmits('deleteData', row);
	});
}

function onClickSubmitForm() {
	formRef.value.validate((valid: any) => {
		if (valid) {
			if (tableFormData.value.formFlag === 1) {
				vmEmits('createData', tableFormData.value.formData);
			}
			if (tableFormData.value.formFlag === 2) {
				vmEmits('updateData', tableFormData.value.formData);
			}
			cleanRulesFormRef();
			tableFormData.value.formData = {};
			onGetData();
			tableFormData.value.formFlag = 0;
			tableFormData.value.isLoading = false;
			tableFormData.value.formVisible = false;
		}
	});
}

function onClickCancelForm() {
	tableFormData.value.formData = {};
	tableFormData.value.formVisible = false;
}

function onClickImage(row: string) {
	currentImage.value = row;
	imageViewVisiable.value = true;
}

function onGetData() {
	tableFormData.value.isLoading = true;
	vmEmits('getData');
}

const handleSizeChange = (newSize: number) => {
	tableFormData.value.page.pageSize = newSize;
};

const handleCurrentChange = (newPage: number) => {
	tableFormData.value.page.currentPage = newPage;
	onGetData();
};

const cleanRulesFormRef = () => {
	formRef.value?.resetFields();
};

// 初始化默认验证规则
const initDefaultRules = () => {
	const defaultRules: Record<string, any> = {};

	tableFormData.value.tableHead.forEach((item: any) => {
		if (!tableFormData.value.formRules) {
			tableFormData.value.formRules = {};
		}
		if (item.formShow && !tableFormData.value.formRules[item.prop]) {
			switch (item.datatype) {
				case 'text':
				case 'password':
					defaultRules[item.prop] = [{ required: true, message: `${item.label}不能为空`, trigger: 'blur' }];
					break;

				case 'select':
					defaultRules[item.prop] = [{ required: true, message: `请选择${item.label}`, trigger: 'change' }];
					break;

				case 'number':
					defaultRules[item.prop] = [
						{
							type: 'number',
							message: '必须为数字类型',
							trigger: 'blur',
							transform: (value: string) => Number(value),
						},
						{
							validator: (_: any, value: number) => value >= 0,
							message: '不能小于0',
							trigger: 'blur',
						},
					];
					break;

				case 'email':
					defaultRules[item.prop] = [
						{
							type: 'email',
							message: '邮箱格式不正确',
							trigger: 'blur',
						},
					];
					break;

				case 'datetime':
				case 'date':
				case 'time':
					defaultRules[item.prop] = [
						{
							type: 'date',
							required: true,
							message: `请选择${item.label}`,
							trigger: 'change',
						},
					];
					break;
			}
		}
	});

	// 合并规则（自定义规则优先）
	tableFormData.value.formRules = {
		...defaultRules,
		...tableFormData.value.formRules,
	};
};

// 导出数据方法
const onClickExport = () => {
	ElMessageBox({
		title: '导出数据',
		message: '请选择导出格式',
		showCancelButton: true,
		confirmButtonText: 'Excel',
		cancelButtonText: 'CSV',
		beforeClose: (action, instance, done) => {
			if (action === 'confirm') {
				exportData('xlsx');
				done();
			} else if (action === 'cancel') {
				exportData('csv');
				done();
			}
		},
	});
};

// 实际导出逻辑
const exportData = (format: string) => {
	tableFormData.value.isLoading = true;

	// 准备导出的数据
	const exportColumns = tableFormData.value.tableHead
		.filter((item: any) => !['button', 'morebutton', 'id'].includes(item.datatype) && item.tableShow)
		.map((item: any) => ({
			label: item.label,
			prop: item.prop,
			formatter: (value: any) => {
				if (item.datatype === 'select') return formatDataOptions(value, item.prop);
				if (item.datatype === 'datetime') return formatDatetimeStr(value);
				if (item.datatype === 'date') return formatDateStr(value);
				if (item.datatype === 'time') return formatTimeStr(value);
				return value;
			},
		}));
	vmEmits('exportData', (data: any) => {
		let tableData = data || tableFormData.value.tableData;
		const exportData = tableData.map((row: any) => {
			const obj: any = {};
			exportColumns.forEach((col: any) => {
				obj[col.label] = col.formatter ? col.formatter(row[col.prop]) : row[col.prop];
			});
			return obj;
		});

		// 使用第三方库实现导出
		import('xlsx')
			.then((XLSX) => {
				const ws = XLSX.utils.json_to_sheet(exportData);
				const wb = XLSX.utils.book_new();
				XLSX.utils.book_append_sheet(wb, ws, 'Sheet1');

				const fileName = `${tableFormData.value.title}_${new Date().toISOString().slice(0, 10)}`;

				if (format === 'xlsx') {
					XLSX.writeFile(wb, `${fileName}.xlsx`);
				} else {
					XLSX.writeFile(wb, `${fileName}.csv`, { bookType: 'csv' });
				}

				tableFormData.value.isLoading = false;
			})
			.catch(() => {
				ElMessage.error('导出失败，请稍后重试');
				tableFormData.value.isLoading = false;
			});
	});
};

// 在组件挂载时初始化
onMounted(() => {
	initDefaultRules();
});

onBeforeUnmount(() => {
	cleanRulesFormRef();
});
</script>

<style scoped></style>
