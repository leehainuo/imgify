<script lang="ts" setup>
import { deleteUserUsingPost, listUserVoByPageUsingPost, updateUserUsingPost } from '@/api/yonghumokuai'
import { message } from 'ant-design-vue'
import dayjs from 'dayjs'
import { computed, onMounted, reactive, ref } from 'vue'

const columns = [
  {
    title: 'id',
    dataIndex: 'id',
  },
  {
    title: '账号',
    dataIndex: 'userAccount',
  },
  {
    title: '用户名',
    dataIndex: 'userName',
  },
  {
    title: '头像',
    dataIndex: 'userAvatar',
  },
  {
    title: '简介',
    dataIndex: 'userProfile',
  },
  {
    title: '用户角色',
    dataIndex: 'userRole',
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
  },
  {
    title: '操作',
    key: 'action',
    dataIndex: 'action',
  },
]

// 从后端获取数据
const dataList = ref<API.UserVO[]>([])
const total = ref(0)

// 搜索条件
const searchParams = reactive<API.UserQueryRequest>({
  current: 1,
  pageSize: 10,
})

const handleSearch = () => {
  // 重置页码
  searchParams.current = 1
  fetchData()
}

// 分页参数 由于这些参数是动态变化的，需要使用到 Vue 的 computed 计算属性
// 否则当 searchParams 改变时，分页参数并不会更新
const pagination = computed(() => {
  return {
    current: searchParams.current ?? 1,
    pageSize: searchParams.pageSize ?? 10,
    total: total.value,
    showSizeChanger: true,
    showTotal: (total: number) => `共 ${total} 条`,
  }
})

// 表格变化处理
const handleTableChange = (page: { current: number; pageSize: number }) => {
  searchParams.current = page.current
  searchParams.pageSize = page.pageSize
  fetchData()
}

// 表格获取数据
const fetchData = async () => {
  const res = await listUserVoByPageUsingPost({
    ...searchParams,
  })
  if (res.data.data) {
    dataList.value = res.data.data.records ?? []
    total.value = Number(res.data.data.total ?? 0)
  } else {
    message.error('获取数据失败,' + res.data.message)
  }
}

const deleteModalVisible = ref(false)
const deleteId = ref(0)

const editModalVisible = ref(false)
const editForm = ref<API.UserUpdateRequest>({})
const editFormLoading = ref(false)

// 打开编辑弹窗并填充数据
const openEditModal = (record: API.UserUpdateRequest) => {
  editForm.value = { ...record }
  editModalVisible.value = true
}

// 提交编辑
const handleEdit = async () => {
  console.log('🌀 测试提交')
  editFormLoading.value = true
  const res = await updateUserUsingPost(editForm.value)
  editFormLoading.value = false
  if (res.data.code === 0) {
    message.success('修改成功')
    editModalVisible.value = false
    fetchData()
  } else {
    message.error('修改失败,' + res.data.message)
  }
}

// 打卡删除弹窗
const openDeleteModal = (id: number) => {
  deleteModalVisible.value = true
  deleteId.value = id
}

// 删除表格数据
const handleDelete = async () => {
  const id = deleteId.value
  if (!id) {
    return
  }
  const res = await deleteUserUsingPost({ id })
  if (res.data.code === 0) {
    message.success('删除成功')
    // 刷新数据
    fetchData()
  } else {
    message.error('删除失败')
  }
}

// 页面加载请求一次
onMounted(() => {
  fetchData()
})
</script>

<template>
  <div id="userManagePage">
    <a-flex style="margin-bottom: 1em;" justify="space-between">
      <h2>用户管理</h2>
    </a-flex>
    <!-- 搜索框 -->
    <a-form layout="inline" :model="searchParams" @finish="handleSearch">
      <a-form-item label="账号">
        <a-input v-model:value="searchParams.userAccount" placeholder="输入账号" />
      </a-form-item>
      <a-form-item label="用户名">
        <a-input v-model:value="searchParams.userName" placeholder="输入用户名" />
      </a-form-item>
      <a-form-item>
        <a-button type="primary" html-type="submit">搜索</a-button>
      </a-form-item>
    </a-form>
    <div style="margin-bottom: 2em" />
    <!-- 表格 -->
    <a-table
      :columns="columns"
      :data-source="dataList"
      :pagination="pagination"
      @change="handleTableChange"
      :scroll="{ x: 'max-content' }"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.dataIndex === 'userAvatar'">
          <a-image :src="record.userAvatar" :width="120" />
        </template>
        <template v-else-if="column.dataIndex === 'userRole'">
          <div v-if="record.userRole === 'admin'">
            <a-tag color="green">管理员</a-tag>
          </div>
          <div v-else>
            <a-tag color="blue">普通用户</a-tag>
          </div>
        </template>
        <template v-else-if="column.dataIndex === 'createTime'">
          {{ dayjs(record.createTime).format('YYYY-MM-DD HH:mm:ss') }}
        </template>
        <template v-else-if="column.key === 'action'">
          <div class="action">
            <a-button type="link" @click="openEditModal(record)">编辑</a-button>
            <a-button type="link" danger @click="openDeleteModal(record.id)">删除</a-button>
          </div>
        </template>
      </template>
    </a-table>
    <!-- 编辑窗口 -->
    <a-modal
      v-model:open="editModalVisible"
      title="编辑用户"
      @ok="handleEdit"
      @cancel="editModalVisible = false"
      :confirm-loading="editFormLoading"
      destoryOnClose
    >
      <a-form :model="editForm" layout="vertical" >
        <a-form-item label="用户名">
          <a-input v-model:value="editForm.userName" />
        </a-form-item>
        <a-form-item label="头像">
          <a-input v-model:value="editForm.userAvatar" />
        </a-form-item>
        <a-form-item label="简介">
          <a-textarea v-model:value="editForm.userProfile" />
        </a-form-item>
        <a-form-item label="角色">
          <a-select v-model:value="editForm.userRole">
            <a-select-option value="admin">管理员</a-select-option>
            <a-select-option value="user">普通用户</a-select-option>
          </a-select>
        </a-form-item>
      </a-form>
    </a-modal>
    <!-- 删除窗口 -->
    <a-modal
      v-model:open="deleteModalVisible"
      title="删除用户"
      @ok="handleDelete"
      @cancel="deleteModalVisible = false"
      destoryOnClose
    >
      <p style="color: red">你确定要删除此用户吗？不可恢复！</p>
    </a-modal>
  </div>
</template>

<style scoped>
.action {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 1em;
}
</style>
