<template>
  <div>
    <div class="toolbar">
      <h3>分类管理</h3>
      <el-button type="primary" @click="showAddModal = true">新增分类</el-button>
    </div>
    
    <el-tree
      :data="treeData"
      :props="defaultProps"
      node-key="id"
      default-expand-all
      class="tree-container"
    >
      <template #default="{ node, data }">
        <span class="tree-node">
          <span>{{ node.label }}</span>
          <span class="node-actions">
            <el-button link type="primary" size="small" @click="editCategory(data)">编辑</el-button>
            <el-button link type="danger" size="small" @click="deleteCategory(data.id)">删除</el-button>
            <el-button link type="success" size="small" @click="addChild(data.id)">添加子分类</el-button>
          </span>
        </span>
      </template>
    </el-tree>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="showAddModal" :title="editData.id ? '编辑分类' : '新增分类'" width="400px">
      <el-form :model="editData" label-width="80px">
        <el-form-item label="上级分类">
          <el-select v-model="editData.parentId" placeholder="选择上级分类">
            <el-option :value="0" label="顶级分类" />
            <el-option v-for="c in parentOptions" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="分类名称">
          <el-input v-model="editData.name" placeholder="输入分类名称" />
        </el-form-item>
        <el-form-item label="分类编码">
          <el-input v-model="editData.code" placeholder="输入分类编码" />
        </el-form-item>
        <el-form-item label="排序号">
          <el-input-number v-model="editData.sortOrder" :min="0" :max="999" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="closeModal">取消</el-button>
        <el-button type="primary" @click="saveCategory">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { baseApi } from '@/api/modules'
import { ElMessage, ElMessageBox } from 'element-plus'

const treeData = ref<any[]>([])
const showAddModal = ref(false)

const editData = reactive({
  id: 0,
  parentId: 0,
  name: '',
  code: '',
  sortOrder: 0
})

const defaultProps = {
  children: 'children',
  label: 'name'
}

const parentOptions = computed(() => {
  const options: any[] = []
  const flatten = (nodes: any[]) => {
    nodes.forEach(node => {
      if (node.id !== editData.id) {
        options.push({ id: node.id, name: node.name })
      }
      if (node.children) flatten(node.children)
    })
  }
  flatten(treeData.value)
  return options
})

onMounted(() => loadData())

async function loadData() {
  const res = await baseApi.getCategoryTree()
  treeData.value = res.data || []
}

function showAddForm(parentId = 0) {
  editData.id = 0
  editData.parentId = parentId
  editData.name = ''
  editData.code = ''
  editData.sortOrder = 0
  showAddModal.value = true
}

function editCategory(data: any) {
  editData.id = data.id
  editData.parentId = data.parentId || 0
  editData.name = data.name
  editData.code = data.code || ''
  editData.sortOrder = data.sortOrder || 0
  showAddModal.value = true
}

function addChild(parentId: number) {
  showAddForm(parentId)
}

function closeModal() {
  showAddModal.value = false
}

async function saveCategory() {
  if (!editData.name.trim()) {
    ElMessage.warning('请输入分类名称')
    return
  }
  
  if (editData.id) {
    await baseApi.updateCategory(editData.id, editData)
    ElMessage.success('修改成功')
  } else {
    await baseApi.addCategory(editData)
    ElMessage.success('添加成功')
  }
  
  showAddModal.value = false
  loadData()
}

async function deleteCategory(id: number) {
  ElMessageBox.confirm('确定删除该分类？', '提示', { type: 'warning' })
    .then(async () => {
      await baseApi.deleteCategory(id)
      ElMessage.success('删除成功')
      loadData()
    })
    .catch(() => {})
}
</script>

<style scoped>
.toolbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.tree-container { border: 1px solid #e8e8e8; border-radius: 4px; padding: 16px; min-height: 400px; }
.tree-node { display: flex; justify-content: space-between; align-items: center; width: 100%; }
.node-actions { visibility: hidden; }
.tree-node:hover .node-actions { visibility: visible; }
</style>