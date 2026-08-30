import axios from 'axios';
import { ElMessage } from 'element-plus';



// 创建 axios 实例
const instance = axios.create({
  baseURL: import.meta.env.VITE_API_URL, // 默认的请求地址，可以根据实际情况修改
  timeout: 5000, // 请求超时设置
  headers: {
    'Content-Type': 'application/json', // 默认内容类型
  },
});

// 请求拦截器：可以在请求发出之前做一些处理（如添加认证 token 等）
instance.interceptors.request.use(
  (config) => {
    // 在这里添加认证 token 或其他请求头
    const token = sessionStorage.getItem('token'); // 假设你存储了 token
    if (token) {
      config.headers['Authorization'] = `${token}`;
    }
    return config;
  },
  (error) => {
    // 请求错误时的处理
    return Promise.reject(error);
  }
);

// 响应拦截器：可以在响应返回时做一些处理（如全局处理错误提示）
instance.interceptors.response.use(
  (response) => {
    console.log('response=>', response);
    // 如果需要，可以根据业务需求对响应做处理
    if(response.data.code === 9999){
      ElMessage.error(response.data.msg);
    }  
    return response; // 返回响应的数据部分
  },
  (error) => {
    // 可以在这里统一处理请求错误
    if (error.response) {
      // 响应有错误时的处理
      switch (error.response.status) {
        case 401:
          console.error('未授权，请登录');
          break;
        case 404:
          console.error('请求的资源未找到');
          break;
        default:
          console.error('请求失败，请稍后重试');
      }
    } else if (error.request) {
      // 请求发出后没有响应的处理
      console.error('请求未收到响应');
    } else {
      // 其他错误
      console.error('请求配置错误');
    }
    return Promise.reject(error);
  }
);

export default instance;
