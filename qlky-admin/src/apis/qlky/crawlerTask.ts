// /src/apis/qlky/task.ts
import { uploadFileApi, postApi } from '../apis';

/**
 * 获取单个爬虫任务
 * @param crawlerId 爬虫ID
 */
export const getCrawlerTask = (crawlerId: number) => {
	return postApi({
		url: '/getCrawlerTask',
		requestParams: { crawlerId },
		description: '获取爬虫任务',
	});
};

/**
 * 获取爬虫任务列表
 * @param params 查询参数
 */
export const getTaskList = (params: any) => {
	return postApi({
		url: '/getCrawlerTaskList',
		requestParams: params,
		description: '获取爬虫任务列表',
	});
};

/**
 * 创建/更新爬虫任务
 * @param task 爬虫任务数据
 */
export const saveTask = (task: any) => {
	return postApi({
		url: '/saveCrawlerTask',
		requestParams: task,
		description: '保存爬虫任务',
	});
};

/**
 * 删除爬虫任务
 * @param taskId 任务ID
 */
export const deleteTask = (taskId: any) => {
	return postApi({
		url: '/deleteCrawlerTask',
		requestParams: { taskId },
		description: '删除爬虫任务',
	});
};

/**
 * 执行爬虫任务
 * @param crawlerId 爬虫ID
 */
export const executeCrawlerTask = (crawlerId: number, taskId: number) => {
	return postApi({
		url: '/executeCrawlerTask',
		requestParams: { crawlerId, taskId },
		description: '执行爬虫任务',
	});
};

/**
 * 取消任务
 */
export const cancelCrawlerTask = (crawlerId: number, taskId: number) => {
	return postApi({
		url: '/cancelCrawlerTask',
		requestParams: { crawlerId, taskId },
		description: '取消任务',
	});
};


export const deleteCrawlerData = (taskId: number) => {
	return postApi({
		url: '/deleteCrawlerData',
		requestParams: {
			taskId
		},
		description: '删除爬虫数据',
	});
};