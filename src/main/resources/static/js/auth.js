// js/auth.js

/**
 * Checks if the user is authenticated and optionally checks for a specific role.
 * Redirects to index.html if unauthenticated or unauthorized.
 * 
 * @param {string|null} requiredRole - The role required to access the page (e.g., 'ADMIN', 'STUDENT', 'LECTURER'). Null if any logged-in user is allowed.
 * @returns {object|null} The user object if authenticated, otherwise null.
 */
function checkAuth(requiredRole = null) {
    const userStr = localStorage.getItem('user');
    
    if (!userStr) {
        window.location.href = '/index.html';
        return null;
    }
    
    try {
        const user = JSON.parse(userStr);
        
        if (requiredRole && user.role !== requiredRole) {
            alert('Access Denied. Insufficient permissions.');
            window.location.href = '/index.html';
            return null;
        }
        
        return user;
    } catch (e) {
        console.error('Error parsing user session', e);
        window.location.href = '/index.html';
        return null;
    }
}

/**
 * Logs the user out and redirects to the login page.
 */
function logout() {
    localStorage.removeItem('user');
    window.location.href = '/index.html';
}
