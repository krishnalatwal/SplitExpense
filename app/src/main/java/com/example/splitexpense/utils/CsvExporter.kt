package com.example.splitexpense.utils

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.example.splitexpense.data.entity.Expense
import com.example.splitexpense.ui.viewmodel.Settlement
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object CsvExporter {

    fun exportExpenses(context: Context, groupName: String, expenses: List<Expense>) {
        val csv = buildString {
            appendLine("Date,Description,Amount,Paid By")
            expenses.forEach { expense ->
                val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(expense.date))
                appendLine("$date,${expense.title},${expense.amount},${expense.paidByMemberId}")
            }
        }

        saveAndShareCsv(context, "${groupName}_expenses.csv", csv)
    }

    fun exportSettlements(context: Context, groupName: String, settlements: List<Settlement>) {
        val csv = buildString {
            appendLine("From,To,Amount")
            settlements.forEach { settlement ->
                appendLine("${settlement.from},${settlement.to},${settlement.amount}")
            }
        }

        saveAndShareCsv(context, "${groupName}_settlements.csv", csv)
    }

    private fun saveAndShareCsv(context: Context, filename: String, content: String) {
        val file = File(context.cacheDir, filename)
        file.writeText(content)

        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(Intent.createChooser(intent, "Export CSV"))
    }
}