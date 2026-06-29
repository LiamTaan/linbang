import { uploadFile } from '@/utils/request'

export function uploadAppFile(filePath, directory = 'linbang/app') {
  return uploadFile(filePath, {
    formData: {
      directory
    }
  })
}
