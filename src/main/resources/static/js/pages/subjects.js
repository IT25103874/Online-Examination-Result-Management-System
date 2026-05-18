// js/pages/subjects.js

import { api } from '../api.js';
import { ui } from '../ui.js';
import { realtime } from '../realtime.js';

let allSubjects = [];
let allCourses = [];
let coursesMap = new Map();

export const initSubjectPage = () => {
  // Bind add button
  document.getElementById('btn-add-subject').addEventListener('click', openCreateSubjectModal);
  
  // Bind search & filters
  document.getElementById('search-input').addEventListener('input', applyFilters);
  document.getElementById('filter-course').addEventListener('change', applyFilters);

  // Initial load and start updates (every 5 seconds)
  loadInitialData();
  realtime.start('subjects-polling', loadInitialData, 5000);
};

// Initial concurrent fetch
async function loadInitialData() {
  try {
    const [coursesData, subjectsData] = await Promise.all([
      api.get('/v1/courses'),
      api.get('/subjects/all')
    ]);

    allCourses = coursesData || [];
    allSubjects = subjectsData || [];

    // Rebuild lookup map
    coursesMap.clear();
    allCourses.forEach(c => {
      coursesMap.set(Number(c.id), c);
    });

    populateCourseFilter();
    applyFilters();
    updateStatistics();
  } catch (err) {
    ui.toast(err.message || 'Failed to sync subjects and courses logs.', 'error');
  }
}

// Populate course filter dropdown
function populateCourseFilter() {
  const filterSelect = document.getElementById('filter-course');
  const currentVal = filterSelect.value;
  
  // Clear and keep first option
  filterSelect.innerHTML = '<option value="ALL">All Academic Courses</option>';
  
  allCourses.forEach(c => {
    const opt = document.createElement('option');
    opt.value = c.id;
    opt.textContent = `${c.title} (${c.courseCode})`;
    filterSelect.appendChild(opt);
  });

  // Restore previous select value if possible
  if (currentVal && Array.from(filterSelect.options).some(o => o.value === currentVal)) {
    filterSelect.value = currentVal;
  }
}

// Stats generator
function updateStatistics() {
  const totalSubjectsEl = document.getElementById('stat-total-subjects');
  const unlinkedSubjectsEl = document.getElementById('stat-unlinked-subjects');
  const topCourseNumEl = document.getElementById('stat-max-subjects');
  const topCourseTitleEl = document.getElementById('stat-max-subjects-course');

  totalSubjectsEl.textContent = allSubjects.length;

  let unlinkedCount = 0;
  const courseCounts = new Map();

  allSubjects.forEach(s => {
    const courseId = Number(s.courseCourseId);
    if (!courseId || !coursesMap.has(courseId)) {
      unlinkedCount++;
    } else {
      courseCounts.set(courseId, (courseCounts.get(courseId) || 0) + 1);
    }
  });

  unlinkedSubjectsEl.textContent = unlinkedCount;

  // Find course with max subjects
  let maxCount = 0;
  let topCourseTitle = 'No active course programs';

  courseCounts.forEach((count, courseId) => {
    if (count > maxCount) {
      maxCount = count;
      const course = coursesMap.get(courseId);
      if (course) {
        topCourseTitle = `${course.title} (${course.courseCode})`;
      }
    }
  });

  topCourseNumEl.textContent = maxCount;
  topCourseTitleEl.textContent = allCourses.length > 0 && maxCount > 0 ? topCourseTitle : 'No active course programs';
}

// Filter and render logic
function applyFilters() {
  const searchVal = document.getElementById('search-input').value.toLowerCase();
  const courseVal = document.getElementById('filter-course').value;
  const tbody = document.getElementById('subject-table-body');

  let filtered = allSubjects.filter(s => {
    const name = (s.name || '').toLowerCase();
    const id = s.subjectId ? s.subjectId.toString() : '';
    const desc = (s.description || '').toLowerCase();
    return name.includes(searchVal) || id.includes(searchVal) || desc.includes(searchVal);
  });

  if (courseVal !== 'ALL') {
    filtered = filtered.filter(s => Number(s.courseCourseId) === Number(courseVal));
  }

  tbody.innerHTML = '';

  if (filtered.length === 0) {
    tbody.innerHTML = `
      <tr>
        <td colspan="4" class="px-6 py-8 text-center text-gray-500">
          No subject modules found matching your criteria.
        </td>
      </tr>
    `;
    return;
  }

  filtered.forEach(s => {
    const tr = document.createElement('tr');
    tr.className = 'hover:bg-slate-50 transition-colors group';

    const courseId = Number(s.courseCourseId);
    let courseText = '<span class="italic text-amber-600 font-semibold bg-amber-50 px-2 py-0.5 rounded border border-amber-100 text-xs">Unlinked</span>';
    
    if (courseId && coursesMap.has(courseId)) {
      const course = coursesMap.get(courseId);
      courseText = `<span class="font-bold text-slate-800">${course.courseCode}</span> - <span class="text-gray-500 text-xs">${course.title}</span>`;
    }

    tr.innerHTML = `
      <td class="px-6 py-4 font-semibold text-slate-700">#SUB-${s.subjectId}</td>
      <td class="px-6 py-4 font-bold text-slate-900">${s.name}</td>
      <td class="px-6 py-4 text-gray-500 max-w-xs truncate" title="${s.description || ''}">
        ${s.description || '<span class="italic text-gray-400 text-xs">No description</span>'}
      </td>
      <td class="px-6 py-4">${courseText}</td>
    `;
    tbody.appendChild(tr);
  });
}

// Add Subject Modal Action
function openCreateSubjectModal() {
  if (allCourses.length === 0) {
    ui.toast('Cannot create a subject module. You must create an academic course program first.', 'warning');
    return;
  }

  let courseOptions = '';
  allCourses.forEach(c => {
    courseOptions += `<option value="${c.id}">${c.title} (${c.courseCode})</option>`;
  });

  const modalHTML = `
    <form id="create-subject-form" class="space-y-4">
      <div class="grid grid-cols-2 gap-3">
        <div>
          <label class="block text-xs font-bold text-slate-700 uppercase mb-1">Subject ID (Integer)</label>
          <input type="number" id="subjectId" required placeholder="e.g. 101" min="1"
                 class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none text-sm font-medium shadow-sm transition-shadow">
        </div>
        <div>
          <label class="block text-xs font-bold text-slate-700 uppercase mb-1">Subject Name</label>
          <input type="text" id="subjectName" required placeholder="e.g. Object Oriented Programming" 
                 class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none text-sm font-medium shadow-sm transition-shadow">
        </div>
      </div>
      <div>
        <label class="block text-xs font-bold text-slate-700 uppercase mb-1">Belongs to Course Program</label>
        <select id="subjectCourseId" required 
                class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none text-sm shadow-sm transition-shadow bg-white">
          ${courseOptions}
        </select>
      </div>
      <div>
        <label class="block text-xs font-bold text-slate-700 uppercase mb-1">Description</label>
        <textarea id="subjectDesc" placeholder="Brief outline of syllabus / module contents..." 
                  class="w-full min-h-[100px] px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none text-sm shadow-sm transition-shadow resize-none"></textarea>
      </div>
    </form>
  `;

  ui.showModal({
    title: 'Add New Subject Module',
    html: modalHTML,
    confirmText: 'Add Subject',
    confirmClass: 'bg-blue-600 hover:bg-blue-700 text-white font-bold',
    onConfirm: async () => {
      const subjectId = document.getElementById('subjectId').value.trim();
      const name = document.getElementById('subjectName').value.trim();
      const courseCourseId = document.getElementById('subjectCourseId').value;
      const description = document.getElementById('subjectDesc').value.trim();

      if (!subjectId || !name || !courseCourseId) {
        ui.toast('Subject ID, name, and course mapping are all required.', 'error');
        return false;
      }

      ui.showLoading();
      try {
        await api.post('/subjects/add', {
          subjectId: parseInt(subjectId, 10),
          name,
          description,
          courseCourseId: parseInt(courseCourseId, 10)
        });
        ui.toast('Subject module registered successfully!', 'success');
        loadInitialData();
        return true;
      } catch (err) {
        ui.toast(err.message || 'Failed to register subject module.', 'error');
        return false;
      } finally {
        ui.hideLoading();
      }
    }
  });
}
