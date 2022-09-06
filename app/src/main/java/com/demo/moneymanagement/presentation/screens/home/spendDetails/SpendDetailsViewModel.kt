package com.demo.moneymanagement.presentation.screens.home.spendDetails

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demo.moneymanagement.data.Category
import com.demo.moneymanagement.data.Constants.CategoriesChild
import com.demo.moneymanagement.data.Constants.SpendChild
import com.demo.moneymanagement.data.Spend
import com.demo.moneymanagement.presentation.DataState
import com.demo.moneymanagement.presentation.screens.home.spend.getMonth
import com.demo.preferences.general.GeneralGeneralPrefsStoreImpl
import com.google.firebase.database.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@HiltViewModel
open class SpendDetailsViewModel @Inject constructor(
    private val generalGeneralPrefsStoreImpl: GeneralGeneralPrefsStoreImpl
) : ViewModel() {


    private var databaseReference: DatabaseReference =
        FirebaseDatabase.getInstance().getReference("users")

    private var job: Job? = null
    private val _stateSpend = mutableStateOf(DataState<List<SpentDetails>>())
    val stateSpend: State<DataState<List<SpentDetails>>> = _stateSpend


    fun getSpentData() {
        job?.cancel()
        _stateSpend.value = DataState()
        _stateSpend.value = DataState(isLoading = true)
        job?.cancel()
        job = viewModelScope.launch {
            generalGeneralPrefsStoreImpl.getID().onEach {
                databaseReference.child(it)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val spentDetailList: MutableList<SpentDetails> = ArrayList()

                            val categories: MutableList<Category> = ArrayList()
                            if (snapshot.hasChild(CategoriesChild)) {
                                for (data: DataSnapshot in snapshot.child(CategoriesChild).children) {
                                    val category = data.getValue(Category::class.java)
                                    category?.let {
                                        categories.add(category)
                                    }
                                }
                            }

                            if (snapshot.child(SpendChild).hasChild(getMonth())) {
                                val spentChild = snapshot.child(SpendChild)
                                    .child(getMonth()).children
                                for (data: DataSnapshot in spentChild) {
                                    val spent = data.getValue(Spend::class.java)
                                    categories.forEachIndexed { index, category ->
                                        if (category.id == spent?.categoryId) {
                                            val spentDetail = SpentDetails(
                                                categoryID = category.id,
                                                categoryName =category.name,
                                                spent = spent?.amount,
                                                id =spent?.id
                                            )
                                            spentDetailList.add(spentDetail)

                                        }
                                    }
                                }
                                _stateSpend.value = DataState(data = spentDetailList)

                            } else {
                                _stateSpend.value = DataState(error = "not found Categories")
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            _stateSpend.value = DataState(error = error.message)

                        }

                    })
            }.launchIn(viewModelScope)
        }
    }

    open fun resetState() {
        _stateSpend.value = DataState()

        job?.cancel()
    }

    suspend fun deleteSpend(spendID:String):Boolean{
         return suspendCoroutine {result->
            generalGeneralPrefsStoreImpl.getID().onEach {
                databaseReference.child(it).child(SpendChild)
                    .child(getMonth()).child(spendID).removeValue()
                    .addOnSuccessListener {
                        result.resume(true)
                    }
                    .addOnFailureListener{
                        result.resume(false)
                    }
            }.launchIn(viewModelScope)
        }

    }
}

data class SpentDetails(
    val categoryID: String? = null, val categoryName: String? = null, var spent: String? = null,
    val id: String? = null

)