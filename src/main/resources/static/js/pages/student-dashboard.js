import { request } from '../api.js';
import { showToast, showLoading, hideLoading, showConfirmModal } from '../ui.js';
import { realtime } from '../realtime.js';

let currentUser = null;
let activeExamsList = [];
let upcomingExamsList = [];
let resultsList = [];
let notificationsList = [];
let countdownIntervals = [];

// Initialize Student Dashboard Core
export function initStudentDashboard() {
    const userStr = localStorage.getItem('user');
    if (!userStr) {
        window.location.href = '/index.html';
        return;
    }
    currentUser = JSON.parse(userStr);

    // Initial sync
    syncDashboardData();

    // Setup tab nav transitions
    setupSectionTransitions();

    // Start visibility-aware polling loops
    realtime.start('student-dashboard', async () => {
        await syncDashboardData();
    }, 5000);

    // Bind profile update submission
    const profileForm = document.getElementById('studentUpdateProfileForm');
    if (profileForm) {
        profileForm.addEventListener('submit', handleProfileUpdate);
    }

    // Bind lobby passcode submission
    const passcodeForm = document.getElementById('examLobbyPasscodeForm');
    if (passcodeForm) {
        passcodeForm.addEventListener('submit', submitExamPasscode);
    }

    // Bind inbox global action buttons
    const markAllBtn = document.getElementById('btn-mark-all-read');
    if (markAllBtn) {
        markAllBtn.addEventListener('click', markAllNotifsRead);
    }
}

// Synchronize all dashboard lists
async function syncDashboardData() {
    await fetchStudentProfile();
    await fetchExams();
    await fetchResults();
    await fetchNotifications();
}

// Switch dashboard view sections cleanly
function switchSection(sectionId, btnElement) {
    // Hide all view panels
    document.querySelectorAll('.section-container').forEach(el => {
        el.classList.remove('section-active');
        el.classList.add('section-hidden');
    });

    // Show selected panel
    const target = document.getElementById(`section-${sectionId}`);
    if (target) {
        target.classList.remove('section-hidden');
        target.classList.add('section-active');
    }

    // Update layout title text in top navbar
    const headerTitle = document.getElementById('layout-header-title');
    if (headerTitle) {
        if (sectionId === 'dashboard') headerTitle.textContent = 'Student Hub Overview';
        else if (sectionId === 'profile') headerTitle.textContent = 'Personal Profiles & Settings';
        else if (sectionId === 'notifications') headerTitle.textContent = 'Academic Bulletins & Inbox';
    }

    // Update active highlight classes on sidebar buttons
    if (btnElement) {
        document.querySelectorAll('.sidebar-btn').forEach(btn => {
            btn.className = 'sidebar-btn w-full flex items-center gap-3 p-3 rounded-lg hover:bg-gray-100 text-gray-700 font-medium transition-all';
        });
        btnElement.className = 'sidebar-btn w-full flex items-center gap-3 p-3 rounded-lg bg-blue-600 text-white font-bold transition-all shadow-sm';
    }
}
window.switchSection = switchSection;

// Fetch / Validate Student Profiles
async function fetchStudentProfile() {
    try {
        const uid = currentUser.id || currentUser.userId || currentUser.user_id;
        const rollNo = `#EDU-${uid}`;
        const nameParts = currentUser.name.trim().split(/\s+/);
        const firstName = nameParts[0];
        const initials = nameParts.length >= 2 ? nameParts[0][0] + nameParts[1][0] : nameParts[0].substring(0, 2);

        // Update home headers and stats widgets
        document.getElementById('welcome-student-title').textContent = `Welcome Back, ${firstName}!`;
        document.getElementById('widget-student-name').textContent = currentUser.name;
        document.getElementById('widget-student-id').textContent = rollNo;
        document.getElementById('widget-avatar-initials').textContent = initials.toUpperCase();

        // Update profile form fields
        document.getElementById('profile-student-name').textContent = currentUser.name;
        document.getElementById('profile-student-id').textContent = rollNo;
        document.getElementById('profile-avatar-initials').textContent = initials.toUpperCase();
        document.getElementById('profile-edit-name').value = currentUser.name;
        document.getElementById('profile-edit-phone').value = currentUser.phone || '';
        document.getElementById('profile-read-email').textContent = currentUser.email;
        document.getElementById('profile-read-dob').textContent = currentUser.dateOfBirth || 'N/A';
        document.getElementById('profile-read-course').textContent = currentUser.courseId || 'General Program';
    } catch (e) {
        console.error('Error fetching student profile:', e);
    }
}

// Fetch both active and scheduled upcoming exams
async function fetchExams() {
    try {
        let exams = [];
        try {
            // Stage 1: Try dedicated student exams endpoint
            exams = await request(`/student/exams`);
        } catch (err) {
            // Stage 2: Fallback to general exams list
            exams = await request(`/exams`);
        }

        if (Array.isArray(exams)) {
            // Filter categories
            activeExamsList = exams.filter(e => e.status === 'ACTIVE');
            upcomingExamsList = exams.filter(e => e.status === 'SCHEDULED');

            renderActiveExams();
            renderUpcomingExams();
        }
    } catch (e) {
        console.error('Error fetching exam lists:', e);
    }
}

// Render active live exams cards
function renderActiveExams() {
    const container = document.getElementById('active-exams-list-container');
    const badge = document.getElementById('active-exams-count-badge');
    if (!container) return;

    badge.textContent = `${activeExamsList.length} Live`;
    if (activeExamsList.length === 0) {
        container.innerHTML = `
            <div class="py-8 text-center text-slate-400">
                <span class="material-symbols-outlined text-[36px] block mb-1 text-slate-300">notifications_paused</span>
                No examinations are currently active.
            </div>
        `;
        return;
    }

    container.innerHTML = '';
    activeExamsList.forEach(exam => {
        const div = document.createElement('div');
        div.className = 'py-4 flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4 group';
        div.innerHTML = `
            <div>
                <h4 class="font-bold text-slate-800 text-sm group-hover:text-blue-600 transition-colors">${exam.title}</h4>
                <p class="text-xs text-slate-500 mt-0.5">${exam.description || 'General program online assessment'}</p>
                <div class="flex items-center gap-2 mt-2">
                    <span class="px-2 py-0.5 bg-green-50 text-green-700 border border-green-200 rounded text-[10px] font-bold">ACTIVE NOW</span>
                    <span class="text-[10px] text-slate-400 font-medium">Duration: ${exam.durationInMinutes} mins</span>
                </div>
            </div>
            <button class="join-exam-btn bg-green-600 hover:bg-green-700 text-white font-bold px-4 py-2 rounded-xl text-xs shadow-sm transition-all flex items-center gap-1 shrink-0" data-id="${exam.id}" data-title="${exam.title}" data-duration="${exam.durationInMinutes}">
                <span class="material-symbols-outlined text-[16px]">key</span>
                Unlock Lobby
            </button>
        `;
        container.appendChild(div);
    });

    // Attach Event Listeners
    container.querySelectorAll('.join-exam-btn').forEach(btn => {
        btn.addEventListener('click', () => {
            const id = btn.getAttribute('data-id');
            const title = btn.getAttribute('data-title');
            const duration = btn.getAttribute('data-duration');
            openExamLobby(id, title, duration);
        });
    });
}

// Render scheduled exams countdown
function renderUpcomingExams() {
    const container = document.getElementById('upcoming-exams-list-container');
    if (!container) return;

    // Clear previous timer intervals to avoid memory leaks
    countdownIntervals.forEach(clearInterval);
    countdownIntervals = [];

    if (upcomingExamsList.length === 0) {
        container.innerHTML = `
            <div class="py-8 text-center text-slate-400">
                <span class="material-symbols-outlined text-[36px] block mb-1 text-slate-300">event_note</span>
                No upcoming exams scheduled.
            </div>
        `;
        return;
    }

    container.innerHTML = '';
    upcomingExamsList.forEach((exam, idx) => {
        const div = document.createElement('div');
        div.className = 'py-4 flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4';
        const timerId = `upcoming-timer-${idx}`;
        const startTime = new Date(exam.startTime).toLocaleString();

        div.innerHTML = `
            <div>
                <h4 class="font-bold text-slate-800 text-sm">${exam.title}</h4>
                <p class="text-xs text-slate-500 mt-0.5">Starts at: ${startTime}</p>
                <div class="flex items-center gap-2 mt-2">
                    <span class="px-2 py-0.5 bg-purple-50 text-purple-700 border border-purple-200 rounded text-[10px] font-bold">SCHEDULED</span>
                    <span class="text-[10px] text-slate-400 font-medium">Duration: ${exam.durationInMinutes} mins</span>
                </div>
            </div>
            <div class="bg-slate-50 border border-slate-200 rounded-xl px-4 py-2 shrink-0 flex flex-col items-center shadow-inner">
                <span class="text-[10px] font-bold text-slate-400 uppercase tracking-wide">Starting in</span>
                <span class="text-xs font-black text-slate-700 font-mono tracking-wider mt-0.5" id="${timerId}">Calculating...</span>
            </div>
        `;
        container.appendChild(div);

        // Start countdown timer thread
        startTimerCountdown(exam.startTime, timerId);
    });
}

function startTimerCountdown(targetDateStr, timerElementId) {
    const target = new Date(targetDateStr).getTime();
    const update = () => {
        const el = document.getElementById(timerElementId);
        if (!el) return;

        const now = new Date().getTime();
        const diff = target - now;

        if (diff <= 0) {
            el.textContent = 'Lobby Opening...';
            el.className = 'text-xs font-black text-green-600 font-mono';
            return;
        }

        const days = Math.floor(diff / (1000 * 60 * 60 * 24));
        const hours = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
        const mins = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60));
        const secs = Math.floor((diff % (1000 * 60)) / 1000);

        let timeText = '';
        if (days > 0) timeText += `${days}d `;
        timeText += `${hours.toString().padStart(2, '0')}h ${mins.toString().padStart(2, '0')}m ${secs.toString().padStart(2, '0')}s`;

        el.textContent = timeText;
    };

    update();
    const interval = setInterval(update, 1000);
    countdownIntervals.push(interval);
}

// Fetch Recent Results
async function fetchResults() {
    try {
        let results = [];
        try {
            results = await request(`/student/results`);
        } catch (err) {
            const allRes = await request(`/results`);
            if (Array.isArray(allRes)) {
                // Filter student results
                const uid = currentUser.id || currentUser.userId || currentUser.user_id;
                results = allRes.filter(r => r.studentId == uid || r.student?.id == uid);
            }
        }

        if (Array.isArray(results)) {
            resultsList = results;
            renderResults();
        }
    } catch (e) {
        console.error('Error fetching academic results:', e);
    }
}

// Render Results Table
function renderResults() {
    const tbody = document.getElementById('results-table-body');
    if (!tbody) return;

    if (resultsList.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="5" class="px-6 py-8 text-center text-slate-400 bg-slate-50/10">
                    <span class="material-symbols-outlined text-[36px] block mb-1 text-slate-300">analytics</span>
                    No grading sheets available yet.
                </td>
            </tr>
        `;
        return;
    }

    tbody.innerHTML = '';
    resultsList.forEach(res => {
        const score = res.marks !== undefined ? res.marks : res.score;
        let grade = 'C';
        let color = 'slate';
        let remarks = 'Pass';

        if (score >= 75) {
            grade = 'A';
            color = 'green';
            remarks = 'Excellent';
        } else if (score >= 65) {
            grade = 'B';
            color = 'blue';
            remarks = 'Good';
        } else if (score >= 50) {
            grade = 'C';
            color = 'yellow';
            remarks = 'Satisfactory';
        } else {
            grade = 'F';
            color = 'red';
            remarks = 'Re-academic';
        }

        const tr = document.createElement('tr');
        tr.className = 'hover:bg-slate-50/30 transition-colors border-b border-slate-100';
        tr.innerHTML = `
            <td class="px-6 py-4 font-bold text-slate-800">${res.examTitle || 'Semester End Examination'}</td>
            <td class="px-6 py-4 font-mono text-xs text-slate-400">#EXM-${res.examId || '00'}</td>
            <td class="px-6 py-4 font-black text-slate-700">${score}%</td>
            <td class="px-6 py-4">
                <span class="px-2.5 py-0.5 rounded font-black text-xs bg-${color}-50 text-${color}-700 border border-${color}-100">${grade}</span>
            </td>
            <td class="px-6 py-4 text-right">
                <span class="text-xs font-semibold text-slate-500">${remarks}</span>
            </td>
        `;
        tbody.appendChild(tr);
    });
}

// Fetch Notifications Announcements Inbox
async function fetchNotifications() {
    try {
        const uid = currentUser.id || currentUser.userId || currentUser.user_id;
        const notices = await request(`/student/notifications/${uid}`);
        if (Array.isArray(notices)) {
            notificationsList = notices;
            renderNotifications();
        }
    } catch (e) {
        console.error('Error fetching announcements inbox:', e);
    }
}

// Render Announcements in Dashboard widgets and full Inbox
function renderNotifications() {
    const previewContainer = document.getElementById('bulletin-list-preview-container');
    const fullContainer = document.getElementById('notif-inbox-container');
    if (!previewContainer || !fullContainer) return;

    // Filter preview: show latest 3 announcements
    if (notificationsList.length === 0) {
        previewContainer.innerHTML = `
            <div class="p-6 text-center text-slate-400 text-xs">
                No recent notices published.
            </div>
        `;
    } else {
        previewContainer.innerHTML = '';
        notificationsList.slice(0, 3).forEach(n => {
            const dateStr = n.createdAt
                ? new Date(n.createdAt).toLocaleDateString('en-US', { month: 'short', day: 'numeric' })
                : 'Today';

            const unreadDot = !n.isRead ? '<div class="w-1.5 h-1.5 bg-blue-500 rounded-full shrink-0 mt-1 animate-pulse"></div>' : '';

            const div = document.createElement('div');
            div.className = 'p-4 hover:bg-slate-50 transition-colors flex gap-2 items-start cursor-pointer';
            div.innerHTML = `
                <div class="flex-grow">
                    <h5 class="font-bold text-slate-800 text-xs line-clamp-1">${n.title || 'Notice'}</h5>
                    <p class="text-[10px] text-slate-400 mt-0.5">${dateStr} • Campus News</p>
                </div>
                ${unreadDot}
            `;
            // Click redirects student to notifications tab
            div.addEventListener('click', () => {
                const button = document.querySelectorAll('.sidebar-btn')[2];
                if (button) button.click();
            });
            previewContainer.appendChild(div);
        });
    }

    // Render Full Notifications Inbox
    const activeFilter = document.getElementById('inbox-filter-badge').textContent;
    let filteredList = notificationsList;

    if (activeFilter === 'Unread') {
        filteredList = notificationsList.filter(n => !n.isRead);
    }

    if (filteredList.length === 0) {
        fullContainer.innerHTML = `
            <div class="h-full flex flex-col items-center justify-center py-12 text-slate-400">
                <span class="material-symbols-outlined text-[48px] mb-2 text-slate-300">mail_outline</span>
                <span class="text-xs font-semibold">Your inbox is clear.</span>
            </div>
        `;
        return;
    }

    fullContainer.innerHTML = '';
    filteredList.forEach(n => {
        const dateStr = n.createdAt
            ? new Date(n.createdAt).toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' })
            : 'Today';

        const unreadCls = !n.isRead ? 'bg-blue-50/20 border-l-4 border-l-blue-600' : 'border-l-4 border-l-transparent';
        const unreadDot = !n.isRead ? '<span class="w-2 h-2 bg-blue-500 rounded-full animate-pulse"></span>' : '';
        const actionBtnText = !n.isRead ? 'Mark Read' : 'Mark Unread';
        const actionIcon = !n.isRead ? 'done' : 'mark_email_unread';

        const div = document.createElement('div');
        div.className = `p-5 hover:bg-slate-50 transition-colors flex gap-4 items-start ${unreadCls} group relative`;
        div.innerHTML = `
            <div class="p-2 bg-blue-50 text-blue-600 rounded-full border border-blue-100 shrink-0">
                <span class="material-symbols-outlined text-[20px]">campaign</span>
            </div>
            <div class="flex-grow pr-20">
                <div class="flex items-center gap-2">
                    <h4 class="font-bold text-slate-800 text-sm">${n.title || 'Announcement'}</h4>
                    ${unreadDot}
                </div>
                <p class="text-xs text-slate-600 mt-1 leading-relaxed">${n.message || 'Automated message from administration.'}</p>
                <div class="text-[10px] text-slate-400 mt-2 font-mono">${dateStr}</div>
            </div>

            <!-- Card Actions -->
            <div class="absolute right-4 top-1/2 -translate-y-1/2 opacity-0 group-hover:opacity-100 transition-all flex items-center gap-2">
                <button class="toggle-read-btn w-8 h-8 flex items-center justify-center bg-white border border-slate-200 rounded-xl hover:bg-blue-50 hover:text-blue-600 transition-colors shadow-sm" data-id="${n.recipientId || n.id}" title="${actionBtnText}">
                    <span class="material-symbols-outlined text-[16px]">${actionIcon}</span>
                </button>
                <button class="delete-notif-btn w-8 h-8 flex items-center justify-center bg-white border border-slate-200 rounded-xl hover:bg-red-50 hover:text-red-600 transition-colors shadow-sm" data-id="${n.recipientId || n.id}" title="Delete Notice">
                    <span class="material-symbols-outlined text-[16px]">delete</span>
                </button>
            </div>
        `;

        // Action Bindings
        div.querySelector('.toggle-read-btn').addEventListener('click', (e) => {
            e.stopPropagation();
            toggleNotifReadState(n.recipientId || n.id, !n.isRead);
        });

        div.querySelector('.delete-notif-btn').addEventListener('click', (e) => {
            e.stopPropagation();
            deleteNotif(n.recipientId || n.id);
        });

        fullContainer.appendChild(div);
    });
}

// Mark notice read/unread
async function toggleNotifReadState(id, markRead) {
    try {
        const endpoint = markRead ? `/student/notifications/read/${id}` : `/student/notifications/unread/${id}`;
        await request(endpoint, { method: 'PUT' });
        
        // Update local object list in memory
        const notice = notificationsList.find(n => (n.recipientId || n.id) == id);
        if (notice) {
            notice.isRead = markRead;
            renderNotifications();
            showToast(markRead ? 'Notice marked as read' : 'Notice marked as unread', 'success');
        }
    } catch (e) {
        console.error(e);
        showToast('Error syncing notifications status.', 'error');
    }
}

// Delete single notification notice
async function deleteNotif(id) {
    showConfirmModal(
        'Delete Announcement?',
        'This will permanently delete this notice from your personal inbox. This action cannot be reversed.',
        async () => {
            showLoading();
            try {
                await request(`/student/notifications/delete/${id}`, { method: 'DELETE' });
                notificationsList = notificationsList.filter(n => (n.recipientId || n.id) != id);
                renderNotifications();
                showToast('Announcement removed', 'success');
            } catch (e) {
                console.error(e);
                showToast('Failed to delete notification', 'error');
            } finally {
                hideLoading();
            }
        }
    );
}

// Bulk mark all notices read
async function markAllNotifsRead() {
    const unread = notificationsList.filter(n => !n.isRead);
    if (unread.length === 0) return;

    showLoading();
    try {
        await Promise.all(unread.map(n => 
            request(`/student/notifications/read/${n.recipientId || n.id}`, { method: 'PUT' })
        ));
        notificationsList.forEach(n => n.isRead = true);
        renderNotifications();
        showToast('All messages marked as read.', 'success');
    } catch (e) {
        console.error(e);
        showToast('Bulk update failed.', 'error');
    } finally {
        hideLoading();
    }
}

// Action: Update profile details form
async function handleProfileUpdate(e) {
    e.preventDefault();
    hideLoading();

    const name = document.getElementById('profile-edit-name').value.trim();
    const phone = document.getElementById('profile-edit-phone').value.trim();

    if (name.length < 3) {
        showToast('Full name must be at least 3 characters.', 'error');
        return;
    }

    showConfirmModal(
        'Save Profile Changes?',
        'Confirm update of your personal profile credentials.',
        async () => {
            showLoading();
            try {
                const uid = currentUser.id || currentUser.userId || currentUser.user_id;
                const res = await request(`/user/update-profile/${uid}`, {
                    method: 'PUT',
                    body: { name, phone }
                });
                
                showToast(res || 'Profile credentials updated successfully.', 'success');

                // Update session state instantly so layout is dynamic
                currentUser.name = name;
                currentUser.phone = phone;
                localStorage.setItem('user', JSON.stringify(currentUser));

                // Recalculate avatar layouts
                await fetchStudentProfile();
            } catch (err) {
                console.error(err);
                showToast(err.message || 'Profile credentials synchronization failed.', 'error');
            } finally {
                hideLoading();
            }
        }
    );
}

// Action: Open examination lobby verification passcode modal
function openExamLobby(examId, title, duration) {
    document.getElementById('lobby-exam-id').value = examId;
    document.getElementById('lobby-exam-title').textContent = title;
    document.getElementById('lobby-exam-details').textContent = `Duration: ${duration} minutes • Online Examination Paper`;
    document.getElementById('lobby-passcode').value = '';
    document.getElementById('lobby-passcode-error').classList.add('hidden');

    const modal = document.getElementById('exam-lobby-modal');
    const content = document.getElementById('lobby-modal-content');

    modal.classList.remove('opacity-0', 'pointer-events-none');
    content.classList.remove('scale-95');
}
window.openExamLobby = openExamLobby;

function closeExamLobbyModal() {
    const modal = document.getElementById('exam-lobby-modal');
    const content = document.getElementById('lobby-modal-content');

    modal.classList.add('opacity-0', 'pointer-events-none');
    content.classList.add('scale-95');
}
window.closeExamLobbyModal = closeExamLobbyModal;

// Action: Verify entrance passcode and attempt exam lobby redirection
async function submitExamPasscode(e) {
    e.preventDefault();
    const examId = document.getElementById('lobby-exam-id').value;
    const passcode = document.getElementById('lobby-passcode').value.trim();
    const err = document.getElementById('lobby-passcode-error');

    showLoading();
    try {
        // Authenticate exam schedule credentials
        const exam = activeExamsList.find(x => x.id == examId);
        if (exam && exam.passcode === passcode) {
            showToast('Passcode Verified! Entering Examination Room...', 'success');
            setTimeout(() => {
                closeExamLobbyModal();
                hideLoading();
                // Route to live student exam taking portal
                window.location.href = `/pages/attempt-exam.html?id=${examId}`;
            }, 1000);
        } else {
            hideLoading();
            err.classList.remove('hidden');
        }
    } catch (e) {
        console.error(e);
        hideLoading();
        showToast('Connection lobby validation error.', 'error');
    }
}

// Tab navigation initialization
function setupSectionTransitions() {
    const sidebar = document.body.querySelector('aside');
    if (!sidebar) return;

    // Repaint sidebar profile card with session data
    const name = currentUser.name || 'Student Name';
    const initials = name.trim().split(/\s+/);
    const initialsStr = initials.length >= 2 ? initials[0][0] + initials[1][0] : initials[0].substring(0, 2);

    sidebar.innerHTML = `
        <div class="flex flex-col h-full p-4 space-y-2">
            <div class="p-6 border-b shrink-0">
                <h1 class="text-2xl font-black text-blue-900">EduAssess Pro</h1>
            </div>

            <div class="p-4 space-y-2 flex-grow overflow-y-auto">
                <!-- Profile Block -->
                <div class="flex items-center gap-3 p-3 bg-slate-50 border rounded-xl mb-6 shrink-0">
                    <div class="w-10 h-10 rounded-full bg-blue-100 text-blue-700 flex items-center justify-center font-bold text-base border border-blue-200">
                        ${initialsStr.toUpperCase()}
                    </div>
                    <div class="min-w-0 flex-1">
                        <p class="font-bold text-slate-800 text-xs truncate w-32">${name}</p>
                        <p class="text-[10px] text-slate-400 font-mono">#EDU-${currentUser.id || '000'}</p>
                    </div>
                </div>

                <button class="sidebar-btn w-full flex items-center gap-3 p-3 rounded-lg bg-blue-600 text-white font-bold transition-all shadow-sm">
                    <span class="material-symbols-outlined text-[20px]">space_dashboard</span>
                    <span class="text-xs">Dashboard</span>
                </button>

                <button class="sidebar-btn w-full flex items-center gap-3 p-3 rounded-lg hover:bg-gray-100 text-gray-700 font-medium transition-all">
                    <span class="material-symbols-outlined text-[20px]">person</span>
                    <span class="text-xs">My Profile</span>
                </button>
                
                <button class="sidebar-btn w-full flex items-center justify-between p-3 rounded-lg hover:bg-gray-100 text-gray-700 font-medium transition-all">
                    <div class="flex items-center gap-3">
                        <span class="material-symbols-outlined text-[20px]">campaign</span>
                        <span class="text-xs">Bulletins Inbox</span>
                    </div>
                    <span id="sidebar-notif-badge" class="hidden bg-red-500 text-white text-[10px] px-2 py-0.5 rounded-full font-bold">0</span>
                </button>
            </div>

            <div class="p-4 border-t shrink-0">
                <button id="sidebar-student-logout" class="w-full bg-red-50 text-red-600 py-3 rounded-xl hover:bg-red-100 transition-colors font-bold text-xs flex items-center justify-center gap-1.5">
                    <span class="material-symbols-outlined text-[20px]">logout</span>
                    <span>Sign Out</span>
                </button>
            </div>
        </div>
    `;

    // Bind sidebar buttons to switchSection tabs
    const buttons = sidebar.querySelectorAll('.sidebar-btn');
    buttons[0].addEventListener('click', () => switchSection('dashboard', buttons[0]));
    buttons[1].addEventListener('click', () => switchSection('profile', buttons[1]));
    buttons[2].addEventListener('click', () => switchSection('notifications', buttons[2]));

    // Bind sidebar logout
    sidebar.querySelector('#sidebar-student-logout').addEventListener('click', () => {
        localStorage.removeItem('user');
        window.location.href = '/index.html';
    });

    // Bind bulletins filters
    const inboxFilterDropdown = document.getElementById('notif-filter-dropdown');
    const filterBtn = document.getElementById('btn-toggle-notif-filter');

    if (filterBtn && inboxFilterDropdown) {
        filterBtn.addEventListener('click', (e) => {
            e.stopPropagation();
            inboxFilterDropdown.classList.toggle('hidden');
        });

        document.addEventListener('click', () => {
            inboxFilterDropdown.classList.add('hidden');
        });

        inboxFilterDropdown.querySelectorAll('button').forEach(btn => {
            btn.addEventListener('click', () => {
                const val = btn.getAttribute('data-filter');
                document.getElementById('inbox-filter-badge').textContent = val;
                renderNotifications();
            });
        });
    }
}
