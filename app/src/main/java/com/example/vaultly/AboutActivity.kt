package com.example.vaultly

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        setInfoRow(R.id.rowDevelopedFor, R.string.developed_for_label, R.string.developed_for_value)
        setInfoRow(R.id.rowStorage, R.string.storage_label, R.string.storage_value)
        setInfoRow(R.id.rowBuiltWith, R.string.built_with_label, R.string.built_with_value)

        setupBottomNav()
    }

    private fun setInfoRow(rowId: Int, labelRes: Int, valueRes: Int) {
        val row = findViewById<android.view.View>(rowId)
        row.findViewById<TextView>(R.id.tvInfoLabel).text = getString(labelRes)
        row.findViewById<TextView>(R.id.tvInfoValue).text = getString(valueRes)
    }

    private fun setupBottomNav() {
        findViewById<LinearLayout>(R.id.navHome).setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
        findViewById<LinearLayout>(R.id.navSaved).setOnClickListener {
            startActivity(Intent(this, SavedProductsActivity::class.java))
        }
    }
}
