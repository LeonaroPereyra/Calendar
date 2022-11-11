package com.calendar.calendar.view

import android.media.Image
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.calendar.calendar.databinding.AcivityGalleryBinding
import com.calendar.calendar.model.Collection
import com.calendar.calendar.view.adapter.GalleryAdapter
import com.calendar.calendar.viewModel.GalleryViewModel
import com.calendar.calendar.viewModel.action.GalleryAction

class GalleryActivity : BaseActivity() {
    private var adapterGallery: GalleryAdapter? = null
    private lateinit var binding: AcivityGalleryBinding
    private val viewModel: GalleryViewModel by viewModels()
    private var curp: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AcivityGalleryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpView()
        curp = intent.getStringExtra(Collection.Prestamo.CURP).toString()
    }

    private fun setUpView() {
        adapterGallery = GalleryAdapter(this,::imageSelected)
        val layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL, false
        )
        binding.rvGallery.layoutManager = layoutManager
        binding.rvGallery.adapter = adapterGallery
        observerViewModel()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getImage(curp)
    }

    private fun observerViewModel() {
        viewModel.getGalleryAction().observe(this) { action ->
            when (action) {
                is GalleryAction.GetGallery -> {
                    adapterGallery?.submitList(mutableListOf())
                    adapterGallery?.submitList(action.list)
                }
                is GalleryAction.DeleteFallery->{
                    adapterGallery?.submitList(mutableListOf())
                    viewModel.getImage(curp)
                }
            }
        }
    }

    private fun imageSelected(urlImage: String){
      viewModel.delete(urlImage)
    }
}