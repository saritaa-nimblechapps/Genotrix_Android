package com.mystrica.fragment

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.mystrica.DatabaseHendler.DatabaseHelper
import com.mystrica.DatabaseHendler.EmpModelClass
import com.mystrica.R
import com.mystrica.util.ConnectionManager
import com.mystrica.util.ConnetingServicHelper
import com.mystrica.util.Constant
import kotlinx.android.synthetic.main.connect_blu_fragment.view.*
import org.jetbrains.anko.find
import java.util.*
import kotlin.experimental.and

class BLeValueSetFragment() : Fragment() , ConnetingServicHelper {

    private lateinit var rootview : View

    companion object {

        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            BLeValueSetFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootview= inflater.inflate(R.layout.connect_blu_fragment, container, false)

        setupViews()


        return  rootview
    }

    private fun setupViews() {

        rootview?.pr_collect_data?.setVisibility(View.GONE)


        rootview?.txt_type_title?.text="connecting device "
        rootview?.txt_blue_text_connecty?.text= "waiting for device connecting"


        rootview?.txt_connect.setOnClickListener {

            //open dialog fragment
            DailogPwmValueGet()

        }
    }


    private fun DailogPwmValueGet() {

        val dialog = Dialog(requireContext()!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dialog_fragment)


        val txtSetLed =
            dialog.findViewById<View>(R.id.txtsetLed) as TextView

         val txtCalibrate  =dialog.findViewById<TextView>(R.id.txtSetCalibrate) as TextView

        val txtPwm =dialog.findViewById<TextView>(R.id.txtPwmValue) as TextView
        val txtReadValue=dialog.findViewById<TextView>(R.id.txtReadValue) as TextView
        val txtNotification=dialog.find<TextView>(R.id.txtNotification) as TextView
        val txtdisNotification=dialog.find<TextView>(R.id.txtDisableNotication) as TextView
        val txtCurrent =dialog.findViewById<TextView>(R.id.txtCurrentvalue) as TextView
        val txtADD=dialog.findViewById<TextView>(R.id.txtAddRecord)
        val txtView=dialog.findViewById<TextView>(R.id.txtViewRecord)

        val custom_byte = Constant.intconvert.toByte()
        val pwmBitValue = Constant.pwmconvert.toByte()

        val databaseHandler: DatabaseHelper= DatabaseHelper(requireActivity().applicationContext)


        val setcalibrate = byteArrayOf(
            0x00.toByte(),
            (custom_byte and 0xFF.toByte()),
            0x00.toByte(),
            0x00.toByte()
        )

        val setpwm = byteArrayOf(
            0x02.toByte(),
            (custom_byte and 0xFF.toByte()),
            (pwmBitValue and 0xFF.toByte()),
            0x00.toByte()
        )

        val setcurrent = byteArrayOf(
            0x03.toByte(),
            (custom_byte and 0xFF.toByte()),
            (pwmBitValue and 0xFF.toByte()),
            0x00.toByte()
        )

        val setired = byteArrayOf(
            0x01.toByte(),
            (custom_byte and 0xFF.toByte()),
            0x00.toByte(),
            0x00.toByte()
        )

        val device =ConnectionManager.getDevice()

        txtSetLed.setOnClickListener {
            ConnectionManager.writedCharetistic(device,Constant.MYSTERIA_CHARACTERISTIC_TEST_UUID,"setled",setired,false)
            ConnectionManager.writedCharetistic(device,Constant.MYSTERIA_CHARACTERISTIC_TEST_UUID,"setCalibrate",setcalibrate,true)
//            ConnectionManager.setEnableNotification(device,Constant.MYSTERIA_CHARACTERISTIC_RESILT_UUID,"setResult")
//            ConnectionManager.readCharetistic(device,Constant.MYSTERIA_CHARACTERISTIC_RESILT_UUID,"readvalue")

        }

        txtCalibrate.setOnClickListener {
            ConnectionManager.writedCharetistic(device,Constant.MYSTERIA_CHARACTERISTIC_TEST_UUID,"setCalibrate",setcalibrate,true)
            ConnectionManager.setEnableNotification(device,Constant.MYSTERIA_CHARACTERISTIC_RESILT_UUID,"setResult")
        }

        txtPwm.setOnClickListener {


            ConnectionManager.writedCharetistic(device,Constant.MYSTERIA_CHARACTERISTIC_TEST_UUID,"setPwm",setpwm,false)
        }

        txtReadValue.setOnClickListener {

            ConnectionManager.readCharetistic(device,Constant.MYSTERIA_CHARACTERISTIC_RESILT_UUID,"readvalue")
//            ConnectionManager.setEnableNotification(device,Constant.MYSTERIA_CHARACTERISTIC_RESILT_UUID,"setResult")

        }

        txtNotification.setOnClickListener {
            ConnectionManager.setEnableNotification(device,Constant.MYSTERIA_CHARACTERISTIC_RESILT_UUID,"setResult")
        }

        txtCurrent.setOnClickListener {
            ConnectionManager.writedCharetistic(device,Constant.MYSTERIA_CHARACTERISTIC_TEST_UUID,"setCurrent",setcurrent,false)
        }

        txtdisNotification.setOnClickListener {
            ConnectionManager.setDisableNotification(device,Constant.MYSTERIA_CHARACTERISTIC_RESILT_UUID,"setResultDisaable")

        }


        txtADD.setOnClickListener {
            val databaseHandler: DatabaseHelper= DatabaseHelper(requireActivity().applicationContext)

            val status = databaseHandler.addEmployee(EmpModelClass("xyz", "xyz@gmail.com"))
            if(status > -1)
            {
                Toast.makeText(requireContext(),"record save",Toast.LENGTH_LONG).show()

            }
            else{
                Toast.makeText(requireContext(),"id or name or email cannot be blank",Toast.LENGTH_LONG).show()
            }
        }

        txtView.setOnClickListener {
            val emp: List<EmpModelClass> = databaseHandler.viewEmployee()

            for( employee in emp){
                Log.wtf("view record "," name of the employee"+employee.userName)
            }

        }

        dialog.show()
    }

    fun setDeviceName(devicename: String?) {
        rootview?.txt_type_title?.text="Connected device "
        rootview?.txt_blue_text_connecty?.text= devicename
    }

    override fun readvalue(value: ByteArray) {

        Log.wtf("read"," read value  :"+ Arrays.toString(value))
    }

    fun setReadValue(byte: ByteArray) {
        Log.wtf("read"," read value  :"+ Arrays.toString(byte))

    }
}