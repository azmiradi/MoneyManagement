package com.demo.moneymanagement.presentation.screens.auth.login

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demo.moneymanagement.data.Constants
import com.demo.moneymanagement.data.LoginRequest
import com.demo.moneymanagement.data.RegistrarRequest
import com.demo.moneymanagement.presentation.DataState
import com.demo.preferences.general.GeneralGeneralPrefsStoreImpl
import com.google.firebase.database.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val generalGeneralPrefsStoreImpl: GeneralGeneralPrefsStoreImpl
) : ViewModel() {

    private var databaseReference: DatabaseReference =
        FirebaseDatabase.getInstance().getReference("users")

    private var job: Job? = null
    private val _state = mutableStateOf(DataState<Boolean>())
    val state: State<DataState<Boolean>> = _state


    private val _errorUsername = mutableStateOf(false)
    val errorUsername: State<Boolean> = _errorUsername

    private val _errorPassword = mutableStateOf(false)
    val errorPassword: State<Boolean> = _errorPassword


    fun login(username: String, password: String) {
        _state.value = DataState()
        _state.value = DataState(isLoading = true)

        isValidLoginRequest(username, password)?.let {
            checkUserExist(username, password, userExit = {
                saveData(it)
                _state.value = DataState(data = true)
            }, userNotExit = {
                _state.value = DataState(error = "Error Information !")

            }) {
                _state.value = DataState(error = it)

            }
        }
    }

    private fun checkUserExist(
        username: String,
        password: String,
        userNotExit: () -> Unit,
        userExit: (String) -> Unit,
        error: (String) -> Unit
    ) {
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (userSnap: DataSnapshot in snapshot.children) {
                    val user = userSnap.getValue(RegistrarRequest::class.java)
                    user?.let {
                        if (it.username == username && it.password == password) {
                            userExit(it.id.toString())
                            return
                        }
                    }
                }
                userNotExit()
            }

            override fun onCancelled(error: DatabaseError) {
                error(error.message)
            }

        })
    }

    private fun isValidLoginRequest(email: String, password: String): LoginRequest? {
        return if (email.isEmpty() || password.isEmpty()) {
            _errorUsername.value = email.isEmpty()
            _errorPassword.value = password.isEmpty()
            null
        } else
            LoginRequest(username = email, password = password)
    }

    private fun saveData(id: String) {
        Constants.UserID=id
        viewModelScope.launch {
            generalGeneralPrefsStoreImpl.saveID(id)
        }
    }

    fun resetState() {
        _state.value = DataState()
        _errorUsername.value=false
        _errorPassword.value=false
        job?.cancel()
    }


}