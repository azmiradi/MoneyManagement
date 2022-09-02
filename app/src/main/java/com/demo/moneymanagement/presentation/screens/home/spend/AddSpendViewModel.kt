package com.demo.moneymanagement.presentation.screens.home.spend

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demo.moneymanagement.data.Category
import com.demo.moneymanagement.data.Constants
import com.demo.moneymanagement.data.Spend
import com.demo.moneymanagement.presentation.DataState
import com.demo.preferences.general.GeneralGeneralPrefsStoreImpl
import com.google.firebase.database.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AddSpendViewModel @Inject constructor(
    private val generalGeneralPrefsStoreImpl: GeneralGeneralPrefsStoreImpl
) : ViewModel() {

    private var databaseReference: DatabaseReference =
        FirebaseDatabase.getInstance().getReference("users")

    private var job: Job? = null
    private val _state = mutableStateOf(DataState<List<Category>>())
    val state: State<DataState<List<Category>>> = _state
    private val _stateAddAmount = mutableStateOf(DataState<Boolean>())
    val stateAddAmount: State<DataState<Boolean>> = _stateAddAmount

    private val _amountInput = mutableStateOf(false)
    val amountInput: State<Boolean> = _amountInput

    private val _categoryInput = mutableStateOf(false)
    val categoryInput: State<Boolean> = _categoryInput

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
                            if (snapshot.hasChild(Constants.CategoriesChild)) {
                                val categories: MutableList<Category> = ArrayList()
                                for (data: DataSnapshot in snapshot.child(Constants.CategoriesChild).children) {
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

    fun addSpend(amount: String, categoryId: String) {
        _amountInput.value = false
        _categoryInput.value = false
        if (amount.isEmpty()) {
            _amountInput.value = true
            return
        }
        if (categoryId.isEmpty()) {
            _categoryInput.value = true
            return
        }
        val month = getMonth()
        job?.cancel()
        _stateAddAmount.value = DataState()
        _stateAddAmount.value = DataState(isLoading = true)
        job = viewModelScope.launch {
            generalGeneralPrefsStoreImpl.getID().onEach {
                val spendID = databaseReference.child(it)
                    .child(Constants.SpendChild).push().key
                databaseReference.child(it)
                    .child(Constants.SpendChild)
                    .child(month)
                    .child(spendID.toString())
                    .setValue(Spend(amount = amount, id = spendID, categoryId = categoryId))
                    .addOnFailureListener {
                        _stateAddAmount.value = DataState(error = it.message.toString())
                    }.addOnSuccessListener {
                        _stateAddAmount.value = DataState(data = true)
                    }

            }.launchIn(viewModelScope)
        }
    }



    fun resetState() {
        _stateAddAmount.value = DataState()
        _state.value = DataState()
        _categoryInput.value=false
        _amountInput.value=false
        job?.cancel()
    }


}

fun getMonth(): String {
    val calender = Calendar.getInstance()
    val sdf = SimpleDateFormat("yyyy_MM", Locale.ENGLISH)
    return sdf.format(calender.timeInMillis)
}