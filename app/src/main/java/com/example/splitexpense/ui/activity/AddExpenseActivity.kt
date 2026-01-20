package com.example.splitexpense.ui.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.splitexpense.data.database.AppDatabase
import com.example.splitexpense.data.repository.ExpenseRepository
import com.example.splitexpense.databinding.ActivityAddExpenseBinding
import com.example.splitexpense.ui.adapter.MemberCheckboxAdapter
import com.example.splitexpense.ui.adapter.MemberRadioAdapter
import com.example.splitexpense.ui.viewmodel.ExpenseViewModel
import com.example.splitexpense.ui.viewmodel.ExpenseViewModelFactory
import kotlinx.coroutines.launch

class AddExpenseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddExpenseBinding
    private lateinit var viewModel: ExpenseViewModel

    private lateinit var paidByAdapter: MemberRadioAdapter
    private lateinit var splitAdapter: MemberCheckboxAdapter

    private lateinit var groupId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddExpenseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        groupId = intent.getStringExtra(EXTRA_GROUP_ID).orEmpty()

        val db = AppDatabase.getDatabase(applicationContext)
        val repo = ExpenseRepository(db.appDao())
        viewModel = ViewModelProvider(this, ExpenseViewModelFactory(repo))[ExpenseViewModel::class.java]

        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener { finish() }

        paidByAdapter = MemberRadioAdapter()
        splitAdapter = MemberCheckboxAdapter()

        binding.rvPaidBy.layoutManager = LinearLayoutManager(this)
        binding.rvPaidBy.adapter = paidByAdapter

        binding.rvSplit.layoutManager = LinearLayoutManager(this)
        binding.rvSplit.adapter = splitAdapter

        viewModel.selectGroup(groupId)

        lifecycleScope.launch {
            viewModel.currentMembers.collect { members ->
                paidByAdapter.submitList(members)
                splitAdapter.submitList(members)

                if (members.isNotEmpty()) {
                    paidByAdapter.setSelectedId(members.first().id)
                    splitAdapter.setCheckedIds(members.map { it.id }.toSet())
                }
            }
        }

        binding.btnAddExpense.setOnClickListener {
            val title = binding.etTitle.text?.toString()?.trim().orEmpty()
            val amount = binding.etAmount.text?.toString()?.trim()?.toDoubleOrNull()

            if (title.isBlank()) {
                Toast.makeText(this, "Enter description", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (amount == null || amount <= 0) {
                Toast.makeText(this, "Enter valid amount", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val payerId = paidByAdapter.getSelectedId()
            val participants = splitAdapter.getCheckedIds().toList()

            if (payerId.isBlank()) {
                Toast.makeText(this, "Select payer", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (participants.isEmpty()) {
                Toast.makeText(this, "Select participants", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.addExpense(groupId, title, amount, payerId, participants)
            Toast.makeText(this, "Expense added", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    companion object {
        const val EXTRA_GROUP_ID = "extra_group_id"
    }
}
