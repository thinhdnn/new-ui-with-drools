"use client"
import { useEffect, useState } from 'react'
import { useQuery } from '@tanstack/react-query'
import { useRouter } from 'next/navigation'
import { api, fetchApi } from '@/lib/api'
import { FileCheck, Plus, CheckCircle, XCircle, Package, Clock, Eye, ExternalLink } from 'lucide-react'

export type ChangeRequest = {
  id: number
  factType: string
  title: string
  description?: string
  status: 'Pending' | 'Approved' | 'Rejected'
  changesJson?: string
  approvedBy?: string
  approvedDate?: string
  rejectedBy?: string
  rejectedDate?: string
  rejectionReason?: string
  createdAt?: string
  createdBy?: string
}

export default function ChangeRequestsPage() {
  const router = useRouter()
  const [factTypes, setFactTypes] = useState<string[]>([])
  const [selectedFactType, setSelectedFactType] = useState<string>('All')
  const [selectedStatus, setSelectedStatus] = useState<string>('All')
  const [showCreateModal, setShowCreateModal] = useState(false)
  const [selectedChangeRequest, setSelectedChangeRequest] = useState<ChangeRequest | null>(null)
  const [showDetailModal, setShowDetailModal] = useState(false)
  const [changeRequestRules, setChangeRequestRules] = useState<Map<number, any>>(new Map())
  const [createForm, setCreateForm] = useState({
    factType: 'Declaration',
    title: '',
    description: '',
    rulesToAdd: [] as number[],
    rulesToUpdate: [] as number[],
    rulesToDelete: [] as number[],
  })

  // Load fact types on mount
  useEffect(() => {
    const loadFactTypes = async () => {
      try {
        const types = await fetchApi<string[]>(api.rules.factTypes())
        setFactTypes(types.length > 0 ? types : ['Declaration'])
      } catch (err) {
        console.error('Failed to load fact types:', err)
        setFactTypes(['Declaration'])
      }
    }
    loadFactTypes()
  }, [])

  // Fetch change requests
  const { data: changeRequests, isLoading, refetch } = useQuery<ChangeRequest[]>({
    queryKey: ['changeRequests', selectedFactType, selectedStatus],
    queryFn: async () => {
      const factType = selectedFactType !== 'All' ? selectedFactType : undefined
      const status = selectedStatus !== 'All' ? selectedStatus : undefined
      return fetchApi<ChangeRequest[]>(api.changeRequests.list(factType, status))
    },
    staleTime: 10_000,
  })

  // Load rules for fact type selection
  const { data: rules } = useQuery<any[]>({
    queryKey: ['rules', createForm.factType],
    queryFn: async () => {
      const allRules = await fetchApi<any[]>(api.rules.list())
      return allRules.filter((r: any) => r.factType === createForm.factType)
    },
    enabled: showCreateModal,
  })

  const handleCreate = async () => {
    try {
      const payload = {
        factType: createForm.factType,
        title: createForm.title,
        description: createForm.description,
        changes: {
          rulesToAdd: createForm.rulesToAdd,
          rulesToUpdate: createForm.rulesToUpdate,
          rulesToDelete: createForm.rulesToDelete,
        },
      }

      await fetchApi(api.changeRequests.create(), {
        method: 'POST',
        body: JSON.stringify(payload),
      })

      setShowCreateModal(false)
      setCreateForm({
        factType: 'Declaration',
        title: '',
        description: '',
        rulesToAdd: [],
        rulesToUpdate: [],
        rulesToDelete: [],
      })
      refetch()
      alert('Change request created successfully!')
    } catch (err) {
      console.error('Failed to create change request:', err)
      alert(err instanceof Error ? err.message : 'Failed to create change request')
    }
  }

  const handleApprove = async (id: number) => {
    if (!confirm('Are you sure you want to approve this change request? This will apply the changes and deploy the rules.')) {
      return
    }

    try {
      await fetchApi(api.changeRequests.approve(id), {
        method: 'POST',
        body: JSON.stringify({ approvedBy: 'current-user' }),
      })
      refetch()
      alert('Change request approved and changes deployed successfully!')
    } catch (err) {
      console.error('Failed to approve change request:', err)
      alert(err instanceof Error ? err.message : 'Failed to approve change request')
    }
  }

  const handleReject = async (id: number) => {
    const reason = prompt('Please provide a reason for rejection:')
    if (!reason) return

    try {
      await fetchApi(api.changeRequests.reject(id), {
        method: 'POST',
        body: JSON.stringify({ rejectedBy: 'current-user', rejectionReason: reason }),
      })
      refetch()
      alert('Change request rejected successfully!')
    } catch (err) {
      console.error('Failed to reject change request:', err)
      alert(err instanceof Error ? err.message : 'Failed to reject change request')
    }
  }

  const handleViewDetail = async (changeRequest: ChangeRequest) => {
    setSelectedChangeRequest(changeRequest)
    setShowDetailModal(true)

    // Parse changes JSON and load rule details
    if (changeRequest.changesJson) {
      try {
        const changes = JSON.parse(changeRequest.changesJson)
        const allRuleIds = [
          ...(changes.rulesToAdd || []),
          ...(changes.rulesToUpdate || []),
          ...(changes.rulesToDelete || []),
        ]

        // Load rule details for all rule IDs
        const rulesMap = new Map<number, any>()
        await Promise.all(
          allRuleIds.map(async (ruleId: number) => {
            try {
              const rule = await fetchApi(api.rules.get(ruleId.toString()))
              rulesMap.set(ruleId, rule)
            } catch (err) {
              console.error(`Failed to load rule ${ruleId}:`, err)
            }
          })
        )
        setChangeRequestRules(rulesMap)
      } catch (err) {
        console.error('Failed to parse changes JSON:', err)
      }
    }
  }

  const handleViewRule = (ruleId: number) => {
    router.push(`/rules/${ruleId}`)
  }

  const getStatusBadge = (status: string) => {
    const baseClasses = 'inline-flex items-center gap-1 px-2 py-1 rounded-full text-xs font-medium'
    switch (status) {
      case 'Pending':
        return (
          <span className={`${baseClasses} bg-yellow-100 text-yellow-800`}>
            <Clock className="w-3 h-3" />
            Pending
          </span>
        )
      case 'Approved':
        return (
          <span className={`${baseClasses} bg-green-100 text-green-800`}>
            <CheckCircle className="w-3 h-3" />
            Approved
          </span>
        )
      case 'Rejected':
        return (
          <span className={`${baseClasses} bg-red-100 text-red-800`}>
            <XCircle className="w-3 h-3" />
            Rejected
          </span>
        )
      default:
        return <span className={baseClasses}>{status}</span>
    }
  }

  return (
    <div className="flex flex-col gap-4">
      {/* Header */}
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-semibold flex items-center gap-2">
          <FileCheck className="w-6 h-6" />
          Change Requests
        </h1>
        <button
          onClick={() => setShowCreateModal(true)}
          className="flex items-center gap-2 px-4 py-2 bg-indigo-600 text-white rounded-md hover:bg-indigo-700 focus-ring"
        >
          <Plus size={16} />
          New Change Request
        </button>
      </div>

      {/* Filters */}
      <div className="flex items-center gap-4 bg-white border border-outlineVariant rounded-md p-4">
        {/* Fact Type Filter */}
        <div className="flex items-center gap-2">
          <Package size={16} className="text-slate-500" />
          <label className="text-sm font-medium text-slate-700">Fact Type:</label>
          <select
            value={selectedFactType}
            onChange={(e) => setSelectedFactType(e.target.value)}
            className="px-3 py-1.5 text-sm border border-outlineVariant rounded-md focus-ring"
          >
            <option value="All">All</option>
            {factTypes.map((type) => (
              <option key={type} value={type}>
                {type}
              </option>
            ))}
          </select>
        </div>

        {/* Status Filter */}
        <div className="flex items-center gap-2">
          <label className="text-sm font-medium text-slate-700">Status:</label>
          <select
            value={selectedStatus}
            onChange={(e) => setSelectedStatus(e.target.value)}
            className="px-3 py-1.5 text-sm border border-outlineVariant rounded-md focus-ring"
          >
            <option value="All">All</option>
            <option value="Pending">Pending</option>
            <option value="Approved">Approved</option>
            <option value="Rejected">Rejected</option>
          </select>
        </div>
      </div>

      {/* Change Requests List */}
      <div className="bg-white border border-outlineVariant rounded-md overflow-hidden">
        {isLoading ? (
          <div className="p-8 text-center text-slate-500">Loading...</div>
        ) : !changeRequests || changeRequests.length === 0 ? (
          <div className="p-8 text-center text-slate-500">No change requests found</div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full">
              <thead className="bg-slate-50 border-b border-outlineVariant">
                <tr>
                  <th className="px-4 py-3 text-left text-xs font-medium text-slate-700 uppercase">ID</th>
                  <th className="px-4 py-3 text-left text-xs font-medium text-slate-700 uppercase">Title</th>
                  <th className="px-4 py-3 text-left text-xs font-medium text-slate-700 uppercase">Fact Type</th>
                  <th className="px-4 py-3 text-left text-xs font-medium text-slate-700 uppercase">Status</th>
                  <th className="px-4 py-3 text-left text-xs font-medium text-slate-700 uppercase">Created</th>
                  <th className="px-4 py-3 text-left text-xs font-medium text-slate-700 uppercase">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-outlineVariant">
                {changeRequests.map((cr) => (
                  <tr
                    key={cr.id}
                    className="hover:bg-slate-50 cursor-pointer"
                    onClick={() => handleViewDetail(cr)}
                  >
                    <td className="px-4 py-3 text-sm text-slate-900">#{cr.id}</td>
                    <td className="px-4 py-3 text-sm text-slate-900">{cr.title}</td>
                    <td className="px-4 py-3 text-sm text-slate-600">{cr.factType}</td>
                    <td className="px-4 py-3">{getStatusBadge(cr.status)}</td>
                    <td className="px-4 py-3 text-sm text-slate-600">
                      {cr.createdAt ? new Date(cr.createdAt).toLocaleDateString() : '-'}
                    </td>
                    <td className="px-4 py-3" onClick={(e) => e.stopPropagation()}>
                      <div className="flex items-center gap-2">
                        <button
                          onClick={() => handleViewDetail(cr)}
                          className="px-2 py-1 text-xs bg-indigo-600 text-white rounded hover:bg-indigo-700 focus-ring flex items-center gap-1"
                          title="View Details"
                        >
                          <Eye className="w-3 h-3" />
                          View
                        </button>
                        {cr.status === 'Pending' && (
                          <>
                            <button
                              onClick={() => handleApprove(cr.id)}
                              className="px-3 py-1 text-xs bg-green-600 text-white rounded hover:bg-green-700 focus-ring"
                            >
                              Approve
                            </button>
                            <button
                              onClick={() => handleReject(cr.id)}
                              className="px-3 py-1 text-xs bg-red-600 text-white rounded hover:bg-red-700 focus-ring"
                            >
                              Reject
                            </button>
                          </>
                        )}
                        {cr.status === 'Rejected' && cr.rejectionReason && (
                          <span className="text-xs text-red-600" title={cr.rejectionReason}>
                            {cr.rejectionReason}
                          </span>
                        )}
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* Create Modal */}
      {showCreateModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/30">
          <div className="bg-white rounded-lg shadow-lg w-full max-w-2xl max-h-[90vh] overflow-y-auto">
            <div className="p-6 border-b border-outlineVariant">
              <h2 className="text-xl font-semibold">Create Change Request</h2>
            </div>
            <div className="p-6 space-y-4">
              {/* Fact Type */}
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">Fact Type</label>
                <select
                  value={createForm.factType}
                  onChange={(e) => setCreateForm({ ...createForm, factType: e.target.value })}
                  className="w-full px-3 py-1.5 text-sm border border-outlineVariant rounded-md focus-ring"
                >
                  {factTypes.map((type) => (
                    <option key={type} value={type}>
                      {type}
                    </option>
                  ))}
                </select>
              </div>

              {/* Title */}
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">Title</label>
                <input
                  type="text"
                  value={createForm.title}
                  onChange={(e) => setCreateForm({ ...createForm, title: e.target.value })}
                  className="w-full px-3 py-1.5 text-sm border border-outlineVariant rounded-md focus-ring"
                  placeholder="e.g., Update Declaration Rules for Q1 2025"
                />
              </div>

              {/* Description */}
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">Description</label>
                <textarea
                  value={createForm.description}
                  onChange={(e) => setCreateForm({ ...createForm, description: e.target.value })}
                  className="w-full px-3 py-1.5 text-sm border border-outlineVariant rounded-md focus-ring"
                  rows={3}
                  placeholder="Describe the proposed changes..."
                />
              </div>

              {/* Selected Rules Summary */}
              {(createForm.rulesToAdd.length > 0 || createForm.rulesToUpdate.length > 0 || createForm.rulesToDelete.length > 0) && (
                <div className="bg-slate-50 border border-outlineVariant rounded-md p-4 space-y-2">
                  <div className="text-sm font-medium text-slate-700">Selected Changes:</div>
                  {createForm.rulesToAdd.length > 0 && (
                    <div className="text-xs text-slate-600">
                      <span className="font-medium text-green-700">To Add:</span> {createForm.rulesToAdd.length} rule(s)
                    </div>
                  )}
                  {createForm.rulesToUpdate.length > 0 && (
                    <div className="text-xs text-slate-600">
                      <span className="font-medium text-blue-700">To Update:</span> {createForm.rulesToUpdate.length} rule(s)
                    </div>
                  )}
                  {createForm.rulesToDelete.length > 0 && (
                    <div className="text-xs text-slate-600">
                      <span className="font-medium text-red-700">To Delete:</span> {createForm.rulesToDelete.length} rule(s)
                    </div>
                  )}
                </div>
              )}

              {/* Rules Table */}
              {rules && rules.length > 0 && (
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-2">Select Rules</label>
                  <div className="border border-outlineVariant rounded-md overflow-hidden">
                    <div className="overflow-x-auto max-h-96 overflow-y-auto">
                      <table className="w-full">
                        <thead className="bg-slate-50 border-b border-outlineVariant sticky top-0">
                          <tr>
                            <th className="px-4 py-2 text-left text-xs font-medium text-slate-700 uppercase">Rule Name</th>
                            <th className="px-4 py-2 text-left text-xs font-medium text-slate-700 uppercase">Status</th>
                            <th className="px-4 py-2 text-center text-xs font-medium text-slate-700 uppercase">Actions</th>
                          </tr>
                        </thead>
                        <tbody className="divide-y divide-outlineVariant">
                          {rules.map((rule: any) => {
                            const ruleId = typeof rule.id === 'string' ? parseInt(rule.id, 10) : rule.id
                            const isInAdd = createForm.rulesToAdd.includes(ruleId)
                            const isInUpdate = createForm.rulesToUpdate.includes(ruleId)
                            const isInDelete = createForm.rulesToDelete.includes(ruleId)
                            const isSelected = isInAdd || isInUpdate || isInDelete
                            const isDraft = !rule.active
                            const isActive = rule.active

                            return (
                              <tr
                                key={ruleId}
                                className={`hover:bg-slate-50 ${isSelected ? 'bg-indigo-50' : ''}`}
                              >
                                <td className="px-4 py-2 text-sm text-slate-900">
                                  {rule.ruleName || rule.name || `Rule #${ruleId}`}
                                </td>
                                <td className="px-4 py-2">
                                  {isDraft ? (
                                    <span className="inline-flex items-center gap-1 px-2 py-0.5 rounded-full text-xs font-medium bg-yellow-100 text-yellow-800">
                                      Draft
                                    </span>
                                  ) : (
                                    <span className="inline-flex items-center gap-1 px-2 py-0.5 rounded-full text-xs font-medium bg-green-100 text-green-800">
                                      Active
                                    </span>
                                  )}
                                </td>
                                <td className="px-4 py-2">
                                  <div className="flex items-center justify-center gap-2">
                                    {isDraft && (
                                      <button
                                        onClick={() => {
                                          if (isInAdd) {
                                            setCreateForm({
                                              ...createForm,
                                              rulesToAdd: createForm.rulesToAdd.filter((id) => id !== ruleId),
                                            })
                                          } else {
                                            setCreateForm({
                                              ...createForm,
                                              rulesToAdd: [...createForm.rulesToAdd, ruleId],
                                              rulesToUpdate: createForm.rulesToUpdate.filter((id) => id !== ruleId),
                                              rulesToDelete: createForm.rulesToDelete.filter((id) => id !== ruleId),
                                            })
                                          }
                                        }}
                                        className={`px-2 py-1 text-xs rounded focus-ring ${
                                          isInAdd
                                            ? 'bg-green-600 text-white hover:bg-green-700'
                                            : 'bg-green-100 text-green-700 hover:bg-green-200'
                                        }`}
                                      >
                                        {isInAdd ? '✓ Add' : 'Add'}
                                      </button>
                                    )}
                                    {isActive && (
                                      <>
                                        <button
                                          onClick={() => {
                                            if (isInUpdate) {
                                              setCreateForm({
                                                ...createForm,
                                                rulesToUpdate: createForm.rulesToUpdate.filter((id) => id !== ruleId),
                                              })
                                            } else {
                                              setCreateForm({
                                                ...createForm,
                                                rulesToUpdate: [...createForm.rulesToUpdate, ruleId],
                                                rulesToAdd: createForm.rulesToAdd.filter((id) => id !== ruleId),
                                                rulesToDelete: createForm.rulesToDelete.filter((id) => id !== ruleId),
                                              })
                                            }
                                          }}
                                          className={`px-2 py-1 text-xs rounded focus-ring ${
                                            isInUpdate
                                              ? 'bg-blue-600 text-white hover:bg-blue-700'
                                              : 'bg-blue-100 text-blue-700 hover:bg-blue-200'
                                          }`}
                                        >
                                          {isInUpdate ? '✓ Update' : 'Update'}
                                        </button>
                                        <button
                                          onClick={() => {
                                            if (isInDelete) {
                                              setCreateForm({
                                                ...createForm,
                                                rulesToDelete: createForm.rulesToDelete.filter((id) => id !== ruleId),
                                              })
                                            } else {
                                              setCreateForm({
                                                ...createForm,
                                                rulesToDelete: [...createForm.rulesToDelete, ruleId],
                                                rulesToAdd: createForm.rulesToAdd.filter((id) => id !== ruleId),
                                                rulesToUpdate: createForm.rulesToUpdate.filter((id) => id !== ruleId),
                                              })
                                            }
                                          }}
                                          className={`px-2 py-1 text-xs rounded focus-ring ${
                                            isInDelete
                                              ? 'bg-red-600 text-white hover:bg-red-700'
                                              : 'bg-red-100 text-red-700 hover:bg-red-200'
                                          }`}
                                        >
                                          {isInDelete ? '✓ Delete' : 'Delete'}
                                        </button>
                                      </>
                                    )}
                                  </div>
                                </td>
                              </tr>
                            )
                          })}
                        </tbody>
                      </table>
                    </div>
                  </div>
                </div>
              )}
              {rules && rules.length === 0 && (
                <div className="text-sm text-slate-500 text-center py-4">
                  No rules found for this fact type
                </div>
              )}
            </div>
            <div className="p-6 border-t border-outlineVariant flex justify-end gap-2">
              <button
                onClick={() => setShowCreateModal(false)}
                className="px-4 py-2 text-sm border border-outlineVariant rounded-md hover:bg-slate-50 focus-ring"
              >
                Cancel
              </button>
              <button
                onClick={handleCreate}
                disabled={!createForm.title || !createForm.factType}
                className="px-4 py-2 text-sm bg-indigo-600 text-white rounded-md hover:bg-indigo-700 disabled:opacity-50 disabled:cursor-not-allowed focus-ring"
              >
                Create
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Change Request Detail Modal */}
      {showDetailModal && selectedChangeRequest && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/30">
          <div className="bg-white rounded-lg shadow-lg w-full max-w-4xl max-h-[90vh] overflow-y-auto">
            <div className="p-6 border-b border-outlineVariant">
              <div className="flex items-center justify-between">
                <h2 className="text-xl font-semibold">Change Request Details</h2>
                <button
                  onClick={() => {
                    setShowDetailModal(false)
                    setSelectedChangeRequest(null)
                    setChangeRequestRules(new Map())
                  }}
                  className="text-slate-500 hover:text-slate-700 focus-ring"
                >
                  ✕
                </button>
              </div>
            </div>
            <div className="p-6 space-y-6">
              {/* Basic Information */}
              <div className="space-y-4">
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">Title</label>
                  <div className="text-sm text-slate-900">{selectedChangeRequest.title}</div>
                </div>
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">Fact Type</label>
                  <div className="text-sm text-slate-900">{selectedChangeRequest.factType}</div>
                </div>
                {selectedChangeRequest.description && (
                  <div>
                    <label className="block text-sm font-medium text-slate-700 mb-1">Description</label>
                    <div className="text-sm text-slate-900 whitespace-pre-wrap">{selectedChangeRequest.description}</div>
                  </div>
                )}
                <div className="flex items-center gap-4">
                  <div>
                    <label className="block text-sm font-medium text-slate-700 mb-1">Status</label>
                    <div>{getStatusBadge(selectedChangeRequest.status)}</div>
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-slate-700 mb-1">Created</label>
                    <div className="text-sm text-slate-600">
                      {selectedChangeRequest.createdAt ? new Date(selectedChangeRequest.createdAt).toLocaleString() : '-'}
                    </div>
                  </div>
                  {selectedChangeRequest.createdBy && (
                    <div>
                      <label className="block text-sm font-medium text-slate-700 mb-1">Created By</label>
                      <div className="text-sm text-slate-600">{selectedChangeRequest.createdBy}</div>
                    </div>
                  )}
                </div>
                {selectedChangeRequest.status === 'Approved' && selectedChangeRequest.approvedBy && (
                  <div>
                    <label className="block text-sm font-medium text-slate-700 mb-1">Approved</label>
                    <div className="text-sm text-slate-600">
                      By {selectedChangeRequest.approvedBy} on{' '}
                      {selectedChangeRequest.approvedDate
                        ? new Date(selectedChangeRequest.approvedDate).toLocaleString()
                        : '-'}
                    </div>
                  </div>
                )}
                {selectedChangeRequest.status === 'Rejected' && selectedChangeRequest.rejectedBy && (
                  <div>
                    <label className="block text-sm font-medium text-slate-700 mb-1">Rejected</label>
                    <div className="text-sm text-slate-600">
                      By {selectedChangeRequest.rejectedBy} on{' '}
                      {selectedChangeRequest.rejectedDate
                        ? new Date(selectedChangeRequest.rejectedDate).toLocaleString()
                        : '-'}
                    </div>
                    {selectedChangeRequest.rejectionReason && (
                      <div className="mt-1 text-sm text-red-600">{selectedChangeRequest.rejectionReason}</div>
                    )}
                  </div>
                )}
              </div>

              {/* Changes Table */}
              {selectedChangeRequest.changesJson && (
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-2">Proposed Changes</label>
                  <div className="border border-outlineVariant rounded-md overflow-hidden">
                    <div className="overflow-x-auto">
                      <table className="w-full">
                        <thead className="bg-slate-50 border-b border-outlineVariant">
                          <tr>
                            <th className="px-4 py-2 text-left text-xs font-medium text-slate-700 uppercase">Action</th>
                            <th className="px-4 py-2 text-left text-xs font-medium text-slate-700 uppercase">Rule ID</th>
                            <th className="px-4 py-2 text-left text-xs font-medium text-slate-700 uppercase">Rule Name</th>
                            <th className="px-4 py-2 text-left text-xs font-medium text-slate-700 uppercase">Status</th>
                            <th className="px-4 py-2 text-center text-xs font-medium text-slate-700 uppercase">Actions</th>
                          </tr>
                        </thead>
                        <tbody className="divide-y divide-outlineVariant">
                          {(() => {
                            try {
                              const changes = JSON.parse(selectedChangeRequest.changesJson)
                              const rows: Array<{ action: string; ruleId: number; color: string }> = []

                              // Rules to Add
                              if (changes.rulesToAdd && Array.isArray(changes.rulesToAdd)) {
                                changes.rulesToAdd.forEach((ruleId: number) => {
                                  rows.push({ action: 'Add', ruleId, color: 'green' })
                                })
                              }

                              // Rules to Update
                              if (changes.rulesToUpdate && Array.isArray(changes.rulesToUpdate)) {
                                changes.rulesToUpdate.forEach((ruleId: number) => {
                                  rows.push({ action: 'Update', ruleId, color: 'blue' })
                                })
                              }

                              // Rules to Delete
                              if (changes.rulesToDelete && Array.isArray(changes.rulesToDelete)) {
                                changes.rulesToDelete.forEach((ruleId: number) => {
                                  rows.push({ action: 'Delete', ruleId, color: 'red' })
                                })
                              }

                              return rows.map((row) => {
                                const rule = changeRequestRules.get(row.ruleId)
                                const ruleName = rule?.ruleName || rule?.name || `Rule #${row.ruleId}`
                                const isActive = rule?.active ?? false

                                return (
                                  <tr key={`${row.action}-${row.ruleId}`} className="hover:bg-slate-50">
                                    <td className="px-4 py-2">
                                      <span
                                        className={`inline-flex items-center px-2 py-1 rounded-full text-xs font-medium ${
                                          row.color === 'green'
                                            ? 'bg-green-100 text-green-800'
                                            : row.color === 'blue'
                                            ? 'bg-blue-100 text-blue-800'
                                            : 'bg-red-100 text-red-800'
                                        }`}
                                      >
                                        {row.action}
                                      </span>
                                    </td>
                                    <td className="px-4 py-2 text-sm text-slate-900">#{row.ruleId}</td>
                                    <td className="px-4 py-2 text-sm text-slate-900">{ruleName}</td>
                                    <td className="px-4 py-2">
                                      {isActive ? (
                                        <span className="inline-flex items-center gap-1 px-2 py-0.5 rounded-full text-xs font-medium bg-green-100 text-green-800">
                                          Active
                                        </span>
                                      ) : (
                                        <span className="inline-flex items-center gap-1 px-2 py-0.5 rounded-full text-xs font-medium bg-yellow-100 text-yellow-800">
                                          Draft
                                        </span>
                                      )}
                                    </td>
                                    <td className="px-4 py-2 text-center">
                                      <button
                                        onClick={() => handleViewRule(row.ruleId)}
                                        className="inline-flex items-center gap-1 px-2 py-1 text-xs bg-indigo-100 text-indigo-700 rounded hover:bg-indigo-200 focus-ring"
                                        title="View Rule Details"
                                      >
                                        <ExternalLink className="w-3 h-3" />
                                        View Rule
                                      </button>
                                    </td>
                                  </tr>
                                )
                              })
                            } catch (err) {
                              return (
                                <tr>
                                  <td colSpan={5} className="px-4 py-4 text-sm text-slate-500 text-center">
                                    Failed to parse changes
                                  </td>
                                </tr>
                              )
                            }
                          })()}
                        </tbody>
                      </table>
                    </div>
                  </div>
                </div>
              )}

              {/* Action Buttons for Pending Requests */}
              {selectedChangeRequest.status === 'Pending' && (
                <div className="flex justify-end gap-2 pt-4 border-t border-outlineVariant">
                  <button
                    onClick={() => {
                      setShowDetailModal(false)
                      handleApprove(selectedChangeRequest.id)
                    }}
                    className="px-4 py-2 text-sm bg-green-600 text-white rounded-md hover:bg-green-700 focus-ring"
                  >
                    Approve
                  </button>
                  <button
                    onClick={() => {
                      setShowDetailModal(false)
                      handleReject(selectedChangeRequest.id)
                    }}
                    className="px-4 py-2 text-sm bg-red-600 text-white rounded-md hover:bg-red-700 focus-ring"
                  >
                    Reject
                  </button>
                </div>
              )}
            </div>
          </div>
        </div>
      )}
    </div>
  )
}

