package ako.`fun`

import java.io.File
import java.security.MessageDigest

val String.md5: String
    get() {
        val digest = MessageDigest.getInstance("MD5")
        // 对字符串进行编码，然后更新 digest 对象
        digest.update(this.toByteArray())
        // 完成哈希计算
        val hash = digest.digest()
        // 将字节数组转换为十六进制字符串
        return hash.fold("") { str, it -> str + "%02x".format(it) }
    }

val File.md5: String
    get() {
        val md = MessageDigest.getInstance("MD5")
        inputStream().use { fis ->
            val buffer = ByteArray(1024 * 1024) // 1MB buffer
            var read: Int
            while (fis.read(buffer).also { read = it } > 0) {
                md.update(buffer, 0, read)
            }
        }
        // Convert the byte array to hex format
        val sb = StringBuilder()
        for (b in md.digest()) {
            sb.append(String.format("%02x", b))
        }
        return sb.toString()
    }

val ByteArray.md5: String
    get() {
        val md = MessageDigest.getInstance("MD5")
        md.update(this)
        val sb = StringBuilder()
        for (b in md.digest()) {
            sb.append(String.format("%02x", b))
        }
        return sb.toString()
    }