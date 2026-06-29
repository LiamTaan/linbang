import request from '@/config/axios'

export interface EscrowProof {
  id: number
  proofNo?: string
  orderId?: number
  unitId?: number
  userId?: number
  merchantId?: number
  escrowAmount?: number
  proofStatus?: string
  lockReason?: string
  unlockReason?: string
  createTime?: string
}

export const EscrowProofApi = {
  getEscrowProofPage: async (params: any) => {
    return await request.get({ url: `/wallet/escrow-proof/page`, params })
  },

  getEscrowProof: async (id: number) => {
    return await request.get<EscrowProof>({ url: `/wallet/escrow-proof/get?id=${id}` })
  }
}
