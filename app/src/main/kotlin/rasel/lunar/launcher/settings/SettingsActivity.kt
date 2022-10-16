/*
 * Lunar Launcher
 * Copyright (C) 2022 Md Rasel Hossain
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package rasel.lunar.launcher.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import rasel.lunar.launcher.BuildConfig
import rasel.lunar.launcher.R
import rasel.lunar.launcher.databinding.AboutBinding
import rasel.lunar.launcher.databinding.SettingsActivityBinding
import rasel.lunar.launcher.helpers.Constants
import rasel.lunar.launcher.settings.childs.*


internal class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: SettingsActivityBinding
    private val constants = Constants()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /* set up view */
        binding = SettingsActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /* launch child settings dialogs on button clicks */
        binding.timeDate.setOnClickListener {
            TimeDate().show(supportFragmentManager, constants.BOTTOM_SHEET_TAG)
        }

        binding.weather.setOnClickListener {
            WeatherSettings().show(supportFragmentManager, constants.BOTTOM_SHEET_TAG)
        }

        binding.todo.setOnClickListener {
            TodoSettings().show(supportFragmentManager, constants.BOTTOM_SHEET_TAG)
        }

        binding.apps.setOnClickListener {
            Apps().show(supportFragmentManager, constants.BOTTOM_SHEET_TAG)
        }

        binding.look.setOnClickListener {
            Appearances().show(supportFragmentManager, constants.BOTTOM_SHEET_TAG)
        }

        binding.more.setOnClickListener {
            More().show(supportFragmentManager, constants.BOTTOM_SHEET_TAG)
        }

        binding.advance.setOnClickListener {
            Advance().show(supportFragmentManager, constants.BOTTOM_SHEET_TAG)
        }

        /* about and support dialogs */
        binding.about.setOnClickListener { aboutDialog() }
        binding.support.setOnClickListener { supportDialog() }

        /* show app version name */
        binding.version.text = BuildConfig.VERSION_NAME
    }

    /* about dialog */
    private fun aboutDialog() {
        val bottomSheetDialog = BottomSheetDialog(this)
        val aboutBinding = AboutBinding.inflate(this.layoutInflater)
        bottomSheetDialog.setContentView(aboutBinding.root)
        bottomSheetDialog.show()
    }

    /* support dialog */
    private fun supportDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.support)
            .setMessage(R.string.support_message)
            /* star button */
            .setNeutralButton(R.string.star) {dialog, _ ->
                dialog.dismiss()
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/iamrasel/lunar-launcher")))
            }
            /* donate button */
            .setPositiveButton(R.string.donate) { dialog, _ ->
                dialog.dismiss()
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://iamrasel.github.io/donate")))
            }
            .show()
    }

}
