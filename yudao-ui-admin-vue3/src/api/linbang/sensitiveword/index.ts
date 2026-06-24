import request from '@/config/axios'

/** 敏感词表信息 */
export interface SensitiveWord {
  id: number
  word?: string
  wordType?: string
  matchType?: string
  blockLevel?: string
  status?: string
  createTime?: string
  updateTime?: string
}

export interface SensitiveWordFormData {
  id?: number
  word?: string
  wordType?: string
  matchType?: string
  blockLevel?: string
  status?: string
}

export interface SensitiveWordDetail extends SensitiveWord {
  sameWordTypeCount?: number
  sameMatchTypeCount?: number
  sameBlockLevelCount?: number
  relatedWords?: SensitiveWordRelatedWord[]
}

export interface SensitiveWordRelatedWord {
  id: number
  word?: string
  wordType?: string
  matchType?: string
  blockLevel?: string
  status?: string
  createTime?: string
}

// 敏感词表 API
export const SensitiveWordApi = {
  // 查询敏感词表分页
  getSensitiveWordPage: async (params: any) => {
    return await request.get({ url: `/risk/sensitive-word/page`, params })
  },

  // 查询敏感词表详情
  getSensitiveWord: async (id: number) => {
    return await request.get<SensitiveWordDetail>({ url: `/risk/sensitive-word/get?id=` + id })
  },

  // 新增敏感词表
  createSensitiveWord: async (data: SensitiveWordFormData) => {
    return await request.post({ url: `/risk/sensitive-word/create`, data })
  },

  // 修改敏感词表
  updateSensitiveWord: async (data: SensitiveWordFormData) => {
    return await request.put({ url: `/risk/sensitive-word/update`, data })
  },

  // 删除敏感词表
  deleteSensitiveWord: async (id: number) => {
    return await request.delete({ url: `/risk/sensitive-word/delete?id=` + id })
  },

  /** 批量删除敏感词表 */
  deleteSensitiveWordList: async (ids: number[]) => {
    return await request.delete({ url: `/risk/sensitive-word/delete-list?ids=${ids.join(',')}` })
  },

  // 导出敏感词表 Excel
  exportSensitiveWord: async (params: any) => {
    return await request.download({ url: `/risk/sensitive-word/export-excel`, params })
  }
}
