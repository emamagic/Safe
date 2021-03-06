package com.emamagic.safe.connectivity

import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET
import android.net.NetworkRequest
import android.util.Log
import androidx.lifecycle.LiveData
import com.emamagic.safe.util.General
import kotlinx.coroutines.*

class ConnectionLiveData(context: Context, val lifecycleScope: CoroutineScope? = null) : LiveData<ConnectivityStatus>(), ConnectivityPublisherDelegate {

    val TAG = "C-Manager"

    private lateinit var networkCallback: ConnectivityManager.NetworkCallback
    private val cm = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
    private lateinit var apiCalls: List<suspend () -> Unit>

    fun enableOfflineMode() {
        General.shouldRetryNetworkCall = false
        postValue(ConnectivityStatus.OFFLINE_MODE)
    }

    fun setRefreshVisibleFragmentFunc(functions: List<suspend () -> Unit>) { apiCalls = functions }

    fun disableOfflineMode() {
        General.shouldRetryNetworkCall = true
        refreshVisibleFragmentFuncIfEnable()
    }

    fun connect() {
        Log.e(TAG, "connect: ", )
        General.shouldRetryNetworkCall = true
        refreshVisibleFragmentFuncIfEnable()
        postValue(ConnectivityStatus.CONNECT)
    }

    fun disconnect() {
        Log.e(TAG, "disconnect: ", )
        General.shouldRetryNetworkCall = false
        postValue(ConnectivityStatus.DISCONNECT)
    }

    override fun onActive() {
        Log.d(TAG, "onAvailable")
        networkCallback = createNetworkCallback()
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NET_CAPABILITY_INTERNET)
            .build()
        cm.registerNetworkCallback(networkRequest, networkCallback)
        ConnectivityPublisher.subscribe(this, General.CONNECT)
        ConnectivityPublisher.subscribe(this, General.DISCONNECT)
    }

    override fun onInactive() {
        cm.unregisterNetworkCallback(networkCallback)
        ConnectivityPublisher.unSubscribe(this, General.CONNECT)
        ConnectivityPublisher.unSubscribe(this, General.DISCONNECT)
    }

    private fun createNetworkCallback() = object : ConnectivityManager.NetworkCallback() {

        override fun onAvailable(network: Network) {
            val networkCapabilities = cm.getNetworkCapabilities(network)
            val hasInternetCapability = networkCapabilities?.hasCapability(NET_CAPABILITY_INTERNET)
            Log.d(TAG, "onAvailable: ${network}, $hasInternetCapability")
            if (hasInternetCapability == true) {
                // check if this network actually has internet
                CoroutineScope(Dispatchers.IO).launch {
                    val hasInternet = DoesNetworkHaveInternet.execute()
                    if (hasInternet) {
                        withContext(Dispatchers.Main) {
                            Log.d(TAG, "onAvailable: adding network. $network")
                            connect()
                        }
                    }
                }
            }
        }


        override fun onLost(network: Network) {
            Log.d(TAG, "onLost: $network")
            disconnect()
        }

    }

    override fun receiveConnectivity(connectivity: Connectivity) {
        Log.e(TAG, "receiveConnectivity: ${connectivity.status}")
        when (connectivity.status) {
            General.CONNECT -> connect()
            General.DISCONNECT -> disconnect()
            General.OFFLINE_MODE -> enableOfflineMode()
        }
    }

    fun refreshVisibleFragmentFuncIfEnable() {
        if (this::apiCalls.isInitialized && !apiCalls.isNullOrEmpty()) {
            lifecycleScope?.launch(Dispatchers.IO) {
                apiCalls.forEach { it() }
            }
        }
    }

}