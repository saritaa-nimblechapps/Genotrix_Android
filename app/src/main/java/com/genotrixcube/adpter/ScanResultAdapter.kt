

package com.genotrixcube.adpter

import android.bluetooth.le.ScanResult
import android.content.Context
import android.os.Build
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.genotrixcube.R
import com.genotrixcube.fragment.ConnectBluetothFragment
import com.genotrixcube.util.Utils
import kotlinx.android.synthetic.main.roe_device.view.*
import org.jetbrains.anko.layoutInflater
import kotlin.collections.ArrayList

class ScanResultAdapter(
    private val items: ArrayList<ScanResult>,
    private val requireActivity: FragmentActivity,
    private val applicationContext: Context

) : RecyclerView.Adapter<ScanResultAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = parent.context.layoutInflater.inflate(
            R.layout.roe_device,
            parent,
            false
        )
        return ViewHolder(view)
    }

    override fun getItemCount() = items.size

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item,requireActivity,applicationContext)
    }

    class ViewHolder(
        private val view: View

    ) : RecyclerView.ViewHolder(view) {

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        fun bind(
            result: ScanResult,
            requireActivity: FragmentActivity,
            applicationContext: Context
        ) {
            view.txt_row_device_name.text = result.device.name ?: "Unnamed"


            view.txt_row_device_name.setOnClickListener {
                Log.wtf("device","Adpter click device name"+result.device.name)


                var fragment =requireActivity?. supportFragmentManager.findFragmentById(R.id.container) as ConnectBluetothFragment
                if(fragment != null){

                    fragment.stopBleScan()

                    //passing main service (select from constan string value
                    fragment.setDevie(result.device)

                    Log.wtf("mulfile","device select adpter call :")

                    fragment.connectGatt(false, result.device)

                }

            }

        }


    }




}
