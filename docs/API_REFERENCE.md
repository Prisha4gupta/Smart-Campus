# 🌐 API Reference - Smart Campus Assistant

Complete API documentation for all REST endpoints in the Smart Campus Assistant backend.

**Last Updated**: December 12, 2025  
**API Version**: 2.1  
**Total Endpoints**: 115 (63 REST + 49 pages + 3 auth)

---

## 🔗 Base URL

```
Local Development: http://localhost:8080
Production: https://your-domain.com
```

---

## 📋 API Summary

| Module | Base Path | Endpoints | Auth Required |
|--------|-----------|-----------|---------------|
| Authentication | `/login`, `/register` | 3 | Public |
| Dashboard | `/api/dashboard` | 1 | ✓ |
| Courses | `/api/courses` | 6 | ✓ |
| Faculty | `/api/faculty` | 7 | ✓ |
| Offerings | `/api/offerings` | 7 | ✓ |
| Enrollments | `/api/enrollments` | 7 | ✓ |
| Timetable Entries | `/api/timetable-entries` | 7 | ✓ |
| Timetable (Legacy) | `/api/timetable` | 5 | ✓ |
| Events | `/api/events` | 8 | ✓ |
| Notifications | `/api/notifications` | 7 | ✓ |
| **Quizzes (Admin)** | `/admin/quizzes` | 16 | ✓ Admin |
| **Quizzes (Student)** | `/student/grades` | 2 | ✓ Student |
| **Messages (Admin)** | `/admin/messages` | 7 | ✓ Admin |
| **Messages (Student)** | `/student/messages` | 5 | ✓ Student |
| **Notifications (REST)** | `/api/notifications` | 9 | ✓ |
| Page Routes | `/dashboard`, `/admin/*`, `/student/*` | 48 | ✓ |

---

## 🔐 Authentication & Security

All API endpoints (except `/login` and `/register`) require authentication via Spring Security session-based auth.

### CSRF Protection

State-changing requests (POST, PUT, DELETE) require CSRF token included in request headers.

```javascript
const csrfToken = document.querySelector('meta[name="_csrf"]').content;
const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;

fetch('/api/courses', {
    method: 'POST',
    headers: {
        'Content-Type': 'application/json',
        [csrfHeader]: csrfToken
    },
    body: JSON.stringify(data)
});
```

---

## 📖 Detailed Endpoint Documentation

For detailed endpoint documentation including request/response examples, refer to individual controller implementations in:
- `src/main/java/com/sca/smartcampusbackend/controller/`

### Quick Reference by Module

#### 1. Authentication (3 endpoints)
- `GET /login` - Login page
- `GET /signup` - Registration page  
- `POST /register` - User registration

#### 2. Dashboard (1 endpoint)
- `GET /api/dashboard/{studentId}` - Get personalized dashboard data

#### 3. Courses (6 endpoints)
- `GET /api/courses` - List all courses
- `GET /api/courses/{id}` - Get course by ID
- `GET /api/courses/code/{code}` - Get course by code
- `POST /api/courses` - Create course (Admin)
- `PUT /api/courses/{id}` - Update course (Admin)
- `DELETE /api/courses/{id}` - Delete course (Admin)

#### 4. Faculty (7 endpoints)
- `GET /api/faculty` - List all faculty
- `GET /api/faculty/{id}` - Get faculty by ID
- `GET /api/faculty/department/{dept}` - Get faculty by department
- `GET /api/faculty/active` - Get active faculty only
- `POST /api/faculty` - Create faculty (Admin)
- `PUT /api/faculty/{id}` - Update faculty (Admin)
- `DELETE /api/faculty/{id}` - Delete faculty (Admin)

#### 5. Course Offerings (7 endpoints)
- `GET /api/offerings` - List all offerings
- `GET /api/offerings/{id}` - Get offering by ID
- `GET /api/offerings/semester/{semester}` - Get by semester
- `GET /api/offerings/faculty/{facultyId}` - Get by faculty
- `POST /api/offerings` - Create offering (Admin)
- `PUT /api/offerings/{id}` - Update offering (Admin)
- `DELETE /api/offerings/{id}` - Delete offering (Admin)

#### 6. Enrollments (7 endpoints)
- `GET /api/enrollments/student/{userId}` - Get student enrollments
- `GET /api/enrollments/student/{userId}/active` - Get active enrollments
- `GET /api/enrollments/offering/{offeringId}` - Get offering enrollments (Admin)
- `GET /api/enrollments/offering/{offeringId}/count` - Get enrollment count
- `GET /api/enrollments/offering/{offeringId}/is-full` - Check if full
- `POST /api/enrollments` - Enroll student
- `DELETE /api/enrollments/{id}` - Drop enrollment

#### 7. Timetable Entries (7 endpoints)
- `GET /api/timetable-entries/student/{userId}` - Get personalized timetable
- `GET /api/timetable-entries/student/{userId}/day/{day}` - Get by day
- `GET /api/timetable-entries/offering/{offeringId}` - Get offering schedule
- `GET /api/timetable-entries/{id}` - Get entry by ID
- `POST /api/timetable-entries` - Create entry (Admin)
- `PUT /api/timetable-entries/{id}` - Update entry (Admin)
- `DELETE /api/timetable-entries/{id}` - Delete entry (Admin)

#### 8. Events (8 endpoints)
- `GET /api/events/public` - Get public events
- `GET /api/events/upcoming` - Get upcoming events
- `GET /api/events/dashboard` - Get dashboard events (next 7 days)
- `GET /api/events` - Get all events (Admin)
- `GET /api/events/{id}` - Get event by ID
- `POST /api/events` - Create event (Admin)
- `PUT /api/events/{id}` - Update event (Admin)
- `DELETE /api/events/{id}` - Delete event (Admin)

#### 9. Notifications (7 endpoints)
- `GET /api/notifications/user/{userId}` - Get all notifications
- `POST /api/notifications/user/{userId}` - Create notification (Admin)
- `POST /api/notifications/generate` - Bulk generate (Admin)

#### 10. Quizzes - Admin (16 endpoints)
- `GET /admin/quizzes` - List all quizzes
- `GET /admin/quizzes/create` - Quiz creation form
- `POST /admin/quizzes` - Create new quiz
- `GET /admin/quizzes/{id}/edit` - Edit quiz form
- `POST /admin/quizzes/{id}/update` - Update quiz
- `POST /admin/quizzes/{id}/delete` - Delete quiz
- `GET /admin/quizzes/{id}/questions` - List questions for quiz
- `GET /admin/quizzes/{id}/questions/create` - Question creation form
- `POST /admin/quizzes/{id}/questions` - Add question to quiz
- `GET /admin/quizzes/{quizId}/questions/{questionId}/edit` - Edit question form
- `POST /admin/quizzes/{quizId}/questions/{questionId}/update` - Update question
- `POST /admin/quizzes/{quizId}/questions/{questionId}/delete` - Delete question
- `GET /admin/quizzes/{id}/attempts` - View all attempts for a quiz
- `POST /admin/quizzes/{quizId}/attempts/{studentId}/mark` - Mark student attempt
- `GET /admin/quizzes/{quizId}/attempts/{attemptId}/questions` - Get attempt questions
- `POST /admin/quizzes/{quizId}/attempts/{attemptId}/questions/{questionId}/grade` - Grade individual question

#### 11. Quizzes - Student (2 endpoints)
- `GET /student/grades` - View all quiz grades and attempts
- `GET /student/grades/{quizId}` - View detailed grade for specific quiz with question breakdown

#### 12. Messaging - Admin (7 endpoints)
- `GET /admin/messages` - Admin inbox with conversations and broadcasts
- `GET /admin/messages/broadcast` - Broadcast message form
- `POST /admin/messages/broadcast` - Send broadcast to all students
- `GET /admin/messages/chat/{studentId}` - Open conversation with specific student
- `POST /admin/messages/send` - Send message to student
- `GET /admin/messages/compose` - Compose new message form
- `POST /admin/messages/compose` - Submit new message

#### 13. Messaging - Student (5 endpoints)
- `GET /student/messages` - Student inbox with conversations and broadcasts
- `GET /student/messages/chat/{receiverId}` - Chat with admin
- `POST /student/messages/send` - Send message
- `GET /student/messages/compose` - Compose message form
- `POST /student/messages/compose` - Submit new message

#### 14. Notifications - REST API (9 endpoints)
- `POST /api/notifications/generate` - Bulk generate notifications
- `POST /api/notifications/user/{userId}` - Create notification
- `GET /api/notifications` - Get all notifications
- `GET /api/notifications/unread` - Get unread notifications
- `GET /api/notifications/user/{userId}/unread` - Get user unread
- `GET /api/notifications/unread-count` - Get unread count
- `PUT /api/notifications/{id}/read` - Mark notification as read
- `PUT /api/notifications/read-all` - Mark all as read
- `PUT /api/notifications/user/{userId}/read-all` - Mark user's all as read

#### 15. Page Routes (49 endpoints)

**Student Pages (10):**
- `GET /dashboard` - Student dashboard
- `GET /timetable` - Weekly timetable view
- `GET /events` - Events page
- `GET /enroll` - Course enrollment page
- `GET /notifications` - Notifications page
- `GET /profile` - User profile (StudentPageController)
- `GET /settings` - Settings page (StudentPageController)
- `GET /settings/change-password` - Change password form (StudentPageController)
- `POST /settings/change-password` - Submit password change (StudentPageController)
- `GET /admin/timetable` - Manage timetable

**Admin Pages (18):**
- `GET /admin` - Admin home (redirects to offerings)
- `GET /admin/offerings` - Manage offerings
- `GET /admin/offerings/create` - Create offering form
- `GET /admin/offerings/{id}/edit` - Edit offering form
- `GET /admin/events` - Manage events
- `GET /admin/events/create` - Create event form
- `GET /admin/courses` - Manage courses
- `GET /admin/courses/create` - Create course form
- `GET /admin/courses/{id}/edit` - Edit course form
- `GET /admin/faculty` - Manage faculty
- `GET /admin/faculty/create` - Create faculty form
- `GET /admin/faculty/{id}/edit` - Edit faculty form
- `GET /admin/timetable` - Manage timetable (PageController)
- `GET /admin/timetable/create` - Create timetable entry (PageController)
- `GET /admin/timetable/{id}/edit` - Edit timetable entry (PageController)
- `GET /profile` - User profile (PageController - accessible by all roles)
- `GET /settings` - Settings (PageController - accessible by all roles)
- `GET /settings/change-password` - Change password (PageController - POST also available)

**Note**: Quiz pages (16 admin + 2 student) and Message pages (7 admin + 5 student) are documented in their respective sections above.

**Authentication Pages (3):**
- `GET /login` - Login page
- `GET /signup` - Registration page
- `POST /register` - User registration handler

---

## 📞 Support

For API questions or issues:
- **GitHub**: [SmartCampus Repository](https://github.com/Jdsb06/SmartCampus)
- **Issues**: [Report Bug](https://github.com/Jdsb06/SmartCampus/issues)

---

**Maintained by Team BholeChature**  
**IIIT Bangalore | December 2025**
