package com.hyundai.ngmb

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.UUID
import java.util.concurrent.TimeUnit

// 기기 내부에 X-Owner-Id(UUID)를 영구 저장하는 DataStore
private val Context.dataStore by preferencesDataStore(name = "sleepcare_owner_prefs")

class ApiCallException(
    val httpStatus: Int,
    val code: String,
    override val message: String,
    val fieldErrors: Map<String, String> = emptyMap()
) : Exception(message)

object SleepCareApiClient {
    private const val TAG = "SleepCareApi"

    // 에뮬레이터에서 로컬 서버 접속 주소 (배포 APK 제출 시 Railway 주소로 교체)
    private const val BASE_URL = "https://backend-production-910889bb.up.railway.app/api/v1/profiles"
    private val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()
    private val OWNER_ID_KEY = stringPreferencesKey("x_owner_id")

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(5, TimeUnit.SECONDS)
        .build()

    // 앱 최초 실행 시 UUID를 생성해 DataStore에 저장하고 계속 재사용
    suspend fun getOrCreateOwnerId(context: Context): String {
        val prefs = context.dataStore.data.first()
        val existing = prefs[OWNER_ID_KEY]
        if (!existing.isNullOrBlank()) {
            return existing
        }
        val newUuid = UUID.randomUUID().toString()
        context.dataStore.edit { settings ->
            settings[OWNER_ID_KEY] = newUuid
        }
        return newUuid
    }

    // 1. 내 프로필 목록 조회: GET /api/v1/profiles
    suspend fun listProfiles(context: Context): List<SleepProfile> = withContext(Dispatchers.IO) {
        val ownerId = getOrCreateOwnerId(context)
        val request = Request.Builder()
            .url(BASE_URL)
            .header("X-Owner-Id", ownerId)
            .get()
            .build()

        httpClient.newCall(request).execute().use { response ->
            val bodyStr = response.body?.string().orEmpty()
            if (!response.isSuccessful) {
                throw parseError(response.code, bodyStr)
            }
            val root = JSONObject(bodyStr)
            val arr = root.getJSONArray("profiles")
            buildList {
                for (i in 0 until arr.length()) {
                    add(parseProfileJson(arr.getJSONObject(i)))
                }
            }
        }
    }

    // 2. 프로필 생성: POST /api/v1/profiles
    suspend fun createProfile(
        context: Context,
        name: String,
        template: SleepProfile
    ): SleepProfile = withContext(Dispatchers.IO) {
        val ownerId = getOrCreateOwnerId(context)
        val payload = JSONObject().apply {
            put("name", name.trim())
            put("preset", buildPresetJson(template))
        }
        val request = Request.Builder()
            .url(BASE_URL)
            .header("X-Owner-Id", ownerId)
            .post(payload.toString().toRequestBody(JSON_MEDIA_TYPE))
            .build()

        httpClient.newCall(request).execute().use { response ->
            val bodyStr = response.body?.string().orEmpty()
            if (!response.isSuccessful) {
                throw parseError(response.code, bodyStr)
            }
            parseProfileJson(JSONObject(bodyStr))
        }
    }

    // 3. 프로필 1개 조회: GET /api/v1/profiles/{profileId}
    suspend fun getProfile(context: Context, profileId: Long): SleepProfile = withContext(Dispatchers.IO) {
        val ownerId = getOrCreateOwnerId(context)
        val request = Request.Builder()
            .url("$BASE_URL/$profileId")
            .header("X-Owner-Id", ownerId)
            .get()
            .build()

        httpClient.newCall(request).execute().use { response ->
            val bodyStr = response.body?.string().orEmpty()
            if (!response.isSuccessful) {
                throw parseError(response.code, bodyStr)
            }
            parseProfileJson(JSONObject(bodyStr))
        }
    }

    // 4. 프로필 이름 변경: PATCH /api/v1/profiles/{profileId}
    suspend fun renameProfile(
        context: Context,
        profileId: Long,
        newName: String
    ): SleepProfile = withContext(Dispatchers.IO) {
        val ownerId = getOrCreateOwnerId(context)
        val payload = JSONObject().apply {
            put("name", newName.trim())
        }
        val request = Request.Builder()
            .url("$BASE_URL/$profileId")
            .header("X-Owner-Id", ownerId)
            .patch(payload.toString().toRequestBody(JSON_MEDIA_TYPE))
            .build()

        httpClient.newCall(request).execute().use { response ->
            val bodyStr = response.body?.string().orEmpty()
            if (!response.isSuccessful) {
                throw parseError(response.code, bodyStr)
            }
            parseProfileJson(JSONObject(bodyStr))
        }
    }

    // 5. 프리셋 전체 수정: PUT /api/v1/profiles/{profileId}/preset
    suspend fun updatePreset(
        context: Context,
        profileId: Long,
        updated: SleepProfile
    ): SleepProfile = withContext(Dispatchers.IO) {
        val ownerId = getOrCreateOwnerId(context)
        val payload = buildPresetJson(updated)
        val request = Request.Builder()
            .url("$BASE_URL/$profileId/preset")
            .header("X-Owner-Id", ownerId)
            .put(payload.toString().toRequestBody(JSON_MEDIA_TYPE))
            .build()

        httpClient.newCall(request).execute().use { response ->
            val bodyStr = response.body?.string().orEmpty()
            if (!response.isSuccessful) {
                throw parseError(response.code, bodyStr)
            }
            parseProfileJson(JSONObject(bodyStr))
        }
    }

    // 6. 프로필 삭제: DELETE /api/v1/profiles/{profileId}
    suspend fun deleteProfile(context: Context, profileId: Long) = withContext(Dispatchers.IO) {
        val ownerId = getOrCreateOwnerId(context)
        val request = Request.Builder()
            .url("$BASE_URL/$profileId")
            .header("X-Owner-Id", ownerId)
            .delete()
            .build()

        httpClient.newCall(request).execute().use { response ->
            val bodyStr = response.body?.string().orEmpty()
            if (!response.isSuccessful) {
                throw parseError(response.code, bodyStr)
            }
        }
    }

    // 각도와 단계는 정수(Int), 온도는 실수(Double), 무드는 코드 문자열(AMBER 등)로 전송
    private fun buildPresetJson(profile: SleepProfile): JSONObject {
        val safeHeat = if (profile.seatVentLevel > 0) 0 else profile.seatHeatLevel
        val safeVent = if (safeHeat > 0) 0 else profile.seatVentLevel

        return JSONObject().apply {
            put("backrest", JSONObject().put("angle", profile.backrestAngle))
            put("seatHeat", JSONObject().put("level", safeHeat))
            put("seatVent", JSONObject().put("level", safeVent))
            put("temperature", JSONObject().put("targetCelsius", profile.targetCelsius))
            put("ambientLight", JSONObject().put("mood", profile.mood.apiCode))
        }
    }

    private fun parseProfileJson(obj: JSONObject): SleepProfile {
        val preset = obj.getJSONObject("preset")
        val backrestAngle = preset.getJSONObject("backrest").getInt("angle")
        val seatHeatLevel = preset.getJSONObject("seatHeat").getInt("level")
        val seatVentLevel = preset.getJSONObject("seatVent").getInt("level")
        val targetCelsius = preset.getJSONObject("temperature").getDouble("targetCelsius")
        val moodCode = preset.getJSONObject("ambientLight").getString("mood")

        return SleepProfile(
            profileId = obj.getLong("profileId"),
            name = obj.getString("name"),
            backrestAngle = backrestAngle,
            seatHeatLevel = seatHeatLevel,
            seatVentLevel = seatVentLevel,
            targetCelsius = targetCelsius,
            mood = MoodType.fromApiCode(moodCode),
            createdAt = obj.optString("createdAt", null),
            updatedAt = obj.optString("updatedAt", null)
        )
    }

    // 서버 공통 에러 응답(code, message, fieldErrors) 파싱
    // 생성(POST)의 preset.seatHeat.level 과 수정(PUT)의 seatHeat.level 경로를 모두 처리
    private fun parseError(httpStatus: Int, bodyStr: String): ApiCallException {
        return runCatching {
            val json = JSONObject(bodyStr)
            val code = json.optString("code", "UNKNOWN_ERROR")
            val baseMessage = json.optString("message", "요청 처리 중 오류가 발생했습니다.")
            val fieldMap = mutableMapOf<String, String>()

            val fieldArr = json.optJSONArray("fieldErrors")
            if (fieldArr != null) {
                for (i in 0 until fieldArr.length()) {
                    val item = fieldArr.getJSONObject(i)
                    val rawField = item.optString("field")
                    val normalizedField = rawField.removePrefix("preset.")
                    val reason = item.optString("reason")
                    fieldMap[normalizedField] = reason
                }
            }

            if (httpStatus == 401 || code == "OWNER_ID_REQUIRED") {
                Log.e(TAG, "401 OWNER_ID_REQUIRED 개발 버그 확인 필요: $bodyStr")
            }

            val detailedMessage = when {
                code == "PROFILE_LIMIT_EXCEEDED" || httpStatus == 409 -> "프로필은 최대 10개입니다."
                fieldMap.isNotEmpty() -> fieldMap.entries.joinToString("\n") { (_, reason) -> reason }
                else -> baseMessage
            }

            ApiCallException(
                httpStatus = httpStatus,
                code = code,
                message = detailedMessage,
                fieldErrors = fieldMap
            )
        }.getOrElse {
            ApiCallException(
                httpStatus = httpStatus,
                code = "INTERNAL_ERROR",
                message = "서버 통신 오류가 발생했습니다 ($httpStatus)"
            )
        }
    }
}