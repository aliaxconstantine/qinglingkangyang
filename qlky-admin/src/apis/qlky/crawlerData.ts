import { postApi } from "../apis";

export const getCrawlerDataByCrawlerId = (data: any) => {
    return postApi({
      url: "/getCrawlerData",
      requestParams: data,
      description: "获取爬虫数据信息",
    });
};


export const addOrUpdateCrawlerData = (data: any) => {
    return postApi({
      url: "/updateOrSaveData",
      requestParams: data,
      description: "修改或者新增爬虫数据信息",
    });
};

export const deleteCrawlerData = (data: any) => {
    return postApi({
      url: "/deleteData",
      requestParams: data,
      description: "删除爬虫数据信息",
    });
};