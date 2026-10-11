import axios from 'axios'

import ApiError from './ApiError'

const REQUEST_TIMEOUT_MS = 10000
const TIMEOUT_ERROR_CODES = ['ECONNABORTED', 'ETIMEDOUT']

const DEFAULT_ERROR_MESSAGE = 'Não foi possível completar a requisição. Tente novamente.'
const TIMEOUT_ERROR_MESSAGE = 'O servidor demorou para responder. Tente novamente.'
const NETWORK_ERROR_MESSAGE = 'Não foi possível conectar ao servidor.'

const httpClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: REQUEST_TIMEOUT_MS,
})

export function toApiError(error) {
  if (error.response) {
    const { status, data } = error.response
    return new ApiError(data?.message ?? DEFAULT_ERROR_MESSAGE, status, data?.fieldErrors ?? {})
  }

  if (TIMEOUT_ERROR_CODES.includes(error.code)) {
    return new ApiError(TIMEOUT_ERROR_MESSAGE)
  }

  return new ApiError(NETWORK_ERROR_MESSAGE)
}

httpClient.interceptors.response.use(
  (response) => response,
  (error) => Promise.reject(toApiError(error)),
)

export default httpClient
