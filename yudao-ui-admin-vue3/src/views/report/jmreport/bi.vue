<template>
  <doc-alert title="大屏设计器" url="https://doc.iocoder.cn/screen/" />

  <ContentWrap :bodyStyle="{ padding: '0px' }" class="!mb-0">
    <IFrame v-if="src" :src="src" />
  </ContentWrap>
</template>
<script lang="ts" setup>
import { createAdminSceneTicket } from '@/api/login/sceneTicket'

defineOptions({ name: 'JimuBI' })

const src = ref('')

onMounted(async () => {
  const ticket = await createAdminSceneTicket({ scene: 'GOVIEW' })
  src.value = `${import.meta.env.VITE_BASE_URL}/drag/list?token=${encodeURIComponent(ticket.token)}`
})
</script>
