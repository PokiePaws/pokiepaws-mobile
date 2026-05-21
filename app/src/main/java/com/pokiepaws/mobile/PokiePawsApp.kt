package com.pokiepaws.mobile

import android.app.Application
import com.pokiepaws.mobile.data.sync.NetworkSyncMonitor
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class PokiePawsApp : Application() {
    @Inject
    lateinit var networkSyncMonitor: NetworkSyncMonitor

    override fun onCreate() {
        super.onCreate()
        networkSyncMonitor.start()
    }
}
