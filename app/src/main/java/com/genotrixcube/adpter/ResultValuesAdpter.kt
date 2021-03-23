package com.genotrixcube.adpter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.data.Entry
import com.genotrixcube.R
import com.genotrixcube.util.Constant
import com.genotrixcube.util.Utils
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
        val listItem =
            layoutInflater.inflate(R.layout.row_result_data, parent, false)
        return ViewHolder(listItem)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {

        val second = resultdata[position]!!.x
        val resultData = resultdata[position]!!.y


        val estimatedSecond=Utils.pref_get_int(Constant.limitMilisecond,context).toFloat()

        if(second==estimatedSecond){
            holder.view_dash_line.visibility=View.VISIBLE

        }else{
            holder.view_dash_line.visibility=View.GONE

        }

        val decimalFormat = DecimalFormat("0.000")
        val f1= String.format("%.3f",resultData)
        if (position == 0 && position == 1) {
        } else {
            val secondvalue = second.toInt()
            holder.txtvalue.text = f1.toString()
            holder.txtSecond.text = secondvalue.toString() +"s"
        }
    }

    override fun getItemCount(): Int {
        return resultdata.size
    }

    inner class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val txtvalue: TextView
        val txtSecond: TextView
        val view_dash_line : View

        init {
            txtvalue = itemView.findViewById(R.id.txt_row_value4result)
            txtSecond = itemView.findViewById(R.id.txt_row_second4result)
            view_dash_line=itemView.findViewById(R.id.v_row_eastimated_dash_line)

        }
    }

    init {
        context = activity
    }
}