package com.mystrica.fragment

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.mystrica.R
import com.mystrica.activity.CoverActivity.Companion.strUserId
import com.mystrica.activity.DashboardActivity
import com.mystrica.adpter.ScanResultAdapter
import com.mystrica.util.ConnectionManager
import com.mystrica.util.Constant
import com.mystrica.util.Utils
import com.mystrica.util.Utils.TodayDate
import kotlinx.android.synthetic.main.connect_blu_fragment.view.*
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.runOnUiThread


@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class ConnectBluetothFragment() : Fragment() {

    private var rootview : View ?= null

    private var titleName : String ?= ""


    private val scanResults : ArrayList<ScanResult> = arrayListOf()

    private lateinit var scanResultAdapter : ScanResultAdapter

    private var strbtnConnextText=""

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
    private val bleScanner by lazy {
        bluetoothAdapter.bluetoothLeScanner
    }

    private val bluetoothAdapter: BluetoothAdapter by lazy {
        val bluetoothManager = requireActivity().getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
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
            runOnUiThread { visibleScanly(if (value) "Stop Scan" else "Start Scan") }
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


        setupViews()

        (context as DashboardActivity?)!!.HeadetTitle("Connect Device")
        (activity as DashboardActivity?)!!.visibleBackButton()
        (activity as DashboardActivity?)!!.hideHomeButton()
        return  rootview
    }

    private fun setupViews() {
        rootview?.pr_collect_data?.setVisibility(View.GONE)
        val today = TodayDate()

        rootview?.txt_type_title?.text="Covid-19-" + strUserId + "-" + today


        setupRecyclerView()

        //click linstner
        rootview?.txt_connect?.setOnClickListener{
            isScanning=false
            startBleScan()
        }

    }

    fun requestResult() {

        rootview?.txt_connect?.performClick();

    }

    private fun bleEnableDisable() {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter.isEnabled) {
            startScanningBle()

        }
        else if (bluetoothAdapter.disable()) {
            strbtnConnextText="Enable Bluethooth!"
            ButtonTextSet("Enable Bluethooth!")
            runOnUiThread {
                alert {
                    title = "Mystrica"
                    message = "Enable bluetooth?"
                    isCancelable = false
                    positiveButton(android.R.string.ok) {
                        bluetoothAdapter.enable()
                        rootview?.txt_connect!!.text="Start Scan"

                    }
                }.show()
            }


        }
    }

    private fun startScanningBle(){
        scanResults.clear()
        scanResultAdapter.notifyDataSetChanged()
        bleScanner.startScan(null, scanSettings, scanCallback)
        isScanning = true
    }

    public fun startBleScan() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !(isLocationPermissionGranted as Boolean)) {
            strbtnConnextText="Locatin Permission"
            ButtonTextSet(strbtnConnextText)
            requestLocationPermission()
        } else {

            bleEnableDisable()


        }
    }

    public fun stopBleScan() {
        bleScanner.stopScan(scanCallback)
        isScanning = false

        //visibleScanly("Start Scan")

        //startBleScan()
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
        }
    }

    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(requireActivity().applicationContext)
        rootview?.recy_blu_device?.layoutManager = layoutManager

        scanResultAdapter=ScanResultAdapter(scanResults,requireActivity()!!, requireActivity().applicationContext)
        rootview?.recy_blu_device?.adapter=scanResultAdapter

    }




    //todo visiblilty function
    fun visibleScanly(message: String?) {

        rootview?.txt_connect?.setText(message)

        if(message.equals("Stop Scan")){
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
    val isLocationPermissionGranted get() = hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)

    private fun hasPermission(permissionType: String): Any {
        return ContextCompat.checkSelfPermission(requireActivity(), permissionType) ==
                PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        if (isLocationPermissionGranted as Boolean) {
            return
        }

        runOnUiThread {
            alert {
                title = "Mystrica"
                message = "Location permission is required"
                isCancelable = false
                positiveButton(android.R.string.ok) {
                    requestPermission(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        LOCATION_PERMISSION_REQUEST_CODE
                    )
                }
            }.show()
        }
    }




    private fun requestPermission(permission: String, requestCode: Int) {
        ActivityCompat.requestPermissions(requireActivity(), arrayOf(permission), requestCode)
    }

    //todo ble callback call class
    fun connectGatt(b: Boolean, getdevice: BluetoothDevice) {

        ConnectionManager.connect(getdevice,requireContext(), Constant.MY_TEST_SERVICE_UUID)



//        Utils.replaceFragment(requireActivity(), WarmupFragment(), true, ConnectBluetothFragment::class.java.name)
//        Utils.replaceFragment(requireActivity(), BLeValueSetFragment(), true, ConnectBluetothFragment::class.java.name)


    }


    override fun onResume() {
        super.onResume()

    }

  private fun  ButtonTextSet(title : String){
      rootview?.txt_connect?.text=title
  }



}



