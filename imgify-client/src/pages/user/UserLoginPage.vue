<script lang="ts" setup>
import { userLoginUsingPost } from '@/api/yonghumokuai'
import { useLoginUserStore } from '@/stores/useLoginUserStore'
import { message } from 'ant-design-vue'
import { reactive } from 'vue'
import { useRouter } from 'vue-router'
import '@/assets/font/font.css'
import SvgCloudBackgound from '@/components/svg/SvgCloudBackgound.vue'

const router = useRouter()
const loginUserStore = useLoginUserStore()

const formState = reactive<API.UserLoginRequest>({
  userAccount: '',
  userPassword: '',
})

/**
 * 提交表单
 * @parm values
 */
const handleSumbit = async (values: { userAccount: string; userPassword: string }) => {
  const res = await userLoginUsingPost(values)
  // 登录成功，把登录态保存到全局状态中
  if (res.data.code === 0 && res.data.data) {
    await loginUserStore.fetchLoginUser()
    message.success('登录成功')
    router.push({
      path: '/',
      replace: true,
    })
  } else {
    message.error('登录失败，' + res.data.message)
  }
}

// 处理返回
const handleReturn = () => {
  router.push(
    '/'
  )
}

</script>

<template>
  <SvgCloudBackgound />
  <div id="userLoginPage">
    <h2 class="title">
      <span>Img<span>ify</span></span> 云图库 - 用户登录
    </h2>
    <div class="desc">企业级智能协同云图库</div>
    <a-form :model="formState" name="basic" autocomplete="off" @finish="handleSumbit">
      <a-form-item name="userAccount" :rules="[{ required: true, message: '请输入账号' }]">
        <a-input v-model:value="formState.userAccount" placeholder="请输入账号" />
      </a-form-item>
      <a-form-item
        name="userPassword"
        :rules="[
          { required: true, message: '请输入密码' },
          { min: 8, message: '密码长度不能小于 8 位' },
        ]"
      >
        <a-input-password v-model:value="formState.userPassword" placeholder="请输入密码" />
      </a-form-item>
      <div class="tips">
        没有账号？
        <RouterLink to="/user/register">去注册</RouterLink>
      </div>
      <a-form-item>
        <a-button type="primary" html-type="submit" style="width: 100%">登录</a-button>
        <div class="desc" style="cursor: pointer;margin-top: 0.5em;" @click="handleReturn">返回首页</div>
      </a-form-item>
    </a-form>
  </div>
</template>

<style scoped>
#userLoginPage {
  max-width: 360px;
  margin: 60px auto;
}

.title {
  font-size: 1.45rem;
  text-align: center;
  margin-bottom: 16px;
}

.title span {
  font-size: 1.85rem;
  font-weight: 900;
  font-family: 'Geist';
}

.title span span {
  color: #1f87fd;
}

.desc {
  text-align: center;
  color: #bbb;
  margin-bottom: 16px;
}

.tips {
  margin-bottom: 16px;
  color: #bbb;
  font-size: 13px;
  text-align: right;
}
</style>
