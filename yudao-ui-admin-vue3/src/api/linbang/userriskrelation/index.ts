import request from '@/config/axios'

export interface UserRiskRelation {
  id: number
  userId?: number
  userNo?: string
  userNickname?: string
  userMobile?: string
  relatedUserId?: number
  relatedUserNo?: string
  relatedUserNickname?: string
  relatedUserMobile?: string
  relationType?: string
  relationValue?: string
  status?: string
  remark?: string
  createTime?: string
}

export const UserRiskRelationApi = {
  getPage: async (params: any) => {
    return await request.get({ url: '/risk/user-risk-relation/page', params })
  }
}
