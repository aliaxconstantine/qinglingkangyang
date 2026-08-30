<template>
	<div class="file-uploader">
		<el-upload
			action="#"
			:auto-upload="false"
			:show-file-list="false"
			:on-change="handleFileChange"
			:accept="accept"
			:multiple="multiple"
			:disabled="uploading"
		>
			<el-button type="primary" :loading="uploading">
				{{ uploading ? '上传中...' : '选择文件' }}
			</el-button>

			<template #tip>
				<!-- 文件预览/显示区域 -->
				<div class="preview-area">
					<!-- 图片模式预览 -->
					<div v-if="mode === 'url' && model" class="image-preview">
						<img :src="model" alt="预览图片" />
						<div class="preview-mask">
							<el-button type="danger" size="small" @click.stop="clearFile"> 清除 </el-button>
						</div>
					</div>

					<!-- 文件模式显示 -->
					<div v-else-if="model" class="file-info">
						<span v-if="mode === 'file'"> {{ model.name }} ({{ formatFileSize(model.size) }}) </span>
						<span v-else> 已选择文件 </span>
						<el-button type="danger" size="small" @click.stop="clearFile" class="ml-2"> 清除 </el-button>
					</div>

					<div v-else class="upload-tip">
						{{ tip || defaultTip }}
					</div>
				</div>
			</template>
		</el-upload>
	</div>
</template>

<script lang="ts" setup>
import { ref, computed } from 'vue';
import { ElMessage, UploadFile, type UploadFiles } from 'element-plus';
import { uploadFile as uploadFileApi } from '/@/apis/qlky/home';

const props = defineProps({
	// 模式：file-返回文件对象  url-返回图片路径
	mode: {
		type: String as () => 'file' | 'url',
		default: 'file',
		validator: (v: string) => ['file', 'url'].includes(v),
	},
	accept: {
		type: String,
		default: '*',
	},
	multiple: {
		type: Boolean,
		default: false,
	},
	tip: {
		type: String,
		default: '',
	},
	// 上传API地址（仅在url模式需要）
	uploadUrl: {
		type: String,
		default: '',
	},
});

const emit = defineEmits(['upload-success', 'upload-error']);

// 双向绑定值
const model = defineModel<any>({ required: false });

// 状态
const uploading = ref(false);
const defaultTip = computed(() => (props.mode === 'url' ? '请选择图片文件（支持格式：JPEG/PNG）' : '请选择文件'));

// 文件大小格式化
const formatFileSize = (bytes: number): string => {
	if (bytes === 0) return '0 Bytes';
	const k = 1024;
	const sizes = ['Bytes', 'KB', 'MB', 'GB'];
	const i = Math.floor(Math.log(bytes) / Math.log(k));
	return `${(bytes / Math.pow(k, i)).toFixed(2)} ${sizes[i]}`;
};

// 处理文件变化
const handleFileChange = async (uploadFile: UploadFile, uploadFiles: UploadFiles) => {
  console.log('handleFileChange', uploadFile, uploadFiles);
	if (!uploadFiles.length) return;

	const rawFile = uploadFiles[0].raw;
	if (!rawFile) return;

	// 图片模式处理
	if (props.mode === 'url') {
		// 验证图片类型
		if (!rawFile.type.startsWith('image/')) {
			ElMessage.error('请选择有效的图片文件');
			return;
		}

		uploading.value = true;
		uploadFileApi(rawFile, 'image')
			.then((res) => {
        if(res.code  === 8888){
          model.value = res.data?.url
        }
      })
			.catch((err: any) => {
				ElMessage.error(`上传失败：${err.message || '服务器错误'}`);
				emit('upload-error', err);
			})
			.finally(() => {
				uploading.value = false;
			});
	} else {
		// 文件模式直接绑定
		model.value = rawFile;
	}
};

// 清除文件
const clearFile = () => {
	model.value = null;
	uploading.value = false;
};
</script>

<style scoped>
.file-uploader {
	width: 100%;
	max-width: 600px;
}

.preview-area {
	margin-top: 12px;
}

.image-preview {
	position: relative;
	width: 200px;
	height: 200px;
	border: 1px solid var(--el-border-color);
	border-radius: 4px;
	overflow: hidden;

	img {
		width: 100%;
		height: 100%;
		object-fit: contain;
	}

	.preview-mask {
		position: absolute;
		bottom: 0;
		left: 0;
		right: 0;
		padding: 8px;
		background: rgba(0, 0, 0, 0.5);
		display: flex;
		justify-content: flex-end;
	}
}

.file-info {
	display: flex;
	align-items: center;
	color: var(--el-text-color-regular);
	font-size: 14px;
}

.upload-tip {
	color: var(--el-text-color-secondary);
	font-size: 12px;
	margin-top: 8px;
}

.ml-2 {
	margin-left: 8px;
}
</style>
