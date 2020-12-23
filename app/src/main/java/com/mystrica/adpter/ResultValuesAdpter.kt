package com.mystrica.adpter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.data.Entry
import com.mystrica.R
import java.text.DecimalFormat
import java.util.*

class ResultValuesAdpter(
    private val resultdata: ArrayList<Entry>,
    activity: FragmentActivity,
    private val fragment: Fragment
) : RecyclerView.Adapter<ResultValuesAdpter.ViewHolder>() {
    private val context: Context
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        //        View listItem= layoutInflater.inflate(R.layout.row_sample, parent, false);
        val listItem =
            layoutInflater.inflate(R.layout.row_result_data, parent, false)
        return ViewHolder(listItem)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {

//        String getdata=resultdata.get(position).toString();
        val second = resultdata[position]!!.x
        val resultData = resultdata[position]!!.y
        val decimalFormat = DecimalFormat("#.##")
        val f1 = decimalFormat.format(resultData.toDouble()).toFloat()
        if (position == 0 && position == 1) {
        } else {
            val secondvalue = second.toInt()
            holder.txtvalue.text = f1.toString()
            holder.txtSecond.text = secondvalue.toString()
        }
    }

    override fun getItemCount(): Int {
        return resultdata.size
    }

    inner class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val txtvalue: TextView
        val txtSecond: TextView

        init {
            txtvalue = itemView.findViewById(R.id.txt_row_value4result)
            txtSecond = itemView.findViewById(R.id.txt_row_second4result)
        }
    }

    init {
        context = activity
    }
}