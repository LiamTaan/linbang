<template>
  <div>
    <Dialog v-model="dialogVisible" :title="dialogTitle" width="860px">
      <el-form
        ref="formRef"
        v-loading="formLoading"
        :model="formData"
        :rules="formRules"
        label-width="180px"
      >
        <el-form-item label="渠道状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio
              v-for="dict in getDictOptions(DICT_TYPE.COMMON_STATUS)"
              :key="parseInt(dict.value)"
              :value="parseInt(dict.value)"
            >
              {{ dict.label }}
            </el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="聚合下单基础地址" prop="config.baseUrl">
          <el-input
            v-model="formData.config.baseUrl"
            placeholder="请输入 /manage/pay/* 所属聚合中间层地址"
          />
        </el-form-item>
        <el-form-item label="收款商户号" prop="config.merchantNo">
          <el-input v-model="formData.config.merchantNo" placeholder="请输入银盛收款商户号" />
        </el-form-item>
        <el-form-item label="商户名称" prop="config.merchantName">
          <el-input v-model="formData.config.merchantName" placeholder="请输入商户名称" />
        </el-form-item>
        <el-form-item label="开放平台 partnerId" prop="config.partnerId">
          <el-input v-model="formData.config.partnerId" placeholder="为空时默认复用收款商户号" />
        </el-form-item>
        <el-form-item label="退款网关地址" prop="config.openApiGatewayUrl">
          <el-input
            v-model="formData.config.openApiGatewayUrl"
            placeholder="请输入银盛线上交易网关地址"
          />
        </el-form-item>
        <el-form-item label="提现网关地址" prop="config.transferGatewayUrl">
          <el-input
            v-model="formData.config.transferGatewayUrl"
            placeholder="请输入银盛代付网关地址"
          />
        </el-form-item>
        <el-form-item label="商户私钥证书路径" prop="config.privateKeyFilePath">
          <el-input
            v-model="formData.config.privateKeyFilePath"
            placeholder="请输入商户 pfx 证书绝对路径"
          />
        </el-form-item>
        <el-form-item label="私钥证书密码" prop="config.privateKeyPassword">
          <el-input
            v-model="formData.config.privateKeyPassword"
            show-password
            placeholder="请输入商户私钥证书密码"
          />
        </el-form-item>
        <el-form-item label="银盛公钥证书路径" prop="config.ysepayPublicKeyFilePath">
          <el-input
            v-model="formData.config.ysepayPublicKeyFilePath"
            placeholder="请输入银盛 cer 证书绝对路径"
          />
        </el-form-item>
        <el-form-item label="签名算法" prop="config.signType">
          <el-select v-model="formData.config.signType" class="!w-240px">
            <el-option label="RSA" value="RSA" />
            <el-option label="SM" value="SM" />
          </el-select>
        </el-form-item>
        <el-form-item label="字符集" prop="config.charset">
          <el-input v-model="formData.config.charset" placeholder="例如 utf-8" class="!w-240px" />
        </el-form-item>
        <el-form-item label="接口版本" prop="config.version">
          <el-input v-model="formData.config.version" placeholder="例如 3.0" class="!w-240px" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="formData.remark" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button :disabled="formLoading" type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="dialogVisible = false">取 消</el-button>
      </template>
    </Dialog>
  </div>
</template>

<script lang="ts" setup>
import { CommonStatusEnum } from '@/utils/constants'
import { DICT_TYPE, getDictOptions } from '@/utils/dict'
import * as ChannelApi from '@/api/pay/channel'

defineOptions({ name: 'AggregateChannelForm' })

const { t } = useI18n()
const message = useMessage()

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formLoading = ref(false)
const formRef = ref()
const formData = ref<any>({})
const formRules = {
  status: [{ required: true, message: '渠道状态不能为空', trigger: 'blur' }],
  'config.baseUrl': [{ required: true, message: '聚合下单基础地址不能为空', trigger: 'blur' }],
  'config.merchantNo': [{ required: true, message: '收款商户号不能为空', trigger: 'blur' }],
  'config.merchantName': [{ required: true, message: '商户名称不能为空', trigger: 'blur' }],
  'config.openApiGatewayUrl': [{ required: true, message: '退款网关地址不能为空', trigger: 'blur' }],
  'config.transferGatewayUrl': [{ required: true, message: '提现网关地址不能为空', trigger: 'blur' }],
  'config.privateKeyFilePath': [{ required: true, message: '商户私钥证书路径不能为空', trigger: 'blur' }],
  'config.privateKeyPassword': [{ required: true, message: '私钥证书密码不能为空', trigger: 'blur' }],
  'config.ysepayPublicKeyFilePath': [{ required: true, message: '银盛公钥证书路径不能为空', trigger: 'blur' }],
  'config.signType': [{ required: true, message: '签名算法不能为空', trigger: 'change' }],
  'config.charset': [{ required: true, message: '字符集不能为空', trigger: 'blur' }],
  'config.version': [{ required: true, message: '接口版本不能为空', trigger: 'blur' }]
}

const createDefaultConfig = () => ({
  baseUrl: '',
  merchantNo: '826584873720104',
  merchantName: '深圳市旺佳盈科技有限公司',
  partnerId: '826584873720104',
  openApiGatewayUrl: 'https://openapi.ysepay.com/gateway.do',
  transferGatewayUrl: 'https://df.ysepay.com/gateway.do',
  privateKeyFilePath: '',
  privateKeyPassword: '',
  ysepayPublicKeyFilePath: '',
  signType: 'RSA',
  charset: 'utf-8',
  version: '3.0'
})

const resetForm = (appId, code) => {
  formData.value = {
    appId,
    code,
    status: CommonStatusEnum.ENABLE,
    remark: '',
    feeRate: 0,
    config: createDefaultConfig()
  }
  formRef.value?.resetFields()
}

const open = async (appId, code) => {
  dialogVisible.value = true
  formLoading.value = true
  resetForm(appId, code)
  try {
    const data = await ChannelApi.getChannel(appId, code)
    if (data && data.id) {
      formData.value = data
      formData.value.config = {
        ...createDefaultConfig(),
        ...JSON.parse(data.config)
      }
    }
    dialogTitle.value = !formData.value.id ? '创建支付渠道' : '编辑支付渠道'
  } finally {
    formLoading.value = false
  }
}

defineExpose({ open })

const emit = defineEmits(['success'])

const submitForm = async () => {
  if (!formRef.value) return
  const valid = await formRef.value.validate()
  if (!valid) return
  formLoading.value = true
  try {
    const data = { ...formData.value } as unknown as ChannelApi.ChannelVO
    data.config = JSON.stringify(formData.value.config)
    if (!data.id) {
      await ChannelApi.createChannel(data)
      message.success(t('common.createSuccess'))
    } else {
      await ChannelApi.updateChannel(data)
      message.success(t('common.updateSuccess'))
    }
    dialogVisible.value = false
    emit('success')
  } finally {
    formLoading.value = false
  }
}
</script>
