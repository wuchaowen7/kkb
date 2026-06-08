<template>
  <div>
    <div class="toolbar">
      <h3>系统配置</h3>
      <el-button type="primary" @click="saveConfig">保存配置</el-button>
    </div>
    
    <el-card title="基本信息" class="config-card">
      <el-form :model="configForm" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="系统名称">
              <el-input v-model="configForm.appName" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="系统版本">
              <el-input v-model="configForm.appVersion" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="公司名称">
              <el-input v-model="configForm.companyName" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="版权信息">
              <el-input v-model="configForm.copyright" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </el-card>

    <el-card title="备份设置" class="config-card">
      <el-form :model="configForm" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="自动备份">
              <el-switch v-model="configForm.autoBackup" active-text="开启" inactive-text="关闭" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="备份时间">
              <el-time-picker v-model="configForm.backupTime" format="HH:mm" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </el-card>

    <el-card title="预警设置" class="config-card">
      <el-form :model="configForm" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="预警阈值(%)">
              <el-input-number v-model="configForm.alertThreshold" :min="1" :max="100" style="width:150px" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="临期预警天数">
              <el-input-number v-model="configForm.expiryWarningDays" :min="1" :max="365" style="width:150px" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </el-card>

    <el-card title="安全设置" class="config-card">
      <el-form :model="configForm" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="会话超时(分钟)">
              <el-input-number v-model="configForm.sessionTimeout" :min="5" :max="120" style="width:150px" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="语言">
              <el-select v-model="configForm.language">
                <el-option label="中文" value="zh-CN" />
                <el-option label="English" value="en-US" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { reactive, onMounted } from 'vue'
import { systemApi } from '@/api/modules'
import { ElMessage } from 'element-plus'

const configForm = reactive({
  appName: '',
  appVersion: '',
  companyName: '',
  copyright: '',
  autoBackup: true,
  backupTime: '02:00',
  alertThreshold: 10,
  expiryWarningDays: 30,
  sessionTimeout: 30,
  language: 'zh-CN',
  theme: 'default'
})

onMounted(() => loadConfig())

async function loadConfig() {
  const res = await systemApi.getConfig()
  Object.assign(configForm, res.data)
}

async function saveConfig() {
  await systemApi.updateConfig(configForm)
  ElMessage.success('配置保存成功')
}
</script>

<style scoped>
.toolbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.config-card { margin-bottom: 16px; }
</style>