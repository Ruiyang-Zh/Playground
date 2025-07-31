export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
  timestamp: string
}

export interface PageResponse<T> {
  content: T[]
  pageable: {
    paged: boolean
    unpaged: boolean
    pageNumber: number
    pageSize: number
    offset: number
    sort: Array<{
      direction: string
      property: string
      ignoreCase: boolean
      nullHandling: string
      ascending: boolean
      descending: boolean
    }>
  }
  total: number
  empty: boolean
  number: number
  size: number
  numberOfElements: number
  sort: Array<{
    direction: string
    property: string
    ignoreCase: boolean
    nullHandling: string
    ascending: boolean
    descending: boolean
  }>
  first: boolean
  last: boolean
  totalPages: number
  totalElements: number
}
