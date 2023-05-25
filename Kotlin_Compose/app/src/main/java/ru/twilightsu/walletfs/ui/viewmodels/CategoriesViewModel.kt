package ru.twilightsu.walletfs.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import ru.twilightsu.walletfs.CategoryRepository
import ru.twilightsu.walletfs.entities.Category

class CategoriesViewModel(
    savedStateHandle: SavedStateHandle,
    private val categoryRepository: CategoryRepository
): ViewModel() {
    val categoryTypeId: Int = checkNotNull(savedStateHandle["categoryTypeId"])
    val allTypedCategories: LiveData<List<Category>> = categoryRepository.getAllTyped(categoryTypeId).asLiveData()
}