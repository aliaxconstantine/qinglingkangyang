import { paramApi, postApi } from "../apis";

export const getSystemList = (data: any) => {
    return postApi({
      url: "/SystemList",
      requestParams: data,
      description: "获取系统设置",
    });
};

export const saveSystem = (data: any) => {
    return postApi({
      url: "/SystemSave",
      requestParams: data,
      description: "保存系统设置",
    });
};


export const deleteSystem = (data: any) => {
    return postApi({
      url: "/SystemDelete",
      requestParams: data,
      description: "删除系统设置",
    });
};

