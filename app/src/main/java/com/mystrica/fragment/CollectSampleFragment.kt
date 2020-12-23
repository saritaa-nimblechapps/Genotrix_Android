package com.mystrica.fragment

import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.data.Entry
import com.mystrica.R
import com.mystrica.activity.DashboardActivity
import com.mystrica.adpter.SampleDataAdpter
import com.mystrica.fragment.ConnectBluetothFragment
import com.mystrica.fragment.GetClientIdFragment.Companion.strPatinetId
import com.mystrica.util.Constant
import com.mystrica.util.Utils
import java.util.*

class CollectSampleFragment : Fragment {
    private var rootview: View? = null
    private var txtTitle: TextView? = null
    private var txtMsg: TextView? = null
    private var txtConnect: TextView? = null
    private var txtMin: TextView? = null
    private var txtMax: TextView? = null
    private var txtMessage: TextView? = null
    private var ly: LinearLayout? = null
    private val lyResult: RelativeLayout? = null
    private var progressBar: ProgressBar? = null
    private var strHeaderTitle = ""
    private val strMsg =
        "Pour 3 drops in the sample tube, shake it while it remains in the holder"
    private var progressStatus = 0
    private var progressMaxSecond = 180
    private var maxStat = 0
    private val handler = Handler()
    var countSeconds: MyCountDownTimerSecondUPdate? =
        null
    var adpter: SampleDataAdpter? = null
    private val interval_one_second: Long = 1000 // 10 sec in milisecond
    private var maxTimeInMilliseconds: Long = 0 // 3 mint in milisec
    private var progress_inteval = 0 // get from mili seconds (milis /1000)

    //    public static boolean isCalibrated = false;
    var graphfragment: Fragment? = null

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
        (activity as DashboardActivity?)!!.visibleBackButton()
        (activity as DashboardActivity?)!!.visibleHomeButton()
        prefValues
        setupViews()
        setupCounterTime_textValueset()
        val today: String = Utils.TodayDate()!!
        txtTitle!!.text = "Covid-19-" + strPatinetId.toString() + "-" + today
        txtMsg!!.text = strMsg

//        txtMax.setText("3 : 00  Max");
        txtConnect!!.setOnClickListener { //                DashboardActivity.communicator.connectToDevice(null);
            var fragment: ResultFragment? = null
            //check for array null or not
            val passingData =
                setupdAdpter()
            if (passingData != null) {
                if (passingData.size >= 1) {
                    fragment = ResultFragment(strHeaderTitle)
                    Utils.replaceFragment(requireActivity(),fragment,true,ConnectBluetothFragment::class.java.name)
//                    (context as DashboardActivity?).replaceFragment(
//                        fragment,
//                        ConnectBluetothFragment::class.java.simpleName
//                    )
                } else {
                }
            } else {
            }
        }
        return rootview
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
            val intervaladd: Long = 24000
            maxTimeInMilliseconds = getMiliSecond - intervaladd
            progress_inteval = maxTimeInMilliseconds.toInt() / 1000
            progressMaxSecond = progress_inteval
        }

    private fun setupCounterTime_textValueset() {
        countSeconds = MyCountDownTimerSecondUPdate(
            maxTimeInMilliseconds /*3 mint*/,
            interval_one_second
        )
        val maxtime: String = Utils.miliSecondToString(maxTimeInMilliseconds)!!
        txtMax!!.text = "$maxtime Max"
        txtMin!!.text = "$maxtime Min"
    }

    private fun setupdAdpter(): ArrayList<Entry>? {
        return (graphfragment as GraphFragment?)!!.resultArray()!!!!
    }

    private fun setupViews() {
        txtTitle = rootview!!.findViewById(R.id.txt_type_title)
        txtMsg = rootview!!.findViewById(R.id.txt_blue_text_connecty)
        txtConnect = rootview!!.findViewById(R.id.txt_connect)
        ly = rootview!!.findViewById(R.id.ly_pr_timer)
        txtMessage = rootview!!.findViewById(R.id.txt_message_graph)
        txtMessage!!.setVisibility(View.GONE)
        txtMin = rootview!!.findViewById(R.id.txt_min_mints)
        txtMax = rootview!!.findViewById(R.id.txt_max_mint)
        txtConnect!!.setText("Next")
        progressBar = rootview!!.findViewById(R.id.pr_collect_data)
        progressBar!!.setVisibility(View.VISIBLE)
        progressBar!!.setMax(progress_inteval)
        ly!!.setVisibility(View.VISIBLE)
        txtConnect!!.setVisibility(View.INVISIBLE)
        graphfragment = GraphFragment(true)

        // setupGraph();
        setupGraphFragment()
        storeData =
            ArrayList()
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

        // sList.add(data);

        //sperate 2 parameter
        // getGraphXvalue(data, Second);
        val grahp = GraphFragment(true)
        if (graphfragment != null) {
            (graphfragment as GraphFragment).addGraphPloatData(data!!, Second, byteArraydata!!)
            //            ((GraphFragment)graphfragment).addGraphPloatData(data, Second);
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
        txtConnect!!.visibility = View.INVISIBLE
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
                    //                            Log.d("progress ", "progress stats :" + progressStatus + "/" + progressBar.getMax());
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
        // isCalibrated = false;
        countSeconds!!.cancel()
    }

    // Counter Class
    inner class MyCountDownTimerSecondUPdate(
        startTime: Long,
        interval: Long
    ) : CountDownTimer(startTime, interval) {
        override fun onFinish() {
//            Log.e("Times up", "Times up");
            setTextTimer("0 : 00" + " Min")
            txtConnect!!.visibility = View.VISIBLE
        }

        override fun onTick(millisUntilFinished: Long) {
            txtMessage!!.visibility = View.GONE
            var time = ""
            val remainedSecs = millisUntilFinished / 1000
            val mint = (remainedSecs / 60).toString()
            val sec = (remainedSecs % 60).toString()
            time = "$mint : $sec Min"
            val minvalue = mint.toInt()
            if (minvalue == 0) {
                time = "$sec Sec"
            }
            //            Log.d("progress ", "progress seconds :" + remainedSecs);
            //    Log.d("progress ", "progress stats :" + mint + ":" + sec);
            //   progressBar.setProgress((int) remainedSecs);

            // int min_int = Integer.parseInt(mint) + 1;
            setTextTimer(time)
        }

        init {
            txtMessage!!.visibility = View.VISIBLE
        }
    }

    private fun setTextTimer(time: String) {
        txtMin!!.text = time
    }

    companion object {
        var storeData: ArrayList<Entry>? = null
        var tempData: ArrayList<Entry>? = null
    }
}