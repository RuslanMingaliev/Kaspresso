package com.kaspersky.kaspressample.device_tests

import android.Manifest
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET
import android.net.NetworkCapabilities.TRANSPORT_CELLULAR
import android.net.NetworkRequest
import android.net.wifi.WifiManager
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule
import com.agoda.kakao.screen.Screen
import com.kaspersky.kaspressample.device.DeviceSampleActivity
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import com.kaspersky.kaspresso.testcases.core.testcontext.BaseTestContext
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.IllegalStateException

class DeviceInternetSampleTest : TestCase() {

    companion object {
        private const val NETWORK_ESTABLISHMENT_DELAY = 2_500L
    }

    @get:Rule
    val runtimePermissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.CHANGE_NETWORK_STATE
    )

    @get:Rule
    val activityTestRule = ActivityTestRule(DeviceSampleActivity::class.java, true, true)

    @Test
    fun internetSampleTest() {
        before {
            device.network.enable()
        }.after {
            device.network.enable()
        }.run {

            step("Disable internet") {
                device.network.disable()
                Screen.idle(NETWORK_ESTABLISHMENT_DELAY)
                assertFalse(isConnectedToNetwork())
            }

            step("Enable internet") {
                device.network.enable()
                Screen.idle(NETWORK_ESTABLISHMENT_DELAY)
                assertTrue(isConnectedToNetwork())
            }

            step("Toggle WiFi") {
                device.network.toggleWiFi(false)
                Screen.idle(NETWORK_ESTABLISHMENT_DELAY)
                assertFalse(isWiFiEnabled())

                device.network.toggleWiFi(true)
                Screen.idle(NETWORK_ESTABLISHMENT_DELAY)
                assertTrue(isWiFiEnabled())
            }
        }
    }

    private fun BaseTestContext.isConnectedToNetwork(): Boolean {
        val manager = device.context.getSystemService(ConnectivityManager::class.java) as ConnectivityManager
        val cdl = CountDownLatch(1)
        var isConnected: Boolean = false

        val request = NetworkRequest.Builder()
            .addTransportType(TRANSPORT_CELLULAR)
            .addCapability(NET_CAPABILITY_INTERNET)
            .build()

        manager.requestNetwork(request, object : ConnectivityManager.NetworkCallback() {
            override fun onLost(network: Network) {
                super.onLost(network)
                manager.unregisterNetworkCallback(this)
                isConnected = false
                cdl.countDown()
            }

            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                manager.unregisterNetworkCallback(this)
                isConnected = true
                cdl.countDown()
            }
        })

        cdl.await(10L, TimeUnit.SECONDS)
        return isConnected;
    }

    private fun BaseTestContext.isWiFiEnabled(): Boolean =
        device.context.getSystemService(WifiManager::class.java)?.isWifiEnabled
            ?: throw IllegalStateException("WifiManager is unavailable")
}