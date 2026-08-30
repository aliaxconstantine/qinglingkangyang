import { uploadFileApi, postApi } from "../apis";

/**
 * 提交爬虫（上传文件+创建/更新爬虫）
 * @param file 上传的文件
 * @param crawlerEntity 爬虫数据
 */
export const saveCrawler = (file: File, crawlerEntity: any) => {
  const formData = new FormData();
  formData.append('file', file);
  Object.keys(crawlerEntity).forEach(key => {
    if(crawlerEntity[key] != null && crawlerEntity[key] != undefined ){
      formData.append(key, crawlerEntity[key]);
    }
  });

  return uploadFileApi({
    url: '/submitCrawler',
    requestParams: formData,
    description: '提交爬虫程序'
  });
};

/**
 * 删除爬虫
 * @param crawlerId 爬虫ID
 * @param crawlerProgramPath 爬虫程序路径（用于物理删除文件）
 */
export const deleteCrawler = (crawlerId: number, crawlerProgramPath: string) => {
  return postApi({
    url: '/deleteCrawler',
    requestParams: { crawlerId: crawlerId, crawlerProgramPath: crawlerProgramPath },
    description: '删除爬虫'
  });
};

/**
 * 更新爬虫路径
 * @param crawlerId 爬虫ID
 * @param newPath 新路径
 */
export const updateCrawlerPath = (crawlerId: number, newPath: string) => {
  return postApi({
    url: '/updateCrawler',
    requestParams: { crawlerId: crawlerId, crawlerProgramPath: newPath },
    description: '更新爬虫路径'
  });
};

/**
 * 更新爬虫信息（不涉及文件操作）
 * @param crawlerEntity 爬虫实体数据
 */
export const updateCrawlerInfo = (crawlerEntity: any) => {
  return postApi({
    url: '/updateCrawlerInfo',
    requestParams: crawlerEntity,
    description: '更新爬虫信息'
  });
};

/**
 * 获取爬虫列表
 * @param params 查询参数
 */
export const getCrawlerList = (params?: any) => {
  return postApi({
    url: '/getCrawlerList', // 注意这里用的是你示例中的SystemList
    requestParams: params,
    description: '获取爬虫列表'
  });
};