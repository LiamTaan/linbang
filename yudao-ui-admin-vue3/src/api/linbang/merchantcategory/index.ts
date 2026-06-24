import request from '@/config/axios'

const MERCHANT_SERVICE_CATEGORY_MAX_PAGE_SIZE = 200

/** 服务类目表信息 */
export interface MerchantServiceCategory {
          id: number; // 主键
          parentId?: number; // 父级ID
          categoryName?: string; // 类目名称
          categoryLevel?: number; // 层级
          sortNo?: number; // 排序
          icon: string; // 图标
          defaultPricingMode: string; // 默认计价方式
          supportSplit?: boolean; // 是否支持拆单
          supportInvoice?: boolean; // 是否支持开票
          riskLevel: string; // 风险等级
          status?: string; // 状态
  }

// 服务类目表 API
export const MerchantServiceCategoryApi = {
  // 查询服务类目表分页
  getMerchantServiceCategoryPage: async (params: any) => {
    return await request.get({ url: `/linbang/merchant-service-category/page`, params })
  },

  // 查询服务类目表全量列表（按后端分页上限自动翻页）
  getMerchantServiceCategoryAllList: async (params: any = {}) => {
    const list: MerchantServiceCategory[] = []
    let pageNo = 1

    while (true) {
      const pageResult = await request.get({
        url: `/linbang/merchant-service-category/page`,
        params: {
          ...params,
          pageNo,
          pageSize: MERCHANT_SERVICE_CATEGORY_MAX_PAGE_SIZE
        }
      })
      const records = pageResult.list ?? []
      list.push(...records)

      if (
        records.length < MERCHANT_SERVICE_CATEGORY_MAX_PAGE_SIZE ||
        list.length >= Number(pageResult.total ?? 0)
      ) {
        break
      }
      pageNo += 1
    }

    return list
  },

  // 查询服务类目表详情
  getMerchantServiceCategory: async (id: number) => {
    return await request.get<MerchantServiceCategory>({ url: `/linbang/merchant-service-category/get?id=` + id })
  },

  // 新增服务类目表
  createMerchantServiceCategory: async (data: MerchantServiceCategory) => {
    return await request.post({ url: `/linbang/merchant-service-category/create`, data })
  },

  // 修改服务类目表
  updateMerchantServiceCategory: async (data: MerchantServiceCategory) => {
    return await request.put({ url: `/linbang/merchant-service-category/update`, data })
  },

  // 删除服务类目表
  deleteMerchantServiceCategory: async (id: number) => {
    return await request.delete({ url: `/linbang/merchant-service-category/delete?id=` + id })
  },

  /** 批量删除服务类目表 */
  deleteMerchantServiceCategoryList: async (ids: number[]) => {
    return await request.delete({ url: `/linbang/merchant-service-category/delete-list?ids=${ids.join(',')}` })
  },

  // 导出服务类目表 Excel
  exportMerchantServiceCategory: async (params) => {
    return await request.download({ url: `/linbang/merchant-service-category/export-excel`, params })
  }
}
