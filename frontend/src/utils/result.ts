import { UnionOf, ofType, unionize } from 'unionize'

type Error = {
  status?: number
  message?: string
}

export function toApiResult(status: number, data: Record<string, any> = { message: '' }) {
  if (status < 300) {
    return ApiResult.Ok({ data })
  }
  if (status === 401) {
    return ApiResult.Unauthorized({
      status,
      message: data?.message,
    })
  }
  if (status === 404) {
    return ApiResult.NotFound({
      status,
      message: data?.message,
    })
  }
  if (status === 400) {
    return ApiResult.BadRequest({
      status,
      message: data?.message,
    })
  }
  if (status === 500) {
    return ApiResult.InternalServerError({
      status,
      message: data?.message,
    })
  }
  return ApiResult.Unknown({
    status,
    message: data?.message,
  })
}

export const ApiResult = unionize({
  Ok: ofType<any>(),
  NotFound: ofType<Error>(),
  BadRequest: ofType<Error>(),
  InternalServerError: ofType<Error>(),
  Unauthorized: ofType<Error>(),
  Unknown: ofType<Error>(),
})

export type ApiResult = UnionOf<typeof ApiResult>
