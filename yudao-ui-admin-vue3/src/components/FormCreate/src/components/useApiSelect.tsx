import request from '@/config/axios'
import { isEmpty } from '@/utils/is'
import { ApiSelectProps } from '@/components/FormCreate/src/type'
import { jsonParse } from '@/utils'
import { getCurrentUserId } from '@/utils/auth'

export const useApiSelect = (option: ApiSelectProps) => {
  return defineComponent({
    name: option.name,
    props: {
      // 选项标签
      labelField: {
        type: String,
        default: () => option.labelField ?? 'label'
      },
      // 选项的值
      valueField: {
        type: String,
        default: () => option.valueField ?? 'value'
      },
      // api 接口
      url: {
        type: String,
        default: () => option.url ?? ''
      },
      // 请求类型
      method: {
        type: String,
        default: 'GET'
      },
      // 选项解析函数
      parseFunc: {
        type: String,
        default: ''
      },
      // 选项数组在接口响应中的路径，例如 list、data.items
      dataPath: {
        type: String,
        default: ''
      },
      // 请求参数
      data: {
        type: String,
        default: ''
      },
      // 选择器类型，下拉框 select、多选框 checkbox、单选框 radio
      selectType: {
        type: String,
        default: 'select'
      },
      // 是否多选
      multiple: {
        type: Boolean,
        default: false
      },
      // 是否远程搜索
      remote: {
        type: Boolean,
        default: false
      },
      // 远程搜索时携带的参数
      remoteField: {
        type: String,
        default: 'label'
      },
      // 返回值类型（用于部门选择器等）：id 返回 ID，name 返回名称
      returnType: {
        type: String,
        default: 'id'
      },
      // 是否默认选中当前用户（仅 UserSelect 使用）
      defaultCurrentUser: {
        type: Boolean,
        default: false
      }
    },
    setup(props, { emit }) {
      const attrs = useAttrs()
      const options = ref<any[]>([]) // 下拉数据
      const loading = ref(false) // 是否正在从远程获取数据
      const queryParam = ref<any>() // 当前输入的值

      // 检查是否有有效的预设值
      const hasValidPresetValue = (): boolean => {
        const value = attrs.modelValue
        if (value === undefined || value === null || value === '') {
          return false
        }
        if (Array.isArray(value)) {
          return value.length > 0
        }
        return true
      }

      // 设置默认当前用户（仅当 defaultCurrentUser 为 true 且无预设值时）
      const setDefaultCurrentUser = () => {
        // 仅当组件名为 UserSelect 且 defaultCurrentUser 为 true 时处理
        if (option.name !== 'UserSelect' || !props.defaultCurrentUser) {
          return
        }
        // 检查是否已有预设值（预设值优先级高于默认当前用户）
        if (hasValidPresetValue()) {
          return
        }

        // 获取当前用户 ID
        const currentUserId = getCurrentUserId()
        if (currentUserId) {
          // 根据多选/单选模式设置默认值
          const defaultValue = props.multiple ? [currentUserId] : currentUserId
          emit('update:modelValue', defaultValue)
        }
      }

      const getOptions = async () => {
        options.value = []
        // 接口选择器
        if (isEmpty(props.url)) {
          return
        }

        const safeUrl = normalizeApiPath(props.url)
        if (!safeUrl) {
          console.warn(`接口选择器地址[${props.url}]不是合法的站内接口路径`)
          return
        }

        switch (props.method.toUpperCase()) {
          case 'GET':
            let url: string = safeUrl
            if (props.remote) {
              if (queryParam.value != undefined) {
                const field = encodeURIComponent(props.remoteField)
                const value = encodeURIComponent(String(queryParam.value))
                if (url.includes('?')) {
                  url = `${url}&${field}=${value}`
                } else {
                  url = `${url}?${field}=${value}`
                }
              }
            }
            parseOptions(await request.get({ url: url }))
            break
          case 'POST':
            const parsedData = jsonParse(props.data)
            const data: Record<string, unknown> =
              parsedData && typeof parsedData === 'object' && !Array.isArray(parsedData)
                ? parsedData
                : {}
            if (props.remote) {
              data[props.remoteField] = queryParam.value
            }
            parseOptions(await request.post({ url: safeUrl, data: data }))
            break
        }
      }

      function parseOptions(data: any) {
        // 自定义响应仅允许通过属性路径提取，禁止执行表单配置中的 JavaScript。
        const configuredPath = props.dataPath || props.parseFunc
        if (!isEmpty(configuredPath)) {
          const customOptions = getValueByPath(data, configuredPath)
          if (Array.isArray(customOptions)) {
            parseOptions0(customOptions)
            return
          }
          console.warn(`接口[${props.url}]的选项数组路径[${configuredPath}]无效`)
        }
        // 情况二：返回的直接是一个列表
        if (Array.isArray(data)) {
          parseOptions0(data)
          return
        }
        // 情况二：返回的是分页数据,尝试读取 list
        data = data.list
        if (!!data && Array.isArray(data)) {
          parseOptions0(data)
          return
        }
        // 情况三：不是 yudao-vue-pro 标准返回
        console.warn(
          `接口[${props.url}] 返回结果不是 yudao-vue-pro 标准返回建议采用自定义解析函数处理`
        )
      }

      function parseOptions0(data: any[]) {
        if (Array.isArray(data)) {
          options.value = data.map((item: any) => {
            const label = parseExpression(item, props.labelField)
            let value = parseExpression(item, props.valueField)

            // 根据 returnType 决定返回值
            // 如果设置了 returnType 为 'name'，则返回 label 作为 value
            if (props.returnType === 'name') {
              value = label
            }

            return {
              label: label,
              value: value
            }
          })
          return
        }
        console.warn(`接口[${props.url}] 返回结果不是一个数组`)
      }

      function parseExpression(data: any, template: string) {
        // 检测是否使用了表达式
        if (template.indexOf('${') === -1) {
          return getValueByPath(data, template)
        }
        // 正则表达式匹配模板字符串中的 ${...}
        const pattern = /\$\{([^}]*)}/g
        // 使用replace函数配合正则表达式和回调函数来进行替换
        return template.replace(pattern, (_, expr) => {
          // expr 是匹配到的 ${} 内的表达式（这里是属性名），从 data 中获取对应的值
          const result = getValueByPath(data, expr.trim())
          if (!result) {
            console.warn(
              `接口选择器选项模版[${template}][${expr.trim()}] 解析值失败结果为[${result}], 请检查属性名称是否存在于接口返回值中,存在则忽略此条！！！`
            )
          }
          return result
        })
      }

      function getValueByPath(data: any, rawPath: string): any {
        let path = rawPath.trim()
        if (path === 'data') {
          return data
        }
        if (path.startsWith('data.')) {
          path = path.slice(5)
        }
        const segments = path.split('.')
        if (
          !segments.length ||
          segments.some(
            (segment) =>
              !/^[A-Za-z_$][\w$]*$/.test(segment) ||
              ['__proto__', 'prototype', 'constructor'].includes(segment)
          )
        ) {
          return undefined
        }
        return segments.reduce((value, segment) => value?.[segment], data)
      }

      function normalizeApiPath(rawUrl: string): string | null {
        const url = rawUrl.trim()
        if (
          !url.startsWith('/') ||
          url.startsWith('//') ||
          url.includes('\\') ||
          /[\u0000-\u001f]/.test(url)
        ) {
          return null
        }
        try {
          const parsed = new URL(url, window.location.origin)
          if (parsed.origin !== window.location.origin || parsed.hash) {
            return null
          }
          return `${parsed.pathname}${parsed.search}`
        } catch {
          return null
        }
      }

      const remoteMethod = async (query: any) => {
        if (!query) {
          return
        }
        loading.value = true
        try {
          queryParam.value = query
          await getOptions()
        } finally {
          loading.value = false
        }
      }

      onMounted(async () => {
        await getOptions()
        // 设置默认当前用户（在数据加载完成后）
        setDefaultCurrentUser()
      })

      const buildSelect = () => {
        if (props.multiple) {
          // fix：多写此步是为了解决 multiple 属性问题
          return (
            <el-select
              class="w-1/1"
              multiple
              loading={loading.value}
              {...attrs}
              remote={props.remote}
              {...(props.remote && { remoteMethod: remoteMethod })}
            >
              {options.value.map((item, index) => (
                <el-option key={index} label={item.label} value={item.value} />
              ))}
            </el-select>
          )
        }
        return (
          <el-select
            class="w-1/1"
            loading={loading.value}
            {...attrs}
            remote={props.remote}
            {...(props.remote && { remoteMethod: remoteMethod })}
          >
            {options.value.map((item, index) => (
              <el-option key={index} label={item.label} value={item.value} />
            ))}
          </el-select>
        )
      }
      const buildCheckbox = () => {
        if (isEmpty(options.value)) {
          options.value = [
            { label: '选项1', value: '选项1' },
            { label: '选项2', value: '选项2' }
          ]
        }
        return (
          <el-checkbox-group class="w-1/1" {...attrs}>
            {options.value.map((item, index) => (
              <el-checkbox key={index} label={item.label} value={item.value} />
            ))}
          </el-checkbox-group>
        )
      }
      const buildRadio = () => {
        if (isEmpty(options.value)) {
          options.value = [
            { label: '选项1', value: '选项1' },
            { label: '选项2', value: '选项2' }
          ]
        }
        return (
          <el-radio-group class="w-1/1" {...attrs}>
            {options.value.map((item, index) => (
              <el-radio key={index} value={item.value}>
                {item.label}
              </el-radio>
            ))}
          </el-radio-group>
        )
      }
      return () => (
        <>
          {props.selectType === 'select'
            ? buildSelect()
            : props.selectType === 'radio'
              ? buildRadio()
              : props.selectType === 'checkbox'
                ? buildCheckbox()
                : buildSelect()}
        </>
      )
    }
  })
}
