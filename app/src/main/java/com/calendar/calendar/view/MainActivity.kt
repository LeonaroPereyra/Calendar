package com.calendar.calendar.view

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.calendar.calendar.R
import com.calendar.calendar.databinding.ActivityMainBinding

class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.fab.setOnClickListener { view ->
            validateAccess()
        }
    }

    private fun validateAccess() {
        val text: String =
            "${binding.datePicker1.dayOfMonth}/${binding.datePicker1.month+1}/${binding.datePicker1.year}"
        if ( "1/1/1999" == "1/1/1999") {
            startActivity(Intent(this, ActivityLogin::class.java))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        validateTime()
    }

}