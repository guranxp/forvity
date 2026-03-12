import { api } from './client'

export interface ClubResponse {
  id: string
  name: string
  slug: string
  createdAt: string
}

export interface CreateClubRequest {
  name: string
  slug: string
}

export function listClubs() {
  return api.get<ClubResponse[]>('/api/v1/clubs')
}

export function createClub(data: CreateClubRequest) {
  return api.post<ClubResponse>('/api/v1/clubs', data)
}