# 🧪 API Testing with Postman - Smart Campus Assistant

**Simplified Guide for API Testing**

This guide demonstrates how to test **1-2 core APIs** using Postman. All other features can be demonstrated through the frontend web interface.

**APIs to Test:**
1. **GET /api/courses** - Retrieve all courses (Simple GET request)
2. **POST /api/courses** - Create a new course (POST with authentication)

---

## 📦 Step 1: Prerequisites

### Install Postman

**Option A: Desktop App (Recommended)**
1. Visit [https://www.postman.com/downloads/](https://www.postman.com/downloads/)
2. Download for your operating system (Windows/Mac/Linux)
3. Install and launch Postman
4. Create free account or skip (desktop works offline)

**Option B: Web Version**
1. Visit [https://www.postman.com/web](https://www.postman.com/web)
2. Sign in with Google/email
3. Use browser-based interface

---

## 🚀 Step 2: Start the Application

### Using Terminal/Command Prompt

**On Mac/Linux:**
```bash
cd /path/to/smart-campus-backend
./mvnw spring-boot:run
```

**On Windows:**
```cmd
cd C:\path\to\smart-campus-backend
mvnw.cmd spring-boot:run
```

### Wait for Application to Start

You should see in console:
```
Started SmartCampusBackendApplication in X.XXX seconds
```

**Application is now running at:** `http://localhost:8080`

**Do NOT close this terminal** - keep it running while testing APIs.

---

## 🔐 Step 3: Get Authentication (Session Cookie)

Our application uses **session-based authentication**. You need to login first to access protected APIs.

### Login via Browser (Easiest Method)

#### 3.1 Open Browser and Login

1. Open any web browser (Chrome, Firefox, Edge, Safari)
2. Go to: **http://localhost:8080/login**
3. Enter credentials:
   - **Username:** `admin`
   - **Password:** `admin123`
4. Click **Login** button
5. You should be redirected to dashboard

#### 3.2 Extract Session Cookie (JSESSIONID)

**For Chrome/Edge:**
1. Press **F12** to open Developer Tools
2. Click **Application** tab (top menu)
3. In left sidebar: **Storage** → **Cookies** → `http://localhost:8080`
4. Find row with Name: **JSESSIONID**
5. **Copy the entire Value** (looks like: `E3F2A1B9C8D7E6F5A4B3C2D1E0F9A8B7`)

**For Firefox:**
1. Press **F12** to open Developer Tools
2. Click **Storage** tab
3. Expand **Cookies** → `http://localhost:8080`
4. Find **JSESSIONID**
5. **Copy the Value**

**For Safari:**
1. Enable Developer Menu: Safari → Preferences → Advanced → "Show Develop menu"
2. Press **⌘⌥I** or Develop → Show Web Inspector
3. Click **Storage** tab
4. Cookies → `http://localhost:8080`
5. Find and copy **JSESSIONID** value

**Keep this JSESSIONID value** - you'll need it for Postman!

---

## 🧪 Step 4: Test API #1 - GET All Courses (Simple GET Request)

This is a **simple read operation** that retrieves all courses from the database.

### 4.1 Create New Request in Postman

1. Open Postman application
2. Click **"New"** button (top-left) or press **Ctrl+N** (Cmd+N on Mac)
3. Select **"HTTP Request"**
4. A new tab opens with "Untitled Request"

### 4.2 Configure the GET Request

**Step-by-step:**

1. **Set HTTP Method:**
   - Click dropdown (shows "GET" by default)
   - Keep it as **GET**

2. **Enter URL:**
   - In the URL field, type: `http://localhost:8080/api/courses`
   - Do NOT click Send yet!

3. **Add Authentication Cookie:**
   - Click **"Headers"** tab (below URL field)
   - Click **"Key"** field in first row
   - Type: `Cookie`
   - Click **"Value"** field
   - Type: `JSESSIONID=` followed by the value you copied earlier
   - Example: `JSESSIONID=E3F2A1B9C8D7E6F5A4B3C2D1E0F9A8B7`

Your Headers should look like:
```
Key             | Value
----------------|----------------------------------------
Cookie          | JSESSIONID=E3F2A1B9C8D7E6F5A4B3C2D1E0F9A8B7
```

### 4.3 Send the Request

1. Click the blue **"Send"** button
2. Wait 1-2 seconds

### 4.4 Verify Response

**You should see in the bottom panel:**

**Status:** `200 OK` (in green) - Success!

**Response Body (JSON):**
```json
[
  {
    "id": 1,
    "code": "CS101",
    "title": "Introduction to Programming",
    "description": "Fundamentals of programming using Java",
    "credits": 4,
    "department": "CSE",
    "semester": 1
  },
  {
    "id": 2,
    "code": "MATH101",
    "title": "Calculus I",
    "description": "Differential and integral calculus",
    "credits": 4,
    "department": "MATH",
    "semester": 1
  },
  ...more courses...
]
```

**Response Time:** Usually 50-500ms (shown at top-right)

**Size:** Shows data size (e.g., "2.3 KB")

### 4.5 Save the Request (Optional)

1. Click **"Save"** button (top-right)
2. Name it: `Get All Courses`
3. Create new collection: `Smart Campus APIs`
4. Click **"Save"**

---

## ✏️ Step 5: Test API #2 - POST Create Course (POST with Data)

This is a **write operation** that creates a new course in the database.

### 5.1 Get CSRF Token

For security, our application requires a **CSRF token** for POST/PUT/DELETE operations.

**Get CSRF Token from Browser:**

1. In browser (still logged in), go to any page (e.g., dashboard)
2. **Right-click** anywhere on page → **View Page Source** (or press Ctrl+U)
3. **Find this line** (use Ctrl+F to search):
   ```html
   <meta name="_csrf" content="abc123def456..." />
   ```
4. **Copy the content value** (the long string between quotes after `content=`)
   - Example: `d9e8f7a6-b5c4-3d2e-1a0b-9c8d7e6f5a4b`

**Keep this token** - you'll use it in the next step!

### 5.2 Create New POST Request

1. In Postman, click **"New"** → **"HTTP Request"**
2. Or click the **"+"** tab next to your previous request

### 5.3 Configure the POST Request

**Step-by-step:**

1. **Set HTTP Method:**
   - Click dropdown next to URL field
   - Select **POST**

2. **Enter URL:**
   - Type: `http://localhost:8080/api/courses`

3. **Add Headers:**
   - Click **"Headers"** tab
   - Add 3 headers:

   | Key | Value |
   |-----|-------|
   | `Cookie` | `JSESSIONID=<your-session-id>` |
   | `X-CSRF-TOKEN` | `<your-csrf-token>` |
   | `Content-Type` | `application/json` |

   Example:
   ```
   Cookie: JSESSIONID=E3F2A1B9C8D7E6F5A4B3C2D1E0F9A8B7
   X-CSRF-TOKEN: d9e8f7a6-b5c4-3d2e-1a0b-9c8d7e6f5a4b
   Content-Type: application/json
   ```

4. **Add Request Body:**
   - Click **"Body"** tab (below URL)
   - Select **"raw"** radio button
   - Dropdown on right: Select **"JSON"**
   - In text area, paste this JSON:

   ```json
   {
     "code": "CS401",
     "title": "Database Management Systems",
     "description": "Introduction to relational databases, SQL, and database design principles",
     "credits": 4,
     "department": "CSE",
     "semester": 4
   }
   ```

### 5.4 Send the Request

1. Double-check all headers are correct
2. Double-check JSON body is valid (no red underlines)
3. Click blue **"Send"** button

### 5.5 Verify Response

**Expected Success Response:**

**Status:** `201 Created` (in green)

**Response Body:**
```json
{
  "id": 15,
  "code": "CS401",
  "title": "Database Management Systems",
  "description": "Introduction to relational databases, SQL, and database design principles",
  "credits": 4,
  "department": "CSE",
  "semester": 4
}
```

**Note:** The `id` field is auto-generated by the database.

### 5.6 Verify in Browser

1. Go back to browser
2. Navigate to: **http://localhost:8080/admin/courses/list**
3. You should see your newly created course **"CS401 - Database Management Systems"** in the list!

---

## ✅ Success! You've Tested Both APIs

You have successfully:
- ✅ **GET /api/courses** - Retrieved all courses
- ✅ **POST /api/courses** - Created a new course

**All other features** (Dashboard, Timetable, Events, Enrollments, etc.) can be demonstrated through the **frontend web interface** at http://localhost:8080

---

## 🎯 Quick Reference Card

### API #1: GET All Courses

```
Method:  GET
URL:     http://localhost:8080/api/courses
Headers:
  Cookie: JSESSIONID=<your-session-id>

Expected: 200 OK with JSON array of courses
```

### API #2: POST Create Course

```
Method:  POST
URL:     http://localhost:8080/api/courses
Headers:
  Cookie: JSESSIONID=<your-session-id>
  X-CSRF-TOKEN: <your-csrf-token>
  Content-Type: application/json

Body (JSON):
{
  "code": "CS401",
  "title": "Database Management Systems",
  "description": "Introduction to relational databases...",
  "credits": 4,
  "department": "CSE",
  "semester": 4
}

Expected: 201 Created with created course JSON
```

---

## 🐛 Common Issues & Solutions

### ❌ Issue: 401 Unauthorized

**What it means:** You're not logged in or session expired

**How to fix:**
1. Go back to browser: http://localhost:8080/login
2. Login again with `admin` / `admin123`
3. Get fresh JSESSIONID from cookies (F12 → Application → Cookies)
4. Update Cookie header in Postman with new JSESSIONID

---

### ❌ Issue: 403 Forbidden

**What it means:** Missing or wrong CSRF token

**How to fix:**
1. Go to browser, view page source (Ctrl+U)
2. Search for `_csrf` meta tag
3. Copy the new token value
4. Update `X-CSRF-TOKEN` header in Postman

---

### ❌ Issue: 404 Not Found

**What it means:** Wrong URL or resource doesn't exist

**How to fix:**
- Check URL is exactly: `http://localhost:8080/api/courses` (not `/courses`)
- Check application is running (see terminal with Spring Boot logs)
- Verify course ID exists if you're trying to access specific course

---

### ❌ Issue: 400 Bad Request

**What it means:** Invalid data in request body

**How to fix:**
- Check JSON is valid (no red underlines in Postman)
- Verify all required fields are present (`code`, `title`, `description`, `credits`, `department`, `semester`)
- Check field types are correct (credits should be number, not string)

**Example of WRONG JSON:**
```json
{
  "code": "CS401",
  "title": "Database Systems"
  // Missing: description, credits, department, semester
}
```

**Example of CORRECT JSON:**
```json
{
  "code": "CS401",
  "title": "Database Systems",
  "description": "Full description here",
  "credits": 4,
  "department": "CSE",
  "semester": 4
}
```

---

### ❌ Issue: 500 Internal Server Error

**What it means:** Something went wrong on server

**How to fix:**
1. Check terminal where Spring Boot is running
2. Look for error messages (usually in red)
3. Common causes:
   - Database not connected
   - Duplicate course code (trying to create course that already exists)
   - Invalid foreign key references

---

## 📋 Complete Testing Checklist

**Before submitting/demonstrating:**

### Setup Checklist
- [ ] Postman installed and working
- [ ] Application running (`./mvnw spring-boot:run`)
- [ ] Logged in via browser as admin
- [ ] JSESSIONID copied from browser cookies
- [ ] CSRF token extracted from page source

### API #1: GET /api/courses
- [ ] Request created in Postman
- [ ] Method set to GET
- [ ] URL: `http://localhost:8080/api/courses`
- [ ] Cookie header added with JSESSIONID
- [ ] Sent successfully
- [ ] Received 200 OK status
- [ ] Response shows array of courses

### API #2: POST /api/courses
- [ ] Request created in Postman
- [ ] Method set to POST
- [ ] URL: `http://localhost:8080/api/courses`
- [ ] All 3 headers added (Cookie, X-CSRF-TOKEN, Content-Type)
- [ ] Body set to "raw" and "JSON"
- [ ] Valid JSON with all required fields
- [ ] Sent successfully
- [ ] Received 201 Created status
- [ ] Response shows created course with auto-generated ID
- [ ] Verified new course appears in browser at /admin/courses/list

---

## 🎓 For TA/Instructor Demonstration

### Recommended Demo Flow:

**1. Show Application Running (30 seconds)**
- Terminal with Spring Boot logs
- Point out "Started SmartCampusBackendApplication"

**2. Demonstrate GET Request (1 minute)**
- Open Postman
- Show GET /api/courses request
- Highlight Cookie header
- Click Send
- Show 200 OK response with course data

**3. Demonstrate POST Request (2 minutes)**
- Create new POST /api/courses request
- Show all 3 headers (Cookie, CSRF, Content-Type)
- Show JSON body with course data
- Click Send
- Show 201 Created response
- Switch to browser, navigate to admin courses list
- Show newly created course in the table

**4. Show Frontend (remaining time)**
- All other features (Dashboard, Timetable, Events, Enrollments) demonstrated via web UI
- No need to test every API in Postman - frontend proves they work

**Total API testing time: ~3-4 minutes**  
**Frontend demonstration: Remaining time**

---

## 📥 Import Ready-Made Postman Collection

**Skip manual setup!** We've created a ready-to-use Postman collection.

### Quick Import Steps:

1. **Download the collection file:**
   - Located at project root: `Smart_Campus_API_Tests.postman_collection.json`
   - Or from GitHub: [Download Link](../Smart_Campus_API_Tests.postman_collection.json)

2. **Import to Postman:**
   - Open Postman
   - Click **Import** button (top-left)
   - Drag & drop the JSON file, or click **Upload Files**
   - Click **Import**

3. **Update Variables:**
   - Click **Smart Campus API Tests** collection
   - Go to **Variables** tab
   - Update these values:
     - `base_url`: `http://localhost:8080` (already set)
     - `session_id`: Paste your JSESSIONID (from Step 3 above)
     - `csrf_token`: Paste your CSRF token (from Step 5 above)
   - Click **Save**

4. **Test the APIs:**
   - Expand the collection
   - Click **"1. GET All Courses"** → Click **Send**
   - Click **"2. POST Create Course"** → Click **Send**

**That's it!** Both APIs are now ready to test with one click.

---

## 📞 Additional Resources

**For more details on all 115 endpoints:**
- See [API_REFERENCE.md](API_REFERENCE.md) - Complete API documentation

**For frontend features:**
- All features accessible at: http://localhost:8080
- Admin panel: http://localhost:8080/admin/*
- Student pages: Dashboard, Timetable, Events, Enrollments

**For troubleshooting:**
- See [TROUBLESHOOTING.md](TROUBLESHOOTING.md)

---

**Last Updated**: December 12, 2025  
**Maintained by**: Team BholeChature  
**IIIT Bangalore | December 2025**
