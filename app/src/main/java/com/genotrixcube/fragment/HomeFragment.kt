package com.genotrixcube.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.genotrixcube.R
import com.genotrixcube.activity.DashboardActivity
import com.genotrixcube.adpter.HomeAdpter
import com.genotrixcube.fragment.GetClientIdFragment.Companion.strPatinetId
import com.genotrixcube.fragment.RegentIdFragment.Companion.strRegentId
import com.genotrixcube.model.HomeDataDO
import com.genotrixcube.util.ConnectionManager
import com.genotrixcube.util.Constant
import com.genotrixcube.util.Utils
import java.util.*
import kotlin.experimental.and

class HomeFragment : Fragment() {
    private var rootview: View? = null
    private var reHome: RecyclerView? = null
    private var arrayHome: ArrayList<HomeDataDO>? = null
    private var homeAdpter: HomeAdpter? = null
    private var mGridLayoutManager: GridLayoutManager? = null


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootview = inflater.inflate(R.layout.fragment_home, container, false)

        setupViews()
        (context as DashboardActivity?)!!.HeadetTitle("Home")
        (activity as DashboardActivity?)!!.hideBackButton()
        (activity as DashboardActivity?)!!.visibleLogOutButton()
        Utils.hideKeyboard(requireContext())
        arrayHome = ArrayList()
        arrayHome!!.add(HomeDataDO("Run Test", true))
        arrayHome!!.add(HomeDataDO("Run QC", true))
        arrayHome!!.add(HomeDataDO("History", true))
        arrayHome!!.add(HomeDataDO("Preferences", false))
        arrayHome!!.add(HomeDataDO("Tools", false))
        arrayHome!!.add(HomeDataDO("Settings", true))

        homeAdpter = HomeAdpter(
            arrayHome!!,
            activity,
            requireContext(),
            HomeFragment::class.java.simpleName,
            this@HomeFragment
        )
        reHome!!.adapter = homeAdpter
        return rootview
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSIONS_REQUEST_HOME_LOCATION_DASHBORD -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                gotoSubTypeFragment()
            } else if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                @SuppressLint("NewApi", "LocalSuppress") val showRationale =
                    shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)
                if (!showRationale) {

                    //set up setting dialog
                    Utils.callPermissionONMessagesDilog(
                        requireContext(),
                        "Genotrix",
                        "Must on location permission"
                    )
                } else {
                    requestLocationPermission()
                }
            }
        }
    }



    override fun onDestroy() {
        super.onDestroy()
        Constant.selectTest = ""
    }

    override fun onResume() {
        super.onResume()
        (context as DashboardActivity?)!!.HeadetTitle("Home")
    }

    private fun callConnectBlue() {
        DashboardActivity.firstResult = false
        DashboardActivity.firstResultValue = ""
        strPatinetId = ""
        strRegentId = ""
        Constant.selectTest = ""
        Constant.selectSubTest = ""
        DashboardActivity.countDownTimer!!.cancel()
        val fm = requireActivity().supportFragmentManager
        for (i in 0 until fm.backStackEntryCount) {
            fm.popBackStack()
        }

        Utils.replaceFragment(requireActivity(),ConnectBluetothFragment.newInstance("newInstance"),true,HomeFragment::class.java.name)
    }

    fun checkHomePermission(title: String) {
        Constant.selectTest = ""
        Constant.selectTest = title

        if (title == "Settings") {

            val fragment = SettingFragmnet("Settings")
            Utils.replaceFragment(requireActivity(),fragment,true,HomeFragment::class.java.name)
        }
        else if(title.equals("History")){

            val fragment = HistoryFragment()
            Utils.replaceFragment(requireActivity(),fragment,true,HomeFragment::class.java.name)

        }
        else {
            //checkforPermission
            if (hasLocationPermission()) {
                gotoSubTypeFragment()
            } else {
                requestLocationPermission()
            }
        }
    }

    private fun gotoSubTypeFragment() {
        val fragment =
            TestTypeFragment(Constant.selectTest)

        Utils.replaceFragment(requireActivity(),fragment,true,HomeFragment::class.java.name)

    }

    //request permission code
    private fun hasLocationPermission(): Boolean {
        return (ContextCompat.checkSelfPermission(
           requireActivity(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        )== PackageManager.PERMISSION_GRANTED)
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            PERMISSIONS_REQUEST_HOME_LOCATION_DASHBORD
        )
    }

    private fun setupViews() {
        reHome = rootview!!.findViewById(R.id.recy_home)
        mGridLayoutManager = GridLayoutManager(activity, 2)
        reHome!!.setLayoutManager(mGridLayoutManager)
    }

    companion object {
        const val PERMISSIONS_REQUEST_HOME_LOCATION_DASHBORD = 10001
    }
}