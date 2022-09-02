package com.demo.moneymanagement.presentation.screens.home

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demo.moneymanagement.data.Constants
import com.demo.moneymanagement.data.Constants.SpendChild
import com.demo.moneymanagement.data.RegistrarRequest
import com.demo.moneymanagement.data.Spend
import com.demo.moneymanagement.presentation.DataState
import com.demo.moneymanagement.presentation.screens.home.spend.getMonth
import com.demo.preferences.general.GeneralGeneralPrefsStoreImpl
import com.google.firebase.database.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val generalGeneralPrefsStoreImpl: GeneralGeneralPrefsStoreImpl
) : ViewModel() {

    private var databaseReference: DatabaseReference =
        FirebaseDatabase.getInstance().getReference("users")

    private var job: Job? = null
    private val _state = mutableStateOf(DataState<RegistrarRequest>())
    val state: State<DataState<RegistrarRequest>> = _state

    private val _stateAddReach = mutableStateOf(DataState<Boolean>())
    val stateAddReach: State<DataState<Boolean>> = _stateAddReach


    private val _salaryInput = mutableStateOf(false)
    val salaryInput: State<Boolean> = _salaryInput

    fun getUserData() {
        _state.value = DataState()
        _state.value = DataState(isLoading = true)
        job?.cancel()
        job = viewModelScope.launch {
            generalGeneralPrefsStoreImpl.getID().onEach {
                val spent = getSpent(it)
                if (!spent.first) {
                    _state.value = DataState(error = spent.second)
                    return@onEach
                }
                databaseReference.child(it)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val user = snapshot.getValue(RegistrarRequest::class.java)
                            user?.totalSpent = spent.second
                            _state.value = DataState(data = user)
                        }

                        override fun onCancelled(error: DatabaseError) {
                            _state.value = DataState(error = error.message)

                        }

                    })
            }.launchIn(viewModelScope)
        }
    }


    private suspend fun getSpent(userID: String): Pair<Boolean, String> {
        val month = getMonth()
        return suspendCoroutine { continution ->
            databaseReference.child(userID)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var spentAmount = 0
                        if (snapshot.child(SpendChild).hasChild(month)) {
                            val snapData = snapshot.child(SpendChild)
                                .child(month).children
                            for (snap: DataSnapshot in snapData) {
                                val spent = snap.getValue(Spend::class.java)
                                spent?.amount?.let {
                                    spentAmount = (spentAmount + it.toInt())
                                }
                            }
                        }
                        continution.resume(Pair(true, spentAmount.toString()))
                    }

                    override fun onCancelled(error: DatabaseError) {
                        continution.resume(Pair(false, error.message))
                    }

                })
        }
    }

    fun addReachMoney(amount: String) {
        if (amount.isEmpty()) {
            _salaryInput.value = true
            return
        }
        _stateAddReach.value = DataState(isLoading = true)
        job = viewModelScope.launch {
            generalGeneralPrefsStoreImpl.getID().onEach {
                databaseReference.child(it)
                    .child("reachAmount")
                    .setValue(amount)
                    .addOnFailureListener {
                        _stateAddReach.value = DataState(error = it.message.toString())
                    }.addOnSuccessListener {
                        _stateAddReach.value = DataState(data = true)
                    }
            }.launchIn(viewModelScope)
        }
    }

    fun logout() {
        viewModelScope.launch {
            generalGeneralPrefsStoreImpl.clearData()
        }
    }

    fun resetState() {
        _state.value = DataState()
        _stateAddReach.value = DataState()
        _salaryInput.value = false
        job?.cancel()
    }


}