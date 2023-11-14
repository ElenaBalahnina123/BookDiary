package com.elena_balakhnina.bookdiary.authentication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GoogleAuthViewModel @Inject constructor(
    private val repository: GoogleAuthRepository
) : ViewModel() {

    private val _signInFlow = MutableStateFlow<FirebaseUser?>(null)
    val signInFlow: StateFlow<FirebaseUser?> = _signInFlow

    private val _signupFlow = MutableStateFlow<FirebaseUser?>(null)
    val signupFlow: StateFlow<FirebaseUser?> = _signupFlow

    val currentUser: FirebaseUser?
        get() = repository.currentUser

    init {
        if (repository.currentUser != null) {
            _signInFlow.value = repository.currentUser
        }
    }

    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            val result = repository.signInWithEmailAndPassword(email, password)
            _signInFlow.value = result
        }
    }


    fun signupUser(name: String, email: String, password: String) = viewModelScope.launch {
        val result = repository.signUpWithEmailAndPassword(name, email, password)
        _signupFlow.value = result
    }

    fun logout() {
        repository.signOut()
        _signInFlow.value = null
        _signupFlow.value = null
    }


}