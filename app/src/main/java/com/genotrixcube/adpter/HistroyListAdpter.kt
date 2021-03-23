package com.genotrixcube.adpter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.genotrixcube.R
import com.genotrixcube.fragment.HistoryFragment
import com.genotrixcube.model.RecordDataHeaderConvertDO
import java.text.SimpleDateFormat
import java.util.*

class HistroyListAdpter(
    var recordList: ArrayList<RecordDataHeaderConvertDO>,
    val requireActivity: FragmentActivity,
    val historyFragment: HistoryFragment,
    val requireContext: Context
) : RecyclerView.Adapter<HistroyListAdpter.ViewHolder>() {

    companion object{
        var arrayBolSelectRecord : ArrayList<RecordDataHeaderConvertDO> ?= arrayListOf()
    }


    inner class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
            var txtPatientId : TextView ?= null
            var txtDatetime : TextView ?= null
            var checkReocrd : CheckBox ?= null
        var lyDate_id : ConstraintLayout ?= null
        init {

            txtPatientId=itemView.findViewById(R.id.txt_row_patientid)
            txtDatetime=itemView.findViewById(R.id.txt_row_dateTime)
            lyDate_id=itemView.findViewById(R.id.ly_patient_id_date)
            checkReocrd=itemView.findViewById(R.id.opt_row_check_record)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val listItem = layoutInflater.inflate(R.layout.row_history, parent, false)
        return ViewHolder(listItem)
    }

    override fun getItemCount(): Int {

        return  recordList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        try {

            val dateDO=recordList.get(position)

            holder.txtPatientId!!.text=dateDO.patientId

            val getDODate=dateDO.testDate

            val dateFormat = SimpleDateFormat("dd-MMM-yyyy")
            val dateTime = dateFormat.format(getDODate)

            holder.txtDatetime!!.text=dateTime+" "+dateDO.testTime

            holder.checkReocrd!!.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener{
                override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {

                    if(isChecked){

                        arrayBolSelectRecord?.add(dateDO)

                        callNextButtonVisiblitiy(requireContext)


                    }else{
                        arrayBolSelectRecord?.remove(dateDO)

                        callNextButtonVisiblitiy(requireContext)

                    }
                }
            })

            holder.lyDate_id!!.setOnClickListener {


                getSelectedRecord(dateDO.patientId,requireContext)



            }


        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun getSelectedRecord(patientId: String,context: Context) {
        val fragment = (context as? AppCompatActivity)?.supportFragmentManager?.findFragmentById(R.id.container)
        if(fragment is HistoryFragment){
            fragment.selectRecordDisplayData(patientId)
        }
    }

    private fun callNextButtonVisiblitiy(context: Context) {

        //next button visible set if any check box click than than visible , if not or all selected check box uncheck than then invisible the next button.
        try {
            val fragment = (context as? AppCompatActivity)?.supportFragmentManager?.findFragmentById(R.id.container)

            if(arrayBolSelectRecord!!.size>0){
                if(fragment is HistoryFragment){
                    fragment.visibleitNextButton()
                }
            }
            else{
                if(fragment is HistoryFragment){
                    fragment.HideNextButton()
                    notifyDataSetChanged()
                }
            }
        }catch (e:java.lang.Exception){
            e.printStackTrace()
        }
    }

    fun updateList(temp: ArrayList<RecordDataHeaderConvertDO>) {
        recordList.clear()
        recordList.addAll(temp)
        notifyDataSetChanged()
    }

}