<script lang="ts" setup>
import { computed, h, onMounted, ref } from 'vue'
import { HomeOutlined, LogoutOutlined, UserOutlined } from '@ant-design/icons-vue'
import { message, type MenuProps } from 'ant-design-vue'
import { useRouter } from 'vue-router'
import { useLoginUserStore } from '@/stores/useLoginUserStore'
import '@/assets/font/font.css'
import { userLogoutUsingPost } from '@/api/yonghumokuai'

const loginUserStore = useLoginUserStore()

// 菜单列表
const originItems = [
  {
    key: '/',
    icon: () => h(HomeOutlined),
    label: '主页',
  },
  {
    key: '/admin/userManage',
    label: '用户管理',
  },
  {
    key: "/admin/pictureManage",
    label: '图片管理',
  },
  {
    key: '/addPicture',
    label: '创建图片',
  },
  {
    key: '/others',
    label: h(
      'a',
      {
        href: 'https://www.lihainuo.com',
        target: '_blank',
        class: 'personal-website-link',
      },
      '个人网站',
    ),
    
  },
]

// 过滤菜单项
const filterMenus = (menus = [] as MenuProps['items']) => {
  return menus?.filter((menu) => {
    // 先判断 menu.key 是否为字符串，避免类型错误
    if (typeof menu?.key === 'string' && menu.key.startsWith('/admin')) {
      const loginUser = loginUserStore.loginUser
      if (!loginUser || loginUser.userRole !== 'admin') {
        return false
      }
    }
    return true
  })
}

const items = computed<MenuProps['items']>(() => filterMenus(originItems))

const router = useRouter()
const current = ref<string[]>([''])

const onMenuClick = ({ key }: { key: string }) => {
  router.push({
    path: key,
  })
}

router.afterEach((to) => {
  current.value = [to.path]
})

// 用户注销
const handlerLogout = async () => {
  const res = await userLogoutUsingPost()
  console.log(res)
  if (res.data.code === 0) {
    loginUserStore.setLoginUser({
      userName: '未登录',
    })
    message.success('退出登录成功')
    await router.push('/user/login')
  } else {
    message.error('退出登录失败，' + res.data.message)
  }
}

onMounted(() => {
  loginUserStore.fetchLoginUser()
})
</script>
<template>
  <div id="globalHeader">
    <a-row :wrap="false">
      <a-col flex="200px">
        <router-link to="/">
          <div class="title-bar">
            <img class="logo" src="/src/assets/logo.png" alt="logo" />
            <div class="title"><span>Imgify</span> 云图库</div>
          </div>
        </router-link>
      </a-col>
      <a-col flex="auto">
        <a-menu
          v-model:selectedKeys="current"
          mode="horizontal"
          @click="onMenuClick"
          :items="items"
        />
      </a-col>
      <a-col flex="120px">
        <div class="user-login-status">
          <div v-if="loginUserStore.loading">
            <!-- 空白 -->
          </div>
          <div v-else-if="loginUserStore.loginUser.id">
            <a-dropdown>
              <ASpace>
                <a-avatar :src="loginUserStore.loginUser.userAvatar">
                  <template #icon><UserOutlined /></template>
                </a-avatar>
                {{ loginUserStore.loginUser.userName ?? '请输入昵称' }}
              </ASpace>
              <template #overlay>
                <a-menu>
                  <a-menu-item @click="handlerLogout">
                    <LogoutOutlined />
                    退出登录
                  </a-menu-item>
                </a-menu>
              </template>
            </a-dropdown>
          </div>
          <div v-else>
            <a-button type="primary" href="/user/login">登录</a-button>
          </div>
        </div>
      </a-col>
    </a-row>
  </div>
</template>

<style scoped>
.title-bar {
  display: flex;
  align-items: center;
}

.title {
  color: black;
  font-size: 16px;
  font-weight: 500;
  margin-left: 16px;
}

.title span {
  color: #1f87fd;
  font-size: 22px;
  font-weight: 900;
  font-family: 'Geist';
}

.logo {
  height: 40px;
}

.personal-website-link {
  position: relative;
  transition: all 0.3s cubic-bezier(0.645, 0.045, 0.355, 1);
  display: inline-block;
  font-weight: 500; 
  text-shadow: 0 0 2px rgba(24, 144, 255, 0.15); /* 默认微弱蓝光 */
}

.personal-website-link:hover {
  color: #1890ff !important;
  transform: translateY(-1px);
  text-shadow:
    0 0 12px rgba(24, 144, 255, 0.45),
    0 0 24px rgba(24, 144, 255, 0.25); /* 悬停更亮更广 */
}

.personal-website-link::after {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 0;
  width: 0;
  height: 2px;
  background: linear-gradient(90deg, #1890ff, #40a9ff);
  transition: width 0.3s cubic-bezier(0.645, 0.045, 0.355, 1);
  border-radius: 1px;
}

.personal-website-link:hover::after {
  width: 100%;
}

/* 添加一个微妙的发光效果 */
.personal-website-link:hover {
  text-shadow: 0 0 8px rgba(24, 144, 255, 0.3);
}

/* 覆盖 Ant Design 菜单样式 */
:deep(.ant-menu-item) .personal-website-link {
  color: inherit;
  text-decoration: none;
}

:deep(.ant-menu-item:hover) .personal-website-link {
  color: #1890ff !important;
  transform: translateY(-1px);
  text-shadow:
    0 0 12px rgba(24, 144, 255, 0.45),
    0 0 24px rgba(24, 144, 255, 0.25);
}

:deep(.ant-menu-item:hover) .personal-website-link::after {
  width: 100%;
}

:deep(.ant-menu-item:hover) .personal-website-link {
  text-shadow: 0 0 8px rgba(24, 144, 255, 0.3);
}
</style>
