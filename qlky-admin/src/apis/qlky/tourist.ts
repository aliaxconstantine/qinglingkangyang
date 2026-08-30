//获取特产全部数据

import { postApi } from '../apis';

// /pageData
export const getSpecialtyAll = () => {
	return postApi({
		url: '/pageData',
		requestParams: {
            crawlerId: 3,
			pageSize: 10000,
			currentPage: 1
        },
		description: '获取特产信息',
	});
};

export const getTourismAll = () => {
	return postApi({
		url: '/pageData',
		requestParams: { crawlerId: 2, pageSize: 10000, currentPage: 1 },
		description: 'Get live tourism data',
	});
};

