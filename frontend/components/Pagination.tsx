"use client"
import { ChevronLeft, ChevronRight } from 'lucide-react'

type Props = {
  page: number
  pageSize: number
  total: number
  onPageChange: (p: number) => void
  onPageSizeChange: (n: number) => void
}

export function PaginationBar({ page, pageSize, total, onPageChange, onPageSizeChange }: Props) {
  const totalPages = Math.max(1, Math.ceil(total / pageSize))
  const start = total === 0 ? 0 : (page - 1) * pageSize + 1
  const end = Math.min(total, page * pageSize)

  return (
    <div className="h-14 bg-surfaceContainer border border-outlineVariant rounded-md flex items-center justify-end px-3 gap-3" role="navigation" aria-label="Pagination" data-testid="pagination-root">
      <div className="flex items-center gap-2">
        <label className="text-sm text-slate-600">Rows per page</label>
        <select className="h-9 px-2 rounded-md bg-white border border-outlineVariant focus-ring" value={pageSize} onChange={(e) => onPageSizeChange(Number(e.target.value))} data-testid="rows-per-page">
          <option value={10}>10</option>
          <option value={25}>25</option>
          <option value={50}>50</option>
        </select>
      </div>
      <div className="text-sm text-slate-600">{start}–{end} of {total}</div>
      <div className="flex items-center gap-1">
        <button
          className="h-9 w-9 inline-flex items-center justify-center rounded-md border border-outlineVariant hover:bg-surfaceContainerHigh focus-ring disabled:opacity-50"
          onClick={() => onPageChange(Math.max(1, page - 1))}
          disabled={page <= 1}
          aria-label="Previous page"
          data-testid="btn-prev"
        >
          <ChevronLeft size={18} />
        </button>
        <button
          className="h-9 w-9 inline-flex items-center justify-center rounded-md border border-outlineVariant hover:bg-surfaceContainerHigh focus-ring disabled:opacity-50"
          onClick={() => onPageChange(Math.min(totalPages, page + 1))}
          disabled={page >= totalPages}
          aria-label="Next page"
          data-testid="btn-next"
        >
          <ChevronRight size={18} />
        </button>
      </div>
    </div>
  )
}


