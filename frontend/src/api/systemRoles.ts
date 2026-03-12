import { api } from './client'

export interface SystemRoleResponse {
  id: string
  email: string
  username: string
  role: string
  createdAt: string
}

export interface CreateSystemRoleRequest {
  email: string
  username: string
  password: string
}

export function listSystemRoles() {
  return api.get<SystemRoleResponse[]>('/api/v1/system/roles')
}

export function createSystemRole(data: CreateSystemRoleRequest) {
  return api.post<SystemRoleResponse>('/api/v1/system/roles', data)
}

export function deleteSystemRole(id: string) {
  return api.delete<void>(`/api/v1/system/roles/${id}`)
}