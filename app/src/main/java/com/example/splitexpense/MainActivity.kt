package com.example.splitexpense

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.splitexpense.data.database.AppDatabase
import com.example.splitexpense.data.repository.ExpenseRepository
import com.example.splitexpense.databinding.ActivityMainBinding
import com.example.splitexpense.ui.activity.CreateGroupActivity
import com.example.splitexpense.ui.screens.CreateGroupScreen
import com.example.splitexpense.ui.activity.GroupDetailsActivity
import com.example.splitexpense.ui.adapter.GroupAdapter
import com.example.splitexpense.ui.viewmodel.ExpenseViewModel
import com.example.splitexpense.ui.viewmodel.ExpenseViewModelFactory
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: ExpenseViewModel
    private lateinit var adapter: GroupAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val database = AppDatabase.getDatabase(applicationContext)
        val repository = ExpenseRepository(database.appDao())
        viewModel = ViewModelProvider(this, ExpenseViewModelFactory(repository))[ExpenseViewModel::class.java]

        setSupportActionBar(binding.toolbar)

        adapter = GroupAdapter { groupId ->
            viewModel.selectGroup(groupId) // keep VM updated
            val intent = Intent(this, GroupDetailsActivity::class.java).apply {
                putExtra(GroupDetailsActivity.EXTRA_GROUP_ID, groupId)
            }
            startActivity(intent)
        }

        binding.rvGroups.layoutManager = LinearLayoutManager(this)
        binding.rvGroups.adapter = adapter

        binding.fabCreateGroup.setOnClickListener {
            startActivity(Intent(this, CreateGroupActivity::class.java))
        }

        lifecycleScope.launch {
            viewModel.groups.collect { groups ->
                adapter.submitList(groups)
            }
        }
    }

    private fun setSupportActionBar(toolbar: androidx.appcompat.widget.Toolbar?) {
        // If your toolbar class is MaterialToolbar, this isn't needed.
        // Keeping it safe. If you get error, delete this method.
    }
}
