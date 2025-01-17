package com.example.app.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.treasurehunter.data.model.Gender
import com.example.treasurehunter.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Date

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
            gender: Gender,
            dob: Date,
            onSuccess: () -> Unit,
            onError: (String) -> Unit
        ) {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val uid = auth.currentUser?.uid ?: return@addOnCompleteListener
                        val userData = hashMapOf(
                            "uid" to uid,
                            "fullName" to fullName,
                            "gender" to gender.name,
                            "dob" to dob,
                            "highestScore" to 0
                        )

                        db.collection(USERS_COLLECTION).document(uid).set(userData)
                            .addOnSuccessListener {
                                val user = User(uid, fullName, gender, dob)
                                _currentUser.value = user
                                onSuccess()
                            }
                            .addOnFailureListener { e ->
                                onError(e.message ?: "Đã xảy ra lỗi khi tạo tài khoản")
                            }
                    } else {
                        onError(task.exception?.message ?: "Đăng ký thất bại")
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
                                    try {
                                        val data = document.data
                                        if (data != null) {
                                            val user = User(
                                                uid = data["uid"] as? String ?: "",
                                                fullName = data["fullName"] as? String ?: "",
                                                gender = Gender.fromString(data["gender"] as? String),
                                                dob = (data["dob"] as? Date) ?: Date(),
                                                highestScore = (data["highestScore"] as? Long)?.toInt() ?: 0
                                            )
                                            _currentUser.value = user
                                            onSuccess()
                                        } else {
                                            onError("Dữ liệu người dùng không hợp lệ")
                                        }
                                    } catch (e: Exception) {
                                        onError("Lỗi khi xử lý dữ liệu người dùng: ${e.message}")
                                    }
                                } else {
                                    onError("Không tìm thấy thông tin người dùng")
                                }
                            }
                            .addOnFailureListener { e ->
                                onError(e.message ?: "Đã xảy ra lỗi khi đăng nhập")
                            }
                    } else {
                        onError(task.exception?.message ?: "Đăng nhập thất bại")
                    }
                }
        }

        fun updateHighestScore(
            newScore: Int,
            onSuccess: () -> Unit,
            onError: (String) -> Unit
        ) {
            val currentUid = auth.currentUser?.uid
            if (currentUid == null) {
                onError("Bạn cần đăng nhập để cập nhật điểm")
                return
            }

            val user = _currentUser.value
            if (user == null) {
                onError("Không tìm thấy thông tin người dùng")
                return
            }

            if (newScore <= user.highestScore) {
                onSuccess()
                return
            }

            db.collection(USERS_COLLECTION).document(currentUid)
                .update("highestScore", newScore)
                .addOnSuccessListener {
                    _currentUser.value = user.copy(highestScore = newScore)
                    onSuccess()
                }
                .addOnFailureListener { e ->
                    onError(e.message ?: "Cập nhật điểm cao nhất thất bại")
                }
        }

        fun logoutUser() {
            auth.signOut()
            _currentUser.value = null
        }
    }
}