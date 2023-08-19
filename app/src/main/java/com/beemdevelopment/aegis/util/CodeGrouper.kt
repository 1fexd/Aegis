package com.beemdevelopment.aegis.util

import com.beemdevelopment.aegis.Preferences.CodeGrouping

object CodeGrouper {
    fun formatCode(codeGrouping: CodeGrouping, code: String): String {
        val groupSize: Int
        return buildString {
            when (codeGrouping) {
                CodeGrouping.NO_GROUPING -> groupSize = code.length
                CodeGrouping.HALVES -> groupSize = code.length / 2 + code.length % 2
                else -> {
                    groupSize = codeGrouping.value
                    require(groupSize > 0) { "Code group size cannot be zero or negative" }
                }
            }
            for (i in code.indices) {
                if (i != 0 && i % groupSize == 0) {
                    append(" ")
                }

                append(code[i])
            }
        }
    }
}