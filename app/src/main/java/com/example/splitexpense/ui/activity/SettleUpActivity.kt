package com.example.splitexpense.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.splitexpense.databinding.ActivitySettleUpBinding
import com.example.splitexpense.ui.adapter.SettlementAdapter
import com.example.splitexpense.ui.viewmodel.Settlement

class SettleUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettleUpBinding
    private lateinit var adapter: SettlementAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettleUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener { finish() }

        adapter = SettlementAdapter()
        binding.rvSettlements.layoutManager = LinearLayoutManager(this)
        binding.rvSettlements.adapter = adapter

        adapter.submitList(tempSettlements ?: emptyList())

//        binding.btnExportCsv.setOnClickListener {
//            // You can plug your CsvExporter here later
//        }
    }

    companion object {
        const val EXTRA_GROUP_ID = "extra_group_id"

        private var tempSettlements: List<Settlement>? = null
        fun setTempSettlements(list: List<Settlement>) {
            tempSettlements = list
        }
    }
}
