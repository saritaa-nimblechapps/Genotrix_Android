package com.mystrica.util

import android.bluetooth.*
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.mystrica.activity.DashboardActivity
import com.mystrica.util.ConnectionManager.pendingOperation
import com.mystrica.util.ConnectionManager.signalEndOfOperation
import com.mystrica.util.Constant.Companion.CCC_DESCRIPTOR_UUID
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class MyBleCallBackHelper(
   private var context: Context,
   private var mainServiceUUID: UUID
) : BluetoothGattCallback(){


    private lateinit var gatt : BluetoothGatt
    private lateinit var device: BluetoothDevice

    private lateinit var mystService : BluetoothGattService
    private var assignChartValue: BluetoothGattCharacteristic? = null
    private var readAssingCharacteristic: BluetoothGattCharacteristic? = null
    private var listner : ConnetingServicHelper ?= null

    companion object{
        val deviceGattMap = ConcurrentHashMap<BluetoothDevice, BluetoothGatt>()
    }






    //int
    val characteristics by lazy {
        servicesOnDevice(device)?.flatMap { service ->
            service.characteristics ?: listOf()
        } ?: listOf()
    }

    val characteristicProperties by lazy {
        characteristics!!.map { characteristic ->
            characteristic to mutableListOf<CharacteristicProperty>().apply {
                if (characteristic.isIndicatable()) add(CharacteristicProperty.Indicatable)
                if (characteristic.isReadable()) add(CharacteristicProperty.Readable)
                if (characteristic.isWritable()) add(CharacteristicProperty.Writable)
                if(characteristic.isNotifiable()) add(CharacteristicProperty.Notifiable)
                if (characteristic.isWritableWithoutResponse()) {
                    add(CharacteristicProperty.WritableWithoutResponse)
                }
            }.toList()
        }.toMap()
    }


    //notification Enable
    fun servicesOnDevice(device: BluetoothDevice): List<BluetoothGattService>? = deviceGattMap[device]?.services

    fun BluetoothDevice.isConnected() = deviceGattMap.containsKey(this)


    override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
        super.onConnectionStateChange(gatt, status, newState)
        val deviceAddress = gatt!!.device.address

            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    this.gatt = gatt
                    device=gatt.device
                    deviceGattMap[gatt.device] = gatt
                    Log.wtf("device :", device.name.toString())

                    BrodcastDeviceName(device.name,context!!)
                    Handler(Looper.getMainLooper()).post {
                        gatt.discoverServices()
                    }
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {

                    //brodcast device is disconnect
                }

            }
            else{
                if (pendingOperation is Connect) {
                    signalEndOfOperation()
                }
            }
    }

    override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
        super.onServicesDiscovered(gatt, status)

        if (status == BluetoothGatt.GATT_SUCCESS) {

            mystService = gatt?.getService(mainServiceUUID)!!

        }

    }

    override fun onCharacteristicChanged(
        gatt: BluetoothGatt?,
        characteristic: BluetoothGattCharacteristic?
    ) {
        super.onCharacteristicChanged(gatt, characteristic)

        if (characteristic != null) {

            val getCurrentCharteristic=characteristic

            val isNotifiable=checkingPropertySet(getCurrentCharteristic, "enablenotfication", CharacteristicProperty.Notifiable)
            val isReadable=checkingPropertySet(getCurrentCharteristic, "enablenotfication", CharacteristicProperty.Readable)

            if(isNotifiable){
                readCharteristic(getCurrentCharteristic.uuid,"readchaterisctic")
            }
            else{

            }

            if(isReadable){
                readCharteristic(getCurrentCharteristic.uuid,"readchaterisctic")
            }
        }
    }

    override fun onDescriptorWrite(
        gatt: BluetoothGatt?,
        descriptor: BluetoothGattDescriptor?,
        status: Int
    ) {
//        with(descriptor){
            when(status){
                BluetoothGatt.GATT_SUCCESS->{

                    Log.wtf("write","Write Descriptor scuccss")

                }
                BluetoothGatt.GATT_WRITE_NOT_PERMITTED -> {
                    Log.wtf("write","Write Descriptor not permitted")

                }
                else -> {
                    Log.wtf("write","Write Descriptor failer")

                }
            }
//        }

        if(pendingOperation is EnableNotifications){
            signalEndOfOperation()
        }
    }

    override fun onDescriptorRead(
        gatt: BluetoothGatt?,
        descriptor: BluetoothGattDescriptor?,
        status: Int
    ) {
        super.onDescriptorRead(gatt, descriptor, status)
        with(descriptor){
            when(status){
                BluetoothGatt.GATT_SUCCESS->{

                    Log.wtf("write","read Descriptor scuccss")
                }
                BluetoothGatt.GATT_WRITE_NOT_PERMITTED -> {
                    Log.wtf("write","read Descriptor not permitted")

                }
                else -> {
                    Log.wtf("write","read Descriptor failer")

                }
            }
        }

        if (pendingOperation is DescriptorRead) {
            signalEndOfOperation()
        }
    }


    override fun onCharacteristicWrite(
        gatt: BluetoothGatt?,
        characteristic: BluetoothGattCharacteristic?,
        status: Int
    ) {
       // super.onCharacteristicWrite(gatt, characteristic, status)

        if(status==BluetoothGatt.GATT_SUCCESS){
            Log.wtf("callback","success fully write :"+characteristic!!.uuid.toString())

        }
        else if(status==BluetoothGatt.GATT_FAILURE){
            Log.wtf("callback","failer write :"+characteristic!!.uuid.toString())
        }
        if (pendingOperation is CharacteristicWrite) {
            Log.wtf("callback","pendingOperation onCharacteristicWrite:"+characteristic!!.uuid.toString())

            signalEndOfOperation()
        }
    }

    override fun onCharacteristicRead(
        gatt: BluetoothGatt?,
        characteristic: BluetoothGattCharacteristic?,
        status: Int
    ) {
        super.onCharacteristicRead(gatt, characteristic, status)

        with(characteristic){
            when(status){
                BluetoothGatt.GATT_SUCCESS->{

                    Log.wtf("read","onCharacteristicRead scuccss :"+  Arrays.toString(characteristic!!.value))

                    setUpBrodcast(characteristic!!.value,context)
                }
                BluetoothGatt.GATT_WRITE_NOT_PERMITTED -> {
                    Log.wtf("read","onCharacteristicRead not permitted")

                }
                else -> {
                    Log.wtf("read","onCharacteristicRead failer")

                }
            }
        }

        if (pendingOperation is CharacteristicRead) {
            signalEndOfOperation()
        }
    }


    private fun checkingPropertySet(
        readAssingCharacteristic: BluetoothGattCharacteristic?,
        tag: String,
        notifiable: CharacteristicProperty
    ) :Boolean{
        //set value for notfication
        val properties= characteristicProperties[readAssingCharacteristic]

        if(properties!= null){
            try {
                properties.first()

                for(pro in 0 until properties.size step 1 )
                {
                    //CharacteristicProperty.Notifiable
                    if( notifiable.equals(properties.get(pro)))
                    {
                        return true
                    }
                }
            }   catch (e:Exception){
                //   e.printStackTrace()
            }
        }
        return false
    }


    fun writeCharacteristic(writeuuid: UUID, title: String,payload: ByteArray,isNotifcationEnable : Boolean) {

        //checking queue for assign char. (if not assign than assign)
        //assign service to the charet
        assignChartValue=mystService.getCharacteristic(writeuuid)
        Log.wtf("mystService","mystService write :"+title + " mystService of response "+ mystService + " , assignChartValue : "+ assignChartValue)


        if(assignChartValue!= null){
            val writeType = when {
                assignChartValue!!.isWritable() -> BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
                assignChartValue!!.isWritableWithoutResponse() -> {
                    BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE
                }

                else -> error("Characteristic ${assignChartValue!!.uuid} cannot be written to"+"Name of char :"+title)
            }

            gatt!!?.let { gatt ->
//            characteristic.writeType = writeType
                assignChartValue!!.value = payload
                if(gatt.writeCharacteristic(assignChartValue)){
                    Log.wtf("char","char write :"+title + " type of response "+ writeType)
                }
                else{
                    Log.wtf("char","char write :"+title + " type of response "+ writeType)

                }

            }


        }
    }

    fun readCharteristic(readUUid : UUID,title : String){
        val assignChartValue=mystService.getCharacteristic(readUUid)

        var boolean=checkingPropertySet(assignChartValue,title,CharacteristicProperty.Readable)
        if(assignChartValue!= null){

            if(boolean){
                gatt!!?.let { gatt ->
//            characteristic.writeType = writeType

                    if(gatt.readCharacteristic(assignChartValue)){
//                        Log.wtf("char","char read :"+title + " type of response ")
                    }
                    else{
                        Log.wtf("char","char read :"+title + " type of response ")
                    }

                }
            }
        }
    }

    fun setEnableNotfication(mysteriaCharacteristicResiltUuid: UUID?, tag: String) {
       val readAssingCharacteristic=mystService.getCharacteristic(mysteriaCharacteristicResiltUuid)
        val isNotifiable=checkingPropertySet(readAssingCharacteristic, tag, CharacteristicProperty.Notifiable)
        if(isNotifiable){
            enableNotification(readAssingCharacteristic!!,tag)
        }

    }

    fun setDisableNotificatin(characteristicUuid: UUID, tag: String) {
        val readAssingCharacteristic=mystService.getCharacteristic(characteristicUuid)

        val isNotifiable=checkingPropertySet(readAssingCharacteristic, tag, CharacteristicProperty.Notifiable)
        if(isNotifiable){
            disableNotification(readAssingCharacteristic!!,tag)
        }
    }

    private fun disableNotification(assignChartValue: BluetoothGattCharacteristic, tag: String) {
        try{
            assignChartValue.let { characteristic->

                val cccdUuid = UUID.fromString(CCC_DESCRIPTOR_UUID)
                characteristic.getDescriptor(cccdUuid)?.let { cccDescriptor ->
                    if (!gatt.setCharacteristicNotification(characteristic, false)) {
                        signalEndOfOperation()
                        return
                    }

                    cccDescriptor.value = BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE
                    gatt.writeDescriptor(cccDescriptor)
                }

            }
        }catch (e: Exception){
            e.printStackTrace()
        }

    }


    private fun enableNotification(
        assignChartValue: BluetoothGattCharacteristic,
        tag: String
    ) {

        try{
            assignChartValue.let { characteristic->

                val cccdUuid = UUID.fromString(Constant.CCC_DESCRIPTOR_UUID)

                val payload = when {

                    characteristic.isNotifiable() ->
                        BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                    else ->
                        error("${characteristic.uuid} doesn't support notifications")
                }

                characteristic.getDescriptor(cccdUuid)?.let { cccDescriptor ->
                    if (!gatt!!.setCharacteristicNotification(assignChartValue, true)) {
                        return
                    }
                    cccDescriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                    if( gatt!!.writeDescriptor(cccDescriptor)){
                        Log.wtf("write","Descripter write "+characteristic.uuid.toString()+ " set on :"+tag)
                    }

                }

            }
        }catch (e: Exception){
            e.printStackTrace()
        }

    }


    fun getMyService(): BluetoothGattService {
       return mystService!!
    }

    fun disconnect(){
        if(gatt!= null){
            gatt.disconnect()
        }
    }







    //brodcast value
    fun BrodcastDeviceName(string: String, context: Context) {
        Log.e("device :", string.toString())

        try {
            if(device != null)
            {
                val intent = Intent(DashboardActivity.intentAction)
                intent.putExtra("deviceNm", string)
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
            }

        } catch (e: Exception) {
            Log.d("Brodcast error :", e.toString())
        }
    }

    private fun setUpBrodcast( arrar: ByteArray,context: Context) {
        try {
            val intent = Intent(DashboardActivity.intentAction)
            intent.putExtra("bytedata", arrar)
            intent.putExtra("data", Arrays.toString(arrar))
            intent.putExtra("code", "000")
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
        } catch (e: java.lang.Exception) {
            Log.d("Brodcast error :", e.toString())
        }
    }



}

