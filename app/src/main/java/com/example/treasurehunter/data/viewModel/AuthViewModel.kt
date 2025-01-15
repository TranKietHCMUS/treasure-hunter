package com.example.app.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.treasurehunter.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    companion object {
        private val auth: FirebaseAuth = FirebaseAuth.getInstance()
        private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

        private val _currentUser = MutableStateFlow<User?>(null)
        val currentUser: StateFlow<User?> = _currentUser

        const val USERS_COLLECTION = "users"

        fun registerUser(
            email: String,
            password: String,
            fullName: String,
            gender: String,
            birthDate: String,
            onSuccess: () -> Unit,
            onError: (String) -> Unit
        ) {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val uid = auth.currentUser?.uid ?: return@addOnCompleteListener
                        val user = User(uid, fullName, gender, birthDate)

                        db.collection(USERS_COLLECTION).document(uid).set(user)
                            .addOnSuccessListener {
                                _currentUser.value = user
                                onSuccess()
                            }
                            .addOnFailureListener { e ->
                                onError(e.message ?: "An error occurred")
                            }
                    } else {
                        onError(task.exception?.message ?: "Registration failed")
                    }
                }
        }

        fun loginUser(
            email: String,
            password: String,
            onSuccess: () -> Unit,
            onError: (String) -> Unit
        ) {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val uid = auth.currentUser?.uid ?: return@addOnCompleteListener
                        db.collection(USERS_COLLECTION).document(uid).get()
                            .addOnSuccessListener { document ->
                                if (document.exists()) {
                                    val user = document.toObject(User::class.java)
                                    _currentUser.value = user
                                    onSuccess()
                                } else {
                                    onError("User not found")
                                }
                            }
                            .addOnFailureListener { e ->
                                onError(e.message ?: "An error occurred")
                            }
                    } else {
                        onError(task.exception?.message ?: "Login failed")
                    }
                }
        }

        fun logoutUser() {
            // Đăng xuất khỏi Firebase Auth
            auth.signOut()

            // Cập nhật currentUser thành null khi người dùng đăng xuất
            _currentUser.value = null
        }
    }
}
