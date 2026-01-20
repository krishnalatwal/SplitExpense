package com.example.splitexpense.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.splitexpense.data.entity.Member
import com.example.splitexpense.databinding.ItemCheckboxMemberBinding

class MemberCheckboxAdapter : RecyclerView.Adapter<MemberCheckboxAdapter.VH>() {

    private var items: List<Member> = emptyList()
    private val checkedIds = mutableSetOf<String>()

    inner class VH(val binding: ItemCheckboxMemberBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemCheckboxMemberBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val m = items[position]
        holder.binding.tvName.text = m.name
        holder.binding.checkbox.isChecked = checkedIds.contains(m.id)

        holder.binding.root.setOnClickListener {
            toggle(m.id)
            notifyItemChanged(position)
        }
        holder.binding.checkbox.setOnClickListener {
            toggle(m.id)
        }
    }

    private fun toggle(id: String) {
        if (checkedIds.contains(id)) checkedIds.remove(id) else checkedIds.add(id)
    }

    fun submitList(list: List<Member>) {
        items = list
        notifyDataSetChanged()
    }

    fun setCheckedIds(ids: Set<String>) {
        checkedIds.clear()
        checkedIds.addAll(ids)
        notifyDataSetChanged()
    }

    fun getCheckedIds(): Set<String> = checkedIds.toSet()
}
