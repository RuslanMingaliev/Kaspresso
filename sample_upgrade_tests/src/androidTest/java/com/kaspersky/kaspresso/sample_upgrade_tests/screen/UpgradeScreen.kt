package com.kaspersky.kaspresso.sample_upgrade_tests.screen

import com.kaspersky.components.kautomator.dsl.edit.UiEditText
import com.kaspersky.components.kautomator.dsl.text.UiButton
import com.kaspersky.components.kautomator.dsl.text.UiTextView

object UpgradeScreen : UpgradeUiScreen<UpgradeScreen>() {

    val version = UiTextView { withId(this@UpgradeScreen.packageName, "upgrade_version") }
    val input = UiEditText { withId(this@UpgradeScreen.packageName, "upgrade_value_input") }
    val applyBtn = UiButton { withId(this@UpgradeScreen.packageName, "upgrade_apply_btn") }
    val value = UiTextView { withId(this@UpgradeScreen.packageName, "upgrade_value_current") }
}
