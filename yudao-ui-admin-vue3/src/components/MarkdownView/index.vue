<template>
  <div ref="contentRef" class="markdown-view" v-dompurify-html="renderedMarkdown"></div>
</template>

<script setup lang="ts">
import { useClipboard } from '@vueuse/core'
import MarkdownIt from 'markdown-it'
import 'highlight.js/styles/vs2015.min.css'
import hljs from 'highlight.js'

// 定义组件属性
const props = defineProps({
  content: {
    type: String,
    required: true
  }
})

const message = useMessage() // 消息弹窗
const { copy } = useClipboard({ legacy: true }) // 初始化 copy 到粘贴板
const contentRef = ref()
let clickHandler: ((event: Event) => void) | null = null

const md = new MarkdownIt({
  html: false,
  highlight: function (str, lang) {
    const codeHtml =
      lang && hljs.getLanguage(lang)
        ? hljs.highlight(str, { language: lang, ignoreIllegals: true }).value
        : md.utils.escapeHtml(str)
    const copyHtml =
      '<button type="button" class="copy-btn" data-copy-code="1">复制</button>'
    return `<pre class="code-block">${copyHtml}<code class="hljs">${codeHtml}</code></pre>`
  }
})

/** 渲染 markdown */
const renderedMarkdown = computed(() => md.render(props.content))

/** 初始化 **/
onMounted(() => {
  clickHandler = async (event: Event) => {
    const target = event.target as HTMLElement | null
    if (!target?.matches('[data-copy-code="1"]')) {
      return
    }
    const code = target.closest('pre')?.querySelector('code')?.textContent ?? ''
    if (!code) {
      return
    }
    await copy(code)
    message.success('复制成功!')
  }
  contentRef.value?.addEventListener('click', clickHandler)
})

onBeforeUnmount(() => {
  if (clickHandler) {
    contentRef.value?.removeEventListener('click', clickHandler)
  }
})
</script>

<style lang="scss">
.markdown-view {
  max-width: 100%;
  font-family: 'PingFang SC';
  font-size: 0.95rem;
  font-weight: 400;
  line-height: 1.6rem;
  letter-spacing: 0;
  color: #3b3e55;
  text-align: left;

  pre {
    position: relative;
  }

  .copy-btn {
    position: absolute;
    top: 5px;
    right: 10px;
    color: #fff;
    cursor: pointer;
    background: transparent;
    border: 0;
  }

  pre code.hljs {
    width: auto;
  }

  code.hljs {
    width: auto;
    padding-top: 20px;
    border-radius: 6px;

    @media screen and (width >= 1536px) {
      width: 960px;
    }

    @media screen and (width <= 1536px) and (width >= 1024px) {
      width: calc(100vw - 400px - 64px - 32px * 2);
    }

    @media screen and (width <= 1024px) and (width >= 768px) {
      width: calc(100vw - 32px * 2);
    }

    @media screen and (width <= 768px) {
      width: calc(100vw - 16px * 2);
    }
  }

  p,
  code.hljs {
    margin-bottom: 16px;
  }

  p {
    //margin-bottom: 1rem !important;
    margin: 0;
    margin-bottom: 3px;
  }

  /* 标题通用格式 */
  h1,
  h2,
  h3,
  h4,
  h5,
  h6 {
    margin: 24px 0 8px;
    font-weight: 600;
    color: #3b3e55;
  }

  h1 {
    font-size: 22px;
    line-height: 32px;
  }

  h2 {
    font-size: 20px;
    line-height: 30px;
  }

  h3 {
    font-size: 18px;
    line-height: 28px;
  }

  h4 {
    font-size: 16px;
    line-height: 26px;
  }

  h5 {
    font-size: 16px;
    line-height: 24px;
  }

  h6 {
    font-size: 16px;
    line-height: 24px;
  }

  /* 列表（有序，无序） */
  ul,
  ol {
    padding: 0;
    margin: 0 0 8px;
    font-size: 16px;
    line-height: 24px;
    color: #3b3e55; // var(--color-CG600);
  }

  li {
    margin: 4px 0 0 20px;
    margin-bottom: 1rem;
  }

  ol > li {
    margin-bottom: 1rem;
    list-style-type: decimal;
    // 表达式,修复有序列表序号展示不全的问题
    // &:nth-child(n + 10) {
    //     margin-left: 30px;
    // }

    // &:nth-child(n + 100) {
    //     margin-left: 30px;
    // }
  }

  ul > li {
    margin-right: 11px;
    margin-bottom: 1rem;
    font-size: 16px;
    line-height: 24px;
    color: #3b3e55; // var(--color-G900);
    list-style-type: disc;
  }

  ol ul,
  ol ul > li,
  ul ul,
  ul ul li {
    margin-bottom: 1rem;
    margin-left: 6px;
    // list-style: circle;
    font-size: 16px;
    list-style: none;
  }

  ul ul ul,
  ul ul ul li,
  ol ol,
  ol ol > li,
  ol ul ul,
  ol ul ul > li,
  ul ol,
  ul ol > li {
    list-style: square;
  }
}
</style>
