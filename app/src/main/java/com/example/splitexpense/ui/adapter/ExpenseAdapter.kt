package com.example.splitexpense.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.splitexpense.data.entity.Expense
import com.example.splitexpense.data.entity.Member
import com.example.splitexpense.databinding.ItemExpenseBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ExpenseAdapter : ListAdapter<Expense, ExpenseAdapter.VH>(Diff) {

    private var members: List<Member> = emptyList()
    private val df = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    object Diff : DiffUtil.ItemCallback<Expense>() {
        override fun areItemsTheSame(oldItem: Expense, newItem: Expense) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Expense, newItem: Expense) = oldItem == newItem
    }

    inner class VH(val binding: ItemExpenseBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemExpenseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val e = getItem(position)
        val payerName = members.find { it.id == e.paidByMemberId }?.name ?: "Unknown"
        holder.binding.tvTitle.text = e.title
        holder.binding.tvAmount.text = "₹%.2f".format(e.amount)
        holder.binding.tvMeta.text = "Paid by $payerName • ${df.format(Date(e.date))}"
    }

    fun setMembers(list: List<Member>) {
        members = list
        notifyDataSetChanged()
    }
}
