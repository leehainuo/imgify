import { getLoginUserUsingGet } from '@/api/yonghumokuai'
import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useLoginUserStore = defineStore('loginUser', () => {
  const loginUser = ref<API.LoginUserVO>({
    userName: '未登录',
  })
  const loading = ref(true)

  async function fetchLoginUser() {
    loading.value = true
    const res = await getLoginUserUsingGet()
    if (res.data.code === 0 && res.data.data) {
      loginUser.value = res.data.data
    }
    loading.value = false
  }

  function setLoginUser(newLoginUser: API.LoginUserVO) {
    loginUser.value = newLoginUser
  }

  return { loginUser, setLoginUser, fetchLoginUser, loading }
})
