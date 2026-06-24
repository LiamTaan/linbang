import request from '@/config/axios'

export interface MessageRecord {
  id: number
  templateId?: number
  pushTaskId?: number
  receiverUserId?: number
  receiverUserNo?: string
  receiverUserNickname?: string
  receiverUserMobile?: string
  channelType?: string
  bizType?: string
  bizId?: number
  sendStatus?: string
  sendTime?: string
  failReason?: string
  createTime?: string
}

export interface MessageRecordDetail extends MessageRecord {
  updateTime?: string
  template?: MessageRecordTemplateSummary
}

export interface MessageRecordTemplateSummary {
  id?: number
  templateCode?: string
  templateName?: string
  templateType?: string
  channelType?: string
  status?: string
}

export const MessageRecordApi = {
  getMessageRecordPage: async (params: any) => {
    return await request.get({ url: '/message/record/page', params })
  },
  getMessageRecord: async (id: number) => {
    return await request.get<MessageRecordDetail>({ url: `/message/record/get?id=${id}` })
  }
}
