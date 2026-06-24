import request from '@/config/axios'
import { buildDynamicKeyHeaders } from '@/api/linbang/security'

/** 风控规则表信息 */
export interface RiskRule {
  id: number
  ruleCode?: string
  ruleName?: string
  ruleGroup?: string
  ruleValue?: string
  valueType?: string
  status?: string
  remark?: string
  createTime?: string
  updateTime?: string
}

export interface RiskRuleFormData {
  id?: number
  ruleCode?: string
  ruleName?: string
  ruleGroup?: string
  ruleValue?: string
  valueType?: string
  status?: string
  remark?: string
}

export interface RiskRuleDetail extends RiskRule {
  hitCount?: number
  pendingEventCount?: number
  userBlacklistCount?: number
  recentEvents?: RiskRuleEvent[]
  relatedBlacklists?: RiskRuleBlacklist[]
}

export interface RiskRuleEvent {
  id: number
  bizType?: string
  bizId?: number
  user?: {
    id?: number
    userNo?: string
    mobile?: string
    nickname?: string
    currentRoleCode?: string
    status?: string
  }
  riskType?: string
  riskLevel?: string
  hitRuleCode?: string
  status?: string
  handleBy?: number
  handleTime?: string
  remark?: string
  createTime?: string
}

export interface RiskRuleBlacklist {
  id: number
  userId?: number
  blackType?: string
  reason?: string
  status?: string
  startTime?: string
  endTime?: string
  createTime?: string
  user?: {
    id?: number
    userNo?: string
    mobile?: string
    nickname?: string
    currentRoleCode?: string
    status?: string
  }
}

// 风控规则表 API
export const RiskRuleApi = {
  // 查询风控规则表分页
  getRiskRulePage: async (params: any) => {
    return await request.get({ url: `/risk/rule/page`, params })
  },

  // 查询风控规则表详情
  getRiskRule: async (id: number) => {
    return await request.get<RiskRuleDetail>({ url: `/risk/rule/get?id=` + id })
  },

  // 新增风控规则表
  createRiskRule: async (data: RiskRuleFormData, verifyToken?: string) => {
    return await request.post({
      url: `/risk/rule/create`,
      data,
      headers: buildDynamicKeyHeaders(verifyToken)
    })
  },

  // 修改风控规则表
  updateRiskRule: async (data: RiskRuleFormData, verifyToken?: string) => {
    return await request.put({
      url: `/risk/rule/update`,
      data,
      headers: buildDynamicKeyHeaders(verifyToken)
    })
  },

  // 删除风控规则表
  deleteRiskRule: async (id: number, verifyToken?: string) => {
    return await request.delete({
      url: `/risk/rule/delete?id=` + id,
      headers: buildDynamicKeyHeaders(verifyToken)
    })
  },

  /** 批量删除风控规则表 */
  deleteRiskRuleList: async (ids: number[], verifyToken?: string) => {
    return await request.delete({
      url: `/risk/rule/delete-list?ids=${ids.join(',')}`,
      headers: buildDynamicKeyHeaders(verifyToken)
    })
  },

  // 导出风控规则表 Excel
  exportRiskRule: async (params: any) => {
    return await request.download({ url: `/risk/rule/export-excel`, params })
  }
}
