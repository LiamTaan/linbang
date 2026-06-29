type QueryPrimitive = string | number | boolean | null | undefined

type SerializeQueryOptions = {
  allowDots?: boolean
  indices?: boolean
}

const isPlainObject = (value: unknown): value is Record<string, unknown> => {
  return Object.prototype.toString.call(value) === '[object Object]'
}

const appendQueryValue = (
  searchParams: URLSearchParams,
  key: string,
  value: unknown,
  options: SerializeQueryOptions
) => {
  if (value === undefined || value === null) {
    return
  }

  if (Array.isArray(value)) {
    value.forEach((item, index) => {
      const nextKey = options.indices === false ? key : `${key}[${index}]`
      appendQueryValue(searchParams, nextKey, item, options)
    })
    return
  }

  if (value instanceof Date) {
    searchParams.append(key, value.toISOString())
    return
  }

  if (isPlainObject(value)) {
    Object.entries(value).forEach(([childKey, childValue]) => {
      const nextKey = options.allowDots ? `${key}.${childKey}` : `${key}[${childKey}]`
      appendQueryValue(searchParams, nextKey, childValue, options)
    })
    return
  }

  searchParams.append(key, String(value as QueryPrimitive))
}

export const stringifyQuery = (
  query: Record<string, unknown> | undefined,
  options: SerializeQueryOptions = {}
): string => {
  if (!query) {
    return ''
  }
  const searchParams = new URLSearchParams()
  Object.entries(query).forEach(([key, value]) => {
    appendQueryValue(searchParams, key, value, options)
  })
  return searchParams.toString()
}

export const parseQuery = (queryString = ''): Record<string, string | string[]> => {
  const normalized = queryString.startsWith('?') ? queryString.slice(1) : queryString
  const searchParams = new URLSearchParams(normalized)
  const result: Record<string, string | string[]> = {}

  searchParams.forEach((value, key) => {
    const currentValue = result[key]
    if (currentValue === undefined) {
      result[key] = value
      return
    }
    result[key] = Array.isArray(currentValue) ? [...currentValue, value] : [currentValue, value]
  })

  return result
}
