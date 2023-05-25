package ru.twilightsu.walletfs.ui.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import ru.twilightsu.walletfs.CategoryRepository
import ru.twilightsu.walletfs.entities.Category
import ru.twilightsu.walletfs.entities.CategoryTypeEnum

class CategoryViewModel(
    savedStateHandle: SavedStateHandle,
    private val categoryRepository: CategoryRepository
) : ViewModel() {
    private val categoryId: Int = checkNotNull(savedStateHandle["categoryId"])
    private val categoryTypeId: Int = checkNotNull(savedStateHandle["categoryTypeId"])

    private val _uiState = MutableStateFlow(CategoryUiState(categoryTypeId = categoryTypeId))
    val uiState: StateFlow<CategoryUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.value = categoryRepository.get(categoryId)
                .filterNotNull()
                .first()
                .toCategoryUiState()
        }
    }

    fun updateName(name: String){
        _uiState.value = _uiState.value.copy(
            name = name
        )
        isFormValid()
    }

    fun updateCategoryTypeId(typeId: Int){
        _uiState.value =_uiState.value.copy(
            categoryTypeId = typeId
        )
        isFormValid()
    }

    fun isFormValid() {
        var formValid = true
        val state = _uiState.value

        if (state.name.isBlank())
            formValid = false

        _uiState.value = _uiState.value.copy(
            isFormValid = formValid
        )
    }

    suspend fun saveCategory(){
        categoryRepository.insert(_uiState.value.toCategory())
    }
}

data class CategoryUiState(
    val id: Int = 0,
    val name: String = "",
    val categoryTypeId: Int = CategoryTypeEnum.EXPENSE.value,

    val isFormValid: Boolean = false
)

fun CategoryUiState.toCategory(): Category = Category(
    Id = id,
    Name = name,
    CategoryTypeId = categoryTypeId
)

fun Category.toCategoryUiState(): CategoryUiState = CategoryUiState(
    id = Id,
    name = Name,
    categoryTypeId = CategoryTypeId
)