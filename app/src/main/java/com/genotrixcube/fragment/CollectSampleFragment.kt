package com.genotrixcube.fragment

import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.data.Entry
import com.genotrixcube.DatabaseHendler.DatabaseHelper
import com.genotrixcube.R
import com.genotrixcube.activity.CoverActivity
import com.genotrixcube.activity.DashboardActivity
import com.genotrixcube.adpter.SampleDataAdpter
import com.genotrixcube.fragment.ConnectBluetothFragment
import com.genotrixcube.fragment.GetClientIdFragment.Companion.strPatinetId
import com.genotrixcube.model.FileDataDO
import com.genotrixcube.model.ReocrdDataHeaderDO
import com.genotrixcube.util.Constant
import com.genotrixcube.util.Utils
import java.math.RoundingMode
import java.text.DecimalFormat


class CollectSampleFragment : Fragment {
    private var rootview: View? = null
    private var txtTitle: TextView? = null
    private var txtMsg: TextView? = null
    private var txtViewReults: TextView? = null
    private var txtMin: TextView? = null
    private var txtMax: TextView? = null
    private var txtMessage: TextView? = null
    private var txtNochartMessage : TextView ?= null
    private var txtStartMessage : TextView ?= null
    private var txtStart : Button?= null
    private var ly: LinearLayout? = null
    private val lyResult: RelativeLayout? = null
    private var progressBar: ProgressBar? = null
    private var strHeaderTitle = ""

    private var progressStatus = 0
    private var progressMaxSecond = 0
    private var maxStat = 0
    private val handler = Handler()
    var countSeconds: MyCountDownTimerSecondUPdate? = null
    var adpter: SampleDataAdpter? = null

    private val interval_one_second: Long = 1000 // 10 sec in milisecond
    private var maxTimeInMilliseconds: Long = 0 //  mint in milisec
    private var progress_inteval = 0 // get from mili seconds (milis /1000)

    var graphfragment: Fragment? = null
    var isStart : Boolean ?= false

    var strTitleStor: String =""

    var getCurentTimeSetting :Int=0
    constructor() {}

    override fun onStart() {
        super.onStart()
    }

    constructor(strHeaderTitle: String) {
        this.strHeaderTitle = strHeaderTitle
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootview = inflater.inflate(R.layout.fragment_connect_bluetoth, container, false)
        (activity as DashboardActivity?)!!.HeadetTitle(strHeaderTitle)
        (activity as DashboardActivity?)!!.hideBackButton()
        (activity as DashboardActivity?)!!.visibleHomeButton()

        Utils.hideKeyboard(requireContext())


        prefValues


        setupViews()
        if(strHeaderTitle.equals("Run Test")){
            strTitleStor="Run Result"
            txtStartMessage!!.text="Press 'START' to start the Test"
        }else{
            strTitleStor="QC Result"
            txtStartMessage!!.text="Press 'START' to start the QC"

        }

        setupCounterTime_textValueset()

        val today: String = Utils.TodayDate()!!
        txtTitle!!.text =  strPatinetId.toString() + "-" + today

        val getCurrentDrops = Utils.pref_get_int_default(Constant.drops,requireContext(),3)
        txtMsg!!.text = "Pour "+getCurrentDrops.toString()+" drops in the sample tube, shake it while it remains in the holder"

        txtViewReults!!.setOnClickListener {
            var fragment: ResultFragment? = null
            //check for array null or not
            val passingData =
                setupdAdpter()
            if (passingData != null) {
                if (passingData.size >= 1) {
                    fragment = ResultFragment(strHeaderTitle)

                    Utils.replaceFragment(requireActivity(),fragment,true,ConnectBluetothFragment::class.java.name)

                }
            }
        }


        txtStart!!.setOnClickListener {
            visibleGraph()
        }

        return rootview
    }


    private fun visibleGraph(){


        txtNochartMessage!!.visibility=View.GONE

        setupGraphFragment()

        DashboardActivity.isSendingData=false
        DashboardActivity.countDownTimer?.start()

        val strAcquistionStartTime=Utils.TodayDate()+" "+ Utils.TodayTime12hrs()
        Utils.pref_set_String(Constant.acquisistionStartTime,strAcquistionStartTime,requireContext())

        isStart=true
        startCounter()

    }

    private val prefValues: Unit
        private get() {
            val getMiliSecond = Utils!!.pref_get_long(
                Constant.qcMaxmiliSecond,
                activity!!,
                Constant.maxDefaultMinit
            )
            val getinterval = Utils!!.pref_get_long(
                Constant.qcintervalSecond,
                activity!!,
                Constant.maxDefalultInterval
            )
            val intervaladd: Long = Constant.addExtraIntrval
            maxTimeInMilliseconds = getMiliSecond - intervaladd
            progress_inteval = maxTimeInMilliseconds.toInt() / 1000
            progressMaxSecond = progress_inteval
            DashboardActivity.currentValue4Yaxis = 0.1f

        }

    private fun setupCounterTime_textValueset() {

        val getMiliSecond = Utils!!.pref_get_long(
            Constant.qcMaxmiliSecond,
            requireActivity(),
            Constant.maxDefaultMinit
        )
        val intervaladd: Long = Constant.addExtraIntrval

       val  maxTimeInMilliseconds = getMiliSecond - intervaladd

        countSeconds = MyCountDownTimerSecondUPdate(
            maxTimeInMilliseconds /*3 mint*/,
            interval_one_second
        )
        val mintime : String=Utils.miliMaxSecondToString(getMiliSecond)!!

        txtMax!!.text = "$mintime "
        txtMin!!.text = "$mintime "
    }

    private fun setupdAdpter(): ArrayList<Entry>? {
        return (graphfragment as GraphFragment?)!!.resultArray()!!!!
    }

    private fun setupViews()
    {

        txtTitle = rootview!!.findViewById(R.id.txt_type_title)
        txtMsg = rootview!!.findViewById(R.id.txt_blue_text_connecty)
        txtViewReults = rootview!!.findViewById(R.id.txt_connect)
        ly = rootview!!.findViewById(R.id.ly_pr_timer)
        txtMessage = rootview!!.findViewById(R.id.txt_message_graph)
        txtStart=rootview!!.findViewById(R.id.txt_start)
        txtNochartMessage=rootview!!.findViewById(R.id.txt_no_chart_data)
        txtStartMessage=rootview!!.findViewById(R.id.txt_start_test_title)

        txtMessage!!.setVisibility(View.GONE)
        txtNochartMessage!!.setVisibility(View.VISIBLE)
        txtMin = rootview!!.findViewById(R.id.txt_min_mints)
        txtMax = rootview!!.findViewById(R.id.txt_max_mint)
        txtViewReults!!.setText("VIEW RESULT")
        progressBar = rootview!!.findViewById(R.id.pr_collect_data)
        progressBar!!.setVisibility(View.VISIBLE)
        progressBar!!.setMax(progress_inteval)
        ly!!.setVisibility(View.VISIBLE)
        graphfragment = GraphFragment(true)

        storeData = ArrayList()
        tempData =
            ArrayList()
        if (storeData != null && tempData != null) {
            storeData!!.clear()
            storeData!!.clear()
            tempData!!.clear()
        }



    }

    private fun setupGraphFragment() {
        val transaction =
            childFragmentManager.beginTransaction()
        transaction.replace(R.id.graph_container, graphfragment!!).commit()
    }

    fun addData(data: String?, Second: Int, byteArraydata: ByteArray?) {


            val grahp = GraphFragment(true)
            if (graphfragment != null) {

                if(isStart!!){

                    (graphfragment as GraphFragment).addGraphPloatData(data!!, Second, byteArraydata!!)
                }

            }


    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onResume() {
        super.onResume()
        countSeconds!!.cancel()
        progressBar!!.progress = 0
        ly!!.visibility = View.VISIBLE
        txtViewReults!!.isEnabled=false


    }

    fun stopTimer() {
        countSeconds!!.cancel()
    }

    fun startCounter() {
        countSeconds!!.start()
        //timer for 3 mint
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
                     Log.wtf("progress ", "progress stats :" + progressStatus + "/" + progressBar!!.getMax());
                    if (progressStatus == progressMaxSecond) {
                        DashboardActivity.isCalibrated = false
                        //  txtConnect.setVisibility(View.VISIBLE);
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

    override fun onDestroy() {
        super.onDestroy()
        countSeconds!!.cancel()
    }

    fun enableButtons(){
        txtViewReults!!.isEnabled=true
        txtStart!!.isEnabled=false

        val strTestLastTime=Utils.TodayDate() + " "+Utils.TodayTime12hrs()
        Utils.pref_set_String(Constant.test_date,Utils.TodayDate(),requireContext())
        Utils.pref_set_String(Constant.test_time,Utils.TodayTime12hrs(),requireContext())

        if(!strHeaderTitle.equals("Run Test")){
            Utils.pref_set_String(Constant.lastQcDateTime,strTestLastTime,requireContext())
        }
    }
    // coustom counter class create.
    inner class MyCountDownTimerSecondUPdate(
        startTime: Long,
        interval: Long
    ) : CountDownTimer(startTime, interval) {
        override fun onFinish() {
//            Log.e("Times up", "Times up");
            setTextTimer("00 : 00")


          //  wireJsonObj()
        }

        override fun onTick(millisUntilFinished: Long) {
            txtMessage!!.visibility = View.GONE
            txtStart!!.isEnabled=false
            var time = ""
            val remainedSecs = millisUntilFinished / 1000
            var mint = (remainedSecs / 60).toString()
            var sec = (remainedSecs % 60).toString()
            if(mint.length<2){
                mint = "0"+mint
            }

            if(sec.length<2){
                sec="0"+sec
            }
            time = "$mint : $sec "
            val minvalue = mint.toInt()
//            if (minvalue == 0) {
//                time = "$sec "
//            }

            setTextTimer(time)
        }

        init {
            txtMessage!!.visibility = View.GONE
        }
    }

    private fun setTextTimer(time: String) {
        txtMin!!.text = time
    }

    companion object {
        var storeData: ArrayList<Entry>? = null
        var tempData: ArrayList<Entry>? = null
    }

    private fun setUpDeviceUniqueName() {
        val strDeviceName=Utils.pref_get_String(Constant.deviceName,requireContext())

        val uniqueId=Utils.pref_get_int_default(Constant.deviceUniqueId,requireContext(),0)

        val udpateUniqueId=uniqueId+ 1

        Utils.pref_set_String(Constant.deviceUniqueName,"000"+udpateUniqueId,requireContext())
        Utils.pref_set_int(Constant.deviceUniqueId,udpateUniqueId,requireContext())
        Log.wtf("uniqueId","Device name :"+uniqueId + ":"+udpateUniqueId)

    }


    fun addDBRecord(){
        try{
            DashboardActivity.countDownTimer!!.onFinish()
            DashboardActivity.countDownTimer!!.cancel()


            enableButtons()
            setUpDeviceUniqueName()

            val databaseHandler: DatabaseHelper = DatabaseHelper(requireActivity().applicationContext)

            val dataResult =(graphfragment as GraphFragment).resultArray()


            var getLastRecordId : String=databaseHandler.getLastRecord()

            var convertInt =0

            if(getLastRecordId!=""){

                val getLastId=getLastRecordId.toInt()
                if(getLastId>0){
                    convertInt=getLastId+1
                }
            }
            else{
                convertInt=1
            }

            val limitSecond=Utils.pref_get_int(Constant.limitMilisecond,requireContext())

            val precision = DecimalFormat("0.0000")
            precision.setRoundingMode(RoundingMode.DOWN);

            for(resultDo in dataResult!!) {

                val x = resultDo.x
                val y = resultDo.y


                if ((x == 0f) && (y == 0f)) {

                } else {

                    val recordId : String=convertInt.toString()

                    val getinterval = Utils!!.pref_get_long(
                        Constant.qcintervalSecond,
                        requireActivity(),
                        Constant.maxDefalultInterval
                    )

                    val intervaladd: Long =  Constant.addExtraIntrval


                    val getMiliSecond = Utils!!.pref_get_long(
                        Constant.qcMaxmiliSecond,
                        requireActivity()!!,
                        Constant.maxDefaultMinit
                    )


                    val getFlagVlaue = Utils!!.pref_get_int_default(
                        Constant.estimateFlag,
                        requireActivity(),1
                    )

                    val maxtime_miliscond=getMiliSecond-intervaladd

                    val maxtime: String = Utils.pref_get_String(Constant.maxMint,requireContext())!!

                    val patientId: String=strPatinetId
                    val regentId: String= RegentIdFragment.strRegentId
                    val userid: String= CoverActivity.strUserId
                    val sampletype: String= Constant.selectSubTest
                    val testPerfomed: String="Yes"
                    val testDate: String= Utils.pref_get_String(Constant.test_date,requireContext())!!
                    val testTime: String= Utils.pref_get_String(Constant.test_time,requireContext())!!
                    val lastQcDateTime: String=Utils.pref_get_String(Constant.lastQcDateTime,requireContext())!!
                    val qcPerformed: String

                    if((!(lastQcDateTime.equals("")))){
                        qcPerformed="Yes"

                    }
                    else{
                        qcPerformed="No"
                    }

                    val reactionDuration: String=maxtime

                    val interval: String=Utils!!.pref_get_int_default(Constant.testinterval,requireContext(),5).toString()
                    val sampleAddDateTime: String=Utils.pref_get_String(Constant.sample_date_time,requireContext())!!
                    val acquistionDateTime: String=Utils.pref_get_String(Constant.acquisistionStartTime,requireContext())!!
                    val sampleOk: String="Yes"
                    val deviceName: String=Utils.pref_get_String(Constant.deviceName,requireContext())!!
                    val softwareVersion: String="v"+Utils.getAppVersion()

                    val result_x_vaule=resultDo.x.toInt()
                    val process_flagValue: String
                    if(result_x_vaule==limitSecond){
                        process_flagValue="1"
                    }
                    else{
                        process_flagValue="0"
                    }


                    val filename: String = Utils.pref_get_String(Constant.deviceName,requireContext())!!+"_"+Utils.pref_get_String(Constant.deviceUniqueName,requireContext())!!+"_"+Utils.TodayDate()+"_"+Utils.TodayTime12hrs()+"_"+strPatinetId

                 val reactionResult= precision.format( resultDo.y)
                    val intreactionResultTime=resultDo.x.toInt()

                val status = databaseHandler.addTestRecord(
                    FileDataDO(recordId,intreactionResultTime.toString(),reactionResult,patientId,regentId,userid,sampletype,testPerfomed,testDate,testTime,qcPerformed,lastQcDateTime,reactionDuration,
                        interval,process_flagValue,sampleAddDateTime,acquistionDateTime,sampleOk,deviceName,softwareVersion,filename,strTitleStor,limitSecond.toString(),maxtime_miliscond.toString(),getFlagVlaue.toString())
                )
                if(status > -1)
                {
                    Log.e("reocrd save","record save"+ resultDo.x.toString())
                }
                else{
                    Log.e("reocrd save","id or name or email cannot be blank")
                }

                }


            }

            //put blank line into the dp after success fully one record add.
            val empt1 = databaseHandler.addTestRecord(
                FileDataDO(convertInt.toString(),"","","","","","","","","","","","",
                    "","","","","","","","" ,"","","","")
            )
            val empt2 = databaseHandler.addTestRecord(
                FileDataDO(convertInt.toString(),"","","","","","","","","","","","",
                    "","","","","","","","","","","","")
            )

            val empt3 = databaseHandler.addTestRecord(
                FileDataDO(convertInt.toString(),"","","","","","","","","","","","",
                    "","","","","","","","" ,"","","","")
            )


            val testDate: String= Utils.pref_get_String(Constant.test_date,requireContext())!!
            val testime: String= Utils.pref_get_String(Constant.test_time,requireContext())!!


            val addRordInview=databaseHandler.addRecordView(ReocrdDataHeaderDO(convertInt.toString(),strPatinetId,RegentIdFragment.strRegentId,CoverActivity.strUserId,testDate,testime))

            if(addRordInview> -1){
                Log.e("reocrd save","record save"+ addRordInview)

            }else{
                Log.e("reocrd save","record save"+ addRordInview)
            }

        }catch (e: Exception){
            e.printStackTrace()
        }
    }


}