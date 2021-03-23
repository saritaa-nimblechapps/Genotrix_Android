package com.genotrixcube.util

import android.bluetooth.BluetoothDevice
import android.content.Context
import java.util.*

sealed class BleOperationType {
    abstract val device: BluetoothDevice
}


data class Connect(
    override val device: BluetoothDevice,
    val context: Context,
    val mainServiceUUID: UUID
) : BleOperationType()

data class Disconnect(override val device: BluetoothDevice) : BleOperationType()

data class CharacteristicWrite(
    override val device: BluetoothDevice,
    val characteristicUuid: UUID,
    val writeType: Int,
    val payload: ByteArray,
    val title : String
) : BleOperationType() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CharacteristicWrite

        if (device != other.device) return false
        if (characteristicUuid != other.characteristicUuid) return false
        if (writeType != other.writeType) return false
        if (!payload.contentEquals(other.payload)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = device.hashCode()
        result = 31 * result + characteristicUuid.hashCode()
        result = 31 * result + writeType
        result = 31 * result + payload.contentHashCode()
        return result
    }
}

data class CharacteristicRead(
    override val device: BluetoothDevice,
    val characteristicUuid: UUID,
   val title: String
) : BleOperationType()

data class DescriptorWrite(
    override val device: BluetoothDevice,
    val descriptorUuid: UUID,
    val payload: ByteArray
) : BleOperationType() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DescriptorWrite

        if (device != other.device) return false
        if (descriptorUuid != other.descriptorUuid) return false
        if (!payload.contentEquals(other.payload)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = device.hashCode()
        result = 31 * result + descriptorUuid.hashCode()
        result = 31 * result + payload.contentHashCode()
        return result
    }
}

data class DescriptorRead(
    override val device: BluetoothDevice,
    val descriptorUuid: UUID
) : BleOperationType()

data class EnableNotifications(
    override val device: BluetoothDevice,
    val characteristicUuid: UUID,
   val tag: String
) : BleOperationType()

data class DisableNotifications(
    override val device: BluetoothDevice,
    val characteristicUuid: UUID,
    val tag: String
) : BleOperationType()
