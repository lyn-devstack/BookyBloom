package com.lesliedev.bookybloom.Cliente

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.PathEffect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ReportFragment.Companion.reportFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.lesliedev.bookybloom.MainActivityCliente
import com.lesliedev.bookybloom.R
import com.lesliedev.bookybloom.databinding.ActivityRegistroClienteBinding

class Registro_Cliente : AppCompatActivity() {

    private lateinit var binding : ActivityRegistroClienteBinding

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var progresDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroClienteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        progresDialog = ProgressDialog(this)
        progresDialog.setTitle("Espere por favor")
        progresDialog.setCanceledOnTouchOutside(false)

        //Eventos
        binding.IbRegresar.setOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }

        binding.BtnRegistrarCl.setOnClickListener{
            validarInformacion()
        }

        binding.TxtTengoCuenta.setOnClickListener {
            startActivity(Intent(this@Registro_Cliente, Login_Cliente::class.java))
        }
    }

    //Variables para almacenar lo que inserte el usuario
    var nombre = ""
    var apellidos = ""
    var edad = ""
    var email = ""
    var password = ""
    var r_password = ""

    private fun validarInformacion() {
        //Obtener datos
        nombre = binding.EtNombreCl.text.toString().trim()
        apellidos = binding.EtApellidosCl.text.toString().trim()
        edad = binding.EtEdadCl.text.toString().trim()
        email = binding.EtEmailCl.text.toString().trim()
        password = binding.EtPasswordCl.text.toString().trim()
        r_password = binding.EtRPasswordCl.text.toString().trim()

        //Comprobaciones para que los datos sean ingresados correctamente
        if(nombre.isEmpty()){
            binding.EtNombreCl.error = "Ingrese nombre"
            binding.EtNombreCl.requestFocus()

        }else if (apellidos.isEmpty()){
            binding.EtApellidosCl.error = "Ingrese apellido"
            binding.EtApellidosCl.requestFocus()

        }else if (edad.isEmpty()){
            binding.EtEdadCl.error = "Ingrese edad"
            binding.EtEdadCl.requestFocus()

        }else if (email.isEmpty()){
            binding.EtEmailCl.error = "Ingrese email"
            binding.EtEmailCl.requestFocus()

        }else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.EtEmailCl.error = "Correo no válido"
            binding.EtEmailCl.requestFocus()

        }else if (password.isEmpty()){
            binding.EtPasswordCl.error = "Ingrese contraseña"
            binding.EtPasswordCl.requestFocus()

        }else if (password.length<6){
            binding.EtPasswordCl.error = "La contraseña debe tener más de 6 caracteres"
            binding.EtPasswordCl.requestFocus()

        }else if (r_password.isEmpty()){
            binding.EtRPasswordCl.error = "Confirme su contraseña"
            binding.EtRPasswordCl.requestFocus()

        }else if (r_password !=password){
            binding.EtRPasswordCl.error = "Las contraseñas no coinciden"
            binding.EtRPasswordCl.requestFocus()

        }else{
            crearCuentaCliente(email, password)
        }
    }

    private fun crearCuentaCliente(email: String, password: String) {
        progresDialog.setMessage("Creando cuenta...")
        progresDialog.show()

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                agregarInforDb()

            }
            .addOnFailureListener{e->
                progresDialog.dismiss()
                Toast.makeText(applicationContext, "El registro ha fallado. Error: ${e.message}",
                    Toast.LENGTH_SHORT).show()
            }
    }

    private fun agregarInforDb() {
        progresDialog.setMessage("La información se está guardando...")

        val tiempo = System.currentTimeMillis()

        //usuario actual del registro
        val uid = firebaseAuth.uid!!

        //Configurar datos que agregaremos a ese usuario
        val datos_cliente : HashMap<String, Any> = HashMap()

        datos_cliente["uid"] = uid
        datos_cliente["nombre"] = nombre
        datos_cliente["apellidos"] = apellidos
        datos_cliente["edad"] = edad
        datos_cliente["email"] = email
        datos_cliente["rol"] = "cliente"
        datos_cliente["tiempo_registro"] = tiempo
        datos_cliente["imagen"] = ""

        //Crear referencia a la DB
        val reference = FirebaseDatabase.getInstance().getReference("Usuarios")
        reference.child(uid)
            .setValue(datos_cliente)
            .addOnSuccessListener {
                progresDialog.dismiss()
                Toast.makeText(applicationContext, "Bienvenido a BookyBloom, cuenta creada.",
                    Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivityCliente::class.java))
                finishAffinity()
            }
            .addOnFailureListener{e->
                progresDialog.dismiss()
                Toast.makeText(applicationContext, "El registro ha fallado. Error: ${e.message}",
                    Toast.LENGTH_SHORT).show()
            }



    }
}