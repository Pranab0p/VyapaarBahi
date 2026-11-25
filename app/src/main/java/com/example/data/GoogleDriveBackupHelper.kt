package com.example.data

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.auth.UserRecoverableAuthException
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.Scope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GoogleDriveBackupHelper {
    const val DRIVE_APPDATA_SCOPE = "https://www.googleapis.com/auth/drive.appdata"
    private const val BACKUP_FILE_NAME = "vyapaarbahi_backup.json"

    private val client = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    sealed class DriveResult {
        data class Success(val message: String, val jsonContent: String? = null) : DriveResult()
        data class NeedSignIn(val reason: String) : DriveResult()
        data class NeedConsent(val intent: Intent) : DriveResult()
        data class Error(val message: String) : DriveResult()
    }

    fun hasDriveScope(context: Context): Boolean {
        val account = GoogleSignIn.getLastSignedInAccount(context) ?: return false
        return GoogleSignIn.hasPermissions(account, Scope(DRIVE_APPDATA_SCOPE))
    }

    suspend fun backupToDrive(context: Context, jsonData: String): DriveResult = withContext(Dispatchers.IO) {
        val account = GoogleSignIn.getLastSignedInAccount(context)
            ?: return@withContext DriveResult.NeedSignIn("Please sign in with Google first to connect Google Drive.")
        val androidAccount = account.account
            ?: return@withContext DriveResult.NeedSignIn("Google account permissions missing. Please sign in again.")

        val token = try {
            GoogleAuthUtil.getToken(context, androidAccount, "oauth2:$DRIVE_APPDATA_SCOPE")
        } catch (e: UserRecoverableAuthException) {
            val intent = e.intent
            return@withContext if (intent != null) DriveResult.NeedConsent(intent)
            else DriveResult.Error("Google Drive permission required.")
        } catch (e: Exception) {
            return@withContext DriveResult.Error("Auth failed: ${e.localizedMessage ?: "Check Google Play Services & OAuth SHA-1"}")
        }

        try {
            val existingFileId = findBackupFileId(token)
            val jsonMediaType = "application/json; charset=utf-8".toMediaType()

            if (existingFileId != null) {
                // Update existing file in appDataFolder
                val request = Request.Builder()
                    .url("https://www.googleapis.com/upload/drive/v3/files/$existingFileId?uploadType=media")
                    .addHeader("Authorization", "Bearer $token")
                    .patch(jsonData.toRequestBody(jsonMediaType))
                    .build()
                client.newCall(request).execute().use { resp ->
                    if (!resp.isSuccessful) {
                        return@withContext DriveResult.Error("Drive upload failed (${resp.code}): ${resp.body?.string() ?: resp.message}")
                    }
                }
            } else {
                // Create new file in appDataFolder
                val metadata = JSONObject().apply {
                    put("name", BACKUP_FILE_NAME)
                    put("parents", JSONArray().put("appDataFolder"))
                }.toString()

                val multipartBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addPart(metadata.toRequestBody(jsonMediaType))
                    .addPart(jsonData.toRequestBody(jsonMediaType))
                    .build()

                val request = Request.Builder()
                    .url("https://www.googleapis.com/upload/drive/v3/files?uploadType=multipart")
                    .addHeader("Authorization", "Bearer $token")
                    .post(multipartBody)
                    .build()

                client.newCall(request).execute().use { resp ->
                    if (!resp.isSuccessful) {
                        return@withContext DriveResult.Error("Drive create failed (${resp.code}): ${resp.body?.string() ?: resp.message}")
                    }
                }
            }
            DriveResult.Success("Ledger backed up to Google Drive (${account.email ?: "Connected"})")
        } catch (e: Exception) {
            DriveResult.Error("Network error: ${e.localizedMessage ?: "Unable to reach Google Drive"}")
        }
    }

    suspend fun restoreFromDrive(context: Context): DriveResult = withContext(Dispatchers.IO) {
        val account = GoogleSignIn.getLastSignedInAccount(context)
            ?: return@withContext DriveResult.NeedSignIn("Please sign in with Google first to restore from Drive.")
        val androidAccount = account.account
            ?: return@withContext DriveResult.NeedSignIn("Google account permissions missing. Please sign in again.")

        val token = try {
            GoogleAuthUtil.getToken(context, androidAccount, "oauth2:$DRIVE_APPDATA_SCOPE")
        } catch (e: UserRecoverableAuthException) {
            val intent = e.intent
            return@withContext if (intent != null) DriveResult.NeedConsent(intent)
            else DriveResult.Error("Google Drive permission required.")
        } catch (e: Exception) {
            return@withContext DriveResult.Error("Auth failed: ${e.localizedMessage ?: "Check Google Play Services & OAuth SHA-1"}")
        }

        try {
            val existingFileId = findBackupFileId(token)
                ?: return@withContext DriveResult.Error("No VyapaarBahi backup found in this Google Drive account.")

            val request = Request.Builder()
                .url("https://www.googleapis.com/drive/v3/files/$existingFileId?alt=media")
                .addHeader("Authorization", "Bearer $token")
                .get()
                .build()

            client.newCall(request).execute().use { resp ->
                if (!resp.isSuccessful) {
                    return@withContext DriveResult.Error("Drive download failed (${resp.code})")
                }
                val body = resp.body?.string() ?: return@withContext DriveResult.Error("Empty backup file on Drive")
                DriveResult.Success("Backup downloaded from Google Drive!", body)
            }
        } catch (e: Exception) {
            DriveResult.Error("Network error: ${e.localizedMessage ?: "Unable to reach Google Drive"}")
        }
    }

    private fun findBackupFileId(token: String): String? {
        val url = "https://www.googleapis.com/drive/v3/files?spaces=appDataFolder&q=name%3D%27$BACKUP_FILE_NAME%27&fields=files(id,name,modifiedTime)"
        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $token")
            .get()
            .build()

        client.newCall(request).execute().use { resp ->
            if (!resp.isSuccessful) return null
            val bodyStr = resp.body?.string() ?: return null
            val files = JSONObject(bodyStr).optJSONArray("files") ?: return null
            if (files.length() == 0) return null
            return files.getJSONObject(0).optString("id", null)
        }
    }
}
