<template>
  <div>
    <div class="toolbar"><h3>菜单管理</h3><el-button type="primary" @click="handleAdd">新增根菜单</el-button></div>
    <el-table :data="menuTree" border stripe row-key="id" default-expand-all :tree-props="{ children: 'children' }">
      <el-table-column prop="menuName" label="菜单名称" />
      <el-table-column prop="menuType" label="类型" width="80">
        <template #default="{ row }"><el-tag size="small">{{ menuTypeLabel(row.menuType) }}</el-tag></template>
      </el-table-column>
      <el-table-column prop="path" label="路由" width="200" />
      <el-table-column prop="perms" label="权限标识" width="180" />
      <el-table-column prop="sort" label="排序" width="60" />
      <el-table-column label="操作" width="180">
        <template #default="{ row }">
          <el-button v-if="row.menuType !== 'F'" link type="primary" @click="addChild(row)">添加子节点</el-button>
          <el-button link type="primary">编辑</el-button>
          <el-button link type="danger" @click="deleteMenu(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { systemApi } from '@/api/modules'
import { ElMessage } from 'element-plus'

const menuTree = ref<any[]>([])

onMounted(async () => { const res = await systemApi.getMenuTree(); menuTree.value = res.data || [] })

function menuTypeLabel(t: string) { const m: any = { M: '目录', C: '菜单', F: '按钮' }; return m[t] || t }

function handleAdd() { ElMessage.info('新增根菜单') }
function addChild(row: any) { ElMessage.info('添加子节点: ' + row.menuName) }
function deleteMenu(id: number) { ElMessage.info('删除菜单: ' + id) }
</script>
<style scoped>.toolbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }</style>
