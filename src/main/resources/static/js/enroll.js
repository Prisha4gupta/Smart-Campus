/**
 * Smart Campus Assistant - Enrollment Module
 * Handles course enrollment AJAX operations
 * 
 * @author Team Stack Underflow
 * @version 1.0.0
 */

/**
 * Enroll student in a course offering
 * @param {number} offeringId - Course offering ID
 * @param {HTMLElement} buttonElement - Enroll button element
 */
async function enrollInOffering(offeringId, buttonElement) {
    const userId = window.scaUtils.getCurrentUserId();
    if (!userId) {
        window.scaUtils.showToast('User session not found. Please login again.', 'error');
        return;
    }
    
    // Disable button to prevent double submission
    buttonElement.disabled = true;
    buttonElement.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Enrolling...';
    
    try {
        const payload = {
            userId: userId,
            offeringId: offeringId
        };
        
        const response = await window.scaUtils.fetchWithCSRF('/api/enrollments', {
            method: 'POST',
            body: JSON.stringify(payload)
        });
        
        const result = await window.scaUtils.handleApiResponse(response);
        
        // Show success message
        const statusText = result.enrollmentStatus === 'WAITLISTED' ? 
            'You have been added to the waitlist' : 
            'Enrollment successful!';
        
        window.scaUtils.showToast(statusText, 'success');
        
        // Update button state
        buttonElement.innerHTML = '<i class="bi bi-check-circle me-1"></i>Enrolled';
        buttonElement.classList.remove('btn-primary');
        buttonElement.classList.add('btn-success');
        
        // Refresh page after 2 seconds to show updated data
        setTimeout(() => {
            location.reload();
        }, 2000);
        
    } catch (error) {
        console.error('Enrollment failed:', error);
        window.scaUtils.showToast(error.message || 'Failed to enroll. Please try again.', 'error');
        
        // Re-enable button
        buttonElement.disabled = false;
        buttonElement.innerHTML = '<i class="bi bi-plus-circle me-1"></i>Enroll';
    }
}

/**
 * Drop an enrollment
 * @param {number} enrollmentId - Enrollment ID
 */
async function dropEnrollment(enrollmentId) {
    if (!window.scaUtils.confirmAction('Are you sure you want to drop this enrollment?')) {
        return;
    }
    
    try {
        window.scaUtils.showLoading();
        
        const response = await window.scaUtils.fetchWithCSRF(`/api/enrollments/${enrollmentId}`, {
            method: 'DELETE'
        });
        
        if (!response.ok) {
            throw new Error('Failed to drop enrollment');
        }
        
        window.scaUtils.showToast('Enrollment dropped successfully', 'success');
        
        // Reload page to reflect changes
        setTimeout(() => {
            location.reload();
        }, 1500);
        
    } catch (error) {
        console.error('Failed to drop enrollment:', error);
        window.scaUtils.showToast(error.message || 'Failed to drop enrollment', 'error');
    } finally {
        window.scaUtils.hideLoading();
    }
}

/**
 * Check offering capacity and update UI
 * @param {number} offeringId - Course offering ID
 * @param {HTMLElement} capacityElement - Capacity display element
 */
async function checkOfferingCapacity(offeringId, capacityElement) {
    try {
        const response = await window.scaUtils.fetchWithCSRF(`/api/offerings/${offeringId}`, {
            method: 'GET'
        });
        
        const offering = await window.scaUtils.handleApiResponse(response);
        
        const enrolled = offering.enrolledCount || 0;
        const capacity = offering.capacity || 0;
        const percentage = (enrolled / capacity) * 100;
        
        // Update capacity bar
        if (capacityElement) {
            capacityElement.innerHTML = `
                <div class="capacity-bar mb-2">
                    <div class="capacity-bar-fill" style="width: ${percentage}%"></div>
                </div>
                <small class="text-muted">${enrolled} / ${capacity} enrolled</small>
            `;
        }
        
        return {
            isFull: enrolled >= capacity,
            enrolled,
            capacity
        };
        
    } catch (error) {
        console.error('Failed to check capacity:', error);
        return null;
    }
}

// Export functions
window.enrollInOffering = enrollInOffering;
window.dropEnrollment = dropEnrollment;
window.checkOfferingCapacity = checkOfferingCapacity;
