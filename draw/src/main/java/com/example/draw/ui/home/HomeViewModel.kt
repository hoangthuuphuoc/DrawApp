package com.example.draw.ui.home

import android.content.ContentResolver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.draw.data.model.ImageItem
import com.example.draw.data.reponsitory.ImageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val repository = ImageRepository()

    private val _listImage =
        MutableStateFlow<List<ImageItem>>(emptyList())

    val listImage = _listImage.asStateFlow()

    fun loadAllImage(
        contentResolver: ContentResolver,
        isFavourite: Boolean
    ) {
        viewModelScope.launch(Dispatchers.IO) {

            val list =
                repository.loadAllImageUri(contentResolver)

            if (isFavourite) {
                _listImage.value =
                    list.filter {
                        it.favourite
                    }
            } else {
                _listImage.value = list
            }
        }
    }
}