package com.lesliedev.bookybloom

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Bienvenida : AppCompatActivity() {

    private lateinit var firebaseAuth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bienvenida)
        firebaseAuth = FirebaseAuth.getInstance()
        VerBienvenida()
    }

    fun VerBienvenida(){
        object : CountDownTimer(2000, 1000){
            override fun onTick(millisUntilFinished: Long) {

            }

            override fun onFinish() {
                ComprobarSesion()
            }

        }.start()
    }

    fun ComprobarSesion(){
        val firebaseUser = firebaseAuth.currentUser
        //Si no existe ningun usuario, lo redirigimos a Seleccionar rol
        if(firebaseUser == null){
            startActivity(Intent(this, Seleccionar_rol::class.java))
            finishAffinity()
        }else{
            //Para comprobar la sesion, vamos a la BD a confirmar el usuario actual
            val reference = FirebaseDatabase.getInstance().getReference("Usuarios")
            reference.child(firebaseUser.uid)
                //leer en tiempo real la BD el usuario actual
                .addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                       //obtener el rol
                        val rol = snapshot.child("rol").value
                        //Si el usuario es administrador lo redirije al MainActivity
                        if(rol == "admin"){
                            startActivity(Intent(this@Bienvenida, MainActivity::class.java))
                            finishAffinity()
                        }
                        else if(rol == "cliente"){
                            startActivity(Intent(this@Bienvenida, MainActivityCliente::class.java))
                            finishAffinity()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                })
        }
    }
}