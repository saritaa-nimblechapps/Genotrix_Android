package com.mystrica.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.mystrica.R
import com.mystrica.activity.DashboardActivity
import com.mystrica.fragment.GetClientIdFragment.Companion.strPatinetId
import com.mystrica.util.ConnectionManager
import com.mystrica.util.Constant
import com.mystrica.util.Utils
import com.mystrica.util.Utils.TodayDate
import kotlin.experimental.and

class StartFragment(strHeaderTitle: String) : Fragment() {
    private var rootview: View? = null
    private var txtTitle: TextView? = null
    private var txtMsg: TextView? = null
    private var txtConnect: TextView? = null
    private var txtMessage: TextView? = null
    private var lyProgress: LinearLayout? = null
    private var progressBar: ProgressBar? = null
    private var strHeaderTitle = ""
    private val strMsg =
        "Open the Lid, insert the tube in adapter, mix the swab for 10 seconds"
    private var utils: Utils? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootview = inflater.inflate(R.layout.fragment_start, container, false)
        (activity as DashboardActivity?)!!.HeadetTitle(strHeaderTitle)
        (activity as DashboardActivity?)!!.visibleBackButton()
        (activity as DashboardActivity?)!!.visibleHomeButton()
        setupViews()
        val today = TodayDate()
        txtTitle!!.text = "Covid-19-" + strPatinetId.toString() + "-" + today
        txtMsg!!.text = strMsg


//        ((DashboardActivity)getContext()).CalibrateCall();
        if (Constant.selectTest == "Run Test") {
//            txtConnect.setVisibility(View.INVISIBLE);
//            new CountDownTimer( 20000, 1000) {
//
//                public void onTick(long millisUntilFinished) {
//                    txtMessage.setVisibility(View.VISIBLE);
//                }
//
//                public void onFinish() {
//                    checkBluEnable();
//                    txtMessage.setVisibility(View.GONE);
//                    txtConnect.setVisibility(View.VISIBLE);
//                }
//            }.start();
        }
        txtConnect!!.setOnClickListener {
            if (Constant.selectTest == "Run Test") {
                samplecollectFragmentCall()
            } else {
                //check for location permission
                checkBluEnable()
            }
        }
        return rootview
    }

    private fun checkBluEnable() {
        val adapter = BluetoothAdapter.getDefaultAdapter()
        if (adapter.isEnabled) {

//            Log.d("off","on bluethooth");
            checkHomePermission()
        } else {
            AlertDialog.Builder(context)
                .setTitle("Oops Device disconnect")
                .setMessage("Go back and select device")
                .setPositiveButton("Ok") { dialogInterface, i ->
                    dialogInterface.dismiss()
                   requireActivity().onBackPressed()
                }
                .show()
        }
    }

    fun checkHomePermission() {
        //checkforPermission
        if (hasLocationPermission()) {
            gotoCollectSample()
        } else {
            requestLocationPermission()
        }
    }

    private fun hasLocationPermission(): Boolean {
        return context != null && ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        if (activity != null) {
            ActivityCompat.requestPermissions(
               requireActivity(),
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),
                PERMISSIONS_REQUEST_START_LOCATION_DASHBORD
            )
        }
    }

    private fun gotoCollectSample() {
        if (Constant.selectTest == "Run Test") {
            val s =ConnectionManager.getMyService()
            if (s == null ) {
                connectLost()
            } else {
                val device=ConnectionManager.getDevice()
                val custom_byte = Constant.intconvert.toByte()
                val pwmBitValue = Constant.pwmconvert.toByte()

                val setcalibrate = byteArrayOf(
                    0x00.toByte(),
                    (custom_byte and 0xFF.toByte()),
                    0x00.toByte(),
                    0x00.toByte()
                )
                ConnectionManager.writedCharetistic(device,Constant.MYSTERIA_CHARACTERISTIC_TEST_UUID,"setCalibrate",setcalibrate,true)
            }
        } else {



            samplecollectFragmentCall()
        }
    }

    private fun connectLost() {
        try {
            AlertDialog.Builder(context)
                .setTitle("Service connection lost please try again ! ") //                    .setMessage("go back and try again ")
                .setPositiveButton("Ok") { dialogInterface, i ->
                    dialogInterface.dismiss()
                    requireActivity().supportFragmentManager.popBackStack(
                        null,
                        FragmentManager.POP_BACK_STACK_INCLUSIVE
                    )
                    ConnectionManager.disconnect()
                }
                .show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun samplecollectFragmentCall() {
      //  val fragment = CollectSampleFragment(strHeaderTitle)

        Utils.replaceFragment(requireActivity(),CollectSampleFragment(strHeaderTitle),true, StartFragment::class.java.name)

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSIONS_REQUEST_START_LOCATION_DASHBORD -> if (grantResults.size > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                gotoCollectSample()
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

    private fun setupViews() {
        Utils!!.HideKeybord(requireContext(), rootview!!)
        txtTitle = rootview!!.findViewById(R.id.txt_type_title)
        txtMsg = rootview!!.findViewById(R.id.txt_blue_text_connecty)
        txtMessage = rootview!!.findViewById(R.id.txt_message4start)
        lyProgress = rootview!!.findViewById(R.id.ly_pr_timer)
        txtConnect = rootview!!.findViewById(R.id.txt_connect)
        progressBar = rootview!!.findViewById(R.id.pr_collect_data)
        txtConnect!!.setText("Next")
        txtMessage!!.setVisibility(View.GONE)
        progressBar!!.setVisibility(View.GONE)
        lyProgress!!.setVisibility(View.GONE)
    }

    override fun onDestroy() {
        super.onDestroy()
        //CollectSampleFragment.isCalibrated=false;
        DashboardActivity.firstResult = false
    }

    companion object {
        const val PERMISSIONS_REQUEST_START_LOCATION_DASHBORD = 10003
    }

    init {
        this.strHeaderTitle = strHeaderTitle
    }
}