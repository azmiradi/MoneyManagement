package com.demo.moneymanagement.presentation.screens.home.categories

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demo.moneymanagement.data.Category
import com.demo.moneymanagement.data.Constants.CategoriesChild
import com.demo.moneymanagement.data.RegistrarRequest
import com.demo.moneymanagement.presentation.DataState
import com.demo.preferences.general.GeneralGeneralPrefsStoreImpl
import com.google.firebase.database.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val generalGeneralPrefsStoreImpl: GeneralGeneralPrefsStoreImpl
) : ViewModel() {

    private val _categoryInput = mutableStateOf(false)
    val categoryInput: State<Boolean> = _categoryInput

    private var databaseReference: DatabaseReference =
        FirebaseDatabase.getInstance().getReference("users")

    private var job: Job? = null
    private val _state = mutableStateOf(DataState<List<Category>>())
    val state: State<DataState<List<Category>>> = _state

    private val _stateAddCategory = mutableStateOf(DataState<Boolean>())
    val stateAddCategory: State<DataState<Boolean>> = _stateAddCategory

    private val _stateDeleteCategory = mutableStateOf(DataState<Boolean>())
    val stateDeleteCategory: State<DataState<Boolean>> = _stateDeleteCategory

    fun getCategories() {
        job?.cancel()
        _state.value = DataState()
        _state.value = DataState(isLoading = true)
        job?.cancel()
        job = viewModelScope.launch {
            generalGeneralPrefsStoreImpl.getID().onEach {
                databaseReference.child(it)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.hasChild(CategoriesChild)) {
                                val categories: MutableList<Category> = ArrayList()
                                for (data: DataSnapshot in snapshot.child(CategoriesChild).children) {
                                    data.getValue(Category::class.java)?.let {
                                        categories.add(it)
                                    }
                                }
                                _state.value = DataState(data = categories)

                            } else {
                                _state.value = DataState(error = "not found Categories")
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            _state.value = DataState(error = error.message)

                        }

                    })
            }.launchIn(viewModelScope)
        }
    }

    fun addCategoryMoney(name: String) {
        if (name.isEmpty()) {
            _categoryInput.value = true
            return
        }

        job?.cancel()
        _stateAddCategory.value = DataState()
        _stateAddCategory.value = DataState(isLoading = true)
        job = viewModelScope.launch {
            generalGeneralPrefsStoreImpl.getID().onEach {
                checkCategoryExist(category = name, userID = it){
                    val categoryID = databaseReference.child(it)
                        .child(CategoriesChild).push().key
                    databaseReference.child(it)
                        .child(CategoriesChild)
                        .child(categoryID.toString())
                        .setValue(Category(name = name, id = categoryID.toString()))
                        .addOnFailureListener {
                            _stateAddCategory.value = DataState(error = it.message.toString())
                        }.addOnSuccessListener {
                            _stateAddCategory.value = DataState(data = true)
                        }
                }
            }.launchIn(viewModelScope)
        }
    }

    fun resetState() {
        _state.value = DataState()
        _stateDeleteCategory.value = DataState()
        _stateAddCategory.value = DataState()
        _categoryInput.value=false
        job?.cancel()
    }

    fun deleteCategory(id: String) {
        _stateDeleteCategory.value = DataState()
        job?.cancel()
        _stateDeleteCategory.value = DataState(isLoading = true)
        job = viewModelScope.launch {
            generalGeneralPrefsStoreImpl.getID().onEach {
                databaseReference.child(it)
                    .child(CategoriesChild)
                    .child(id).removeValue()
                    .addOnFailureListener {
                        _stateDeleteCategory.value = DataState(error = it.message.toString())
                    }.addOnSuccessListener {
                        _stateDeleteCategory.value = DataState(data = true)
                    }
            }.launchIn(viewModelScope)
        }
    }

    private fun checkCategoryExist(
        category: String,
        userID: String,
        categoryNotExit: () -> Unit,
     ) {
        databaseReference.child(userID).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.hasChild(CategoriesChild)) {
                    for (userSnap: DataSnapshot in snapshot.child(CategoriesChild).children) {
                        val categoryData = userSnap.getValue(Category::class.java)
                        categoryData?.let {
                            if (it.name==category) {
                                _stateAddCategory.value =
                                    DataState(error = "this Category Exist Before!")
                                return
                            }
                        }
                    }
                    categoryNotExit()
                } else {
                    categoryNotExit()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                _stateAddCategory.value = DataState(error = error.message.toString())
            }

        })

    }

}