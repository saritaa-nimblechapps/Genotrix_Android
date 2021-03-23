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
import com.genotrixcube.R
import com.genotrixcube.activity.DashboardActivity
import com.genotrixcube.adpter.ResultSelectUserAdpter
import com.genotrixcube.model.FileDataDO
import com.genotrixcube.util.Constant
import com.genotrixcube.util.Utils
import com.genotrixcube.util.Utils.TodayDate
import com.genotrixcube.util.Utils.TodayTime
import com.github.mikephil.charting.data.Entry

import kotlinx.android.synthetic.main.fragment_result.*
import java.io.File
import java.io.FileWriter
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*


class ResultSelectUserDataFragment(listOfFileDataDO: ArrayList<FileDataDO>?) : Fragment() {
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
    private var txtDeviceName : TextView?= null
    private var txtSoftwareVersion : TextView?= null
    private var txt_testPerformed : TextView?= null
    private var txt_qcPerformed : TextView?= null
    private var txt_test_date_time : TextView?= null
    private var txt_interval : TextView?= null
    private var txtQc_perform_flag: TextView? = null
    private var txt_qc_last_date_time : TextView?= null
    private var txt_sample_add_date_time : TextView?= null
    private var txt_acquisition_date_time : TextView?= null
    private var txt_reaction_duration : TextView?= null
    private var txt_sample_ok : TextView?= null
    private var txt_new_test : TextView?= null
    private var recyValueSecond: RecyclerView? = null
    private val lyQc: LinearLayout? = null

    private var today: String = ""
    private var todayTime = ""
    private val strDeviceName = ""

    var adpter: ResultSelectUserAdpter? = null
    var graphFragment: Fragment? = null

    var listFileDataDO: ArrayList<FileDataDO> ?= arrayListOf()

    init {
        listFileDataDO!!.addAll(listOfFileDataDO!!)
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootview = inflater.inflate(R.layout.fragment_result, container, false)


        var title : String = listFileDataDO!!.get(0).record_title


        (activity as DashboardActivity?)!!.HeadetTitle(title)
        (activity as DashboardActivity?)!!.visibleBackButton()
        (activity as DashboardActivity?)!!.visibleHomeButton()

        setupviews()

        today = TodayDate()!!
        todayTime = TodayTime()!!

        txtTitle!!.text =  listFileDataDO!!.get(0).patientId + "-" + today


        txtSend!!.setOnClickListener { //code for counter with date sele

            if(Utils.isNetConnected(requireContext())){

                createFile()

            }else{
                Utils.showToast(requireContext(), "No Internet Connect")

            }

        }

        txt_print.setOnClickListener {

            createPrintView()
        }

    //    setupGraphFragment()


        return rootview
    }

    private fun createPrintView() {

        val directory: File = Utils.getDirectory(requireContext())!!

//        PdfGenerator.getBuilder()
//            .setContext(context)
//            .fromLayoutXMLSource()
//            .fromLayoutXML(R.layout.fragment_result) /* "fromLayoutXML()" takes array of layout resources.
//			 * You can also invoke "fromLayoutXMLList()" method here which takes list of layout resources instead of array. */
//            .setPageSize(PdfGenerator.PageSize.A4) /* It takes default page size like A4,A5,WRAP_CONTENT.*/
//            .setFileName("Test-PDF") /* It is file name */
//            .setFolderName("Test-PDF-folder") /* It is folder name. If you set the folder name like this pattern (FolderA/FolderB/FolderC), then
//			 * FolderA creates first.Then FolderB inside FolderB and also FolderC inside the FolderB and finally
//			 * the pdf file named "Test-PDF.pdf" will be store inside the FolderB. */
//            .openPDFafterGeneration(true) /* It true then the generated pdf will be shown after generated. */
//            .build(object : PdfGeneratorListener() {
//                override fun onFailure(failureResponse: FailureResponse) {
//                    super.onFailure(failureResponse)
//                    /* If pdf is not generated by an error then you will findout the reason behind it
//				 * from this FailureResponse. */
//                }
//
//                override fun onStartPDFGeneration() {
//                    /*When PDF generation begins to start*/
//                }
//
//                override fun onFinishPDFGeneration() {
//                    /*When PDF generation is finished*/
//                }
//
//                override fun showLog(log: String) {
//                    super.showLog(log)
//                    /*It shows logs of events inside the pdf generation process*/
//                }
//
//                override fun onSuccess(response: SuccessResponse) {
//                    super.onSuccess(response)
//                    /* If PDF is generated successfully then you will find SuccessResponse
//				 * which holds the PdfDocument,File and path (where generated pdf is stored)*/
//                }
//            })

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

        txtDeviceName=rootview!!.findViewById(R.id.txt_device4result)
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
        txtSend!!.setText("SEND CSV")
        txt_new_test = rootview!!.findViewById(R.id.txt_new_test)
        txt_new_test!!.visibility=View.GONE


        txtsubtype = rootview!!.findViewById(R.id.txt_sub_type4result)
        recyValueSecond = rootview!!.findViewById(R.id.recy_result4ValueSeconds)
        recyValueSecond!!.setHasFixedSize(true)
        recyValueSecond!!.setLayoutManager(LinearLayoutManager(activity))



        setupResultvalue()
        displayId()

        setupGraphFragment()
    }

    private fun setupResultvalue() {
        var resultdata: ArrayList<Entry>? = null
        try {
            val arrayEntry =getArrayEntry()

            resultdata =arrayEntry
            setupdAdpter(resultdata)



        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getArrayEntry(): ArrayList<Entry> {

        var resultdata: ArrayList<Entry>? = arrayListOf()

        for(listDataDo in listFileDataDO!!){
            val decimalFormat = DecimalFormat("0.000")
            decimalFormat.setRoundingMode(RoundingMode.DOWN);

            val time=listDataDo.reactionTime.toFloat()
            val gety=listDataDo.reactionResult.toFloat()


            val result = decimalFormat.format(gety)


            resultdata!!.add(Entry(time, result.toFloat()))
        }
        return resultdata!!
    }

    private fun setupdAdpter(resultdata: ArrayList<Entry>?) {

        val getEastimatedLiminit=listFileDataDO!!.get(0).estimated_second

        adpter = ResultSelectUserAdpter(
            resultdata!!,
            requireActivity(),
            this@ResultSelectUserDataFragment,
            getEastimatedLiminit
        )
        recyValueSecond!!.adapter = adpter
    }


    private fun displayId() {
        val today: String = TodayDate()!!
        val todayTime: String = TodayTime()!!
        txtToday!!.text = today
        txttodayTime!!.text = todayTime

        txtRegentid!!.setText(listFileDataDO!!.get(0).regentId)

        //details sets

        txtDeviceName?.text = listFileDataDO!!.get(0).deviceId
        txtSoftwareVersion?.text = listFileDataDO!!.get(0).softwareVersion
        txtUserid!!.setText(listFileDataDO!!.get(0).userId)
        txtPatentid!!.setText(listFileDataDO!!.get(0).patientId)
        txtsubtype!!.text = listFileDataDO!!.get(0).sampleType
        txt_testPerformed!!.text =listFileDataDO!!.get(0).testPerformed
        txt_test_date_time!!.text = listFileDataDO!!.get(0).testDate+" "+listFileDataDO!!.get(0).testTime


        txt_qcPerformed!!.text = listFileDataDO!!.get(0).qcPerformed
        txt_qc_last_date_time!!.text =listFileDataDO!!.get(0).lastQcDateTime
        txt_sample_add_date_time!!.text = listFileDataDO!!.get(0).sampleDateTime


        txt_reaction_duration!!.text =listFileDataDO!!.get(0).reactionDuratio+"m"
        txt_interval!!.text =listFileDataDO!!.get(0).interval+"s"
        txtQc_perform_flag!!.text = listFileDataDO!!.get(0).estimated_flag_mint+"m"
        txt_acquisition_date_time!!.text = listFileDataDO!!.get(0).acquisionDateTime
        txt_sample_ok!!.text = listFileDataDO!!.get(0).sampleOkpress

    }

    private fun setupGraphFragment() {
        val arrayEntry =getArrayEntry()

        val getEastimatedLiminit=listFileDataDO!!.get(0).estimated_flag_mint
        graphFragment = ResultSeleteUserGraphFragment(
            listFileDataDO!!.get(0).max_mailisecond, listFileDataDO!!.get(
                0
            ).interval, arrayEntry, getEastimatedLiminit
        )


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
                )!! + "_" + Utils.TodayDate() + "_" + Utils.TodayTime12hrs() + "_" + listFileDataDO!!.get(
                    0
                ).patientId

                val f = File(
                    directory, filename + ".csv"
                )
                f.createNewFile()

                val fileWriter = FileWriter(f)

                val writer = CSVWriter(fileWriter)

                val header = listOf(
                    "Reaction Time (Seconds)",
                    "Reaction Result",
                    "Patient ID",
                    "Reagent ID",
                    "User ID",
                    "Sample Type",
                    "Test Performed",
                    "Testing Date and Time",
                    "QC Performed",
                    "Last QC Date and Time",
                    "Reaction Duration",
                    "Intervals",
                    "End of Process Flag",
                    "Sample Addition Date and Time",
                    "Acquision Start Date and Time",
                    "Sample Addition / Reaction Started by Pressing OK",
                    "Device ID",
                    "Software Version",
                    "ID"
                )



                writer.writeNext(header.toTypedArray())



                for(resultDo in listFileDataDO!!) {

                    val getinterval = Utils!!.pref_get_long(
                        Constant.qcintervalSecond,
                        requireActivity(),
                        Constant.maxDefalultInterval
                    )

                    val intervaladd: Long = Constant.addExtraIntrval


                    val getMiliSecond = Utils!!.pref_get_long(
                        Constant.qcMaxmiliSecond,
                        requireActivity(),
                        Constant.maxDefaultMinit
                    )

                    val maxTimeInMilliseconds = getMiliSecond - intervaladd

                    val maxtime: String = Utils.pref_get_String(Constant.maxMint, requireContext())!!



                    val patientId: String =resultDo.patientId
                    val regentId: String = resultDo.regentId
                    val userid: String = resultDo.userId
                    val sampletype: String = resultDo.sampleType
                    val testPerfomed: String = resultDo.testPerformed
                    val testDateTime: String = resultDo.testDate+ " "+resultDo.testTime

                    val lastQcDateTime: String =resultDo.lastQcDateTime

                    val reactionDuration: String =resultDo.reactionDuratio

                    val qcPerformed: String=resultDo.qcPerformed

                    val interval: String =resultDo.interval
                    val sampleAddDateTime: String =resultDo.sampleDateTime

                    val acquistionDateTime: String =resultDo.acquisionDateTime

                    val sampleOk: String = resultDo.sampleOkpress
                    val deviceName: String =resultDo.deviceId

                    val softwareVersion: String = resultDo.softwareVersion

                    val limitSecond=resultDo.estimated_second.toInt()


                    val result_x_vaule=resultDo.reactionTime.toInt()
                    val process_flagValue: String
                    if(result_x_vaule==limitSecond){
                        process_flagValue="1"
                    }
                    else{
                        process_flagValue="0"
                    }

                    val format = DecimalFormat("0.000")

                    val dataf= resultDo.reactionResult.toFloat()
                    val result=format.format(dataf)

                    val data = listOf(
                        resultDo.reactionTime,
                        result,
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
        catch (e: Exception){
            e.printStackTrace()
        }
    }

    private fun sendEmail(file: String) {

        try {
            val intentShareFile = Intent(Intent.ACTION_SEND)

            val f = File(file)

            val filedir = File(file)
            if(filedir.exists()) {
//                intentShareFile.setType("text/*")
                intentShareFile.setType("message/rfc822")

                val authority = requireContext().packageName + ".provider"
                val uri = FileProvider.getUriForFile(requireContext(), authority, f)

                intentShareFile.putExtra(Intent.EXTRA_STREAM, uri)
                intentShareFile.putExtra(
                    Intent.EXTRA_SUBJECT,
                    "Test Result"
                )
                intentShareFile.putExtra(Intent.EXTRA_TEXT, "Test Result")
                startActivity(Intent.createChooser(intentShareFile, f.getName()))
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }
}