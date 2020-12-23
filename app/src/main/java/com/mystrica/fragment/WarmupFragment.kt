package com.mystrica.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.mystrica.R
import com.mystrica.activity.DashboardActivity
import com.mystrica.util.ConnectionManager
import com.mystrica.util.Constant
import com.mystrica.util.Utils
import kotlinx.android.synthetic.main.fragment_warmup.view.*
import java.util.concurrent.TimeUnit
import kotlin.experimental.and


class WarmupFragment : Fragment(),View.OnClickListener {

    var rootview : View ?= null
    private var progressBar: ProgressBar? = null

    var countDownTimer: MyWarmCountDownTimer? = null
    val strBtnTitle="START DEVICE WARM UP"
    val interval_one_second: Long = 1000
    private val handler = Handler()


    private var progressStatus = 0
    private var progressMaxSecond = 180
    private var progress_inteval = 0
    private var maxStat = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootview= inflater.inflate(R.layout.fragment_warmup, container, false)

        (activity as DashboardActivity?)!!.HeadetTitle("Warm up")
        (activity as DashboardActivity?)!!.visibleBackButton()
        progressBar = rootview!!.findViewById(R.id.prg_qc)
        rootview?.txtQc_time?.text="00:00"
        WarmUpTime_setup()


        val getMiliSecond: Long = Utils.pref_get_long(
            Constant.qcStartmiliSecond,
            requireContext(),
            Constant.maxDefaultMinit
        )

        progress_inteval = getMiliSecond.toInt() / 1000
        progressMaxSecond = progress_inteval
        rootview?.prg_qc?.max=progressMaxSecond

        countDownTimer = MyWarmCountDownTimer(
            getMiliSecond /*3 mint*/,
            interval_one_second,
            requireContext(),requireActivity(),rootview!!
        )

        changeTextValue(strBtnTitle)


        rootview?.txtStartQc?.setOnClickListener (this)
        rootview?.txtSkipWarmUp?.setOnClickListener(this)

        return  rootview
    }

    private fun WarmUpTime_setup() {
        val getMintes = "5"
        val getInterval = "1"
        val m = getMintes.toInt()
        val s = getInterval.toInt()
        val millis = m * 60 * 1000.toLong()
        val sec = s * 1000.toLong()

//        Log.i("minites","milil :"+millis+" Seconds :"+sec);

        //  Toast.makeText(getActivity(),"milil :"+millis+" Seconds :"+sec,Toast.LENGTH_LONG).show();
        val milisecondset = millis
        Utils.pref_set_long(Constant.qcStartmiliSecond, milisecondset, requireActivity())

    }

    companion object {

        @JvmStatic
        fun newInstance() =
            WarmupFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

    override fun onClick(p0: View?) {

        when(p0?.id){
            R.id.txtStartQc->{

                val text = rootview?.txtStartQc?.text.toString()
                callOperation()

                if(text.equals(strBtnTitle)){
                    startCounter()
                    disableButtons()
                    changeTextValue("Ok")
                }else{
                    Utils.replaceFragment(requireActivity(), HomeFragment(), true, ConnectBluetothFragment::class.java.name)

                }

            }
            R.id.txtSkipWarmUp->{
                callOperation()

                Utils.replaceFragment(requireActivity(), HomeFragment(), true, ConnectBluetothFragment::class.java.name)
            }
        }
    }

    //todo custom Counter Class
    class MyWarmCountDownTimer(
        var startTime: Long,
        var interval: Long,
        var applicationContext: Context,
        var requireActivity: FragmentActivity,
       var rootview: View
    ) : CountDownTimer(startTime, interval) {
        var getMiliSecond: Long = Utils.pref_get_long(Constant.qcMaxmiliSecond,   applicationContext, Constant.maxDefaultMinit)
        var addExtra4second: Long = 24000
        override fun onFinish() {
            //val second = ((getMiliSecond - DashboardActivity.currentUpdateMilisecond) / 1000) as Int
            val time = "00 : 00"

            rootview?.txtQc_time?.text=time
            enableButtons()
        }

        override fun onTick(millisUntilFinished: Long) {
            val remainedSecs = millisUntilFinished / 1000
            val mint = (remainedSecs / 60).toString()
            val sec = (remainedSecs % 60).toString()
            val time = "$mint : $sec"

            rootview?.txtQc_time?.text=time
            val currentMiliSecond = getMiliSecond - addExtra4second
            if (millisUntilFinished == currentMiliSecond) {
                onFinish()
//                enableButtons()

            }
        }

        private fun enableButtons(){
            rootview?.txtSkipWarmUp?.visibility=View.VISIBLE
            rootview?.txtStartQc?.visibility=View.VISIBLE
        }

    }

    private fun enableButtons(){
        rootview?.txtSkipWarmUp?.isEnabled=true
        rootview?.txtStartQc?.isEnabled=true
    }

    private fun disableButtons(){
        rootview?.txtSkipWarmUp?.visibility=View.INVISIBLE
        rootview?.txtStartQc?.visibility=View.INVISIBLE
    }

    private fun changeTextValue(textTitle: String){
        rootview?.txtStartQc?.text=textTitle
    }

    fun startCounter() {
        countDownTimer!!.start()

        startTimerProgress()
    }
    private fun startTimerProgress() {
        Thread(Runnable {
            while (progressStatus < progressMaxSecond) {
                progressStatus += 1
                // Update the progress bar and display the
                //current value in the text view
                handler.post {
                    progressBar!!.progress = progressStatus
                    maxStat = progressBar!!.max

                    Log.wtf("progress ", "progress stats :" + progressStatus + "/" + maxStat);
                    if (progressStatus == progressMaxSecond) {

                    }
                }
                try {
                    // Sleep for 200 milliseconds.
                    Thread.sleep(interval_one_second)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }).start()
    }

    private fun callOperation(){
        val custom_byte = Constant.intconvert.toByte()
        val pwmBitValue = Constant.pwmconvert.toByte()

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
        val device = ConnectionManager.getDevice()
        DashboardActivity.isCalibrated = true


        ConnectionManager.writedCharetistic(device,Constant.MYSTERIA_CHARACTERISTIC_TEST_UUID,"setled",setired,false)
        ConnectionManager.writedCharetistic(device,Constant.MYSTERIA_CHARACTERISTIC_TEST_UUID,"setCalibrate",setcalibrate,true)
        ConnectionManager.setEnableNotification(device,Constant.MYSTERIA_CHARACTERISTIC_RESILT_UUID,"setResult")

        ConnectionManager.writedCharetistic(device,Constant.MYSTERIA_CHARACTERISTIC_TEST_UUID,"setPwm",setpwm,false)
        ConnectionManager.writedCharetistic(device,Constant.MYSTERIA_CHARACTERISTIC_TEST_UUID,"setCurrent",setcurrent,false)

    }

}