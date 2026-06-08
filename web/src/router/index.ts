import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { usePermissionStore } from '@/stores/permission'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/views/login/LoginView.vue'),
      meta: { title: '登录' }
    },
    {
      path: '/',
      name: 'Layout',
      component: () => import('@/views/layout/LayoutView.vue'),
      redirect: '/dashboard',
      children: [
        {
          path: 'dashboard',
          name: 'Dashboard',
          component: () => import('@/views/dashboard/DashboardView.vue'),
          meta: { title: '首页', icon: 'HomeFilled' }
        },
        {
          path: 'base/products',
          name: 'ProductList',
          component: () => import('@/views/base/ProductList.vue'),
          meta: { title: '商品管理', permission: 'base:product:list' }
        },
        {
          path: 'base/categories',
          name: 'CategoryList',
          component: () => import('@/views/base/CategoryList.vue'),
          meta: { title: '分类管理', permission: 'base:category:list' }
        },
        {
          path: 'base/warehouses',
          name: 'WarehouseList',
          component: () => import('@/views/base/WarehouseList.vue'),
          meta: { title: '仓库管理', permission: 'base:warehouse:list' }
        },
        {
          path: 'base/suppliers',
          name: 'SupplierList',
          component: () => import('@/views/base/SupplierList.vue'),
          meta: { title: '供应商管理', permission: 'base:supplier:list' }
        },
        {
          path: 'stock/inventory',
          name: 'InventoryList',
          component: () => import('@/views/stock/InventoryList.vue'),
          meta: { title: '库存查询', permission: 'stock:inventory:list' }
        },
        {
          path: 'stock/inbound',
          name: 'InboundList',
          component: () => import('@/views/stock/InboundList.vue'),
          meta: { title: '入库管理', permission: 'stock:inbound:list' }
        },
        {
          path: 'stock/outbound',
          name: 'OutboundList',
          component: () => import('@/views/stock/OutboundList.vue'),
          meta: { title: '出库管理', permission: 'stock:outbound:list' }
        },
        {
          path: 'stock/transfer',
          name: 'TransferList',
          component: () => import('@/views/stock/TransferList.vue'),
          meta: { title: '库存调拨', permission: 'stock:transfer:list' }
        },
        {
          path: 'stock/check',
          name: 'CheckList',
          component: () => import('@/views/stock/CheckList.vue'),
          meta: { title: '库存盘点', permission: 'stock:check:list' }
        },
        {
          path: 'stock/return',
          name: 'ReturnList',
          component: () => import('@/views/stock/ReturnList.vue'),
          meta: { title: '退库管理', permission: 'stock:return:list' }
        },
        {
          path: 'intelligence/alerts',
          name: 'AlertList',
          component: () => import('@/views/intelligence/AlertList.vue'),
          meta: { title: '预警列表', permission: 'intelligence:alert:list' }
        },
        {
          path: 'intelligence/predict',
          name: 'PredictView',
          component: () => import('@/views/intelligence/PredictView.vue'),
          meta: { title: '销量预测', permission: 'intelligence:predict:view' }
        },
        {
          path: 'intelligence/replenish',
          name: 'ReplenishView',
          component: () => import('@/views/intelligence/ReplenishView.vue'),
          meta: { title: '智能补货', permission: 'intelligence:replenish:view' }
        },
        {
          path: 'intelligence/analysis',
          name: 'AnalysisView',
          component: () => import('@/views/intelligence/AnalysisView.vue'),
          meta: { title: '智能分析', permission: 'intelligence:analysis:view' }
        },
        {
          path: 'report/dashboard',
          name: 'ReportDashboard',
          component: () => import('@/views/report/ReportDashboard.vue'),
          meta: { title: '数据大屏', permission: 'report:dashboard:view' }
        },
        {
          path: 'report/in-out-stock',
          name: 'InOutStock',
          component: () => import('@/views/report/InOutStock.vue'),
          meta: { title: '进销存报表', permission: 'report:inout:view' }
        },
        {
          path: 'report/inventory-detail',
          name: 'InventoryDetail',
          component: () => import('@/views/report/InventoryDetail.vue'),
          meta: { title: '库存明细', permission: 'report:detail:view' }
        },
        {
          path: 'system/user',
          name: 'UserList',
          component: () => import('@/views/system/UserList.vue'),
          meta: { title: '用户管理', permission: 'system:user:list' }
        },
        {
          path: 'system/role',
          name: 'RoleList',
          component: () => import('@/views/system/RoleList.vue'),
          meta: { title: '角色管理', permission: 'system:role:list' }
        },
        {
          path: 'system/menu',
          name: 'MenuManage',
          component: () => import('@/views/system/MenuManage.vue'),
          meta: { title: '菜单管理', permission: 'system:menu:list' }
        },
        {
          path: 'system/log',
          name: 'OperLog',
          component: () => import('@/views/system/OperLog.vue'),
          meta: { title: '操作日志', permission: 'system:log:list' }
        },
        {
          path: 'system/profile',
          name: 'ProfileView',
          component: () => import('@/views/system/ProfileView.vue'),
          meta: { title: '个人中心' }
        },
        {
          path: 'system/backup',
          name: 'BackupList',
          component: () => import('@/views/system/BackupList.vue'),
          meta: { title: '数据备份', permission: 'system:backup:list' }
        },
        {
          path: 'system/config',
          name: 'SystemConfig',
          component: () => import('@/views/system/SystemConfig.vue'),
          meta: { title: '系统配置', permission: 'system:config:view' }
        }
      ]
    },
    {
      path: '/:pathMatch(.*)*',
      redirect: '/dashboard'
    }
  ]
})

router.beforeEach(async (to, _from, next) => {
  const userStore = useUserStore()
  const permissionStore = usePermissionStore()

  if (to.path === '/login') {
    next()
    return
  }

  if (!userStore.token) {
    next('/login')
    return
  }

  if (!userStore.userId) {
    try {
      await userStore.getUserInfo()
      await permissionStore.generateRoutes()
    } catch {
      userStore.logout()
      next('/login')
      return
    }
  }

  const permission = to.meta.permission as string
  if (permission && !userStore.hasPermission(permission)) {
    next('/dashboard')
    return
  }

  next()
})

export default router
