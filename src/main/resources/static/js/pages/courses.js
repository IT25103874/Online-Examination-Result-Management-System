// js/pages/courses.js

import { api } from '../api.js';
import { ui } from '../ui.js';
import { realtime } from '../realtime.js';

let allCourses = [];

export const initCoursePage = () => {
  // Bind buttons
  document.getElementById('btn-add-course').addEventListener('click', openCreateCourseModal);
  
  // Bind filters & search
  document.getElementById('search-input').addEventListener('input', applyFilters);
  document.getElementById('filter-subject-count').addEventListener('change', applyFilters);

  // Load initial data and start real-time updates (every 5 seconds)
  loadCourses();
  realtime.start('courses-polling', loadCourses, 5000);
};

// Main fetch function
async function loadCourses() {
  try {
    const data = await api.get('/v1/courses');
    allCourses = data || [];
    applyFilters();
    updateStatistics();
  } catch (err) {
    ui.toast(err.message || 'Failed to sync course logs.', 'error');
  }
}

// Statistics calculator
function updateStatistics() {
  const totalCoursesEl = document.getElementById('stat-total-courses');
  const maxSubjectsEl = document.getElementById('stat-max-subjects');
  const maxCourseTitleEl = document.getElementById('stat-max-course-title');
  const totalSubjectsEl = document.getElementById('stat-total-subjects');

  totalCoursesEl.textContent = allCourses.length;

  let maxCount = 0;
  let maxCourseTitle = 'No courses registered';
  let totalSubjects = 0;

  allCourses.forEach(c => {
    const subjectCount = c.subjects ? c.subjects.length : 0;
    totalSubjects += subjectCount;
    if (subjectCount > maxCount) {
      maxCount = subjectCount;
      maxCourseTitle = `${c.title} (${c.courseCode})`;
    }
  });

  maxSubjectsEl.textContent = maxCount;
  maxCourseTitleEl.textContent = allCourses.length > 0 ? maxCourseTitle : 'No courses registered';
  totalSubjectsEl.textContent = totalSubjects;
}

// Filter and render logic
function applyFilters() {
  const searchVal = document.getElementById('search-input').value.toLowerCase();
  const filterVal = document.getElementById('filter-subject-count').value;
  const tbody = document.getElementById('course-table-body');

  let filtered = allCourses.filter(c => {
    const code = (c.courseCode || '').toLowerCase();
    const title = (c.title || '').toLowerCase();
    const desc = (c.description || '').toLowerCase();
    return code.includes(searchVal) || title.includes(searchVal) || desc.includes(searchVal);
  });

  if (filterVal === 'HAS_SUBJECTS') {
    filtered = filtered.filter(c => c.subjects && c.subjects.length > 0);
  } else if (filterVal === 'NO_SUBJECTS') {
    filtered = filtered.filter(c => !c.subjects || c.subjects.length === 0);
  }

  tbody.innerHTML = '';

  if (filtered.length === 0) {
    tbody.innerHTML = `
      <tr>
        <td colspan="6" class="px-6 py-8 text-center text-gray-500">
          No courses found matching your criteria.
        </td>
      </tr>
    `;
    return;
  }

  filtered.forEach(c => {
    const tr = document.createElement('tr');
    tr.className = 'hover:bg-slate-50 transition-colors group';

    const subjectCount = c.subjects ? c.subjects.length : 0;
    const badgeColor = subjectCount > 0 ? 'bg-blue-50 text-blue-700 border-blue-100' : 'bg-amber-50 text-amber-700 border-amber-100';

    tr.innerHTML = `
      <td class="px-6 py-4 font-semibold text-slate-700">#CRS-${c.id}</td>
      <td class="px-6 py-4 font-bold text-slate-900">${c.courseCode}</td>
      <td class="px-6 py-4 font-medium">${c.title}</td>
      <td class="px-6 py-4 max-w-xs truncate text-gray-500" title="${c.description || ''}">${c.description || '<span class="italic text-gray-400">No description</span>'}</td>
      <td class="px-6 py-4">
        <span class="px-2.5 py-1 border rounded-full text-xs font-bold ${badgeColor}">
          ${subjectCount} modules
        </span>
      </td>
      <td class="px-6 py-4 text-right">
        <button class="delete-btn w-8 h-8 flex items-center justify-center text-gray-400 hover:text-red-600 hover:bg-red-50 rounded-full border border-transparent hover:border-red-100 transition-colors" 
                data-id="${c.id}" data-title="${c.title}" title="Delete Program">
          <span class="material-symbols-outlined text-[18px]">delete</span>
        </button>
      </td>
    `;
    tbody.appendChild(tr);
  });

  // Re-bind actions
  tbody.querySelectorAll('.delete-btn').forEach(btn => {
    btn.addEventListener('click', () => {
      confirmDeleteCourse(btn.dataset.id, btn.dataset.title);
    });
  });
}

// Add course action
function openCreateCourseModal() {
  const modalHTML = `
    <form id="create-course-form" class="space-y-4">
      <div>
        <label class="block text-xs font-bold text-slate-700 uppercase mb-1">Course Code</label>
        <input type="text" id="courseCode" required placeholder="e.g. CS101" 
               class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none text-sm font-medium shadow-sm transition-shadow">
      </div>
      <div>
        <label class="block text-xs font-bold text-slate-700 uppercase mb-1">Course Title</label>
        <input type="text" id="courseTitle" required placeholder="e.g. Bachelor of Science in Information Technology" 
               class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none text-sm font-medium shadow-sm transition-shadow">
      </div>
      <div>
        <label class="block text-xs font-bold text-slate-700 uppercase mb-1">Description</label>
        <textarea id="courseDesc" placeholder="Brief summary of program goals and modules..." 
                  class="w-full min-h-[100px] px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none text-sm shadow-sm transition-shadow resize-none"></textarea>
      </div>
    </form>
  `;

  ui.showModal({
    title: 'Create New Academic Program',
    html: modalHTML,
    confirmText: 'Create Course',
    confirmClass: 'bg-blue-600 hover:bg-blue-700 text-white font-bold',
    onConfirm: async () => {
      const code = document.getElementById('courseCode').value.trim();
      const title = document.getElementById('courseTitle').value.trim();
      const description = document.getElementById('courseDesc').value.trim();

      if (!code || !title) {
        ui.toast('Code and title are required fields.', 'error');
        return false; // keeps modal open
      }

      ui.showLoading();
      try {
        await api.post('/v1/courses', { courseCode: code, title, description });
        ui.toast('Course created successfully!', 'success');
        loadCourses();
        return true;
      } catch (err) {
        ui.toast(err.message || 'Failed to submit new course.', 'error');
        return false;
      } finally {
        ui.hideLoading();
      }
    }
  });
}

// Delete course action
function confirmDeleteCourse(id, title) {
  ui.showModal({
    title: 'Delete Academic Program',
    html: `
      <div class="space-y-3">
        <p class="text-sm text-slate-600">Are you sure you want to delete the course <strong class="text-slate-900">${title}</strong>?</p>
        <p class="text-xs text-red-600 bg-red-50 border border-red-100 p-3 rounded-lg flex items-start gap-2">
          <span class="material-symbols-outlined text-[18px] shrink-0">warning</span>
          <span><strong>WARNING:</strong> This will delete the course and all associated subjects and exams permanently from the database. This action is irreversible.</span>
        </p>
      </div>
    `,
    confirmText: 'Delete Permanently',
    confirmClass: 'bg-red-600 hover:bg-red-700 text-white font-bold',
    onConfirm: async () => {
      ui.showLoading();
      try {
        await api.delete(`/v1/courses/${id}`);
        ui.toast('Course deleted successfully.', 'success');
        loadCourses();
        return true;
      } catch (err) {
        ui.toast(err.message || 'Failed to delete course.', 'error');
        return false;
      } finally {
        ui.hideLoading();
      }
    }
  });
}
