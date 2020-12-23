package com.mystrica.adpter

import android.annotation.SuppressLint
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
import com.mystrica.fragment.TestTypeFragment
import com.mystrica.model.HomeDataDO
import com.mystrica.util.Constant
import java.util.*

class TestTypeAdpter(
    sList: List<HomeDataDO>,
    activity: FragmentActivity,
    title: String,
    back: String,
    testTypeFragment: TestTypeFragment
) : RecyclerView.Adapter<TestTypeAdpter.ViewHolder>() {
    var sList: List<HomeDataDO> = ArrayList()
    private val context: Context
    private val title: String
    private val backName: String
    private val fragment: Fragment
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
            holder.txtTitle.text = homeDataDO.title
            if (isEnable) {
                holder.lyContainer.background =
                    context.getDrawable(R.drawable.rounded_corner_light_blue)
                holder.txtTitle.setTextColor(context.getColor(R.color.color_blue))
                val selectedTest =
                    Constant.selectSubTest
                if (!(selectedTest == "" || selectedTest == null || selectedTest.isEmpty())) {
                    if (homeDataDO.title == Constant.selectSubTest) {
                        holder.lyContainer.background =
                            context.getDrawable(R.drawable.round_corner_blue)
                        holder.txtTitle.setTextColor(context.getColor(R.color.myst_white))
                    }
                } else {
                    holder.lyContainer.background =
                        context.getDrawable(R.drawable.rounded_corner_light_blue)
                    holder.txtTitle.setTextColor(context.getColor(R.color.color_blue))
                }
                holder.lyContainer.setOnClickListener {

                    (fragment as TestTypeFragment).checkSubType(homeDataDO.title, title)

                }
            } else {
                holder.lyContainer.background =
                    context.getDrawable(R.drawable.rounded_corner_light_blue)
                holder.txtTitle.setTextColor(context.getColor(R.color.color_blue))
                //                holder.lyContainer.setBackground(context.getDrawable(R.drawable.rounded_light_grey));
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
        context = activity
        this.title = title
        backName = back
        fragment = testTypeFragment
    }
}