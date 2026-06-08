import { defineStore } from 'pinia'
import { ref } from 'vue'
import { systemApi } from '@/api/modules'
import { useUserStore } from './user'

export interface MenuItem {
  id: number
  parentId: number
  menuName: string
  menuType: string
  path?: string
  component?: string
  perms?: string
  icon?: string
  sort: number
  visible: number
  children?: MenuItem[]
}

export const usePermissionStore = defineStore('permission', () => {
  const menuTree = ref<MenuItem[]>([])
  const isGenerated = ref(false)

  function hasChildMenu(menu: MenuItem): boolean {
    return !!(menu.children && menu.children.some(c => c.menuType !== 'F'))
  }

  function filterMenuByPermission(menus: MenuItem[], permissions: string[]): MenuItem[] {
    const result: MenuItem[] = []
    
    for (const menu of menus) {
      // 如果是按钮类型，跳过
      if (menu.menuType === 'F') continue
      
      // 如果是超级管理员，显示所有菜单
      if (permissions.includes('*') || permissions.includes('SUPER_ADMIN')) {
        const children = menu.children ? filterMenuByPermission(menu.children, permissions) : []
        result.push({ ...menu, children: children.length > 0 ? children : undefined })
        continue
      }
      
      // 如果菜单有权限标识，检查是否有权限
      if (menu.perms && menu.perms.trim()) {
        if (permissions.includes(menu.perms)) {
          const children = menu.children ? filterMenuByPermission(menu.children, permissions) : []
          result.push({ ...menu, children: children.length > 0 ? children : undefined })
        }
      } else {
        // 没有权限标识的菜单（如一级菜单），检查其子菜单是否有权限
        const children = menu.children ? filterMenuByPermission(menu.children, permissions) : []
        if (children.length > 0) {
          result.push({ ...menu, children })
        }
      }
    }
    
    return result
  }

  async function generateRoutes() {
    const userStore = useUserStore()
    if (!userStore.token) return

    try {
      const res = await systemApi.getMenuTree()
      const allMenus = res.data || []
      
      // 根据用户权限过滤菜单
      const permissions = userStore.permissions || []
      menuTree.value = filterMenuByPermission(allMenus, permissions)
      
      isGenerated.value = true
    } catch (e) {
      console.error('获取菜单失败', e)
    }
  }

  return { menuTree, isGenerated, generateRoutes }
})
