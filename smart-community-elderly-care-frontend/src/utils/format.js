import dayjs from 'dayjs'

export function formatDate(date, format = 'YYYY-MM-DD HH:mm:ss') {
  return date ? dayjs(date).format(format) : '-'
}

export function formatPrice(price) {
  return price ? `¥${parseFloat(price).toFixed(2)}` : '¥0.00'
}

export function formatPhone(phone) {
  if (!phone || phone.length !== 11) return phone
  return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2')
}
