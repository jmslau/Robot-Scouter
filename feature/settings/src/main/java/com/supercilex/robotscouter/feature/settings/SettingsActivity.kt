package com.supercilex.robotscouter.feature.settings

import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.transaction
import com.supercilex.robotscouter.Bridge
import com.supercilex.robotscouter.SettingsActivityCompanion
import com.supercilex.robotscouter.core.RobotScouter
import com.supercilex.robotscouter.core.ui.ActivityBase
import com.supercilex.robotscouter.core.ui.OnBackPressedListener
import com.supercilex.robotscouter.shared.handleUpNavigation
import org.jetbrains.anko.intentFor

@Bridge
class SettingsActivity : ActivityBase() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.RobotScouter_Settings)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        if (savedInstanceState == null) {
            supportFragmentManager.transaction {
                add(R.id.settings, SettingsFragment.newInstance(), SettingsFragment.TAG)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            val handledBack = supportFragmentManager.fragments
                    .any { it is OnBackPressedListener && it.onBackPressed() }
            if (handledBack) return true

            handleUpNavigation()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    companion object : SettingsActivityCompanion {
        override fun createIntent() = RobotScouter.intentFor<SettingsActivity>()
    }
}
