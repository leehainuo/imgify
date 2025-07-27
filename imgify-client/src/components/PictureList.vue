<script lang="ts" setup>
import { useRouter } from 'vue-router'

interface Props {
  dataList?: API.PictureVO[]
  loading?: boolean
}

const props = defineProps<Props>()

// 处理点击跳转
const router = useRouter()
// 跳转至图片详情
const handleClickPicture = (picture: API.PictureVO) => {
  router.push({
    path: `/picture/${picture.id}`,
  })
}
</script>

<template>
  <!-- 图片列表 -->
  <a-list
    :grid="{ gutter: 16, xs: 1, sm: 2, md: 3, lg: 4, xl: 5, xxl: 6 }"
    :data-source="props.dataList"
    :loading="props.loading"
  >
    <template #renderItem="{ item: picture }">
      <a-list-item style="padding: 0">
        <!-- 单张图片 -->
        <a-card hoverable @click="handleClickPicture(picture)">
          <template #cover>
            <img style="height: 180px; object-fit: cover" :alt="picture.name" :src="picture.url" />
          </template>
          <a-card-meta :title="picture.name">
            <template #description>
              <a-flex>
                <a-tag color="green">
                  {{ picture.category ?? '默认' }}
                </a-tag>
                <a-tag v-for="tag in picture.tags" :key="tag">
                  {{ tag }}
                </a-tag>
              </a-flex>
            </template>
          </a-card-meta>
        </a-card>
      </a-list-item>
    </template>
  </a-list>
</template>

<style scoped></style>
