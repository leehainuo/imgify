<script lang="ts" setup>
import { deletePictureUsingPost, getPictureVoByIdUsingGet } from '@/api/tupianmokuai'
import { useLoginUserStore } from '@/stores/useLoginUserStore'
import { downloadImage, formatSize } from '@/utils'
import {
  ArrowLeftOutlined,
  DeleteOutlined,
  DownloadOutlined,
  EditOutlined,
} from '@ant-design/icons-vue'
import { message } from 'ant-design-vue'
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'

const props = defineProps<{
  id: string | number
}>()

const picture = ref<API.PictureVO>({})

// 获取图片详情
const fetchPictureDetail = async () => {
  try {
    const res = await getPictureVoByIdUsingGet({
      id: Number(props.id),
    })
    if (res.data.code === 0 && res.data.data) {
      picture.value = res.data.data
    } else {
      message.error('获取图片详情失败，' + res.data.message)
    }
  } catch (error) {
    message.error('获取图片详情失败：' + error)
  }
}

// 页面加载时获取数据
onMounted(() => {
  fetchPictureDetail()
})

const router = useRouter()
const loginUserStore = useLoginUserStore()

// 是否具有编辑权限
const canEdit = computed(() => {
  const loginUser = loginUserStore.loginUser
  // 未登录不可编辑
  if (!loginUser.id) {
    return false
  }
  // 仅本人或管理员可编辑
  const user = picture.value.user || {}
  return loginUser.id === user.id || loginUser.userRole === 'admin'
})

// 编辑
const handlerEdit = () => {
  router.push('/addPicture?id=' + picture.value.id)
}
// 删除
const handlerDelete = async () => {
  const id = picture.value.id
  if (!id) {
    return
  }
  const res = await deletePictureUsingPost({ id })
  if (res.data.code === 0) {
    message.success('删除成功')
  } else {
    message.error('删除失败')
  }
}
// 下载
const handlerDownload = () => {
  downloadImage(picture.value.url)
}
// 退出
const handlerOut = () => {
  router.back()
}
</script>

<template>
  <div id="pictureDetailPage">
    <!-- 悬浮返回按钮 -->
    <a-float-button type="primary" shape="circle" @click="handlerOut" class="floating-back-btn">
      <template #icon>
        <ArrowLeftOutlined />
      </template>
    </a-float-button>
    <a-row :gutter="[16, 16]">
      <!-- 图片展示区 -->
      <a-col :sm="24" :md="16" :xl="18">
        <a-card title="图片预览" >
          <div style="width: 100%; height: 100%; display: flex; justify-content: center;">
            <a-image style="max-height: 600px; object-fit: contain" :src="picture.url" />
          </div>
        </a-card>
      </a-col>
      <!-- 图片信息区 -->
      <a-col :sm="24" :md="8" :xl="6">
        <a-card title="图片信息">
          <a-descriptions :column="1">
            <a-descriptions-item label="作者">
              <a-space>
                <a-avatar :size="24" :src="picture.user?.userAvatar" />
                <div>{{ picture.user?.userName }}</div>
              </a-space>
            </a-descriptions-item>
            <a-descriptions-item label="名称">
              {{ picture.name ?? '未命名' }}
            </a-descriptions-item>
            <a-descriptions-item label="简介">
              {{ picture.introduction ?? '-' }}
            </a-descriptions-item>
            <a-descriptions-item label="分类">
              {{ picture.category ?? '默认' }}
            </a-descriptions-item>
            <a-descriptions-item label="标签">
              <a-tag v-for="tag in picture.tags" :key="tag">
                {{ tag }}
              </a-tag>
            </a-descriptions-item>
            <a-descriptions-item label="格式">
              {{ picture.picFormat ?? '-' }}
            </a-descriptions-item>
            <a-descriptions-item label="宽度">
              {{ picture.picWidth ?? '-' }}
            </a-descriptions-item>
            <a-descriptions-item label="高度">
              {{ picture.picHeight ?? '-' }}
            </a-descriptions-item>
            <a-descriptions-item label="宽高比">
              {{ picture.picScale ?? '-' }}
            </a-descriptions-item>
            <a-descriptions-item label="大小">
              {{ formatSize(picture.picSize) }}
            </a-descriptions-item>
          </a-descriptions>
          <a-space wrap>
            <a-button type="primary" @click="handlerDownload">
              下载
              <template #icon>
                <DownloadOutlined />
              </template>
            </a-button>

            <a-button v-if="canEdit" type="default" @click="handlerEdit">
              编辑
              <template #icon>
                <EditOutlined />
              </template>
            </a-button>
            <a-button v-if="canEdit" danger @click="handlerDelete">
              删除
              <template #icon>
                <DeleteOutlined />
              </template>
            </a-button>
          </a-space>
        </a-card>
      </a-col>
    </a-row>
  </div>
</template>

<style scoped>
#pictureDetailPage {
  padding: 24px;
  position: relative;
}

/* 悬浮返回按钮样式 */
.floating-back-btn {
  position: fixed !important;
  bottom: 24px !important;
  right: 24px !important;
  z-index: 1000;
  animation: float 3s ease-in-out infinite;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  transition: all 0.3s ease;
}

.floating-back-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.2);
}

/* 上浮动画 */
@keyframes float {
  0%,
  100% {
    transform: translateY(0px);
  }
  50% {
    transform: translateY(-4px);
  }
}
</style>
