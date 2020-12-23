package com.mystrica.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.mystrica.R
import com.mystrica.activity.DashboardActivity
import com.mystrica.fragment.GetClientIdFragment.Companion.strPatinetId
import com.mystrica.fragment.HomeFragment
import com.mystrica.fragment.RegentIdFragment.Companion.strRegentId
import com.mystrica.util.ConnectionManager
import com.mystrica.util.Constant
import com.mystrica.util.Utils
import com.mystrica.util.Utils.TodayDate

class SendEmailFragment(private val strHeaderTitle: String) :
    Fragment() {
    private var rootview: View? = null
    private var txtTitle: TextView? = null
    private var txtSendEmail: TextView? = null
    private val edtEamil: AppCompatEditText? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootview = inflater.inflate(R.layout.fragment_send_email, container, false)
        (activity as DashboardActivity?)!!.HeadetTitle(strHeaderTitle)
        (activity as DashboardActivity?)!!.visibleBackButton()
        (activity as DashboardActivity?)!!.visibleHomeButton()
        setupViews()
        val today = TodayDate()
        txtTitle!!.text = "Covid-19-" + strPatinetId.toString() + "-" + today
        txtSendEmail!!.setOnClickListener { // sendEmailButtonClick();
            (activity as DashboardActivity?)!!.callHomeDashboard()
        }
        return rootview
    }

    private fun setupViews() {
        txtTitle = rootview!!.findViewById(R.id.txt_email_tilte)
        txtSendEmail = rootview!!.findViewById(R.id.txt_send_email)
    }

    private fun sendHomePage() {
        val fm = requireActivity().supportFragmentManager
        fm.popBackStack(
            HomeFragment::class.java.simpleName,
            FragmentManager.POP_BACK_STACK_INCLUSIVE
        )
    }

    private fun sendEmailButtonClick() {
        DashboardActivity.firstResult = false
        DashboardActivity.firstResultValue = ""
        strPatinetId = ""
        strRegentId = ""
        Constant.selectTest = ""
        Constant.selectSubTest = ""
        DashboardActivity.currentValue4Yaxis = 0.1f
        DashboardActivity.countDownTimer!!.cancel()
        val fm = requireActivity().supportFragmentManager
        for (i in 0 until fm.backStackEntryCount) {
            fm.popBackStack()
        }

        //set for connect blue

//        Log.i("home","go to connect page");
        ConnectionManager.disconnect()

        Utils.replaceFragment(requireActivity(),ConnectBluetothFragment.newInstance("Connect Device"),false,SendEmailFragment::class.java.name)
    }

}