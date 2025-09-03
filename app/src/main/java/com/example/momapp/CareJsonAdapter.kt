package com.example.momapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CareJsonAdapter(
    private var careList: List<JsonCareItem>,
    private val onItemClick: (JsonCareItem) -> Unit
) : RecyclerView.Adapter<CareJsonAdapter.CareViewHolder>() {

    inner class CareViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCareName: TextView = itemView.findViewById(R.id.tvCareName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CareViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recyclerview, parent, false)
        return CareViewHolder(view)
    }

    override fun onBindViewHolder(holder: CareViewHolder, position: Int) {
        val item = careList[position]
        holder.tvCareName.text = item.childCareInstNm ?: "기관명 없음"

        holder.itemView.setOnClickListener {
            onItemClick(item)
        }
    }

    override fun getItemCount(): Int = careList.size

    fun updateList(newList: List<JsonCareItem>) {
        careList = newList
        notifyDataSetChanged()
    }
}
