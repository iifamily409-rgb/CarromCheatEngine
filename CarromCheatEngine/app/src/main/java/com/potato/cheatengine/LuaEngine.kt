package com.potato.cheatengine

import org.luaj.vm2.Globals
import org.luaj.vm2.LuaValue
import org.luaj.vm2.lib.jse.JsePlatform

class LuaEngine {
    private val globals: Globals = JsePlatform.standardGlobals()

    fun executeScript(script: String) {
        globals.load(script).call()
    }

    fun registerMemoryFunctions(scanner: MemoryScanner) {
        val memLib = LuaValue.tableOf()
        memLib.set("search", object : org.luaj.vm2.lib.VarArgFunction() {
            override fun invoke(args: org.luaj.vm2.Varargs): LuaValue {
                val value = args.arg1().tolong()
                val typeStr = args.arg(2).tojstring()
                val type = when (typeStr) {
                    "INT" -> MemoryScanner.ValueType.INT
                    "LONG" -> MemoryScanner.ValueType.LONG
                    "FLOAT" -> MemoryScanner.ValueType.FLOAT
                    "DOUBLE" -> MemoryScanner.ValueType.DOUBLE
                    else -> MemoryScanner.ValueType.INT
                }
                val results = scanner.searchValue(value, type)
                val table = LuaValue.tableOf()
                results.forEachIndexed { index, addr ->
                    table.set(index + 1, LuaValue.valueOf(addr))
                }
                return table
            }
        })
        memLib.set("write", object : org.luaj.vm2.lib.VarArgFunction() {
            override fun invoke(args: org.luaj.vm2.Varargs): LuaValue {
                val address = args.arg1().tolong()
                val value = args.arg(2).tolong()
                val typeStr = args.arg(3).tojstring()
                val type = when (typeStr) {
                    "INT" -> MemoryScanner.ValueType.INT
                    "LONG" -> MemoryScanner.ValueType.LONG
                    "FLOAT" -> MemoryScanner.ValueType.FLOAT
                    "DOUBLE" -> MemoryScanner.ValueType.DOUBLE
                    else -> MemoryScanner.ValueType.INT
                }
                scanner.writeValue(address, value, type)
                return LuaValue.TRUE
            }
        })
        globals.set("memory", memLib)
    }
}
