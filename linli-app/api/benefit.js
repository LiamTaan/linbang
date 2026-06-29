import { get } from '@/utils/request'

export function getBenefitOverview() {
  return get('/benefit/overview/get')
}
