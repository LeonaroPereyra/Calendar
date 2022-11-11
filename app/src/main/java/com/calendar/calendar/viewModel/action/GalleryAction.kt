package com.calendar.calendar.viewModel.action

sealed class GalleryAction {
    class GetGallery(val list: MutableList<Pair<String,String>>) : GalleryAction()
    object DeleteFallery : GalleryAction()
}