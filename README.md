### API Authenticate

<details>
<summary><strong>Authentication Flow</strong></summary>

### 1. Register
```
Client → POST /api/v1/auth/register
         ↓
Server → Validate email/password
         ↓
Server → Create user + Generate tokens
         ↓
Client ← Access Token (15min) + Refresh Token (7 days)
```

### 2. Login
```
Client → POST /api/v1/auth/login
         ↓
Server → Verify credentials
         ↓
Server → Generate tokens
         ↓
Client ← Access Token + Refresh Token
```

### 3. Token Refresh
```
Client → POST /api/v1/auth/refresh-token (với refresh token cũ)
         ↓
Server → Validate refresh token
         ↓
Server → Generate NEW access token + NEW refresh token
         ↓
Server → Revoke refresh token cũ
         ↓
Client ← Access Token mới + Refresh Token mới
```

</details>


<details>
<summary><strong>Authorization Flow</strong></summary>

### 1. JWT Filter
```
Request → JWT Filter
         ↓
Filter → Extract token từ Authorization header
         ↓
Filter → Validate token signature + expiration
         ↓
Filter → Extract user info từ token
         ↓
Request → Controller với user context
```

### 2. Role-based Access
```java
// URL-level security
/api/v1/auth/**     → Permit All (không cần đăng nhập)
/api/v1/sessions/** → Authenticated users
/api/v1/database/** → ADMIN role only

// Method-level security
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<?> adminOnly() { ... }
```

### 3. Security Context
```java
// Lấy thông tin user hiện tại
Authentication auth = SecurityContextHolder.getContext().getAuthentication();
String username = auth.getName();
UserDetails user = (UserDetails) auth.getPrincipal();
```

</details>

<details>
<summary><strong>Token Management</strong></summary>

### Access Token (JWT)
- **Loại**: JWT (JSON Web Token)
- **Thời hạn**: 15 phút
- **Lưu trữ**: Client-side (localStorage/cookie)
- **Mục đích**: Xác thực API calls
- **Đặc điểm**: Stateless, chứa user info

### Refresh Token
- **Loại**: UUID string
- **Thời hạn**: 7 ngày
- **Lưu trữ**: Database (hashed)
- **Mục đích**: Lấy access token mới
- **Đặc điểm**: Stateful, có rotation

### Token Rotation
Mỗi lần refresh → tạo refresh token mới + vô hiệu hóa token cũ
- **Bảo mật**: Ngăn token reuse
- **Session isolation**: Mỗi device có session riêng
- **Compromise recovery**: Tự động revoke khi phát hiện compromise

</details>

<details>
<summary><strong>Authentication APIs</strong></summary>

### Public Endpoints
```http
POST /api/v1/auth/register    # Đăng ký user mới
POST /api/v1/auth/login       # Đăng nhập
POST /api/v1/auth/refresh-token # Refresh token
GET  /api/v1/auth/health      # Health check
```

### Protected Endpoints
```http
POST /api/v1/auth/logout      # Đăng xuất (cần access token)
POST /api/v1/auth/logout-all  # Đăng xuất tất cả sessions
GET  /api/v1/sessions/my-sessions # Xem sessions của user
DELETE /api/v1/sessions/revoke-family/{id} # Thu hồi session
DELETE /api/v1/sessions/revoke-all # Thu hồi tất cả sessions
```

### Admin Only
```http
GET /api/v1/database/users        # Xem tất cả users
GET /api/v1/database/refresh-tokens # Xem tất cả tokens
GET /api/v1/database/stats        # Thống kê database
```

</details>



## 🔧 Configuration

<details>
<summary><strong>Key Settings</strong></summary>

### application.properties
```properties
# JWT Configuration
jwt.secret=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
jwt.expiration=900000                    # 15 minutes
jwt.refresh-token.expiration=604800000   # 7 days

# Database
spring.datasource.url=jdbc:h2:mem:testdb
spring.h2.console.enabled=true

# Internationalization
spring.messages.basename=messages
spring.messages.encoding=UTF-8
```

</details>

## 🎯 Key Features

- ✅ **JWT Authentication**: Stateless authentication với JWT
- ✅ **Refresh Token Rotation**: Tự động rotate refresh token
- ✅ **Role-based Authorization**: Phân quyền theo USER/ADMIN
- ✅ **Session Management**: Quản lý multiple sessions
- ✅ **Centralized Error Handling**: Xử lý lỗi tập trung
- ✅ **Internationalization**: Hỗ trợ đa ngôn ngữ
- ✅ **Response Standardization**: Format response thống nhất


