package com.potato.cheatengine

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LuaEditorActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lua_editor)

        val etCode = findViewById<EditText>(R.id.et_lua_code)
        val btnRun = findViewById<Button>(R.id.btn_run_lua)

        btnRun.setOnClickListener {
            val scanner = MemoryScannerHolder.getScanner()
            if (scanner == null) {
                Toast.makeText(this, "No process selected", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val engine = LuaEngine()
            engine.registerMemoryFunctions(scanner)
            try {
                engine.executeScript(etCode.text.toString())
                Toast.makeText(this, "Script executed", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
