// js/ui.js

let toastContainer = null;
let loadingOverlay = null;
let modalOverlay = null;
let previousActiveElement = null;

// Toast deduplication cache
let lastErrorText = '';
let lastErrorTime = 0;

// Initialize elements dynamically to prevent lifecycle/ordering dependency bugs
const ensureToastContainer = () => {
  if (toastContainer) return;
  toastContainer = document.getElementById('toast-container');
  if (!toastContainer) {
    toastContainer = document.createElement('div');
    toastContainer.id = 'toast-container';
    toastContainer.className = 'fixed bottom-6 right-6 z-[100] flex flex-col gap-2 pointer-events-none';
    document.body.appendChild(toastContainer);
  }
};

const ensureLoadingOverlay = () => {
  if (loadingOverlay) return;
  loadingOverlay = document.getElementById('loading-overlay');
  if (!loadingOverlay) {
    loadingOverlay = document.createElement('div');
    loadingOverlay.id = 'loading-overlay';
    loadingOverlay.className = 'fixed inset-0 bg-slate-900/40 backdrop-blur-[2px] flex items-center justify-center z-[90] hidden opacity-0 transition-opacity duration-200';
    loadingOverlay.innerHTML = `
      <div class="bg-white p-5 rounded-2xl shadow-xl flex items-center space-x-3">
        <span class="material-symbols-outlined animate-spin text-blue-600 text-3xl">sync</span>
        <span class="text-slate-700 font-semibold text-sm">Processing request...</span>
      </div>
    `;
    document.body.appendChild(loadingOverlay);
  }
};

const ensureModalContainer = () => {
  if (modalOverlay) return;
  modalOverlay = document.getElementById('global-modal');
  if (!modalOverlay) {
    modalOverlay = document.createElement('div');
    modalOverlay.id = 'global-modal';
    modalOverlay.className = 'fixed inset-0 z-50 flex items-center justify-center opacity-0 pointer-events-none transition-opacity duration-300';
    modalOverlay.innerHTML = `
      <div class="absolute inset-0 bg-slate-900/60 backdrop-blur-sm" id="global-modal-backdrop"></div>
      <div class="bg-white rounded-2xl shadow-2xl w-full max-w-lg relative z-10 transform scale-95 transition-transform duration-300 flex flex-col overflow-hidden max-h-[90vh]" id="global-modal-content">
        <!-- Header -->
        <div class="px-6 py-4 border-b flex justify-between items-center bg-gray-50/50 shrink-0">
          <h3 class="text-xl font-bold text-slate-800 flex items-center gap-2" id="global-modal-title">Modal Title</h3>
          <button type="button" class="w-8 h-8 flex items-center justify-center rounded-full text-gray-400 hover:text-gray-600 hover:bg-gray-100 transition-colors" id="global-modal-close">
            <span class="material-symbols-outlined">close</span>
          </button>
        </div>
        <!-- Body -->
        <div class="p-6 overflow-y-auto" id="global-modal-body"></div>
        <!-- Footer -->
        <div class="px-6 py-4 border-t bg-gray-50 flex justify-end gap-3 shrink-0" id="global-modal-footer">
          <button type="button" class="px-5 py-2 text-sm font-semibold text-slate-600 bg-white border border-slate-300 rounded-lg hover:bg-slate-50 transition-colors" id="global-modal-cancel">Cancel</button>
          <button type="button" class="px-5 py-2 text-sm font-bold text-white bg-blue-600 rounded-lg hover:bg-blue-700 transition-all flex items-center gap-2" id="global-modal-confirm">Confirm</button>
        </div>
      </div>
    `;
    document.body.appendChild(modalOverlay);

    // Event listener for ESC key to close modal
    document.addEventListener('keydown', (e) => {
      if (e.key === 'Escape' && !modalOverlay.classList.contains('pointer-events-none')) {
        closeModal();
      }
    });

    // Click outside listener
    document.getElementById('global-modal-backdrop').addEventListener('click', closeModal);
    document.getElementById('global-modal-close').addEventListener('click', closeModal);
    document.getElementById('global-modal-cancel').addEventListener('click', closeModal);
  }
};

// --- Named Toast Function with Deduplication ---
export const showToast = (message, type = 'success', duration = 4000) => {
  if (type === 'error') {
    const now = Date.now();
    // If the exact same error toast is called within 3 seconds, ignore to prevent spamming
    if (message === lastErrorText && (now - lastErrorTime) < 3000) {
      return;
    }
    lastErrorText = message;
    lastErrorTime = now;
  }

  ensureToastContainer();
  const toast = document.createElement('div');
  const colors = type === 'success' ? 'bg-gray-800 text-white' : 'bg-red-600 text-white';
  const icon = type === 'success' ? 'check_circle' : 'error';
  
  toast.className = `flex items-center gap-3 px-4 py-3 rounded-xl shadow-xl border border-gray-700 ${colors} transform translate-y-10 opacity-0 transition-all duration-300 pointer-events-auto`;
  toast.innerHTML = `
    <span class="material-symbols-outlined text-[20px]">${icon}</span>
    <span class="text-sm font-medium">${message}</span>
  `;
  
  toastContainer.appendChild(toast);
  
  // Trigger transition
  requestAnimationFrame(() => {
    toast.classList.remove('translate-y-10', 'opacity-0');
  });

  setTimeout(() => {
    toast.classList.add('translate-y-10', 'opacity-0');
    setTimeout(() => toast.remove(), 300);
  }, duration);
};

// --- Named Loading Spinner Overlay ---
export const showLoading = () => {
  ensureLoadingOverlay();
  loadingOverlay.classList.remove('hidden');
  loadingOverlay.offsetHeight; // Force reflow
  loadingOverlay.classList.remove('opacity-0');
};

export const hideLoading = () => {
  if (!loadingOverlay) return;
  loadingOverlay.classList.add('opacity-0');
  setTimeout(() => {
    loadingOverlay.classList.add('hidden');
  }, 200);
};

// --- Reusable Named Modal Dialog (Accessible) ---
export const showModal = ({
  title = 'Confirm Action',
  html = '',
  confirmText = 'Confirm',
  cancelText = 'Cancel',
  confirmClass = 'bg-blue-600 hover:bg-blue-700 text-white',
  showCancel = true,
  onConfirm = null,
  onCancel = null
}) => {
  ensureModalContainer();
  
  previousActiveElement = document.activeElement;

  const titleEl = document.getElementById('global-modal-title');
  const bodyEl = document.getElementById('global-modal-body');
  const cancelBtn = document.getElementById('global-modal-cancel');
  const confirmBtn = document.getElementById('global-modal-confirm');
  const contentEl = document.getElementById('global-modal-content');

  titleEl.textContent = title;
  bodyEl.innerHTML = html;

  confirmBtn.textContent = confirmText;
  confirmBtn.className = `px-5 py-2 text-sm font-bold rounded-lg transition-all flex items-center gap-2 ${confirmClass}`;
  confirmBtn.onclick = async () => {
    let shouldClose = true;
    if (onConfirm) {
      try {
        const result = await onConfirm(bodyEl);
        if (result === false) shouldClose = false;
      } catch (e) {
        showToast(e.message || 'Action failed', 'error');
        shouldClose = false;
      }
    }
    if (shouldClose) closeModal();
  };

  cancelBtn.textContent = cancelText;
  if (showCancel) {
    cancelBtn.classList.remove('hidden');
    cancelBtn.onclick = () => {
      if (onCancel) onCancel();
      closeModal();
    };
  } else {
    cancelBtn.classList.add('hidden');
  }

  modalOverlay.classList.remove('opacity-0', 'pointer-events-none');
  contentEl.classList.remove('scale-95');
  contentEl.classList.add('scale-100');

  setTimeout(() => {
    const inputs = bodyEl.querySelectorAll('input, select, textarea');
    if (inputs.length > 0) {
      inputs[0].focus();
    } else {
      confirmBtn.focus();
    }
  }, 50);
};

export const closeModal = () => {
  if (!modalOverlay) return;
  const contentEl = document.getElementById('global-modal-content');
  
  modalOverlay.classList.add('opacity-0', 'pointer-events-none');
  contentEl.classList.remove('scale-100');
  contentEl.classList.add('scale-95');

  if (previousActiveElement) {
    previousActiveElement.focus();
    previousActiveElement = null;
  }
};

// Form/Confirmation quick modal wrappers
export const showConfirmModal = (title, message, onConfirm) => {
  showModal({
    title,
    html: `<p class="text-sm text-slate-600 leading-relaxed">${message}</p>`,
    confirmText: 'Confirm',
    cancelText: 'Cancel',
    onConfirm
  });
};

export const showFormModal = (title, description, confirmText, fields, onConfirm) => {
  let fieldsHtml = `<p class="text-xs text-slate-500 mb-4 leading-relaxed">${description}</p><div class="space-y-4">`;
  fields.forEach(field => {
    fieldsHtml += `
      <div>
        <label class="block text-xs font-bold text-slate-500 mb-1.5 uppercase">${field.label}</label>
        <input id="${field.id}" type="${field.type}" placeholder="${field.placeholder || ''}" ${field.required ? 'required' : ''} class="w-full text-sm border border-slate-300 rounded-xl px-4 py-2.5 focus:ring-2 focus:ring-blue-500 outline-none transition-all">
      </div>
    `;
  });
  fieldsHtml += '</div>';

  showModal({
    title,
    html: fieldsHtml,
    confirmText,
    onConfirm: () => {
      const data = {};
      fields.forEach(field => {
        data[field.id] = document.getElementById(field.id).value.trim();
      });
      return onConfirm(data);
    }
  });
};

// Namespace wrapper for backward compatibility
export const ui = {
  toast: showToast,
  showLoading,
  hideLoading,
  showModal,
  closeModal,
  showConfirmModal,
  showFormModal
};
