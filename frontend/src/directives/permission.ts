import type { Directive } from 'vue'
import { hasPermission } from '@/utils/permission'

export const permission: Directive<HTMLElement, string | string[]> = {
  mounted(el, binding) {
    const value = binding.value
    const permissions = Array.isArray(value) ? value : [value]
    if (!permissions.some((p) => hasPermission(p))) {
      el.remove()
    }
  },
}
