package com.example.splitexpense.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.splitexpense.databinding.ItemSettlementBinding
import com.example.splitexpense.ui.viewmodel.Settlement

class SettlementAdapter : ListAdapter<Settlement, SettlementAdapter.VH>(Diff) {

    object Diff : DiffUtil.ItemCallback<Settlement>() {
        override fun areItemsTheSame(oldItem: Settlement, newItem: Settlement) =
            oldItem.from == newItem.from && oldItem.to == newItem.to && oldItem.amount == newItem.amount

        override fun areContentsTheSame(oldItem: Settlement, newItem: Settlement) = oldItem == newItem
    }

    inner class VH(val binding: ItemSettlementBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemSettlementBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val s = getItem(position)
        holder.binding.tvFromTo.text = "${s.from} → ${s.to}"
        holder.binding.tvAmount.text = "₹%.2f".format(s.amount)
    }
}
