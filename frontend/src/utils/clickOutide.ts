type Action = (
  node: HTMLElement,
  parameters: any
) => {
  update?: (parameters: any) => void
  destroy?: () => void
}

const isHamburger = (el?: HTMLElement) => {
  return el?.tagName === 'SPAN' || el?.id === 'hamburger'
}

export function clickOutside(
  node: HTMLElement,
  params: { enabled: boolean; cb: Function }
): ReturnType<Action> {
  const { enabled: initialEnabled, cb } = params

  const handleOutsideClick = (el: MouseEvent) => {
    if (!isHamburger(el.target as HTMLElement) && !node.contains(el.target as Node)) cb() // typescript hack, not sure how to solve without asserting as Node
  }

  function update({ enabled }: { enabled: boolean }) {
    if (enabled) {
      document.addEventListener('mousedown', handleOutsideClick)
      document.addEventListener('touchstart', handleOutsideClick)
    } else {
      document.removeEventListener('mousedown', handleOutsideClick)
      document.removeEventListener('touchstart', handleOutsideClick)
    }
  }
  update({ enabled: initialEnabled })
  return {
    update,
    destroy() {
      document.removeEventListener('mousedown', handleOutsideClick)
      document.removeEventListener('touchstart', handleOutsideClick)
    },
  }
}
