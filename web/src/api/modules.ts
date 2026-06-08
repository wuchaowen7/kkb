import request from './request'

// ==================== 认证 ====================
export const authApi = {
  login: (data: { username: string; password: string }) =>
    request.post('/auth/login', data),
  getInfo: () => request.get('/auth/info'),
  logout: () => request.post('/auth/logout'),
  updateProfile: (data: any) => request.put('/auth/profile', data),
  changePassword: (data: { oldPassword: string; newPassword: string }) => 
    request.put('/auth/password', data)
}

// ==================== 系统管理 ====================
export const systemApi = {
  // 用户管理
  getUserPage: (params: any) => request.get('/system/user/page', { params }),
  getUser: (id: number) => request.get(`/system/user/${id}`),
  getUserRoles: (id: number) => request.get(`/system/user/${id}/roles`),
  addUser: (data: any) => request.post('/system/user', data),
  updateUser: (id: number, data: any) => request.put(`/system/user/${id}`, data),
  deleteUser: (ids: string) => request.delete(`/system/user/${ids}`),
  assignRoles: (id: number, roleIds: number[]) => request.put(`/system/user/${id}/roles`, roleIds),
  resetPwd: (id: number, data: any) => request.put(`/system/user/${id}/resetPwd`, data),
  // 角色管理
  getRoleList: () => request.get('/system/role/list'),
  getRoleMenus: (roleId: number) => request.get(`/system/role/${roleId}/menus`),
  addRole: (data: any) => request.post('/system/role', data),
  updateRole: (id: number, data: any) => request.put(`/system/role/${id}`, data),
  deleteRole: (ids: string) => request.delete(`/system/role/${ids}`),
  assignMenus: (roleId: number, menuIds: number[]) => request.put(`/system/role/${roleId}/menus`, menuIds),
  // 菜单管理
  getMenuTree: () => request.get('/system/menu/tree'),
  addMenu: (data: any) => request.post('/system/menu', data),
  updateMenu: (id: number, data: any) => request.put(`/system/menu/${id}`, data),
  deleteMenu: (id: number) => request.delete(`/system/menu/${id}`),
  // 数据备份
  getBackupList: () => request.get('/system/backup/list'),
  createBackup: (data: any) => request.post('/system/backup', data),
  deleteBackup: (fileName: string) => request.delete(`/system/backup/${fileName}`),
  restoreBackup: (fileName: string) => request.post(`/system/backup/restore/${fileName}`),
  // 系统配置
  getConfig: () => request.get('/system/config'),
  updateConfig: (data: any) => request.put('/system/config', data)
}

// ==================== 基础信息 ====================
export const baseApi = {
  // 商品
  getProductPage: (params: any) => request.get('/base/products/page', { params }),
  getProduct: (id: number) => request.get(`/base/products/${id}`),
  addProduct: (data: any) => request.post('/base/products', data),
  updateProduct: (id: number, data: any) => request.put(`/base/products/${id}`, data),
  updateProductStatus: (id: number, status: number) => request.put(`/base/products/${id}/status`, { status }),
  deleteProduct: (ids: string) => request.delete(`/base/products/${ids}`),
  // 分类
  getCategoryTree: () => request.get('/base/categories/tree'),
  addCategory: (data: any) => request.post('/base/categories', data),
  updateCategory: (id: number, data: any) => request.put(`/base/categories/${id}`, data),
  deleteCategory: (id: number) => request.delete(`/base/categories/${id}`),
  // 仓库
  getWarehousePage: (params: any) => request.get('/base/warehouses/page', { params }),
  getWarehouseList: () => request.get('/base/warehouses/list'),
  addWarehouse: (data: any) => request.post('/base/warehouses', data),
  updateWarehouse: (id: number, data: any) => request.put(`/base/warehouses/${id}`, data),
  deleteWarehouse: (ids: string) => request.delete(`/base/warehouses/${ids}`),
  // 供应商
  getSupplierPage: (params: any) => request.get('/base/suppliers/page', { params }),
  getSupplierList: () => request.get('/base/suppliers/list'),
  addSupplier: (data: any) => request.post('/base/suppliers', data),
  updateSupplier: (id: number, data: any) => request.put(`/base/suppliers/${id}`, data),
  deleteSupplier: (ids: string) => request.delete(`/base/suppliers/${ids}`)
}

// ==================== 库存业务 ====================
export const stockApi = {
  // 库存查询
  getInventoryList: (params: any) => request.get('/stock/inventory/list', { params }),
  getInventoryPage: (params: any) => request.get('/stock/inventory/page', { params }),
  // 仓库列表
  getWarehouseList: () => request.get('/base/warehouses/list'),
  // 分类列表
  getCategoryList: () => request.get('/base/categories/tree'),
  // 入库
  getInboundPage: (params: any) => request.get('/stock/inbound/page', { params }),
  getInbound: (id: number) => request.get(`/stock/inbound/${id}`),
  getInboundDetails: (id: number) => request.get(`/stock/inbound/${id}/details`),
  createInbound: (data: any) => request.post('/stock/inbound', data),
  updateInbound: (id: number, data: any) => request.put(`/stock/inbound/${id}`, data),
  deleteInbound: (id: number) => request.delete(`/stock/inbound/${id}`),
  confirmInbound: (id: number) => request.put(`/stock/inbound/${id}/confirm`),
  // 出库
  getOutboundPage: (params: any) => request.get('/stock/outbound/page', { params }),
  getOutbound: (id: number) => request.get(`/stock/outbound/${id}`),
  getOutboundDetails: (id: number) => request.get(`/stock/outbound/${id}/details`),
  createOutbound: (data: any) => request.post('/stock/outbound', data),
  updateOutbound: (id: number, data: any) => request.put(`/stock/outbound/${id}`, data),
  deleteOutbound: (id: number) => request.delete(`/stock/outbound/${id}`),
  auditOutbound: (id: number, approved: boolean, opinion?: string) =>
    request.put(`/stock/outbound/${id}/audit?approved=${approved}&opinion=${opinion || ''}`),
  // 调拨
  getTransferPage: (params: any) => request.get('/stock/transfer/page', { params }),
  createTransfer: (data: any) => request.post('/stock/transfer', data),
  confirmTransfer: (id: number) => request.put(`/stock/transfer/${id}/confirm`),
  // 盘点
  getCheckPage: (params: any) => request.get('/stock/check/page', { params }),
  createCheck: (data: any) => request.post('/stock/check', data),
  confirmCheck: (id: number) => request.put(`/stock/check/${id}/confirm`),
  // 退库
  getReturnPage: (params: any) => request.get('/stock/return/page', { params }),
  getReturn: (id: number) => request.get(`/stock/return/${id}`),
  createReturn: (data: any) => request.post('/stock/return', data),
  updateReturn: (id: number, data: any) => request.put(`/stock/return/${id}`, data),
  deleteReturn: (id: number) => request.delete(`/stock/return/${id}`),
  confirmReturn: (id: number) => request.put(`/stock/return/${id}/confirm`)
}

// ==================== 智能模块 ====================
export const intelligenceApi = {
  getAlerts: (params?: any) => request.get('/intelligence/alerts', { params }),
  getAlertCount: () => request.get('/intelligence/alerts/count'),
  handleAlert: (id: number) => request.put(`/intelligence/alerts/${id}/handle`),
  batchHandleAlerts: (ids: number[]) => request.put('/intelligence/alerts/batch-handle', ids),
  predict: (productId: number, days = 7) => request.get(`/intelligence/predict/${productId}?days=${days}`),
  replenishSuggest: (productId: number) => request.post(`/intelligence/replenish/suggest/${productId}`),
  topSelling: (limit = 10) => request.get(`/intelligence/analysis/top-selling?limit=${limit}`),
  turnover: () => request.get('/intelligence/analysis/turnover'),
  salesTrend: (days = 7) => request.get(`/intelligence/analysis/sales-trend?days=${days}`),
  categoryAnalysis: () => request.get('/intelligence/analysis/category'),
  stats: () => request.get('/intelligence/analysis/stats'),
  alertStats: () => request.get('/intelligence/analysis/alert-stats')
}

// ==================== 报表 ====================
export const reportApi = {
  dashboard: () => request.get('/report/dashboard'),
  inOutStock: (params: any) => request.get('/report/in-out-stock', { params }),
  inventoryDetail: (params: any) => request.get('/report/inventory-detail', { params }),
  exportExcel: (type: string) => request.get('/report/export/excel', { params: { type }, responseType: 'blob' })
}
