import { type FormEvent, useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { listClubs, createClub, type CreateClubRequest } from '../api/clubs'
import { Layout } from '../components/Layout'
import { Card } from '../components/Card'
import { Button } from '../components/Button'
import { Input } from '../components/Input'
import { Plus } from 'lucide-react'

export function ClubsPage() {
  const queryClient = useQueryClient()
  const [showForm, setShowForm] = useState(false)
  const [form, setForm] = useState<CreateClubRequest>({ name: '', slug: '' })
  const [formError, setFormError] = useState('')

  const { data: clubs = [], isLoading } = useQuery({
    queryKey: ['clubs'],
    queryFn: listClubs,
  })

  const createMutation = useMutation({
    mutationFn: createClub,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['clubs'] })
      setShowForm(false)
      setForm({ name: '', slug: '' })
      setFormError('')
    },
    onError: (err: Error) => setFormError(err.message),
  })

  function handleNameChange(name: string) {
    const slug = name.toLowerCase().replace(/\s+/g, '-').replace(/[^a-z0-9-]/g, '')
    setForm({ name, slug })
  }

  function handleCreate(e: FormEvent) {
    e.preventDefault()
    setFormError('')
    createMutation.mutate(form)
  }

  return (
    <Layout>
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-xl font-semibold text-gray-900">Clubs</h1>
        <Button onClick={() => setShowForm(!showForm)}>
          <Plus size={16} />
          Create club
        </Button>
      </div>

      {showForm && (
        <Card title="New club" className="mb-6">
          <form onSubmit={handleCreate} className="flex flex-col gap-4 max-w-md">
            <Input
              label="Club name"
              value={form.name}
              onChange={e => handleNameChange(e.target.value)}
              required
            />
            <Input
              label="Slug"
              value={form.slug}
              onChange={e => setForm({ ...form, slug: e.target.value })}
              placeholder="e.g. fc-stockholm"
              required
            />
            {formError && <p className="text-sm text-red-500">{formError}</p>}
            <div className="flex gap-2">
              <Button type="submit" loading={createMutation.isPending}>Create</Button>
              <Button type="button" variant="ghost" onClick={() => setShowForm(false)}>Cancel</Button>
            </div>
          </form>
        </Card>
      )}

      <Card>
        {isLoading ? (
          <p className="text-sm text-gray-500">Loading...</p>
        ) : clubs.length === 0 ? (
          <p className="text-sm text-gray-500">No clubs yet.</p>
        ) : (
          <table className="w-full text-sm">
            <thead>
              <tr className="text-left text-gray-500 border-b border-gray-100">
                <th className="pb-3 font-medium">Name</th>
                <th className="pb-3 font-medium">Slug</th>
                <th className="pb-3 font-medium">Created</th>
              </tr>
            </thead>
            <tbody>
              {clubs.map(club => (
                <tr key={club.id} className="border-b border-gray-50 last:border-0">
                  <td className="py-3 text-gray-900">{club.name}</td>
                  <td className="py-3 text-gray-500 font-mono text-xs">{club.slug}</td>
                  <td className="py-3 text-gray-400">{new Date(club.createdAt).toLocaleDateString()}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </Card>
    </Layout>
  )
}
