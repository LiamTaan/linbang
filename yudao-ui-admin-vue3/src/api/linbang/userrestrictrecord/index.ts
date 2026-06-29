import request from '@/config/axios'

export interface UserRestrictRecord {
  id: number
  userId?: number
  userNo?: string
  userNickname?: string
  userMobile?: string
  restrictType?: string
  status?: string
  startTime?: string
  endTime?: string
  sourceRuleCode?: string
  sourceBizType?: string
  sourceBizId?: number
  reason?: string
  releasedBy?: number
  releasedTime?: string
  releaseRemark?: string
  createTime?: string
}

export const UserRestrictRecordApi = {
  getPage: async (params: any) => {
    return await request.get({ url: '/risk/user-restrict-record/page', params })
  },
  get: async (id: number) => {
    return await request.get<UserRestrictRecord>({ url: `/risk/user-restrict-record/get?id=${id}` })
  }
}
