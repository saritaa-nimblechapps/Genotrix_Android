package com.genotrixcube.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import au.com.bytecode.opencsv.CSVWriter
import com.genotrixcube.DatabaseHendler.DatabaseHelper
import com.genotrixcube.R
import com.genotrixcube.activity.DashboardActivity
import com.genotrixcube.adpter.HistroyListAdpter
import com.genotrixcube.model.FileDataDO
import com.genotrixcube.model.RecordDataHeaderConvertDO
import com.genotrixcube.model.ReocrdDataHeaderDO
import com.genotrixcube.util.Constant
import com.genotrixcube.util.Utils
import kotlinx.android.synthetic.main.fragment_history.view.*
import org.jetbrains.anko.collections.forEachReversedByIndex
import java.io.File
import java.io.FileWriter
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class HistoryFragment : Fragment(), View.OnClickListener {

    private var rootview: View? = null

    private var recordList: ArrayList<ReocrdDataHeaderDO>? = arrayListOf()
    private var recordListFilter: ArrayList<RecordDataHeaderConvertDO>? = arrayListOf()
    private var recordListtempFilter: ArrayList<RecordDataHeaderConvertDO>? = arrayListOf()
    private var recordListConverDate: ArrayList<RecordDataHeaderConvertDO>? = arrayListOf()
    private var recordTextSearch: ArrayList<RecordDataHeaderConvertDO>? = arrayListOf()

    private var recyHistory: RecyclerView? = null

    private var adpter: HistroyListAdpter? = null


    companion object {
        var strSearchHistoryId = ""
        var strStartDate = ""
        var strEndDate = ""

        var isSearchByText: Boolean = false
        var isStartDate: Boolean = false
        var isEndDate: Boolean = false
        var isScan: Boolean = false

        const val PERMISSIONS_REQUEST_SEARCH_CAMERA = 1111

        @JvmStatic
        fun newInstance() =
            HistoryFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        HistroyListAdpter.arrayBolSelectRecord!!.clear()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootview = inflater.inflate(R.layout.fragment_history, container, false)

        (activity as DashboardActivity?)!!.HeadetTitle("History")
        (activity as DashboardActivity?)!!.visibleBackButton()


        setupViews()


        rootview?.btn_export_multiple_csv?.setOnClickListener(this)
        rootview?.img_scan_search?.setOnClickListener(this)
        rootview?.img_search_cross?.setOnClickListener(this)
        rootview?.btn_clear?.setOnClickListener(this)
        rootview?.edt_start_date?.setOnClickListener(this)
        rootview?.edt_end_date?.setOnClickListener(this)

        return rootview
    }

    override fun onDestroy() {
        super.onDestroy()
        clearAll()
    }


    override fun onResume() {
        super.onResume()
        setSearchId()
        if (adpter != null) {

            searchHistoryText(strSearchHistoryId, strStartDate, strEndDate, isScan)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSIONS_REQUEST_SEARCH_CAMERA -> if (grantResults.size > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                gotoCodeScan()
            } else if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                @SuppressLint("NewApi", "LocalSuppress") val showRationale =
                    shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)
                if (!showRationale) {

                    //set up setting dialog
                    Utils.callPermissionONMessagesDilog(
                        requireActivity(),
                        "Genotrix",
                        "Must on camera permission"
                    )
                } else {

                    requestCameraParmeission()
                }
            }
            else -> throw IllegalStateException("Unexpected value: $requestCode")
        }
    }

    private fun setupdAdpter(recordList: ArrayList<RecordDataHeaderConvertDO>?) {

        if (recordList!!.size > 0) {
            visibleRecyList()
            adpter = HistroyListAdpter(
                recordList!!,
                requireActivity(),
                this@HistoryFragment,
                requireContext()
            )
            recyHistory!!.adapter = null
            recyHistory!!.adapter = adpter
        } else {
            hideRecyList("No Such Reocrd")
        }

    }

    private fun clearAll() {
        strSearchHistoryId = ""
        strEndDate = ""
        strStartDate = ""

        isSearchByText = false
        isStartDate = false
        isEndDate = false
        isScan = false

        recordListFilter!!.clear()
        recordList!!.clear()
        recordListConverDate!!.clear()
        recordTextSearch!!.clear()
        recordListtempFilter!!.clear()
    }

    private fun setupViews() {

        recyHistory = rootview!!.findViewById(R.id.record_list_view)
        recyHistory!!.setHasFixedSize(true)
        recyHistory!!.setLayoutManager(LinearLayoutManager(activity))


        val format = SimpleDateFormat("dd-MMM-yyyy")

        if (isSearchByText || isStartDate || isEndDate) {

            if (isSearchByText) {
                setSearchId()
                searchHistoryText(strSearchHistoryId, strStartDate, strEndDate, isScan)
            }

        } else {
            recordList = getAllHeaderRecords()
            recordListFilter!!.clear()
            recordListConverDate!!.clear()
            for (recordDataDO in recordList!!) {

                val strDate = recordDataDO.testDate
                val date: Date = format.parse(strDate)

                recordListFilter!!.add(
                    RecordDataHeaderConvertDO(
                        recordDataDO.reacordId,
                        recordDataDO.patientId,
                        recordDataDO.regentId,
                        recordDataDO.userId,
                        date,
                        recordDataDO.testTime
                    )
                )
                recordListConverDate!!.add(
                    RecordDataHeaderConvertDO(
                        recordDataDO.reacordId,
                        recordDataDO.patientId,
                        recordDataDO.regentId,
                        recordDataDO.userId,
                        date,
                        recordDataDO.testTime
                    )
                )
                //  recordListtempFilter!!.add(RecordDataHeaderConvertDO(recordDataDO.reacordId,recordDataDO.patientId,recordDataDO.regentId,recordDataDO.userId,date,recordDataDO.testTime))

            }
        }



        Log.wtf(
            "newList",
            "new list size :" + recordListFilter!!.size + " other filter list :" + recordListConverDate!!.size!!
        )


        rootview?.edt_search_id?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if (!isScan) {


                    if (s!!.equals("") || s.isNullOrEmpty()) {

                        clearSelectedCheckValue()
                        if (isStartDate || isEndDate) {
                            searchHistoryText(strSearchHistoryId, strStartDate, strEndDate, false)
                        } else {
                            recordListConverDate!!.clear()
                            recordListConverDate!!.addAll(recordListFilter!!)
                            recyHistory!!.adapter = null
                            setupdAdpter(recordListConverDate!!)
                        }


                    } else {
                        Log.wtf("searchText", s.toString())

                        strSearchHistoryId = s.toString()
                        filter(strSearchHistoryId, false)
                    }

                }

            }

        })

        rootview?.edt_start_date?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if(s.isNullOrEmpty()){

                }
                else{
                    clearSelectedCheckValue()
                    strStartDate = s.toString()

                    if (strSearchHistoryId != "" || strStartDate != "" || strEndDate != "") {
                        searchHistoryText(strSearchHistoryId, strStartDate, strEndDate, isScan)

                    }
                }

            }

        })


        rootview?.edt_end_date?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if(s.isNullOrEmpty()){

                }else{
                    clearSelectedCheckValue()

                    strEndDate = s.toString()

                    if (strSearchHistoryId != "" || strStartDate != "" || strEndDate != "") {
                        searchHistoryText(strSearchHistoryId, strStartDate, strEndDate, isScan)

                    }

                }


            }

        })

        setupdAdpter(recordListConverDate!!)

    }


    private fun searchHistoryText(
        strsearchText: String,
        strStartDate: String?,
        strEndDate: String?,
        isScan: Boolean
    ) {

        var searchCase: Int = 0

        if (strsearchText != "" && strStartDate != "" && strEndDate != "") {
            searchCase = 0
        } else if (strsearchText != "" && strStartDate != "") {
            searchCase = 1
        } else if (strsearchText != "" && strEndDate != "") {
            searchCase = 2
        } else if (strStartDate != "" && strEndDate != "") {
            searchCase = 3
        } else if (strStartDate != "") {
            searchCase = 4
        } else if (strEndDate != "") {
            searchCase = 5
        } else if (strsearchText != "") {
            searchCase = 6
        }

        val arrayFilterStartDate: ArrayList<RecordDataHeaderConvertDO> = arrayListOf()

        when (searchCase) {
            0 -> {
                val strStartDate = rootview?.edt_start_date!!.text.toString()
                val strEndDate = rootview?.edt_end_date!!.text.toString()
                val strSearchId = rootview?.edt_search_id!!.text.toString()

                if (strStartDate != "" && strEndDate != "" && strStartDate != "") {
                    val format = SimpleDateFormat("dd-MMM-yyyy")
                    val selection__start_date: Date = format.parse(strStartDate)
                    val selection__end_date: Date = format.parse(strEndDate)

                    if((selection__end_date.time > selection__start_date.time) || (selection__end_date.time == selection__start_date.time) ){
                        arrayFilterStartDate.clear()

                        filter(strsearchText, isScan)

                        arrayFilterStartDate.addAll(
                            funFliterDateRange(
                                selection__start_date,
                                selection__end_date,
                                recordTextSearch!!,
                                recordListFilter!!
                            )
                        )

                        Log.d("startdate", "size of start date :" + arrayFilterStartDate.size)

                        isStartDate = true
                        isEndDate = true
                        isSearchByText = true

                        if (arrayFilterStartDate.size > 0) {

                            recordListConverDate!!.clear()
                            recordListConverDate!!.addAll(arrayFilterStartDate)

                            setupdAdpter(arrayFilterStartDate)
                        } else {
                            hideRecyList("No Such Records")
                        }
                    }
                    else if(selection__end_date.time<selection__start_date.time){
                        Toast.makeText(context,"end must be greater than start date",Toast.LENGTH_LONG).show()
                    }


                }

            }
            1 -> {
                val strStartDate = rootview?.edt_start_date!!.text.toString()

                if (strStartDate != "" || strStartDate != null) {
                    val format = SimpleDateFormat("dd-MMM-yyyy")
                    val selection_date: Date = format.parse(strStartDate)

                    arrayFilterStartDate.clear()

                    arrayFilterStartDate?.addAll(
                        funFilterStartDate(
                            selection_date,
                            recordTextSearch!!,
                            recordListFilter!!
                        )
                    )

                    Log.d("startdate", "size of start date :" + arrayFilterStartDate.size)


                    if (arrayFilterStartDate?.size!! > 0) {

                        recordListConverDate!!.clear()
                        recordListConverDate!!.addAll(arrayFilterStartDate!!)

                        setupdAdpter(arrayFilterStartDate!!)
                    } else {
                        hideRecyList("No Such Reocrd")
                    }
                }

            }
            2 -> {
                val strEndDate = rootview?.edt_end_date!!.text.toString()

                if (strEndDate != "" || strEndDate != null) {
                    val format = SimpleDateFormat("dd-MMM-yyyy")
                    val selection_date: Date = format.parse(strEndDate)

                    arrayFilterStartDate.clear()

                    arrayFilterStartDate?.addAll(
                        funFilterEndDate(
                            selection_date,
                            recordTextSearch!!,
                            recordListFilter!!
                        )
                    )

                    Log.d("startdate", "size of start date :" + arrayFilterStartDate.size)


                    if (arrayFilterStartDate?.size!! > 0) {

                        recordListConverDate!!.clear()
                        recordListConverDate!!.addAll(arrayFilterStartDate!!)

                        setupdAdpter(arrayFilterStartDate!!)
                    } else {
                        hideRecyList("No Such Records")
                    }
                }

            }
            3 -> {


                val strStartDate = rootview?.edt_start_date!!.text.toString()
                val strEndDate = rootview?.edt_end_date!!.text.toString()

                if (strStartDate != "" || strStartDate != null || strEndDate != "" || strEndDate != null) {
                    val format = SimpleDateFormat("dd-MMM-yyyy")
                    val selection_date: Date = format.parse(strStartDate)

                    val selection_enddate: Date = format.parse(strEndDate)

                    //check for end date
                    if((selection_enddate.time > selection_date.time) || (selection_enddate.time == selection_date.time) ){
                        arrayFilterStartDate.clear()

                        arrayFilterStartDate?.addAll(
                            funFliterDateRange(
                                selection_date,
                                selection_enddate,
                                recordTextSearch!!,
                                recordListFilter!!
                            )
                        )

                        Log.wtf("startdate", "size of start date :" + arrayFilterStartDate.size)

                        isStartDate = true
                        isEndDate = true

                        if (arrayFilterStartDate?.size!! > 0) {

                            recordListConverDate!!.clear()
                            recordListConverDate!!.addAll(arrayFilterStartDate!!)

                            setupdAdpter(arrayFilterStartDate!!)
                        } else {
                            hideRecyList("No Such Records")
                        }
                    }
                    else if(selection_enddate.time<selection_date.time){
                        Toast.makeText(context,"end must be greater than start date",Toast.LENGTH_LONG).show()
                    }


                }


            }
            4 -> {
                val strStartDate = rootview?.edt_start_date!!.text.toString()

                if (strStartDate != "" || strStartDate != null) {
                    val format = SimpleDateFormat("dd-MMM-yyyy")
                    val selection_date: Date = format.parse(strStartDate)

                    arrayFilterStartDate.clear()

                    arrayFilterStartDate?.addAll(
                        funFilterStartDate(
                            selection_date,
                            recordTextSearch!!,
                            recordListFilter!!
                        )
                    )

                    Log.wtf("startdate", "size of start date :" + arrayFilterStartDate.size)


                    isStartDate = true
                    if (arrayFilterStartDate?.size!! > 0) {

                        recordListConverDate!!.clear()
                        recordListConverDate!!.addAll(arrayFilterStartDate!!)

                        setupdAdpter(arrayFilterStartDate!!)
                    } else {
                        hideRecyList("No Such Reocrd")
                    }
                }

            }
            5 -> {
                val strStartDate = rootview?.edt_end_date!!.text.toString()


                if (strStartDate != null || strStartDate != "") {
                    val format = SimpleDateFormat("dd-MMM-yyyy")
                    val selection_date: Date = format.parse(strStartDate)


                    arrayFilterStartDate.clear()

                    arrayFilterStartDate?.addAll(
                        funFilterEndDate(
                            selection_date,
                            recordTextSearch!!,
                            recordListFilter!!
                        )
                    )

                    Log.wtf("startdate", "size of start date :" + arrayFilterStartDate.size)

                    isEndDate = true
                    if (arrayFilterStartDate?.size!! > 0) {

                        recordListConverDate!!.clear()
                        recordListConverDate!!.addAll(arrayFilterStartDate!!)

                        setupdAdpter(arrayFilterStartDate!!)
                    } else {
                        hideRecyList("No Such Reocrd")
                    }
                }

            }
            6 -> {
                filter(strsearchText, isScan)
            }
        }


    }

    private fun funFliterDateRange(
        selectionDate: Date,
        selectionEnddate: Date,
        recordTextSearch: ArrayList<RecordDataHeaderConvertDO>,
        recordListConverDate: ArrayList<RecordDataHeaderConvertDO>
    ): Collection<RecordDataHeaderConvertDO> {

        var filterDatesList: ArrayList<RecordDataHeaderConvertDO> = ArrayList()
        filterDatesList = arrayListOf()

        val arraylist: ArrayList<RecordDataHeaderConvertDO> = arrayListOf()

        if (recordTextSearch.size > 0) {

            arraylist.addAll(recordTextSearch)
        } else {
            arraylist.addAll(recordListConverDate)
        }

        try {

            val formate: SimpleDateFormat = SimpleDateFormat("dd-MMM-yyyy")

            val start_date: Date = selectionDate
            val end_date: Date = selectionEnddate

            val interval = (24 * 1000 * 60 * 60).toLong() // 1 hour in millis
            val endTime =
                end_date.getTime() // create your endtime here, possibly using Calendar or Date
            var curTime = start_date.getTime()


            var dates: ArrayList<Date> = ArrayList<Date>()
            dates = arrayListOf()

            while (curTime <= endTime) {
                dates.add(Date(curTime))
                curTime += interval
            }

            var date_convert: SimpleDateFormat? = null
            var str_convert_date: String? = null
            //  var convert_dat: Date? = null

            for (datefilter in dates) {

                val dates_filter: String = formate.format(datefilter)

                for (recordDO in arraylist!!) {
                    if (recordDO.testDate != null) {


                        date_convert = SimpleDateFormat("dd-MMM-yyyy")
                        str_convert_date = date_convert.format(recordDO.testDate)


                        if (dates_filter.equals(str_convert_date)) {
                            filterDatesList.add(recordDO)
                        }
                    }
                }
            }


        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("maintance", "Date range creating error : " + e.printStackTrace())
        }
        return filterDatesList

    }

    private fun funFilterEndDate(
        selectionDate: Date,
        recordTextSearch: ArrayList<RecordDataHeaderConvertDO>,
        recordListConverDate: ArrayList<RecordDataHeaderConvertDO>
    ): Collection<RecordDataHeaderConvertDO> {

        var filterEndlist: ArrayList<RecordDataHeaderConvertDO> = ArrayList()
        filterEndlist = arrayListOf()

        val arrayFilterValue: ArrayList<RecordDataHeaderConvertDO> = arrayListOf()

        if (recordTextSearch.size > 0) {
            arrayFilterValue.addAll(recordTextSearch)
        } else {
            arrayFilterValue.addAll(recordListConverDate)
        }
        try {

            var formate4_list: SimpleDateFormat? = null
            var listDatestring: Date? = null

            var date_convert: SimpleDateFormat? = null
            var str_convert_date: String? = null
            var convert_dat: Date? = null

            for (recrodDO in arrayFilterValue!!) {

                //"2019-08-28T06:29:22.282Z"
                if (recrodDO.testDate != null) {

                    formate4_list = SimpleDateFormat("dd-MMM-yyyy")
                    //listDatestring = formate4_list.parse(recrodDO!!.testDate!!.toString())

                    date_convert = SimpleDateFormat("dd-MMM-yyyy")
                    str_convert_date = date_convert.format(recrodDO!!.testDate!!)
                    convert_dat = date_convert.parse(str_convert_date!!)
//
                    if (selectionDate.compareTo(convert_dat) == 0) {
                        Log.e("start date filter", "start date :" + convert_dat.toString())
                        filterEndlist.add(recrodDO)
                    }

                    if (selectionDate.after(convert_dat)) {
                        Log.e("start date filter", "start date :" + str_convert_date.toString())
                        filterEndlist.add(recrodDO)
                    }
                }

            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return filterEndlist
    }

    private fun funFilterStartDate(
        selectionDate: Date,
        recordTextSearch: ArrayList<RecordDataHeaderConvertDO>,
        recordListConverDate: ArrayList<RecordDataHeaderConvertDO>
    ): Collection<RecordDataHeaderConvertDO> {

        var filterStartDate: ArrayList<RecordDataHeaderConvertDO> = ArrayList()
        filterStartDate = arrayListOf()

        val arrayFilterValue: ArrayList<RecordDataHeaderConvertDO> = arrayListOf()

        if (recordTextSearch.size > 0) {
            arrayFilterValue.addAll(recordTextSearch)
        } else {
            arrayFilterValue.addAll(recordListConverDate)
        }
        try {

            var formate4_list: SimpleDateFormat? = null
            var listDatestring: Date? = null

            var date_convert: SimpleDateFormat? = null
            var str_convert_date: String? = null
            var convert_dat: Date? = null

            for (recrodDO in arrayFilterValue!!) {

                //"2019-08-28T06:29:22.282Z"
                if (recrodDO.testDate != null) {

                    formate4_list = SimpleDateFormat("dd-MMM-yyyy")
                    //listDatestring = formate4_list.parse(recrodDO!!.testDate!!.toString())

                    date_convert = SimpleDateFormat("dd-MMM-yyyy")
                    str_convert_date = date_convert.format(recrodDO!!.testDate!!)
                    convert_dat = date_convert.parse(str_convert_date!!)
//
                    if (selectionDate.compareTo(convert_dat) == 0) {
                        Log.e("start date filter", "start date :" + convert_dat.toString())
                        filterStartDate.add(recrodDO)
                    }

                    if (selectionDate.before(convert_dat)) {
                        Log.e("start date filter", "start date :" + str_convert_date.toString())
                        filterStartDate.add(recrodDO)
                    }
                }

            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return filterStartDate
    }

    private fun filter(searchId: String, scan: Boolean) {

        if (searchId.equals("")) {

        } else {
            recordTextSearch!!.clear()
            if (scan) {
                isScan = false
            }
            recordListConverDate?.let { mListEmployees ->
                val temp = ArrayList<RecordDataHeaderConvertDO>()
                for (d in mListEmployees) {
                    //or use .equal(text) with you want equal match
                    //use .toLowerCase() for better matches
                    if (d.patientId.contains(searchId) || d.reacordId.contains(searchId) || d.regentId.contains(
                            searchId
                        ) || d.userId.contains(searchId)
                    ) {
                        temp.add(d)
                    }
                }

                if (temp.size == 0) {
                    hideRecyList("No Such Records")
                } else {
                    visibleRecyList()
                }

                isSearchByText = true

                //update recyclerview
                recordTextSearch!!.addAll(temp)

                adpter?.updateList(temp)
            }
        }


    }


    private fun getAllHeaderRecords(): ArrayList<ReocrdDataHeaderDO>? {
        val databaseHandler: DatabaseHelper = DatabaseHelper(requireActivity().applicationContext)

        val recordList = databaseHandler.viewTestHeaderReocord()

        Log.wtf("db", "Total db records :" + recordList!!.size)

        val tempElements = ArrayList<ReocrdDataHeaderDO>(recordList.size)
        recordList.forEachReversedByIndex { tempElements.add(it) }

        return tempElements
    }

    private fun clearSelectedCheckValue(){
        if(HistroyListAdpter.arrayBolSelectRecord!!.size>0){

            HistroyListAdpter.arrayBolSelectRecord!!.clear()
            HideNextButton()
        }
    }

    fun visibleitNextButton() {

        rootview?.btn_export_multiple_csv?.isEnabled = true
    }


    fun HideNextButton() {
        rootview?.btn_export_multiple_csv?.isEnabled = false
    }

    private fun getAllSelectedRecords(idpass: String): ArrayList<FileDataDO>? {
        val databaseHandler: DatabaseHelper = DatabaseHelper(requireActivity().applicationContext)

        val recordList = databaseHandler.getSelectRecords(idpass)

        Log.wtf("db", "Total db records :" + recordList!!.size)

        return recordList
    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.btn_export_multiple_csv -> {

                if (HistroyListAdpter.arrayBolSelectRecord!!.size > 0) {

                  if(  Utils.isNetConnected(requireContext())){
                      crateFile()
                    }
                    else{
                      Utils.showToast(requireContext(),"No Internet Connect")
                    }



                }
            }
            R.id.img_scan_search -> {
                checkForCameraParmession()
            }
            R.id.img_search_cross -> {

                val strSearchText=rootview?.edt_search_id?.text.toString()

                if(strSearchText.isNullOrEmpty()){

                }else{
                    if (isSearchByText) {
                        isSearchByText = false

                        val edtSearch: EditText = rootview?.findViewById(R.id.edt_search_id) as EditText

                        strSearchHistoryId = ""
                        edtSearch.setText("")

                        //check other filter or not
                        if (isStartDate || isEndDate) {
                            //   searchHistoryText(strSearchHistoryId, strStartDate, strEndDate,false)

                        } else {

//                        recordListConverDate!!.clear()
//                        recordListConverDate!!.addAll(recordListFilter!!)
//
//                        setupdAdpter(recordListFilter!!)
//                        Utils.hideKeyboard(requireContext())

                        }

                    }
                    else{

                        if (isStartDate || isEndDate) {

                        }
                        else{

                            recordListConverDate!!.clear()
                            recordListConverDate!!.addAll(recordListFilter!!)
                            setupdAdpter(recordListFilter!!)
                        }

                    }
                    cleaIdText()
                }

            }
            R.id.btn_clear -> {
                clearDate()
            }
            R.id.edt_start_date -> {
                Utils.DialogDateSelected(
                    requireContext(),
                    rootview!!,
                    true,
                    rootview?.edt_start_date!!
                )
            }
            R.id.edt_end_date -> {
                Utils.DialogDateSelected(
                    requireContext(),
                    rootview!!,
                    true,
                    rootview?.edt_end_date!!
                )
            }
        }
    }

    private fun cleaIdText() {
        rootview?.edt_search_id?.text!!.clear()
        Utils.hideKeyboard(requireContext())
    }

    private fun crateFile() {
        try {

            val directory: File = Utils.getDirectory(requireContext())!!
            var selctedRecords: ArrayList<FileDataDO> = arrayListOf()

            if (directory != null) {

                val filename: String = "exp_"+Utils.pref_get_String(Constant.deviceName, requireContext())!! + "_" + Utils.TodayDate() + "_" + Utils.TodayTime12hrs()

                val f = File(
                    directory, filename + ".csv"
                )
                f.createNewFile()

                val fileWriter = FileWriter(f)

                val writer = CSVWriter(fileWriter)

                // title header print into the file
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

                //put blank line
                val empty = listOf(
                    "\n",
                    "\n",
                    "\n",
                    "\n",
                    "\n",
                    "\n",
                    "\n",
                    "\n",
                    "\n",
                    "\n",
                    "\n",
                    "\n",
                    "\n",
                    "\n",
                    "\n",
                    "\n",
                    "\n",
                    "\n",
                    "\n"
                )

                for ( data in HistroyListAdpter.arrayBolSelectRecord!!) {

                    val records = getAllSelectedRecords(data.patientId)

                    for (dataReocrds in records!!) {

                         val format = DecimalFormat("0.000")

                        val dataf= dataReocrds.reactionResult.toFloat()
                        val result=format.format(dataf)

                        val data = listOf(
                            dataReocrds.reactionTime,
                            result,
                            dataReocrds.patientId,
                            dataReocrds.regentId,
                            dataReocrds.userId,
                            dataReocrds.sampleType,
                            dataReocrds.testPerformed,
                            dataReocrds.testDate+" "+dataReocrds.testTime,
                            dataReocrds.qcPerformed,
                            dataReocrds.lastQcDateTime,
                            dataReocrds.reactionDuratio,
                            dataReocrds.interval,
                            dataReocrds.endOfPrescessFalg,
                            dataReocrds.sampleDateTime,
                            dataReocrds.acquisionDateTime,
                            dataReocrds.sampleOkpress,
                            dataReocrds.deviceId,
                            dataReocrds.softwareVersion,
                            dataReocrds.id
                        )

                        writer.writeNext(data.toTypedArray())


                    }

                    writer.writeNext(empty.toTypedArray());


                }
                Log.wtf("csv", "select mulreocrd :" + selctedRecords!!.size)


                // closing writer connection
                writer.close()

                sendEmail(f.path)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun sendEmail(file: String) {

        try {
            val intentShareFile = Intent(Intent.ACTION_SEND)

            val f = File(file)

            val filedir = File(file)
            if (filedir.exists()) {
                //only open the file shate usege app icon
                intentShareFile.setType("message/rfc822")

               // val file_uri=FileProvider.getUriForFile(requireContext(),requireContext().packageName+".provider"+f)
                val authority = requireContext().packageName + ".provider"
                val uri = FileProvider.getUriForFile(requireContext(), authority, f)

                intentShareFile.putExtra(Intent.EXTRA_STREAM, uri)
                intentShareFile.putExtra(Intent.EXTRA_SUBJECT, "Test Results" )
                intentShareFile.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

                intentShareFile.putExtra(Intent.EXTRA_TEXT, "Test Results" )
                startActivity(Intent.createChooser(intentShareFile, f.getName()))
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun checkForCameraParmession() {
        if (hasCameraPermission()) {
            gotoCodeScan()
        } else {
            requestCameraParmeission()
        }
    }

    private fun requestCameraParmeission() {
        ActivityCompat.requestPermissions(
            requireActivity(), arrayOf(Manifest.permission.CAMERA),
            PERMISSIONS_REQUEST_SEARCH_CAMERA
        )
    }

    private fun hasCameraPermission(): Boolean {
        return (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED)
    }

    private fun gotoCodeScan() {
        isScan = true
        val fragment = SeachIdScanFragment()
        Utils.replaceFragment(requireActivity(), fragment, true, HistoryFragment::class.java.name)

    }

    private fun setSearchId() {
        Log.wtf("patient setPatientId :", HistoryFragment.strSearchHistoryId)
        if (strSearchHistoryId != "") {
            Log.wtf("patient inside if :", HistoryFragment.strSearchHistoryId)
            rootview?.edt_search_id?.setText(HistoryFragment.strSearchHistoryId)
        }
    }


    private fun visibleRecyList() {
        rootview?.record_list_view?.visibility = View.VISIBLE
        rootview?.txt_list_msg?.visibility = View.GONE
    }

    private fun hideRecyList(strMessage: String) {
        rootview?.record_list_view?.visibility = View.GONE
        rootview?.txt_list_msg?.text = strMessage
        rootview?.txt_list_msg?.visibility = View.VISIBLE

    }

    private fun clearDate() {
        isStartDate = false
        isEndDate = false

        strEndDate = ""
        strStartDate = ""



        if (isSearchByText) {
            recordListConverDate!!.clear()
            recordListConverDate!!.addAll(recordListFilter!!)
            recyHistory!!.adapter = null
            setupdAdpter(recordListConverDate!!)

        } else {
            recordListConverDate!!.clear()
            recordListConverDate!!.addAll(recordListFilter!!)
            setupdAdpter(recordListFilter!!)
        }

        val edtStart: EditText = rootview?.findViewById(R.id.edt_start_date) as EditText
        val edtEnd: EditText = rootview?.findViewById(R.id.edt_end_date) as EditText

        edtStart.setText("")
        edtEnd.setText("")
    }

    fun selectRecordDisplayData(patientId: String) {

        val records = getAllSelectedRecords(patientId)

        Utils.replaceFragment(requireActivity(),ResultSelectUserDataFragment(records),true, HistoryFragment::class.java.name)

    }

}