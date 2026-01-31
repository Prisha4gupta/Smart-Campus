/**
 * Events Management Module
 */
let allEvents = [];

document.addEventListener('DOMContentLoaded', function () {
    const container = document.getElementById('eventsList');
    if (container) {
        loadEvents();
    }
});

async function loadEvents() {
    const container = document.getElementById('eventsList');
    if (!container) return;

    try {
        const response = await window.scaUtils.fetchWithCSRF('/api/events', { method: 'GET' });

        if (!response.ok) {
            console.error('Events fetch failed:', response.status);
            showEventsError();
            return;
        }

        const data = await response.json();
        allEvents = Array.isArray(data) ? data : [];
        displayEvents(allEvents);
    } catch (error) {
        console.error('Error loading events:', error);
        showEventsError();
    }
}

function displayEvents(events) {
    const container = document.getElementById('eventsList');
    if (!container) return;

    if (!Array.isArray(events)) events = [];

    if (events.length === 0) {
        container.innerHTML = `
            <div class="text-center py-5">
                <i class="bi bi-calendar-x fs-1 text-muted"></i>
                <p class="text-muted mt-3">No events found</p>
                <a href="/admin/events/create" class="btn btn-primary mt-2">Create First Event</a>
            </div>
        `;
        return;
    }

    container.innerHTML = `
        <div class="table-responsive">
            <table class="table table-hover">
                <thead class="table-light">
                    <tr>
                        <th>Title</th>
                        <th>Date & Time</th>
                        <th>Visibility</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    ${events.map(e => `
                        <tr>
                            <td>
                                <div class="fw-bold">${escapeHtml(e.title || '')}</div>
                                <small class="text-muted">${escapeHtml((e.body || '').substring(0, 50))}${(e.body || '').length > 50 ? '...' : ''}</small>
                            </td>
                            <td>${formatDateTime(e.startDatetime)}${e.endDatetime ? ' - ' + formatDateTime(e.endDatetime) : ''}</td>
                            <td><span class="badge bg-${e.isPublic ? 'success' : 'warning'}">${e.isPublic ? 'Public' : 'Private'}</span></td>
                            <td>
                                <div class="btn-group btn-group-sm">
                                    <button class="btn btn-outline-danger" onclick="deleteEvent(${e.id})"><i class="bi bi-trash"></i></button>
                                </div>
                            </td>
                        </tr>
                    `).join('')}
                </tbody>
            </table>
        </div>
    `;
}

function filterEvents() {
    const search = (document.getElementById('searchEvent')?.value || '').toLowerCase();
    const status = document.getElementById('eventStatusFilter')?.value || '';
    const now = new Date();

    const filtered = allEvents.filter(e => {
        const matchesSearch = !search || (e.title || '').toLowerCase().includes(search);
        let matchesStatus = true;
        if (status === 'Upcoming') {
            matchesStatus = new Date(e.startDatetime) >= now;
        } else if (status === 'Past') {
            matchesStatus = new Date(e.startDatetime) < now;
        }
        return matchesSearch && matchesStatus;
    });
    displayEvents(filtered);
}

async function deleteEvent(id) {
    if (!window.scaUtils.confirmAction('Delete this event?')) return;
    try {
        const response = await window.scaUtils.fetchWithCSRF(`/api/events/${id}`, { method: 'DELETE' });
        if (response.ok) {
            window.scaUtils.showToast('Event deleted', 'success');
            await loadEvents();
        } else {
            window.scaUtils.showToast('Failed to delete event', 'error');
        }
    } catch (e) {
        console.error(e);
        window.scaUtils.showToast('Error deleting event', 'error');
    }
}

function showEventsError() {
    const container = document.getElementById('eventsList');
    if (container) {
        container.innerHTML = '<div class="text-center text-danger py-5"><i class="bi bi-exclamation-circle fs-1"></i><p>Error loading events</p><button class="btn btn-primary" onclick="loadEvents()">Retry</button></div>';
    }
}

function escapeHtml(text) {
    if (!text) return '';
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

function formatDateTime(dateStr) {
    if (!dateStr) return '';
    try {
        const d = new Date(dateStr);
        return d.toLocaleString('en-US', { month: 'short', day: 'numeric', year: 'numeric', hour: '2-digit', minute: '2-digit' });
    } catch { return dateStr; }
}

window.loadEvents = loadEvents;
window.filterEvents = filterEvents;
window.deleteEvent = deleteEvent;
