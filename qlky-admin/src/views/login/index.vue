<template>
	<div class="login-container">
		<el-card class="login-box" shadow="always">
			<h2 class="title">秦岭康养大数据管理平台</h2>
			<el-form :model="loginForm" ref="formRef" class="login-form" @submit.native.prevent="onSubmit" :rules="formRules">
				<el-form-item label="用户名" :label-width="'70px'" prop="username">
					<el-input v-model="loginForm.username" placeholder="请输入用户名" autocomplete="off"></el-input>
				</el-form-item>

				<el-form-item label="密码" :label-width="'70px'" prop="password">
					<el-input v-model="loginForm.password" type="password" placeholder="请输入密码" autocomplete="off"></el-input>
				</el-form-item>

				<el-form-item>
					<el-button type="primary" class="submit-btn" :loading="isLoading" @click="onSubmit">登录</el-button>
				</el-form-item>
			</el-form>
		</el-card>
	</div>

	<el-dialog v-model="showVerify" title="安全验证" width="400px">
		<SlideVerify @verify-success="onVerifySuccess" v-if="showVerify" />
	</el-dialog>
</template>

<script setup lang="ts">
import { ref, computed, reactive } from 'vue';
import { type ElForm, ElFormItem, ElInput, ElButton, ElCard, ElMessage } from 'element-plus';
import { userLoginByAccount } from '/@/apis/login';
import { useRoute, useRouter } from 'vue-router';
import { useI18n } from 'vue-i18n';
import { storeToRefs } from 'pinia';
import { useThemeConfig } from '/@/stores/themeConfig';
import Cookies from 'js-cookie';
import { NextLoading } from '/@/utils/loading';
import { initFrontEndControlRoutes } from '/@/router/frontEnd';
import { initBackEndControlRoutes } from '/@/router/backEnd';
import { Session } from '/@/utils/storage';
import SlideVerify from '/@/views/login/components/SlideVerify.vue';
import { formatAxis } from '/@/utils/formatTime';
const { t } = useI18n();
const storesThemeConfig = useThemeConfig();
const { themeConfig } = storeToRefs(storesThemeConfig);
const route = useRoute();
const router = useRouter();
const showVerify = ref(false);
const isVerified = ref(false);
// 时间获取
const currentTime = computed(() => {
	return formatAxis(new Date());
});
const state = reactive({
	isShowPassword: false,
	loading: {
		signIn: false,
	},
});
interface LoginForm {
	username: string;
	password: string;
}
const formRef = ref<any>(null);
const loginForm = ref<LoginForm>({
	username: '',
	password: '',
});

const isLoading = ref(false);

// 表单验证规则
const formRules = ref({
	username: [
		{ required: true, message: '请输入用户名', trigger: 'blur' },
		{ min: 3, max: 15, message: '用户名长度在 3 到 15 个字符之间', trigger: 'blur' },
	],
	password: [
		{ required: true, message: '请输入密码', trigger: 'blur' },
		{ min: 6, max: 20, message: '密码长度在 6 到 20 个字符之间', trigger: 'blur' },
	],
});

const onVerifySuccess = () => {
	showVerify.value = false;
	isVerified.value = true;
	if (isVerified.value) {
		onSubmit();
	}
};

const onSubmit = () => {
	if (!isVerified.value) {
		showVerify.value = true;
		return;
	}
	isVerified.value = false;
	isLoading.value = true;
	const formInstance = formRef.value;
	formInstance.validate((isValid: boolean) => {
		if (isValid) {
			// 校验通过，模拟登录请求
			userLoginByAccount({ account: loginForm.value.username, password: loginForm.value.password }).then((res: any) => {
				if (res.code == 8888) {
					ElMessage.success(res.msg);
					onSignIn();
				} else {
					isLoading.value = false;
				}
			});
		} else {
			isLoading.value = false;
		}
	});
};

// 登录
const onSignIn = async () => {
	state.loading.signIn = true;
	// 存储 token 到浏览器缓存
	Session.set('token', Math.random().toString(36).substr(0));
	Cookies.set('userName', '秦岭康养管理员');
	if (!themeConfig.value.isRequestRoutes) {
		// 前端控制路由，2、请注意执行顺序
		const isNoPower = await initFrontEndControlRoutes();
		signInSuccess(isNoPower);
	} else {
		// 模拟后端控制路由，isRequestRoutes 为 true，则开启后端控制路由
		// 添加完动态路由，再进行 router 跳转，否则可能报错 No match found for location with path "/"
		const isNoPower = await initBackEndControlRoutes();
		// 执行完 initBackEndControlRoutes，再执行 signInSuccess
		signInSuccess(isNoPower);
	}
};
// 登录成功后的跳转
const signInSuccess = (isNoPower: boolean | undefined) => {
	if (isNoPower) {
		ElMessage.warning('抱歉，您没有登录权限');
		Session.clear();
	} else {
		// 初始化登录成功时间问候语
		let currentTimeInfo = currentTime.value;
		// 登录成功，跳到转首页
		// 如果是复制粘贴的路径，非首页/登录页，那么登录成功后重定向到对应的路径中
		if (route.query?.redirect) {
			router.push({
				path: <string>route.query?.redirect,
				query: Object.keys(<string>route.query?.params).length > 0 ? JSON.parse(<string>route.query?.params) : '',
			});
		} else {
			router.push('/');
		}
		// 登录成功提示
		const signInText = t('message.signInText');
		ElMessage.success(`${currentTimeInfo}，管理员`);
		// 添加 loading，防止第一次进入界面时出现短暂空白
		NextLoading.start();
	}
	state.loading.signIn = false;
};
</script>

<style scoped>
.login-container {
	display: flex;
	justify-content: center;
	align-items: center;
	height: 100vh;
	background-color: #f4f7fc;
}

.login-box {
	width: 100%;
	max-width: 400px;
	padding: 40px;
}

.title {
	text-align: center;
	font-size: 24px;
	font-weight: 600;
	margin-bottom: 20px;
	color: #4e73df;
}

.login-form {
	display: flex;
	flex-direction: column;
}

.submit-btn {
	width: 100%;
}
</style>
