@file:Suppress("TYPE_INFERENCE_ONLY_INPUT_TYPES_WARNING")

package com.mystrica.util

import android.bluetooth.*
import android.content.Context
import android.util.Log
import com.mystrica.util.Constant.Companion.CCC_DESCRIPTOR_UUID
import com.mystrica.util.MyBleCallBackHelper.Companion.deviceGattMap
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue


object ConnectionManager  {

    private lateinit var context :Context

    private var mainServiceUUID : UUID?=null
    private var mystService: BluetoothGattService? = null
    private var testCommontCharacteristic: BluetoothGattCharacteristic? = null
    private var testresultommontCharacteristic: BluetoothGattCharacteristic? = null

    private lateinit var callback: MyBleCallBackHelper


    private var isIrLedWrite = false
    private var isCallibrateWrite = false
    private  val isPwmwrite = false
    private var writeStatus = false

//    private lateinit var device: BluetoothDevice


     val operationQueue = ConcurrentLinkedQueue<BleOperationType>()
     var pendingOperation: BleOperationType? = null

    private var connectDevice :BluetoothDevice ?= null

    private fun BluetoothDevice.isConnected() = deviceGattMap.containsKey(this)

    fun  getDevice(): BluetoothDevice {
        return connectDevice!!
    }

    fun getMyService(): BluetoothGattService? {

        if(callback!= null){
            callback.getMyService()
        }
        return  mystService
    }

    fun connect(device: BluetoothDevice, context: Context, mainServiceUUID: UUID) {
        this.context=context
        this.mainServiceUUID=mainServiceUUID
        if (device.isConnected()) {
            //todo already connect
        } else {
            callback=MyBleCallBackHelper(context, mainServiceUUID)
            enqueueOperation(Connect(device, context.applicationContext,mainServiceUUID))

          //  device.connectGatt(context, false, callback!!)
        }
    }


    @Synchronized
    private fun enqueueOperation(operation: BleOperationType) {
        operationQueue.add(operation)
        if (pendingOperation == null) {
            doNextOperation()
        }
//        else{
//            signalEndOfOperation()
//        }
    }

    @Synchronized
     fun signalEndOfOperation() {
        Log.wtf("end calling","End of $pendingOperation")
        Log.wtf("callback","pendingOperation onCharacteristicWrite:"+pendingOperation)

        pendingOperation = null
        if (operationQueue.isNotEmpty()) {
            doNextOperation()
        }
    }

    private fun doNextOperation() {
        if (pendingOperation != null) {
            Log.e("pending operation","doNextOperation() called when an operation is pending! Aborting.")
            return
        }

        val operation = operationQueue.poll() ?: run {
            Log.e("pending operation","Operation queue empty, returning")
            return
        }
        pendingOperation = operation


        if (operation is Connect) {
            with(operation) {
                connectDevice=device
//                Log.e("Connect device","Connecting to ${device.name}")
                device.connectGatt(context, false, callback)
            }
            pendingOperation= null
            return
        }

        val gatt = deviceGattMap[operation.device]
            ?: this@ConnectionManager.run {
                Log.wtf("gatt","Not connected to ${operation.device.address}! Aborting $operation operation.")
                signalEndOfOperation()
                return
            }

        when (operation) {
            is Disconnect -> with(operation) {
              Log.wtf("diconnect","Disconnecting from ${device.address}")
                gatt!!.close()
                deviceGattMap.remove(device)
                signalEndOfOperation()
            }
            is CharacteristicWrite -> with(operation) {
                gatt!!.findCharacteristic(characteristicUuid)?.let { characteristic ->
                    callback!!.writeCharacteristic(characteristicUuid,title,payload,false)
                } ?: this@ConnectionManager.run {
                    Log.wtf("write","Cannot find $characteristicUuid to write to, gatt :"+gatt)
                    signalEndOfOperation()
                }
            }
            is CharacteristicRead -> with(operation) {
                gatt!!.findCharacteristic(characteristicUuid)?.let { characteristic ->
                    callback!!.readCharteristic(characteristicUuid,title)
                } ?: this@ConnectionManager.run {
                    Log.wtf("read","Cannot find $characteristicUuid to read from")
                    signalEndOfOperation()
                }
            }
            is DescriptorWrite -> with(operation) {
                gatt!!.findDescriptor(descriptorUuid)?.let { descriptor ->
                    descriptor.value = payload
                    gatt!!.writeDescriptor(descriptor)
                } ?: this@ConnectionManager.run {
                    Log.wtf("writeDescripter","Cannot find $descriptorUuid to write to , gatt :"+gatt)
                    signalEndOfOperation()
                }
            }
            is DescriptorRead -> with(operation) {
                gatt!!.findDescriptor(descriptorUuid)?.let { descriptor ->
                    gatt!!.readDescriptor(descriptor)
                } ?: this@ConnectionManager.run {
                    Log.wtf("readDescripter","Cannot find $descriptorUuid to read from")
                    signalEndOfOperation()
                }
            }
            is EnableNotifications -> with(operation) {
                gatt!!.findCharacteristic(characteristicUuid)?.let { characteristic ->

                    callback!!.setEnableNotfication(characteristicUuid,tag)
                } ?: this@ConnectionManager.run {
                    Log.wtf("notification","Cannot find $characteristicUuid! Failed to enable notifications.")
                    signalEndOfOperation()
                }
            }
            is DisableNotifications -> with(operation) {


                gatt!!.findCharacteristic(characteristicUuid)?.let { characteristic ->

                    callback!!.setDisableNotificatin(characteristicUuid,tag)
                } ?: this@ConnectionManager.run {
                    Log.wtf("notification","Cannot find $characteristicUuid! Failed to disable notifications.")
                    signalEndOfOperation()
                }
            }

        }
    }

    fun readCharetistic(device: BluetoothDevice,readUUid : UUID,title : String){
        if(callback != null){
            enqueueOperation(CharacteristicRead(device, readUUid,title))
        }
    }
    fun writedCharetistic(device: BluetoothDevice,writeUuid: UUID, title: String,payload: ByteArray,isNotficationEnable : Boolean){
        if(callback!= null){

            enqueueOperation(CharacteristicWrite(device, writeUuid, 2, payload,title))
        }
    }

    fun setEnableNotification(device: BluetoothDevice, mysteriaCharacteristicResiltUuid: UUID?, tag: String) {
        if(callback!= null){

            enqueueOperation(EnableNotifications(device, mysteriaCharacteristicResiltUuid!!,tag))
        }
    }

    fun disconnect(){
        if(callback!= null){
            enqueueOperation(Disconnect(getDevice()))
        }
    }

    fun setDisableNotification(device: BluetoothDevice, mysteriaCharacteristicResiltUuid: UUID?, tag: String) {
        if(callback!= null){

            enqueueOperation(DisableNotifications(device, mysteriaCharacteristicResiltUuid!!,tag))
        }
    }

}
