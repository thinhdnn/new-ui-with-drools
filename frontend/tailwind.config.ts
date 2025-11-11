import type { Config } from 'tailwindcss'

export default {
  content: [
    './app/**/*.{ts,tsx}',
    './components/**/*.{ts,tsx}',
    './styles/**/*.{ts,tsx}',
  ],
  theme: {
    extend: {
      colors: {
        primary: {
          DEFAULT: '#4F46E5',
        },
        surface: 'var(--color-surface)',
        surfaceContainer: 'var(--color-surface-container)',
        surfaceContainerHigh: 'var(--color-surface-container-high)',
        surfaceContainerHighest: 'var(--color-surface-container-highest)',
        outlineVariant: 'var(--color-outline-variant)'
      },
      borderRadius: {
        md: 'var(--radius-md)'
      },
      boxShadow: {
        appbar: '0 1px 2px rgba(0,0,0,0.06), 0 1px 3px rgba(0,0,0,0.1)',
        card: '0 1px 2px rgba(0,0,0,0.06)'
      }
    },
  },
  plugins: [],
} satisfies Config


