<template>
  <div class="divBox">
    <el-row :gutter="14" class="baseInfo">
      <el-col v-bind="grid" class="ivu-mb">
        <el-card :bordered="false" dis-hover :padding="12" shadow="never">
          <el-collapse v-model="activeCPU" >
            <el-collapse-item title="CPU信息" name="1">
              <el-table
                  :data="cpuInfo"
                  style="width: 100%">
                <el-table-column
                    prop="name"
                    label="属性">
                </el-table-column>
                <el-table-column
                    prop="value"
                    label="值">
                </el-table-column>
              </el-table>
            </el-collapse-item>
          </el-collapse>
        </el-card>
      </el-col>
      <el-col v-bind="grid" class="ivu-mb">
        <el-card :bordered="false" dis-hover :padding="12" shadow="never">
          <el-collapse v-model="activeMemory">
            <el-collapse-item title="内存信息" name="1">
              <el-table
                  :data="memoryInfo"
                  style="width: 100%">
                <el-table-column
                    prop="name"
                    label="属性">
                </el-table-column>
                <el-table-column
                    prop="value"
                    label="内存">
                </el-table-column>
                  <el-table-column
                      prop="JVM"
                      label="JVM">
                </el-table-column>
              </el-table>
            </el-collapse-item>
          </el-collapse>
        </el-card>
      </el-col>
      <el-col class="mt14">
        <el-card :bordered="false" dis-hover :padding="12" shadow="never">
          <el-collapse v-model="activeServer">
            <el-collapse-item title="服务器信息" name="1">
              <el-descriptions class="margin-top" :column="2" border>
                <el-descriptions-item>
                  <template slot="label">
                    服务器名称
                  </template>
                  {{serverInfo.serverName}}
                </el-descriptions-item>
                <el-descriptions-item>
                  <template slot="label">
                    操作系统
                  </template>
                  {{serverInfo.operatingSystem}}
                </el-descriptions-item>
                <el-descriptions-item>
                  <template slot="label">
                    服务器IP
                  </template>
                  {{serverInfo.serverIp}}
                </el-descriptions-item>
                <el-descriptions-item>
                  <template slot="label">
                    系统架构
                  </template>
                  {{serverInfo.systemArchitecture}}
                </el-descriptions-item>
              </el-descriptions>
            </el-collapse-item>
          </el-collapse>
        </el-card>
      </el-col>
      <el-col class="mt14">
        <el-card :bordered="false" dis-hover :padding="12" shadow="never">
          <el-collapse v-model="activeJvm">
            <el-collapse-item title="Java虚拟机信息" name="1">
              <el-descriptions class="margin-top" :column="2" border>
                <el-descriptions-item>
                  <template slot="label">
                    Java名称
                  </template>
                  {{jvmInfo.javaName}}
                </el-descriptions-item>
                <el-descriptions-item>
                  <template slot="label">
                    Java版本
                  </template>
                  {{jvmInfo.javaVersion}}
                </el-descriptions-item>
                <el-descriptions-item>
                  <template slot="label">
                    启动时间
                  </template>
                  {{jvmInfo.startTime}}
                </el-descriptions-item>
                <el-descriptions-item>
                  <template slot="label">
                    运行时长
                  </template>
                  {{jvmInfo.runningTime}}
                </el-descriptions-item>
                <el-descriptions-item>
                  <template slot="label">
                    安装路径
                  </template>
                  {{jvmInfo.installationPath}}
                </el-descriptions-item>
                <el-descriptions-item>
                  <template slot="label">
                    项目路径
                  </template>
                  {{jvmInfo.projectPath}}
                </el-descriptions-item>
                <el-descriptions-item>
                  <template slot="label">
                    运行参数
                  </template>
                  {{jvmInfo.operatingParameters}}
                </el-descriptions-item>
              </el-descriptions>
            </el-collapse-item>
          </el-collapse>
        </el-card>
      </el-col>
      <el-col class="mt14">
        <el-card :bordered="false" dis-hover :padding="12" shadow="never">
          <el-collapse v-model="activeDiskInfo">
            <el-collapse-item title="磁盘状态" name="1">
              <el-table
                  :data="diskInfoList"
                  style="width: 100%">
                <el-table-column
                    prop="driveLetterPath"
                    label="盘符路径">
                </el-table-column>
                <el-table-column
                    prop="fileSystem"
                    label="文件系统">
                </el-table-column>
                <el-table-column
                    prop="driveLetterType"
                    label="盘符类型">
                </el-table-column>
                <el-table-column
                    prop="totalSize"
                    label="总大小">
                </el-table-column>
                <el-table-column
                    prop="usableSize"
                    label="可用大小">
                </el-table-column>
                <el-table-column
                    prop="usedSize"
                    label="已用大小">
                </el-table-column>
                <el-table-column
                    prop="usedRate"
                    label="已用百分比">
                </el-table-column>
              </el-table>
            </el-collapse-item>
          </el-collapse>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { systemStateInfoApi } from '@/api/systemConfig';

export default {
  name: 'systemState',
  data() {
    return {
      grid: { xl: 12, lg: 12, md: 12, sm: 12, xs: 24 },
      cpuInfo: [], // CPU信息部分
      diskInfoList: [], // 磁盘状态信息部分
      jvmInfo: {}, // java虚拟机信息部分
      memoryInfo: [], // 内存信息部分
      serverInfo: {}, // 服务器信息部分
      activeCPU: ['1'],
      activeMemory:  ['1'],
      activeServer:  ['1'],
      activeJvm:  ['1'],
      activeDiskInfo:  ['1'],
    };
  },
  mounted() {
    this.getSystemStateInfo();
  },
  methods: {
    // 状态详情
    async getSystemStateInfo() {
      try {
        let res = await systemStateInfoApi();
        this.cpuInfo = [{
          name: '核心数',
          value: res.cpuInfo.coreNum,
        }, {
          name: '用户使用率',
          value: res.cpuInfo.userUsageRate,
        }, {
          name: '系统使用率',
          value: res.cpuInfo.systemUsageRate,
        }, {
          name: '当前空闲率',
          value: res.cpuInfo.idleRate,
        }]; // CPU信息部分
        this.diskInfoList = res.diskInfoList; // 磁盘状态信息部分
        this.jvmInfo = res.jvmInfo; // java虚拟机信息部分
        this.memoryInfo = [{
          name: '总内存',
          value: res.memoryInfo.memoryTotalMemory,
          JVM: res.memoryInfo.jvmTotalMemory
        }, {
          name: '已用内存',
          value: res.memoryInfo.memoryUsedMemory,
          JVM: res.memoryInfo.jvmUsedMemory
        }, {
          name: '剩余内存',
          value: res.memoryInfo.memoryRemainingMemory,
          JVM: res.memoryInfo.jvmRemainingMemory
        }, {
          name: '使用率',
          value: res.memoryInfo.memoryUsageRate,
          JVM: res.memoryInfo.jvmUsageRate
        }]; // 内存信息部分
        this.serverInfo = res.serverInfo; // 服务器信息部分
      } catch (e) {}
    },
  },
};
</script>

<style scoped></style>
