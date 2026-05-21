// js/pages/exams.js

import { api } from '../api.js';
import { ui } from '../ui.js';
import { realtime } from '../realtime.js';

let allExams = [];

export const initExamPage = () => {
  // Bind add button
  document.getElementById('btn-add-exam').addEventListener('click', () => openExamModal(null));
  
  // Bind filters & search
  document.getElementById('search-input').addEventListener('input', applyFilters);
  document.getElementById('filter-status').addEventListener('change', applyFilters);

  // Load initial data and start real-time updates (every 5 seconds)
  loadExams();
  realtime.start('exams-polling', loadExams, 5000);
};

// Main fetch function
async function loadExams() {
  try {
    const data = await api.get('/exams');
    allExams = data || [];
    applyFilters();
    updateStatistics();
  } catch (err) {
    ui.toast(err.message || 'Failed to sync examinations catalog.', 'error');
  }
}

// Stats generator
function updateStatistics() {
  const totalEl = document.getElementById('stat-total');
  const activeEl = document.getElementById('stat-active');
  const scheduledEl = document.getElementById('stat-scheduled');
  const cancelledEl = document.getElementById('stat-cancelled');

  let total = allExams.length;
  let active = 0;
  let scheduled = 0;
  let cancelled = 0;

  allExams.forEach(e => {
    if (e.status === 'ACTIVE') active++;
    else if (e.status === 'SCHEDULED') scheduled++;
    else if (e.status === 'CANCELLED') cancelled++;
  });

  totalEl.textContent = total;
  activeEl.textContent = active;
  scheduledEl.textContent = scheduled;
  cancelledEl.textContent = cancelled;
}

// Search and filter mapping
function applyFilters() {
  const searchVal = document.getElementById('search-input').value.toLowerCase();
  const statusVal = document.getElementById('filter-status').value;
  const tbody = document.getElementById('exam-table-body');

  let filtered = allExams.filter(e => {
    const title = (e.title || '').toLowerCase();
    const passcode = (e.passcode || '').toLowerCase();
    const id = e.id ? e.id.toString() : '';
    return title.includes(searchVal) || passcode.includes(searchVal) || id.includes(searchVal);
  });

  if (statusVal !== 'ALL') {
    filtered = filtered.filter(e => e.status === statusVal);
  }

  tbody.innerHTML = '';

  if (filtered.length === 0) {
    tbody.innerHTML = `
      <tr>
        <td colspan="8" class="px-6 py-8 text-center text-gray-500">
          No examinations scheduled matching your criteria.
        </td>
      </tr>
    `;
    return;
  }

  filtered.forEach(e => {
    const tr = document.createElement('tr');
    tr.className = 'hover:bg-slate-50 transition-colors group';

    // Status styling
    let statusClass = 'bg-gray-100 text-gray-700 border-gray-200';
    if (e.status === 'ACTIVE') statusClass = 'bg-green-50 text-green-700 border-green-200 animate-pulse';
    else if (e.status === 'SCHEDULED') statusClass = 'bg-blue-50 text-blue-700 border-blue-200';
    else if (e.status === 'CANCELLED') statusClass = 'bg-red-50 text-red-700 border-red-200';
    else if (e.status === 'COMPLETED') statusClass = 'bg-slate-100 text-slate-800 border-slate-300';

    // Format times
    const startStr = e.startTime ? new Date(e.startTime).toLocaleString('en-US', { month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' }) : 'N/A';
    const endStr = e.endTime ? new Date(e.endTime).toLocaleString('en-US', { month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' }) : 'N/A';

    // Action button render based on status
    let actionButtons = '';
    
    if (e.status === 'SCHEDULED' || e.status === 'ACTIVE') {
      actionButtons += `
        <button class="cancel-btn w-8 h-8 flex items-center justify-center text-gray-400 hover:text-amber-600 hover:bg-amber-50 rounded-full transition-colors"
                data-id="${e.id}" data-title="${e.title}" title="Cancel Exam">
          <span class="material-symbols-outlined text-[18px]">block</span>
        </button>
      `;
    }

    actionButtons += `
      <button class="edit-btn w-8 h-8 flex items-center justify-center text-gray-400 hover:text-blue-600 hover:bg-blue-50 rounded-full transition-colors"
              data-id="${e.id}" title="Edit Exam Details">
        <span class="material-symbols-outlined text-[18px]">edit</span>
      </button>
      <button class="delete-btn w-8 h-8 flex items-center justify-center text-gray-400 hover:text-red-600 hover:bg-red-50 rounded-full transition-colors"
              data-id="${e.id}" data-title="${e.title}" title="Delete Exam">
        <span class="material-symbols-outlined text-[18px]">delete</span>
      </button>
    `;

    tr.innerHTML = `
      <td class="px-6 py-4 font-semibold text-slate-700">#EXM-${e.id}</td>
      <td class="px-6 py-4 font-bold text-slate-900">${e.title}</td>
      <td class="px-6 py-4 font-semibold text-slate-700">${e.durationInMinutes} mins</td>
      <td class="px-6 py-4 text-xs font-mono text-gray-600">${startStr}</td>
      <td class="px-6 py-4 text-xs font-mono text-gray-600">${endStr}</td>
      <td class="px-6 py-4"><span class="font-mono bg-slate-100 border border-slate-200 px-2 py-0.5 rounded text-xs select-all">${e.passcode}</span></td>
      <td class="px-6 py-4">
        <span class="px-2.5 py-1 border rounded-full text-xs font-bold ${statusClass}">
          ${e.status}
        </span>
      </td>
      <td class="px-6 py-4 text-right">
        <div class="flex items-center justify-end gap-1">
          ${actionButtons}
        </div>
      </td>
    `;
    tbody.appendChild(tr);
  });

  // Re-bind click event listeners
  tbody.querySelectorAll('.cancel-btn').forEach(btn => {
    btn.addEventListener('click', () => confirmCancelExam(btn.dataset.id, btn.dataset.title));
  });

  tbody.querySelectorAll('.edit-btn').forEach(btn => {
    btn.addEventListener('click', () => openExamModal(Number(btn.dataset.id)));
  });

  tbody.querySelectorAll('.delete-btn').forEach(btn => {
    btn.addEventListener('click', () => confirmDeleteExam(btn.dataset.id, btn.dataset.title));
  });
}

// Truncates ISO datetime (YYYY-MM-DDTHH:MM:SS) to fit datetime-local inputs (YYYY-MM-DDTHH:MM)
function truncateDateTimeForInput(dateTimeStr) {
  if (!dateTimeStr) return '';
  return dateTimeStr.substring(0, 16);
}

// Modals: Add / Edit Exam
function openExamModal(examId = null) {
  const isEdit = examId !== null;
  const exam = isEdit ? allExams.find(e => Number(e.id) === Number(examId)) : {
    title: '',
    description: '',
    durationInMinutes: 60,
    startTime: '',
    endTime: '',
    passcode: Math.random().toString(36).substring(2, 8).toUpperCase(),
    status: 'SCHEDULED'
  };

  const formattedStart = truncateDateTimeForInput(exam.startTime);
  const formattedEnd = truncateDateTimeForInput(exam.endTime);

  let statusOptions = '';
  if (isEdit) {
    statusOptions = `
      <div>
        <label class="block text-xs font-bold text-slate-700 uppercase mb-1">State Status</label>
        <select id="examStatus" required class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none text-sm bg-white shadow-sm">
          <option value="SCHEDULED" ${exam.status === 'SCHEDULED' ? 'selected' : ''}>Scheduled</option>
          <option value="ACTIVE" ${exam.status === 'ACTIVE' ? 'selected' : ''}>Active / Live</option>
          <option value="COMPLETED" ${exam.status === 'COMPLETED' ? 'selected' : ''}>Completed</option>
          <option value="CANCELLED" ${exam.status === 'CANCELLED' ? 'selected' : ''}>Cancelled</option>
        </select>
      </div>
    `;
  }

  const modalHTML = `
    <form id="exam-form" class="space-y-4">
      <div>
        <label class="block text-xs font-bold text-slate-700 uppercase mb-1">Exam Title</label>
        <input type="text" id="examTitle" required placeholder="e.g. Object Oriented Programming - Mid Semester" value="${exam.title}"
               class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none text-sm font-medium shadow-sm transition-shadow">
      </div>
      <div>
        <label class="block text-xs font-bold text-slate-700 uppercase mb-1">Instructions / Description</label>
        <textarea id="examDesc" placeholder="Rules, syllabus contents, grading information..." 
                  class="w-full min-h-[80px] px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none text-sm shadow-sm transition-shadow resize-none">${exam.description || ''}</textarea>
      </div>
      <div class="grid grid-cols-2 gap-3">
        <div>
          <label class="block text-xs font-bold text-slate-700 uppercase mb-1">Duration (Minutes)</label>
          <input type="number" id="examDuration" required min="5" value="${exam.durationInMinutes}"
                 class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none text-sm font-medium shadow-sm">
        </div>
        <div>
          <label class="block text-xs font-bold text-slate-700 uppercase mb-1">Access Passcode</label>
          <input type="text" id="examPasscode" required placeholder="6-digit Lock" value="${exam.passcode}"
                 class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none text-sm font-mono font-bold shadow-sm">
        </div>
      </div>
      <div class="grid grid-cols-2 gap-3">
        <div>
          <label class="block text-xs font-bold text-slate-700 uppercase mb-1">Start Datetime</label>
          <input type="datetime-local" id="examStart" required value="${formattedStart}"
                 class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none text-sm font-medium shadow-sm">
        </div>
        <div>
          <label class="block text-xs font-bold text-slate-700 uppercase mb-1">End Datetime</label>
          <input type="datetime-local" id="examEnd" required value="${formattedEnd}"
                 class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none text-sm font-medium shadow-sm">
        </div>
      </div>
      ${statusOptions}
    </form>
  `;

  ui.showModal({
    title: isEdit ? 'Modify Scheduled Examination' : 'Schedule New Academic Examination',
    html: modalHTML,
    confirmText: isEdit ? 'Save Changes' : 'Schedule Exam',
    confirmClass: 'bg-blue-600 hover:bg-blue-700 text-white font-bold',
    onConfirm: async () => {
      const title = document.getElementById('examTitle').value.trim();
      const description = document.getElementById('examDesc').value.trim();
      const durationInMinutes = parseInt(document.getElementById('examDuration').value, 10);
      const passcode = document.getElementById('examPasscode').value.trim();
      
      let startVal = document.getElementById('examStart').value;
      let endVal = document.getElementById('examEnd').value;

      if (!title || !durationInMinutes || !passcode || !startVal || !endVal) {
        ui.toast('Please fill in all mandatory fields.', 'error');
        return false;
      }

      // Convert datetime-local YYYY-MM-DDTHH:MM to YYYY-MM-DDTHH:MM:00 for Spring Boot LocalDateTime mapping
      if (startVal.length === 16) startVal += ':00';
      if (endVal.length === 16) endVal += ':00';

      const startTime = new Date(startVal);
      const endTime = new Date(endVal);

      if (endTime <= startTime) {
        ui.toast('The end datetime must occur after the start datetime.', 'error');
        return false;
      }

      const payload = {
        title,
        description,
        durationInMinutes,
        passcode,
        startTime: startVal,
        endTime: endVal,
        status: isEdit ? document.getElementById('examStatus').value : 'SCHEDULED'
      };

      ui.showLoading();
      try {
        if (isEdit) {
          await api.put(`/exams/${examId}`, payload);
          ui.toast('Examination modified successfully.', 'success');
        } else {
          await api.post('/exams/schedule', payload);
          ui.toast('Examination scheduled successfully! Announcement notifications generated.', 'success');
        }
        loadExams();
        return true;
      } catch (err) {
        ui.toast(err.message || 'Failed to dispatch examination configuration.', 'error');
        return false;
      } finally {
        ui.hideLoading();
      }
    }
  });
}

// Action: Cancel Exam
function confirmCancelExam(id, title) {
  ui.showModal({
    title: 'Cancel Examination Session',
    html: `
      <div class="space-y-3">
        <p class="text-sm text-slate-600">Are you sure you want to cancel the examination session <strong class="text-slate-900">${title}</strong>?</p>
        <p class="text-xs text-amber-600 bg-amber-50 border border-amber-100 p-3 rounded-lg flex items-start gap-2">
          <span class="material-symbols-outlined text-[18px] shrink-0">warning</span>
          <span>Students will no longer be permitted to log in or start this attempt. Any active attempts will be frozen.</span>
        </p>
      </div>
    `,
    confirmText: 'Cancel Exam Session',
    confirmClass: 'bg-amber-500 hover:bg-amber-600 text-white font-bold',
    onConfirm: async () => {
      ui.showLoading();
      try {
        await api.put(`/exams/${id}/cancel`);
        ui.toast('Examination session marked as CANCELLED.', 'success');
        loadExams();
        return true;
      } catch (err) {
        ui.toast(err.message || 'Failed to process cancellation.', 'error');
        return false;
      } finally {
        ui.hideLoading();
      }
    }
  });
}

// Action: Delete Exam
function confirmDeleteExam(id, title) {
  ui.showModal({
    title: 'Delete Examination Session',
    html: `
      <div class="space-y-3">
        <p class="text-sm text-slate-600">Are you sure you want to completely delete the examination session <strong class="text-slate-900">${title}</strong>?</p>
        <p class="text-xs text-red-600 bg-red-50 border border-red-100 p-3 rounded-lg flex items-start gap-2">
          <span class="material-symbols-outlined text-[18px] shrink-0">warning</span>
          <span>This will delete all traces of this exam schedule, passcode logs, and attempt data permanently. This is a destructive database modification.</span>
        </p>
      </div>
    `,
    confirmText: 'Delete Permanently',
    confirmClass: 'bg-red-600 hover:bg-red-700 text-white font-bold',
    onConfirm: async () => {
      ui.showLoading();
      try {
        await api.delete(`/exams/${id}`);
        ui.toast('Examination schedule deleted successfully.', 'success');
        loadExams();
        return true;
      } catch (err) {
        ui.toast(err.message || 'Failed to delete scheduled exam.', 'error');
        return false;
      } finally {
        ui.hideLoading();
      }
    }
  });
}
