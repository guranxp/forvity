import { type FormEvent, useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { listSystemRoles, createSystemRole, deleteSystemRole, type CreateSystemRoleRequest } from '../api/systemRoles'
import { Layout } from '../components/Layout'
import { Card } from '../components/Card'
import { Button } from '../components/Button'
import { Input } from '../components/Input'
import { Trash2, Plus } from 'lucide-react'

export function SystemAdminsPage() {
  const queryClient = useQueryClient()
  const [showForm, setShowForm] = useState(false)
  const [form, setForm] = useState<CreateSystemRoleRequest>({ email: '', username: '', password: '' })
  const [formError, setFormError] = useState('')

  const { data: admins = [], isLoading } = useQuery({
    queryKey: ['system-roles'],
    queryFn: listSystemRoles,
  })

  const createMutation = useMutation({
    mutationFn: createSystemRole,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['system-roles'] })
      setShowForm(false)
      setForm({ email: '', username: '', password: '' })
      setFormError('')
    },
    onError: (err: Error) => setFormError(err.message),
  })

  const deleteMutation = useMutation({
    mutationFn: deleteSystemRole,
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['system-roles'] }),
  })

  async function handleCreate(e: FormEvent) {
    e.preventDefault()
    setFormError('')
    createMutation.mutate(form)
  }

  return (
    <Layout>
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-xl font-semibold text-gray-900">System Admins</h1>
        <Button onClick={() => setShowForm(!showForm)}>
          <Plus size={16} />
          Add SUPERADMIN
        </Button>
      </div>

      {showForm && (
        <Card title="New SUPERADMIN" className="mb-6">
          <form onSubmit={handleCreate} className="flex flex-col gap-4 max-w-md">
            <Input
              label="Email"
              type="email"
              value={form.email}
              onChange={e => setForm({ ...form, email: e.target.value })}
              required
            />
            <Input
              label="Username"
              value={form.username}
              onChange={e => setForm({ ...form, username: e.target.value })}
              required
            />
            <Input
              label="Password"
              type="password"
              value={form.password}
              onChange={e => setForm({ ...form, password: e.target.value })}
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
        ) : admins.length === 0 ? (
          <p className="text-sm text-gray-500">No system admins found.</p>
        ) : (
          <table className="w-full text-sm">
            <thead>
              <tr className="text-left text-gray-500 border-b border-gray-100">
                <th className="pb-3 font-medium">Email</th>
                <th className="pb-3 font-medium">Username</th>
                <th className="pb-3 font-medium">Role</th>
                <th className="pb-3 font-medium">Created</th>
                <th className="pb-3" />
              </tr>
            </thead>
            <tbody>
              {admins.map(admin => (
                <tr key={admin.id} className="border-b border-gray-50 last:border-0">
                  <td className="py-3 text-gray-900">{admin.email}</td>
                  <td className="py-3 text-gray-600">{admin.username}</td>
                  <td className="py-3">
                    <span className="rounded-full bg-indigo-50 px-2 py-0.5 text-xs font-medium text-indigo-700">
                      {admin.role}
                    </span>
                  </td>
                  <td className="py-3 text-gray-400">{new Date(admin.createdAt).toLocaleDateString()}</td>
                  <td className="py-3 text-right">
                    {admin.role !== 'ROOT' && (
                      <Button
                        variant="ghost"
                        onClick={() => deleteMutation.mutate(admin.id)}
                        loading={deleteMutation.isPending}
                        title="Revoke"
                      >
                        <Trash2 size={15} className="text-red-500" />
                      </Button>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </Card>
    </Layout>
  )
}
