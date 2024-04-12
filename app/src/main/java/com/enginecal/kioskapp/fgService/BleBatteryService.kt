package com.enginecal.kioskapp.fgService

import android.Manifest
import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.ServiceCompat
import androidx.core.content.ContextCompat
import com.enginecal.kioskapp.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds


class BleBatteryService : Service() {

    private val binder = LocalBinder()
    private var timerJob: Job? = null
    private val coroutineScope = CoroutineScope(Job())

    private val bluetoothAdapter: BluetoothAdapter? by lazy(LazyThreadSafetyMode.NONE) {
        val bluetoothManager =
            getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }
    private val expectedDeviceAddress = "F7:6B:D5:29:36:8F"
    private var expectedDevice: BluetoothDevice? = null
    private var bluetoothGatt: BluetoothGatt? = null
    private var isPowerConnected = false
    private var isExpectedDeviceFound = false

    inner class LocalBinder : Binder() {
        fun getService(): BleBatteryService = this@BleBatteryService
    }
    override fun onBind(intent: Intent?): IBinder? {
     Log.d(TAG, "onBind")
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand")
        startAsForegroundService()
        return super.onStartCommand(intent, flags, startId)
    }


    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate")
        Toast.makeText(this, "Foreground Service created", Toast.LENGTH_SHORT).show()
//        startServiceRunningTicker()
        startBluetoothScanning()

        // Register broadcast receivers for Bluetooth and power connection
        try {
            val combinedfilter = IntentFilter().apply {
                addAction(Intent.ACTION_POWER_CONNECTED)
                addAction(Intent.ACTION_POWER_DISCONNECTED)
                addAction(BluetoothDevice.ACTION_FOUND)
                addAction(Companion.ACTION_DEVICE_NOT_FOUND)
            }
            registerReceiver(combinedReceiver, combinedfilter)
            /*val filterBluetooth = IntentFilter(BluetoothDevice.ACTION_FOUND)
            //filterPower(ACTION_FOUND)
            registerReceiver(bluetoothReceiver, filterBluetooth)

            val filterPower = IntentFilter()
            filterPower.addAction(Intent.ACTION_POWER_CONNECTED)
            filterPower.addAction(Intent.ACTION_POWER_DISCONNECTED)
            registerReceiver(powerConnectionReceiver, filterPower)*/

        } catch (e: Exception) {
            Log.e("BleBatteryService", "Error registering broadcast receivers: ${e.localizedMessage}")
        }


    }


    private val combinedReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                Intent.ACTION_POWER_CONNECTED -> {
                    Log.d(TAG, "Power connected")
                    isPowerConnected = true
                    checkConditionsAndStartActivity()
                }
                Intent.ACTION_POWER_DISCONNECTED -> {
                    Log.d(TAG, "Power disconnected")
                    isPowerConnected = false
                }
                BluetoothDevice.ACTION_FOUND -> {
                    val device =
                        intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                    Log.d(TAG, "Found Bluetooth device: ${device?.name}, Address: ${device?.address}")

                    if (device?.address == expectedDeviceAddress) {
                        expectedDevice = device
                        Log.d(TAG, "Expected device found during scanning: ${device?.name}")

                        // Stop Bluetooth scanning if the expected device is found
                        if (ContextCompat.checkSelfPermission(
                                applicationContext,
                                Manifest.permission.BLUETOOTH_SCAN
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            // TODO: Consider requesting permissions if needed
                            return
                        }
                        bluetoothAdapter?.cancelDiscovery()
                        isExpectedDeviceFound = true
                        checkConditionsAndStartActivity()
                    }
                }
                Companion.ACTION_DEVICE_NOT_FOUND -> {
                    // Handle the case where the expected device is not found
                    Log.d(TAG, "Expected device not found during scanning")
                    isExpectedDeviceFound = false
                    //checkConditionsAndStartActivity()
                }
            }
        }

        private fun checkConditionsAndStartActivity() {
            if (isPowerConnected && isExpectedDeviceFound) {
                Toast.makeText(
                    this@BleBatteryService,
                    "Battery connected and expected device found",
                    Toast.LENGTH_SHORT
                ).show()
                val mIntent = Intent(applicationContext, MainActivity::class.java).apply {
                    setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                startActivity(mIntent)
            }
        }
    }

    /*
        private val powerConnectionReceiver: BroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                when (intent.action) {
                    Intent.ACTION_POWER_CONNECTED -> {
                        Log.d(TAG, "Power connected")
                        if (expectedDevice != null) {
                            Toast.makeText(
                                this@BleBatteryService,
                                "Battery connected and expected device found",
                                Toast.LENGTH_SHORT
                            ).show()
                            val mIntent = Intent(applicationContext, MainActivity::class.java).apply {
                                setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                            startActivity(mIntent)
                        }
                    }
                    Intent.ACTION_POWER_DISCONNECTED -> {
                        Log.d(TAG, "Power disconnected")
                        Toast.makeText(
                            this@BleBatteryService,
                            "Battery disconnected ",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }



        private val receiver: Receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action) {
                    BluetoothDevice.ACTION_FOUND -> {
                        val device =
                            intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                        if (device?.address == expectedDeviceAddress) {
                            expectedDevice = device
                            Log.d(TAG, "Expected device found during scanning: ${device?.name}")
                            // Stop Bluetooth scanning if the expected device is found
                            if (ContextCompat.checkSelfPermission(
                                    applicationContext,
                                    Manifest.permission.BLUETOOTH_SCAN
                                ) != PackageManager.PERMISSION_GRANTED
                            ) {
                                return
                            }
                            bluetoothAdapter?.cancelDiscovery()
                            connectToDevice()
                        }
                    }
                }
            }
        }

        private val bluetoothReceiver: BroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action) {
                    BluetoothDevice.ACTION_FOUND -> {
                        val device =
                            intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                        Log.d(TAG, "Found Bluetooth device: ${device?.name}, Address: ${device?.address}")

                        if (device?.address == expectedDeviceAddress) {
                            expectedDevice = device
                            Log.d(TAG, "Expected device found during scanning: ${device?.name}")

                            // Stop Bluetooth scanning if the expected device is found
                            if (ContextCompat.checkSelfPermission(
                                    applicationContext,
                                    Manifest.permission.BLUETOOTH_SCAN
                                ) != PackageManager.PERMISSION_GRANTED
                            ) {
                                // TODO: Consider calling
                                //    ActivityCompat#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for ActivityCompat#requestPermissions for more details.
                                return
                            }
                            bluetoothAdapter?.cancelDiscovery()
                            connectToDevice()
                        }
                    }
                }
            }
        }

    */

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
        timerJob?.cancel()
        coroutineScope.coroutineContext.cancelChildren()
        //unregisterReceiver(bluetoothReceiver);
        unregisterReceiver(combinedReceiver);
        //unregisterReceiver(powerConnectionReceiver);
        disconnectGatt()
        Toast.makeText(this, "Foreground Service destroyed", Toast.LENGTH_SHORT).show()
    }


    private fun startAsForegroundService() {
        // create the notification channel
        NotificationsHelper.createNotificationChannel(this)

        // promote service to foreground service
        ServiceCompat.startForeground(
            this,
            1,
            NotificationsHelper.buildNotification(this),
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                ServiceInfo.FOREGROUND_SERVICE_TYPE_CONNECTED_DEVICE
            } else {
                0
            }
        )
    }

    fun stopForegroundService() {
        stopSelf()
    }

    private fun startBluetoothScanning() {
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(combinedReceiver, filter)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            return
        }
        bluetoothAdapter?.startDiscovery()
    }


    private fun connectToDevice() {
        expectedDevice?.let { device ->
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                return
            }
            bluetoothGatt = device.connectGatt(this, false, gattCallback)
        }
    }

    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            when (newState) {
                BluetoothGatt.STATE_CONNECTED -> {
                    if (ContextCompat.checkSelfPermission(
                            applicationContext,
                            Manifest.permission.BLUETOOTH_CONNECT
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                         return
                    }
                    Log.d(TAG, "Connected to expected device: ${expectedDevice?.name}")

                    // Device connected, do any additional setup here if needed
                }
                BluetoothGatt.STATE_DISCONNECTED -> {
                    Log.d(TAG, "Disconnected from expected device: ${expectedDevice?.name}")

                    // Device disconnected, start scanning again
                    startBluetoothScanning()
                }
            }
        }
    }

    private fun disconnectGatt() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            return
        }
        bluetoothGatt?.disconnect()
        bluetoothGatt?.close()
        bluetoothGatt = null
    }


    private fun startServiceRunningTicker() {
        timerJob?.cancel()
        timerJob = coroutineScope.launch {
            tickerFlow()
                .collectLatest {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@BleBatteryService,
                            "Foreground Service still running!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }

    private fun tickerFlow(
        period: Duration = TICKER_PERIOD_SECONDS,
        initialDelay: Duration = TICKER_PERIOD_SECONDS
    ) = flow {
        delay(initialDelay)
        while (true) {
            emit(Unit)
            delay(period)
        }
    }

    companion object {
        lateinit var LocalBinder: Any
        private const val TAG = "BleBatteryService"
        private val LOCATION_UPDATES_INTERVAL_MS = 1.seconds.inWholeMilliseconds
        private val TICKER_PERIOD_SECONDS = 5.seconds
        const val ACTION_DEVICE_NOT_FOUND = "com.enginecal.kioskapp.ACTION_DEVICE_NOT_FOUND"
    }




}