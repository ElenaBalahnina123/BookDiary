package com.elena_balakhnina.bookdiary.authentication

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resumeWithException

interface GoogleAuthRepository {
    suspend fun signInWithEmailAndPassword(email: String, password: String): FirebaseUser?

    suspend fun signUpWithEmailAndPassword(name: String, email: String, password: String): FirebaseUser

    val currentUser: FirebaseUser?

    fun signOut()

}

class GoogleAuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,

    ) : GoogleAuthRepository {


    override suspend fun signInWithEmailAndPassword(email: String, password: String): FirebaseUser? {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("OLOLO", "signInWithEmail:success")
                firebaseAuth.currentUser
                Log.d("OLOLO", firebaseAuth.currentUser?.email.toString())
            } else {
                Log.w("OLOLO", "signInWithEmail:failure", task.exception)

            }
        }
        return firebaseAuth.currentUser
    }

    override suspend fun signUpWithEmailAndPassword(
        name: String,
        email: String,
        password: String
    ): FirebaseUser {
        val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        result.user?.updateProfile(UserProfileChangeRequest.Builder().setDisplayName(name).build())?.await()
        return result.user!!

    }

    override val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser

    override fun signOut() {
        firebaseAuth.signOut()
    }

}

@InstallIn(SingletonComponent::class)
@Module
class GoogleAuthModule {
    @Provides
    fun providesAuthRepository(impl: GoogleAuthRepositoryImpl): GoogleAuthRepository = impl

    @Provides
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()
}


@OptIn(ExperimentalCoroutinesApi::class)
suspend fun <T> Task<T>.await(): T {
    return suspendCancellableCoroutine { cont ->
        addOnCompleteListener {
            if (it.exception != null) {
                cont.resumeWithException(it.exception!!)
            } else {
                cont.resume(it.result, null)
            }
        }
    }
}


