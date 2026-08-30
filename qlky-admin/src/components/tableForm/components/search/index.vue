<template>
	<div style="display: flex">
		<el-form inline :model="tableFormData.searchForm" label-width="80px">
      <el-form-item
        v-for="(item, index) in tableFormData.searchHead"
        :key="index"
        :label="item.label"
        :prop="item.prop"
        :rules="item.rules"
      >
        <div v-if="item.datatype === 'text'">
          <el-input
            v-model="tableFormData.searchForm[item.prop]"
            :placeholder="item.placeholder"
            :clearable="item.clearable"
            :disabled="item.disabled"
            :readonly="item.readonly"
          />
        </div>
        <div v-else-if="item.datatype === 'date'">
          <el-date-picker
            v-model="tableFormData.searchForm[item.prop]"
            :type="item.timetype"
            :placeholder="item.placeholder"
            :clearable="item.clearable"
            :disabled="item.disabled"
            :readonly="item.readonly"
          />
        </div>
        <div v-else>
          <el-select v-model="tableFormData.searchForm[item.prop]" :placeholder="`请选择${item.label}`" clearable>
							<el-option v-for="(v, k) in tableFormData[item.prop]" :key="k" :label="formatOption(v, 1)" :value="formatOption(v, 2)"></el-option>
						</el-select>
        </div>
      </el-form-item>
			<el-form-item>
				<div style="display: flex">
					<el-button type="primary" @click="onSearch">搜索</el-button>
          <el-button type="primary" @click="onResetSearch">重置</el-button>
				</div>
			</el-form-item>
		</el-form>
	</div>
</template>

<script setup lang="ts">
import { defineModel, onBeforeUnmount, ref } from 'vue';
const tableFormData = defineModel<any>();
const vmProps = defineProps(['formatter']);
const vmEmits = defineEmits(['updateData', 'createData', 'deleteData', 'getData', 'beforeUpdateData', 'beforeCreateData']);
function onResetSearch() {
  tableFormData.value.searchForm = {};
}

function onSearch() {
  vmEmits('getData', tableFormData.value.searchForm);
}
function formatOption(data: any, type: number) {
	const formData = tableFormData.value;
	const prop = data.prop;
	if (!formData[prop] || !formData.optionsName || !formData.optionsValue) {
		return '-';
	}
	if (type === 1) {
		const optionNameKey = formData.optionsName[prop];
		return optionNameKey ? formData[prop][optionNameKey] : '-';
	} else {
		const optionValueKey = formData.optionsValue[prop];
		return optionValueKey ? formData[prop][optionValueKey] : '-';
	}
}

</script>

<style scoped></style>
