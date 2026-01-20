package com.example.splitexpense.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.splitexpense.data.entity.Group
import com.example.splitexpense.databinding.ItemGroupBinding

class GroupAdapter(
    private val onClick: (String) -> Unit
) : ListAdapter<Group, GroupAdapter.VH>(Diff) {

    object Diff : DiffUtil.ItemCallback<Group>() {
        override fun areItemsTheSame(oldItem: Group, newItem: Group) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Group, newItem: Group) = oldItem == newItem
    }

    inner class VH(val binding: ItemGroupBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemGroupBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val g = getItem(position)
        holder.binding.tvName.text = g.name
        holder.binding.tvSubtitle.text = if (g.isPinned) "Personal expenses" else "Group expenses"
        holder.binding.root.setOnClickListener { onClick(g.id) }
    }
}
