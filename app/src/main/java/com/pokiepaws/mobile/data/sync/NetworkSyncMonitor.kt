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
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
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
        private val syncMutex = Mutex()
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
                        launchSync()
                    }
                },
            )

            if (connectivityManager.activeNetwork != null) {
                launchSync()
            }
        }

        private fun launchSync() {
            scope.launch {
                syncMutex.withLock {
                    synchronizer.syncReadableCache()
                }
            }
        }
    }
