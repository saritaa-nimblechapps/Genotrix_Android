package com.mystrica.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mystrica.R
import com.mystrica.activity.CoverActivity.Companion.strUserId
import com.mystrica.activity.DashboardActivity
import com.mystrica.adpter.TestTypeAdpter
import com.mystrica.fragment.HomeFragment
import com.mystrica.fragment.TestTypeFragment
import com.mystrica.model.HomeDataDO
import com.mystrica.util.ConnectionManager
import com.mystrica.util.Constant
import com.mystrica.util.Utils
import com.mystrica.util.Utils.TodayDate
import java.util.*
import kotlin.experimental.and

class TestTypeFragment : Fragment {
    private var rootview: View? = null
    private var reyTest: RecyclerView? = null
    private var txtTypeTitle: TextView? = null
    private var arrayHome: ArrayList<HomeDataDO>? = null
    private var testAdpter: TestTypeAdpter? = null
    private var mGridLayoutManager: GridLayoutManager? = null
    private var titleHeader = ""

    constructor() {}
    constructor(title: String) {
        titleHeader = title
    }

    private var strSendtitle = ""
    private var dialog: AlertDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootview = inflater.inflate(R.layout.fragment_test_type, container, false)

        //change Dashboard header title.
        (activity as DashboardActivity?)!!.HeadetTitle(titleHeader)
        (activity as DashboardActivity?)!!.visibleBackButton()
        (activity as DashboardActivity?)!!.visibleHomeButton()
        setupViews()

        callOperation()

        arrayHome = ArrayList()
        arrayHome!!.add(HomeDataDO("Saliva", true))
        arrayHome!!.add(HomeDataDO("Plasma", true))
        arrayHome!!.add(HomeDataDO("Blood", true))
        arrayHome!!.add(HomeDataDO("Urine", true))
        arrayHome!!.add(HomeDataDO("Swab - Naso Pharingeal", true))
        arrayHome!!.add(HomeDataDO("Viral Transport Medium", true))

        //
        val today = TodayDate()
        txtTypeTitle!!.text = "Covid-19-" + strUserId.toString() + "-" + today
        testAdpter = TestTypeAdpter(
            arrayHome!!,
            requireActivity(),
            titleHeader,
            HomeFragment::class.java.simpleName,
            this@TestTypeFragment
        )
        reyTest!!.adapter = testAdpter
        return rootview
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSIONS_REQUEST_TYPE_LOCATION_DASHBORD -> if (grantResults.size > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                //DialogCalibrateResponse();
            } else if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                @SuppressLint("NewApi", "LocalSuppress") val showRationale =
                    shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)
                if (!showRationale) {

                    //set up setting dialog
                    Utils.callPermissionONMessagesDilog(
                        requireContext(),
                        "Mystrica",
                        "Must on location permission"
                    )
                } else {
                    requestLocationPermission()
                }
            }
        }
    }

    fun checkSubType(homeDataDOTitle: String?, title: String) {
        Constant.selectSubTest = ""
        Constant.selectSubTest = homeDataDOTitle!!
        strSendtitle = title
        DashboardActivity.isSubType = true

        //       gotoConnectBluFragment();

        //checkforPermission
        if (hasLocationPermission()) {
            PwmCall()
            // DialogCalibrateResponse();
        } else {
            requestLocationPermission()
        }
    }

    fun PwmCall() {



        gotoConnectBluFragment()
    }


    private fun callOperation(){
        val custom_byte = Constant.intconvert.toByte()
        val pwmBitValue = Constant.pwmconvert.toByte()

        val setcalibrate = byteArrayOf(
            0x00.toByte(),
            (custom_byte and 0xFF.toByte()),
            0x00.toByte(),
            0x00.toByte()
        )
        val setpwm = byteArrayOf(
            0x02.toByte(),
            (custom_byte and 0xFF.toByte()),
            (pwmBitValue and 0xFF.toByte()),
            0x00.toByte()
        )

        val setcurrent = byteArrayOf(
            0x03.toByte(),
            (custom_byte and 0xFF.toByte()),
            (pwmBitValue and 0xFF.toByte()),
            0x00.toByte()
        )

        val setired = byteArrayOf(
            0x01.toByte(),
            (custom_byte and 0xFF.toByte()),
            0x00.toByte(),
            0x00.toByte()
        )
        val device = ConnectionManager.getDevice()
        DashboardActivity.isCalibrated = true


        ConnectionManager.writedCharetistic(device,Constant.MYSTERIA_CHARACTERISTIC_TEST_UUID,"setled",setired,false)
        ConnectionManager.writedCharetistic(device,Constant.MYSTERIA_CHARACTERISTIC_TEST_UUID,"setCalibrate",setcalibrate,true)
        ConnectionManager.setEnableNotification(device,Constant.MYSTERIA_CHARACTERISTIC_RESILT_UUID,"setResult")

        ConnectionManager.writedCharetistic(device,Constant.MYSTERIA_CHARACTERISTIC_TEST_UUID,"setPwm",setpwm,false)
        ConnectionManager.writedCharetistic(device,Constant.MYSTERIA_CHARACTERISTIC_TEST_UUID,"setCurrent",setcurrent,false)

    }
    private fun DialogCalibrateResponse() {

        //    callCalibrate();
        dialog = AlertDialog.Builder(context)
            .setTitle("Device calibrate")
            .setMessage("Please wait device is calibrating... ")
            .show()
    }

    fun DialogClose(value: String?) {
        if (value != null) {
            dialog!!.dismiss()
            gotoConnectBluFragment()
        }
        //        else{
//            Handler handler = new Handler();
//            handler.postDelayed(new Runnable() {
//                public void run() {
//                    // yourMethod();
//                    dialog.dismiss();
//                    DailogforReCalibrate();
//                }
//            }, 2000);   //5 seconds
//        }
    }

    private fun DailogforReCalibrate() {
        dialog = AlertDialog.Builder(context)
            .setTitle("")
            .setMessage("Recalibrate ")
            .setPositiveButton("Ok") { dialogInterface, i -> DialogCalibrateResponse() }
            .show()
    }

    private fun gotoConnectBluFragment() {
        val fragment = GetClientIdFragment(strSendtitle)

        Utils.replaceFragment(activity,GetClientIdFragment(strSendtitle),true,TestTypeFragment::class.java.name)
    }

    //request permission code
    private fun hasLocationPermission(): Boolean {
        return (ContextCompat.checkSelfPermission(
            requireContext()!!,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
                == PackageManager.PERMISSION_GRANTED)
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            PERMISSIONS_REQUEST_TYPE_LOCATION_DASHBORD
        )
    }

    override fun onResume() {
        super.onResume()
        (activity as DashboardActivity?)!!.HeadetTitle(titleHeader)
    }

    override fun onDestroy() {
        super.onDestroy()
        Constant.selectSubTest = ""
    }

    private fun setupViews() {
        txtTypeTitle = rootview!!.findViewById(R.id.txt_type_title)
        reyTest = rootview!!.findViewById(R.id.recy_test)
        mGridLayoutManager = GridLayoutManager(activity, 2)
        reyTest!!.setLayoutManager(mGridLayoutManager)
    }

    companion object {
        const val PERMISSIONS_REQUEST_TYPE_LOCATION_DASHBORD = 10002
    }
}