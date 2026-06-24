import request from '@/config/axios'
import { buildDynamicKeyHeaders } from '@/api/linbang/security'

/** 分账规则信息 */
export interface DivideRule {
  id: number
  ruleName?: string
  cityLevel?: string
  categoryId?: number
  categoryName?: string
  merchantRate?: number
  platformRate?: number
  partnerRate?: number
  promoterRate?: number
  taxWithholdRate?: number
  minWithdrawAmount?: number
  status?: string
  effectiveTime?: string | number
  createTime?: string
}

export interface DivideRuleDetail extends DivideRule {
  categoryName?: string
  updateTime?: string
  totalRate?: number
  incomeRate?: number
  rateBalanced?: boolean
}

export interface DivideRuleFormData {
  id?: number
  ruleName?: string
  cityLevel?: string
  categoryId?: number
  merchantRate?: number
  platformRate?: number
  partnerRate?: number
  promoterRate?: number
  taxWithholdRate?: number
  minWithdrawAmount?: number
  status?: string
  effectiveTime?: string | number
}

// 分账规则 API
export const DivideRuleApi = {
  // 查询分账规则分页
  getDivideRulePage: async (params: any) => {
    return await request.get({ url: `/wallet/divide-rule/page`, params })
  },

  // 查询分账规则详情
  getDivideRule: async (id: number) => {
    return await request.get<DivideRuleDetail>({ url: `/wallet/divide-rule/get?id=` + id })
  },

  // 新增分账规则
  createDivideRule: async (data: DivideRuleFormData, verifyToken?: string) => {
    return await request.post({
      url: `/wallet/divide-rule/create`,
      data,
      headers: buildDynamicKeyHeaders(verifyToken)
    })
  },

  // 修改分账规则
  updateDivideRule: async (data: DivideRuleFormData, verifyToken?: string) => {
    return await request.put({
      url: `/wallet/divide-rule/update`,
      data,
      headers: buildDynamicKeyHeaders(verifyToken)
    })
  },

  // 删除分账规则
  deleteDivideRule: async (id: number) => {
    return await request.delete({ url: `/wallet/divide-rule/delete?id=` + id })
  },

  /** 批量删除分账规则 */
  deleteDivideRuleList: async (ids: number[]) => {
    return await request.delete({ url: `/wallet/divide-rule/delete-list?ids=${ids.join(',')}` })
  },

  // 导出分账规则 Excel
  exportDivideRule: async (params: any) => {
    return await request.download({ url: `/wallet/divide-rule/export-excel`, params })
  }
}
