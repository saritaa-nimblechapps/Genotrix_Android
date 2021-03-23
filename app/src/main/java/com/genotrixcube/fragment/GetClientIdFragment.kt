package com.genotrixcube.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.view.Window
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
import com.genotrixcube.util.Utils
import com.genotrixcube.util.Utils.hideKeyboard

class GetClientIdFragment(title: String) : Fragment(),
    View.OnClickListener {
    private var rootview: View? = null
    private var btnNext: Button? = null
    private var title: TextView? = null
    private var imgScan: ImageView? = null
    private var txtScanTitle: TextView? = null
    private var txtUserTitle: TextView? = null
    private var container: ConstraintLayout? = null
    private var edtUserId: AppCompatEditText? = null
    private var strtitle = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootview = inflater.inflate(R.layout.activity_cover, container, false)
        Log.wtf("patient on create id", strPatinetId)
        (activity as DashboardActivity?)!!.HeadetTitle(strtitle)
        (activity as DashboardActivity?)!!.visibleHomeButton()
        setupViews()
        title!!.visibility = View.VISIBLE
        txtScanTitle!!.text = requireActivity().getString(R.string.scab_patient_id)
        txtUserTitle!!.text = requireActivity().getString(R.string.enter_patient_id)
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
//                    DailogPwmValueGet();
                //PwmCall();
                btnNext!!.performClick()
                return@OnEditorActionListener true
            }
            false
        })
        return rootview
    }

    private fun setupViews() {
        container = rootview!!.findViewById(R.id.cover_container)
        btnNext = rootview!!.findViewById(R.id.btn_next)
        imgScan = rootview!!.findViewById(R.id.img_scan)
        title = rootview!!.findViewById(R.id.txt_cover_title)
        txtScanTitle = rootview!!.findViewById(R.id.txt_scan_title)
        txtUserTitle = rootview!!.findViewById(R.id.txt_id_title)
        edtUserId = rootview!!.findViewById(R.id.edt_user_id)
        setPatientId()
        btnNext!!.setOnClickListener(this)
        imgScan!!.setOnClickListener(this)

        // screen touch to soft keybord hide listner
        container!!.setOnTouchListener(OnTouchListener { view, motionEvent ->
//hide keyboard call
            hideKeyboard(context)
            true
        })
    }

    fun PwmCall() {}

    private fun DailogPwmValueGet() {
        val dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_char_value_get)
        val edtpwm =
            dialog.findViewById<View>(R.id.edt_dialog_pwm_value) as AppCompatEditText
        val dialog_set =
            dialog.findViewById<View>(R.id.txt_dialog_set) as Button
        val dialogCancel =
            dialog.findViewById<View>(R.id.prg_cancel) as Button
        dialog_set.setOnClickListener {
            val strPwm = edtpwm.text.toString()
            if (!strPwm.isEmpty()) {
                val intconvert = strPwm.toInt()
                val b = intconvert.toByte()
                PwmCall()
            } else {
                Toast.makeText(activity, "write pwm value", Toast.LENGTH_LONG).show()
            }
        }
        dialogCancel.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSIONS_REQUEST_FRAGMENT_CAMERA -> if (grantResults.size > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                gotoCodeScan()
            } else if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                @SuppressLint("NewApi", "LocalSuppress") val showRationale =
                    shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)
                if (!showRationale) {

                    //set up setting dialog
                    Utils.callPermissionONMessagesDilog(
                      requireActivity(),
                        "Genotrix",
                        "Must on camera permission"
                    )
                } else {
                    requestCameraParmeission()
                }
            }
            else -> throw IllegalStateException("Unexpected value: $requestCode")
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_next -> {
                val getuserid = edtUserId!!.text.toString()
                if (!getuserid.isEmpty()) {
                    strPatinetId = getuserid
//                    ((DashboardActivity) getContext()).replaceFragment(fragment, GetClientIdFragment.class.getSimpleName());
                    hideKeyboard(context)
                    val fragment = RegentIdFragment(strtitle)
                    Utils.replaceFragment(requireActivity(),fragment,true,GetClientIdFragment::class.java.name)

                } else {
                    Toast.makeText(context, "Enter patient id", Toast.LENGTH_LONG).show()
                }
            }
            R.id.img_scan -> checkForCameraParmession()
        }
    }

    private fun checkForCameraParmession() {
        if (hasCameraPermission()) {
            gotoCodeScan()
        } else {
            requestCameraParmeission()
        }
    }

    private fun requestCameraParmeission() {
        ActivityCompat.requestPermissions(
            requireActivity(), arrayOf(Manifest.permission.CAMERA),
            PERMISSIONS_REQUEST_FRAGMENT_CAMERA
        )
    }

    private fun hasCameraPermission(): Boolean {
        return (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED)
    }

    private fun gotoCodeScan() {
        val fragment = PatientidScanFragment()
        //call to replce with obj image scann.
        Utils.replaceFragment(requireActivity(),fragment,true,GetClientIdFragment::class.java.name)

    }

    private fun setPatientId() {
        Log.wtf("patient setPatientId :", strPatinetId)
        if (strPatinetId != "") {
            Log.wtf("patient inside if :", strPatinetId)
            edtUserId!!.setText(strPatinetId)
        }
    }

    override fun onResume() {
        super.onResume()
        Log.wtf("patient id", strPatinetId)
        setPatientId()
    }

    companion object {
        var strPatinetId = ""
        const val PERMISSIONS_REQUEST_FRAGMENT_CAMERA = 1111
    }

    init {
        strtitle = title
    }
}