package com.lesliedev.bookybloom.Administrador

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.lesliedev.bookybloom.MainActivity
import com.lesliedev.bookybloom.R
import com.lesliedev.bookybloom.databinding.ActivityLoginAdminBinding

class Login_Admin : AppCompatActivity() {

    private lateinit var binding : ActivityLoginAdminBinding
    private lateinit var firebaseAuth : FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favor")
        progressDialog.setCanceledOnTouchOutside(false)

        //Boton para redirigirnos a la actividad anterior
        binding.IbRegresar.setOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }

        //Boton para llamar a la funcion ValidarInformacion
        binding.BtnLoginAdmin.setOnClickListener{
            ValidarInformacion()
        }

    }

    private var email = ""
    private var password = ""

    private fun ValidarInformacion() {
        email = binding.EtEmailAdmin.text.toString().trim()
        password = binding.EtPasswordAdmin.text.toString().trim()

        if (email.isEmpty()){
            binding.EtEmailAdmin.error = "Ingrese su correo"
            binding.EtEmailAdmin.requestFocus()
        }

        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.EtEmailAdmin.error = "Correo inválido"
            binding.EtEmailAdmin.requestFocus()
        }

        else if (password.isEmpty()){
            binding.EtPasswordAdmin.error = "Ingrese la contraseña"
            binding.EtPasswordAdmin.requestFocus()
        }
        else{
            Login_Administrador()
        }
    }

    private fun Login_Administrador(){
        progressDialog.setMessage("Iniciando sesión")
        progressDialog.show()

        firebaseAuth.signInWithEmailAndPassword(email, password)
        //Caso exitoso
            .addOnSuccessListener {
                progressDialog.dismiss()
                startActivity(Intent(this@Login_Admin, MainActivity::class.java))
                finishAffinity()

            }
        //Caso fallido
            .addOnFailureListener{ e->
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "No se ha podido iniciar sesión debido a ${e.message}",
                    Toast.LENGTH_SHORT).show()

            }
    }
}