import { ElMessageBox } from 'element-plus'
import { AdminDynamicKeyApi } from '@/api/linbang/security'

export const requestDynamicKeyToken = async (actionLabel: string) => {
  const { value } = await ElMessageBox.prompt(
    `请输入后台动态密钥后继续${actionLabel}`,
    '动态密钥校验',
    {
      confirmButtonText: '校验并继续',
      cancelButtonText: '取消',
      inputType: 'password',
      inputPlaceholder: '请输入后台动态密钥',
      inputValidator: (inputValue) => !!inputValue || '动态密钥不能为空',
      closeOnClickModal: false,
      closeOnPressEscape: false
    }
  )
  const resp = await AdminDynamicKeyApi.verify({ password: value })
  return resp.verifyToken
}
