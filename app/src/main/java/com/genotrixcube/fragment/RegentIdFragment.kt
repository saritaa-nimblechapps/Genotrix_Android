package com.genotrixcube.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.genotrixcube.R
import com.genotrixcube.activity.DashboardActivity
import com.genotrixcube.util.Constant
import com.genotrixcube.util.Utils
import com.genotrixcube.util.Utils.hideKeyboard

class RegentIdFragment(title: String) : Fragment(),
    View.OnClickListener {
    private var rootview: View? = null
    private var btnNext: Button? = null
    private var title: TextView? = null
    private var imgScan: ImageView? = null
    private var txtScanTitle: TextView? = null
    private var txtUserTitle: TextView? = null
    private var edtUserId: AppCompatEditText? = null
    private var container: ConstraintLayout? = null
    private var strtitle = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootview = inflater.inflate(R.layout.activity_cover, container, false)
        (activity as DashboardActivity?)!!.HeadetTitle(strtitle)
        (activity as DashboardActivity?)!!.visibleHomeButton()
        setupViews()
        title!!.visibility = View.VISIBLE
        edtUserId!!.isFocusable = true
        txtScanTitle!!.text = requireContext().getString(R.string.scan_regent_id)
        txtUserTitle!!.text = requireContext().getString(R.string.enter_regent_id)
        edtUserId!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence,
                i: Int,
                i1: Int,
                i2: Int
            ) {
            }

            override fun onTextChanged(
                charSequence: CharSequence,
                i: Int,
                i1: Int,
                i2: Int
            ) {
            }

            override fun afterTextChanged(editable: Editable) {
                val id = edtUserId!!.text.toString()
                if (!id.isEmpty()) {
                    btnNext!!.isEnabled = true
                }
            }
        })
        edtUserId!!.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                btnNext!!.performClick()
                return@OnEditorActionListener true
            }
            false
        })
        return rootview
    }

    private fun setStrRegentId() {
        Log.wtf("strRegentId  :", strRegentId)
        if (strRegentId != "") {
            Log.wtf("strRegentId inside if :", strRegentId)
            edtUserId!!.setText(strRegentId)
        }
    }

    private fun setupViews() {
        container = rootview!!.findViewById(R.id.cover_container)
        btnNext = rootview!!.findViewById(R.id.btn_next)
        imgScan = rootview!!.findViewById(R.id.img_scan)
        title = rootview!!.findViewById(R.id.txt_cover_title)
        txtScanTitle = rootview!!.findViewById(R.id.txt_scan_title)
        txtUserTitle = rootview!!.findViewById(R.id.txt_id_title)
        edtUserId = rootview!!.findViewById(R.id.edt_user_id)
        setStrRegentId()

        //screen touch listner
        container!!.setOnTouchListener(OnTouchListener { view, motionEvent ->

            hideKeyboard(requireContext())
            true
        })
        btnNext!!.setOnClickListener(this)
        imgScan!!.setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()
        setStrRegentId()
    }

    private fun checkForCameraParmession() {
        if (hasCameraPermission()) {
            gotoCodeScan()
        } else {
            requestCameraParmeission()
        }
    }

    private fun gotoCodeScan() {
        val fragment = RegentIdScanFragment()
        Utils.replaceFragment(requireActivity(),fragment,true,RegentIdScanFragment::class.java.name)

    }

    private fun requestCameraParmeission() {
        ActivityCompat.requestPermissions(
            requireActivity(), arrayOf(Manifest.permission.CAMERA),
            PERMISSIONS_REQUEST_REGENT_FRAGMENT_CAMERA
        )
    }

    private fun hasCameraPermission(): Boolean {
        return (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSIONS_REQUEST_REGENT_FRAGMENT_CAMERA -> if (grantResults.size > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                gotoCodeScan()
            } else if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                @SuppressLint("NewApi", "LocalSuppress") val showRationale =
                    shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)
                if (!showRationale) {

                    //set up setting dialog
                    Utils.callPermissionONMessagesDilog(
                        requireContext(),
                        "Genotrix",
                        "Must on camera permission"
                    )
                } else {
                    requestCameraParmeission()
                }
            }
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_next -> {
                val getuserid = edtUserId!!.text.toString()
                if (!getuserid.isEmpty()) {
                    strRegentId = getuserid

                    Utils.hideKeyboard(requireContext())
                    val strSampleDateTime =Utils.TodayDate() +" "+Utils.TodayTime12hrs()
                    Utils.pref_set_String(Constant.sample_date_time,strSampleDateTime,requireContext())

                    val fragment = CollectSampleFragment(strtitle)
                    Utils.replaceFragment(requireActivity(),fragment,true,RegentIdScanFragment::class.java.name)

                } else {
                    Toast.makeText(context, "Enter regent id", Toast.LENGTH_LONG).show()
                }
            }
            R.id.img_scan -> checkForCameraParmession()
        }
    }

    companion object {
        var strRegentId = ""
        const val PERMISSIONS_REQUEST_REGENT_FRAGMENT_CAMERA = 1112
    }

    init {
        strtitle = title
    }
}