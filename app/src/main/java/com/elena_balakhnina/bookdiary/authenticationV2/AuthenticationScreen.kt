package com.elena_balakhnina.bookdiary.authenticationV2

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.elena_balakhnina.bookdiary.ui.theme.BookDiaryTheme
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.BeginSignInResult
import com.google.android.gms.auth.api.identity.Identity
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthenticationScreenState(
    val email: TextFieldValue,
    val password: TextFieldValue,
    val canAuthenticate: Boolean,
)

class AuthenticationScreenStateProvider : PreviewParameterProvider<AuthenticationScreenState> {
    override val values: Sequence<AuthenticationScreenState>
        get() = sequenceOf(
            AuthenticationScreenState(
                email = TextFieldValue(),
                password = TextFieldValue(),
                canAuthenticate = true,
            )
        )
}

@Composable
fun AuthPreview() {
    val viewModel: AuthVM = viewModel()

    val state by viewModel.uiState.collectAsState()
    AuthenticationScreen(
        state = state,
        onEmailChange = viewModel::onEmailChange,
        onAuthenticateClick = viewModel::onLoginClick,
        onEmailFocus = viewModel::onEmailFocused,
        onPasswordChange = viewModel::onPasswordChange
    )

    val intentSenderLauncher: ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult> = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = viewModel::onIntentSenderResult
    )

    LaunchedEffect(key1 = viewModel) {
        viewModel.eventFlow
            .onEach {
                when(it) {
                    is AuthVmEvent.BeginSignIn -> {
                        intentSenderLauncher.launch(
                            IntentSenderRequest.Builder(it.signInResult.pendingIntent.intentSender)
                                .build()
                        )
                    }
                }
            }
            .collect()
    }
}

@Composable
@Preview
fun AuthenticationScreen(
    @PreviewParameter(AuthenticationScreenStateProvider::class)
    state: AuthenticationScreenState,
    onEmailFocus: () -> Unit = {},
    onEmailChange: (TextFieldValue) -> Unit = {},
    onPasswordChange: (TextFieldValue) -> Unit = {},
    onAuthenticateClick: () -> Unit = {},
) = BookDiaryTheme {
    Scaffold {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(it)
                .fillMaxSize(),
        ) {
            TextField(
                value = state.email,
                onValueChange = onEmailChange,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                ),
                label = {
                    Text(text = "Email")
                },
                modifier = Modifier.onFocusChanged {
                    if (it.isFocused) {
                        onEmailFocus()
                    }
                }
            )
            TextField(
                value = state.password,
                onValueChange = onPasswordChange,
                visualTransformation = PasswordVisualTransformation(),
                label = {
                    Text("Password")
                }
            )
            Button(onClick = onAuthenticateClick) {
                Text(text = "Войти")
            }
        }
    }
}

data class AuthVmState(
    val login: TextFieldValue = TextFieldValue(),
    val password: TextFieldValue = TextFieldValue(),
    val emailOnceFocused: Boolean = false,
)

sealed class AuthVmEvent {
    data class BeginSignIn(val signInResult: BeginSignInResult) : AuthVmEvent()
}

@HiltViewModel
class AuthVM @Inject constructor(
    @ApplicationContext
    private val context: Context,
) : ViewModel() {

    companion object {
        private const val TAG = "AuthVM"
    }

    private val oneTapClient = Identity.getSignInClient(context)

    private val mutableStateFlow = MutableStateFlow(AuthVmState())

    val uiState: StateFlow<AuthenticationScreenState>
        get() = mutableStateFlow.map { mapToUiState(it) }
            .stateIn(
                viewModelScope, SharingStarted.Eagerly, AuthenticationScreenState(
                    TextFieldValue(),
                    TextFieldValue(),
                    false,
                )
            )

    private val mutableEventFlow = MutableSharedFlow<AuthVmEvent>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.SUSPEND,
    )

    val eventFlow get() = mutableEventFlow.asSharedFlow()

    private fun mapToUiState(input: AuthVmState): AuthenticationScreenState {
        return AuthenticationScreenState(
            email = input.login,
            password = input.password,
            canAuthenticate = true,
        )
    }

    fun onEmailChange(textFieldValue: TextFieldValue) {
        mutableStateFlow.update {
            it.copy(
                login = textFieldValue
            )
        }
    }

    fun onPasswordChange(textFieldValue: TextFieldValue) {
        mutableStateFlow.update {
            it.copy(
                password = textFieldValue
            )
        }
    }

    fun onEmailFocused() {
//        val oldFocusState = mutableStateFlow.value.emailOnceFocused
        mutableStateFlow.update {
            it.copy(
                emailOnceFocused = true
            )
        }
//        if(!oldFocusState) {
//            requestOneTapLogin()
//        }
    }

    private fun requestOneTapLogin() {
        val request = BeginSignInRequest.Builder()
            .setPasswordRequestOptions(
                BeginSignInRequest.PasswordRequestOptions.Builder()
                    .setSupported(true)
                    .build()
            )
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId("297755070705-etrbsukdc98hnv5mphp2lbgdb643jkvu.apps.googleusercontent.com")
                    .setFilterByAuthorizedAccounts(true)
                    .build()
            )
            .build()
        oneTapClient.beginSignIn(request)
            .addOnSuccessListener {
                viewModelScope.launch {
                    mutableEventFlow.emit(AuthVmEvent.BeginSignIn(it))
                }
            }
            .addOnFailureListener {
                Log.e("Error","unable to begin sign in",it)
            }
    }

    fun onLoginClick() {
        requestOneTapLogin()
    }

    fun onIntentSenderResult(activityResult: ActivityResult) {
        if(activityResult.resultCode != Activity.RESULT_OK) {
            Log.e(TAG,"wrong result code: ${activityResult.resultCode}")
            return
        }
        val data = activityResult.data ?: kotlin.run {
            Log.e(TAG,"data is null")
            return
        }
        kotlin.runCatching {
            val credential = oneTapClient.getSignInCredentialFromIntent(data)
            val idToken = credential.googleIdToken
            val username = credential.id
            val password = credential.password
            Log.d(TAG, "onIntentSenderResult: username: $username")
            when {
                idToken != null -> {
                    // Got an ID token from Google. Use it to authenticate
                    // with your backend.
                    Log.d(TAG, "Got ID token.")
                }
                password != null -> {
                    // Got a saved username and password. Use them to authenticate
                    // with your backend.
                    Log.d(TAG, "Got password.")
                }
                else -> {
                    // Shouldn't happen.
                    Log.d(TAG, "No ID token or password!")
                }
            }
        }.onFailure {
            Log.e(TAG,"error",it)
        }
    }
}