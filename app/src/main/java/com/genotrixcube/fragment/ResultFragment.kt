package com.genotrixcube.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import au.com.bytecode.opencsv.CSVWriter
import com.github.mikephil.charting.data.Entry
import com.genotrixcube.R
import com.genotrixcube.activity.CoverActivity.Companion.strUserId
import com.genotrixcube.activity.DashboardActivity
import com.genotrixcube.adpter.ResultValuesAdpter
import com.genotrixcube.fragment.GetClientIdFragment.Companion.strPatinetId
import com.genotrixcube.fragment.RegentIdFragment.Companion.strRegentId
import com.genotrixcube.util.Constant
import com.genotrixcube.util.Utils
import com.genotrixcube.util.Utils.TodayDate
import com.genotrixcube.util.Utils.TodayTime
import org.jetbrains.anko.find
import java.io.File
import java.io.FileWriter
import java.util.*


class ResultFragment(strHeaderTitle: String) : Fragment() {
    private var rootview: View? = null
    private var txtTitle: TextView? = null
    private var txtToday: TextView? = null
    private var txttodayTime: TextView? = null
    private var txtSend: TextView? = null
    private var txtNewTest : TextView?= null
    private var txtUserid: TextView? = null
    private var txtPatentid: TextView? = null
    private var txtRegentid: TextView? = null
    private var txtsubtype: TextView? = null
    private var txtDeviceName : TextView ?= null
    private var txtSoftwareVersion : TextView ?= null
    private var txt_testPerformed : TextView ?= null
    private var txt_qcPerformed : TextView ?= null
    private var txt_test_date_time : TextView ?= null
    private var txt_interval : TextView ?= null
    private var txtQc_perform_flag: TextView? = null
    private var txt_qc_last_date_time : TextView ?= null
    private var txt_sample_add_date_time : TextView ?= null
    private var txt_acquisition_date_time : TextView ?= null
    private var txt_reaction_duration : TextView ?= null
    private var txt_sample_ok : TextView ?= null
    private var txt_new_test : TextView ?= null
    private var recyValueSecond: RecyclerView? = null
    private val lyQc: LinearLayout? = null
    private var strHeaderitle = ""
    private var today: String = ""
    private var todayTime = ""
    private val strDeviceName = ""
    var adpter: ResultValuesAdpter? = null
    var graphFragment: Fragment? = null


    init {
        strHeaderitle = strHeaderTitle
    }

    companion object {

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootview = inflater.inflate(R.layout.fragment_result, container, false)

        var title : String

        if(strHeaderitle.equals("Run Test")){
            title="Run Result"
        }else{
            title="QC Result"
        }
        (activity as DashboardActivity?)!!.HeadetTitle(title)
        (activity as DashboardActivity?)!!.hideBackButton()
        (activity as DashboardActivity?)!!.visibleHomeButton()

        DashboardActivity.countDownTimer!!.cancel()

        setupviews()
        today = TodayDate()!!
        todayTime = TodayTime()!!
        txtTitle!!.text =  strPatinetId.toString() + "-" + today


        txtSend!!.setOnClickListener { //code for counter with date sele

            if(Utils.isNetConnected(requireContext())){

//                CollectSampleFragment.storeData!!.clear()

                createFile()

            }else{
                Utils.showToast(requireContext(),"No Internet Connect")

            }

        }


        txt_new_test?.setOnClickListener {
            val fragment = SendEmailFragment(strHeaderitle)
            (activity as DashboardActivity?)!!.callNewTestDashboard()
        }
        return rootview
    }


    private val meYesterday: Date
        private get() = Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000)

    private fun setupviews() {
        txtTitle = rootview!!.findViewById(R.id.txt_result_tilte)
        txtUserid = rootview!!.findViewById(R.id.txt_userid4result)
        txtPatentid = rootview!!.findViewById(R.id.txt_patinetid4result)
        txtRegentid = rootview!!.findViewById(R.id.txt_regentid4reslut)
        txtToday = rootview!!.findViewById(R.id.txt_today4result)
        txttodayTime = rootview!!.findViewById(R.id.txt_todayTime4result)

        txtDeviceName=rootview!!.find(R.id.txt_device4result)
        txtSoftwareVersion=rootview!!.findViewById(R.id.txt_softwareVersiond4result)
        txt_testPerformed=rootview!!.findViewById(R.id.txt_testPerfomed4result)
        txt_qcPerformed=rootview!!.findViewById(R.id.txt_qcPerformed4result)
        txt_test_date_time=rootview!!.findViewById(R.id.txt_test_date_time4result)
        txt_qc_last_date_time=rootview!!.findViewById(R.id.txt_last_qc_time4result)
        txt_interval=rootview!!.findViewById(R.id.txt_intervals4result)
        txtQc_perform_flag=rootview!!.findViewById(R.id.txt_process_flag4result)
        txt_sample_add_date_time = rootview!!.findViewById(R.id.txt_sample_add_date_time4result)
        txt_sample_ok = rootview!!.findViewById(R.id.txt_sample_ok_time4result)
        txt_reaction_duration = rootview!!.findViewById(R.id.txt_reaction_duration4result)
        txt_acquisition_date_time=rootview!!.findViewById(R.id.txt_acquisition_date_time4result)

        txtSend = rootview!!.findViewById(R.id.txt_send_result)
        txt_new_test = rootview!!.findViewById(R.id.txt_new_test)

        if(!strHeaderitle.equals("Run Test")){
            txt_new_test!!.text="NEW QC"
        }

        txtsubtype = rootview!!.findViewById(R.id.txt_sub_type4result)
        recyValueSecond = rootview!!.findViewById(R.id.recy_result4ValueSeconds)
        recyValueSecond!!.setHasFixedSize(true)
        recyValueSecond!!.setLayoutManager(LinearLayoutManager(activity))
        graphFragment = GraphFragment()

        setupGraphFragment()
        displayId()
        setupResultvalue()
    }

    private fun setupResultvalue() {
        var resultdata: ArrayList<Entry>? = null
        try {
            resultdata =(graphFragment as GraphFragment).resultArray()
            resultdata!!.removeAt(0)
            setupdAdpter(resultdata)



        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setupdAdpter(resultdata: ArrayList<Entry>?) {

        adpter = ResultValuesAdpter(resultdata!!, requireActivity(), this@ResultFragment)
        recyValueSecond!!.adapter = adpter
    }


    private fun displayId() {
        val today: String = TodayDate()!!
        val todayTime: String = TodayTime()!!
        txtToday!!.text = today
        txttodayTime!!.text = todayTime

        txtRegentid!!.setText(strRegentId)

        //details sets

        txtDeviceName?.text = Utils.pref_get_String(Constant.deviceName, requireContext())
        txtSoftwareVersion?.text = "v"+Utils.getAppVersion()
        txtUserid!!.setText(strUserId)
        txtPatentid!!.setText(strPatinetId)
        txtsubtype!!.text = Constant.selectSubTest
        txt_testPerformed!!.text = "Yes"
        txt_test_date_time!!.text = Utils.pref_get_String(Constant.test_date, requireContext())+" "+Utils.pref_get_String(Constant.test_time,requireContext())

        val lastQcDateTime: String=Utils.pref_get_String(Constant.lastQcDateTime,requireContext())!!

        val qcPerformed : String
        if((!(lastQcDateTime.equals("")))){
            qcPerformed="Yes"

        }
        else{
            qcPerformed="No"
        }
        txt_qcPerformed!!.text = qcPerformed
        txt_qc_last_date_time!!.text = lastQcDateTime
        txt_sample_add_date_time!!.text =
            Utils.pref_get_String(Constant.sample_date_time, requireContext())


        val getMiliSecond = Utils!!.pref_get_long(
            Constant.qcMaxmiliSecond,
            requireActivity(),
            Constant.maxDefaultMinit
        )

        val maxtime: String = Utils.pref_get_String(Constant.maxMint,requireContext())!!
        val getinterval = Utils!!.pref_get_long(
            Constant.qcintervalSecond,
            requireActivity(),
            Constant.maxDefalultInterval
        )
        val interval = Utils!!.miliSecondToSecond(getinterval)

        txt_reaction_duration!!.text = maxtime.toString() + "m"
        txt_interval!!.text = interval.toString() + "s"
        txtQc_perform_flag!!.text = Utils.pref_get_int_default(Constant.estimateFlag, requireContext(),1).toString() +"m"
        txt_acquisition_date_time!!.text =
            Utils.pref_get_String(Constant.acquisistionStartTime, requireContext())
        txt_sample_ok!!.text = "Yes"

    }

    private fun setupGraphFragment() {
        val transaction =
            childFragmentManager.beginTransaction()
        transaction.replace(R.id.graph_container, graphFragment!!).commit()
    }

    private fun createFile() {
        try{

            val directory: File = Utils.getDirectory(requireContext())!!

            if(directory!= null) {

                val filename: String = Utils.pref_get_String(
                    Constant.deviceName,
                    requireContext()
                )!!+"_" + Utils.pref_get_String(
                    Constant.deviceUniqueName,
                    requireContext()
                )!! + "_" + Utils.TodayDate() + "_" + Utils.TodayTime12hrs() + "_" + strPatinetId

                val f = File(
                    directory, filename + ".csv"
                )
                f.createNewFile()

                val fileWriter = FileWriter(f)

                val writer = CSVWriter(fileWriter)

                val header = listOf("Reaction Time (Seconds)", "Reaction Result", "Patient ID","Reagent ID",
                    "User ID","Sample Type","Test Performed","Testing Date and Time","QC Performed","Last QC Date and Time","Reaction Duration",
                    "Intervals","End of Process Flag","Sample Addition Date and Time","Acquision Start Date and Time",
                    "Sample Addition / Reaction Started by Pressing OK","Device ID","Software Version","ID")

                val dataResult =(graphFragment as GraphFragment).resultArray()


                writer.writeNext(header.toTypedArray())



                for(resultDo in dataResult!!) {

                    val getinterval = Utils!!.pref_get_long(
                        Constant.qcintervalSecond,
                        requireActivity(),
                        Constant.maxDefalultInterval
                    )

                    val intervaladd: Long = 24000


                    val getMiliSecond = Utils!!.pref_get_long(
                        Constant.qcMaxmiliSecond,
                        requireActivity(),
                        Constant.maxDefaultMinit
                    )

                    val maxTimeInMilliseconds = getMiliSecond - intervaladd

                    val maxtime: String = Utils.pref_get_String(Constant.maxMint,requireContext())!!



                    val patientId: String = strPatinetId
                    val regentId: String = strRegentId
                    val userid: String = strUserId
                    val sampletype: String = Constant.selectSubTest
                    val testPerfomed: String = "Yes"
                    val testDateTime: String = Utils.pref_get_String(Constant.test_date, requireContext())!!+" "+Utils.pref_get_String(Constant.test_time,requireContext())

                    val lastQcDateTime: String =
                        Utils.pref_get_String(Constant.lastQcDateTime, requireContext())!!
                    val reactionDuration: String =maxtime

                    val qcPerformed: String
                    if((!(lastQcDateTime.equals("")))){
                        qcPerformed="Yes"

                    }
                    else{
                        qcPerformed="No"
                    }
                    val interval: String = Utils!!.miliSecondToSecond(getinterval).toString()
                    val qcPerfomFlag: String =Utils.pref_get_int_default(Constant.estimateFlag, requireContext(),1).toString()
                    val sampleAddDateTime: String =
                        Utils.pref_get_String(Constant.sample_date_time, requireContext())!!
                    val acquistionDateTime: String =
                        Utils.pref_get_String(Constant.acquisistionStartTime, requireContext())!!
                    val sampleOk: String = "Yes"
                    val deviceName: String =
                        Utils.pref_get_String(Constant.deviceName, requireContext())!!
                    val softwareVersion: String = "v"+Utils.getAppVersion()

                    val limitSecond=Utils.pref_get_int(Constant.limitMilisecond,requireContext())
                    val getFlagVlaue = Utils!!.pref_get_int_default(
                        Constant.estimateFlag,
                        requireActivity(),1
                    )

                    val result_x_vaule=resultDo.x.toInt()
                    val process_flagValue: String
                    if(result_x_vaule==limitSecond){
                        process_flagValue="1"
                    }
                    else{
                        process_flagValue="0"
                    }

                    val intreactionResult=resultDo.x.toInt()


                    val data = listOf(
                        intreactionResult.toString(),
                        resultDo.y.toString(),
                        patientId,
                        regentId,
                        userid,
                        sampletype,
                        testPerfomed,
                        testDateTime,
                        qcPerformed,
                        lastQcDateTime,
                        reactionDuration,
                        interval,
                        process_flagValue,
                        sampleAddDateTime,
                        acquistionDateTime,
                        sampleOk,
                        deviceName,
                        softwareVersion,
                        filename
                    )

                    writer.writeNext(data.toTypedArray())

                }
                // closing writer connection
                writer.close()

                sendEmail(f.path)
            }

        }
        catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun sendEmail(file: String) {

        try {
            val intentShareFile = Intent(Intent.ACTION_SEND)

            val f = File(file)

            val filedir = File(file)
            if(filedir.exists()) {
//                intentShareFile.setType("text/*");
                intentShareFile.setType("message/rfc822")

                val authority = requireContext().packageName + ".provider"
                val uri = FileProvider.getUriForFile(requireContext(), authority, f)

                intentShareFile.putExtra(Intent.EXTRA_STREAM,uri)
                intentShareFile.putExtra(
                    Intent.EXTRA_SUBJECT,
                    "Test Result"
                )
                intentShareFile.putExtra(Intent.EXTRA_TEXT, "Test Result" )
                startActivity(Intent.createChooser(intentShareFile, f.getName()))
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        }

}