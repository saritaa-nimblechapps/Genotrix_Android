package com.genotrixcube.util

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.genotrixcube.BuildConfig
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

import com.genotrixcube.R
import com.genotrixcube.model.TestTyepSampleDataDO
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object Utils {

    var dialog : Dialog ?= null
    var mProgressBar:ProgressBar ?= null

    const val  strFolderName = "Genotrix Docs"

    fun isNetConnected(context: Context): Boolean {
        val cm = context
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo

        return activeNetwork != null && activeNetwork.isConnectedOrConnecting
    }

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =context.getSystemService(Context.CONNECTIVITY_SERVICE)
        return if (connectivityManager is ConnectivityManager) {
            val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
            networkInfo?.isConnected ?: false
        } else false
    }

    fun showToast(context: Context?,message: String?){
        try {
            context?.let {
                val toast = Toast.makeText(it, message, Toast.LENGTH_LONG)

                // set message color
                val textView = toast.view.findViewById(android.R.id.message) as? TextView
                textView?.setTextColor(Color.WHITE)
                textView?.gravity = Gravity.CENTER

                // set background color
                toast.view.setBackgroundColor(ContextCompat.getColor(it, R.color.color_blue))

                toast.setGravity(Gravity.TOP or Gravity.FILL_HORIZONTAL, 0, 0)

                toast.show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }



    fun startProgress(context: Context){
        try {


            dialog = Dialog(context)
            dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog?.getWindow()?.setBackgroundDrawableResource(android.R.color.transparent)


            dialog!!.setContentView(R.layout.progress_bar_dialog)
            mProgressBar = dialog!!.findViewById(R.id.progress_bar) as ProgressBar
            //  mProgressBar.getIndeterminateDrawable().setColorFilter(context.getResources()
            // .getColor(R.color.material_blue_gray_500), PorterDuff.Mode.SRC_IN);
            mProgressBar!!.setVisibility(View.VISIBLE)
            mProgressBar!!.setIndeterminate(true)
            dialog!!.setCancelable(true);
            dialog!!.setCanceledOnTouchOutside(true)
            dialog!!.show()

        }catch (e:java.lang.Exception){
            e.printStackTrace()
        }
    }

    fun hideProgress(){
        try {

            if(dialog !=null ){
                dialog!!.dismiss()
            }
        }catch (e:java.lang.Exception){
            e.printStackTrace()
        }
    }

    fun replaceFragment(activity: FragmentActivity?, fragment: Fragment?, isAddToBackStack: Boolean, backStackName:String) {
        try {
            fragment?.let {mFragment ->
                if (isAddToBackStack) {
                    activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.container, mFragment)
                        ?.addToBackStack(backStackName)?.commitAllowingStateLoss()
                } else {
                    activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.container, mFragment)
                        ?.commitAllowingStateLoss()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun callPermissionONMessagesDilog(
        context: Context,
        title: String?,
        msg: String?,
        actiion : Boolean
    ) {
        AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(msg)
            .setNegativeButton("No", null)
            .setPositiveButton(
                "Yes"
            ) { dialogInterface, i ->

                if(actiion){
                    val intent = Intent()
                    intent.action =Settings.ACTION_LOCATION_SOURCE_SETTINGS
                    context.startActivity(intent)
                }
                else{
                    val intent = Intent()
                    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    val uri =
                        Uri.fromParts("package", context.packageName, null)
                    intent.data = uri
                    context.startActivity(intent)
                }

            }
            .show()
    }

    fun hideKeyboard(mContext: Context?) {
        if (mContext != null) {
            try {
                val imm = mContext
                    .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                if (imm != null && imm.isActive) {
                    imm.hideSoftInputFromWindow(
                        (mContext as Activity).window
                            .currentFocus!!.windowToken, 0
                    )
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }

    fun byteToInt(b: Byte): Int {
        return b.toInt()
    }

    fun callPermissionONMessagesDilog(
        context: Context,
        title: String?,
        msg: String?
    ) {
        AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(msg)
            .setNegativeButton("No", null)
            .setPositiveButton(
                "Yes"
            ) { dialogInterface, i ->
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri =
                    Uri.fromParts("package", context.packageName, null)
                intent.data = uri
                context.startActivity(intent)
            }
            .show()
    }

    fun getSharedPreferencesData(context: Context): SharedPreferences {
        return context.getSharedPreferences("Mystrica", Context.MODE_PRIVATE)
    }

    fun pref_set_String(
        key: String?,
        userId: String?,
        context: Context
    ) {
        if (userId != null) {
            val editor = getSharedPreferencesData(
                context
            ).edit()
            editor.putString(key, userId).apply()
        }
    }

    fun pref_set_Boolean(key: String?,flag : Boolean,context: Context){

        val editor= getSharedPreferencesData(context).edit()
        editor.putBoolean(key,flag).apply()

    }
    fun pref_set_arrayLedSetting(key : String, yourObject: ArrayList<TestTyepSampleDataDO>, context: Context) {

        val editor= getSharedPreferencesData(
            context
        ).edit()
        val gson : Gson = Gson()
        val josn=gson.toJson(yourObject)
        editor.putString(key,josn)
        editor.apply()

    }

    fun pref_set_int(
        key: String?,
        value: Int,
        context: Context
    ) {
        val editor = getSharedPreferencesData(
            context
        ).edit()
        editor.putInt(key, value).apply()
    }

    fun pref_set_long(
        key: String?,
        value: Long,
        context: Context
    ) {
        val editor = getSharedPreferencesData(
            context
        ).edit()
        editor.putLong(key, value).apply()
    }

    fun pref_get_arrayLedSetting(key : String, context: Context): ArrayList<TestTyepSampleDataDO> {
        val yourArrayList: ArrayList<TestTyepSampleDataDO>
//        val json = getSharedPreferencesData(context).getString(Constant.arryCategoryMaintace.toString(), "")

        val editor= getSharedPreferencesData(
            context
        ).getString(key,"")
        val gson: Gson= Gson()
        val josn=editor


        yourArrayList = when {
            josn.isNullOrEmpty() -> ArrayList()
            else -> gson.fromJson(josn, object : TypeToken<List<TestTyepSampleDataDO>>() {}.type)
        }


        return yourArrayList
    }

    fun pref_get_int(key: String?, context: Context): Int {
        val sharedPrefs = getSharedPreferencesData(context)
        return sharedPrefs.getInt(key, 0)
    }
    fun pref_get_int_default(key: String?, context: Context,drfaultvauel : Int): Int {
        val sharedPrefs = getSharedPreferencesData(context)
        return sharedPrefs.getInt(key, drfaultvauel)
    }

    fun pref_get_String(
        key: String?,
        context: Context
    ): String? {
        val sharedPrefs = getSharedPreferencesData(context)
        return sharedPrefs.getString(key, "")
    }

    fun pref_get_long(
        key: String?,
        context: Context,
        defaultvalue: Long
    ): Long {
        val sharedPrefs = getSharedPreferencesData(context)
        return sharedPrefs.getLong(key, defaultvalue)
    }

    fun pref_get_Boolean(
        key: String?,
        context: Context,
        defaultvalue: Boolean
    ): Boolean {
        val sharedPrefs = getSharedPreferencesData(context)
        return sharedPrefs.getBoolean(key,defaultvalue)
    }

    fun HideKeybord(context: Context, view: View) {
        val imm =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }



    fun TodayDate(): String? {
        var strToday = ""
        val c = Calendar.getInstance().time
        val df =
            SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault())
        strToday = df.format(c)
        return strToday
    }

    fun TodayTime(): String? {
        var strToday = ""
        val c = Calendar.getInstance().time
        val df =
            SimpleDateFormat("HH:mm", Locale.getDefault())
        strToday = df.format(c)
        return strToday
    }

    fun TodayTime12hrs(): String? {
        var strToday = ""
        val c = Calendar.getInstance().time
        val df =
            SimpleDateFormat("hh:mm aa", Locale.getDefault())
        strToday = df.format(c)
        return strToday
    }

    fun miliSecondToString(milisecont: Long): String? {
        var strTime = ""
        val seconds = (milisecont / 1000).toInt() % 60
        val minutes = (milisecont / (1000 * 60) % 60).toInt()
        val cal = Calendar.getInstance()
        cal.timeInMillis = milisecont

        val stringMintesformate= String.format("%1d",minutes)
        val stringSecondformate= String.format("%1d",minutes)
//        strTime=cal.get(Calendar.MINUTE)+" : "+cal.get(Calendar.SECOND);
        strTime = "$stringMintesformate : $stringSecondformate"
        return strTime
    }

    fun miliMaxSecondToString(milisecont: Long): String? {
        var strTime = ""
        val seconds = (milisecont / 1000).toInt() % 60
        val minutes = (milisecont / (1000 * 60) % 60).toInt()
        val cal = Calendar.getInstance()
        cal.timeInMillis = milisecont

//        var stringMintesformate= String.format("%1d",minutes)
        var mintes: String=minutes.toString()
        if(mintes.length<2){
            mintes = "0"+mintes
        }

        strTime = "$mintes : 00"
        return strTime
    }

    fun miliSecondToMinites(milisecont: Long): Int {
        var strTime = 0
        val minutes = (milisecont / (1000 * 60) % 60).toInt()
        strTime = minutes
        return strTime
    }

    fun miliSecondToSecond(milisecont: Long): Int {
        var strTime = 0
        val seconds = (milisecont / 1000).toInt() % 60
        strTime = seconds
        return strTime
    }


    fun getDirectory(context: Context): File? {


        val externalFileDir:  File  = context.getExternalFilesDir(null)!!

        val createdDir = File(externalFileDir.absoluteFile, strFolderName)


        if (!createdDir.exists()) {

            createdDir.mkdirs()
        }


        return createdDir
    }

    fun getAppVersion(): String{
        var strVersionCode : String = ""

        strVersionCode= BuildConfig.VERSION_CODE.toString()

        return strVersionCode
    }

    fun DialogDisconnect(
        context: Context
    ) {


        AlertDialog.Builder(context)
            .setTitle("Disconeted!!")
            .setMessage("Please connect device and try again!   ")
            .setPositiveButton(
                "OK"
            ) { dialogInterface, i ->

                dialogInterface.dismiss()


            }

            .show()
    }

    fun DialogAlertVauleLimitOver(context: Context){



        AlertDialog.Builder(context)
            .setTitle("Invalid input!")
            .setMessage("Valid values are 0 - 255")
            .setPositiveButton(
                "OK"
            ) { dialogInterface, i ->

                dialogInterface.dismiss()

            }
            .show()
    }

    fun DialogDateSelected(
        context: Context,
        rootview: View,
        isStartDate: Boolean,
        edtStartDate: TextView
    ) {

        val c: Calendar = Calendar.getInstance();

        val year: Int
        val mont: Int
        val day: Int

        year = c.get(Calendar.YEAR);
        mont = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            context,
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->


                val calendar = Calendar.getInstance()
                calendar.set(year, monthOfYear, dayOfMonth)


                val format = SimpleDateFormat("dd-MMM-yyyy")
                val strDate = format.format(calendar.time)

                //   edDateTextview.setText(dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year)

                if(isStartDate){
                    edtStartDate.setText(strDate)

                }else{

                    edtStartDate.setText(strDate)

                }

//                rootview.btn_submit4amenitiesbooking?.isEnabled =
//                        strStartDate.isNotEmpty() && strStartTimer.isNotEmpty() && strendTime.isNotEmpty() && strEndDate.isNotEmpty() && strRemark.isNotEmpty()




            },
            year,
            mont,
            day

        )

        datePickerDialog.setButton(
            DialogInterface.BUTTON_NEGATIVE, "Cancel",
            DialogInterface.OnClickListener { dialog, which ->
                if (which == DialogInterface.BUTTON_NEGATIVE) {

                    // Do Stuff for negative

                    if(isStartDate){

                    }else{

                        if(isStartDate){



                        }else{



                        }


                    }
                }
            })
        datePickerDialog.setCanceledOnTouchOutside(true)
       // datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000)
        datePickerDialog.show()

    }


    fun getCurrentScreenlockTime(context: Context): Int {
        var time=0
        try {
            time=Settings.System.getInt( context.contentResolver, Settings.System.SCREEN_OFF_TIMEOUT)
        }
        catch (e:Exception){
            e.printStackTrace()
        }
        return time
    }


}