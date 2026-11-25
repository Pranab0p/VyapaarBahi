package com.example.data

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.content.FileProvider
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit

object InAppUpdateHelper {
    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    data class ReleaseInfo(
        val versionName: String,
        val releaseNotes: String,
        val apkDownloadUrl: String,
        val apkSizeBytes: Long
    )

    sealed class CheckResult {
        data class UpdateAvailable(val release: ReleaseInfo) : CheckResult()
        data class UpToDate(val currentVersion: String) : CheckResult()
        data class Error(val message: String) : CheckResult()
    }

    suspend fun checkGitHubRelease(ownerRepo: String): CheckResult = withContext(Dispatchers.IO) {
        val cleaned = ownerRepo.trim().removePrefix("https://github.com/").trim('/')
        if (!cleaned.contains("/")) {
            return@withContext CheckResult.Error("Enter valid GitHub repo as username/repo")
        }
        try {
            val request = Request.Builder()
                .url("https://api.github.com/repos/$cleaned/releases/latest")
                .addHeader("Accept", "application/vnd.github+json")
                .addHeader("User-Agent", "VyapaarBahi-Android")
                .get()
                .build()

            client.newCall(request).execute().use { resp ->
                if (resp.code == 404) {
                    return@withContext CheckResult.Error("No published release found for $cleaned")
                }
                if (!resp.isSuccessful) {
                    return@withContext CheckResult.Error("GitHub API error (${resp.code})")
                }
                val body = resp.body?.string() ?: return@withContext CheckResult.Error("Empty response")
                val json = JSONObject(body)
                val tag = json.optString("tag_name", "").removePrefix("v").trim()
                val notes = json.optString("body", "Bug fixes and performance improvements.")
                val assets = json.optJSONArray("assets")

                var apkUrl = ""
                var apkSize = 0L
                if (assets != null) {
                    for (i in 0 until assets.length()) {
                        val asset = assets.getJSONObject(i)
                        val name = asset.optString("name", "")
                        if (name.endsWith(".apk", ignoreCase = true)) {
                            apkUrl = asset.optString("browser_download_url", "")
                            apkSize = asset.optLong("size", 0L)
                            break
                        }
                    }
                }

                if (apkUrl.isEmpty()) {
                    return@withContext CheckResult.Error("Latest release ($tag) has no .apk file attached.")
                }

                if (isNewerVersion(tag, BuildConfig.VERSION_NAME)) {
                    CheckResult.UpdateAvailable(ReleaseInfo(tag, notes, apkUrl, apkSize))
                } else {
                    CheckResult.UpToDate(BuildConfig.VERSION_NAME)
                }
            }
        } catch (e: Exception) {
            CheckResult.Error("Check failed: ${e.localizedMessage ?: "No internet connection"}")
        }
    }

    suspend fun downloadAndPrepareApk(
        context: Context,
        apkUrl: String,
        onProgress: (Int) -> Unit
    ): File? = withContext(Dispatchers.IO) {
        try {
            val updatesDir = File(context.cacheDir, "updates").apply { mkdirs() }
            val outFile = File(updatesDir, "vyapaarbahi_update.apk")

            val request = Request.Builder().url(apkUrl).get().build()
            client.newCall(request).execute().use { resp ->
                if (!resp.isSuccessful) return@withContext null
                val body = resp.body ?: return@withContext null
                val total = body.contentLength()
                body.byteStream().use { input ->
                    FileOutputStream(outFile).use { output ->
                        val buffer = ByteArray(8192)
                        var bytesCopied = 0L
                        var read: Int
                        while (input.read(buffer).also { read = it } != -1) {
                            output.write(buffer, 0, read)
                            bytesCopied += read
                            if (total > 0) {
                                val pct = ((bytesCopied * 100) / total).toInt().coerceIn(0, 100)
                                withContext(Dispatchers.Main) { onProgress(pct) }
                            }
                        }
                    }
                }
            }
            outFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun copyApkFromUriAndInspect(context: Context, uri: Uri): Pair<File, String?>? {
        return try {
            val updatesDir = File(context.cacheDir, "updates").apply { mkdirs() }
            val outFile = File(updatesDir, "vyapaarbahi_local_update.apk")
            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(outFile).use { output ->
                    input.copyTo(output)
                }
            } ?: return null

            val pm = context.packageManager
            val pkgInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                pm.getPackageArchiveInfo(outFile.absolutePath, PackageManager.PackageInfoFlags.of(0))
            } else {
                @Suppress("DEPRECATION")
                pm.getPackageArchiveInfo(outFile.absolutePath, 0)
            }
            val versionLabel = pkgInfo?.versionName
            Pair(outFile, versionLabel)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun launchApkInstaller(context: Context, apkFile: File): Boolean {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
                !context.packageManager.canRequestPackageInstalls()
            ) {
                val permIntent = Intent(
                    Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
                    Uri.parse("package:${context.packageName}")
                ).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
                context.startActivity(permIntent)
                return false
            }

            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                apkFile
            )
            val installIntent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/vnd.android.package-archive")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(installIntent)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun isNewerVersion(remote: String, current: String): Boolean {
        val rParts = remote.split(".").map { it.filter { ch -> ch.isDigit() }.toIntOrNull() ?: 0 }
        val cParts = current.split(".").map { it.filter { ch -> ch.isDigit() }.toIntOrNull() ?: 0 }
        val maxLen = maxOf(rParts.size, cParts.size)
        for (i in 0 until maxLen) {
            val r = rParts.getOrElse(i) { 0 }
            val c = cParts.getOrElse(i) { 0 }
            if (r > c) return true
            if (r < c) return false
        }
        return false
    }
}
