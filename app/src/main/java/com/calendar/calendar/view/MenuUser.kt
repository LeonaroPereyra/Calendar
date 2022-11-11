package com.calendar.calendar.view

import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.calendar.calendar.databinding.ActivityMenuUserBinding
import com.calendar.calendar.model.Collection
import com.calendar.calendar.model.Employee
import com.calendar.calendar.utils.SessionManager

class MenuUser : BaseActivity() {
    private lateinit var binding: ActivityMenuUserBinding
    private var session: SessionManager? = null
    private var employee: Employee? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getEmployeePreference()
        if (employee?.type == "ADMIN") {
            setUpAdminView()
        } else {
            setUpNormalViewView()
        }
        binding.btnUser.setOnClickListener {
            var intent =
                Intent(this, ActivitySearchUser::class.java)
            startActivity(intent)
        }
        binding.btnWork.setOnClickListener {
            var intent = Intent(this, DailyWork::class.java)
            startActivity(intent)
        }
        binding.btnEmployee.setOnClickListener {
            var intent = Intent(this, ActivityRegisterEmployee::class.java)
            startActivity(intent)
        }
    }

    private fun getEmployeePreference() {
        session = SessionManager(applicationContext)
        employee = session?.getEmployee()
    }

    private fun setUpAdminView() {
        binding.btnEmployee.visibility = View.VISIBLE
    }

    private fun setUpNormalViewView() {
        binding.btnEmployee.visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()
        validateTime()
    }
}