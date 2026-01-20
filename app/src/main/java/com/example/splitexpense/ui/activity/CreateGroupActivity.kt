package com.example.splitexpense.ui.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.splitexpense.data.database.AppDatabase
import com.example.splitexpense.data.repository.ExpenseRepository
import com.example.splitexpense.databinding.ActivityCreateGroupBinding
import com.example.splitexpense.ui.adapter.MemberInputAdapter
import com.example.splitexpense.ui.viewmodel.ExpenseViewModel
import com.example.splitexpense.ui.viewmodel.ExpenseViewModelFactory

class CreateGroupActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateGroupBinding
    private lateinit var viewModel: ExpenseViewModel
    private lateinit var memberAdapter: MemberInputAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCreateGroupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = AppDatabase.getDatabase(applicationContext)
        val repo = ExpenseRepository(db.appDao())
        viewModel = ViewModelProvider(this, ExpenseViewModelFactory(repo))[ExpenseViewModel::class.java]

        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener { finish() }

        memberAdapter = MemberInputAdapter(mutableListOf(""))
        binding.rvMembers.layoutManager = LinearLayoutManager(this)
        binding.rvMembers.adapter = memberAdapter

        binding.btnAddMember.setOnClickListener {
            memberAdapter.addEmpty()
            binding.rvMembers.scrollToPosition(memberAdapter.itemCount - 1)
        }

        binding.btnCreateGroup.setOnClickListener {
            val groupName = binding.etGroupName.text?.toString()?.trim().orEmpty()
            val members = memberAdapter.getValues().map { it.trim() }.filter { it.isNotBlank() }

            if (groupName.isBlank()) {
                Toast.makeText(this, "Enter group name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (members.isEmpty()) {
                Toast.makeText(this, "Add at least 1 member", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.createGroup(groupName, members)
            Toast.makeText(this, "Group created", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
