package com.potato.cheatengine

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tvStatus = findViewById<TextView>(R.id.tv_status)
        tvStatus.text = if (RootUtils.isRooted()) "Root: Yes" else "Root: No"

        findViewById<Button>(R.id.btn_select_process).setOnClickListener {
            startActivity(Intent(this, ProcessListActivity::class.java))
        }
        findViewById<Button>(R.id.btn_open_lua).setOnClickListener {
            startActivity(Intent(this, LuaEditorActivity::class.java))
        }
        findViewById<Button>(R.id.btn_start_overlay).setOnClickListener {
            startService(Intent(this, FloatingService::class.java))
        }
    }
}
