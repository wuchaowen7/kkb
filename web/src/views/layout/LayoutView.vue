<template>
  <div class="layout-container">
    <!-- 侧边栏 -->
    <el-aside :width="isCollapse ? '64px' : '220px'" class="layout-aside">
      <div class="logo">
        <img src="@/assets/wcw.png" v-if="!isCollapse" />
        <span v-if="!isCollapse">智能库存管理系统</span>
        <span v-else class="logo-mini">ISM</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        router
        :collapse="isCollapse"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
      >
        <template v-for="menu in permissionStore.menuTree" :key="menu.id">
          <el-sub-menu v-if="hasChildMenu(menu)" :index="menu.path || '' + menu.id">
            <template #title>
              <el-icon><component :is="getMenuIcon(menu.icon)" /></el-icon>
              <span>{{ menu.menuName }}</span>
            </template>
            <el-menu-item
              v-for="child in menu.children?.filter(c => c.menuType !== 'F')"
              :key="child.id"
              :index="child.path || ''"
            >
              <el-icon><component :is="getMenuIcon(child.icon)" /></el-icon>
              <span>{{ child.menuName }}</span>
            </el-menu-item>
          </el-sub-menu>
          <el-menu-item v-else :index="menu.path || ''">
            <el-icon><component :is="getMenuIcon(menu.icon)" /></el-icon>
            <span>{{ menu.menuName }}</span>
          </el-menu-item>
        </template>
      </el-menu>
    </el-aside>
    <!-- 主内容区 -->
    <div class="main-content" :style="{ marginLeft: isCollapse ? '64px' : '220px' }">
      <el-header class="layout-header">
        <div class="header-left">
          <el-icon @click="isCollapse = !isCollapse" class="collapse-btn">
            <Fold v-if="!isCollapse" /><Expand v-else />
          </el-icon>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item>{{ route.meta.title }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-badge :value="alertCount" :max="99" class="alert-badge">
            <el-icon :size="20"><Bell /></el-icon>
          </el-badge>
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-avatar :size="32" icon="UserFilled" />
              <span class="username">{{ userStore.realName || userStore.username }}</span>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <el-main class="main-scroll">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { usePermissionStore, type MenuItem } from '@/stores/permission'
import { getIconComponent } from '@/utils/icon'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const permissionStore = usePermissionStore()

const isCollapse = ref(false)
const alertCount = ref(0)

const activeMenu = computed(() => route.path)

function hasChildMenu(menu: MenuItem): boolean {
  return !!(menu.children && menu.children.some(c => c.menuType !== 'F'))
}

function getMenuIcon(iconName?: string) {
  return getIconComponent(iconName) || getIconComponent('Menu')
}

function handleCommand(cmd: string) {
  if (cmd === 'logout') {
    userStore.logout()
  } else if (cmd === 'profile') {
    router.push('/system/profile')
  }
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
  width: 100%;
  overflow: hidden;
  display: flex;
}
.layout-aside {
  background-color: #304156;
  overflow-y: auto;
  position: fixed;
  left: 0;
  top: 0;
  bottom: 0;
  z-index: 100;
  transition: width 0.3s ease;
  flex-shrink: 0;
}
.layout-aside .logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 18px;
  font-weight: bold;
  border-bottom: 1px solid #4a5a6a;
}
.layout-aside .logo img {
  width: 32px;
  margin-right: 8px;
}
.logo-mini {
  font-size: 16px;
  letter-spacing: 2px;
}
.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
  transition: margin-left 0.3s ease;
}
.layout-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #fff;
  border-bottom: 1px solid #e6e6e6;
  padding: 0 20px;
  height: 60px;
  flex-shrink: 0;
}
.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}
.collapse-btn {
  font-size: 20px;
  cursor: pointer;
}
.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}
.alert-badge {
  cursor: pointer;
}
.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}
.username {
  font-size: 14px;
}
.el-menu {
  border-right: none;
}
.main-scroll {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
}
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* 响应式适配 */
@media (max-width: 768px) {
  .layout-aside {
    width: 64px !important;
  }
  .main-content {
    margin-left: 64px !important;
  }
  .header-left {
    flex-wrap: wrap;
    gap: 8px;
  }
  .header-right {
    gap: 12px;
  }
  .username {
    display: none;
  }
}

@media (max-width: 480px) {
  .layout-header {
    padding: 0 10px;
  }
  .el-breadcrumb {
    display: none;
  }
  .main-scroll {
    padding: 8px;
  }
}
</style>
