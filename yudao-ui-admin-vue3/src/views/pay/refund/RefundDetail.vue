<template>
  <Dialog v-model="dialogVisible" title="详情" width="700px" :loading="detailLoading">
    <el-descriptions :column="2" label-class-name="desc-label">
      <el-descriptions-item label="商户退款单号">
        <el-tag size="small">{{ refundDetail.merchantRefundId }}</el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="渠道退款单号">
        <el-tag type="success" size="small" v-if="refundDetail.channelRefundNo">{{
          refundDetail.channelRefundNo
        }}</el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="商户支付单号">
        <el-tag size="small">{{ refundDetail.merchantOrderId }}</el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="渠道支付单号">
        <el-tag type="success" size="small">{{ refundDetail.channelOrderNo }}</el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="应用编号">{{ refundDetail.appId }}</el-descriptions-item>
      <el-descriptions-item label="应用名称">{{ refundDetail.appName }}</el-descriptions-item>
      <el-descriptions-item label="支付金额">
        <el-tag type="success" size="small">
          ￥{{ (refundDetail.payPrice! / 100.0).toFixed(2) }}
        </el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="退款金额">
        <el-tag size="small" type="danger">
          ￥{{ (refundDetail.refundPrice! / 100.0).toFixed(2) }}
        </el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="退款状态">
        <dict-tag
          v-if="refundDetail.status !== undefined"
          :type="DICT_TYPE.PAY_REFUND_STATUS"
          :value="refundDetail.status"
        />
      </el-descriptions-item>
      <el-descriptions-item label="审核状态">
        <dict-tag
          v-if="refundDetail.auditStatus"
          :type="DICT_TYPE.LB_AUDIT_STATUS"
          :value="refundDetail.auditStatus"
        />
      </el-descriptions-item>
      <el-descriptions-item label="退款时间">
        {{ formatDate(refundDetail.successTime) }}
      </el-descriptions-item>
      <el-descriptions-item label="审核时间">
        {{ formatDate(refundDetail.auditTime) }}
      </el-descriptions-item>
      <el-descriptions-item label="创建时间">
        {{ formatDate(refundDetail.createTime) }}
      </el-descriptions-item>
      <el-descriptions-item label="更新时间">
        {{ formatDate(refundDetail.updateTime) }}
      </el-descriptions-item>
    </el-descriptions>
    <!-- 分割线 -->
    <el-divider />
    <el-descriptions :column="2" label-class-name="desc-label">
      <el-descriptions-item label="退款渠道">
        <dict-tag
          v-if="refundDetail.channelCode"
          :type="DICT_TYPE.PAY_CHANNEL_CODE"
          :value="refundDetail.channelCode"
        />
      </el-descriptions-item>
      <el-descriptions-item label="退款原因">{{ refundDetail.reason }}</el-descriptions-item>
      <el-descriptions-item label="退款 IP">{{ refundDetail.userIp }}</el-descriptions-item>
      <el-descriptions-item label="通知 URL">{{ refundDetail.notifyUrl }}</el-descriptions-item>
    </el-descriptions>
    <!-- 分割线 -->
    <el-divider />
    <el-descriptions :column="2" label-class-name="desc-label">
      <el-descriptions-item label="渠道错误码">
        {{ refundDetail.channelErrorCode }}
      </el-descriptions-item>
      <el-descriptions-item label="渠道错误码描述">
        {{ refundDetail.channelErrorMsg }}
      </el-descriptions-item>
      <el-descriptions-item label="审核备注">
        {{ refundDetail.auditRemark || '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="驳回原因">
        {{ refundDetail.rejectReason || '-' }}
      </el-descriptions-item>
    </el-descriptions>
    <el-descriptions :column="1" label-class-name="desc-label" direction="vertical" border>
      <el-descriptions-item label="支付通道异步回调内容">
        <el-text style="overflow-wrap: anywhere; white-space: pre-wrap">
          {{ refundDetail.channelNotifyData }}
        </el-text>
      </el-descriptions-item>
    </el-descriptions>
    <template v-if="refundDetail.bizContext">
      <el-divider />
      <el-descriptions :column="2" label-class-name="desc-label">
        <el-descriptions-item label="关联订单ID">
          {{ refundDetail.bizContext.order?.id ?? refundDetail.bizContext.orderId ?? '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="关联单元ID">
          {{ refundDetail.bizContext.unit?.id ?? refundDetail.bizContext.unitId ?? '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="订单号">
          {{ refundDetail.bizContext.order?.orderNo || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="单元号">
          {{ refundDetail.bizContext.unit?.unitNo || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="订单标题">
          {{ refundDetail.bizContext.order?.title || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="单元标题">
          {{ refundDetail.bizContext.unit?.unitTitle || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="订单状态">
          <dict-tag
            v-if="refundDetail.bizContext.order?.status"
            :type="DICT_TYPE.LB_ORDER_STATUS"
            :value="refundDetail.bizContext.order.status"
          />
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="单元状态">
          <dict-tag
            v-if="refundDetail.bizContext.unit?.status"
            :type="DICT_TYPE.LB_ORDER_UNIT_STATUS"
            :value="refundDetail.bizContext.unit.status"
          />
          <span v-else>-</span>
        </el-descriptions-item>
      </el-descriptions>
      <el-divider content-position="left">退款关联流水</el-divider>
      <el-table :data="refundDetail.bizContext.walletFlows || []" size="small" border>
        <el-table-column label="流水号" prop="flowNo" min-width="160" />
        <el-table-column label="业务类型" prop="bizType" width="120">
          <template #default="{ row }">{{ formatBizType(row.bizType) }}</template>
        </el-table-column>
        <el-table-column label="流水类型" prop="flowType" width="120">
          <template #default="{ row }">{{ formatFlowType(row.flowType) }}</template>
        </el-table-column>
        <el-table-column label="钱包账户ID" prop="walletAccountId" width="130" />
        <el-table-column label="备注" prop="remark" min-width="180" />
        <el-table-column label="创建时间" prop="createTime" :formatter="dateFormatter" width="180" />
      </el-table>
      <el-divider content-position="left">投诉与申诉</el-divider>
      <el-table :data="refundDetail.bizContext.complaints || []" size="small" border>
        <el-table-column label="投诉单号" prop="complaintNo" min-width="160" />
        <el-table-column label="投诉类型" prop="complaintType" width="120">
          <template #default="{ row }">{{ formatComplaintType(row.complaintType) }}</template>
        </el-table-column>
        <el-table-column label="状态" prop="status" width="120">
          <template #default="{ row }">
            <dict-tag v-if="row.status" :type="DICT_TYPE.LB_COMPLAINT_STATUS" :value="row.status" />
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="处理结果" prop="resultDesc" min-width="180" />
        <el-table-column label="创建时间" prop="createTime" :formatter="dateFormatter" width="180" />
      </el-table>
      <el-table :data="refundDetail.bizContext.appeals || []" size="small" border style="margin-top: 12px">
        <el-table-column label="申诉单号" prop="appealNo" min-width="160" />
        <el-table-column label="申诉类型" prop="appealType" width="120">
          <template #default="{ row }">{{ formatAppealType(row.appealType) }}</template>
        </el-table-column>
        <el-table-column label="状态" prop="status" width="120">
          <template #default="{ row }">
            <dict-tag v-if="row.status" :type="DICT_TYPE.LB_APPEAL_STATUS" :value="row.status" />
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="审核状态" prop="auditStatus" width="120">
          <template #default="{ row }">
            <dict-tag v-if="row.auditStatus" :type="DICT_TYPE.LB_AUDIT_STATUS" :value="row.auditStatus" />
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="审核备注" prop="auditRemark" min-width="180" />
        <el-table-column label="创建时间" prop="createTime" :formatter="dateFormatter" width="180" />
      </el-table>
      <el-divider content-position="left">订单操作日志</el-divider>
      <el-table :data="refundDetail.bizContext.operateLogs || []" size="small" border>
        <el-table-column label="操作类型" prop="operateType" width="140">
          <template #default="{ row }">{{ formatOperateType(row.operateType) }}</template>
        </el-table-column>
        <el-table-column label="操作角色" prop="operateRole" width="120">
          <template #default="{ row }">{{ formatOperateRole(row.operateRole) }}</template>
        </el-table-column>
        <el-table-column label="操作人" prop="operateBy" width="100" />
        <el-table-column label="前状态" prop="beforeStatus" width="120">
          <template #default="{ row }">{{ formatOperateStatus(row.beforeStatus) }}</template>
        </el-table-column>
        <el-table-column label="后状态" prop="afterStatus" width="120">
          <template #default="{ row }">{{ formatOperateStatus(row.afterStatus) }}</template>
        </el-table-column>
        <el-table-column label="备注" prop="remark" min-width="180" />
        <el-table-column label="操作时间" prop="operateTime" :formatter="dateFormatter" width="180" />
      </el-table>
    </template>
  </Dialog>
</template>
<script lang="ts" setup>
import { DICT_TYPE } from '@/utils/dict'
import { formatDate, dateFormatter } from '@/utils/formatTime'
import * as RefundApi from '@/api/pay/refund'
import {
  formatAppealType,
  formatBizType,
  formatComplaintType,
  formatFlowType,
  formatOperateRole,
  formatOperateStatus,
  formatOperateType
} from '@/views/linbang/utils/display'

defineOptions({ name: 'PayRefundDetail' })

const dialogVisible = ref(false) // 弹窗的是否展示
const detailLoading = ref(false) // 表单的加载中
const refundDetail = ref<RefundApi.RefundDetailVO>({})

/** 打开弹窗 */
const open = async (id: number) => {
  // 设置数据
  detailLoading.value = true
  dialogVisible.value = true
  try {
    const detail = await RefundApi.getRefund(id)
    if (detail.id) {
      detail.bizContext = await RefundApi.getRefundBizContext(detail.id)
      if (!detail.bizContext.walletFlows) {
        detail.bizContext.walletFlows = []
      }
      if (!detail.bizContext.complaints) {
        detail.bizContext.complaints = []
      }
      if (!detail.bizContext.appeals) {
        detail.bizContext.appeals = []
      }
      if (!detail.bizContext.operateLogs) {
        detail.bizContext.operateLogs = []
      }
    }
    refundDetail.value = detail
  } finally {
    detailLoading.value = false
  }
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗
</script>
