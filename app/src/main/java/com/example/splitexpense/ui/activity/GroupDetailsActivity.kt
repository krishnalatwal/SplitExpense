package com.example.splitexpense.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.splitexpense.data.database.AppDatabase
import com.example.splitexpense.data.repository.ExpenseRepository
import com.example.splitexpense.databinding.ActivityGroupDetailsBinding
import com.example.splitexpense.ui.adapter.ExpenseAdapter
import com.example.splitexpense.ui.viewmodel.ExpenseViewModel
import com.example.splitexpense.ui.viewmodel.ExpenseViewModelFactory
import com.example.splitexpense.ui.activity.SettleUpActivity
import kotlinx.coroutines.launch
import com.example.splitexpense.R

class GroupDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGroupDetailsBinding
    private lateinit var viewModel: ExpenseViewModel
    private lateinit var expenseAdapter: ExpenseAdapter

    private lateinit var groupId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGroupDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        groupId = intent.getStringExtra(EXTRA_GROUP_ID).orEmpty()

        val db = AppDatabase.getDatabase(applicationContext)
        val repo = ExpenseRepository(db.appDao())
        viewModel = ViewModelProvider(this, ExpenseViewModelFactory(repo))[ExpenseViewModel::class.java]

        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener { finish() }
        binding.toolbar.inflateMenu(R.menu.menu_group_details)
        binding.toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_settle_up -> {
                    lifecycleScope.launch {
                        val balances = viewModel.calculateBalances(groupId)
                        val settlements = viewModel.calculateSettlements(balances)
                        SettleUpActivity.setTempSettlements(settlements)

                        startActivity(
                            Intent(this@GroupDetailsActivity, SettleUpActivity::class.java).apply {
                                putExtra(SettleUpActivity.EXTRA_GROUP_ID, groupId)
                            }
                        )
                    }
                    true
                }
                else -> false
            }
        }

        expenseAdapter = ExpenseAdapter()
        binding.rvExpenses.layoutManager = LinearLayoutManager(this)
        binding.rvExpenses.adapter = expenseAdapter

        binding.fabAddExpense.setOnClickListener {
            startActivity(
                Intent(this, AddExpenseActivity::class.java).apply {
                    putExtra(AddExpenseActivity.EXTRA_GROUP_ID, groupId)
                }
            )
        }

        viewModel.selectGroup(groupId)

        lifecycleScope.launch {
            viewModel.groups.collect { groups ->
                val g = groups.find { it.id == groupId }
                binding.toolbar.title = g?.name ?: "Group"
                binding.cardBalances.visibility =
                    if (g?.isPinned == true) android.view.View.GONE else android.view.View.VISIBLE
            }
        }

        lifecycleScope.launch {
            viewModel.currentMembers.collect { members ->
                expenseAdapter.setMembers(members)
            }
        }

        lifecycleScope.launch {
            viewModel.currentExpenses.collect { expenses ->
                expenseAdapter.submitList(expenses)
                updateBalanceUI()
            }
        }
    }

    private fun updateBalanceUI() {
        lifecycleScope.launch {
            val balances = viewModel.calculateBalances(groupId)

            binding.containerBalances.removeAllViews()
            for (b in balances) {
                val row = layoutInflater.inflate(
                    android.R.layout.simple_list_item_2,
                    binding.containerBalances,
                    false
                )
                row.findViewById<android.widget.TextView>(android.R.id.text1).text = b.memberName
                row.findViewById<android.widget.TextView>(android.R.id.text2).text = "₹%.2f".format(b.balance)
                binding.containerBalances.addView(row)
            }
        }
    }



    companion object {
        const val EXTRA_GROUP_ID = "extra_group_id"
    }
}



