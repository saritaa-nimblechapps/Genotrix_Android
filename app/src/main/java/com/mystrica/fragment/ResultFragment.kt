package com.mystrica.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import au.com.bytecode.opencsv.CSVWriter
import com.github.mikephil.charting.data.Entry
import com.mystrica.DatabaseHendler.DatabaseHelper
import com.mystrica.DatabaseHendler.EmpModelClass
import com.mystrica.DatabaseHendler.TestDetailsDO
import com.mystrica.R
import com.mystrica.activity.CoverActivity.Companion.strUserId
import com.mystrica.activity.DashboardActivity
import com.mystrica.adpter.ResultValuesAdpter
import com.mystrica.fragment.GetClientIdFragment.Companion.strPatinetId
import com.mystrica.fragment.RegentIdFragment.Companion.strRegentId
import com.mystrica.model.FileDataDO
import com.mystrica.util.Constant
import com.mystrica.util.Utils
import com.mystrica.util.Utils.TodayDate
import com.mystrica.util.Utils.TodayTime
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
    private var today : String = ""
    private var todayTime = ""
    private val strDeviceName = ""
    var adpter: ResultValuesAdpter? = null
    var graphFragment: Fragment? = null


    companion object{

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootview = inflater.inflate(R.layout.fragment_result, container, false)
        (activity as DashboardActivity?)!!.HeadetTitle(strHeaderitle)
        (activity as DashboardActivity?)!!.visibleBackButton()
        (activity as DashboardActivity?)!!.visibleHomeButton()

//        if(DashboardActivity.communicator!=null){
//            DashboardActivity.communicator.connectToDevice(null);
//        }
        setupviews()
        today = TodayDate()!!
        todayTime = TodayTime()!!
        txtTitle!!.text = "Covid-19-" + strPatinetId.toString() + "-" + today


        txtSend!!.setOnClickListener { //code for counter with date sele

            addRecordDB()

        }


        txt_new_test?.setOnClickListener {
            val fragment = SendEmailFragment(strHeaderitle)
            Utils.replaceFragment(requireActivity(),fragment,true,ResultFragment::class.java.name)

        }
        return rootview
    }

    private fun addRecordDB() {
        try{
            addDBRecord()


        }catch (e:Exception){
            e.printStackTrace()
        }
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
        txt_sample_add_date_time=rootview!!.findViewById(R.id.txt_sample_add_date_time4result)
        txt_sample_ok=rootview!!.findViewById(R.id.txt_acquisition_date_time4result)
        txt_reaction_duration=rootview!!.findViewById(R.id.txt_reaction_duration4result)
        txt_acquisition_date_time=rootview!!.findViewById(R.id.txt_acquisition_date_time4result)

        txtSend = rootview!!.findViewById(R.id.txt_send_result)
        txt_new_test = rootview!!.findViewById(R.id.txt_new_test)
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

        // temp.remove(0);
        adpter = ResultValuesAdpter(resultdata!!, requireActivity(), this@ResultFragment)
        recyValueSecond!!.adapter = adpter
    }

    private fun addDBRecord(){
        try{

            val databaseHandler: DatabaseHelper = DatabaseHelper(requireActivity().applicationContext)

            val dataResult =(graphFragment as GraphFragment).resultArray()

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

            for(resultDo in dataResult!!){

                val recordId : String=convertInt.toString()
                val patientId: String=txtPatentid!!.text.toString()
                val regentId: String=txtRegentid!!.text.toString()
                val userid: String=txtUserid!!.text.toString()
                val sampletype: String=txtsubtype!!.text.toString()
                val testPerfomed: String=txt_testPerformed!!.text.toString()
                val testDateTime: String=txt_test_date_time!!.text.toString()
                val qcPerformed: String=txt_qcPerformed!!.text.toString()
                val lastQcDateTime: String=txt_qc_last_date_time!!.text.toString()
                val reactionDuration: String=txt_reaction_duration!!.text.toString()
                val interval: String=txt_interval!!.text.toString()
                val qcPerfomFlag: String=txtQc_perform_flag!!.text.toString()
                val sampleAddDateTime: String=txt_sample_add_date_time!!.text.toString()
                val acquistionDateTime: String=txt_acquisition_date_time!!.text.toString()
                val sampleOk: String=txt_sample_ok!!.text.toString()
                val deviceName: String=txtDeviceName!!.text.toString()
                val softwareVersion: String=txtSoftwareVersion!!.text.toString()
                val filename: String = Utils.pref_get_String(Constant.deviceName,requireContext())!!+"_"+Utils.TodayDate()+"_"+Utils.TodayTime()+"_"+txtPatentid!!.text


                //COL ggg_0004_14-Dec-2020 01:40 pm_7737747
                val status = databaseHandler.addTestRecord(
                    FileDataDO(recordId,resultDo.x.toString(),resultDo.y.toString(),patientId,regentId,userid,sampletype,testPerfomed,testDateTime,qcPerformed,lastQcDateTime,reactionDuration,
                    interval,qcPerfomFlag,sampleAddDateTime,acquistionDateTime,sampleOk,deviceName,softwareVersion,filename )
                )
                if(status > -1)
                {
                   Log.e("reocrd save","record save"+ resultDo.x.toString())

                }
                else{
                    Log.e("reocrd save","id or name or email cannot be blank")
                }

            }


           // createFile()
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    private fun displayId() {
        val today: String = TodayDate()!!
        val todayTime: String = TodayTime()!!
        txtToday!!.text = today
        txttodayTime!!.text = todayTime

        txtRegentid!!.setText(strRegentId)

        //details sets

        txtDeviceName?.text=Utils.pref_get_String(Constant.deviceName,requireContext())
        txtSoftwareVersion?.text="1.0"
        txtUserid!!.setText(strUserId)
        txtPatentid!!.setText(strPatinetId)
        txtsubtype!!.text =  Constant.selectSubTest
        txt_testPerformed!!.text=Constant.testPerformd
        txt_testPerformed!!.text="Yes"
        txt_test_date_time!!.text=Utils.TodayDate() + " "+Utils.TodayTime()
        txt_qc_last_date_time!!.text=Utils.TodayDate() + " "+Utils.TodayTime()
        txt_sample_add_date_time!!.text=Utils.TodayDate() + " "+Utils.TodayTime()


        val getMiliSecond = Utils!!.pref_get_long(
            Constant.qcMaxmiliSecond,
            requireActivity(),
            Constant.maxDefaultMinit
        )

        val maxtime = Utils.miliSecondToMinites(getMiliSecond)!!

        val getinterval = Utils!!.pref_get_long(
            Constant.qcintervalSecond,
            requireActivity(),
            Constant.maxDefalultInterval
        )
        val interval = Utils!!.miliSecondToSecond(getinterval)

        txt_reaction_duration!!.text=maxtime.toString()+"m"
        txt_interval!!.text=interval.toString()+"s"
        txtQc_perform_flag!!.text=Utils.pref_get_int(Constant.testinterval,requireContext()).toString()
        txt_acquisition_date_time!!.text=Utils.TodayDate() + " "+Utils.TodayTime()
        txt_sample_ok!!.text="Ok"

    }

    private fun setupGraphFragment() {
        val transaction =
            childFragmentManager.beginTransaction()
        transaction.replace(R.id.graph_container, graphFragment!!).commit()
    }

    private fun createFile() {
        try{

            val directory: File = Utils.getDirectory(requireContext())!!

            if(directory!= null){
                val filename: String = Utils.pref_get_String(Constant.deviceName,requireContext())!!+"_"+Utils.TodayDate()+"_"+Utils.TodayTime()+"_"+txtPatentid!!.text

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



                for(resultDo in dataResult!!){

                    val patientId: String=txtPatentid!!.text.toString()
                    val regentId: String=txtRegentid!!.text.toString()
                    val userid: String=txtUserid!!.text.toString()
                    val sampletype: String=txtsubtype!!.text.toString()
                    val testPerfomed: String=txt_testPerformed!!.text.toString()
                    val testDateTime: String=txt_test_date_time!!.text.toString()
                    val qcPerformed: String=txt_qcPerformed!!.text.toString()
                    val lastQcDateTime: String=txt_qc_last_date_time!!.text.toString()
                    val reactionDuration: String=txt_reaction_duration!!.text.toString()
                    val interval: String=txt_interval!!.text.toString()
                    val qcPerfomFlag: String=txtQc_perform_flag!!.text.toString()
                    val sampleAddDateTime: String=txt_sample_add_date_time!!.text.toString()
                    val acquistionDateTime: String=txt_acquisition_date_time!!.text.toString()
                    val sampleOk: String=txt_sample_ok!!.text.toString()
                    val deviceName: String=txtDeviceName!!.text.toString()
                    val softwareVersion: String=txtSoftwareVersion!!.text.toString()

//                    dataList.add(FileDataDO(resultDo.x.toString(),resultDo.y.toString(),
//                        patientId, regentId,userid,sampletype,testPerfomed,testDateTime,qcPerformed,lastQcDateTime,reactionDuration,interval,
//                        qcPerfomFlag,sampleAddDateTime,acquistionDateTime,sampleOk,deviceName,softwareVersion,filename))

                    val data = listOf(resultDo.x.toString(),resultDo.y.toString(),
                        patientId, regentId,userid,sampletype,testPerfomed,testDateTime,qcPerformed,lastQcDateTime,reactionDuration,interval,
                        qcPerfomFlag,sampleAddDateTime,acquistionDateTime,sampleOk,deviceName,softwareVersion,filename)

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


    init {
        strHeaderitle = strHeaderTitle
    }





        private fun sendEmail(file: String) {

        try {
            val intentShareFile = Intent(Intent.ACTION_SEND)

            val f = File(file)

            val filedir = File(file)
            if(filedir.exists()) {
                intentShareFile.setType("text/*");
                intentShareFile.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + file))
                intentShareFile.putExtra(Intent.EXTRA_SUBJECT, "Genotrix File Share: " + f.getName())
                intentShareFile.putExtra(Intent.EXTRA_TEXT, "Genotrix File Share: " + f.getName())
                startActivity(Intent.createChooser(intentShareFile, f.getName()))
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }



}