// js/layout.js

export const initLayout = (pageTitle = 'EduAssess Pro Overview') => {
  // 1. Session / Auth Verification
  const userStr = localStorage.getItem('user');
  if (!userStr) {
    window.location.href = '/index.html';
    return null;
  }

  let user = null;
  try {
    user = JSON.parse(userStr);
    const isStudentPage = window.location.pathname.includes('student-dashboard.html');
    
    // Secure administrative pages vs student pages
    if (isStudentPage) {
      if (user.role !== 'STUDENT') {
        alert('Access Denied. Students only.');
        localStorage.removeItem('user');
        window.location.href = '/index.html';
        return null;
      }
      return user;
    } else {
      if (user.role !== 'ADMIN') {
        alert('Access Denied. Admins only.');
        localStorage.removeItem('user');
        window.location.href = '/index.html';
        return null;
      }
    }
  } catch (e) {
    console.error('Invalid user session data', e);
    localStorage.removeItem('user');
    window.location.href = '/index.html';
    return null;
  }

  // Get name initials
  const name = user.name || 'Administrator';
  const nameParts = name.trim().split(/\s+/);
  let initials = 'AD';
  if (nameParts.length >= 2) {
    initials = (nameParts[0][0] + nameParts[1][0]).toUpperCase();
  } else if (nameParts.length === 1 && nameParts[0].length >= 2) {
    initials = nameParts[0].substring(0, 2).toUpperCase();
  }

  // 2. Define Sidebar HTML (100% Visual Source of Truth Match)
  const sidebarHTML = `
    <aside class="fixed h-full left-0 top-0 w-64 flex flex-col bg-white border-r border-slate-200 z-50">
      <div class="flex flex-col h-full p-4 space-y-2">
        <div class="mb-8 px-2 mt-4">
          <span class="text-2xl font-black text-slate-900">EduAssess Pro</span>
        </div>
        
        <!-- Profile Section -->
        <div class="flex items-center space-x-3 px-2 mb-6">
          <div class="w-10 h-10 rounded-full bg-blue-100 flex items-center justify-center text-blue-700 font-bold border border-blue-200">
            ${initials}
          </div>
          <div class="flex flex-col">
            <span class="text-sm font-bold text-gray-900 line-clamp-1">${name}</span>
            <span class="text-xs text-gray-500">Central Command</span>
          </div>
        </div>
        
        <!-- Main Nav Items -->
        <nav class="space-y-1 flex-grow overflow-y-auto pr-1" id="sidebar-nav">
          <a class="flex items-center space-x-3 px-4 py-3 text-slate-700 hover:bg-slate-100 rounded-lg transition-all" href="/pages/admin-dashboard.html">
            <span class="material-symbols-outlined">dashboard</span>
            <span class="text-sm font-semibold">Dashboard</span>
          </a>
          <a class="flex items-center space-x-3 px-4 py-3 text-slate-700 hover:bg-slate-100 rounded-lg transition-all" href="/user-management.html">
            <span class="material-symbols-outlined">group</span>
            <span class="text-sm font-semibold">User Management</span>
          </a>
          <a class="flex items-center space-x-3 px-4 py-3 text-slate-700 hover:bg-slate-100 rounded-lg transition-all" href="/notification-management.html">
            <span class="material-symbols-outlined">campaign</span>
            <span class="text-sm font-semibold">Notifications</span>
          </a>
          <a class="flex items-center space-x-3 px-4 py-3 text-slate-700 hover:bg-slate-100 rounded-lg transition-all" href="/pages/courses.html">
            <span class="material-symbols-outlined">school</span>
            <span class="text-sm font-semibold">Courses</span>
          </a>
          <a class="flex items-center space-x-3 px-4 py-3 text-slate-700 hover:bg-slate-100 rounded-lg transition-all" href="/pages/subjects.html">
            <span class="material-symbols-outlined">menu_book</span>
            <span class="text-sm font-semibold">Subjects</span>
          </a>
          <a class="flex items-center space-x-3 px-4 py-3 text-slate-700 hover:bg-slate-100 rounded-lg transition-all" href="/pages/exams.html">
            <span class="material-symbols-outlined">assignment</span>
            <span class="text-sm font-semibold">Exams</span>
          </a>
          <a class="flex items-center space-x-3 px-4 py-3 text-slate-700 hover:bg-slate-100 rounded-lg transition-all" href="/pages/exam-schedule.html">
            <span class="material-symbols-outlined">schedule</span>
            <span class="text-sm font-semibold">Schedules</span>
          </a>
          <a class="flex items-center space-x-3 px-4 py-3 text-slate-700 hover:bg-slate-100 rounded-lg transition-all" href="/pages/question-bank.html">
            <span class="material-symbols-outlined">database</span>
            <span class="text-sm font-semibold">Question Bank</span>
          </a>
          <a class="flex items-center space-x-3 px-4 py-3 text-slate-700 hover:bg-slate-100 rounded-lg transition-all" href="/pages/question-paper.html">
            <span class="material-symbols-outlined">description</span>
            <span class="text-sm font-semibold">Question Papers</span>
          </a>
          <a class="flex items-center space-x-3 px-4 py-3 text-slate-700 hover:bg-slate-100 rounded-lg transition-all" href="/pages/exam-monitor.html">
            <span class="material-symbols-outlined">visibility</span>
            <span class="text-sm font-semibold">Live Monitoring</span>
          </a>
          <a class="flex items-center space-x-3 px-4 py-3 text-slate-700 hover:bg-slate-100 rounded-lg transition-all" href="/pages/results.html">
            <span class="material-symbols-outlined">analytics</span>
            <span class="text-sm font-semibold">Results Analytics</span>
          </a>
        </nav>
        
        <!-- Footer Nav -->
        <div class="pt-4 border-t border-slate-200 space-y-1 shrink-0">
          <a class="flex items-center space-x-3 px-4 py-3 text-red-600 hover:bg-red-50 rounded-lg transition-all font-bold" href="#" id="layout-logout-btn">
            <span class="material-symbols-outlined text-[20px]">logout</span>
            <span class="text-sm">Logout</span>
          </a>
        </div>
      </div>
    </aside>
  `;

  // 3. Define Header / Navbar HTML (100% Visual Source of Truth Match)
  const headerHTML = `
    <header class="sticky top-0 z-40 bg-white border-b border-gray-200 px-8 py-4 flex justify-between items-center w-full shadow-sm shrink-0">
      <h1 class="text-xl font-semibold text-slate-800" id="layout-header-title">${pageTitle}</h1>
      <div class="flex items-center space-x-4">
        <span class="text-xs font-semibold bg-blue-50 text-blue-700 px-3 py-1 rounded-full border border-blue-100 flex items-center gap-1 shadow-sm">
          <div class="w-1.5 h-1.5 bg-green-500 rounded-full animate-pulse"></div> Live updates
        </span>
        <button id="layout-header-signout" class="text-sm font-bold text-gray-600 hover:text-red-600 transition-colors font-sans">Sign Out</button>
      </div>
    </header>
  `;

  // 4. Perform Injection safely
  // Remove existing injected sidebars to prevent duplicates
  const existingSidebar = document.querySelector('aside');
  if (existingSidebar) existingSidebar.remove();

  document.body.insertAdjacentHTML('afterbegin', sidebarHTML);
  
  const mainEl = document.querySelector('main');
  if (mainEl) {
    const existingHeader = mainEl.querySelector('header');
    if (existingHeader) existingHeader.remove();

    mainEl.insertAdjacentHTML('afterbegin', headerHTML);
  } else {
    console.error('Could not find <main> element for navbar injection.');
  }

  // 5. Highlight Active Sidebar Nav State
  const path = window.location.pathname;
  const navLinks = document.querySelectorAll('#sidebar-nav a');
  navLinks.forEach(link => {
    const href = link.getAttribute('href');
    if (path === href || path.endsWith(href)) {
      link.className = "flex items-center space-x-3 px-4 py-3 bg-blue-600 text-white rounded-lg font-bold shadow-sm transition-all scale-95 duration-75";
    } else {
      link.className = "flex items-center space-x-3 px-4 py-3 text-slate-700 hover:bg-slate-100 rounded-lg transition-all";
    }
  });

  // 6. Bind Event Listeners (Logout Actions)
  const handleLogout = (e) => {
    if (e) e.preventDefault();
    localStorage.removeItem('user');
    window.location.href = '/index.html';
  };

  const logoutBtn = document.getElementById('layout-logout-btn');
  if (logoutBtn) logoutBtn.addEventListener('click', handleLogout);

  const signoutBtn = document.getElementById('layout-header-signout');
  if (signoutBtn) signoutBtn.addEventListener('click', handleLogout);

  return user;
};
