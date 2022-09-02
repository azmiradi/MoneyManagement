package com.demo.moneymanagement.presentation.screens.home.monthly_report

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

@HiltViewModel
class MonthlyReportViewModel @Inject constructor(
    private val generalGeneralPrefsStoreImpl: GeneralGeneralPrefsStoreImpl
) : ViewModel() {


    private var databaseReference: DatabaseReference =
        FirebaseDatabase.getInstance().getReference("users")

    private var job: Job? = null
    private val _state = mutableStateOf(DataState<List<SpentDetails>>())
    val state: State<DataState<List<SpentDetails>>> = _state


    fun getSpentData() {
        job?.cancel()
        _state.value = DataState()
        _state.value = DataState(isLoading = true)
        job?.cancel()
        job = viewModelScope.launch {
            generalGeneralPrefsStoreImpl.getID().onEach {
                databaseReference.child(it)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            var spentList: MutableList<Spend> = ArrayList()

                            val spentDetailList: MutableList<SpentDetails> = ArrayList()
                            if (snapshot.hasChild(CategoriesChild)) {
                                for (data: DataSnapshot in snapshot.child(CategoriesChild).children) {
                                    val category = data.getValue(Category::class.java)
                                    category?.let {
                                        spentDetailList.add(SpentDetails(it.id, it.name))
                                    }
                                }
                            }
                            if (snapshot.child(SpendChild).hasChild(getMonth())) {
                                val spentChild = snapshot.child(SpendChild)
                                    .child(getMonth()).children
                                for (data: DataSnapshot in spentChild) {
                                    val spent = data.getValue(Spend::class.java)
                                    spentDetailList.forEachIndexed { index, dataSpent ->
                                        if (dataSpent.categoryID == spent?.categoryId) {
                                            dataSpent.spent =
                                                ((dataSpent.spent ?: "0").toInt() + (spent?.amount
                                                    ?: "0").toInt()).toString()
                                            spentDetailList[index] = dataSpent

                                        }
                                    }
                                }
                                _state.value = DataState(data = spentDetailList)

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

    fun resetState() {
        _state.value = DataState()

        job?.cancel()
    }


}

data class SpentDetails(
    val categoryID: String? = null, val categoryName: String? = null, var spent: String? = null
)