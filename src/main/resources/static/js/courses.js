/**
 * Courses Management Module
 */

let allCourses = [];

document.addEventListener('DOMContentLoaded', function () {
    const container = document.getElementById('coursesList');
    if (container) {
        loadCourses();
    }
});

async function loadCourses() {
    const container = document.getElementById('coursesList');
    if (!container) return;

    try {
        const response = await window.scaUtils.fetchWithCSRF('/api/courses', { method: 'GET' });

        if (!response.ok) {
            console.error('Courses fetch failed:', response.status);
            showCoursesError();
            return;
        }

        const data = await response.json();
        allCourses = Array.isArray(data) ? data : [];
        displayCourses(allCourses);
    } catch (error) {
        console.error('Error loading courses:', error);
        showCoursesError();
    }
}

function displayCourses(courses) {
    const container = document.getElementById('coursesList');
    if (!container) return;

    // Ensure courses is an array
    if (!Array.isArray(courses)) {
        courses = [];
    }

    if (courses.length === 0) {
        container.innerHTML = `
            <div class="text-center py-5">
                <i class="bi bi-inbox fs-1 text-muted"></i>
                <p class="text-muted mt-3">No courses found</p>
                <a href="/admin/courses/create" class="btn btn-primary mt-2">Add First Course</a>
            </div>
        `;
        return;
    }

    container.innerHTML = `
        <div class="table-responsive">
            <table class="table table-hover align-middle">
                <thead class="table-light">
                    <tr>
                        <th>Code</th>
                        <th>Title</th>
                        <th>Credits</th>
                        <th>Description</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    ${courses.map(course => `
                        <tr>
                            <td><span class="badge bg-light text-dark border">${course.code || 'N/A'}</span></td>
                            <td class="fw-bold">${course.title || 'N/A'}</td>
                            <td>${course.credits || 0}</td>
                            <td><small class="text-muted">${course.description ? (course.description.substring(0, 50) + (course.description.length > 50 ? '...' : '')) : '-'}</small></td>
                            <td>
                                <div class="btn-group btn-group-sm">
                                    <a href="/admin/courses/${course.id}/edit" class="btn btn-outline-primary"><i class="bi bi-pencil"></i></a>
                                    <button class="btn btn-outline-danger" onclick="deleteCourse(${course.id})"><i class="bi bi-trash"></i></button>
                                </div>
                            </td>
                        </tr>
                    `).join('')}
                </tbody>
            </table>
        </div>
    `;
}

function filterCourses() {
    const search = document.getElementById('searchCourse')?.value.toLowerCase() || '';
    const credit = document.getElementById('creditFilter')?.value;

    const filtered = allCourses.filter(c => {
        const matchesSearch = (c.code || '').toLowerCase().includes(search) || (c.title || '').toLowerCase().includes(search);
        const matchesCredit = !credit || c.credits == credit;
        return matchesSearch && matchesCredit;
    });
    displayCourses(filtered);
}

async function deleteCourse(id) {
    if (!window.scaUtils.confirmAction('Delete this course?')) return;
    try {
        const response = await window.scaUtils.fetchWithCSRF(`/api/courses/${id}`, { method: 'DELETE' });
        if (response.ok) {
            window.scaUtils.showToast('Course deleted', 'success');
            await loadCourses();
        } else {
            window.scaUtils.showToast('Failed to delete', 'error');
        }
    } catch (e) {
        console.error(e);
        window.scaUtils.showToast('Error deleting course', 'error');
    }
}

function showCoursesError() {
    const c = document.getElementById('coursesList');
    if (c) c.innerHTML = '<div class="text-center text-danger py-5"><i class="bi bi-exclamation-circle fs-1"></i><p>Error loading courses</p><button class="btn btn-primary" onclick="loadCourses()">Retry</button></div>';
}

window.loadCourses = loadCourses;
window.filterCourses = filterCourses;
window.deleteCourse = deleteCourse;
