package com.demo.moneymanagement.presentation.screens.auth.signup

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.demo.moneymanagement.data.RegistrarRequest
import com.demo.moneymanagement.presentation.DataState
import com.google.firebase.database.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import javax.inject.Inject

@HiltViewModel
class SignupViewModel @Inject constructor() :
    ViewModel() {
    private var databaseReference: DatabaseReference =
        FirebaseDatabase.getInstance().getReference("users")

    private var job: Job? = null
    private val _state = mutableStateOf(DataState<Boolean>())
    val state: State<DataState<Boolean>> = _state

    private val _errorTermsConditions = mutableStateOf(false)
    val errorTermsConditions: State<Boolean> = _errorTermsConditions


    private val _errorEmail = mutableStateOf(false)
    val errorEmail: State<Boolean> = _errorEmail

    private val _errorPassword = mutableStateOf(false)
    val errorPassword: State<Boolean> = _errorPassword

    private val _errorConfirmPassword = mutableStateOf(false)
    val errorConfirmPassword: State<Boolean> = _errorConfirmPassword

    private val _usernameInput = mutableStateOf(false)
    val usernameInput: State<Boolean> = _usernameInput

    private val _salaryInput = mutableStateOf(false)
    val salaryInput: State<Boolean> = _salaryInput

    fun signup(
        email: String,
        password: String,
        confirmPassword: String,
        username: String,
        salary: String,
    ) {
        _state.value = DataState()
        isValidRequest(email, password, confirmPassword, username, salary)?.let {
            job?.cancel()
            _state.value = DataState(isLoading = true)
            val userID = databaseReference.push().key
            checkUserExist(username, email, userExit = {
                _state.value = DataState(error = "user Alard Register with username or email!")

            }, userNotExit = {
                databaseReference.child(userID.toString())
                    .setValue(it.copy(id = userID))
                    .addOnFailureListener {
                        _state.value = DataState(error = it.message ?: "Something went wrong!")
                    }.addOnSuccessListener {
                        _state.value = DataState(data = true)
                    }
            }) {
                _state.value = DataState(error = it)

            }

        }
    }

    private fun checkUserExist(
        username: String, email: String,
        userNotExit: () -> Unit,
        userExit: () -> Unit,
        error: (String) -> Unit
    ) {
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (userSnap: DataSnapshot in snapshot.children) {
                    val user = userSnap.getValue(RegistrarRequest::class.java)
                    user?.let {
                        if (it.email == email || it.username == username) {
                            userExit()
                            return
                        } else {
                            userNotExit()
                            return
                        }
                    }
                    userNotExit()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                error(error.message)
            }

        })
    }

    private fun isValidRequest(
        email: String,
        password: String,
        confirmPassword: String,

        username: String,
        salary: String
    ): RegistrarRequest? {
        return if (email.isEmpty() || password.isEmpty() ||
            username.isEmpty() || salary.isEmpty() ||
            confirmPassword.isEmpty()
        ) {

            _errorEmail.value = email.isEmpty()
            _errorPassword.value = password.isEmpty()
            _usernameInput.value = username.isEmpty()
            _errorConfirmPassword.value = confirmPassword.isEmpty()
            _salaryInput.value = salary.isEmpty()
            null
        } else if (confirmPassword != password) {
            _errorConfirmPassword.value = true
            _errorPassword.value = true

            null
        } else {
            _errorEmail.value = false
            _errorPassword.value = false
            _usernameInput.value = false
            _errorConfirmPassword.value = false
            _salaryInput.value = false
            RegistrarRequest(
                password = password,
                salary = salary,
                username = username,
                email = email,
            )
        }
    }

//    fun saveData(token: String, id:Int) {
//        settings[Constants.USER_ID_KEY]=id
//        TOKEN=token
//        settings[TOKEN_KEY] = token
//    }

    fun resetState() {
        _state.value = DataState()
        job?.cancel()
    }


}