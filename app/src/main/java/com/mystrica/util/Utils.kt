package com.mystrica.util

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.mystrica.R
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object Utils {

    var dialog : Dialog ?= null
    var mProgressBar:ProgressBar ?= null

    const val  strFolderName = "Genotrix Docs"


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

    fun pref_get_int(key: String?, context: Context): Int {
        val sharedPrefs = getSharedPreferencesData(context)
        return sharedPrefs.getInt(key, 0)
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

    fun miliSecondToString(milisecont: Long): String? {
        var strTime = ""
        val seconds = (milisecont / 1000).toInt() % 60
        val minutes = (milisecont / (1000 * 60) % 60).toInt()
        val cal = Calendar.getInstance()
        cal.timeInMillis = milisecont

//        strTime=cal.get(Calendar.MINUTE)+" : "+cal.get(Calendar.SECOND);
        strTime = "$minutes : $seconds"
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


}