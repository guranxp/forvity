import { type FormEvent, useState } from 'react'
import { useMutation } from '@tanstack/react-query'
import { createClub, type CreateClubRequest } from '../api/clubs'
import { Layout } from '../components/Layout'
import { Card } from '../components/Card'
import { Button } from '../components/Button'
import { Input } from '../components/Input'
import { Plus } from 'lucide-react'

export function ClubsPage() {
  const [showForm, setShowForm] = useState(false)
  const [form, setForm] = useState<CreateClubRequest>({ name: '', slug: '' })
  const [formError, setFormError] = useState('')
  const [created, setCreated] = useState<{ name: string; slug: string } | null>(null)

  const createMutation = useMutation({
    mutationFn: createClub,
    onSuccess: (club) => {
      setCreated({ name: club.name, slug: club.slug })
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

  async function handleCreate(e: FormEvent) {
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

      {created && (
        <div className="mb-6 rounded-lg bg-green-50 border border-green-200 px-4 py-3 text-sm text-green-700">
          Club <strong>{created.name}</strong> created with slug <code className="bg-green-100 px-1 rounded">{created.slug}</code>.
        </div>
      )}

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
        <p className="text-sm text-gray-500">
          Club listing will be available once <code className="bg-gray-100 px-1 rounded text-xs">GET /api/v1/clubs</code> is implemented.
        </p>
      </Card>
    </Layout>
  )
}
