import { api } from './client'

export interface LoginRequest {
  email: string
  password: string
}

export function login(data: LoginRequest) {
  return api.post<void>('/api/v1/login', data)
}