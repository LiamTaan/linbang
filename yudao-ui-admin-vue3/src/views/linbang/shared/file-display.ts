import { getFile, type FileVO } from '@/api/infra/file'

export type FileLookupMap = Record<number, FileVO>

export const loadFilesByIds = async (ids: Array<number | undefined | null>) => {
  const uniqueIds = Array.from(new Set(ids.filter((id): id is number => Boolean(id))))
  const entries = await Promise.all(
    uniqueIds.map(async (id) => {
      try {
        const file = await getFile(id)
        return [id, file] as const
      } catch {
        return undefined
      }
    })
  )
  return entries.reduce<FileLookupMap>((acc, item) => {
    if (item) {
      acc[item[0]] = item[1]
    }
    return acc
  }, {})
}

export const formatFileBrief = (file?: FileVO, missingText = '附件信息缺失') => {
  if (!file) {
    return missingText
  }
  return [file.name, file.type].filter(Boolean).join(' / ')
}
