package com.example.splitexpense.ui.adapter

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.splitexpense.databinding.ItemMemberInputBinding

class MemberInputAdapter(
    private val items: MutableList<String>
) : RecyclerView.Adapter<MemberInputAdapter.VH>() {

    inner class VH(val binding: ItemMemberInputBinding) : RecyclerView.ViewHolder(binding.root) {
        var watcher: TextWatcher? = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemMemberInputBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.binding.etMember.setText(items[position])

        holder.watcher?.let { holder.binding.etMember.removeTextChangedListener(it) }
        holder.watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val p = holder.bindingAdapterPosition
                if (p != RecyclerView.NO_POSITION) items[p] = s?.toString().orEmpty()
            }
        }
        holder.binding.etMember.addTextChangedListener(holder.watcher)

        holder.binding.btnDelete.setOnClickListener {
            val p = holder.bindingAdapterPosition
            if (p != RecyclerView.NO_POSITION && items.size > 1) {
                items.removeAt(p)
                notifyItemRemoved(p)
            }
        }

        holder.binding.btnDelete.isEnabled = items.size > 1
    }

    fun addEmpty() {
        items.add("")
        notifyItemInserted(items.lastIndex)
    }

    fun getValues(): List<String> = items.toList()
}
