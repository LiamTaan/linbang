import * as FileApi from '@/api/infra/file'
import {
  UploadRequestOptions,
  UploadProgressEvent
} from 'element-plus/es/components/upload/src/upload'
import axios, { AxiosProgressEvent } from 'axios'

/**
 * 获得上传 URL
 */
export const getUploadUrl = (): string => {
  return import.meta.env.VITE_BASE_URL + import.meta.env.VITE_API_URL + '/infra/file/upload'
}

export const useUpload = (directory?: string) => {
  // 后端上传地址
  const uploadUrl = getUploadUrl()
  // 是否使用前端直连上传
  const isClientUpload = UPLOAD_TYPE.CLIENT === import.meta.env.VITE_UPLOAD_TYPE
  // 重写ElUpload上传方法
  const httpRequest = async (options: UploadRequestOptions) => {
    // 文件上传进度监听
    const uploadProgressHandler = (evt: AxiosProgressEvent) => {
      const upEvt: UploadProgressEvent = Object.assign(evt.event)
      upEvt.percent = evt.progress ? evt.progress * 100 : 0
      options.onProgress?.(upEvt) // 触发 el-upload 的 on-progress
    }

    // 模式一：前端上传
    if (isClientUpload) {
      // 1.1 生成文件名称
      const fileName = options.file.name || options.filename
      // 1.2 获取文件预签名地址
      const presignedInfo = await FileApi.getFilePresignedUrl(
        fileName,
        options.file.size,
        directory
      )
      // 1.3 上传文件（不能使用 ElUpload 的 ajaxUpload 方法的原因：其使用的是 FormData 上传，Minio 不支持）
      await axios.put(presignedInfo.uploadUrl, options.file, {
        headers: {
          'Content-Type': presignedInfo.uploadContentType
        },
        onUploadProgress: uploadProgressHandler
      })
      // 1.4. 等待后端完成真实大小、类型和对象元数据校验后，再通知上传成功
      await createFile(presignedInfo)
      return { data: presignedInfo.url }
    } else {
      // 模式二：后端上传
      // 重写 el-upload httpRequest 文件上传成功会走成功的钩子，失败走失败的钩子
      return new Promise((resolve, reject) => {
        FileApi.updateFile({ file: options.file, directory }, uploadProgressHandler)
          .then((res) => {
            if (res.code === 0) {
              resolve(res)
            } else {
              reject(res)
            }
          })
          .catch((res) => {
            reject(res)
          })
      })
    }
  }

  return {
    uploadUrl,
    httpRequest
  }
}

/**
 * 创建文件信息
 * @param vo 文件预签名信息
 */
function createFile(vo: FileApi.FilePresignedUrlRespVO) {
  return FileApi.createFile({
    configId: vo.configId,
    path: vo.path
  })
}

/**
 * 上传类型
 */
enum UPLOAD_TYPE {
  // 客户端直接上传（只支持S3服务）
  CLIENT = 'client',
  // 客户端发送到后端上传
  SERVER = 'server'
}
