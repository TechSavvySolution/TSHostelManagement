package com.techsavvy.tshostelmanagement.data.utils

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

object CdnUploadService {

    private const val CDN_URL = "https://cdn.techsavvysolution.in/api/upload.php"
    private const val API_TOKEN = "MY_SECURE_API_TOKEN"
    private const val BOUNDARY = "----TechSavvyBoundary"
    private const val LINE_FEED = "\r\n"

    /**
     * Uploads a single file to the TechSavvy CDN.
     *
     * @param context  Android context (used to resolve the content URI)
     * @param uri      The content URI of the file to upload
     * @return         The CDN URL string of the uploaded file
     * @throws Exception if the upload fails or the server returns success=false
     */
    suspend fun uploadFile(context: Context, uri: Uri): String = withContext(Dispatchers.IO) {
        val contentResolver = context.contentResolver
        val mimeType = contentResolver.getType(uri)
            ?: getMimeTypeFromUri(uri)
            ?: "application/octet-stream"
        val extension = MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(mimeType) ?: "bin"
        val fileName = "upload_${System.currentTimeMillis()}.$extension"

        val connection = (URL(CDN_URL).openConnection() as HttpURLConnection).apply {
            doOutput = true
            doInput = true
            useCaches = false
            requestMethod = "POST"
            setRequestProperty("Authorization", "Bearer $API_TOKEN")
            setRequestProperty("Content-Type", "multipart/form-data; boundary=$BOUNDARY")
            connectTimeout = 30_000
            readTimeout = 60_000
        }

        try {
            DataOutputStream(connection.outputStream).use { dos ->
                // --- Part header ---
                dos.writeBytes("--$BOUNDARY$LINE_FEED")
                dos.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\"$fileName\"$LINE_FEED")
                dos.writeBytes("Content-Type: $mimeType$LINE_FEED")
                dos.writeBytes(LINE_FEED)

                // --- File bytes ---
                val inputStream: InputStream = contentResolver.openInputStream(uri)
                    ?: throw Exception("Cannot open input stream for URI: $uri")
                inputStream.use { it.copyTo(dos) }

                // --- Closing boundary ---
                dos.writeBytes(LINE_FEED)
                dos.writeBytes("--$BOUNDARY--$LINE_FEED")
                dos.flush()
            }

            val responseCode = connection.responseCode
            val responseBody = if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader(InputStreamReader(connection.inputStream)).use { it.readText() }
            } else {
                BufferedReader(InputStreamReader(connection.errorStream ?: connection.inputStream)).use { it.readText() }
            }

            val json = JSONObject(responseBody)
            if (json.optBoolean("success", false)) {
                json.getString("url")
            } else {
                throw Exception("CDN upload failed: ${json.optString("message", "Unknown error")}")
            }
        } finally {
            connection.disconnect()
        }
    }

    /**
     * Uploads multiple files and returns the list of CDN URLs.
     * Throws on the first failure.
     */
    suspend fun uploadFiles(context: Context, uris: List<Uri>): List<String> {
        return uris.map { uri -> uploadFile(context, uri) }
    }

    private fun getMimeTypeFromUri(uri: Uri): String? {
        val path = uri.path ?: return null
        val extension = path.substringAfterLast('.', "")
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.lowercase())
    }
}
