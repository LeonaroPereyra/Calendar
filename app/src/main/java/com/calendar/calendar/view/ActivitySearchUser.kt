package com.calendar.calendar.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.navigation.ui.AppBarConfiguration
import androidx.recyclerview.widget.LinearLayoutManager
import com.calendar.calendar.databinding.ActivitySearchUserBinding
import com.calendar.calendar.model.Collection
import com.calendar.calendar.model.Collection.User.STATUSUSER
import com.calendar.calendar.model.Employee
import com.calendar.calendar.model.User
import com.calendar.calendar.utils.SessionManager
import com.calendar.calendar.view.adapter.UserAdapter
import com.calendar.calendar.viewModel.SearchUserViewModel
import com.calendar.calendar.viewModel.action.SearchUserAction


class ActivitySearchUser : BaseActivity(), SearchView.OnQueryTextListener {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivitySearchUserBinding
    private var adapterUser: UserAdapter? = null
    private val viewModel: SearchUserViewModel by viewModels()
    private var useSelectedr: User? = null
    private var type: String? = ""
    private var userName: String? = ""
    private var userCurp: String? = ""
    private var session: SessionManager? = null
    private var employee: Employee? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getEmployeePreference()
        binding = ActivitySearchUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        type = intent.getStringExtra(Collection.Employe.TYPE).toString()
        userName = intent.getStringExtra(Collection.Employe.USERNAME).toString()
        val layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL, false
        )
        adapterUser = UserAdapter(
            this,
            ::userSelected,
            ::callClicked,
            ::galleryClicked,
            employee?.type.toString()
        )
        binding.recyclerUser.layoutManager = layoutManager
        binding.recyclerUser.adapter = adapterUser
        binding.inputSearch.setOnQueryTextListener(this)
        observerViewModel()
        binding.switchCurpName.setOnCheckedChangeListener(object :
            CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(toggleButton: CompoundButton, isChecked: Boolean) {
                if (isChecked) {
                    userCurp = ""
                }
            }
        })
        binding.btnAddUser.setOnClickListener {
            if (binding.recyclerUser.adapter?.itemCount ?: 0 == 0 && userCurp?.length == 18) {
                startActivity(
                    Intent(
                        this,
                        ActivitySaveUser::class.java
                    ).putExtra(Collection.User.USER, userCurp?.toUpperCase())
                )
                finish()
            } else {
                setError()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        validateTime()
        viewModel.getAllUser()
    }

    private fun userSelected(user: User) {
        if (user.active == STATUSUSER[0]) {
            useSelectedr = user
            var intent = Intent(this, ActivityRegisterCredit::class.java)
            intent.putExtra(Collection.User.USER, user.curp?.toUpperCase())
                .putExtra(
                    Collection.Employe.TYPE,
                    type
                ).putExtra(Collection.Prestamo.USERNAME, userName)
                .putExtra(Collection.Prestamo.USERNAMECOMPLETE, user.name + " " + user.lastName)
                .putExtra(Collection.User.ACTIVE, user.active)
                .putExtra(Collection.User.NOPRESTAMO, user.noPrestamo.toString())
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(this, "Usuario con Credito", Toast.LENGTH_SHORT).show()
        }
    }

    private fun galleryClicked(curp: String) {
        startActivity(
            Intent(this, GalleryActivity::class.java).putExtra(
                Collection.Prestamo.CURP,
                curp
            )
        )
    }

    private fun callClicked(numberPhone: String) {
        val callIntent = Intent(Intent.ACTION_CALL)
        callIntent.data = Uri.parse("tel:$numberPhone")


        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CALL_PHONE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CALL_PHONE),
                1
            )
        }
        startActivity(callIntent)
    }

    private fun observerViewModel() {
        viewModel.getUserAction().observe(this) { action ->
            when (action) {
                is SearchUserAction.GetAllUser -> {
                    adapterUser?.submitList(mutableListOf())
                    adapterUser?.submitList(action.userList.toList())
                    binding.recyclerUser.visibility = View.VISIBLE
                    binding.empty.visibility = View.GONE
                }
                is SearchUserAction.GetEmpty -> {
                    adapterUser?.submitList(mutableListOf())
                    binding.recyclerUser.visibility = View.GONE
                    binding.empty.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun setError() {
        Toast.makeText(this, "CURP NO VALIDO", Toast.LENGTH_SHORT).show()
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (binding.switchCurpName.isEnabled) {
            userCurp = query?.toUpperCase()
            viewModel.filterable(userCurp, binding.switchCurpName.isEnabled)

        } else {
            if (query?.length == 18) {
                userCurp = query.toUpperCase()
                viewModel.filterable(userCurp, binding.switchCurpName.isEnabled)
            } else {
                setError()
            }
        }

        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText?.isEmpty() == true) {
            viewModel.getAllUser()
        }
        return false
    }

    override fun onBackPressed() {
        super.onBackPressed()
        var intent =
            Intent(this, MenuUser::class.java)
        startActivity(intent)
    }

    private fun getEmployeePreference() {
        session = SessionManager(applicationContext)
        employee = session?.getEmployee()
    }
}
