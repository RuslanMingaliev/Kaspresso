package com.kaspersky.kaspressample.device_tests

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule
import com.kaspersky.kaspressample.device.DeviceSampleActivity
import com.kaspersky.kaspressample.screen.DeviceSampleScreen
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import com.kaspersky.kaspresso.testcases.core.testcontext.BaseTestContext
import org.junit.Assert.assertTrue
import org.junit.Assume.assumeTrue
import org.junit.Rule
import org.junit.Test

class DevicePermissionsSampleTest : TestCase() {

    @get:Rule
    val runtimePermissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    @get:Rule
    val activityTestRule = ActivityTestRule(DeviceSampleActivity::class.java, false, true)

    @Test
    fun permissionsSampleTest() {
        before {
            // Run only on devices with Android M or later and skip the test otherwise.
            assumeTrue(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            //adbServer.performShell("pm revoke ${device.targetContext.packageName} ${Manifest.permission.READ_CALL_LOG}")
        }.after {
        }.run {

            step("Request permissions") {
                DeviceSampleScreen {
                    // Button click requests permission using default Android dialog
                    requestPermissionButton {
                        click()
                    }
                }

                device.permissions.apply {
                    flakySafely { assertTrue(isDialogVisible()) }
                    allowViaDialog()
                }

                // Contacts permission should be granted now
                assertTrue(hasCallLogPermission())
            }
        }
    }

    private fun BaseTestContext.hasCallLogPermission(): Boolean =
        device.targetContext.checkSelfPermission(Manifest.permission.READ_CALL_LOG) ==
                PackageManager.PERMISSION_GRANTED
}
