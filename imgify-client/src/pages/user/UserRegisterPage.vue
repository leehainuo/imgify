<script lang="ts" setup>
import { userRegisterUsingPost } from '@/api/yonghumokuai'
import { message } from 'ant-design-vue'
import { reactive } from 'vue'
import { useRouter } from 'vue-router'
import '@/assets/font/font.css'
import SvgCloudBackgound from '@/components/svg/SvgCloudBackgound.vue'

const router = useRouter()

const formState = reactive<API.UserRegisterRequest>({
  userAccount: '',
  userPassword: '',
  checkPassword: '',
})

/**
 * 提交表单
 * @parm values
 */
const handleSumbit = async (values: {
  userAccount: string
  userPassword: string
  checkPassword: string
}) => {
  // 判断两次密码是否一致
  if (values.userPassword !== values.checkPassword) {
    message.error('二次输入的密码不一致')
    return
  }
  const res = await userRegisterUsingPost(values)
  // 注册成功，跳转到登陆页面
  if (res.data.code === 0 && res.data.data) {
    message.success('注册成果')
    router.push({
      path: '/user/login',
      replace: true,
    })
  } else {
    message.error('注册失败，' + res.data.message)
  }
}

// 处理返回
const handleReturn = () => {
  router.push('/')
}
</script>

<template>
  <SvgCloudBackgound />
  <div id="userRegisterPage">
    <h2 class="title">
      <span>Img<span>ify</span></span> 云图库 - 用户登录
    </h2>
    <div class="desc">企业级智能协同云图库</div>
    <a-form
      :model="formState"
      name="basic"
      label-align="left"
      autocomplete="off"
      @finish="handleSumbit"
    >
      <a-form-item name="userAccount" :rules="[{ required: true, message: '请输入账号' }]">
        <a-input v-model:value="formState.userAccount" placeholder="请输入账号" />
      </a-form-item>
      <a-form-item
        name="userPassword"
        :rules="[
          { required: true, message: '请输入密码' },
          { min: 8, message: '密码不能小于 8 位' },
        ]"
      >
        <a-input-password v-model:value="formState.userPassword" placeholder="请输入密码" />
      </a-form-item>
      <a-form-item
        name="checkPassword"
        :rules="[
          { required: true, message: '请输入确认密码' },
          { min: 8, message: '确认密码不能小于 8 位' },
        ]"
      >
        <a-input-password v-model:value="formState.checkPassword" placeholder="请输入确认密码" />
      </a-form-item>
      <div class="tips">
        已有账号？
        <RouterLink to="/user/login">去登录</RouterLink>
      </div>
      <a-form-item>
        <a-button type="primary" html-type="submit" style="width: 100%">注册</a-button>
        <div class="desc" style="cursor: pointer;margin-top: 0.5em;" @click="handleReturn">返回首页</div>
      </a-form-item>
    </a-form>
  </div>
</template>

<style scoped>
#userRegisterPage {
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
