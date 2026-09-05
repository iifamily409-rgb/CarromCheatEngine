package com.potato.cheatengine

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ScanActivity : AppCompatActivity() {
    private var results: List<Long> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)

        val etValue = findViewById<EditText>(R.id.et_value)
        val spinnerType = findViewById<Spinner>(R.id.spinner_type)
        val btnScan = findViewById<Button>(R.id.btn_scan)
        val btnFilter = findViewById<Button>(R.id.btn_filter)
        val listResults = findViewById<ListView>(R.id.list_results)

        val types = listOf("INT", "LONG", "FLOAT", "DOUBLE")
        spinnerType.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, types)
        spinnerType.setSelection(0)

        btnScan.setOnClickListener {
            val scanner = MemoryScannerHolder.getScanner()
            if (scanner == null) {
                Toast.makeText(this, "No process selected", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val value = etValue.text.toString().toLongOrNull() ?: return@setOnClickListener
            val typeStr = spinnerType.selectedItem.toString()
            val type = MemoryScanner.ValueType.valueOf(typeStr)
            results = scanner.searchValue(value, type)
            updateList(listResults, results)
        }

        btnFilter.setOnClickListener {
            val scanner = MemoryScannerHolder.getScanner() ?: return@setOnClickListener
            val value = etValue.text.toString().toLongOrNull() ?: return@setOnClickListener
            val typeStr = spinnerType.selectedItem.toString()
            val type = MemoryScanner.ValueType.valueOf(typeStr)
            results = results.filter { true }
            updateList(listResults, results)
        }

        listResults.setOnItemClickListener { _, _, position, _ ->
            val address = results[position]
            val scanner = MemoryScannerHolder.getScanner()
            scanner?.writeValue(address, 99999L, MemoryScanner.ValueType.INT)
            Toast.makeText(this, "Written to $address", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateList(listView: ListView, addresses: List<Long>) {
        val display = addresses.map { "0x${it.toString(16)}" }
        listView.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, display)
    }
}
