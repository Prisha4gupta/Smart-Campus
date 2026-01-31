/**
 * Timetable Management Module
 */

let allEntries = [];

const DAY_MAP = {
    1: 'Sunday',
    2: 'Monday',
    3: 'Tuesday',
    4: 'Wednesday',
    5: 'Thursday',
    6: 'Friday',
    7: 'Saturday'
};

document.addEventListener('DOMContentLoaded', function () {
    const container = document.getElementById('timetableList');
    if (container) {
        loadTimetable();
    }
});

async function loadTimetable() {
    const container = document.getElementById('timetableList');
    if (!container) return;

    try {
        const response = await window.scaUtils.fetchWithCSRF('/api/timetable-entries');
        allEntries = await window.scaUtils.handleApiResponse(response);
        displayEntries(allEntries);
    } catch (error) {
        console.error('Error loading timetable:', error);
        showTimetableError();
    }
}

function displayEntries(entries) {
    const container = document.getElementById('timetableList');
    if (!container) return;

    if (entries.length === 0) {
        container.innerHTML = `
            <div class="text-center py-5">
                <i class="bi bi-inbox fs-1 text-muted"></i>
                <p class="text-muted mt-3">No timetable entries found</p>
                <a href="/admin/timetable/create" class="btn btn-primary mt-2">Add First Entry</a>
            </div>
        `;
        return;
    }

    // Sort entries: DayOfWeek (int) then Time
    entries.sort((a, b) => {
        const dayDiff = (a.dayOfWeek || 0) - (b.dayOfWeek || 0);
        if (dayDiff !== 0) return dayDiff;
        return (a.startTime || '').localeCompare(b.startTime || '');
    });

    container.innerHTML = `
        <div class="table-responsive">
            <table class="table table-hover">
                <thead class="table-light">
                    <tr>
                        <th>Day/Time</th>
                        <th>Course</th>
                        <th>Faculty</th>
                        <th>Room</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    ${entries.map(entry => `
                        <tr>
                            <td>
                                <div class="fw-bold text-primary">${DAY_MAP[entry.dayOfWeek] || 'Unknown'}</div>
                                <small>${entry.startTime} - ${entry.endTime}</small>
                            </td>
                            <td>
                                <strong>${entry.courseCode || 'N/A'}</strong>
                                <small class="d-block text-muted">${entry.courseTitle || ''}</small>
                            </td>
                            <td>
                                ${entry.facultyName || 'TBA'}
                            </td>
                            <td><span class="badge bg-light text-dark border">${entry.room || 'TBA'}</span></td>
                            <td>
                                <div class="btn-group btn-group-sm" role="group">
                                    <a href="/admin/timetable/${entry.id}/edit" class="btn btn-outline-primary">
                                        <i class="bi bi-pencil"></i>
                                    </a>
                                    <button class="btn btn-outline-danger" onclick="deleteEntry(${entry.id})">
                                        <i class="bi bi-trash"></i>
                                    </button>
                                </div>
                            </td>
                        </tr>
                    `).join('')}
                </tbody>
            </table>
        </div>
    `;
}

function filterTimetable() {
    const dayInput = document.getElementById('dayFilter');
    const searchInput = document.getElementById('searchTimetable');

    const day = dayInput ? parseInt(dayInput.value) || null : null;
    const search = searchInput ? searchInput.value.toLowerCase() : '';

    const filtered = allEntries.filter(entry => {
        const matchesDay = !day || entry.dayOfWeek === day;
        const matchesSearch = !search ||
            (entry.courseCode || '').toLowerCase().includes(search) ||
            (entry.courseTitle || '').toLowerCase().includes(search) ||
            (entry.room || '').toLowerCase().includes(search);
        return matchesDay && matchesSearch;
    });

    displayEntries(filtered);
}

async function deleteEntry(id) {
    if (!window.scaUtils.confirmAction('Are you sure you want to delete this timetable entry?')) return;

    try {
        const response = await window.scaUtils.fetchWithCSRF(`/api/timetable-entries/${id}`, {
            method: 'DELETE'
        });

        if (response.ok) {
            window.scaUtils.showToast('Entry deleted successfully', 'success');
            await loadTimetable();
        } else {
            window.scaUtils.showToast('Failed to delete entry', 'error');
        }
    } catch (error) {
        console.error('Error deleting entry:', error);
        window.scaUtils.showToast('Failed to delete entry', 'error');
    }
}

function showTimetableError() {
    const container = document.getElementById('timetableList');
    if (container) {
        container.innerHTML = `
            <div class="text-center py-5">
                <i class="bi bi-exclamation-triangle fs-1 text-danger"></i>
                <p class="text-danger mt-3">Error loading timetable</p>
                <button class="btn btn-primary" onclick="loadTimetable()">Retry</button>
            </div>
        `;
    }
}

// Global exports
window.loadTimetable = loadTimetable;
window.deleteEntry = deleteEntry;
window.filterTimetable = filterTimetable;
