import './style.css'

type BridgeStatus = 'idle' | 'ok' | 'error'

declare global {
  interface Window {
    AndroidPrintBridge?: {
      isAvailable?: () => boolean
      connectBrotherPrinter?: () => Promise<string> | string
      printBrotherLabelJson?: (payload: string) => Promise<string> | string
      printBrotherLabel?: (payload: string) => Promise<string> | string
    }
  }
}

const MIDPACK_URL = 'https://www.happykitchens.io/midpack2'

const app = document.querySelector<HTMLDivElement>('#app')!

app.innerHTML = `
  <div class="shell">
    <div class="card">
      <div class="eyebrow">Midpack 2 Android Wrapper</div>
      <h1>Android shell scaffolded</h1>
      <p class="sub">This web bundle only exists as a Capacitor placeholder. The real runtime loads the live <code>/midpack2</code> page inside the native Android WebView and exposes a Brother print bridge.</p>
      <div class="actions">
        <a class="button primary" href="${MIDPACK_URL}" target="_blank" rel="noreferrer">Open live Midpack 2</a>
      </div>
      <div class="status" id="status">Checking bridge...</div>
    </div>
  </div>
`

const statusEl = document.querySelector<HTMLDivElement>('#status')!

function setStatus(status: BridgeStatus, text: string) {
  statusEl.dataset.state = status
  statusEl.textContent = text
}

const bridge = window.AndroidPrintBridge
if (bridge) {
  setStatus('ok', 'Android print bridge object detected in web context.')
} else {
  setStatus('idle', 'No Android bridge in this browser preview. Expected on device only.')
}
