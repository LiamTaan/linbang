import request from '@/config/axios'

/** 信用分规则信息 */
export interface CreditRule {
  id: number
  ruleCode?: string
  ruleName?: string
  scoreChange?: number
  triggerType?: string
  status?: string
  createTime?: string
  updateTime?: string
}

export interface CreditRuleFormData {
  id?: number
  ruleCode?: string
  ruleName?: string
  scoreChange?: number
  triggerType?: string
  status?: string
}

export interface CreditRuleDetail extends CreditRule {
  sameTriggerRuleCount?: number
  positiveRuleCount?: number
  negativeRuleCount?: number
  positiveRule?: boolean
  relatedRules?: CreditRuleRelatedRule[]
}

export interface CreditRuleRelatedRule {
  id: number
  ruleCode?: string
  ruleName?: string
  scoreChange?: number
  triggerType?: string
  status?: string
  createTime?: string
}

// 信用分规则 API
export const CreditRuleApi = {
  // 查询信用分规则分页
  getCreditRulePage: async (params: any) => {
    return await request.get({ url: `/review/credit-rule/page`, params })
  },

  // 查询信用分规则详情
  getCreditRule: async (id: number) => {
    return await request.get<CreditRuleDetail>({ url: `/review/credit-rule/get?id=` + id })
  },

  // 新增信用分规则
  createCreditRule: async (data: CreditRuleFormData) => {
    return await request.post({ url: `/review/credit-rule/create`, data })
  },

  // 修改信用分规则
  updateCreditRule: async (data: CreditRuleFormData) => {
    return await request.put({ url: `/review/credit-rule/update`, data })
  },

  // 删除信用分规则
  deleteCreditRule: async (id: number) => {
    return await request.delete({ url: `/review/credit-rule/delete?id=` + id })
  },

  /** 批量删除信用分规则 */
  deleteCreditRuleList: async (ids: number[]) => {
    return await request.delete({ url: `/review/credit-rule/delete-list?ids=${ids.join(',')}` })
  },

  // 导出信用分规则 Excel
  exportCreditRule: async (params: any) => {
    return await request.download({ url: `/review/credit-rule/export-excel`, params })
  }
}
