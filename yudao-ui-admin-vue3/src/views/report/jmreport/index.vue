<template>
  <doc-alert title="报表设计器" url="https://doc.iocoder.cn/report/" />

  <ContentWrap :bodyStyle="{ padding: '0px' }" class="!mb-0">
    <IFrame v-if="src" :src="src" />
  </ContentWrap>
</template>
<script lang="ts" setup>
import { createAdminSceneTicket } from '@/api/login/sceneTicket'

defineOptions({ name: 'JimuReport' })

const src = ref('')

onMounted(async () => {
  const ticket = await createAdminSceneTicket({ scene: 'JMREPORT' })
  src.value = `${import.meta.env.VITE_BASE_URL}/jmreport/list?token=${encodeURIComponent(ticket.token)}`
})
</script>
