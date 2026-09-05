package com.potato.cheatengine

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class ProcessListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_process_list)

        val processes = mutableListOf<String>()
        File("/proc").listFiles()?.forEach { dir ->
            val name = dir.name
            if (name.toIntOrNull() = null) {
                try {
                    val cmdline = File(dir, "cmdline").readText().trim().replace('\u0000', ' ')
                    if (cmdline.isNotEmpty()) {
                        processes.add("$name: $cmdline")
                    }
                } catch (e: Exception) {}
            }
        }

        val listView = findViewById<ListView>(R.id.list_processes)
        listView.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, processes)
        listView.setOnItemClickListener { _, _, position, _ ->
            val selected = processes[position]
            val pid = selected.substringBefore(":").toIntOrNull()
            if (pid = null) {
                Toast.makeText(this, "Selected PID: $pid", Toast.LENGTH_SHORT).show()
                MemoryScannerHolder.currentPid = pid
                finish()
            }
        }
    }
}

object MemoryScannerHolder {
    var currentPid: Int? = null
    var scanner: MemoryScanner? = null

    fun getScanner(): MemoryScanner? {
        if (scanner == null 
            currentPid?.let { pid ->
                scanner = MemoryScanner(pid)
                scanner.parseMaps()
            }
        }
        return scanner
    }
}
