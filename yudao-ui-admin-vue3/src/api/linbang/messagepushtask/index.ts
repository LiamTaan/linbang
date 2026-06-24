import request from '@/config/axios'
import type { Dayjs } from 'dayjs'

export interface MessagePushTask {
  id: number
  taskName?: string
  targetScope?: string
  channelType?: string
  templateId?: number
  templateName?: string
  bizType?: string
  status?: string
  plannedSendTime?: string | Dayjs
  executeTime?: string | Dayjs
  successCount?: number
  failCount?: number
  createTime?: string | Dayjs
}

export interface MessagePushTaskDetail extends MessagePushTask {
  bizId?: number
  creatorRemark?: string
  updateTime?: string | Dayjs
  recentRecords?: Array<{
    id?: number
    receiverUserId?: number
    receiverUserNo?: string
    receiverUserNickname?: string
    receiverUserMobile?: string
    sendStatus?: string
    failReason?: string
    sendTime?: string | Dayjs
  }>
}

export const MessagePushTaskApi = {
  getMessagePushTaskPage: async (params: any) => {
    return await request.get({ url: '/message/push-task/page', params })
  },

  getMessagePushTask: async (id: number) => {
    return await request.get<MessagePushTaskDetail>({ url: `/message/push-task/get?id=${id}` })
  }
}
