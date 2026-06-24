<template>
  <Dialog v-model="dialogVisible" title="分账规则详情" width="760px" :loading="detailLoading">
    <el-descriptions :column="2" label-class-name="desc-label">
      <el-descriptions-item label="规则名称">{{ detailData.ruleName || '-' }}</el-descriptions-item>
      <el-descriptions-item label="城市等级">{{ detailData.cityLevel || '-' }}</el-descriptions-item>
      <el-descriptions-item label="类目">
        {{ detailData.categoryName || (detailData.categoryId ? '类目信息缺失' : '-') }}
      </el-descriptions-item>
      <el-descriptions-item label="状态">
        <el-tag :type="detailData.status === 'ENABLE' ? 'success' : 'info'">
          {{ formatEnableStatus(detailData.status) }}
        </el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="生效时间">{{ formatDate(detailData.effectiveTime) }}</el-descriptions-item>
      <el-descriptions-item label="创建时间">{{ formatDate(detailData.createTime) }}</el-descriptions-item>
      <el-descriptions-item label="更新时间">{{ formatDate(detailData.updateTime) }}</el-descriptions-item>
    </el-descriptions>
    <el-divider />
    <el-descriptions :column="3" border>
      <el-descriptions-item label="服务商比例">{{ detailData.merchantRate ?? 0 }}</el-descriptions-item>
      <el-descriptions-item label="平台比例">{{ detailData.platformRate ?? 0 }}</el-descriptions-item>
      <el-descriptions-item label="合作商比例">{{ detailData.partnerRate ?? 0 }}</el-descriptions-item>
      <el-descriptions-item label="推广员比例">{{ detailData.promoterRate ?? 0 }}</el-descriptions-item>
      <el-descriptions-item label="个税代扣比例">
        {{ detailData.taxWithholdRate ?? 0 }}
      </el-descriptions-item>
      <el-descriptions-item label="最低提现金额">
        {{ detailData.minWithdrawAmount ?? 0 }}
      </el-descriptions-item>
    </el-descriptions>
    <el-divider />
    <el-descriptions :column="3" border>
      <el-descriptions-item label="总比例">{{ detailData.totalRate ?? 0 }}</el-descriptions-item>
      <el-descriptions-item label="收益侧比例">{{ detailData.incomeRate ?? 0 }}</el-descriptions-item>
      <el-descriptions-item label="比例是否平衡">
        <el-tag :type="detailData.rateBalanced ? 'success' : 'danger'">
          {{ formatBooleanYesNo(detailData.rateBalanced) }}
        </el-tag>
      </el-descriptions-item>
    </el-descriptions>
  </Dialog>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { formatDate } from '@/utils/formatTime'
import { DivideRuleApi, type DivideRuleDetail } from '@/api/linbang/dividerule'
import { formatBooleanYesNo, formatEnableStatus } from '../utils/display'

defineOptions({ name: 'DivideRuleDetailDialog' })

const dialogVisible = ref(false)
const detailLoading = ref(false)
const detailData = ref<DivideRuleDetail>({} as DivideRuleDetail)

const open = async (id: number) => {
  dialogVisible.value = true
  detailLoading.value = true
  try {
    detailData.value = await DivideRuleApi.getDivideRule(id)
  } finally {
    detailLoading.value = false
  }
}

defineExpose({ open })
</script>
