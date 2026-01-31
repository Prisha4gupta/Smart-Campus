/**
 * Smart Campus Assistant - Main JavaScript
 * Global utilities and common functions
 */

function showToast(message, type = 'info') {
    const toastContainer = document.querySelector('.toast-container') || createToastContainer();
    const toastId = 'toast-' + Date.now();

    const iconMap = {
        success: 'check-circle-fill',
        error: 'exclamation-triangle-fill',
        info: 'info-circle-fill',
        warning: 'exclamation-circle-fill'
    };

    const bgMap = {
        success: 'bg-success',
        error: 'bg-danger',
        info: 'bg-info',
        warning: 'bg-warning'
    };

    const toastHTML = `
        <div id="${toastId}" class="toast custom-toast" role="alert" aria-live="assertive" aria-atomic="true">
            <div class="toast-header ${bgMap[type] || 'bg-primary'} text-white">
                <i class="bi bi-${iconMap[type] || 'info-circle'} me-2"></i>
                <strong class="me-auto">${type.charAt(0).toUpperCase() + type.slice(1)}</strong>
                <small>Just now</small>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="toast" aria-label="Close"></button>
            </div>
            <div class="toast-body">
                ${message}
            </div>
        </div>
    `;

    toastContainer.insertAdjacentHTML('beforeend', toastHTML);

    const toastElement = document.getElementById(toastId);
    if (toastElement && window.bootstrap) {
        const toast = new bootstrap.Toast(toastElement, { autohide: true, delay: 5000 });
        toast.show();
        toastElement.addEventListener('hidden.bs.toast', () => toastElement.remove());
    }
}

function createToastContainer() {
    let container = document.querySelector('.toast-container');
    if (!container) {
        container = document.createElement('div');
        container.className = 'toast-container position-fixed bottom-0 end-0 p-3';
        document.body.appendChild(container);
    }
    return container;
}

function showLoading() {
    if (document.querySelector('.spinner-overlay')) return;
    const spinnerHTML = `
        <div class="spinner-overlay position-fixed top-0 start-0 w-100 h-100 d-flex justify-content-center align-items-center bg-dark bg-opacity-50" style="z-index: 1050;">
            <div class="spinner-border text-light" style="width: 3rem; height: 3rem;" role="status">
                <span class="visually-hidden">Loading...</span>
            </div>
        </div>
    `;
    document.body.insertAdjacentHTML('beforeend', spinnerHTML);
}

function hideLoading() {
    const spinner = document.querySelector('.spinner-overlay');
    if (spinner) spinner.remove();
}

function formatDate(date) {
    if (!date) return 'N/A';
    const d = new Date(date);
    return isNaN(d.getTime()) ? 'Invalid Date' : d.toLocaleDateString('en-US', { year: 'numeric', month: 'short', day: 'numeric' });
}

function timeAgo(date) {
    if (!date) return '';
    const now = new Date();
    const past = new Date(date);
    const diffMs = now - past;
    const diffMins = Math.floor(diffMs / 60000);
    const diffHours = Math.floor(diffMins / 60);
    const diffDays = Math.floor(diffHours / 24);

    if (diffMins < 1) return 'Just now';
    if (diffMins < 60) return `${diffMins} min${diffMins !== 1 ? 's' : ''} ago`;
    if (diffHours < 24) return `${diffHours} hr${diffHours !== 1 ? 's' : ''} ago`;
    if (diffDays < 7) return `${diffDays} day${diffDays !== 1 ? 's' : ''} ago`;
    return formatDate(date);
}

function getCurrentUserId() {
    // First try body dataset
    const bodyUserId = document.body.dataset.userId;
    if (bodyUserId) return parseInt(bodyUserId);
    // Then try hidden input
    const input = document.getElementById('currentUserId');
    return input ? parseInt(input.value) : null;
}

/**
 * Handle API Response - auto-detect JSON vs text
 * Works with both Response objects and already-parsed data
 */
async function handleApiResponse(response) {
    // If already parsed data, return as-is
    if (!(response instanceof Response)) {
        return response;
    }

    if (!response.ok) {
        let errorMsg = `HTTP ${response.status}`;
        try {
            const text = await response.text();
            try {
                const json = JSON.parse(text);
                errorMsg = json.message || json.error || errorMsg;
            } catch {
                errorMsg = text || errorMsg;
            }
        } catch { /* ignore */ }
        throw new Error(errorMsg);
    }

    // Check content-type for JSON
    const contentType = response.headers.get('content-type') || '';
    if (contentType.includes('application/json')) {
        return response.json();
    }

    // Try to parse as JSON, fallback to text
    const text = await response.text();
    try {
        return JSON.parse(text);
    } catch {
        return text;
    }
}

function confirmAction(message) {
    return window.confirm(message);
}

document.addEventListener('DOMContentLoaded', () => {
    if (window.bootstrap) {
        const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
        tooltipTriggerList.map(t => new bootstrap.Tooltip(t));

        const popoverTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="popover"]'));
        popoverTriggerList.map(p => new bootstrap.Popover(p));
    }
});

// Expose utilities
window.scaUtils = {
    fetchWithCSRF: window.fetchWithCSRF,
    showToast,
    showLoading,
    hideLoading,
    formatDate,
    timeAgo,
    getCurrentUserId,
    handleApiResponse,
    confirmAction
};

window.showToast = showToast;
