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
    return await request.get<MemberUserAddress>({ url: `/linbang/member-user-address/get?id=` + id })
  },

  // 新增用户地址表
  createMemberUserAddress: async (data: MemberUserAddress) => {
    return await request.post({ url: `/linbang/member-user-address/create`, data })
  },

  // 修改用户地址表
  updateMemberUserAddress: async (data: MemberUserAddress) => {
    return await request.put({ url: `/linbang/member-user-address/update`, data })
  },

  // 删除用户地址表
  deleteMemberUserAddress: async (id: number) => {
    return await request.delete({ url: `/linbang/member-user-address/delete?id=` + id })
  },

  /** 批量删除用户地址表 */
  deleteMemberUserAddressList: async (ids: number[]) => {
    return await request.delete({ url: `/linbang/member-user-address/delete-list?ids=${ids.join(',')}` })
  },

  // 导出用户地址表 Excel
  exportMemberUserAddress: async (params) => {
    return await request.download({ url: `/linbang/member-user-address/export-excel`, params })
  }
}
