package com.mystrica.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.View.OnTouchListener
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.mystrica.R
import com.mystrica.activity.DashboardActivity
import com.mystrica.util.Constant
import com.mystrica.util.Utils
import com.mystrica.util.Utils.callPermissionONMessagesDilog
import com.mystrica.util.Utils.pref_set_String
import java.io.File
import java.util.*

class CoverActivity : AppCompatActivity(), View.OnClickListener {
    private var btnNext: Button? = null
    private var txtTitle: TextView? = null
    private var txtScanTitle: TextView? = null
    private var txt_id: TextView? = null
    private var imgScan: ImageView? = null
    private var edtUserId: AppCompatEditText? = null
    private var container: ConstraintLayout? = null
    private val mCodeScanner: CodeScanner? = null
    var scannerView: CodeScannerView? = null
    private var comingFragmentName: String? = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cover)
        setupViews()
        intentData
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupViews() {
        imgScan = findViewById(R.id.img_scan)
        container = findViewById(R.id.cover_container)
        btnNext = findViewById(R.id.btn_next)
        txtTitle = findViewById(R.id.txt_covid_title)
        txt_id = findViewById(R.id.txt_id_title)
        txtScanTitle = findViewById(R.id.txt_scan_title)
        edtUserId = findViewById(R.id.edt_user_id)

        //visiblity
        txtTitle!!.setVisibility(View.GONE)
        txtScanTitle!!.setText(applicationContext.getString(R.string.scan_user_id))
        txt_id!!.setText(applicationContext.getString(R.string.enter_user_id))
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
                val id = edtUserId!!.getText().toString()
                if (!id.isEmpty()) {
                    btnNext!!.setEnabled(true)
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
        container!!.setOnTouchListener(OnTouchListener { view, motionEvent ->
            Utils.hideKeyboard(applicationContext)
            true
        })
        btnNext!!.setOnClickListener(this)
        imgScan!!.setOnClickListener(this)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        Log.wtf("barcode", "code new intent scan")
        edtUserId!!.setText(strUserId)
    }

    private val intentData: Unit
        private get() {
            Log.wtf("barcode", "code get intent scan")
            edtUserId!!.setText(strUserId)

            val extras = intent.extras
            if (extras != null) {
                edtUserId!!.setText(strUserId)
                if (extras.containsKey("")) {
                    edtUserId!!.setText(strUserId)
                    comingFragmentName = extras.getString("")
                }
            }
        }

    //permission methods
    private fun checkForCameraParmession() {
        if (hasCameraPermission()) {
            gotoCodeScan()
        } else {
            requestCameraParmeission()
        }
    }

    private fun requestCameraParmeission() {
        ActivityCompat.requestPermissions(
            this, arrayOf(Manifest.permission.CAMERA),
            PERMISSIONS_REQUEST_CAMERA
        )
    }

    private fun hasCameraPermission(): Boolean {
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        when (requestCode) {
            PERMISSIONS_REQUEST_CAMERA -> if (grantResults.size > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                gotoCodeScan()
            } else if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                @SuppressLint("NewApi", "LocalSuppress") val showRationale =
                    shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)
                if (!showRationale) {

                    //set up setting dialog
                    callPermissionONMessagesDilog(
                        this,
                        "Mystrica",
                        "Must on camera permission"
                    )
                } else {
                    requestCameraParmeission()
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_next ->
                // bitSetLed();
                callNextDashboard()
            R.id.img_scan -> checkForCameraParmession()
        }
    }



    private fun callNextDashboard() {
        val userid = edtUserId!!.text.toString()
        if (!userid.isEmpty()) {
            if (comingFragmentName == "") {
                pref_set_String(
                    Constant.userprefid,
                    userid,
                    applicationContext
                )
                strUserId = userid
                pref_set_String(
                    Constant.userprefid,
                    userid,
                    applicationContext
                )
                clearAllflag()
                gotoDashboard()
            } else {
                pref_set_String(
                    Constant.userprefid,
                    userid,
                    applicationContext
                )
                strUserId = userid
                clearAllflag()
                gotoDashboard()
                //                onBackPressed();
            }
        } else {
            Toast.makeText(applicationContext, "Enter user id", Toast.LENGTH_LONG).show()
        }
    }

    private fun clearAllflag() {

//        DashboardActivity.firstResult=false;
//        DashboardActivity.firstResultValue = "";
//
//        strPatinetId = "";
//        strUserId = "";
//        strRegentId = "";
//        selectTest = "";
//        selectSubTest = "";
    }

    private fun gotoDashboard() {
        val dashboard = Intent(this, DashboardActivity::class.java)
        //                    dashboard.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    dashboard.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(dashboard)
    }

    private fun gotoCodeScan() {
        val dashboard = Intent(this, CodeActivity::class.java)
        startActivity(dashboard)
    }

    companion object {
        @kotlin.jvm.JvmField
        var strUserId: String=""
        private const val PERMISSIONS_REQUEST_CAMERA = 111
    }
}