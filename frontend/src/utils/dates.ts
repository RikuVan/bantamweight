export const getStorageExpiration = () => addDays(new Date(), 3)

export function addDays(date: Date, days: number) {
  var result = new Date(date)
  result.setDate(result.getDate() + days)
  return result
}
