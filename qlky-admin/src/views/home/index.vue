<template>
	<div class="dashboard-container">
		<!-- 数据概览 - 根据数据结构优化 -->
		<section class="overview">
			<h2><i class="icon-server"></i> 系统实时状态概览</h2>
			<div class="cards">
				<!-- CPU状态卡片 -->
				<div class="card cpu-card">
					<div class="card-header">
						<i class="icon-cpu"></i>
						<h3>处理器状态</h3>
					</div>
					<div class="card-body">
						<div class="metric">
							<span class="label">核心数</span>
							<div class="value">
								<span class="highlight">{{ systemInfo.cpuCores }} 核心</span>
							</div>
						</div>
					</div>
				</div>

				<!-- 内存使用卡片 -->
				<div class="card memory-card">
					<div class="card-header">
						<i class="icon-memory"></i>
						<h3>内存使用</h3>
					</div>
					<div class="card-body">
						<div class="progress-bar">
							<div class="progress" style="width: calc(44 / 260 * 100%)"></div>
						</div>
						<div class="metric-group">
							<span>空闲：<strong>215 GB</strong></span>
							<span>总量：<strong>260 GB</strong></span>
						</div>
					</div>
				</div>

				<!-- 磁盘状态卡片 -->
				<div class="card disk-card">
					<div class="card-header">
						<i class="icon-disk"></i>
						<h3>存储空间</h3>
					</div>
					<div class="card-body">
						<div class="progress-bar">
							<div class="progress" style="width: calc((327-15) / 327 * 100%)"></div>
						</div>
						<div class="metric-group">
							<span>可用：<strong>15 GB</strong></span>
							<span>总量：<strong>327 GB</strong></span>
						</div>
					</div>
				</div>

				<!-- 系统信息卡片 -->
				<div class="card system-card">
					<div class="card-header">
						<i class="icon-system"></i>
						<h3>系统信息</h3>
					</div>
					<div class="card-body">
						<div class="system-info" style="font-size: 12px">
							<p style="font-size: 12px"><span class="label" style="font-size: 12px">操作系统：</span>Windows 11 (10.0)</p>
							<p style="font-size: 12px">
								<span class="label" style="font-size: 12px">运行时间：</span><time style="font-size: 12px">{{systemInfo.uptime}}</time>
							</p>
							<p style="font-size: 12px">
								<span class="label" style="font-size: 12px">服务状态：</span><span class="status-dot healthy" style="font-size: 12px"></span> 正常
							</p>
						</div>
					</div>
				</div>
			</div>
		</section>
		<!-- 快速操作入口 -->
		<section class="quick-actions">
			<h2>快速操作</h2>
			<div class="actions">
				<router-link to="/crawler/crawler-task" class="action-link" v-track:nav="'爬虫管理'">管理爬虫任务</router-link>
				<router-link to="/" class="action-link">查看系统报告</router-link>
				<router-link to="/dashboard" class="action-link">预览大屏</router-link>
				<router-link to="/" class="action-link">性能监控</router-link>
			</div>
		</section>

		<!-- 系统动态 -->
		<section class="system-notifications">
			<h2>系统动态</h2>
			<ul>
				<li v-for="item in systemInfo.crawlerSystemMessageList">
					{{ item.message }}
				</li>
				<li>新爬虫任务启动成功</li>
				<li>数据库优化完成</li>
				<li>系统负载恢复正常</li>
			</ul>
		</section>
	</div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { getSystemInfo } from '/@/apis/qlky/home';
const systemInfo = ref<any>({
	crawlerSystemMessageList: [],
});

function getData() {
	getSystemInfo().then((res) => {
		if (res.code === 8888) {
			systemInfo.value = res.data;
		}
	});
}

onMounted(() => {
	getData();
});
</script>

<style lang="scss" scoped>
.dashboard-container {
	font-family: 'Roboto', sans-serif;
	color: #2c3e50; /* 深色字体 */
	padding: 20px;
	background-color: #f5f7fa; /* 浅灰色背景 */
	min-height: 100vh;
	display: flex;
	flex-direction: column;
}

h2 {
	font-size: 24px;
	margin-bottom: 20px;
	font-weight: 600;
	color: #2c3e50; /* 深色字体 */
}

.overview {
	margin-bottom: 40px;

	.cards {
		display: grid;
		grid-template-columns: repeat(4, 1fr);
		gap: 20px;

		.card {
			background: linear-gradient(135deg, rgba(99, 183, 255, 0.3), rgba(206, 255, 227, 0.3)); /* 浅色渐变背景 */
			padding: 25px;
			border-radius: 10px;
			text-align: center;
			box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1); /* 浅阴影效果 */
			backdrop-filter: blur(12px); /* 透明背景效果 */
			border: 1px solid #e3e3e3; /* 细边框 */
			transition: transform 0.3s ease-in-out;

			h3 {
				font-size: 18px;
				color: #2c3e50; /* 深色字体 */
				margin-bottom: 10px;
			}

			p {
				font-size: 32px;
				font-weight: bold;
				color: #2c3e50; /* 深色字体 */
				margin: 0;
			}

			&:hover {
				transform: scale(1.05);
			}
		}
	}
}

.quick-actions {
	margin-bottom: 40px;

	.actions {
		display: grid;
		grid-template-columns: repeat(4, 1fr);
		gap: 20px;

		.action-link {
			background: rgba(99, 183, 255, 0.6); /* 半透明浅蓝色 */
			color: rgb(9, 33, 78);
			padding: 20px;
			text-align: center;
			border-radius: 10px;
			text-decoration: none;
			font-size: 16px;
			display: block;
			box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
			transition: background 0.3s, transform 0.3s;
			cursor: pointer; /* 设置为点击手型 */

			&:hover {
				background: rgba(99, 183, 255, 0.8);
				transform: translateY(-5px);
			}
		}
	}
}

.system-notifications {
	margin-bottom: 20px;

	ul {
		list-style-type: none;
		padding: 0;

		li {
			padding: 12px;
			background-color: rgba(99, 183, 255, 0.15); /* 半透明浅蓝色背景 */
			margin-bottom: 10px;
			border-radius: 8px;
			font-size: 16px;
			font-weight: 500;
			color: #2c3e50; /* 深色字体 */
			backdrop-filter: blur(10px);
			transition: background 0.3s;

			&:hover {
				background-color: rgba(99, 183, 255, 0.25);
			}
		}
	}
}
/* 卡片通用样式 */
.card {
	background: rgba(255, 255, 255, 0.1);
	border-radius: 8px;
	padding: 1.5rem;
	backdrop-filter: blur(5px);
}

/* 状态指示元素 */
.usage-circle {
	display: inline-block;
	width: 40px;
	height: 40px;
	border-radius: 50%;
	background: conic-gradient(#4caf50 var(--percent), #2c3e50 var(--percent) 100%);
	line-height: 40px;
	text-align: center;
	color: white;
}

/* 进度条样式 */
.progress-bar {
	height: 8px;
	background: #34495e;
	border-radius: 4px;
	margin: 12px 0;
}
.progress {
	height: 100%;
	background: #3498db;
	border-radius: 4px;
	transition: width 0.3s ease;
}

/* 系统状态指示 */
.status-dot {
	display: inline-block;
	width: 10px;
	height: 10px;
	border-radius: 50%;
	margin-right: 6px;
}
.status-dot.healthy {
	background: #2ecc71;
	box-shadow: 0 0 8px #2ecc71;
}
/* 设置鼠标样式 */
a,
.action-link {
	cursor: pointer; /* 鼠标变为手形 */
}
</style>
