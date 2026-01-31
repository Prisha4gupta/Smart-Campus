/**
 * Smart Campus Assistant - Notifications Module
 */

const NOTIFICATION_POLL_INTERVAL = 30000;
let notificationPollTimer = null;

function initNotifications() {
    const userId = document.body.dataset.userId || window.scaUtils.getCurrentUserId();
    if (!userId) {
        console.warn('User ID not found, notifications disabled');
        return;
    }
    fetchNotifications(userId);
    startNotificationPolling(userId);

    document.addEventListener('visibilitychange', function () {
        if (document.hidden) {
            stopNotificationPolling();
        } else {
            startNotificationPolling(userId);
        }
    });
}

function startNotificationPolling(userId) {
    if (notificationPollTimer) return;
    notificationPollTimer = setInterval(() => {
        fetchNotifications(userId);
    }, NOTIFICATION_POLL_INTERVAL);
}

function stopNotificationPolling() {
    if (notificationPollTimer) {
        clearInterval(notificationPollTimer);
        notificationPollTimer = null;
    }
}

async function fetchNotifications(userId) {
    try {
        const url = `/api/notifications/user/${userId}/unread`;
        const response = await window.scaUtils.fetchWithCSRF(url, { method: 'GET' });

        if (!response.ok) {
            console.error('Notification fetch failed:', response.status);
            return;
        }

        const notifications = await response.json();
        updateNotificationUI(notifications);
    } catch (error) {
        console.error('Failed to fetch notifications:', error);
    }
}

function updateNotificationUI(notifications) {
    const unreadCount = Array.isArray(notifications) ? notifications.length : 0;
    const unreadBadge = document.getElementById('unreadCount');
    const sidebarUnreadBadge = document.getElementById('sidebarUnreadCount');
    const notificationList = document.getElementById('notificationList');

    if (unreadBadge) {
        unreadBadge.textContent = unreadCount;
        unreadBadge.style.display = unreadCount > 0 ? 'inline-block' : 'none';
    }

    if (sidebarUnreadBadge) {
        sidebarUnreadBadge.textContent = unreadCount;
        sidebarUnreadBadge.style.display = unreadCount > 0 ? 'inline-block' : 'none';
    }

    if (notificationList) {
        if (unreadCount === 0) {
            notificationList.innerHTML = `
                <div class="text-center text-muted p-3">
                    <i class="bi bi-inbox fs-1 d-block mb-2"></i>
                    No new notifications
                </div>
            `;
        } else {
            notificationList.innerHTML = notifications.map(notif => `
                <li>
                    <a class="dropdown-item notification-item ${notif.isRead ? '' : 'unread'}" 
                       href="#" onclick="markNotificationAsRead(${notif.id}); return false;">
                        <div class="d-flex justify-content-between align-items-start">
                            <div style="flex: 1;">
                                <strong class="d-block">${escapeHtml(notif.title)}</strong>
                                <small class="text-muted">${escapeHtml(notif.body || '')}</small>
                                <div class="time-ago mt-1">
                                    <i class="bi bi-clock me-1"></i>${window.scaUtils.timeAgo(notif.createdAt)}
                                </div>
                            </div>
                            ${!notif.isRead ? '<span class="badge bg-primary ms-2">New</span>' : ''}
                        </div>
                    </a>
                </li>
            `).join('');
        }
    }
}

async function markNotificationAsRead(notificationId) {
    try {
        const url = `/api/notifications/${notificationId}/read`;
        await window.scaUtils.fetchWithCSRF(url, { method: 'PUT' });

        const userId = window.scaUtils.getCurrentUserId();
        if (userId) {
            fetchNotifications(userId);
        }
    } catch (error) {
        console.error('Failed to mark notification as read:', error);
        window.scaUtils.showToast('Failed to update notification', 'error');
    }
}

async function markAllAsRead() {
    const userId = window.scaUtils.getCurrentUserId();
    if (!userId) return;

    try {
        window.scaUtils.showLoading();
        const url = `/api/notifications/user/${userId}/read-all`;
        await window.scaUtils.fetchWithCSRF(url, { method: 'PUT' });

        fetchNotifications(userId);
        window.scaUtils.showToast('All notifications marked as read', 'success');
    } catch (error) {
        console.error('Failed to mark all as read:', error);
        window.scaUtils.showToast('Failed to update notifications', 'error');
    } finally {
        window.scaUtils.hideLoading();
    }
}

function escapeHtml(text) {
    if (!text) return '';
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

window.initNotifications = initNotifications;
window.markNotificationAsRead = markNotificationAsRead;
window.markAllAsRead = markAllAsRead;
