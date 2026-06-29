import request from '@/config/axios'

export interface SceneTicketReqVO {
  scene: 'JMREPORT' | 'GOVIEW' | 'WEBSOCKET'
}

export interface SceneTicketRespVO {
  token: string
  expiresInSeconds: number
}

export const createAdminSceneTicket = (data: SceneTicketReqVO) => {
  return request.post<SceneTicketRespVO>({ url: '/system/auth/scene-ticket', data })
}

export const createAppSceneTicket = (data: Pick<SceneTicketReqVO, 'scene'>) => {
  return request.post<SceneTicketRespVO>({ url: '/member/auth/scene-ticket', data })
}
