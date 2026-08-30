import { paramApi, postApi } from "../apis";

// 用户登录
export const userLoginByAccount = (data: { account: string; password: string;}) => {
    return postApi({
      url: "/loginHome",
      requestParams: data,
      description: "用户登录",
    });
  };