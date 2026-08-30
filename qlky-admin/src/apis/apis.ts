import instance from "/@/apis/request";
import type { RetRequestResult } from "./type/RetRequestResult";
import { type AxiosResponse } from "axios";
import { da } from "element-plus/es/locale";


//将请求数据中的没用数据过滤掉
export const filterPageData = (data: any): any => {
  if (data) {
    data.total = undefined;
  }
  return data;
};

export const postApi = async <T>(
  data: RetRequestResult<any>
): Promise<ApiResponse<T>> => {
  // 发起 POST 请求
  const response: AxiosResponse<ApiResponse<T>> = await instance({
    method: "post",
    url: data.url, // 拼接 URL
    data: filterPageData(data.requestParams), // 将参数作为 body 数据发送
  });
  // 返回响应数据
  return response.data;
};

export const uploadFileApi = async <T>(
  data: RetRequestResult<FormData>
): Promise<ApiResponse<T>> => {
  const response: AxiosResponse<ApiResponse<T>> = await instance({
    method: "post",
    url: data.url,
    data: filterPageData(data.requestParams), // 将参数作为 body 数据发送
    headers: {
      "Content-Type": "multipart/form-data",
    },
  });
  return response.data;
};

export const paramApi = async <T>(
  data: RetRequestResult<any>
): Promise<ApiResponse<T>> => {
  // 发起 GET 请求，使用 params 将 URL 查询参数传递给 API
  const response: AxiosResponse<ApiResponse<T>> = await instance({
    method: "post", // 使用 GET 方法
    url: data.url, // 拼接 URL
    data: filterPageData(data.requestParams), // 将参数作为 body 数据发送
  });

  // 返回响应数据
  return response.data;
};



