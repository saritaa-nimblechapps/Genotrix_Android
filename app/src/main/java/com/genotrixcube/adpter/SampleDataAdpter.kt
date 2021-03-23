package com.genotrixcube.adpter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.genotrixcube.R
import java.util.*

class SampleDataAdpter(sList: List<String>) :
    RecyclerView.Adapter<SampleDataAdpter.ViewHolder>() {
    var sList: List<String> = ArrayList()
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val listItem = layoutInflater.inflate(R.layout.roe_device, parent, false)
        return ViewHolder(listItem)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val getdata = sList[position]
        holder.txtData.text = getdata
    }

    override fun getItemCount(): Int {
        return sList.size
    }

    inner class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val txtData: TextView

        init {
            txtData = itemView.findViewById(R.id.txt_row_device_name)
        }
    }

    init {
        this.sList = sList
    }
}