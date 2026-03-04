// src/utils/dateUtil.js 日期格式化工具
/**
 * 时间戳转换为格式化日期
 * @param {Number|String} timestamp - 时间戳（毫秒）
 * @param {String} format - 格式，默认YYYY-MM-DD
 * @returns {String} 格式化后的日期
 */
export function formatDate(timestamp, format = 'YYYY-MM-DD') {
  if (!timestamp) return '-'
  const date = new Date(Number(timestamp))
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hour = String(date.getHours()).padStart(2, '0')
  const minute = String(date.getMinutes()).padStart(2, '0')
  const second = String(date.getSeconds()).padStart(2, '0')

  // 适配常见格式
  return format
    .replace('YYYY', year)
    .replace('MM', month)
    .replace('DD', day)
    .replace('HH', hour)
    .replace('mm', minute)
    .replace('ss', second)
}

/**
 * 格式化日期为时间戳
 * @param {String|Date} date - 日期字符串/Date对象
 * @returns {Number} 时间戳（毫秒）
 */
export function formatTimestamp(date) {
  if (!date) return null
  return new Date(date).getTime()
}
