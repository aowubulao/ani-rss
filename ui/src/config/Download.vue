<template>
  <el-form label-width="auto"
           @submit="(event)=>{
                    event.preventDefault()
                   }">
    <el-form-item label="下载工具">
      <el-select v-model:model-value="props.config.download">
        <el-option v-for="item in downloadSelect"
                   :key="item"
                   :label="item"
                   :value="item"/>
      </el-select>
    </el-form-item>
    <el-form-item label="地址">
      <el-input v-model:model-value="props.config.host" placeholder="http://192.168.1.66:8080"></el-input>
    </el-form-item>
    <template v-if="props.config.download !== 'Aria2'">
      <el-form-item label="用户名">
        <el-input v-model:model-value="props.config.username" placeholder="username"></el-input>
      </el-form-item>
      <el-form-item label="密码">
        <el-input show-password v-model:model-value="props.config.password" placeholder="password"></el-input>
      </el-form-item>
    </template>
    <el-form-item label="RPC 密钥" v-else>
      <el-input show-password v-model:model-value="props.config.password" placeholder=""></el-input>
    </el-form-item>
    <el-form-item>
      <div style="display:flex;width: 100%;justify-content: end;">
        <el-button @click="downloadLoginTest" bg text :loading="downloadLoginTestLoading">测试</el-button>
      </div>
    </el-form-item>
    <el-form-item label="保存位置">
      <el-input v-model:model-value="props.config.downloadPath" placeholder="/downloads/media/anime"></el-input>
    </el-form-item>
    <el-form-item label="剧场版保存位置">
      <el-input v-model:model-value="props.config.ovaDownloadPath" placeholder="/downloads/media/ova"></el-input>
    </el-form-item>
    <el-form-item label="自动删除">
      <div>
        <el-switch v-model:model-value="props.config.delete"></el-switch>
        <br>
        <el-text class="mx-1" size="small">
          自动删除已完成的任务, 不会删除本地文件
        </el-text>
      </div>
    </el-form-item>
    <el-form-item label="拼音首字母">
      <div>
        <el-switch v-model:model-value="props.config.acronym"></el-switch>
        <br>
        <el-text class="mx-1" size="small">
          存放到 #,0,A-Z 文件夹下
        </el-text>
      </div>
    </el-form-item>
    <el-form-item label="同时下载数量限制">
      <div>
        <el-input-number v-model:model-value="props.config.downloadCount" min="0"></el-input-number>
        <div>
          设置为时 0 不做限制
        </div>
      </div>
    </el-form-item>
    <el-form-item label="检测是否死种">
      <el-switch v-model:model-value="props.config.watchErrorTorrent"/>
    </el-form-item>
  </el-form>
</template>

<script setup>


import {ref} from "vue";
import api from "../api.js";
import {ElMessage} from "element-plus";

const downloadSelect = ref([
  'qBittorrent',
  'Transmission',
  'Aria2'
])

const downloadLoginTestLoading = ref(false)
const downloadLoginTest = () => {
  downloadLoginTestLoading.value = true
  api.post("/api/downloadLoginTestLoading", props.config)
      .then(res => {
        ElMessage.success(res.message)
      })
      .finally(() => {
        downloadLoginTestLoading.value = false
      })
}

let props = defineProps(['config'])
</script>