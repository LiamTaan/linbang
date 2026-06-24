<template>
  <Dialog v-model="dialogVisible" title="信用记录详情" width="960px" :loading="detailLoading">
    <el-descriptions :column="2" label-class-name="desc-label">
      <el-descriptions-item label="用户">{{ formatUserDisplay() }}</el-descriptions-item>
      <el-descriptions-item label="服务商">{{ formatMerchantDisplay() }}</el-descriptions-item>
      <el-descriptions-item label="规则">{{ formatRuleDisplay() }}</el-descriptions-item>
      <el-descriptions-item label="规则编码">{{ detailData.ruleCode || '-' }}</el-descriptions-item>
      <el-descriptions-item label="规则名称">{{ detailData.ruleName || '-' }}</el-descriptions-item>
      <el-descriptions-item label="分值变动">
        <el-tag :type="(detailData.scoreChange || 0) >= 0 ? 'success' : 'danger'">
          {{ detailData.scoreChange ?? 0 }}
        </el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="触发类型">{{ formatTriggerType(detailData.triggerType) }}</el-descriptions-item>
      <el-descriptions-item label="变动前分值">{{ detailData.beforeScore ?? '-' }}</el-descriptions-item>
      <el-descriptions-item label="变动后分值">{{ detailData.afterScore ?? '-' }}</el-descriptions-item>
      <el-descriptions-item label="当前信用分">{{ detailData.currentScore ?? '-' }}</el-descriptions-item>
      <el-descriptions-item label="当前信用等级">{{ detailData.creditLevel || '-' }}</el-descriptions-item>
      <el-descriptions-item label="业务类型">{{ formatBizType(detailData.bizType) }}</el-descriptions-item>
      <el-descriptions-item label="业务对象">{{ detailData.bizDisplay || '-' }}</el-descriptions-item>
      <el-descriptions-item label="创建时间">{{ formatDate(detailData.createTime) }}</el-descriptions-item>
      <el-descriptions-item label="更新时间">{{ formatDate(detailData.updateTime) }}</el-descriptions-item>
      <el-descriptions-item label="备注" :span="2">{{ detailData.remark || '-' }}</el-descriptions-item>
    </el-descriptions>
    <el-divider />
    <el-descriptions :column="4" border>
      <el-descriptions-item label="同用户记录数">
        {{ detailData.sameUserRecordCount ?? 0 }}
      </el-descriptions-item>
      <el-descriptions-item label="同规则记录数">
        {{ detailData.sameRuleRecordCount ?? 0 }}
      </el-descriptions-item>
      <el-descriptions-item label="加分记录数">
        {{ detailData.positiveRecordCount ?? 0 }}
      </el-descriptions-item>
      <el-descriptions-item label="扣分记录数">
        {{ detailData.negativeRecordCount ?? 0 }}
      </el-descriptions-item>
    </el-descriptions>
    <el-divider content-position="left">用户信息</el-divider>
    <el-descriptions :column="2" border>
      <el-descriptions-item label="用户编号">{{ detailData.user?.userNo || '-' }}</el-descriptions-item>
      <el-descriptions-item label="手机号">{{ detailData.user?.mobile || '-' }}</el-descriptions-item>
      <el-descriptions-item label="昵称">{{ detailData.user?.nickname || '-' }}</el-descriptions-item>
      <el-descriptions-item label="当前角色">
        <dict-tag
          v-if="detailData.user?.currentRoleCode"
          :type="DICT_TYPE.LB_ROLE_CODE"
          :value="detailData.user.currentRoleCode"
        />
        <span v-else>-</span>
      </el-descriptions-item>
      <el-descriptions-item label="状态">{{ formatEnableStatus(detailData.user?.status) }}</el-descriptions-item>
    </el-descriptions>
    <el-divider content-position="left">服务商信息</el-divider>
    <el-descriptions :column="2" border>
      <el-descriptions-item label="服务商名称">
        {{ detailData.merchant?.merchantName || '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="联系人">
        {{ detailData.merchant?.contactName || '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="联系电话">
        {{ detailData.merchant?.contactMobile || '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="服务商状态">
        {{ formatEnableStatus(detailData.merchant?.status) }}
      </el-descriptions-item>
      <el-descriptions-item label="接单状态">
        {{ formatAcceptStatus(detailData.merchant?.acceptStatus) }}
      </el-descriptions-item>
      <el-descriptions-item label="服务商信用分">
        {{ detailData.merchant?.creditScore ?? '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="服务商信用等级">
        {{ detailData.merchant?.creditLevel || '-' }}
      </el-descriptions-item>
    </el-descriptions>
    <el-divider content-position="left">规则信息</el-divider>
    <el-descriptions :column="2" border>
      <el-descriptions-item label="规则编码">{{ detailData.rule?.ruleCode || '-' }}</el-descriptions-item>
      <el-descriptions-item label="规则名称">{{ detailData.rule?.ruleName || '-' }}</el-descriptions-item>
      <el-descriptions-item label="规则分值">{{ detailData.rule?.scoreChange ?? '-' }}</el-descriptions-item>
      <el-descriptions-item label="规则触发类型">
        {{ formatTriggerType(detailData.rule?.triggerType) }}
      </el-descriptions-item>
      <el-descriptions-item label="规则状态">
        {{ formatEnableStatus(detailData.rule?.status) }}
      </el-descriptions-item>
      <el-descriptions-item label="规则创建时间">
        {{ formatDate(detailData.rule?.createTime) }}
      </el-descriptions-item>
    </el-descriptions>
  </Dialog>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { DICT_TYPE } from '@/utils/dict'
import { formatDate } from '@/utils/formatTime'
import { CreditRecordApi, type CreditRecordDetail } from '@/api/linbang/creditrecord'
import { formatAcceptStatus, formatBizType, formatEnableStatus, formatTriggerType } from '../utils/display'

defineOptions({ name: 'CreditRecordDetailDialog' })

const dialogVisible = ref(false)
const detailLoading = ref(false)
const detailData = ref<CreditRecordDetail>({} as CreditRecordDetail)

const formatUserDisplay = () => {
  const user = detailData.value.user
  const summary = [user?.nickname, user?.mobile, user?.userNo].filter(Boolean).join(' / ')
  return summary || (detailData.value.userId ? '用户信息缺失' : '-')
}

const formatMerchantDisplay = () => {
  const merchant = detailData.value.merchant
  const summary = [merchant?.merchantName, merchant?.contactMobile, merchant?.contactName]
    .filter(Boolean)
    .join(' / ')
  return summary || (detailData.value.merchantId ? '服务商信息缺失' : '-')
}

const formatRuleDisplay = () => {
  const rule = detailData.value.rule
  const summary = [rule?.ruleName, rule?.ruleCode].filter(Boolean).join(' / ')
  return summary || (detailData.value.ruleId ? '规则信息缺失' : '-')
}

const open = async (id: number) => {
  dialogVisible.value = true
  detailLoading.value = true
  try {
    detailData.value = await CreditRecordApi.getCreditRecord(id)
  } finally {
    detailLoading.value = false
  }
}

defineExpose({ open })
</script>
