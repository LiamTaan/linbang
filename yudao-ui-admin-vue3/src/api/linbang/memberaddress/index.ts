import request from '@/config/axios'

/** 用户地址表信息 */
export interface MemberUserAddress {
  id: number
  userId?: number
  userNo?: string
  userNickname?: string
  userMobile?: string
  receiverName?: string
  receiverMobile?: string
  province?: string
  city?: string
  district?: string
  street?: string
  detailAddress?: string
  longitude?: number
  latitude?: number
  adcode?: string
  isDefault?: boolean
  createTime?: string
}

// 用户地址表 API
export const MemberUserAddressApi = {
  // 查询用户地址表分页
  getMemberUserAddressPage: async (params: any) => {
    return await request.get({ url: `/linbang/member-user-address/page`, params })
  },

  // 查询用户地址表详情
  getMemberUserAddress: async (id: number) => {
    return await request.get<MemberUserAddress>({
      url: `/linbang/member-user-address/get?id=` + id
    })
  },

  // 导出用户地址表 Excel
  exportMemberUserAddress: async (params) => {
    return await request.download({ url: `/linbang/member-user-address/export-excel`, params })
  }
}
