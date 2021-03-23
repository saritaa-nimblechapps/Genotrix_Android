package com.genotrixcube.fragment

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.genotrixcube.R
import com.genotrixcube.activity.CoverActivity
import com.genotrixcube.activity.CoverActivity.Companion.strUserId
import com.genotrixcube.activity.DashboardActivity
import com.genotrixcube.util.Constant
import com.genotrixcube.util.Utils
import org.jetbrains.anko.support.v4.act

class SettingFragmnet(private val strHeaderTitle: String) : Fragment(),
    View.OnClickListener {
    private var rootview: View? = null
    private var txtUserid: TextView? = null
    private var txtChangeUserid: TextView? = null
    private var txtChangeTime: TextView? = null
    private var txtSecond: TextView? = null
    private var txtMintes: TextView? = null
    private var txtDrops : TextView ?= null
    private var txtReaction : TextView ?= null
    private var lyAddSecond: LinearLayout? = null
    private var lyminsSecond: LinearLayout? = null
    private var lyAddMintes: LinearLayout? = null
    private var lyMinsMintes: LinearLayout? = null
    private var lyDropsMins :LinearLayout ?= null
    private var lyDropsAdd : LinearLayout ?= null
    private var lyReactionAdd : LinearLayout ?= null
    private var lyReactionMins : LinearLayout ?= null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootview = inflater.inflate(R.layout.fragment_setting_fragmnet, container, false)

        (activity as DashboardActivity?)!!.HeadetTitle(strHeaderTitle)
        (activity as DashboardActivity)!!.visibleBackButton()
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
        txtDrops=rootview!!.findViewById(R.id.txt_drops_update)
        txtReaction=rootview!!.findViewById(R.id.txt_reaction_update)

        lyAddSecond = rootview!!.findViewById(R.id.ly_add_second)
        lyminsSecond = rootview!!.findViewById(R.id.ly_sec_mins)
        lyAddMintes = rootview!!.findViewById(R.id.ly_add_mintes)
        lyMinsMintes = rootview!!.findViewById(R.id.ly_add_mins)
        lyDropsMins=rootview!!.findViewById(R.id.ly_drops_mins)
        lyDropsAdd=rootview!!.findViewById(R.id.ly_add_drops)
        lyReactionMins=rootview!!.findViewById(R.id.ly_recation_mins)
        lyReactionAdd=rootview!!.findViewById(R.id.ly_add_reaction)

        setCurrentUserid(strUserId)

        pref_mint_inteval

        lyAddMintes!!.setOnClickListener(this)
        lyminsSecond!!.setOnClickListener(this)
        lyAddSecond!!.setOnClickListener(this)
        lyMinsMintes!!.setOnClickListener(this)
        lyDropsAdd!!.setOnClickListener(this)
        lyDropsMins!!.setOnClickListener(this)
        lyReactionAdd!!.setOnClickListener(this)
        lyReactionMins!!.setOnClickListener(this)
        txtChangeUserid!!.setOnClickListener(this)
        txtChangeTime!!.setOnClickListener(this)

    }

    private val pref_mint_inteval: Unit
        private get() {
            val getMiliSecond = Utils!!.pref_get_long(
                Constant.qcMaxmiliSecond,
                requireActivity(),
                Constant.maxDefaultMinit
            )
            val getinterval = Utils!!.pref_get_long(
                Constant.qcintervalSecond,
                requireActivity(),
                Constant.maxDefalultInterval
            )
            val getMint = Utils!!.miliSecondToMinites(getMiliSecond)
            val interval = Utils!!.miliSecondToSecond(getinterval)

            val getDropsVaule=Utils.pref_get_int_default(Constant.drops,activity!!,Constant.dropsDefault)
            val getEstimateFlage=Utils.pref_get_int_default(Constant.estimateFlag,activity!!,Constant.estimatedFlag)


            Log.wtf("timerCounter","Counter timer :+"+getMint + " ,inteval :"+interval + " ,drops :"+getDropsVaule+", flag :"+getEstimateFlage)

            setMint_inteval_txt(getMint, interval,getDropsVaule.toInt(),getEstimateFlage.toInt())
        }

    private fun setMint_inteval_txt(
        minites: Int,
        interval: Int,
        drops: Int,
        estimatedflag: Int
    ) {
        setMinites(minites)
        second = interval
        setDrops(drops)
        setReaction(estimatedflag)
    }

    private fun setCurrentUserid(id: String) {
        txtUserid!!.text = id
    }

    private fun setMinites(minites: Int) {
        txtMintes!!.text = minites.toString()

        val getMintes = txtMintes!!.text.toString()
        val m = getMintes.toInt()
        val millis = m * 60 * 1000.toLong()
        val milisecondset = millis + Constant.addExtraIntrval

        Utils!!.pref_set_long(Constant.qcMaxmiliSecond, milisecondset, requireActivity())
        Utils!!.pref_set_String(Constant.maxMint,getMintes,requireContext())

        (activity as DashboardActivity?)!!.countDownTimet_int()

    }

    private fun setDrops(drops: Int) {
        txtDrops!!.text = drops.toString()
        Utils!!.pref_set_int(Constant.drops,drops,requireContext())

    }

    private fun setReaction(reaction: Int) {
        txtReaction!!.text = reaction.toString()

        val getLimitflag = txtReaction!!.text.toString()
        val l = getLimitflag.toInt()
        val limitSecond = l * 60


        Utils!!.pref_set_int(Constant.limitMilisecond, limitSecond, requireActivity())
        Utils!!.pref_set_int(Constant.estimateFlag,l,requireContext())
        Utils!!.pref_set_int(Constant.reaction,reaction,requireContext())
    }

    private var second: Int
        private get() = txtSecond!!.text.toString().toInt()
        private set(second) {
            txtSecond!!.text = second.toString()

            val getInterval = txtSecond!!.text.toString()
            val s = getInterval.toInt()
            val sec = s * 1000.toLong()


            Utils!!.pref_set_int(Constant.testinterval,s,requireContext())
            Utils!!.pref_set_long(Constant.qcintervalSecond, sec, requireContext())

            (activity as DashboardActivity?)!!.countDownTimet_int()
        }

    private val mintes: Int
        private get() = txtMintes!!.text.toString().toInt()


    private val drops: Int
        private get() = txtDrops!!.text.toString().toInt()

    private val reaction: Int
        private get() = txtReaction!!.text.toString().toInt()

    override fun onClick(view: View) {
        when (view.id) {
            R.id.ly_add_second -> callAddSecond()
            R.id.ly_sec_mins -> callMinesSecond()
            R.id.ly_add_mintes -> callAddMintes()
            R.id.ly_add_mins -> callMinesMinites()
            R.id. ly_add_drops->callAddDrops()
            R.id. ly_drops_mins->callMinesDrops()
            R.id.ly_recation_mins-> callMinesReaction()
            R.id.ly_add_reaction->callReactionadd()
            R.id.txt_change_userid -> callChangeUserId()

        }
    }

    //use with when submit button click and set all details
    private fun callChangeTime() {
        val getMintes = txtMintes!!.text.toString()
        val getInterval = txtSecond!!.text.toString()
        val getLimitflag = txtReaction!!.text.toString()

        val m = getMintes.toInt()
        val l = getLimitflag.toInt()
        val s = getInterval.toInt()
        val millis = m * 60 * 1000.toLong()
        val limitsmillis = l * 1000.toLong()
        val limitSecond = l * 60
        val sec = s * 1000.toLong()


//        val milisecondset = millis + 24000
        val milisecondset = millis + Constant.addExtraIntrval

        Utils!!.pref_set_long(Constant.qcMaxmiliSecond, milisecondset, requireActivity())
        Utils!!.pref_set_String(Constant.maxMint,getMintes,requireContext())

        Utils!!.pref_set_int(Constant.limitMilisecond, limitSecond, requireActivity())
        Utils!!.pref_set_int(Constant.estimateFlag,l,requireContext())
        Utils!!.pref_set_int(Constant.reaction,reaction,requireContext())

        Utils!!.pref_set_int(Constant.drops,drops,requireContext())

        Utils!!.pref_set_int(Constant.testinterval,s,requireContext())
        Utils!!.pref_set_long(Constant.qcintervalSecond, sec, requireContext())

        (activity as DashboardActivity?)!!.countDownTimet_int()
        requireActivity().supportFragmentManager.popBackStack()

//        val estimate=l * 60
//        Log.wtf("limitline","Drow interval limit line :"+estimate)
    }

    private fun callChangeUserId() {
        val dashboard = Intent(activity, CoverActivity::class.java)
        dashboard.putExtra("come", "settingFragment")
        startActivity(dashboard)
    }

    private fun callMinesMinites() {
        val getCurrenMintes = mintes
        var minsMinites : Int=0
        if (getCurrenMintes == 1) {
            requiredDialog("Test time duration should be minimum 1 minute.")
        } else {
             minsMinites = getCurrenMintes - 1
            setMinites(minsMinites)

            if(reaction>mintes){
                setReaction(1)
            }
        }
    }

    private fun callMinesDrops() {
        val getCurrentDrops = drops
        if (getCurrentDrops == 3) {
           // requiredDialog("3 drops required ")
        } else {

            val drops = getCurrentDrops - 1
            if(drops <3){
               // requiredDialog("3 drops required ")
            }else{
                setDrops(drops)
            }
        }
    }

    private fun callMinesReaction() {
        val getCurrentReaction = reaction
        if (getCurrentReaction ==1) {
            requiredDialog("Reaction duration should be minimum 1 minute.")
        } else {

            val reaction = getCurrentReaction - 1
            if(reaction < mintes){
                setReaction(reaction)
            }
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

        if(reaction<mintes){

        }else{
            setReaction(1)
        }
    }

    private fun callAddDrops() {
        val getCurrentDrops = drops
        val addDrops = getCurrentDrops + 1
        setDrops(addDrops)
    }

    private fun callAddSecond() {
        val getCurrentSecond = second
        val addSecond = getCurrentSecond + 5
        second = addSecond


    }

    private fun callReactionadd() {
        val getCurrentReaction = reaction
        val addReaction = getCurrentReaction + 1

        if(addReaction>mintes){
          //  requiredDialog("reaction < mints")
        }else{
           setReaction(addReaction)
        }
    }

    private fun requiredDialog(message: String) {
        //title : Minimum Value selection
        AlertDialog.Builder(context)
            .setTitle("Minimum Value selection")
            .setMessage(message)
            .setPositiveButton("Ok") { dialogInterface, i -> dialogInterface.cancel() }
            .show()
    }

}