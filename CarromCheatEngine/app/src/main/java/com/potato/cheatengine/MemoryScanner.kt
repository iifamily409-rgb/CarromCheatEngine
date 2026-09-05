package com.potato.cheatengine

import java.io.File
import java.io.RandomAccessFile

class MemoryScanner(private val pid: Int) {
    private val memFile = RandomAccessFile("/proc/$pid/mem", "rw")
    private val mapsFile = File("/proc/$pid/maps")
    private var regions = mutableListOf<MemoryRegion>()

    data class MemoryRegion(val start: Long, val end: Long, val perms: String, val path: String)

    fun parseMaps() {
        regions.clear()
        mapsFile.forEachLine { line ->
            val parts = line.split(Regex("\\s+"))
            if (parts.size >= 6) {
                val addr = parts[0].split("-")
                val start = addr[0].toLong(16)
                val end = addr[1].toLong(16)
                val perms = parts[1]
                val path = if (parts.size > 5) parts[5] else ""
                if (perms.contains("rw")) {
                    regions.add(MemoryRegion(start, end, perms, path))
                }
            }
        }
    }

    fun searchValue(value: Long, type: ValueType): List<Long> {
        val results = mutableListOf<Long>()
        val bufferSize = 4096
        val buffer = ByteArray(bufferSize)
        for (region in regions) {
            var offset = region.start
            val regionSize = region.end - region.start
            var remaining = regionSize
            while (remaining > 0) {
                val toRead = minOf(bufferSize.toLong(), remaining).toInt()
                try {
                    memFile.seek(offset)
                    memFile.readFully(buffer, 0, toRead)
                    for (i in 0 until toRead - type.size) {
                        val found = when (type) {
                            ValueType.INT -> buffer.getInt(i) == value.toInt()
                            ValueType.LONG -> buffer.getLong(i) == value
                            ValueType.FLOAT -> buffer.getFloat(i) == value.toFloat()
                            ValueType.DOUBLE -> buffer.getDouble(i) == value.toDouble()
                        }
                        if (found) results.add(offset + i)
                    }
                } catch (e: Exception) {
                    // ignore
                }
                offset += toRead
                remaining -= toRead
            }
        }
        return results
    }

    fun writeValue(address: Long, value: Long, type: ValueType) {
        memFile.seek(address)
        when (type) {
            ValueType.INT -> memFile.writeInt(value.toInt())
            ValueType.LONG -> memFile.writeLong(value)
            ValueType.FLOAT -> memFile.writeFloat(value.toFloat())
            ValueType.DOUBLE -> memFile.writeDouble(value.toDouble())
        }
    }

    enum class ValueType(val size: Int) {
        INT(4), LONG(8), FLOAT(4), DOUBLE(8)
    }
}
