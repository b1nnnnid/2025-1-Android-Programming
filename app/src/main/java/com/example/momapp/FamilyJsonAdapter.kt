package com.example.momapp

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FamilyJsonAdapter(
    private var familyList: List<JsonFamilyItem>,
    private val itemClickListener: (JsonFamilyItem) -> Unit
) : RecyclerView.Adapter<FamilyJsonAdapter.FamilyViewHolder>() {

    inner class FamilyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCareName: TextView = itemView.findViewById(R.id.tvCareName)

        fun bind(item: JsonFamilyItem) {
            val location = listOfNotNull(item.city, item.district).joinToString(" ")
            tvCareName.text = "${item.facilityName ?: "기관명 없음"} ($location)"

            itemView.setOnClickListener {
                itemClickListener(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FamilyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recyclerview, parent, false)
        return FamilyViewHolder(view)
    }

    override fun onBindViewHolder(holder: FamilyViewHolder, position: Int) {
        holder.bind(familyList[position])
    }

    override fun getItemCount(): Int = familyList.size

    fun updateList(newList: List<JsonFamilyItem>) {
        familyList = newList
        notifyDataSetChanged()
    }
}
