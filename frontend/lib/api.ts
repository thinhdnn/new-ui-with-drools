// API configuration - use relative URLs if no backend URL specified (for nginx proxy)
const BACKEND_URL = process.env.NEXT_PUBLIC_BACKEND_URL || ''
const API_BASE = BACKEND_URL ? `${BACKEND_URL}/api/v1` : '/api/v1'

export const api = {
  rules: {
    list: () => `${API_BASE}/rules`,
    get: (id: string | number) => `${API_BASE}/rules/${id}`,
    create: () => `${API_BASE}/rules`,
    update: (id: string | number) => `${API_BASE}/rules/${id}`,
    delete: (id: string | number) => `${API_BASE}/rules/${id}`,
    metadata: (factType?: string) => {
      const url = `${API_BASE}/rules/metadata`
      return factType ? `${url}?factType=${encodeURIComponent(factType)}` : url
    },
    versions: (id: string | number) => `${API_BASE}/rules/${id}/versions`,
    latest: (id: string | number) => `${API_BASE}/rules/${id}/versions/latest`,
    restore: (id: string | number) => `${API_BASE}/rules/${id}/versions/restore`,
    packageInfo: (factType?: string) => {
      const url = `${API_BASE}/rules/package/info`
      return factType ? `${url}?factType=${encodeURIComponent(factType)}` : url
    },
    deploy: (factType?: string) => {
      const url = `${API_BASE}/rules/deploy`
      return factType ? `${url}?factType=${encodeURIComponent(factType)}` : url
    },
    execute: () => `${API_BASE}/rules/execute`,
    factTypes: () => `${API_BASE}/rules/fact-types`,
    containersStatus: () => `${API_BASE}/rules/containers/status`,
    containerStatus: (factType: string) => `${API_BASE}/rules/containers/status/${encodeURIComponent(factType)}`,
  },
  changeRequests: {
    list: (factType?: string, status?: string) => {
      const url = `${API_BASE}/change-requests`
      const params = new URLSearchParams()
      if (factType) params.append('factType', factType)
      if (status) params.append('status', status)
      return params.toString() ? `${url}?${params.toString()}` : url
    },
    get: (id: string | number) => `${API_BASE}/change-requests/${id}`,
    create: () => `${API_BASE}/change-requests`,
    approve: (id: string | number) => `${API_BASE}/change-requests/${id}/approve`,
    reject: (id: string | number) => `${API_BASE}/change-requests/${id}/reject`,
    factTypes: () => `${API_BASE}/change-requests/fact-types`,
  },
}

export async function fetchApi<T>(
  url: string,
  options?: RequestInit
): Promise<T> {
  const response = await fetch(url, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...options?.headers,
    },
  })

  if (!response.ok) {
    const errorText = await response.text()
    let errorMessage = errorText
    try {
      const errorJson = JSON.parse(errorText)
      errorMessage = errorJson.error || errorJson.message || errorText
    } catch {
      // Keep original errorText if not JSON
    }
    throw new Error(errorMessage || `HTTP ${response.status}`)
  }

  return response.json()
}

