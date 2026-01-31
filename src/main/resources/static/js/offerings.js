/**
 * Offerings Management Module
 */
let allOfferings = [];

document.addEventListener('DOMContentLoaded', function () {
    if (document.getElementById('offeringsTable')) {
        loadOfferings();
    }
});

async function loadOfferings() {
    const container = document.getElementById('offeringsTable');
    if (!container) return;

    try {
        const response = await window.fetchWithCSRF('/api/offerings');
        allOfferings = await window.scaUtils.handleApiResponse(response);
        displayOfferings(allOfferings);
    } catch (error) {
        console.error('Error loading offerings:', error);
        container.innerHTML = '<div class="text-center text-danger py-5">Error loading offerings</div>';
    }
}

function displayOfferings(offerings) {
    const container = document.getElementById('offeringsTable');
    if (!container) return;

    if (!offerings || offerings.length === 0) {
        container.innerHTML = `
            <div class="text-center py-5">
                <i class="bi bi-inbox fs-1 text-muted"></i>
                <p class="text-muted mt-3">No course offerings found</p>
                <a href="/admin/offerings/create" class="btn btn-primary mt-2">Create First Offering</a>
            </div>
        `;
        return;
    }

    container.innerHTML = `
        <div class="table-responsive">
            <table class="table table-hover align-middle">
                <thead class="table-light">
                    <tr>
                        <th>Course</th>
                        <th>Section</th>
                        <th>Semester</th>
                        <th>Faculty</th>
                        <th>Status</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    ${offerings.map(off => {
        const enrolled = off.enrolledCount || 0;
        const cap = off.capacity || 0;
        const pct = cap > 0 ? Math.round((enrolled / cap) * 100) : 0;

        return `
                        <tr>
                            <td>
                                <div class="fw-bold">${off.course?.code || 'N/A'}</div>
                                <small class="text-muted">${off.course?.title || ''}</small>
                            </td>
                            <td><span class="badge bg-secondary">${off.sectionCode || 'N/A'}</span></td>
                            <td>${off.semester || 'N/A'}</td>
                            <td>${off.faculty ? (off.faculty.firstName + ' ' + off.faculty.lastName) : 'TBA'}</td>
                            <td style="width: 200px;">
                                <div class="d-flex align-items-center">
                                    <span class="small me-2">${enrolled}/${cap}</span>
                                    <div class="progress flex-grow-1" style="height: 6px;">
                                        <div class="progress-bar ${pct > 90 ? 'bg-danger' : 'bg-success'}" style="width: ${pct}%"></div>
                                    </div>
                                </div>
                            </td>
                            <td>
                                <div class="btn-group btn-group-sm">
                                    <a href="/admin/offerings/${off.id}/edit" class="btn btn-outline-primary"><i class="bi bi-pencil"></i></a>
                                    <button class="btn btn-outline-danger" onclick="deleteOffering(${off.id})"><i class="bi bi-trash"></i></button>
                                </div>
                            </td>
                        </tr>
                        `;
    }).join('')}
                </tbody>
            </table>
        </div>
    `;
}

function filterOfferings() {
    const sem = document.getElementById('semesterFilter')?.value.toLowerCase();
    const search = document.getElementById('searchFilter')?.value.toLowerCase();

    const filtered = allOfferings.filter(o => {
        const matchesSem = !sem || (o.semester || '').toLowerCase().includes(sem);
        const matchesSearch = !search || (o.course?.code || '').toLowerCase().includes(search) || (o.course?.title || '').toLowerCase().includes(search);
        return matchesSem && matchesSearch;
    });
    displayOfferings(filtered);
}

async function deleteOffering(id) {
    if (!window.scaUtils.confirmAction('Delete this offering?')) return;
    try {
        const response = await window.fetchWithCSRF(`/api/offerings/${id}`, { method: 'DELETE' });
        if (response.ok) {
            window.scaUtils.showToast('Offering deleted', 'success');
            await loadOfferings();
        } else {
            window.scaUtils.showToast('Failed to delete', 'error');
        }
    } catch (e) {
        window.scaUtils.showToast('Error deleting offering', 'error');
    }
}

window.loadOfferings = loadOfferings;
window.filterOfferings = filterOfferings;
window.deleteOffering = deleteOffering;
