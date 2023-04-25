package com.example.h2oasis

import android.content.Context

class Prefs(val context: Context) {

    val SHAREDNAME = "MyDTB"
    val SHAREDUSER = "username"
    val SHAREDID = "id"
    val SHAREDPASSWORD = "password"
    val SHAREDUSERLONGNAME = "userLongName"

    val storage = context.getSharedPreferences(SHAREDNAME, 0)

    fun saveUsername(username:String){
        storage.edit().putString(SHAREDUSER, username).apply()
    }
    fun savePassword(password:String){
        storage.edit().putString(SHAREDPASSWORD, password).apply()
    }
    fun saveUserLongName(userLongName: String){
        storage.edit().putString(SHAREDUSERLONGNAME, userLongName).apply()
    }
    fun saveId(id:String){
        storage.edit().putString(SHAREDID, id).apply()
    }
    fun getUsername():String{
        return storage.getString(SHAREDUSER, "")!!
    }
    fun getPassword():String{
        return storage.getString(SHAREDPASSWORD, "")!!
    }
    fun getUserLongName(): String{
        return storage.getString(SHAREDUSERLONGNAME, "")!!
    }
    fun getId():String{
        return storage.getString(SHAREDID, "")!!
    }
    fun wipe(){
        storage.edit().clear().apply()
    }
}