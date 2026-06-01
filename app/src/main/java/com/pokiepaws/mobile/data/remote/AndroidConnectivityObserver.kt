package com.pokiepaws.mobile.data.remote

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import com.pokiepaws.mobile.domain.connectivity.ConnectivityObserver
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AndroidConnectivityObserver
    @Inject
    constructor(
        @ApplicationContext context: Context,
    ) : ConnectivityObserver {
        private val connectivityManager = context.getSystemService(ConnectivityManager::class.java)
        private val _isOnline = MutableStateFlow(connectivityManager.hasValidatedInternet())

        override val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()

        init {
            val request =
                NetworkRequest.Builder()
                    .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    .build()

            connectivityManager.registerNetworkCallback(
                request,
                object : ConnectivityManager.NetworkCallback() {
                    override fun onAvailable(network: Network) {
                        _isOnline.value = connectivityManager.hasValidatedInternet(network)
                    }

                    override fun onCapabilitiesChanged(
                        network: Network,
                        networkCapabilities: NetworkCapabilities,
                    ) {
                        _isOnline.value = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
                    }

                    override fun onLost(network: Network) {
                        _isOnline.value = connectivityManager.hasValidatedInternet()
                    }
                },
            )
        }
    }

private fun ConnectivityManager.hasValidatedInternet(network: Network? = activeNetwork): Boolean {
    val capabilities = network?.let(::getNetworkCapabilities) ?: return false
    return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
        capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
}
