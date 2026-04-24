package com.lesliedev.bookybloom.Administrador

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.lesliedev.bookybloom.MainActivity
import com.lesliedev.bookybloom.R
import com.lesliedev.bookybloom.databinding.ActivityRegistrarAdminBinding

class Registrar_Admin : AppCompatActivity() {

    private lateinit var binding : ActivityRegistrarAdminBinding

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrarAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favor")
        progressDialog.setCanceledOnTouchOutside(false)

        //Botones
        binding.IbRegresar.setOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }

        binding.BtnRegistrarAdmin.setOnClickListener{
            ValidarInformacion()
        }

        binding.TxtTengoCuenta.setOnClickListener {
            startActivity(Intent(this@Registrar_Admin, Login_Admin::class.java))
        }
    }

    var nombre = ""
    var apellidos = ""
    var email = ""
    var password = ""
    var repetir_password = ""

    private fun ValidarInformacion() {

        //Captura los datos que ingresa el usuario
        nombre = binding.EtNombreAdmin.text.toString().trim()
        apellidos = binding.EtApellidosAdmin.text.toString().trim()
        email = binding.EtEmailAdmin.text.toString().trim()
        password = binding.EtPasswordAdmin.text.toString().trim()
        repetir_password = binding.EtRPasswordAdmin.text.toString().trim()

        if(nombre.isEmpty()){
            binding.EtNombreAdmin.error = "Ingrese su nombre"
            binding.EtNombreAdmin.requestFocus()
        }
        else if (apellidos.isEmpty()){
            binding.EtApellidosAdmin.error = "Ingrese apellidos"
            binding.EtApellidosAdmin.requestFocus()
        }
        else if (email.isEmpty()){
            binding.EtEmailAdmin.error = "Ingrese su email"
            binding.EtEmailAdmin.requestFocus()
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.EtEmailAdmin.error = "Email no es válido"
            binding.EtEmailAdmin.requestFocus()
        }
        else if (password.isEmpty()){
            binding.EtPasswordAdmin.error = "Ingrese contraseña"
            binding.EtPasswordAdmin.requestFocus()
        }
        else if (password.length <6){
            binding.EtPasswordAdmin.error = "La contraseña debe tener más de 6 caracteres."
            binding.EtPasswordAdmin.requestFocus()
        }
        else if (repetir_password.isEmpty()){
            binding.EtRPasswordAdmin.error = "Confirme la contraseña."
            binding.EtRPasswordAdmin.requestFocus()
        }
        else if (password != repetir_password){
            binding.EtRPasswordAdmin.error = "Las contraseñas no coinciden."
            binding.EtRPasswordAdmin.requestFocus()
        }
        else{
            CrearCuentaAdmin(email, password)
        }
    }

    private fun CrearCuentaAdmin(email: String, password: String) {
        progressDialog.setMessage("Creando cuenta")
        progressDialog.show()

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                AgregarInfoDB()
            }
            .addOnFailureListener{ e->
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "La creación de la cuenta ha fallado. Error ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }
    }

    private fun AgregarInfoDB() {
        progressDialog.setMessage("Guardando información...")
        val tiempo = System.currentTimeMillis()
        val uid = firebaseAuth.uid

        val datos_admin : HashMap<String, Any?> = HashMap()
        datos_admin["uid"] = uid
        datos_admin["nombre"] = nombre
        datos_admin["apellidos"] = apellidos
        datos_admin["email"] = email
        datos_admin["rol"] = "admin"
        datos_admin["tiempo_registro"] = tiempo
        datos_admin["imagen"]= " "

        val reference = FirebaseDatabase.getInstance().getReference("Usuarios")
        reference.child(uid!!)
            .setValue(datos_admin)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "Cuenta creada exitosamente.", Toast.LENGTH_SHORT)
                    .show()
                startActivity(Intent(this, MainActivity::class.java))
                finishAffinity()
            }
            .addOnFailureListener{ e->
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "La información no ha sido guardada. Error: ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }
    }
}

