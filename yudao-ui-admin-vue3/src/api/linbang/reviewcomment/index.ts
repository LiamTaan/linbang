import request from '@/config/axios'

import type { Dayjs } from 'dayjs'

/** 评价信息 */
export interface ReviewComment {
          id: number; // 主键
          orderId?: number; // 订单ID
          orderNo?: string
          unitId: number; // 单元ID
          unitNo?: string
          fromUserId?: number; // 评价发起人
          fromUserNo?: string
          fromUserNickname?: string
          fromUserMobile?: string
          toUserId?: number; // 评价目标用户
          toUserNo?: string
          toUserNickname?: string
          toUserMobile?: string
          starLevel?: number; // 星级
          content: string; // 评价内容
          isAutoReview?: boolean; // 是否自动评价
          isContentSupplemented?: boolean; // 自动评价文字是否已补充
          editDeadlineTime?: string | Dayjs; // 可编辑截止时间
          lastEditTime?: string | Dayjs; // 最后编辑时间
          editCount?: number; // 编辑次数
          status?: string; // 状态
          createTime?: string | Dayjs
  }

export interface ReviewCommentDetail extends ReviewComment {
  order?: {
    id?: number
    orderNo?: string
    title?: string
    status?: string
    userId?: number
    merchantId?: number
    orderAmount?: number
  }
  unit?: {
    id?: number
    orderId?: number
    unitNo?: string
    unitSeq?: number
    unitTitle?: string
    unitAmount?: number
    status?: string
    isLocked?: boolean
    lockReason?: string
    merchantId?: number
    acceptDeadlineTime?: string | Dayjs
    finishTime?: string | Dayjs
  }
  fromUser?: {
    id?: number
    userNo?: string
    mobile?: string
    nickname?: string
    currentRoleCode?: string
    status?: string
    lastLoginTime?: string | Dayjs
  }
  toUser?: {
    id?: number
    userNo?: string
    mobile?: string
    nickname?: string
    currentRoleCode?: string
    status?: string
    lastLoginTime?: string | Dayjs
  }
  toMerchant?: {
    id?: number
    userId?: number
    merchantName?: string
    contactName?: string
    contactMobile?: string
    status?: string
    acceptStatus?: string
    creditScore?: number
    creditLevel?: string
  }
  summary?: {
    sameOrderReviewCount?: number
    sameTargetReviewCount?: number
    positiveReviewCount?: number
    neutralReviewCount?: number
    negativeReviewCount?: number
    autoReviewCount?: number
    creditRecordCount?: number
  }
  relatedReviews?: Array<{
    id?: number
    fromUserId?: number
    fromUserNo?: string
    fromUserNickname?: string
    fromUserMobile?: string
    toUserId?: number
    toUserNo?: string
    toUserNickname?: string
    toUserMobile?: string
    starLevel?: number
    isAutoReview?: boolean
    status?: string
    content?: string
    createTime?: string | Dayjs
  }>
  creditRecords?: Array<{
    id?: number
    ruleCode?: string
    ruleName?: string
    scoreChange?: number
    beforeScore?: number
    afterScore?: number
    triggerType?: string
    bizType?: string
    bizId?: number
    remark?: string
    createTime?: string | Dayjs
  }>
}

// 评价 API
export const ReviewCommentApi = {
  // 查询评价分页
  getReviewCommentPage: async (params: any) => {
    return await request.get({ url: `/review/comment/page`, params })
  },

  // 查询评价详情
  getReviewComment: async (id: number) => {
    return await request.get<ReviewCommentDetail>({ url: `/review/comment/get?id=` + id })
  },

  // 新增评价
  createReviewComment: async (data: ReviewComment) => {
    return await request.post({ url: `/review/comment/create`, data })
  },

  // 修改评价
  updateReviewComment: async (data: ReviewComment) => {
    return await request.put({ url: `/review/comment/update`, data })
  },

  // 删除评价
  deleteReviewComment: async (id: number) => {
    return await request.delete({ url: `/review/comment/delete?id=` + id })
  },

  /** 批量删除评价 */
  deleteReviewCommentList: async (ids: number[]) => {
    return await request.delete({ url: `/review/comment/delete-list?ids=${ids.join(',')}` })
  },

  // 导出评价 Excel
  exportReviewComment: async (params) => {
    return await request.download({ url: `/review/comment/export-excel`, params })
  }
}
