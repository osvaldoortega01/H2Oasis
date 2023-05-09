package com.example.h2oasis.Models

/**
 * Represents a user in the application with relevant information.
 */
data class Usuario(
    val id: String,
    val completeName: String,
    val username: String,
    val password: String,
    val emailAddress: String,
    val registrationDate: String,
    val enabled: Boolean
)
