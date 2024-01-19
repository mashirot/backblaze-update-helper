package ski.mashiro

import com.backblaze.b2.client.B2StorageClientFactory
import com.backblaze.b2.client.contentSources.B2FileContentSource
import com.backblaze.b2.client.structures.B2UploadFileRequest
import org.apache.commons.io.FileUtils
import java.io.File
import kotlin.system.exitProcess

/**
 * @author mashirot
 * 2024/1/19 21:44
 * @param args[0] filePath: Source file path
 * @param args[1] logFilePath: Log file path
 * @param args[2] appKeyId: AppKeyId
 * @param args[3] appKey: AppKey
 * @param args[4] bucketName: Bucket name
 * @param args[5] parentFolder: Parent folder of file in bucket (Optional)
 */
fun main(args: Array<String>) {
    val (filePath, logFilePath, appKeyId, appKey, bucketName) = args
    val parentFolder = if (args.size > 5) args[5] else ""
    val logFile = File(logFilePath)
    if (!logFile.exists()) {
        println("Log file not found.")
        return
    }
    val file = File(filePath)
    if (!file.exists()) {
        FileUtils.writeStringToFile(logFile, "backblaze-update-helper: File not found.\n", "UTF-8", true)
        return
    }
    val b2Client = B2StorageClientFactory
        .createDefaultFactory()
        .create(appKeyId, appKey, "backblaze-update-helper")
    val bucket = b2Client.getBucketOrNullByName(bucketName) ?: run {
        FileUtils.writeStringToFile(logFile, "backblaze-update-helper: Bucket not found.\n", "UTF-8", true)
        return
    }
    b2Client.uploadSmallFile(
        B2UploadFileRequest.builder(
            bucket.bucketId,
            parentFolder + file.name,
            "text/plain",
            B2FileContentSource.build(file)
        ).build()
    ).run {
        if (isUpload) {
            FileUtils.writeStringToFile(logFile, "backblaze-update-helper: ${file.name} Upload success.\n", "UTF-8", true)
        } else {
            FileUtils.writeStringToFile(logFile, "backblaze-update-helper: ${file.name} Upload failed.\n", "UTF-8", true)
        }
        exitProcess(0)
    }
}