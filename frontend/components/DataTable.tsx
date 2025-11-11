"use client"
import { useMemo, useState } from 'react'
import type { Rule } from '@/app/rules/page'
import { MoreVertical } from 'lucide-react'
import { api, fetchApi } from '@/lib/api'

type Props = {
  items: Rule[]
  loading: boolean
  error: boolean
  onRetry: () => void
  sortField: 'name' | 'updatedAt'
  sortDir: 'asc' | 'desc'
  onSortChange: (f: 'name' | 'updatedAt') => void
}

const RuleTypeColor: Record<Rule['ruleType'], string> = {
  Risk: 'bg-indigo-100 text-indigo-700',
  Classification: 'bg-purple-100 text-purple-700',
  Compliance: 'bg-teal-100 text-teal-700',
  Valuation: 'bg-amber-100 text-amber-700',
}

const StatusColor: Record<Rule['status'], string> = {
  Active: 'bg-green-100 text-green-700',
  Draft: 'bg-amber-100 text-amber-700',
  Inactive: 'bg-slate-200 text-slate-700',
}

const FactTypeColor: Record<string, string> = {
  Declaration: 'bg-blue-100 text-blue-700 border border-blue-200',
  CargoReport: 'bg-purple-100 text-purple-700 border border-purple-200',
}

export function DataTable({ items, loading, error, onRetry, sortField, sortDir, onSortChange }: Props) {
  const [menuIndex, setMenuIndex] = useState<number | null>(null)

  const caret = (field: 'name' | 'updatedAt') => (
    <span aria-hidden className={`ml-1 ${sortField === field ? '' : 'opacity-0'}`}>{sortDir === 'asc' ? '▲' : '▼'}</span>
  )

  const skeletonRows = useMemo(() => Array.from({ length: 5 }).map((_, i) => i), [])

  return (
    <section className="bg-surface rounded-md border border-outlineVariant overflow-hidden" role="region" aria-label="Rules table">
      {error && (
        <div className="bg-red-50 text-red-800 px-4 py-2 flex items-center justify-between" data-testid="state-error">
          <span>Couldn't load rules.</span>
          <button className="px-3 py-1 rounded-md bg-red-600 text-white focus-ring" onClick={onRetry}>Retry</button>
        </div>
      )}
      <div className="overflow-x-auto">
        <table className="w-full text-sm" role="table" aria-label="Rules" data-testid="table-rules">
          <thead className="bg-surfaceContainerHigh text-slate-600" role="rowgroup">
            <tr role="row" className="h-12">
              <th role="columnheader" scope="col" className="text-left px-4 py-2 w-[20%]" data-testid="col-name">
                <button className="font-semibold focus-ring" aria-sort={sortField==='name'? (sortDir==='asc'?'ascending':'descending'):'none'} onClick={() => onSortChange('name')}>Rule Name {caret('name')}</button>
              </th>
              <th role="columnheader" scope="col" className="text-left px-4 py-2 w-[10%]" data-testid="col-fact-type">Fact Type</th>
              <th role="columnheader" scope="col" className="text-left px-4 py-2 w-[10%]" data-testid="col-doc">Document Type</th>
              <th role="columnheader" scope="col" className="text-left px-4 py-2 w-[10%]" data-testid="col-type">Rule Type</th>
              <th role="columnheader" scope="col" className="text-left px-4 py-2 w-[10%]" data-testid="col-output">Output</th>
              <th role="columnheader" scope="col" className="text-left px-4 py-2 w-[10%]" data-testid="col-status">Status</th>
              <th role="columnheader" scope="col" className="text-left px-4 py-2 w-[15%]" data-testid="col-updated">
                <button className="font-semibold focus-ring" aria-sort={sortField==='updatedAt'? (sortDir==='asc'?'ascending':'descending'):'none'} onClick={() => onSortChange('updatedAt')}>Updated {caret('updatedAt')}</button>
              </th>
              <th role="columnheader" scope="col" className="text-right px-2 py-2 w-[15%]" data-testid="col-actions">Actions</th>
            </tr>
          </thead>
          <tbody role="rowgroup">
            {loading && skeletonRows.map((k) => (
              <tr key={k} className="h-13 animate-pulse" data-testid="state-loading">
                {Array.from({ length: 8 }).map((_, i) => (
                  <td key={i} className="px-4 py-3">
                    <div className="h-4 bg-slate-200 rounded" />
                  </td>
                ))}
              </tr>
            ))}

            {!loading && items.length === 0 && (
              <tr>
                <td colSpan={8} className="px-4 py-10 text-center" data-testid="state-empty">
                  <div className="mx-auto w-12 h-12 rounded-full bg-slate-100 mb-2" />
                  <div className="text-slate-700 font-medium mb-2">No rules found</div>
                  <button className="px-3 py-1 rounded-md border border-outlineVariant hover:bg-surfaceContainerHigh focus-ring" onClick={() => window.location.reload()}>Clear filters</button>
                </td>
              </tr>
            )}

            {!loading && items.map((r, idx) => (
              <tr key={r.id} className="h-13 hover:bg-surfaceContainerHigh">
                <td className="px-4 py-3">
                  <div className="font-semibold truncate-tooltip" title={r.name}>{r.name}</div>
                </td>
                <td className="px-4 py-3">
                  <span className={`inline-flex items-center px-2 py-0.5 rounded-md text-xs font-semibold ${FactTypeColor[r.factType] || 'bg-slate-100 text-slate-700 border border-slate-200'}`}>
                    {r.factType}
                  </span>
                </td>
                <td className="px-4 py-3">{r.documentType}</td>
                <td className="px-4 py-3">
                  <span className={`inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium ${RuleTypeColor[r.ruleType]}`}>{r.ruleType}</span>
                </td>
                <td className="px-4 py-3">{r.outputType}</td>
                <td className="px-4 py-3">
                  <span className={`inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium ${StatusColor[r.status]}`}>{r.status}</span>
                </td>
                <td className="px-4 py-3">{new Date(r.updatedAt).toLocaleString(undefined, { month: 'short', day: '2-digit', year: 'numeric', hour: '2-digit', minute: '2-digit' })}</td>
                <td className="px-2 py-3 text-right">
                  <div className="relative inline-block">
                    <button
                      aria-haspopup="menu"
                      aria-expanded={menuIndex===idx}
                      onClick={() => setMenuIndex(menuIndex === idx ? null : idx)}
                      onKeyDown={(e) => { if (e.key === 'Escape') setMenuIndex(null) }}
                      className="p-2 rounded-md hover:bg-surfaceContainerHigh focus-ring"
                      data-testid="menu-row-actions"
                    >
                      <MoreVertical size={16} />
                    </button>
                    {menuIndex === idx && (
                      <div role="menu" className="absolute right-0 mt-1 w-32 bg-white border border-outlineVariant rounded-md shadow-md z-10">
                        <a href={`/rules/${r.id}`} className="block px-3 py-2 hover:bg-surfaceContainerHigh" data-testid="action-view">View</a>
                        <a href={`/rules/${r.id}`} className="block px-3 py-2 hover:bg-surfaceContainerHigh" data-testid="action-edit">Edit</a>
                        <button className="w-full text-left px-3 py-2 hover:bg-surfaceContainerHigh text-red-600" data-testid="action-delete" onClick={async () => {
                          const txt = prompt('Type DELETE to confirm')
                          if (txt === 'DELETE') {
                            await fetchApi(api.rules.delete(r.id), { method: 'DELETE' })
                            window.location.reload()
                          }
                        }}>Delete</button>
                      </div>
                    )}
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </section>
  )
}


