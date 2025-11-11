import './globals.css'
import type { ReactNode } from 'react'
import { Providers } from '@/components/Providers'
import { Sidebar } from '@/components/Sidebar'
import { TopBar } from '@/components/TopBar'

export const metadata = {
  title: 'Rule Management',
  description: 'Rule Management Studio',
}

export default function RootLayout({ children }: { children: ReactNode }) {
  return (
    <html lang="en">
      <body className="antialiased">
        <Providers>
          <div className="flex min-h-screen">
            {/* Permanent on >=1280px; hidden on <768; modal sheet handled inside Sidebar */}
            <Sidebar />
            <div className="flex-1 flex flex-col min-w-0">
              <TopBar />
              <main className="p-4 md:p-6 lg:p-8">
                {children}
              </main>
            </div>
          </div>
        </Providers>
      </body>
    </html>
  )
}


