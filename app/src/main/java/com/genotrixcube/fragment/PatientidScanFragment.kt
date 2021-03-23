package com.genotrixcube.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.genotrixcube.R

class PatientidScanFragment : Fragment() {
    private var rootview: View? = null
    private var imgCamera: ImageView? = null
    private var mCodeScanner: CodeScanner? = null
    private var scannerView: CodeScannerView? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootview = inflater.inflate(R.layout.activity_code, container, false)
        scannerView = rootview!!.findViewById(R.id.scanner_view)
        mCodeScanner = CodeScanner(requireContext(), scannerView!!)
        imgCamera = rootview!!.findViewById(R.id.img_cange_camera)
        mCodeScanner!!.camera = CodeScanner.CAMERA_FRONT
        mCodeScanner!!.decodeCallback = DecodeCallback { result ->
            requireActivity().runOnUiThread {
                GetClientIdFragment.strPatinetId = result.text
                val fm =
                    requireActivity().supportFragmentManager

                fm.popBackStack()
            }
        }
        scannerView!!.setOnClickListener(View.OnClickListener { mCodeScanner!!.startPreview() })
        imgCamera!!.setOnClickListener(View.OnClickListener {
            val currentvalue = mCodeScanner!!.camera
            if (currentvalue == 1) {
                mCodeScanner!!.camera = 0
            } else {
                mCodeScanner!!.camera = 1
            }
        })
        return rootview
    }

    override fun onResume() {
        super.onResume()
        mCodeScanner!!.startPreview()
    }

    override fun onPause() {
        mCodeScanner!!.releaseResources()
        super.onPause()
    }
}