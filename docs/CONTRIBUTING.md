# 🤝 Contributing Guide - Smart Campus Assistant

Thank you for considering contributing to Smart Campus Assistant! This guide will help you get started.

---

## 📋 Table of Contents

- [Code of Conduct](#code-of-conduct)
- [How Can I Contribute?](#how-can-i-contribute)
- [Development Workflow](#development-workflow)
- [Coding Standards](#coding-standards)
- [Commit Message Guidelines](#commit-message-guidelines)
- [Pull Request Process](#pull-request-process)
- [Testing Guidelines](#testing-guidelines)

---

## 📜 Code of Conduct

### Our Pledge

We are committed to providing a welcoming and inclusive environment for all contributors.

### Our Standards

**Positive behavior**:
- Using welcoming and inclusive language
- Being respectful of differing viewpoints
- Gracefully accepting constructive criticism
- Focusing on what is best for the community

**Unacceptable behavior**:
- Harassment or discriminatory language
- Personal attacks or trolling
- Publishing others' private information
- Other unprofessional conduct

---

## 🛠️ How Can I Contribute?

### Reporting Bugs

**Before submitting**:
1. Check existing issues to avoid duplicates
2. Verify bug on latest version
3. Collect error logs and screenshots

**Bug Report Template**:
```markdown
## Bug Description
[Clear description of the bug]

## Steps to Reproduce
1. Go to '...'
2. Click on '...'
3. See error

## Expected Behavior
[What should happen]

## Actual Behavior
[What actually happens]

## Environment
- OS: [Windows/macOS/Linux]
- Java Version: [24/25]
- Browser: [Chrome 120]

## Screenshots/Logs
[Attach relevant files]
```

**Submit**: [GitHub Issues](https://github.com/Jdsb06/SmartCampus/issues) with label `bug`

---

### Suggesting Features

**Feature Request Template**:
```markdown
## Feature Title
[Concise feature name]

## Problem Statement
[What problem does this solve?]

## Proposed Solution
[How should it work?]

## Alternatives Considered
[Other solutions you thought about]

## Additional Context
[Mockups, examples, references]
```

**Submit**: [GitHub Issues](https://github.com/Jdsb06/SmartCampus/issues) with label `feature-request`

---

### Contributing Code

1. **Find an Issue**: Browse [open issues](https://github.com/Jdsb06/SmartCampus/issues) or create one
2. **Comment**: Let others know you're working on it
3. **Fork Repository**: Click "Fork" on GitHub
4. **Create Branch**: Follow naming conventions (see below)
5. **Make Changes**: Follow coding standards
6. **Test**: Ensure all tests pass
7. **Submit PR**: Follow pull request template

---

## 🔄 Development Workflow

### Branch Naming Convention

| Type | Pattern | Example |
|------|---------|---------|
| **Feature** | `feature/<name>` | `feature/notification-system` |
| **Bug Fix** | `bugfix/<name>` | `bugfix/login-redirect-loop` |
| **Refactor** | `refactor/<name>` | `refactor/service-layer` |
| **Hotfix** | `hotfix/<name>` | `hotfix/csrf-vulnerability` |

### Git Workflow

```bash
# 1. Fork repository on GitHub

# 2. Clone your fork
git clone https://github.com/YOUR_USERNAME/SmartCampus.git
cd SmartCampus/smart-campus-backend

# 3. Add upstream remote
git remote add upstream https://github.com/Jdsb06/SmartCampus.git

# 4. Create feature branch
git checkout -b feature/notification-panel

# 5. Make changes and commit frequently
git add .
git commit -m "feat(notifications): add notification panel UI"

# 6. Pull latest changes from upstream
git fetch upstream
git rebase upstream/main

# 7. Push to your fork
git push origin feature/notification-panel

# 8. Create Pull Request on GitHub
```

---

## 📝 Coding Standards

### Java Code Style

**Follow**:
- Google Java Style Guide
- Use Lombok annotations (`@Data`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`)
- Maximum line length: 120 characters
- Indentation: 4 spaces

**Example**:
```java
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    
    @Override
    @Transactional
    public NotificationResponseDTO createNotification(NotificationRequestDTO dto) {
        User user = userRepository.findById(dto.getUserId())
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Notification notification = Notification.builder()
            .user(user)
            .title(dto.getTitle())
            .message(dto.getMessage())
            .type(dto.getType())
            .isRead(false)
            .build();
        
        Notification saved = notificationRepository.save(notification);
        return mapToResponseDTO(saved);
    }
}
```

---

### Naming Conventions

| Type | Convention | Example |
|------|------------|---------|
| **Classes** | PascalCase | `UserService`, `CourseController` |
| **Methods** | camelCase | `getUserById()`, `createCourse()` |
| **Variables** | camelCase | `userId`, `courseList` |
| **Constants** | UPPER_SNAKE_CASE | `MAX_CAPACITY`, `DEFAULT_ROLE` |
| **Packages** | lowercase | `com.sca.smartcampusbackend.service` |

---

### Package Structure

```
com.sca.smartcampusbackend
├── config          # Security, CORS, etc.
├── controller      # REST controllers
├── dto             # Data Transfer Objects
├── entity          # JPA entities
├── repository      # JPA repositories
├── service         # Business logic
│   └── impl        # Service implementations
├── exception       # Custom exceptions
└── util            # Utility classes
```

---

### Documentation Standards

**JavaDoc for Public Methods**:
```java
/**
 * Creates a new course offering for the specified semester.
 *
 * @param dto the course offering request data
 * @return the created course offering response
 * @throws ResourceNotFoundException if course or faculty not found
 * @throws ValidationException if capacity is invalid
 */
@Override
public CourseOfferingResponseDTO createOffering(CourseOfferingRequestDTO dto) {
    // Implementation
}
```

**Inline Comments** (when necessary):
```java
// Check if student is already enrolled to prevent duplicates
if (enrollmentRepository.existsByUserIdAndOfferingId(userId, offeringId)) {
    throw new DuplicateEnrollmentException("Already enrolled");
}
```

---

## 📦 Commit Message Guidelines

### Commit Message Format

```
<type>(<scope>): <subject>

<body>

<footer>
```

### Type

| Type | Description | Example |
|------|-------------|---------|
| `feat` | New feature | `feat(enrollment): add capacity validation` |
| `fix` | Bug fix | `fix(auth): resolve login redirect loop` |
| `docs` | Documentation | `docs(readme): update installation steps` |
| `style` | Code formatting | `style(service): format code with Prettier` |
| `refactor` | Code refactoring | `refactor(dashboard): extract query logic` |
| `test` | Add tests | `test(enrollment): add unit tests` |
| `chore` | Build/config changes | `chore(maven): update dependencies` |

### Scope

- `auth`, `enrollment`, `timetable`, `events`, `dashboard`, `faculty`, `courses`, `notifications`

### Subject

- Use imperative mood: "add" not "added" or "adds"
- Don't capitalize first letter
- No period at the end
- Max 50 characters

### Body (optional)

- Explain **what** and **why**, not **how**
- Wrap at 72 characters

### Footer (optional)

- Reference issues: `Closes #123` or `Fixes #45`

### Examples

```bash
# Feature
feat(notifications): add real-time notification delivery

Implemented WebSocket connection for push notifications.
Added NotificationController endpoint for SSE.

Closes #78

# Bug Fix
fix(enrollment): prevent enrollment when capacity full

Added validation check before creating enrollment.
Updated error message to be more descriptive.

Fixes #92

# Documentation
docs(api): add API documentation for events module

Created comprehensive API reference with examples.
Included request/response samples.
```

---

## 🔀 Pull Request Process

### Before Submitting PR

- [ ] Code follows style guidelines
- [ ] Self-review completed
- [ ] Comments added for complex logic
- [ ] Documentation updated
- [ ] No merge conflicts with `main`
- [ ] All tests passing
- [ ] No console errors/warnings

### PR Template

```markdown
## Description
[Brief description of changes]

## Type of Change
- [ ] Bug fix
- [ ] New feature
- [ ] Refactoring
- [ ] Documentation update

## Related Issue
Closes #[issue number]

## How Has This Been Tested?
[Describe testing approach]

## Screenshots (if applicable)
[Add screenshots]

## Checklist
- [ ] Code follows style guidelines
- [ ] Self-reviewed code
- [ ] Documentation updated
- [ ] Tests added/updated
- [ ] No breaking changes
```

### PR Review Process

1. **Submit PR**: Against `main` branch
2. **Automated Checks**: Wait for CI/CD (if configured)
3. **Code Review**: Team lead reviews within 48 hours
4. **Address Feedback**: Make requested changes
5. **Approval**: Minimum 1 approval required
6. **Merge**: Team lead merges using "Squash and Merge"

---

## 🧪 Testing Guidelines

### Unit Tests (Future Enhancement)

```java
@SpringBootTest
class EnrollmentServiceTest {
    
    @Autowired
    private EnrollmentService enrollmentService;
    
    @MockBean
    private EnrollmentRepository enrollmentRepository;
    
    @Test
    void testEnrollStudent_Success() {
        // Arrange
        EnrollmentRequestDTO dto = new EnrollmentRequestDTO(1L, 1L);
        
        // Act
        EnrollmentResponseDTO result = enrollmentService.enroll(dto);
        
        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getUser().getId());
    }
    
    @Test
    void testEnrollStudent_CapacityFull_ThrowsException() {
        // Test capacity validation
    }
}
```

### Manual Testing Checklist

Before submitting PR, manually test:

- [ ] Feature works as expected
- [ ] No console errors
- [ ] UI displays correctly
- [ ] Mobile responsive (if UI change)
- [ ] CSRF token included (if form/AJAX)
- [ ] Authorization works (student vs admin)
- [ ] Database updates correctly
- [ ] Error handling works

---

## 🎯 Development Best Practices

### 1. Keep PRs Small

- Focus on one feature/fix per PR
- Max 400 lines changed (excluding tests)
- Easier to review and merge

### 2. Write Meaningful Comments

```java
// BAD
int x = 10; // Set x to 10

// GOOD
int maxRetryAttempts = 10; // Maximum login retry attempts before lockout
```

### 3. Use Dependency Injection

```java
// GOOD
@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;
}

// BAD
public class CourseService {
    private CourseRepository courseRepository = new CourseRepository();
}
```

### 4. Handle Exceptions Properly

```java
@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            ex.getMessage(),
            LocalDateTime.now()
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
}
```

---

## 🏆 Recognition

Contributors will be:
- Listed in README.md
- Credited in release notes
- Mentioned in project acknowledgments

---

## 📞 Questions?

- **General**: Open discussion in [GitHub Discussions](https://github.com/Jdsb06/SmartCampus/discussions)
- **Code Review**: Comment on PR
- **Urgent**: Email team lead (see README)

---

**Thank you for contributing to Smart Campus Assistant! 🎓**

**Maintained by**: Team BholeChature  
**Last Updated**: February 2025
