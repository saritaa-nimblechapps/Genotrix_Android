package com.genotrixcube.fragment

import android.Manifest
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.genotrixcube.R
import com.genotrixcube.activity.CoverActivity.Companion.strUserId
import com.genotrixcube.activity.DashboardActivity
import com.genotrixcube.adpter.ScanResultAdapter
import com.genotrixcube.util.ConnectionManager
import com.genotrixcube.util.Constant
import com.genotrixcube.util.Utils
import com.genotrixcube.util.Utils.TodayDate
import kotlinx.android.synthetic.main.connect_blu_fragment.view.*
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.runOnUiThread
import com.genotrixcube.BuildConfig



@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class ConnectBluetothFragment() : Fragment() {

    private var rootview : View ?= null

    private var titleName : String ?= ""


    private val scanResults : ArrayList<ScanResult> = arrayListOf()

    private lateinit var scanResultAdapter : ScanResultAdapter

    private var strbtnConnextText=""

    private var connectDeive : BluetoothDevice ?= null
    private var blscanner : BluetoothLeScanner ?= null
    private var adpter : BluetoothAdapter ?= null

    private var isFirstLocadingScreen : Boolean ?= false

    companion object {

        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(
            titleName: String
        ) =
            ConnectBluetothFragment().apply {
                arguments = Bundle().apply {
                    putString("titlenm", titleName)

                }
            }

        const val LOCATION_PERMISSION_REQUEST_CODE = 2
        const val REQUEST_ENABLE_GPS = 516
    }


    private fun bleScannerDevice(){
        val bluetoothManager = requireActivity().getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        adpter= bluetoothManager.adapter

        blscanner= adpter!!.bluetoothLeScanner
    }


    //scan resutl
    private val scanSettings = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()
    } else {
        TODO("VERSION.SDK_INT < LOLLIPOP")
    }

    private var isScanning = false
        set(value) {
            field = value
            runOnUiThread { visibleScanly(if (value) "Get List of Devices" else "Get List of Devices") }
        }



    //todo star oncreate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            titleName = it.getString("titlenm")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootview= inflater.inflate(R.layout.connect_blu_fragment, container, false)

        isFirstLocadingScreen=true
        setupViews()

        (context as DashboardActivity?)!!.HeadetTitle("Connect Device")
        (activity as DashboardActivity?)!!.hideBackButton()
        (activity as DashboardActivity).visibleLogOutButton()


        return  rootview
    }

    private fun setupViews() {
        rootview?.pr_collect_data?.setVisibility(View.GONE)
        val today = TodayDate()

        rootview?.txt_type_title?.text=strUserId + "-" + today


        setupRecyclerView()
//        isScanning=false

        //click linstner
        rootview?.txt_connect?.setOnClickListener{
            isScanning=false
            startBleScan()
        }

    }

    fun requestResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {

        var isforground=false
        var isBackground=false
        isFirstLocadingScreen=false
        if(requestCode==LOCATION_PERMISSION_REQUEST_CODE){


                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q)
                {
                    for (i in 0 until permissions.size){
                        if (permissions[i].equals(Manifest.permission.ACCESS_COARSE_LOCATION)){

                            if(grantResults[i]==PackageManager.PERMISSION_GRANTED){
                                isforground=true

                                continue

                            }else{


                                ActivityCompat.requestPermissions(
                                    requireActivity(), arrayOf(
                                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                                    ), LOCATION_PERMISSION_REQUEST_CODE
                                )

                                Log.wtf("permission", "forground permission not allowed")
                                isforground=false

                                break
                            }

                        }

                        if(permissions[i].equals(Manifest.permission.ACCESS_BACKGROUND_LOCATION)){
                            if(grantResults[i]==PackageManager.PERMISSION_GRANTED){
                                isBackground=true
                                isforground=true
                                Log.wtf("permission", "backgroudn permission is granted")


                            }else{

                                val isforground2 = ActivityCompat.checkSelfPermission(
                                    requireActivity(),
                                    Manifest.permission.ACCESS_FINE_LOCATION
                                ) == PackageManager.PERMISSION_GRANTED

                                if (isforground2) {
                                    isforground = true

                                }

                            }
                        }
                    }

                }
                else{

                    //<29 api use this function
                    for (i in 0 until permissions.size) {
                        if (permissions[i].equals(Manifest.permission.ACCESS_COARSE_LOCATION)) {

                            if (grantResults[i] >= 0) {
                                isforground = true

                                Log.wtf("permission", "forground permission is running")
                            }

                        } else {

                            Log.wtf("permission", "forground permission not allowed")

                        }
                    }

                }

            if(isforground){
                if(isBackground){

                    Log.wtf("permission", "handleLocationUpdates isbackground true")
                    startScanningDevice()
                }else{
                    startScanningDevice()
                   // rootview?.txt_connect?.performClick()
                    Log.wtf(
                        "permission",
                        "handleForegroundLocationUpdates isbackground false "
                    )
                }
            }

          //  rootview?.txt_connect?.performClick()
        }


    }

    private fun startScanningDevice(){
        rootview?.txt_connect?.performClick()
    }

    private fun bleEnableDisable() {
        bleScannerDevice()
//        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (adpter!!.isEnabled) {


            //location icon is enable or not
            val  locationManager2 = requireActivity().getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager?
            val isGpsEnabled = locationManager2!!.isProviderEnabled(LocationManager.GPS_PROVIDER)

            if(isGpsEnabled){
                startScanningBle()
            }else
                    {
                        openLocationSetting()
                    }



        }
        else if (adpter!!.disable()) {
            hideScanly()

            strbtnConnextText=resources.getString(R.string.enable_ble)
            ButtonTextSet(strbtnConnextText)

            val builder : AlertDialog.Builder = AlertDialog.Builder(requireActivity())

            builder.setTitle(resources.getString(R.string.genotrix_enable_blu_title))
            builder.setMessage(resources.getString(R.string.genotrix_enable_blu_desc))
            builder.setPositiveButton("Ok"){dialogInterface, which ->
                dialogInterface.dismiss()
//                blscanner!!.stopScan(scanCallback)
                adpter!!.enable()
                isScanning=false
                scanResults.clear()
//                hideScanly()
                rootview?.txt_connect!!.text="Get List of Devices"

                Timer()
            }

            val alertDialog: AlertDialog = builder.create()
            alertDialog.setCancelable(false)
            alertDialog.show()

        }
    }

    private fun openLocationSetting() {
        try {

            val settingsCanWrite: Boolean = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Settings.System.canWrite(requireActivity())
            } else {
                TODO("VERSION.SDK_INT < M")
            }

            if (settingsCanWrite) {
                startActivity(Intent(Settings.ACTION_SETTINGS));

            } else {

                val builder = androidx.appcompat.app.AlertDialog.Builder(requireActivity())
                builder.setMessage("Location setting enable location !!")
                    .setTitle("Location setting enable location !!")

                builder.setPositiveButton(
                    "Go to Setting"
                )
                { dialog, id ->
                    Log.i("permission", "Clicked")
                    //callCalenderDialog()
                    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }

                builder.setNegativeButton("Cancel") { dialog, id ->
                    Log.i("permission", "cancel")
                    dialog.dismiss()

                }


                val dialog = builder.create()
                dialog.show()

            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun Timer() {
        try {
            val countDownTimer = object : CountDownTimer(5000,5000){
                override fun onTick(millisUntilFinished: Long) {
                    adpter!!.enable()
                    visibleScanly("Get List of Devices")
                }

                override fun onFinish() {
                    Log.wtf("permission","enter into finish.")
                    hideScanly()
//                    startScanningBle()
                    startScanningDevice()
                }

            }
            countDownTimer.start()

        }
        catch (e: Exception){
            e.printStackTrace()
        }
    }

    private fun startScanningBle(){

            scanResults.clear()
            scanResultAdapter.notifyDataSetChanged()
            blscanner!!.startScan(null, scanSettings, scanCallback)
            isScanning = true

    }

     fun startBleScan() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !(isLocationPermissionGranted as Boolean)) {
            strbtnConnextText="Locatin Permission"
            ButtonTextSet(strbtnConnextText)
//            requestLocationPermission()
            requestLocationAccessPermission()
        } else {

            // ble is enable or not.

            bleEnableDisable()
        }
    }



    private fun requestLocationAccessPermission(){

        val isforground = ActivityCompat.checkSelfPermission(
            requireActivity(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

       if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){

           val isBackground = ActivityCompat.checkSelfPermission(
               requireActivity(),
               Manifest.permission.ACCESS_BACKGROUND_LOCATION
           ) == PackageManager.PERMISSION_GRANTED


           if(isforground){

               if(isBackground)
               {

                   isScanning=false
                   startBleScan()

               }else{

                    //countiue with the forgroud
                   ActivityCompat.requestPermissions(
                       requireActivity(),
                       arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                       LOCATION_PERMISSION_REQUEST_CODE)
               }
           }
           else{

               ActivityCompat.requestPermissions(
                   requireActivity(),
                   arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                   LOCATION_PERMISSION_REQUEST_CODE)
           }
       }
        else{

            //check the permission is permently deny or not

                if(isforground){
                    rootview?.txt_connect?.performClick()
                }else{
                    val forgorund_deniedget : Boolean =shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)

                    if(!forgorund_deniedget){
                        //open own dialog with according working related text and get the permission by button press to got to app setting page.

                        ActivityCompat.requestPermissions(
                            requireActivity(),
                            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                            LOCATION_PERMISSION_REQUEST_CODE)

                    }
                    else{
                        isFirstLocadingScreen=false
                        openAppLocationSetting()
                    }
                }
       }
    }

     fun stopBleScan() {
         blscanner!!.stopScan(scanCallback)
        isScanning = false
    }


    //go to app level setting page dialog
    private fun openAppLocationSetting() {
        try {

            val settingsCanWrite: Boolean = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Settings.System.canWrite(requireActivity())
            } else {
                TODO("VERSION.SDK_INT < M")
            }

            if (settingsCanWrite) {
                startActivity(Intent(Settings.ACTION_SETTINGS));

            } else {

                val builder = androidx.appcompat.app.AlertDialog.Builder(requireActivity()!!)
                builder.setMessage(resources.getString(R.string.genotrix_app_locationd_decs))
                    .setTitle(resources.getString(R.string.genotrix_app_locationd_title))

                builder.setPositiveButton(
                    "Go to Setting"
                )
                { dialog, id ->
                    Log.i("permission", "Clicked")
                    //callCalenderDialog()
                    startActivity(
                        Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.parse("package:" + BuildConfig.APPLICATION_ID))
                    )
                }

                builder.setNegativeButton("Cancel") { dialog, id ->
                    Log.i("permission", "cancel")
                    dialog.dismiss()
                    hideScanly()
                    ButtonTextSet(resources.getString(R.string.enable_pre))
                }


                val dialog = builder.create()
                dialog.show()

            }


            //startActivity(Intent(Settings.ACTION_SETTINGS));


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    // todo call back object (device scaning display in list)
    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val indexQuery = scanResults.indexOfFirst { it.device.address == result.device.address }
            if (indexQuery != -1) { // A scan result already exists with the same address
                scanResults[indexQuery] = result
                scanResultAdapter.notifyItemChanged(indexQuery)
            } else {
                with(result.device) {
                    if(!result.device.getName().isNullOrBlank()){
                        if(!result.device.getName().isNullOrBlank() && result.device.getName().startsWith("COL") || result.device.getName().startsWith("029"))
                        {
                            scanResults.add(result)
                            if(scanResults.size>0){
                                hideScanly()
                            }
                            scanResultAdapter.notifyItemInserted(scanResults.size - 1)

                          Log.wtf("device","Device name "+result.device.name)
                        }
                    }

                }

            }
        }

        override fun onScanFailed(errorCode: Int) {
            Log.wtf("device","Device name "+errorCode)
        }
    }

    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(requireActivity().applicationContext)
        rootview?.recy_blu_device?.layoutManager = layoutManager

        scanResultAdapter=ScanResultAdapter(scanResults,requireActivity(), requireActivity().applicationContext)
        rootview?.recy_blu_device?.adapter=scanResultAdapter

    }




    //todo visiblilty function
    fun visibleScanly(message: String?) {

        rootview?.txt_connect?.setText(message)

        if(message.equals("Get List of Devices")){
            rootview?.ly_scan_device?.setVisibility(View.VISIBLE)
            rootview?.pr_scaning_device?.setVisibility(View.VISIBLE)
            rootview?.txt_progress_message?.setText("Scanning device")
            rootview?.recy_blu_device?.setVisibility(View.GONE)

        }
        else if(message.equals("timer"))
        {
            rootview?.ly_scan_device?.setVisibility(View.VISIBLE)
            rootview?.pr_scaning_device?.setVisibility(View.VISIBLE)
            rootview?.txt_progress_message?.setText("Scanning device")
            rootview?.recy_blu_device?.setVisibility(View.GONE)
        }
    }

    fun hideScanly() {
        rootview?.ly_scan_device?.setVisibility(View.GONE)
        rootview?.recy_blu_device?.setVisibility(View.VISIBLE)
    }

    private fun changeMessage(message: String) {
        rootview?.ly_scan_device?.setVisibility(View.VISIBLE)
        rootview?.pr_scaning_device?.setVisibility(View.GONE)
        rootview?.txt_progress_message?.setText(message)
        rootview?.recy_blu_device?.setVisibility(View.GONE)
    }


    //todo permission call
    val isLocationPermissionGranted get() = hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION)

    private fun hasPermission(permissionType: String): Any {
        return ContextCompat.checkSelfPermission(requireActivity(), permissionType) ==
                PackageManager.PERMISSION_GRANTED
    }


    private fun requestPermission(permission: String, requestCode: Int) {
        ActivityCompat.requestPermissions(requireActivity(), arrayOf(permission), requestCode)
    }


    fun getDevie() :BluetoothDevice{

        var device:BluetoothDevice ?= null
        if(connectDeive!= null){
            device= connectDeive!!

        }
        return device!!
    }

    //todo ble callback call class
    fun connectGatt(progressvisible: Boolean, getdevice: BluetoothDevice) {
        stopBleScan()
        isScanning=true
        hideScanly()


        Log.wtf("connectGatt","Connection visible :"+progressvisible)
        if(progressvisible){

        }
        else{
            Utils.startProgress(requireContext())
        }
        Utils.pref_set_Boolean(Constant.isconnecting,true,requireContext())
        ConnectionManager.connect(getdevice,requireContext(), Constant.MY_TEST_SERVICE_UUID)

    }

    override fun onResume() {
        super.onResume()
        Log.wtf("permission","=======  onResume  ===========")

        bleScannerDevice()
        if(!isFirstLocadingScreen!!){

            Log.wtf("permission","Ble device discount calling dialog ")
            setupConnection()
//            requestLocationAccessPermission()

        }

    }

    private fun setupConnection(){

    }

  private fun  ButtonTextSet(title : String){
      rootview?.txt_connect?.text=title
  }

    fun setDevie(device: BluetoothDevice?) {
            connectDeive=device
    }


}



