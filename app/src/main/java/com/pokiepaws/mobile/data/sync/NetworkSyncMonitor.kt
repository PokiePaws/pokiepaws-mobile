package com.pokiepaws.mobile.data.sync

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkSyncMonitor
    @Inject
    constructor(
        @param:ApplicationContext private val context: Context,
        private val synchronizer: AppDataSynchronizer,
    ) {
        private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        private var registered = false

        fun start() {
            if (registered) return
            registered = true

            val connectivityManager = context.getSystemService(ConnectivityManager::class.java)
            val request =
                NetworkRequest
                    .Builder()
                    .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    .build()

            connectivityManager.registerNetworkCallback(
                request,
                object : ConnectivityManager.NetworkCallback() {
                    override fun onAvailable(network: Network) {
                        scope.launch { synchronizer.syncReadableCache() }
                    }
                },
            )

            if (connectivityManager.activeNetwork != null) {
                scope.launch { synchronizer.syncReadableCache() }
            }
        }
    }
