import { uploadFileApi, postApi } from "../apis";

/**
 * 删除爬虫字段信息
 * @param crawlerId 爬虫ID
 * @param crawlerProgramPath 爬虫程序路径（用于物理删除文件）
 */
export const deleteCrawlerField = (fieldId : Number) => {
  return postApi({
    url: '/deleteCrawlerField',
    requestParams: { fieldId: fieldId },
    description: '删除爬虫字段ID'
  });
};


/**
 * 更新爬虫字段信息
 * @param crawlerFieldEntity 爬虫字段实体数据
 */
export const saveCrawlerField = (crawlerFieldEntity: any) => {
  return postApi({
    url: '/saveCrawlerField',
    requestParams: crawlerFieldEntity,
    description: '更新爬虫字段信息'
  });
};

/**
 * 获取爬虫字段列表
 * @param params 查询参数
 */
export const getCrawlerFieldList = (params?: any) => {
  console.log(params);
  return postApi({
    url: '/getCrawlerFieldList', // 注意这里用的是你示例中的SystemList
    requestParams: params,
    description: '获取爬虫字段列表'
  });
};