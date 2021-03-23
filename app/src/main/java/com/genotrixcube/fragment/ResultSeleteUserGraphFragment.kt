package com.genotrixcube.fragment

import android.graphics.Color
import android.graphics.DashPathEffect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.genotrixcube.R
import com.genotrixcube.activity.DashboardActivity
import com.genotrixcube.adpter.MyMarkerView
import com.genotrixcube.util.Formater
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import java.math.RoundingMode
import java.text.DecimalFormat

class ResultSeleteUserGraphFragment(
    maxMailisecond: String,
    interval: String,
    listEntry: ArrayList<Entry>,
    eastimatedLiminit: String
) : Fragment() {
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
    var strGetMaxMilsecond : String = ""
    var strInterval : String = ""
    val arrayEntry : ArrayList<Entry> = arrayListOf()
    var strestimatedMintes : String=""

    init {
        strGetMaxMilsecond=maxMailisecond
        strInterval=interval
        arrayEntry.addAll(listEntry)
        strestimatedMintes=eastimatedLiminit
    }

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





    private fun setupGraph() {
        val getMiliSecond =strGetMaxMilsecond.toLong()

        var limit_minit=strestimatedMintes
        val l = limit_minit.toInt()
        val limitesLine = l * 60



        val limitFloat =limitesLine.toFloat()


        val limitLine =LimitLine(limitFloat,limit_minit+"m")
        limitLine.labelPosition=LimitLine.LimitLabelPosition.LEFT_BOTTOM
        limitLine.enableDashedLine(10f,10f,10f)
        limitLine.setLineWidth(2f)
         val maxTimeInMilliseconds = getMiliSecond+ 1000L

        val valx = maxTimeInMilliseconds.toInt() / 1000
        val xaxisValue = valx.toFloat()

        xAxis = mChart!!.xAxis
        xAxis!!.enableGridDashedLine(10f, 10f, 0f)
        xAxis!!.setAxisMaximum(xaxisValue)
        xAxis!!.setAxisMinimum(0f)
        xAxis!!.setAxisLineColor(Color.DKGRAY)
        xAxis!!.setTextColor(Color.DKGRAY)
        xAxis!!.setDrawLimitLinesBehindData(false)
        xAxis!!.addLimitLine(limitLine)

        //get max value of flot
        val folatarray : ArrayList<Float> = arrayListOf()

        for( data_x in arrayEntry){
            folatarray.add( data_x.y)
        }

        val getMaxvalue_x=getMax(folatarray)

         val formata = DecimalFormat("0.00")
        formata.setRoundingMode(RoundingMode.DOWN);
        val max_x=formata.format( getMaxvalue_x).toFloat()

        val max_x_givne : Float=max_x + 0.05f

        Log.wtf("getMax_x","value of x :"+getMaxvalue_x.toString() + " covert value :"+max_x.toString() + " title display add text :"+max_x_givne)


        leftAxis = mChart!!.axisLeft
        leftAxis!!.removeAllLimitLines()
//        leftAxis!!.setAxisMaximum(DashboardActivity.currentValue4Yaxis)
        leftAxis!!.setAxisMaximum(max_x_givne)
        leftAxis!!.setAxisMinimum(0.0f)
        leftAxis!!.setAxisLineColor(Color.DKGRAY)
        leftAxis!!.setTextColor(Color.DKGRAY)
        leftAxis!!.enableGridDashedLine(10f, 10f, 0f)
        leftAxis!!.setDrawZeroLine(false)
        leftAxis!!.setDrawLimitLinesBehindData(true)

//        leftAxis!!.addLimitLine(limitLine)

        mChart!!.axisRight.isEnabled = false
        mChart!!.description.text=""
        mChart!!.fitScreen()

        initGraph()
    }

    fun getMax(inputArray: ArrayList<Float>): Float {
        var maxValue = inputArray[0]
        for (i in 1 until inputArray.size) {
            if (inputArray[i] > maxValue) {
                maxValue = inputArray[i]
            }
        }
        return maxValue
    }

    private fun initGraph() {


        values= arrayListOf()
        values.clear()
        for(datavalues in arrayEntry){
            val decimalFormat = DecimalFormat("0.000")
            decimalFormat.setRoundingMode(RoundingMode.UP)



            val x = datavalues.x.toFloat()
            val y = decimalFormat.format(datavalues.y).toFloat()

            values.add(Entry(x,y))

        }

//        for (data in values){
//            Log.wtf("graphentry","secondget :"+data.x.toString()+" ,number"+data.y.toString())
//
//        }

        set1 = LineDataSet(values, "Result Sample Data")
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
        set1!!.formSize = 4f
        val drawable = ContextCompat.getDrawable(requireContext()!!, R.drawable.fade_blue)
        set1!!.fillDrawable = drawable
        set1!!.valueFormatter=Formater()
        dataSets = ArrayList()
        dataSets.add(set1!!)
        mChart!!.isAutoScaleMinMaxEnabled = false

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
