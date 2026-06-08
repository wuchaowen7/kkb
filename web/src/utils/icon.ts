import * as ElementPlusIconsVue from '@element-plus/icons-vue'

const iconMap: Record<string, any> = {
  home: 'HomeFilled',
  dashboard: 'BarChart3',
  product: 'Box',
  stock: 'ShoppingBag',
  alert: 'AlertTriangle',
  system: 'Settings',
  report: 'PieChart',
  user: 'Users',
  role: 'Key',
  menu: 'Menu',
  category: 'FolderOpen',
  warehouse: 'Layout',
  supplier: 'Users',
  inbound: 'Download',
  outbound: 'Upload',
  transfer: 'RefreshCw',
  check: 'FileText',
  analysis: 'Activity',
  Home: 'HomeFilled',
  HomeFilled: 'HomeFilled',
  ShoppingBag: 'ShoppingBag',
  Box: 'Box',
  Settings: 'Settings',
  BarChart3: 'BarChart3',
  AlertTriangle: 'AlertTriangle',
  Layout: 'Layout',
  Users: 'Users',
  FolderOpen: 'FolderOpen',
  Truck: 'Truck',
  FileText: 'FileText',
  RefreshCw: 'RefreshCw',
  PieChart: 'PieChart',
  Activity: 'Activity',
  Bell: 'Bell',
  User: 'User',
  Key: 'Key',
  Menu: 'Menu',
  Download: 'Download',
  Upload: 'Upload'
}

export function getIconComponent(iconName: string | undefined): any {
  if (!iconName) return null
  const name = iconName.replace(/-(\w)/g, (_, c) => c.toUpperCase())
  const mappedName = iconMap[name] || iconMap[iconName] || iconName
  return ElementPlusIconsVue[mappedName as keyof typeof ElementPlusIconsVue] || null
}

export default iconMap