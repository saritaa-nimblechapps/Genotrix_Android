package com.genotrixcube.activity

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.genotrixcube.R
import com.genotrixcube.activity.CoverActivity.Companion.strUserId
import com.genotrixcube.fragment.*
import com.genotrixcube.fragment.GetClientIdFragment.Companion.strPatinetId
import com.genotrixcube.fragment.RegentIdFragment.Companion.strRegentId
import com.genotrixcube.util.ConnectionManager
import com.genotrixcube.util.Constant
import com.genotrixcube.util.Utils

class DashboardActivity : AppCompatActivity(), View.OnClickListener {


    private var toolbar: Toolbar? = null
    private var txtHeadetTitle: TextView? = null

    private var imgHome: ImageView? = null
    private var txtLogout: TextView? = null

    var fm: FragmentManager? = null
    var fragmentTransaction: FragmentTransaction? = null

    var homeFragment: HomeFragment? = null
    var connectFragment: ConnectBluetothFragment? = null



    var isStartTimer = false

    // Global Variable


    companion object{
        //use for receive brodcast value ( parameter set in brodcast intent)
        val intentAction = "com.parse.push.intent.RECEIVE"

        var fistbyteArray: ByteArray ?= null
        var firstResult: Boolean = false
        var firstResultValue : String= ""

        var isSubType = false

        var isCalibrated = false
        var issetPwm = false
        var isSetCurrent = false
        var currentValue4Yaxis = 0.1f
        var currentValue4Xaxis =50f

        var imgback: ImageView? = null
        var countDownTimer: MyCountDownTimer? = null
        var sList: List<String> = arrayListOf()
         var currentUpdateMilisecond: Long = 0
        var isTimerFinished = false

        var value = ""
        var byteArraydata: ByteArray ?=null

         var isSendingData : Boolean =false

        var isLedSetting :Boolean =false

    }




    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)


        setviews()
        HeadetTitle(resources.getString(R.string.home))

        //inint with the connected fragment.
        countDownTimet_int()

        setupHomeFragment()




    }

    private fun setviews() {
        imgback = findViewById(R.id.img_back)
        imgHome = findViewById(R.id.img_home)
        txtLogout = findViewById(R.id.txt_logout)
        txtHeadetTitle = findViewById(R.id.txt_header_title)

        fm = supportFragmentManager



        imgHome!!.setOnClickListener(this)
        imgback!!.setOnClickListener(this)
        txtLogout!!.setOnClickListener(this)
    }



     @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
     private fun setupHomeFragment() {
        //connect fragment calling
        Utils.replaceFragment(this@DashboardActivity, ConnectBluetothFragment.newInstance("Connect Device"), false, DashboardActivity::class.java.name)
    }



    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            ConnectBluetothFragment.LOCATION_PERMISSION_REQUEST_CODE->{
                var fragment = supportFragmentManager.findFragmentById(R.id.container) as ConnectBluetothFragment


                if(fragment != null)
                {
                    Log.wtf(
                        "permission",
                        "=================================  onRequestPermissionsResult ===================================="
                    )
                    //checking for permission is grandted or not
                    //code is updted with grant cheking only.

                    fragment.requestResult(requestCode, permissions as Array<String>,grantResults)



//                    if(grantResults.firstOrNull()==PackageManager.PERMISSION_DENIED){
//
//                        @SuppressLint("NewApi", "LocalSuppress") val showRationale = shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)
//
//                    }
//                    else{
//                        fragment.requestResult()
//                    }



                }


            }
        }
    }

    override fun onClick(view: View?) {

        when (view!!.id) {
            R.id.img_back -> onBacPressHeaderButton()
            R.id.img_home -> callNewTestDashboard()
            R.id.txt_logout -> callLogout()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

    }



    fun HeadetTitle(header: String?) {
        txtHeadetTitle!!.text = header
    }

    fun visibleBackButton() {
        imgback?.setVisibility(View.VISIBLE)
    }

    fun hideBackButton() {
        imgback?.setVisibility(View.INVISIBLE)
    }

    private fun onBacPressHeaderButton() {
        if (supportFragmentManager.backStackEntryCount > 0) {
           val f = (this@DashboardActivity as AppCompatActivity).supportFragmentManager.findFragmentById(R.id.container)
            val fragmnet =supportFragmentManager

//            val f = fm!!.findFragmentById(R.id.container)
            if (f!!.javaClass.name.equals( CollectSampleFragment::class.java.name)) {
                isStartTimer = false
                //  firstResult=false;
                currentValue4Yaxis = 0.1f
                currentValue4Xaxis=100f
                CollectSampleFragment.storeData!!.clear()
                CollectSampleFragment.tempData!!.clear()
                DashboardActivity.countDownTimer!!.cancel()
                fragmnet!!.popBackStack()
            }  else if (f.javaClass.name .equals( RegentIdFragment::class.java.name) ){
                strPatinetId = ""
                fragmnet!!.popBackStack()
            } else if (f.javaClass.name .equals( GetClientIdFragment::class.java.name) ){
                Constant.selectSubTest = ""
                isSubType = false
                isStartTimer = false
                fragmnet!!.popBackStack(
                    TestTypeFragment::class.java.name,
                    FragmentManager.POP_BACK_STACK_INCLUSIVE
                )
            }
            else if(f.javaClass.name.equals(SeachIdScanFragment::class.java.name)){
                fragmnet!!.popBackStack(HistoryFragment::class.java.name,FragmentManager.POP_BACK_STACK_INCLUSIVE)
            }
            else if (f.javaClass.name .equals( SendEmailFragment::class.java.name)) {
            }  else if (f.javaClass.name .equals( ResultFragment::class.java.name) ){
            }
            else if(f.javaClass.name.equals(WarmupFragment::class.java.name)){

            }
            else if (f.javaClass.name .equals( HomeFragment::class.java.name)) {

            } else if (f.javaClass.name .equals( TestTypeFragment::class.java.name) ){
                fragmnet!!.popBackStack()
            } else if (f.javaClass.name .equals( ConnectBluetothFragment::class.java.name) ){
                strUserId = ""
                val connectedDevice: BluetoothDevice = ConnectionManager.getDevice()
                if (connectedDevice != null) {
                    if (true.also { isCalibrated = it }) {
                        isCalibrated = false
                    }
                }
            } else {
                fragmnet!!.popBackStack()
            }
        } else {
            super.onBackPressed()
        }
    }

    fun callNewTestDashboard() {
        firstResult = false
        isSubType = false
        firstResultValue = ""
        //        DashboardActivity.isCalibrated = false;
        isCalibrated = true
        isStartTimer = false
        isSetCurrent = false
        issetPwm = false
        currentValue4Yaxis = 0.1f
        currentValue4Xaxis=100f
        strPatinetId = ""
        strRegentId = ""
        Constant.selectTest = ""
        Constant.selectSubTest = ""
        val f = fm!!.findFragmentById(R.id.container)
        if (f != null) {
            if ((f.javaClass.simpleName .equals( CollectSampleFragment::class.java.simpleName) )||( f.javaClass.simpleName.equals(ResultFragment::class.java.simpleName))) {
                countDownTimer!!.cancel()

                if (CollectSampleFragment.storeData != null || CollectSampleFragment.storeData!!.size > 0) {
                    CollectSampleFragment.storeData!!.clear()
                    CollectSampleFragment.tempData!!.clear()

                }
            }
        }



        DashboardActivity.countDownTimer!!.cancel()

        for (i in 0 until fm!!.backStackEntryCount) {
            fm!!.popBackStack()
        }
        Utils.replaceFragment(this@DashboardActivity, HomeFragment(), false, WarmupFragment::class.java.name)

    }

    fun hideAllLeftButton() {
        imgHome!!.visibility = View.INVISIBLE
        txtLogout!!.visibility = View.GONE

    }

    fun visibleHomeButton() {
        imgHome!!.visibility = View.VISIBLE
        txtLogout!!.visibility = View.GONE
    }

    fun visibleLogOutButton() {
        imgHome!!.visibility = View.GONE
        txtLogout!!.visibility = View.VISIBLE
    }

    //get Brodcastvalue
    private val mBroadcastReceiver = object : BroadcastReceiver() {

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        override fun onReceive(context: Context, intent: Intent) {

            val extras  = intent.getExtras()

            if(extras!= null){
                if(extras.containsKey("deviceNm")){
                    Utils.hideProgress()
                    Log.wtf("mulfile :", "brodcast get the receive info")

                    Utils.pref_set_String(Constant.deviceName,extras.getString("deviceNm"),applicationContext)
                    Utils.replaceFragment(this@DashboardActivity, WarmupFragment(), true, ConnectBluetothFragment::class.java.name)

                }
               else  if(extras.containsKey("deviceError")){

                    //Utils.hideProgress()
                    val cf = supportFragmentManager.findFragmentById(R.id.container)

                    if(cf != null){

                        if(cf  is ConnectBluetothFragment){

                            try{
                                val getdevie =cf.getDevie()

                                if(getdevie != null)
                                {
                                    Log.wtf("mulfile","Connection error call :")

                                    cf.connectGatt(true,getdevie)
                                }
                            }catch (e: Exception){
                                e.printStackTrace()
                            }

                        }

                    }

                }
               else  if(extras.containsKey("code")){

//                    extras.getByteArray("bytedata")?.let { setReadValue(it) }
                    extras.getByteArray("bytedata")?.let { extras.getString("data")?.let { it1 ->
                        checkListValue(
                            it1,it)
                    } }
                }
              else  if(extras.containsKey("disconnect")){

                    val getpref_value=Utils.pref_get_Boolean(Constant.isconnecting,this@DashboardActivity,false)

                    Log.wtf("mul","connecting error :"+getpref_value)
                    if(getpref_value){

                    }else{
                        AlertDialog.Builder(this@DashboardActivity)
                            .setTitle("Disconeted!!")
                            .setMessage("Please connect device and try again!   ")
                            .setPositiveButton(
                                "OK"
                            ) { dialogInterface, i ->

                                dialogInterface.dismiss()
                                callLogout()

                            }
                            .show()
                    }

                }

            }

        }
    }



    private fun isAllZeros(stringArray: Array<String>): Boolean {
        for (i in stringArray.indices) {
            if (stringArray[i].trim { it <= ' ' }
                    .toInt() == 0
            ) // the condition you're checking for is that they're all greater than, so if its less than or equal to, it gets flagged
                return false
        }
        return true
    }

    private fun checkListValue(
        resultValue: String,
        byteaaray: ByteArray
    ) {
        var resultValue = resultValue
        var contains = false

        resultValue = resultValue.replace("[\\[\\](){}]".toRegex(), "")
        value = resultValue
        if (byteArraydata != null) {
            byteArraydata = null
        }
        byteArraydata = ByteArray(byteaaray.size)
        byteArraydata = byteaaray

        if (isCalibrated) {
            val stringArray =
                resultValue.split(",".toRegex()).toTypedArray()
            contains = isAllZeros(stringArray)
            if (contains) {

                //get first value of calibrate.
                if (!firstResult) {
                   firstResultValue = ""
                    DashboardActivity.firstResultValue = value
                    fistbyteArray = byteArraydata
                    Log.i(
                        "bit value",
                        "first value :" + DashboardActivity.firstResultValue
                    )
                    firstResult = true
                }
                val fm = supportFragmentManager
                val f = fm.findFragmentById(R.id.container)
                if (f != null) {
                    if (f.javaClass.name.equals( CollectSampleFragment::class.java.name)) {
                        val cf: CollectSampleFragment? =
                            supportFragmentManager.findFragmentById(R.id.container) as CollectSampleFragment?
                        if (cf != null) {
                            if (!isStartTimer) {
//                                countDownTimer?.start()
//                                cf.startCounter()
                                isStartTimer = true
                            }
                        }
                    }
                    else if (f.javaClass.name.equals(TestTypeFragment::class.java.name)) {
                        val cf: TestTypeFragment? =
                            supportFragmentManager.findFragmentById(R.id.container) as TestTypeFragment?
                        if (cf != null) {
                            Log.d(
                                "subtype",
                                "calibrating sub type :" + isSubType + "value :" + resultValue
                            )
                            if (isSubType) {
                                //cf.DialogClose(value);
                            }
                        }
                    }
                }
            } else {
                val fm = supportFragmentManager
                val f = fm.findFragmentById(R.id.container)
                if (f != null) {
                    if (f.javaClass.simpleName == TestTypeFragment::class.java.getSimpleName()) {
                        val cf: TestTypeFragment? =
                            supportFragmentManager.findFragmentById(R.id.container) as TestTypeFragment?
                        if (cf != null) {
                            Log.d(
                                "subtype",
                                "calibrating sub type :" + isSubType + "value :" + resultValue
                            )
                            if (isSubType) {
                                //cf.DialogClose(value);
                            }
                        }
                    }
                }
            }
        }
    }




    override fun onBackPressed() {
        if (fm!!.backStackEntryCount > 0) {
            callLogout()
        } else {
            super.onBackPressed()
        }
    }


    override fun onPause() {
        super.onPause()

        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);

    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver,  IntentFilter(intentAction))
    }



      fun callLogout(): Unit {
        Utils.pref_set_String(Constant.userprefid, "", applicationContext)
        Utils.pref_set_Boolean(Constant.isconnecting,false,applicationContext)

        //get current fragment
        val f = fm!!.findFragmentById(R.id.container)

        if(f!= null){
            if(f.javaClass.name.equals(ConnectBluetothFragment::class.java.name)){

            }else{
                ConnectionManager.disconnect()
                ConnectionManager.connectionDisconnect()

            }
        }



        firstResult = false
        firstResultValue = ""
        isSubType = false
        isCalibrated = false
        isSetCurrent = false
        issetPwm = false
        strPatinetId = ""
        strUserId = ""
        strRegentId = ""
        Constant.selectTest = ""
        Constant.selectSubTest = ""
        DashboardActivity.countDownTimer!!.cancel()


        finish()


    }


    //todo custom Counter Class
    @SuppressLint("NewApi")
    class MyCountDownTimer(
        var startTime: Long,
        var interval: Long,
        var applicationContext: Context,
        var fm: FragmentManager?,
        var dashboardActivity: DashboardActivity
    ) : CountDownTimer(startTime, interval) {

        var getMiliSecond: Long = Utils.pref_get_long(Constant.qcMaxmiliSecond,   applicationContext, Constant.maxDefaultMinit)
        var addExtra4second: Long = Constant.addExtraIntrval
        override fun onFinish() {
           // val second = ((getMiliSecond - currentUpdateMilisecond) / 1000) as Int
            isTimerFinished = true
            isSendingData=false
            Log.wtf("timerCounter","on fininsh call")
            countDownTimer!!.cancel()


        }

        override fun onTick(millisUntilFinished: Long) {
            val remainedSecs = millisUntilFinished / 1000
            currentUpdateMilisecond = millisUntilFinished
            val mint = (remainedSecs / 60).toString()
            val sec = (remainedSecs % 60).toString()
            val time = "$mint : $sec"
            val second = ((getMiliSecond - millisUntilFinished) / 1000).toInt()

            if(isSendingData){

            }else{
                datasend(value, byteArraydata!!, second,fm!!,applicationContext!!)

            }

            val currentMiliSecond = getMiliSecond - addExtra4second



            if (millisUntilFinished <= Constant.addExtraIntrval) {

                isSendingData=true
                Log.wtf("timerCounter"," current miliseocnd :"+currentMiliSecond + " ,get miliscoend :"+getMiliSecond + " , getaddextrasecond :"+addExtra4second +" , udpate miliseocnd :"+millisUntilFinished)
                onFinish()
                cancel()
                countDownTimer!!.cancel()
                addDBReocrds()

            }
        }

         fun datasend(
             value: String,
             byteArraydata: ByteArray,
              second: Int,
             fm: FragmentManager,
             applicationContext: Context
         ) {

            val f = fm.findFragmentById(R.id.container)
            if (f != null) {
                if (f.javaClass.simpleName == CollectSampleFragment::class.java.simpleName) {
                    val cf = fm.findFragmentById(R.id.container) as CollectSampleFragment?


                    //let valueISVisibleIRRawADC = (UInt16(aArray[5]) << 8) | UInt16(aArray[4])
                    cf!!.addData(value, second, byteArraydata)
                }
            }

        }


        fun addDBReocrds(){
            val f = fm!!.findFragmentById(R.id.container)
            if (f != null) {
                if (f.javaClass.simpleName == CollectSampleFragment::class.java.simpleName) {
                    val cf = fm!!.findFragmentById(R.id.container) as CollectSampleFragment?
//                    cf!!.enableButtons()
                    cf!!.addDBRecord()
                }
            }
        }
    }



    fun countDownTimet_int() {
        // Oncreate
        val getMiliSecond: Long = Utils.pref_get_long(
            Constant.qcMaxmiliSecond,
            applicationContext,
            Constant.maxDefaultMinit
        )
        val getinterval: Long = Utils.pref_get_long(
            Constant.qcintervalSecond,
            applicationContext,
            Constant.maxDefalultInterval
        )

        Log.wtf("timerCounter","Counter timer :+"+getMiliSecond + " inteval :"+getinterval)

        val maxtimeMilisecond=getMiliSecond -Constant.addExtraIntrval
        countDownTimer = MyCountDownTimer(getMiliSecond /*3 mint*/, getinterval,applicationContext,fm,this@DashboardActivity)
    }

    fun replaceFragment(fragment: HomeFragment, backStackk: String) {

        fragmentTransaction = fm!!.beginTransaction()
        fragmentTransaction!!.replace(R.id.container, fragment!!)
        fragmentTransaction!!.addToBackStack(backStackk)
        fragmentTransaction!!.commit()

    }

}