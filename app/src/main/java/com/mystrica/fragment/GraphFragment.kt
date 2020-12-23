package com.mystrica.fragment

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.DashPathEffect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.mystrica.R
import com.mystrica.activity.DashboardActivity
import com.mystrica.adpter.MyMarkerView
import com.mystrica.fragment.CollectSampleFragment.Companion.storeData
import com.mystrica.fragment.CollectSampleFragment.Companion.tempData
import com.mystrica.util.Constant
import com.mystrica.util.Utils
import java.util.*
import kotlin.experimental.and

class GraphFragment : Fragment {
    private var rootview: View? = null
    private var temp = 0

    //user for graph
    private var mChart: LineChart? = null
    var mv: MyMarkerView? = null
    private var values =
        ArrayList<Entry>()
    var set1: LineDataSet? = null
    var leftAxis: YAxis? = null
    var xAxis: XAxis? = null
    var dataSets = ArrayList<ILineDataSet>()
    var data: LineData? = null

    private var isfromCollectsample = false

    constructor(isfromCollectSample: Boolean) {
        isfromCollectsample = isfromCollectSample
    }

    constructor() {}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootview = inflater.inflate(R.layout.graphlayout, container, false)
        mChart = rootview!!.findViewById(R.id.chart_graph)
        mChart!!.setTouchEnabled(true)
        mChart!!.setPinchZoom(false)
        setupGraph()
        return rootview
    }

    fun addGraphPloatData(
        data: String,
        second: Int,
        byteArraydata: ByteArray
    ) {
        //sperate 2 parameter
//        byte[] byteArraydata=new byte[2];
//        byteArraydata= new byte[]{110, 120, 100, 105, 120, (byte) 155, 100, 120};
        getGraphXvalue(data, second, byteArraydata)
    }

    private fun getGraphXvalue(
        xaxis: String,
        sec: Int,
        byteArraydata: ByteArray
    ) {
        // Toast.makeText(getContext(),"y plot :"+xaxis,Toast.LENGTH_LONG).show();
        Log.wtf("bit", "value of array =>$xaxis")
        //        Log.wtf("bit","=================");
//        for(int i=0;i<byteArraydata.length;i++){
//            Log.wtf("bit","value of array =>"+byteArraydata[i]);
//
//        }
        val myList: List<String> = ArrayList(
            Arrays.asList(
                *xaxis.split(",".toRegex()).toTypedArray()
            )
        )
        val strValue = myList[0]
        var number = 0
        var convert = 0
        var convert4 = 0
        var convert5 = 0
        var bitoperation: Long = 0
        try {
            convert = strValue.trim { it <= ' ' }.toInt()
//            convert4 = ((byteArraydata[4] and 0xFF.toByte()).toInt())
            convert4 = (byteArraydata[4].toUByte().toInt())
//            convert5 = ((byteArraydata[5] and 0xFF.toByte()).toInt())
            convert5 =(byteArraydata[5].toUByte().toInt())

            Log.wtf("bit", "=============4 bit value  =>$convert4")
            Log.wtf("bit", "=============5 bit value =>$convert5")

            number = Math.abs(convert)
            val bitop = convert5 shl 8
            val bit4 = convert4
            bitoperation = (bitop or bit4.toLong().toInt()).toLong()


            // let valueISVisibleIRRawADC = (UInt16(aArray[5]) << 8) | UInt16(aArray[4])
            Log.i("bit", "bit operation:$bitoperation")
            val currentvalueLog10 = Math.log10(bitoperation.toDouble())
            var firstlog = 0.0
            if (DashboardActivity.firstResultValue !== "") {
//                Log.i("bit value", "first value :" + firstResultValue);
//                Log.i("bit value", "first value :" + strValue);
                val myList1: List<String> =
                    ArrayList(
                        Arrays.asList(
                            *DashboardActivity.firstResultValue.split(",".toRegex()).toTypedArray()
                        )
                    )
                val Value4 = myList1[4]
                val sValue5 = myList1[5]

                val a : Int= (DashboardActivity.fistbyteArray!![4].toUByte().toInt())

                val convert4_first: Int = (DashboardActivity.fistbyteArray!![4].toUByte().toInt())
                val convert5_first: Int = (DashboardActivity.fistbyteArray!![5].toUByte().toInt())

                val bitop5 = convert5_first shl 8
                val bitoperation_first = (bitop5 or convert4_first.toLong().toInt()).toLong()
                firstlog = Math.log10(bitoperation_first.toDouble())
            }
            val value_x = Constant.x
            val final_abounse_vlaue = (firstlog - currentvalueLog10 + value_x).toFloat()

            Log.wtf("bit", "================= Log value : $final_abounse_vlaue")
            Log.wtf("bit", "=================")

            //            Log.i("bit value", "Fritst log :" + firstlog + " Log other vaule :" + currentvalueLog10 + "  ,=>Final calculate value :" + Math.abs(final_abounse_vlaue));
            @SuppressLint("DefaultLocale") val s = String.format("%.2f", Math.abs(final_abounse_vlaue))

            val plotvalue = s.toFloat()

            entryOfGraghData(Math.abs(final_abounse_vlaue).toDouble(), 10, sec)
        } catch (e: NumberFormatException) {
//            System.out.println("parse value is not valid : " + e);
        }
    }

    private fun entryOfGraghData(
        number: Double,
        curentSceont: Int,
        secondget: Int
    ) {

//        values.add(new Entry(temp,number));
        if (temp == 0) {
            set1!!.clear()
            mChart!!.data.clearValues()
        }
        temp = temp + curentSceont
        DrawGraph(Entry(secondget.toFloat(), number.toFloat()))
    }

    private fun setupGraph() {
        val getMiliSecond = Utils!!.pref_get_long(
            Constant.qcMaxmiliSecond,
            requireActivity(),
            Constant.maxDefaultMinit
        )
        val intervaladd: Long = 24000
        val maxTimeInMilliseconds = getMiliSecond - intervaladd
        val valx = maxTimeInMilliseconds.toInt() / 1000
        val xaxisValue = valx.toFloat()
        xAxis = mChart!!.xAxis
        xAxis!!.enableGridDashedLine(10f, 10f, 0f)
        xAxis!!.setAxisMaximum(xaxisValue)
        xAxis!!.setAxisMinimum(0f)
        xAxis!!.setAxisLineColor(Color.DKGRAY)
        xAxis!!.setTextColor(Color.DKGRAY)
        xAxis!!.setDrawLimitLinesBehindData(true)
        leftAxis = mChart!!.axisLeft
        leftAxis!!.removeAllLimitLines()
        leftAxis!!.setAxisMaximum(DashboardActivity.currentValue4Yaxis)
        leftAxis!!.setAxisMinimum(0.0f)
        leftAxis!!.setAxisLineColor(Color.DKGRAY)
        leftAxis!!.setTextColor(Color.DKGRAY)
        leftAxis!!.enableGridDashedLine(10f, 10f, 0f)
        leftAxis!!.setDrawZeroLine(false)
        leftAxis!!.setDrawLimitLinesBehindData(true)
        mChart!!.axisRight.isEnabled = false
        mChart!!.fitScreen()
        if (isfromCollectsample) {

//            storeData.clear();
//            tempData.clear();
            values.clear()
        }
        DrawGraph(Entry(0F, 0F))
    }



    private fun DrawGraph(entry: Entry) {
        if (isfromCollectsample) {
            values.add(entry)
            storeData!!.add(entry)
            if (entry.y < DashboardActivity.currentValue4Yaxis) {
            } else {
//                Log.i("graph", "y increse call");
                increaseYaxis(entry.y)
            }
            if(entry.x > 100f){
                mChart!!.moveViewToX(entry.x)

            }
        }
        //        else{
//            values=storeData;
//        }
        if (mChart!!.data != null && mChart!!.data.dataSetCount > 0) {
            set1 = mChart!!.data.getDataSetByIndex(0) as LineDataSet
            set1!!.values = values
            mChart!!.data.notifyDataChanged()
            mChart!!.notifyDataSetChanged()
            mChart!!.invalidate()
        } else {
            initGraph()
        }
    }

    fun resultArray(): ArrayList<Entry>? {
        var recylerdata: ArrayList<Entry>? = null
        try {
            tempData = storeData
            recylerdata = tempData
            //recylerdata.remove(0);
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return recylerdata
    }

    private fun increaseYaxis(y: Float) {
        val getcuurenty = mChart!!.yChartMax

        //check for value increase
        var incv = getcuurenty + 0.1f
        if (y < getcuurenty) {
            DashboardActivity.currentValue4Yaxis = incv
        } else {
            incv = getcuurenty + y
            DashboardActivity.currentValue4Yaxis = incv
        }
        leftAxis!!.axisMaximum = DashboardActivity.currentValue4Yaxis
        mChart!!.axisRight.isEnabled = false
    }

    private fun initGraph() {
        if (isfromCollectsample) {
            set1 = LineDataSet(values, "Sample Data")
        } else {
            values = storeData!!
            tempData = storeData
        }
        set1 = LineDataSet(values, "Sample Data")
        set1!!.setDrawIcons(false)
        set1!!.enableDashedLine(10f, 5f, 0f)
        set1!!.enableDashedHighlightLine(10f, 5f, 0f)
        set1!!.color = Color.DKGRAY
        set1!!.setCircleColor(Color.DKGRAY)
        set1!!.lineWidth = 1f
        set1!!.circleRadius = 3f
        set1!!.setDrawCircleHole(false)
        set1!!.valueTextSize = 7f
        set1!!.setDrawFilled(true)
        set1!!.formLineWidth = 1f
        set1!!.formLineDashEffect = DashPathEffect(floatArrayOf(10f, 5f), 0f)
        set1!!.formSize = 10f
        val drawable =
            ContextCompat.getDrawable(requireContext()!!, R.drawable.fade_blue)
        set1!!.fillDrawable = drawable
        dataSets = ArrayList()
        dataSets.add(set1!!)
        //            LineData data = new LineData(dataSets);
        mChart!!.isAutoScaleMinMaxEnabled = true
        mChart!!.data = LineData(dataSets)
        mChart!!.setVisibleXRangeMaximum(100f) // allow 20 values to be displayed at once on the x-axis, not more
        mChart!!.invalidate()
        //mChart.moveViewToX(50f);
    }

    override fun onDestroy() {
        super.onDestroy()

        //storeData.clear();
    }

    companion object {
        var oldXvalue = 2f
        var isFirstXvalue = false
    }
}
