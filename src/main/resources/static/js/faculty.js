/**
 * Faculty Management Module
 */

let allFaculty = [];

document.addEventListener('DOMContentLoaded', function () {
    const container = document.getElementById('facultyList');
    if (container) {
        loadFaculty();
    }
});

async function loadFaculty() {
    const container = document.getElementById('facultyList');
    if (!container) return;

    try {
        const response = await window.scaUtils.fetchWithCSRF('/api/faculty');
        allFaculty = await window.scaUtils.handleApiResponse(response);
        displayFaculty(allFaculty);
    } catch (error) {
        console.error('Error loading faculty:', error);
        showFacultyError();
    }
}

function displayFaculty(facultyList) {
    const container = document.getElementById('facultyList');
    if (!container) return;

    if (facultyList.length === 0) {
        container.innerHTML = `
            <div class="text-center py-5">
                <i class="bi bi-inbox fs-1 text-muted"></i>
                <p class="text-muted mt-3">No faculty found</p>
                <a href="/admin/faculty/create" class="btn btn-primary mt-2">Add First Faculty</a>
            </div>
        `;
        return;
    }

    container.innerHTML = `
        <div class="table-responsive">
            <table class="table table-hover">
                <thead class="table-light">
                    <tr>
                        <th>Name</th>
                        <th>Email</th>
                        <th>Department</th>
                        <th>Contact</th>
                        <th>Status</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    ${facultyList.map(f => `
                        <tr>
                            <td>
                                <div class="d-flex align-items-center">
                                    <div class="avatar-circle bg-primary text-white me-2" style="width: 40px; height: 40px; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-weight: bold;">
                                        ${(f.firstName || '').charAt(0)}${(f.lastName || '').charAt(0)}
                                    </div>
                                    <div>
                                        <div class="fw-bold">${f.firstName} ${f.lastName}</div>
                                    </div>
                                </div>
                            </td>
                            <td>${f.email}</td>
                            <td><span class="badge bg-light text-dark border">${f.department || 'N/A'}</span></td>
                            <td>${f.phone || '-'}</td>
                            <td>
                                <span class="badge bg-${f.active ? 'success' : 'secondary'}">
                                    ${f.active ? 'Active' : 'Inactive'}
                                </span>
                            </td>
                            <td>
                                <div class="btn-group btn-group-sm" role="group">
                                    <a href="/admin/faculty/${f.id}/edit" class="btn btn-outline-primary">
                                        <i class="bi bi-pencil"></i>
                                    </a>
                                    <button class="btn btn-outline-danger" onclick="deleteFaculty(${f.id})">
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

function filterFaculty() {
    const searchInput = document.getElementById('searchFaculty');
    const deptInput = document.getElementById('deptFilter');

    const search = searchInput ? searchInput.value.toLowerCase() : '';
    const department = deptInput ? deptInput.value : '';

    let filtered = allFaculty.filter(f => {
        const matchesSearch = !search ||
            (f.firstName || '').toLowerCase().includes(search) ||
            (f.lastName || '').toLowerCase().includes(search) ||
            (f.email || '').toLowerCase().includes(search) ||
            (f.department || '').toLowerCase().includes(search);
        const matchesDept = !department || (f.department || '').includes(department);
        return matchesSearch && matchesDept;
    });

    displayFaculty(filtered);
}

async function deleteFaculty(id) {
    if (!window.scaUtils.confirmAction('Are you sure you want to delete this faculty member?')) return;

    try {
        const response = await window.scaUtils.fetchWithCSRF(`/api/faculty/${id}`, {
            method: 'DELETE'
        });

        if (response.ok) {
            window.scaUtils.showToast('Faculty deleted successfully', 'success');
            await loadFaculty();
        } else {
            window.scaUtils.showToast('Failed to delete faculty', 'error');
        }
    } catch (error) {
        console.error('Error deleting faculty:', error);
        window.scaUtils.showToast('Failed to delete faculty', 'error');
    }
}

function showFacultyError() {
    const container = document.getElementById('facultyList');
    if (!container) return;

    container.innerHTML = `
        <div class="text-center py-5">
            <i class="bi bi-exclamation-triangle fs-1 text-danger"></i>
            <p class="text-danger mt-3">Error loading faculty list</p>
            <button class="btn btn-primary" onclick="loadFaculty()">Retry</button>
        </div>
    `;
}

// Make functions global for inline button clicks
window.deleteFaculty = deleteFaculty;
window.filterFaculty = filterFaculty;
window.loadFaculty = loadFaculty;

// Attach filter listeners
document.addEventListener('DOMContentLoaded', function () {
    const searchInput = document.getElementById('searchFaculty');
    const deptInput = document.getElementById('deptFilter');
    if (searchInput) searchInput.addEventListener('keyup', filterFaculty);
    if (deptInput) deptInput.addEventListener('change', filterFaculty);
});
