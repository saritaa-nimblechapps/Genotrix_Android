package com.mystrica.activity

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.mystrica.R
import com.mystrica.activity.CoverActivity.Companion.strUserId
import com.mystrica.fragment.*
import com.mystrica.fragment.GetClientIdFragment.Companion.strPatinetId
import com.mystrica.fragment.RegentIdFragment.Companion.strRegentId
import com.mystrica.util.ConnectionManager
import com.mystrica.util.Constant
import com.mystrica.util.Utils
import okhttp3.internal.Util
import java.util.concurrent.TimeUnit

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

    private var fragment: CollectSampleFragment? = null
    private val currentUpdateMilisecond: Long = 0


    companion object{
        val intentAction = "com.parse.push.intent.RECEIVE"
        var fistbyteArray: ByteArray ?= null
        var isCalibrateBroadCast_flag = false
        var firstResult: Boolean = false
        var firstResultValue : String= ""

        var isSubType = false

        var isCalibrated = false
        var issetPwm = false
        var isSetCurrent = false
        var currentValue4Yaxis = 0.1f

        var imgback: ImageView? = null
        var countDownTimer: MyCountDownTimer? = null
        var sList: List<String> = arrayListOf()
         var currentUpdateMilisecond: Long = 0
        var isTimerFinished = false

        var value = ""
        var byteArraydata: ByteArray ?=null
    }




    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)


        setviews()
        HeadetTitle(resources.getString(R.string.home))

        //firist inint with the connected fragment.
        countDownTimet_int()

        setupHomeFragment()




    }

    private fun setviews() {
        imgback = findViewById(R.id.img_back)
        imgHome = findViewById(R.id.img_home)
        txtLogout = findViewById(R.id.txt_logout)
        txtHeadetTitle = findViewById(R.id.txt_header_title)

        fm=supportFragmentManager

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
                if(fragment != null){

                    if(grantResults.firstOrNull()==PackageManager.PERMISSION_DENIED){

                        @SuppressLint("NewApi", "LocalSuppress") val showRationale = shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)

                        if(!showRationale){
                            //parment denined code


                        }else{
                           // fragment.requestResult()
                        }
                    }
                    else{

                        fragment.requestResult()

                    }
                }


            }
        }
    }

    override fun onClick(view: View?) {

        when (view!!.id) {
            R.id.img_back -> onBacPressHeaderButton()
            R.id.img_home -> callHomeDashboard()
            R.id.txt_logout -> callLogout()
        }
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
                CollectSampleFragment.storeData!!.clear()
                CollectSampleFragment.tempData!!.clear()
                DashboardActivity.countDownTimer!!.cancel()
                fragmnet!!.popBackStack()
            } else if (f.javaClass.name.equals( StartFragment::class.java.name) ){
                strRegentId = ""
                fragmnet!!.popBackStack()
            } else if (f.javaClass.name .equals( RegentIdFragment::class.java.name) ){
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
            } else if (f.javaClass.name .equals( SendEmailFragment::class.java.name)) {
                //dialog_backInfomr(fm)
            } else if (f.javaClass.name == HomeFragment::class.java.name) {
                fragmnet!!.popBackStack(
                    null,
                    FragmentManager.POP_BACK_STACK_INCLUSIVE
                )
                //                DashboardActivity.communicator.disconnectBlue();
                ConnectionManager.disconnect()
            } else if (f.javaClass.name .equals( ResultFragment::class.java.name) ){
                //dialog_backInfomr(fm)
            } else if (f.javaClass.name .equals( HomeFragment::class.java.name)) {
                val connectedDevice: BluetoothDevice = ConnectionManager.getDevice()
                if (connectedDevice != null) {
                    ConnectionManager.disconnect()
                    if (connectedDevice != null) {
                        if (true.also { isCalibrated = it }) {
                            isCalibrated = false
                        }
                    }
                }
                fragmnet!!.popBackStack()
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

    fun callHomeDashboard() {
        firstResult = false
        isSubType = false
        firstResultValue = ""
        //        DashboardActivity.isCalibrated = false;
        isCalibrated = true
        isStartTimer = false
        isSetCurrent = false
        issetPwm = false
        strPatinetId = ""
        strRegentId = ""
        Constant.selectTest = ""
        Constant.selectSubTest = ""
        val f = fm!!.findFragmentById(R.id.container)
        if (f != null) {
            if (f.javaClass.simpleName == CollectSampleFragment::class.java.simpleName || f.javaClass.simpleName == ResultFragment::class.java.simpleName) {
                countDownTimer!!.cancel()

                if (CollectSampleFragment.storeData != null || CollectSampleFragment.storeData!!.size > 0) {
                    CollectSampleFragment.storeData = null
                }
            }
        }
        fm!!.popBackStack(
            HomeFragment::class.java.simpleName,
            FragmentManager.POP_BACK_STACK_INCLUSIVE
        )
    }

    fun hideHomeButton() {
        imgHome!!.visibility = View.INVISIBLE
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

        override fun onReceive(context: Context, intent: Intent) {

            val extras  = intent.getExtras()

            if(extras!= null){
                if(extras.containsKey("deviceNm")){
                    Utils.hideProgress()

                    Utils.pref_set_String(Constant.deviceName,extras.getString("deviceNm"),applicationContext)
                    Utils.replaceFragment(this@DashboardActivity, WarmupFragment(), true, ConnectBluetothFragment::class.java.name)

                    setDeviceName(extras.getString("deviceNm") )
                }
                if(extras.containsKey("code")){

//                    extras.getByteArray("bytedata")?.let { setReadValue(it) }
                    extras.getByteArray("bytedata")?.let { extras.getString("data")?.let { it1 ->
                        checkListValue(
                            it1,it)
                    } }
                }

            }

        }
    }

    private fun setReadValue(byte: ByteArray) {

        val cf = supportFragmentManager.findFragmentById(R.id.container)

        if(cf != null){

            if(cf  is BLeValueSetFragment){
                cf.setReadValue(byte)
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
        val resVl = resultValue.toByteArray()
        // convert(resVl);
        resultValue = resultValue.replace("[\\[\\](){}]".toRegex(), "")
        value = resultValue
        if (byteArraydata != null) {
            byteArraydata = null
        }
        byteArraydata = ByteArray(byteaaray.size)
        byteArraydata = byteaaray
        Log.wtf("array ", " isCalibrated :=>" + isCalibrated);
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
                                countDownTimer?.start()
                                cf.startCounter()
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


    private fun setDeviceName(devicename: String?) {

        val cf = supportFragmentManager.findFragmentById(R.id.container)

        if(cf != null){

            if(cf  is BLeValueSetFragment){
                   cf.setDeviceName(devicename)
            }

        }
    }

    override fun onBackPressed() {
        super.onBackPressed()

        if (fm!!.backStackEntryCount > 0) {
            Log.i(
                "bit value",
                "first value :" + fm!!.backStackEntryCount
            )
        }
        ConnectionManager.disconnect()
    }


    override fun onPause() {
        super.onPause()

        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);

    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver,  IntentFilter(intentAction))
    }


    //
    private  fun callLogout(): Unit {
        Utils.pref_set_String(Constant.userprefid, "", applicationContext)
        ConnectionManager.disconnect()
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
        val fm = supportFragmentManager
        for (i in 0 until fm.backStackEntryCount) {
            fm.popBackStack()
        }
        val callGetUserId = Intent(applicationContext, CoverActivity::class.java)
        callGetUserId.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
        callGetUserId.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(callGetUserId)
        finish()
    }


    //todo custom Counter Class
    @SuppressLint("NewApi")
    class MyCountDownTimer(
        var startTime: Long,
        var interval: Long,
        var applicationContext: Context,
        var fm: FragmentManager?
    ) : CountDownTimer(startTime, interval) {
        var getMiliSecond: Long = Utils.pref_get_long(Constant.qcMaxmiliSecond,   applicationContext, Constant.maxDefaultMinit)
        var addExtra4second: Long = 24000
        override fun onFinish() {
           // val second = ((getMiliSecond - currentUpdateMilisecond) / 1000) as Int
            isTimerFinished = true
        }

        override fun onTick(millisUntilFinished: Long) {
            val remainedSecs = millisUntilFinished / 1000
            currentUpdateMilisecond = millisUntilFinished
            val mint = (remainedSecs / 60).toString()
            val sec = (remainedSecs % 60).toString()
            val time = "$mint : $sec"


            // this will decrement 100 to 1
            val second = ((getMiliSecond - millisUntilFinished) / 1000).toInt()
            @SuppressLint("DefaultLocale") val text = String.format(
                "%2d : %02d",
                TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(
                    TimeUnit.MILLISECONDS.toHours(millisUntilFinished)
                ),
                TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
                )
            )
            datasend(value, byteArraydata!!, second,fm!!,applicationContext!!)
            val currentMiliSecond = getMiliSecond - addExtra4second
            if (millisUntilFinished == currentMiliSecond) {
               onFinish()
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
        countDownTimer = MyCountDownTimer(getMiliSecond /*3 mint*/, getinterval,applicationContext,fm)
    }

    fun replaceFragment(fragment: HomeFragment, backStackk: String) {
        fm = supportFragmentManager
        fragmentTransaction = fm!!.beginTransaction()
        fragmentTransaction!!.replace(R.id.container, fragment!!)
        fragmentTransaction!!.addToBackStack(backStackk)
        fragmentTransaction!!.commit()

    }

}