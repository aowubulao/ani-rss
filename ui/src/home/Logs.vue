<template>
  <el-dialog v-model="logDialogVisible" title="日志" center>
    <div style="width: 100%;justify-content: space-between;align-items: center;" class="auto">
      <el-checkbox-group v-model:model-value="selectLevels" @change="()=>getHtmlLogs()">
        <el-checkbox v-for="item in levels" :label="item" size="large"/>
      </el-checkbox-group>
      <div style="display: flex;">
        <el-select
            v-model="selectLoggerNames"
            multiple
            collapse-tags
            placeholder="类名"
            style="width: 260px;"
            @change="()=>getHtmlLogs()"
        >
          <el-option
              v-for="item in loggerNames"
              :key="item"
              :label="item"
              :value="item"
          />
        </el-select>
        <div style="width: 4px;"></div>
        <el-button icon="Refresh" bg text @click="getLogs" :loading="getLogsLoading"/>
      </div>
    </div>
    <div id="#logs" style="background-color:#2e3440ff;
                  color:#d8dee9ff;
                  margin-top: 5px;"
         v-loading="loading">
      <el-scrollbar ref="scrollbarRef" height="450">
        <div ref="innerRef" style="min-height: 400px;" v-html="htmlLogs">
        </div>
      </el-scrollbar>
    </div>
  </el-dialog>
</template>

<script setup>
import {ref} from "vue";
import {codeToHtml} from 'shiki'
import api from "../api.js";

const logDialogVisible = ref(false)
const loading = ref(true)
const logs = ref([])
const scrollbarRef = ref()
const innerRef = ref()

const levels = ['DEBUG', 'INFO', 'WARN', 'ERROR']
const selectLevels = ref([])
const htmlLogs = ref('')
const loggerNames = ref([])
const selectLoggerNames = ref([])

const getHtmlLogs = async () => {
  let log = logs.value
  log = log.filter(it => selectLevels.value.indexOf(it['level']) > -1)
  if (selectLoggerNames.value.length) {
    log = log.filter(it => selectLoggerNames.value.indexOf(it['loggerName']) > -1)
  }
  let code = log.map(it => it['message']).join('\r\n');
  htmlLogs.value = await codeToHtml(code, {
    lang: 'log',
    theme: 'nord'
  })
  setTimeout(() => {
    scrollbarRef.value?.setScrollTop(innerRef.value.clientHeight)
  })
}

const showLogs = () => {
  logs.value = []
  logDialogVisible.value = true
  loading.value = true
  htmlLogs.value = ''
  getLogs()
  selectLevels.value = levels
}

const getLogsLoading = ref(false)

const getLogs = () => {
  getLogsLoading.value = true
  api.get('/api/logs')
      .then(async res => {
        logs.value = res.data
        for (let datum of res.data) {
          if (loggerNames.value.indexOf(datum['loggerName']) > -1) {
            continue
          }
          loggerNames.value.push(datum['loggerName'])
        }
        getHtmlLogs()
      })
      .finally(() => {
        loading.value = false
        getLogsLoading.value = false
      })
}

defineExpose({showLogs})
</script>

<style>
@media (min-width: 1000px) {
  .auto {
    display: flex;
  }
}


</style>
