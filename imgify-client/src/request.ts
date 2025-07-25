import Axios from 'axios'
import { message } from 'ant-design-vue'

// 创建 Axios 实例
// 如果不写这个 withCredentials: true，则前端就不会携带 cookie
const axios = Axios.create({
    baseURL: 'http://localhost:8080',
    timeout: 60000,
    withCredentials: true,
})

// 全局请求拦截器
axios.interceptors.request.use(
    function (config) {
        // 请求前，可以在这里做些事情
        return config
    },
    function (error) {
        return Promise.reject(error)
    }
)

// 全局响应拦截器
axios.interceptors.response.use(
    function (response) {
        const { data } = response
        // 未登录
        if (data.code === 40100) {
            // 不是获取用户信息的请求，并且不是登录请求，则跳转到登录页
            if (
                !response.request.responseURL.includes('/user/get/login') &&
                !window.location.href.includes('/user/login')
            ) {
                message.warning('请先登录')
                // 跳转到登录页
                window.location.href = `/user/login?redirect=${window.location.href}`
            }
        }
        return response
    },
    function (error) {
        return Promise.reject(error)
    }
)

export default axios