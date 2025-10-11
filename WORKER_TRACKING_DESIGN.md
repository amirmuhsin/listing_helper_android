# Worker Tracking & Statistics System Design

## Overview
Track worker productivity by monitoring completed product listings (status = DONE) with time-based aggregations (today, yesterday, week). Workers log in once and stay authenticated. Statistics are displayed in the app with a new bottom navigation structure: Home / Stats / Account.

---

## Backend Specification

### 1. Database Schema Changes

```sql
-- Users/Workers table
CREATE TABLE workers (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(200),
    email VARCHAR(255),
    role ENUM('worker', 'admin', 'supervisor') DEFAULT 'worker',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    last_login TIMESTAMP NULL
);

-- Auth tokens for mobile sessions
CREATE TABLE auth_tokens (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    worker_id BIGINT NOT NULL,
    token VARCHAR(255) UNIQUE NOT NULL,
    device_id VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NOT NULL,
    last_used_at TIMESTAMP NULL,
    FOREIGN KEY (worker_id) REFERENCES workers(id) ON DELETE CASCADE,
    INDEX idx_token (token),
    INDEX idx_worker (worker_id)
);

-- Modify existing products table to track worker
ALTER TABLE products ADD COLUMN worker_id BIGINT NULL;
ALTER TABLE products ADD COLUMN completed_at TIMESTAMP NULL;
ALTER TABLE products ADD FOREIGN KEY (worker_id) REFERENCES workers(id) ON DELETE SET NULL;
ALTER TABLE products ADD INDEX idx_worker_status (worker_id, status, completed_at);
```

### 2. API Endpoints

#### **Authentication**

**Login**
```http
POST /api/v1/auth/login
Content-Type: application/json

Request:
{
  "username": "john_worker",
  "password": "secure_password",
  "device_id": "android_device_12345"
}

Response (200 OK):
{
  "success": true,
  "data": {
    "worker_id": 42,
    "username": "john_worker",
    "full_name": "John Smith",
    "role": "worker",
    "token": "eyJhbGciOiJIUzI1NiIs...",
    "expires_at": "2025-11-11T10:00:00Z"
  }
}

Response (401 Unauthorized):
{
  "success": false,
  "error": "Invalid credentials"
}
```

**Logout**
```http
POST /api/v1/auth/logout
Authorization: Bearer {token}

Response (200 OK):
{
  "success": true,
  "message": "Logged out successfully"
}
```

**Get Current Worker**
```http
GET /api/v1/auth/me
Authorization: Bearer {token}

Response (200 OK):
{
  "success": true,
  "data": {
    "worker_id": 42,
    "username": "john_worker",
    "full_name": "John Smith",
    "email": "john@example.com",
    "role": "worker"
  }
}
```

#### **Statistics**

**Summary Stats**
```http
GET /api/v1/stats/summary
Authorization: Bearer {token}

Response (200 OK):
{
  "success": true,
  "data": {
    "today": {
      "count": 15,
      "date": "2025-10-11"
    },
    "yesterday": {
      "count": 23,
      "date": "2025-10-10"
    },
    "week": {
      "count": 87,
      "start_date": "2025-10-05",
      "end_date": "2025-10-11"
    },
    "month": {
      "count": 342,
      "start_date": "2025-10-01",
      "end_date": "2025-10-11"
    },
    "all_time": {
      "count": 1250
    }
  }
}

Backend Logic:
- Extract worker_id from Bearer token
- Count products WHERE worker_id = {worker_id} AND status = 'DONE'
- Today: completed_at >= start_of_today
- Yesterday: completed_at BETWEEN start_of_yesterday AND end_of_yesterday
- Week: completed_at >= (today - 7 days)
- Month: completed_at >= start_of_current_month
- All time: all completed products
```

**Leaderboard**
```http
GET /api/v1/stats/leaderboard?period=week
Authorization: Bearer {token}
Query Parameters:
  - period: today|yesterday|week|month|all_time

Response (200 OK):
{
  "success": true,
  "data": {
    "period": "week",
    "start_date": "2025-10-05",
    "end_date": "2025-10-11",
    "leaderboard": [
      {
        "rank": 1,
        "worker_id": 42,
        "worker_name": "John Smith",
        "count": 87,
        "is_current_user": true
      },
      {
        "rank": 2,
        "worker_id": 15,
        "worker_name": "Sarah Jones",
        "count": 76,
        "is_current_user": false
      },
      {
        "rank": 3,
        "worker_id": 8,
        "worker_name": "Mike Brown",
        "count": 65,
        "is_current_user": false
      }
    ]
  }
}

Backend Logic:
- Group by worker_id, count products with status = DONE
- Order by count DESC
- Mark is_current_user = true for the authenticated user
```

**Detailed Product List**
```http
GET /api/v1/stats/details?period=week&page=1&limit=50
Authorization: Bearer {token}
Query Parameters:
  - period: today|yesterday|week|month
  - page: int (default: 1)
  - limit: int (default: 50)

Response (200 OK):
{
  "success": true,
  "data": {
    "period": "week",
    "total_count": 87,
    "products": [
      {
        "id": 12345,
        "sku": "PROD-001",
        "name": "Product Name",
        "completed_at": "2025-10-11T14:30:00Z",
        "image_count": 8
      },
      // ... more products
    ],
    "pagination": {
      "page": 1,
      "limit": 50,
      "total_pages": 2,
      "total_items": 87
    }
  }
}

Backend Logic:
- Fetch products for current worker with status = DONE
- Filter by period date range
- Paginate results
```

#### **Product Updates** (modify existing endpoint)

```http
POST /api/v1/products/{id}/status
Authorization: Bearer {token}
Content-Type: application/json

Request:
{
  "status": "DONE"
}

Backend Logic:
- Extract worker_id from Bearer token
- If status changes to DONE:
  - Set product.worker_id = current_worker_id (if not already set)
  - Set product.completed_at = NOW() (if not already set)
- Update product.status

Response (200 OK):
{
  "success": true,
  "data": {
    "id": 12345,
    "status": "DONE",
    "completed_at": "2025-10-11T14:30:00Z"
  }
}
```

### 3. Authentication Flow

1. User enters username/password → POST /api/v1/auth/login
2. Backend validates credentials
3. Backend generates JWT token (expires in 30 days)
4. Backend stores token in auth_tokens table
5. Backend returns token + worker info
6. Android stores token in SharedPreferences
7. All subsequent API calls include: `Authorization: Bearer {token}`
8. On 401 response → Clear local token → Navigate to LoginActivity

---

## Android Specification

### 1. Data Layer

#### **Domain Models**

```kotlin
// domain/auth/Worker.kt
package com.amirmuhsin.listinghelper.domain.auth

data class Worker(
    val id: Long,
    val username: String,
    val fullName: String?,
    val email: String?,
    val role: WorkerRole
)

enum class WorkerRole {
    WORKER, ADMIN, SUPERVISOR
}

// domain/auth/AuthToken.kt
package com.amirmuhsin.listinghelper.domain.auth

data class AuthToken(
    val token: String,
    val expiresAt: String, // ISO 8601 timestamp
    val worker: Worker
)

// domain/stats/Stats.kt
package com.amirmuhsin.listinghelper.domain.stats

data class ProductStats(
    val today: PeriodStats,
    val yesterday: PeriodStats,
    val week: PeriodStats,
    val month: PeriodStats? = null,
    val allTime: AllTimeStats? = null
)

data class PeriodStats(
    val count: Int,
    val date: String? = null,      // for today/yesterday
    val startDate: String? = null, // for week/month
    val endDate: String? = null
)

data class AllTimeStats(
    val count: Int
)

// domain/stats/Leaderboard.kt
package com.amirmuhsin.listinghelper.domain.stats

data class LeaderboardEntry(
    val rank: Int,
    val workerId: Long,
    val workerName: String,
    val count: Int,
    val isCurrentUser: Boolean
)

data class LeaderboardResponse(
    val period: String,
    val startDate: String?,
    val endDate: String?,
    val leaderboard: List<LeaderboardEntry>
)
```

#### **Network Layer**

```kotlin
// data/networking/api/AuthService.kt
package com.amirmuhsin.listinghelper.data.networking.api

import com.amirmuhsin.listinghelper.data.networking.model.ApiResponse
import com.amirmuhsin.listinghelper.domain.auth.AuthToken
import com.amirmuhsin.listinghelper.domain.auth.Worker
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthService {
    @POST("v1/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<ApiResponse<AuthToken>>

    @POST("v1/auth/logout")
    suspend fun logout(): Response<ApiResponse<Unit>>

    @GET("v1/auth/me")
    suspend fun getCurrentWorker(): Response<ApiResponse<Worker>>
}

data class LoginRequest(
    val username: String,
    val password: String,
    val deviceId: String
)

// data/networking/api/StatsService.kt
package com.amirmuhsin.listinghelper.data.networking.api

import com.amirmuhsin.listinghelper.data.networking.model.ApiResponse
import com.amirmuhsin.listinghelper.domain.stats.LeaderboardResponse
import com.amirmuhsin.listinghelper.domain.stats.ProductStats
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface StatsService {
    @GET("v1/stats/summary")
    suspend fun getStatsSummary(): Response<ApiResponse<ProductStats>>

    @GET("v1/stats/leaderboard")
    suspend fun getLeaderboard(
        @Query("period") period: String // today|yesterday|week|month|all_time
    ): Response<ApiResponse<LeaderboardResponse>>
}

// data/networking/model/ApiResponse.kt (if not exists)
package com.amirmuhsin.listinghelper.data.networking.model

data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val error: String? = null
)
```

#### **Local Storage**

```kotlin
// data/prefs/AuthPreferences.kt
package com.amirmuhsin.listinghelper.data.prefs

import android.content.Context

class AuthPreferences(context: Context) {
    private val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    var authToken: String?
        get() = prefs.getString("auth_token", null)
        set(value) = prefs.edit().putString("auth_token", value).apply()

    var tokenExpiresAt: String?
        get() = prefs.getString("token_expires_at", null)
        set(value) = prefs.edit().putString("token_expires_at", value).apply()

    var workerId: Long
        get() = prefs.getLong("worker_id", -1L)
        set(value) = prefs.edit().putLong("worker_id", value).apply()

    var workerName: String?
        get() = prefs.getString("worker_name", null)
        set(value) = prefs.edit().putString("worker_name", value).apply()

    var workerUsername: String?
        get() = prefs.getString("worker_username", null)
        set(value) = prefs.edit().putString("worker_username", value).apply()

    var workerRole: String?
        get() = prefs.getString("worker_role", null)
        set(value) = prefs.edit().putString("worker_role", value).apply()

    fun saveAuthToken(token: AuthToken) {
        authToken = token.token
        tokenExpiresAt = token.expiresAt
        workerId = token.worker.id
        workerName = token.worker.fullName
        workerUsername = token.worker.username
        workerRole = token.worker.role.name
    }

    fun clear() {
        prefs.edit().clear().apply()
    }

    fun isLoggedIn(): Boolean {
        return !authToken.isNullOrEmpty() && workerId != -1L
    }
}
```

#### **Interceptor for Auth Token**

```kotlin
// data/networking/AuthInterceptor.kt
package com.amirmuhsin.listinghelper.data.networking

import com.amirmuhsin.listinghelper.data.prefs.AuthPreferences
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val authPreferences: AuthPreferences
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Skip auth for login endpoint
        if (originalRequest.url.encodedPath.contains("/auth/login")) {
            return chain.proceed(originalRequest)
        }

        val token = authPreferences.authToken
        if (token.isNullOrEmpty()) {
            return chain.proceed(originalRequest)
        }

        val newRequest = originalRequest.newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .build()

        return chain.proceed(newRequest)
    }
}
```

#### **Repositories**

```kotlin
// domain/auth/AuthRepository.kt
package com.amirmuhsin.listinghelper.domain.auth

import com.amirmuhsin.listinghelper.core_views.result.Result

interface AuthRepository {
    suspend fun login(username: String, password: String, deviceId: String): Result<AuthToken>
    suspend fun logout(): Result<Unit>
    suspend fun getCurrentWorker(): Result<Worker>
    fun isLoggedIn(): Boolean
    fun clearAuth()
}

// repository/AuthRepositoryImpl.kt
package com.amirmuhsin.listinghelper.repository

import com.amirmuhsin.listinghelper.core_views.result.Result
import com.amirmuhsin.listinghelper.data.networking.api.AuthService
import com.amirmuhsin.listinghelper.data.networking.api.LoginRequest
import com.amirmuhsin.listinghelper.data.prefs.AuthPreferences
import com.amirmuhsin.listinghelper.domain.auth.AuthRepository
import com.amirmuhsin.listinghelper.domain.auth.AuthToken
import com.amirmuhsin.listinghelper.domain.auth.Worker

class AuthRepositoryImpl(
    private val authService: AuthService,
    private val authPreferences: AuthPreferences
) : AuthRepository {

    override suspend fun login(username: String, password: String, deviceId: String): Result<AuthToken> {
        return Result.runCatching {
            val request = LoginRequest(username, password, deviceId)
            val response = authService.login(request)

            if (!response.isSuccessful) {
                throw retrofit2.HttpException(response)
            }

            val body = response.body() ?: throw IllegalStateException("Response body is null")
            if (!body.success || body.data == null) {
                throw IllegalStateException(body.error ?: "Login failed")
            }

            // Save auth token locally
            authPreferences.saveAuthToken(body.data)

            body.data
        }
    }

    override suspend fun logout(): Result<Unit> {
        return Result.runCatching {
            val response = authService.logout()

            // Clear local auth regardless of server response
            authPreferences.clear()

            if (!response.isSuccessful) {
                throw retrofit2.HttpException(response)
            }
        }
    }

    override suspend fun getCurrentWorker(): Result<Worker> {
        return Result.runCatching {
            val response = authService.getCurrentWorker()

            if (!response.isSuccessful) {
                throw retrofit2.HttpException(response)
            }

            val body = response.body() ?: throw IllegalStateException("Response body is null")
            if (!body.success || body.data == null) {
                throw IllegalStateException(body.error ?: "Failed to get worker info")
            }

            body.data
        }
    }

    override fun isLoggedIn(): Boolean {
        return authPreferences.isLoggedIn()
    }

    override fun clearAuth() {
        authPreferences.clear()
    }
}

// domain/stats/StatsRepository.kt
package com.amirmuhsin.listinghelper.domain.stats

import com.amirmuhsin.listinghelper.core_views.result.Result

interface StatsRepository {
    suspend fun getStatsSummary(): Result<ProductStats>
    suspend fun getLeaderboard(period: String): Result<LeaderboardResponse>
}

// repository/StatsRepositoryImpl.kt
package com.amirmuhsin.listinghelper.repository

import com.amirmuhsin.listinghelper.core_views.result.Result
import com.amirmuhsin.listinghelper.data.networking.api.StatsService
import com.amirmuhsin.listinghelper.domain.stats.LeaderboardResponse
import com.amirmuhsin.listinghelper.domain.stats.ProductStats
import com.amirmuhsin.listinghelper.domain.stats.StatsRepository

class StatsRepositoryImpl(
    private val statsService: StatsService
) : StatsRepository {

    override suspend fun getStatsSummary(): Result<ProductStats> {
        return Result.runCatching {
            val response = statsService.getStatsSummary()

            if (!response.isSuccessful) {
                throw retrofit2.HttpException(response)
            }

            val body = response.body() ?: throw IllegalStateException("Response body is null")
            if (!body.success || body.data == null) {
                throw IllegalStateException(body.error ?: "Failed to get stats")
            }

            body.data
        }
    }

    override suspend fun getLeaderboard(period: String): Result<LeaderboardResponse> {
        return Result.runCatching {
            val response = statsService.getLeaderboard(period)

            if (!response.isSuccessful) {
                throw retrofit2.HttpException(response)
            }

            val body = response.body() ?: throw IllegalStateException("Response body is null")
            if (!body.success || body.data == null) {
                throw IllegalStateException(body.error ?: "Failed to get leaderboard")
            }

            body.data
        }
    }
}
```

### 2. UI Layer

#### **Navigation Structure**

```
App Start
    ↓
Check isLoggedIn()
    ↓
┌───────────┴───────────┐
│ NO                    │ YES
↓                       ↓
LoginActivity    MainActivity with BottomNavigationView
                        ├── HomeFragment (existing product list)
                        ├── StatsFragment (NEW - statistics)
                        └── AccountFragment (NEW - profile)
```

#### **New Activities/Fragments**

**1. LoginActivity**
- Location: `ui/auth/LoginActivity.kt`
- Layout: `activity_login.xml`
- Features:
  - Username EditText
  - Password EditText (password input type)
  - Login Button
  - Loading indicator (ProgressBar)
  - Error message display (TextView)
- ViewModel: `LoginViewModel`
- Navigation: On success → Navigate to MainActivity with FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TASK

**2. StatsFragment**
- Location: `ui/stats/StatsFragment.kt`
- Layout: `fragment_stats.xml`
- Features:
  - Stats summary cards (CardViews for Today, Yesterday, Week)
  - Optional: Leaderboard section (top 3 workers)
  - SwipeRefreshLayout for pull-to-refresh
  - Empty state when no data
- ViewModel: `StatsViewModel`

**3. AccountFragment**
- Location: `ui/account/AccountFragment.kt`
- Layout: `fragment_account.xml`
- Features:
  - Worker name display
  - Username display
  - Role display
  - Logout button
  - Optional: Settings section
- ViewModel: `AccountViewModel`

#### **Updated MainActivity**

```kotlin
// ui/common/main/MainActivity.kt - UPDATE
class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>(
    ActivityMainBinding::inflate
) {
    override val viewModel: MainViewModel by viewModel()

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if user is logged in
        val authRepo: AuthRepository by inject()
        if (!authRepo.isLoggedIn()) {
            navigateToLogin()
            return
        }

        setupBottomNavigation()
        handleSharedImages()
    }

    private fun setupBottomNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        binding.bottomNav.setupWithNavController(navController)
    }

    private fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    // ... existing code
}
```

#### **Layout Changes**

**activity_main.xml - UPDATE**
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <androidx.fragment.app.FragmentContainerView
        android:id="@+id/nav_host_fragment"
        android:name="androidx.navigation.fragment.NavHostFragment"
        android:layout_width="0dp"
        android:layout_height="0dp"
        app:defaultNavHost="true"
        app:layout_constraintBottom_toTopOf="@id/bottom_nav"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:navGraph="@navigation/nav_main_graph" />

    <com.google.android.material.bottomnavigation.BottomNavigationView
        android:id="@+id/bottom_nav"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:background="?attr/colorSurface"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:menu="@menu/bottom_nav_menu" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

**menu/bottom_nav_menu.xml - NEW**
```xml
<?xml version="1.0" encoding="utf-8"?>
<menu xmlns:android="http://schemas.android.com/apk/res/android">
    <item
        android:id="@+id/homeFragment"
        android:icon="@drawable/ic_home"
        android:title="Home" />

    <item
        android:id="@+id/statsFragment"
        android:icon="@drawable/ic_stats"
        android:title="Stats" />

    <item
        android:id="@+id/accountFragment"
        android:icon="@drawable/ic_account"
        android:title="Account" />
</menu>
```

**fragment_stats.xml - NEW**
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.swiperefreshlayout.widget.SwipeRefreshLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:id="@+id/swipe_refresh"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <androidx.core.widget.NestedScrollView
        android:layout_width="match_parent"
        android:layout_height="match_parent">

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="vertical"
            android:padding="16dp">

            <TextView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="Your Statistics"
                android:textSize="24sp"
                android:textStyle="bold"
                android:layout_marginBottom="16dp" />

            <!-- Today Card -->
            <com.google.android.material.card.MaterialCardView
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:layout_marginBottom="12dp"
                app:cardElevation="2dp"
                app:cardCornerRadius="8dp">

                <LinearLayout
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:orientation="vertical"
                    android:padding="16dp">

                    <TextView
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:text="Today"
                        android:textSize="14sp"
                        android:textColor="?android:attr/textColorSecondary" />

                    <TextView
                        android:id="@+id/tv_today_count"
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:text="0"
                        android:textSize="32sp"
                        android:textStyle="bold"
                        android:textColor="?attr/colorPrimary" />

                </LinearLayout>
            </com.google.android.material.card.MaterialCardView>

            <!-- Yesterday Card -->
            <com.google.android.material.card.MaterialCardView
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:layout_marginBottom="12dp"
                app:cardElevation="2dp"
                app:cardCornerRadius="8dp">

                <LinearLayout
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:orientation="vertical"
                    android:padding="16dp">

                    <TextView
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:text="Yesterday"
                        android:textSize="14sp"
                        android:textColor="?android:attr/textColorSecondary" />

                    <TextView
                        android:id="@+id/tv_yesterday_count"
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:text="0"
                        android:textSize="32sp"
                        android:textStyle="bold" />

                </LinearLayout>
            </com.google.android.material.card.MaterialCardView>

            <!-- Week Card -->
            <com.google.android.material.card.MaterialCardView
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:layout_marginBottom="12dp"
                app:cardElevation="2dp"
                app:cardCornerRadius="8dp">

                <LinearLayout
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:orientation="vertical"
                    android:padding="16dp">

                    <TextView
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:text="This Week"
                        android:textSize="14sp"
                        android:textColor="?android:attr/textColorSecondary" />

                    <TextView
                        android:id="@+id/tv_week_count"
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:text="0"
                        android:textSize="32sp"
                        android:textStyle="bold" />

                </LinearLayout>
            </com.google.android.material.card.MaterialCardView>

            <!-- Progress Indicator -->
            <ProgressBar
                android:id="@+id/progress_bar"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_gravity="center"
                android:visibility="gone" />

        </LinearLayout>

    </androidx.core.widget.NestedScrollView>

</androidx.swiperefreshlayout.widget.SwipeRefreshLayout>
```

**fragment_account.xml - NEW**
```xml
<?xml version="1.0" encoding="utf-8"?>
<ScrollView xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:padding="16dp">

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Account"
            android:textSize="24sp"
            android:textStyle="bold"
            android:layout_marginBottom="24dp" />

        <!-- Profile Card -->
        <com.google.android.material.card.MaterialCardView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginBottom="16dp"
            app:cardElevation="2dp"
            app:cardCornerRadius="8dp">

            <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:orientation="vertical"
                android:padding="16dp">

                <TextView
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:text="Profile"
                    android:textSize="18sp"
                    android:textStyle="bold"
                    android:layout_marginBottom="12dp" />

                <TextView
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:text="Name"
                    android:textSize="12sp"
                    android:textColor="?android:attr/textColorSecondary" />

                <TextView
                    android:id="@+id/tv_worker_name"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:text="-"
                    android:textSize="16sp"
                    android:layout_marginBottom="12dp" />

                <TextView
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:text="Username"
                    android:textSize="12sp"
                    android:textColor="?android:attr/textColorSecondary" />

                <TextView
                    android:id="@+id/tv_username"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:text="-"
                    android:textSize="16sp"
                    android:layout_marginBottom="12dp" />

                <TextView
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:text="Role"
                    android:textSize="12sp"
                    android:textColor="?android:attr/textColorSecondary" />

                <TextView
                    android:id="@+id/tv_role"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:text="-"
                    android:textSize="16sp" />

            </LinearLayout>
        </com.google.android.material.card.MaterialCardView>

        <!-- Logout Button -->
        <Button
            android:id="@+id/btn_logout"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Logout"
            android:textColor="@android:color/white"
            app:backgroundTint="@color/design_default_color_error" />

    </LinearLayout>

</ScrollView>
```

### 3. Koin Modules Update

```kotlin
// di/DataModule.kt - NEW
package com.amirmuhsin.listinghelper.di

import com.amirmuhsin.listinghelper.data.prefs.AuthPreferences
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataModule = module {
    single {
        AuthPreferences(androidContext())
    }
}

// di/NetworkModule.kt - UPDATE
package com.amirmuhsin.listinghelper.di

import com.amirmuhsin.listinghelper.data.networking.AuthInterceptor
import com.amirmuhsin.listinghelper.data.networking.ProductNetworkModule
import com.amirmuhsin.listinghelper.data.networking.api.AuthService
import com.amirmuhsin.listinghelper.data.networking.api.ImageService
import com.amirmuhsin.listinghelper.data.networking.api.ProductService
import com.amirmuhsin.listinghelper.data.networking.api.StatsService
import org.koin.dsl.module

val networkModule = module {
    single {
        AuthInterceptor(authPreferences = get())
    }

    single<ProductService> {
        ProductNetworkModule.productService
    }

    single<ImageService> {
        ProductNetworkModule.imageService
    }

    // NEW
    single<AuthService> {
        ProductNetworkModule.retrofit.create(AuthService::class.java)
    }

    // NEW
    single<StatsService> {
        ProductNetworkModule.retrofit.create(StatsService::class.java)
    }
}

// di/RepositoryModule.kt - UPDATE
package com.amirmuhsin.listinghelper.di

import com.amirmuhsin.listinghelper.domain.auth.AuthRepository
import com.amirmuhsin.listinghelper.domain.stats.StatsRepository
import com.amirmuhsin.listinghelper.repository.AuthRepositoryImpl
import com.amirmuhsin.listinghelper.repository.StatsRepositoryImpl
import org.koin.dsl.module

val repositoryModule = module {
    // ... existing repositories

    // NEW
    single<AuthRepository> {
        AuthRepositoryImpl(
            authService = get(),
            authPreferences = get()
        )
    }

    // NEW
    single<StatsRepository> {
        StatsRepositoryImpl(
            statsService = get()
        )
    }
}

// di/ViewModelModule.kt - UPDATE
package com.amirmuhsin.listinghelper.di

import com.amirmuhsin.listinghelper.ui.auth.LoginViewModel
import com.amirmuhsin.listinghelper.ui.stats.StatsViewModel
import com.amirmuhsin.listinghelper.ui.account.AccountViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    // ... existing ViewModels

    // NEW
    viewModel {
        LoginViewModel(
            authRepository = get()
        )
    }

    // NEW
    viewModel {
        StatsViewModel(
            statsRepository = get()
        )
    }

    // NEW
    viewModel {
        AccountViewModel(
            authRepository = get()
        )
    }
}

// ListingHelperApp.kt - UPDATE
package com.amirmuhsin.listinghelper

import android.app.Application
import com.amirmuhsin.listinghelper.di.*
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class ListingHelperApp : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@ListingHelperApp)
            modules(
                dataModule,         // NEW
                databaseModule,
                networkModule,
                repositoryModule,
                viewModelModule
            )
        }
    }
}
```

### 4. ProductNetworkModule Update (Add AuthInterceptor)

```kotlin
// data/networking/ProductNetworkModule.kt - UPDATE
package com.amirmuhsin.listinghelper.data.networking

// Add AuthInterceptor to OkHttpClient
// This requires refactoring to inject AuthInterceptor via Koin
// See implementation notes below
```

### 5. Key Implementation Details

#### **Device ID Generation**
```kotlin
// util/DeviceUtils.kt - NEW
package com.amirmuhsin.listinghelper.util

import android.content.Context
import android.provider.Settings
import java.util.UUID

object DeviceUtils {
    fun getDeviceId(context: Context): String {
        // Use Android ID as device identifier
        val androidId = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        )
        return androidId ?: UUID.randomUUID().toString()
    }
}
```

#### **401 Unauthorized Handling**
```kotlin
// Create a response interceptor to handle 401
// data/networking/UnauthorizedInterceptor.kt - NEW
package com.amirmuhsin.listinghelper.data.networking

import com.amirmuhsin.listinghelper.data.prefs.AuthPreferences
import okhttp3.Interceptor
import okhttp3.Response

class UnauthorizedInterceptor(
    private val authPreferences: AuthPreferences
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())

        if (response.code == 401) {
            // Clear auth and trigger navigation to login
            authPreferences.clear()
            // Note: Actual navigation should be handled in ViewModel/Activity
        }

        return response
    }
}
```

---

## Implementation Priority

### Phase 1: Core Authentication (MVP)
**Goal:** Workers can log in and stay authenticated

1. **Backend:**
   - Create `workers` table
   - Create `auth_tokens` table
   - Implement `POST /api/v1/auth/login`
   - Implement `POST /api/v1/auth/logout`
   - Implement JWT token generation/validation

2. **Android:**
   - Create `AuthPreferences`
   - Create `AuthService` + `AuthRepository`
   - Create `LoginActivity` + `LoginViewModel`
   - Update `MainActivity` to check auth state
   - Add `AuthInterceptor` to network layer
   - Test login → home flow

### Phase 2: Statistics Display
**Goal:** Workers see their productivity stats

1. **Backend:**
   - Alter `products` table (add `worker_id`, `completed_at`)
   - Implement `GET /api/v1/stats/summary`
   - Update product completion logic to track worker

2. **Android:**
   - Create `StatsService` + `StatsRepository`
   - Create `StatsFragment` + `StatsViewModel`
   - Add bottom navigation to `MainActivity`
   - Implement stats display with cards
   - Add pull-to-refresh

### Phase 3: Account Management
**Goal:** Workers view profile and logout

1. **Android:**
   - Create `AccountFragment` + `AccountViewModel`
   - Display worker info from `AuthPreferences`
   - Implement logout flow
   - Update bottom navigation

### Phase 4: Enhanced Stats (Optional)
**Goal:** Leaderboard and detailed views

1. **Backend:**
   - Implement `GET /api/v1/stats/leaderboard`
   - Implement `GET /api/v1/stats/details`

2. **Android:**
   - Add leaderboard to `StatsFragment`
   - Add detailed product list view
   - Add charts/graphs (optional)

### Phase 5: HomeFragment Stats Banner (Optional)
**Goal:** Show quick stats in home screen

1. **Android:**
   - Add stats summary banner to `HomeFragment`
   - Fetch stats on fragment resume
   - Display today/week counts

---

## Testing Checklist

### Backend Testing
- [ ] Worker can register/be created
- [ ] Worker can login with valid credentials
- [ ] Login fails with invalid credentials
- [ ] Auth token is generated and stored
- [ ] Auth token validates correctly
- [ ] Stats count products with status = DONE only
- [ ] Stats filter by worker_id correctly
- [ ] Stats calculate date ranges correctly (today, yesterday, week)
- [ ] Product completion updates worker_id and completed_at
- [ ] 401 response when token is invalid/expired

### Android Testing
- [ ] App shows LoginActivity when not logged in
- [ ] Login succeeds with valid credentials
- [ ] Login fails with invalid credentials (show error)
- [ ] Auth token is saved to SharedPreferences
- [ ] MainActivity loads when logged in
- [ ] Bottom navigation switches between tabs
- [ ] Stats fetch and display correctly
- [ ] Stats pull-to-refresh works
- [ ] Account displays worker info
- [ ] Logout clears auth and navigates to LoginActivity
- [ ] All API calls include Authorization header
- [ ] 401 response triggers logout + navigate to LoginActivity

---

## Security Notes

1. **Password Storage:** Use bcrypt or Argon2 for password hashing on backend
2. **Token Security:**
   - Use JWT with strong secret key
   - Set reasonable expiration (e.g., 30 days)
   - Consider refresh token mechanism for production
3. **HTTPS Only:** Ensure all API communication uses HTTPS
4. **Input Validation:** Sanitize all user inputs on backend
5. **Rate Limiting:** Implement login rate limiting to prevent brute force

---

## Future Enhancements

1. **Admin Dashboard:** Web/mobile interface to view all workers' stats
2. **Push Notifications:** Notify workers of daily/weekly goals
3. **Gamification:** Badges, achievements, streaks
4. **Team Stats:** Group statistics for team leads
5. **Export Reports:** CSV/PDF export of worker productivity
6. **Offline Support:** Cache stats locally, sync when online
7. **Real-time Updates:** WebSocket for live leaderboard updates
8. **Password Reset:** Forgot password functionality
9. **Profile Photos:** Allow workers to upload profile pictures
10. **Advanced Analytics:** Charts, trends, predictions

---

## Notes for Next Session

- Backend implementation can start immediately (database + auth endpoints)
- Android implementation should start with Phase 1 (authentication)
- Need to decide on exact API base URL
- Need to create worker accounts for testing
- Consider using Postman collection for API testing
- May want to add SwipeRefreshLayout dependency if not already present
- Bottom navigation icons need to be created/sourced

---

## Contact & Questions

For any clarifications on the design, refer to this document. Implementation can proceed in phases as outlined above.
