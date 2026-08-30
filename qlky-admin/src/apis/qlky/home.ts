
// /src/apis/qlky/task.ts
import { uploadFileApi, postApi } from "../apis";


/**
 * 获取单个爬虫任务
 * @param crawlerId 爬虫ID
 */
export const getSystemInfo = () => {
  return postApi({
    url: '/getSystemInfo',
    requestParams: {  },
    description: '获取系统信息'
  });
};


export const uploadFile = (file: File, category: string) => {
  const formData = new FormData();
  formData.append('file', file);
  formData.append('category', category);
  return uploadFileApi({
    url: '/uploadFile',
    requestParams: formData,
    description: '上传文件'
  });
};
