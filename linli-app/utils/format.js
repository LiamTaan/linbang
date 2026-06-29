function pad(num) {
  return `${num}`.padStart(2, '0')
}

export function formatMoney(value, fallback = '0.00') {
  if (value === undefined || value === null || value === '') {
    return fallback
  }
  const numberValue = Number(value)
  if (Number.isNaN(numberValue)) {
    return fallback
  }
  return numberValue.toFixed(2)
}

export function formatDateTime(value, fallback = '--') {
  if (!value) {
    return fallback
  }
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) {
    return fallback
  }
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}`
}

export function formatShortDateTime(value, fallback = '--') {
  if (!value) {
    return fallback
  }
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) {
    return fallback
  }
  return `${pad(date.getMonth() + 1)}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}`
}

export function maskMobile(value) {
  if (!value || `${value}`.length < 11) {
    return value || ''
  }
  return `${value}`.replace(/^(\d{3})\d{4}(\d{4})$/, '$1****$2')
}

export function enumText(value, mapping, fallback = '--') {
  if (!value) {
    return fallback
  }
  return mapping[value] || value
}
