package com.mystrica.fragment

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.mystrica.R
import com.mystrica.activity.CoverActivity
import com.mystrica.activity.CoverActivity.Companion.strUserId
import com.mystrica.activity.DashboardActivity
import com.mystrica.util.Constant
import com.mystrica.util.Utils

class SettingFragmnet(private val strHeaderTitle: String) : Fragment(),
    View.OnClickListener {
    private var rootview: View? = null
    private var txtUserid: TextView? = null
    private var txtChangeUserid: TextView? = null
    private var txtChangeTime: TextView? = null
    private var txtSecond: TextView? = null
    private var txtMintes: TextView? = null
    private var lyAddSecond: LinearLayout? = null
    private var lyminsSecond: LinearLayout? = null
    private var lyAddMintes: LinearLayout? = null
    private var lyMinsMintes: LinearLayout? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootview = inflater.inflate(R.layout.fragment_setting_fragmnet, container, false)
        (activity as DashboardActivity?)!!.HeadetTitle(strHeaderTitle)
        setViews()
        return rootview
    }

    override fun onResume() {
        super.onResume()
        setCurrentUserid(strUserId)
    }

    private fun setViews() {
        txtUserid = rootview!!.findViewById(R.id.txt_current_user_id4setting)
        txtChangeUserid = rootview!!.findViewById(R.id.txt_change_userid)
        txtChangeTime = rootview!!.findViewById(R.id.txt_change_interval)
        txtSecond = rootview!!.findViewById(R.id.txt_sec_update)
        txtMintes = rootview!!.findViewById(R.id.txt_minites_updates)
        lyAddSecond = rootview!!.findViewById(R.id.ly_add_second)
        lyminsSecond = rootview!!.findViewById(R.id.ly_sec_mins)
        lyAddMintes = rootview!!.findViewById(R.id.ly_add_mintes)
        lyMinsMintes = rootview!!.findViewById(R.id.ly_add_mins)
        setCurrentUserid(strUserId)
        pref_mint_inteval
        lyAddMintes!!.setOnClickListener(this)
        lyminsSecond!!.setOnClickListener(this)
        lyAddSecond!!.setOnClickListener(this)
        lyMinsMintes!!.setOnClickListener(this)
        txtChangeUserid!!.setOnClickListener(this)
        txtChangeTime!!.setOnClickListener(this)
    }

    private val pref_mint_inteval: Unit
        private get() {
            val getMiliSecond = Utils!!.pref_get_long(
                Constant.qcMaxmiliSecond,
                activity!!,
                Constant.maxDefaultMinit
            )
            val getinterval = Utils!!.pref_get_long(
                Constant.qcintervalSecond,
                activity!!,
                Constant.maxDefalultInterval
            )
            val getMint = Utils!!.miliSecondToMinites(getMiliSecond)
            val interval = Utils!!.miliSecondToSecond(getinterval)
            setMint_inteval_txt(getMint, interval)
        }

    private fun setMint_inteval_txt(minites: Int, interval: Int) {
        setMinites(minites)
        second = interval
    }

    private fun setCurrentUserid(id: String) {
        txtUserid!!.text = id
    }

    private fun setMinites(minites: Int) {
        txtMintes!!.text = minites.toString()
    }

    private var second: Int
        private get() = txtSecond!!.text.toString().toInt()
        private set(second) {
            txtSecond!!.text = second.toString()
        }

    private val mintes: Int
        private get() = txtMintes!!.text.toString().toInt()

    override fun onClick(view: View) {
        when (view.id) {
            R.id.ly_add_second -> callAddSecond()
            R.id.ly_sec_mins -> callMinesSecond()
            R.id.ly_add_mintes -> callAddMintes()
            R.id.ly_add_mins -> callMinesMinites()
            R.id.txt_change_userid -> callChangeUserId()
            R.id.txt_change_interval -> callChangeTime()
        }
    }

    private fun callChangeTime() {
        val getMintes = txtMintes!!.text.toString()
        val getInterval = txtSecond!!.text.toString()
        val m = getMintes.toInt()
        val s = getInterval.toInt()
        val millis = m * 60 * 1000.toLong()
        val sec = s * 1000.toLong()

//        Log.i("minites","milil :"+millis+" Seconds :"+sec);

        //  Toast.makeText(getActivity(),"milil :"+millis+" Seconds :"+sec,Toast.LENGTH_LONG).show();
        val milisecondset = millis + 24000
        Utils!!.pref_set_long(
            Constant.qcMaxmiliSecond,
            milisecondset,
           requireActivity()
        )
        Utils!!.pref_set_int(Constant.testinterval,s,requireContext())
        Utils!!.pref_set_long(Constant.qcintervalSecond, sec, requireContext())
        Utils!!.pref_set_int(Constant.testFlag, 1, requireContext())
        (activity as DashboardActivity?)!!.countDownTimet_int()
        requireActivity().supportFragmentManager.popBackStack()
    }

    private fun callChangeUserId() {
        val dashboard = Intent(activity, CoverActivity::class.java)
        dashboard.putExtra("come", "settingFragment")
        startActivity(dashboard)
    }

    private fun callMinesMinites() {
        val getCurrenMintes = mintes
        if (getCurrenMintes == 1) {
            requiredDialog("Test time duration should be minimum 1 minute.")
        } else {
            val minsMinites = getCurrenMintes - 1
            setMinites(minsMinites)
        }
    }

    private fun callMinesSecond() {
        val getCurrentSecond = second
        if (getCurrentSecond == 5) {
            requiredDialog("Test interval should be minimum 5 seconds.")
        } else {
            val minsSecond = getCurrentSecond - 5
            second = minsSecond
        }
    }

    private fun callAddMintes() {
        val getCurrentMintes = mintes
        val addMintes = getCurrentMintes + 1
        setMinites(addMintes)
    }

    private fun callAddSecond() {
        val getCurrentSecond = second
        val addSecond = getCurrentSecond + 5
        second = addSecond
    }

    private fun requiredDialog(message: String) {
        //title : Minimum Value selection
        //message:  Test time should be minimum 1 mintes/5 second
        AlertDialog.Builder(context)
            .setTitle("Minimum Value selection")
            .setMessage(message)
            .setPositiveButton("Ok") { dialogInterface, i -> dialogInterface.cancel() }
            .show()
    }

}