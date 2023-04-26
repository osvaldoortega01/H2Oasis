package com.example.h2oasis

import android.os.StrictMode
import android.util.Log
import java.lang.Exception
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class SQLConnection {
    private val ip="h2oasis.database.windows.net"
    // private val ip = "192.168.1.23"
    private val port="1433"
    private val db="db_h2oasis"
    private val username="h2oasis@h2oasis"
    // private val username= "test"

    private val password="diGuI7Gir77#"
    //private val password = "123"

    fun dbConn(): Connection? {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        var conn: Connection? = null
        val connString : String
        try{
            Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance()
            connString = "jdbc:jtds:sqlserver://" + ip + ":"+port+"/"+db+";" +
                    // Si deseas hacer una conexión local debes de comentar el siguiente connString
            "encrypt=true;trustServerCertificate=false;loginTimeout=30;ssl=request;"

            conn = DriverManager.getConnection(connString, username, password)
        } catch (ex: SQLException){
            Log.e("Error: ", ex.message!!)
        } catch (ex1: ClassNotFoundException)
        {
            Log.e("Error: ", ex1.message!!)
        } catch (ex2: Exception){
            Log.e("Error: ", ex2.message!!)
        }
        return conn
    }
}