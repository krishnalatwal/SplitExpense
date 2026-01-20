package com.example.splitexpense.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.splitexpense.data.entity.Member
import com.example.splitexpense.databinding.ItemRadioMemberBinding

class MemberRadioAdapter : RecyclerView.Adapter<MemberRadioAdapter.VH>() {

    private var items: List<Member> = emptyList()
    private var selectedId: String = ""

    inner class VH(val binding: ItemRadioMemberBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemRadioMemberBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val m = items[position]
        holder.binding.tvName.text = m.name
        holder.binding.radio.isChecked = m.id == selectedId

        holder.binding.root.setOnClickListener { setSelectedId(m.id) }
        holder.binding.radio.setOnClickListener { setSelectedId(m.id) }
    }

    fun submitList(list: List<Member>) {
        items = list
        notifyDataSetChanged()
    }

    fun setSelectedId(id: String) {
        selectedId = id
        notifyDataSetChanged()
    }

    fun getSelectedId(): String = selectedId
}
