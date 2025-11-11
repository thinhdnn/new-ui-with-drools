'use client'

import { useState, useRef, useEffect } from 'react'
import { useRouter } from 'next/navigation'
import { ChevronDown, Clock, Check } from 'lucide-react'

export type RuleVersion = {
  id: number
  version: number
  isLatest: boolean
  versionNotes: string | null
  updatedAt: string
  updatedBy: string | null
}

type Props = {
  currentVersion: RuleVersion
  versions: RuleVersion[]
  onVersionChange?: (versionId: number) => void
}

export function VersionDropdown({ currentVersion, versions, onVersionChange }: Props) {
  const [isOpen, setIsOpen] = useState(false)
  const dropdownRef = useRef<HTMLDivElement>(null)
  const router = useRouter()

  // Close dropdown when clicking outside
  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target as Node)) {
        setIsOpen(false)
      }
    }

    if (isOpen) {
      document.addEventListener('mousedown', handleClickOutside)
    }

    return () => {
      document.removeEventListener('mousedown', handleClickOutside)
    }
  }, [isOpen])

  const handleVersionSelect = (versionId: number) => {
    setIsOpen(false)
    if (onVersionChange) {
      onVersionChange(versionId)
    } else {
      router.push(`/rules/${versionId}`)
    }
  }

  const formatDate = (dateString: string) => {
    const date = new Date(dateString)
    const now = new Date()
    const diffMs = now.getTime() - date.getTime()
    const diffMins = Math.floor(diffMs / 60000)
    const diffHours = Math.floor(diffMs / 3600000)
    const diffDays = Math.floor(diffMs / 86400000)

    if (diffMins < 60) return `${diffMins}m ago`
    if (diffHours < 24) return `${diffHours}h ago`
    if (diffDays < 7) return `${diffDays}d ago`
    
    return date.toLocaleDateString('en-US', { 
      month: 'short', 
      day: 'numeric',
      year: date.getFullYear() !== now.getFullYear() ? 'numeric' : undefined
    })
  }

  return (
    <div className="relative" ref={dropdownRef}>
      <button
        onClick={() => setIsOpen(!isOpen)}
        className="flex items-center gap-2 px-3 py-1.5 bg-blue-50 text-blue-700 rounded-md hover:bg-blue-100 transition-colors focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-1"
      >
        <Clock className="w-4 h-4" />
        <span className="text-sm font-medium">Version {currentVersion.version}</span>
        <ChevronDown className={`w-4 h-4 transition-transform ${isOpen ? 'rotate-180' : ''}`} />
      </button>

      {isOpen && (
        <div className="absolute top-full left-0 mt-1 bg-white border border-slate-200 rounded-md shadow-lg w-96 z-50 max-h-96 overflow-hidden flex flex-col">
          <div className="px-3 py-2 border-b bg-slate-50">
            <p className="text-xs font-semibold text-slate-600 uppercase tracking-wide">
              Version History
            </p>
          </div>
          
          <div className="overflow-y-auto flex-1">
            {versions.map((v) => (
              <button
                key={v.id}
                onClick={() => handleVersionSelect(v.id)}
                className={`w-full text-left px-3 py-2.5 hover:bg-slate-50 border-b border-slate-100 transition-colors ${
                  v.id === currentVersion.id ? 'bg-blue-50' : ''
                }`}
              >
                <div className="flex justify-between items-start gap-2">
                  <div className="flex-1 min-w-0">
                    <div className="flex items-center gap-2">
                      <p className="text-sm font-medium text-slate-900">
                        Version {v.version}
                      </p>
                      {v.isLatest && (
                        <span className="px-2 py-0.5 bg-green-100 text-green-700 text-xs font-medium rounded">
                          Latest
                        </span>
                      )}
                      {v.id === currentVersion.id && (
                        <Check className="w-4 h-4 text-blue-600" />
                      )}
                    </div>
                    
                    {v.versionNotes && (
                      <p className="text-xs text-slate-600 mt-1 line-clamp-2">
                        {v.versionNotes}
                      </p>
                    )}
                    
                    {v.updatedBy && (
                      <p className="text-xs text-slate-400 mt-1">
                        by {v.updatedBy}
                      </p>
                    )}
                  </div>
                  
                  <p className="text-xs text-slate-400 whitespace-nowrap">
                    {formatDate(v.updatedAt)}
                  </p>
                </div>
              </button>
            ))}
          </div>
        </div>
      )}
    </div>
  )
}

