package com.mystrica.adpter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.mystrica.R
import com.mystrica.fragment.HomeFragment
import com.mystrica.model.HomeDataDO
import com.mystrica.util.Constant
import com.mystrica.util.Utils
import java.util.*

class HomeAdpter(
    sList: List<HomeDataDO>,
    activity: Context?,
    context: Context,
    backName: String,
    homeFragment: HomeFragment
) : RecyclerView.Adapter<HomeAdpter.ViewHolder>() {
    var sList: List<HomeDataDO> = ArrayList()
    private val fr: FragmentActivity? = null
    private val context: Context
    private val activity: Activity?
    private val fragment: Fragment
    private val backName: String
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val listItem = layoutInflater.inflate(R.layout.row_home, parent, false)
        return ViewHolder(listItem)
    }

    @SuppressLint("NewApi")
    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        try {
            val homeDataDO = sList[position]
            val isEnable: Boolean = homeDataDO.isEnable
            holder!!.txtTitle.text = homeDataDO.title
            if (isEnable) {
                holder.lyContainer!!.background =
                    context.getDrawable(R.drawable.rounded_corner_light_blue)
                holder.txtTitle.setTextColor(context.getColor(R.color.color_blue))
                val selectedTest = Constant.selectTest
                if (!(selectedTest == "" || selectedTest == null || selectedTest.isEmpty())) {
                    if (homeDataDO.title == Constant.selectTest) {
                        holder.lyContainer.background =
                            context.getDrawable(R.drawable.round_corner_blue)
                        holder.txtTitle.setTextColor(context.getColor(R.color.myst_white))
                    }
                } else {
                    holder.lyContainer.background =
                        context.getDrawable(R.drawable.rounded_corner_light_blue)
                    holder.txtTitle.setTextColor(context.getColor(R.color.color_blue))
                }
                holder.lyContainer.setOnClickListener { //if tag is qc test than go inside the qc test
                    (fragment as HomeFragment).checkHomePermission(homeDataDO.title)
                }
            } else {
                holder.lyContainer.background = context.getDrawable(R.drawable.rounded_light_grey)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int {
        return sList.size
    }

    inner class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val lyContainer: RelativeLayout
        val txtTitle: TextView

        init {
            txtTitle = itemView.findViewById(R.id.row_txt_title)
            lyContainer = itemView.findViewById(R.id.row_ly_container)
        }
    }

    init {
        this.sList = sList
        this.context = context
        this.activity = activity as Activity?
        this.backName = backName
        fragment = homeFragment
    }
}