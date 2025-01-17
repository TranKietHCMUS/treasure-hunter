package com.example.treasurehunter.data.model

import com.google.firebase.firestore.PropertyName
import java.util.Date

enum class Gender {
    MALE,
    FEMALE,
    OTHER;

    companion object {
        fun fromString(value: String?): Gender {
            return try {
                value?.let { valueOf(it) } ?: MALE
            } catch (e: Exception) {
                MALE
            }
        }
    }
}

data class User(
    @get:PropertyName("uid")
    @set:PropertyName("uid")
    var uid: String = "",

    @get:PropertyName("fullName")
    @set:PropertyName("fullName")
    var fullName: String = "",

    @get:PropertyName("gender")
    @set:PropertyName("gender")
    var gender: Gender = Gender.MALE,

    @get:PropertyName("dob")
    @set:PropertyName("dob")
    var dob: Date = Date(),

    @get:PropertyName("highestScore")
    @set:PropertyName("highestScore")
    var highestScore: Int = 0
) {
    // Constructor không tham số cho Firestore
    constructor() : this("", "", Gender.MALE, Date(), 0)
}