package com.lesliedev.bookybloom.Cliente

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.lesliedev.bookybloom.MainActivityCliente
import com.lesliedev.bookybloom.R
import com.lesliedev.bookybloom.databinding.ActivityLoginClienteBinding

class Login_Cliente : AppCompatActivity() {

    private lateinit var binding : ActivityLoginClienteBinding

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var progressDialog: ProgressDialog

    private  lateinit var mGoogleSignInClient : GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginClienteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favor")
        progressDialog.setCanceledOnTouchOutside(false)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        //Regresar a la actividad anterior
        binding.IbRegresar.setOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }

        //Evento al boton
        binding.BtnLoginCliente.setOnClickListener {
            validarInformacion()
        }

        //Btn Google -->
        binding.BtnLoginGoogle.setOnClickListener {
            iniciarSesionGoogle()
        }
    }

    private fun iniciarSesionGoogle() {
        val googleSignIntent = mGoogleSignInClient.signInIntent
        googleSignInARL.launch(googleSignIntent)
    }

    private val googleSignInARL = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()){resultado->
        if (resultado.resultCode == RESULT_OK){
            val data = resultado.data
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val cuenta = task.getResult(ApiException::class.java)

                autenticarGoogleFirebase(cuenta.idToken)

            }catch (e:Exception){
                Toast.makeText(applicationContext, "${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
        else{
            Toast.makeText(applicationContext, "Cancelado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun autenticarGoogleFirebase(idToken: String?) {
        val credencial = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credencial)
            .addOnSuccessListener {authResult->
                //si el usuario es nuevo
                if (authResult.additionalUserInfo!!.isNewUser){
                    GuardarInformacionDB()
                }else{
                    startActivity(Intent(this, MainActivityCliente::class.java))
                    finishAffinity()
                }
            }
            .addOnFailureListener {e->
                Toast.makeText(applicationContext, "${e.message}", Toast.LENGTH_SHORT).show()

            }
    }

    private fun GuardarInformacionDB() {
        progressDialog.setMessage("Su información se esta registrando...")
        progressDialog.show()

        //Obtener info de cuenta de Google
        val uidGoogle = firebaseAuth.uid
        val emailGoogle = firebaseAuth.currentUser?.email
        val nombreGoogle = firebaseAuth.currentUser?.displayName


        //Pasar a string el nombre de usuario
        val nombre_usuario_google = nombreGoogle.toString()

        //Obtener el tiempo
        val tiempo = System.currentTimeMillis()

        val datos_cliente = HashMap<String, Any?> ()
        datos_cliente["uid"] = uidGoogle
        datos_cliente["nombre"] = nombre_usuario_google
        datos_cliente["apellidos"] = ""
        datos_cliente["email"] = emailGoogle
        datos_cliente["edad"] = ""
        datos_cliente["tiempo_registro"] = tiempo
        datos_cliente["imagen"] = ""
        datos_cliente["rol"] = "cliente"

        //referencia a la BD
        val reference = FirebaseDatabase.getInstance().getReference("Usuarios")
        reference.child(uidGoogle!!)
            .setValue(datos_cliente)
            .addOnSuccessListener {
                progressDialog.dismiss()
                startActivity(Intent(applicationContext, MainActivityCliente::class.java))
                Toast.makeText(applicationContext, "Bienvenido a BookyBloom, registro exitoso.", Toast.LENGTH_SHORT).show()
                finishAffinity()
            }
            .addOnFailureListener {e->
                Toast.makeText(applicationContext, "${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private var email = ""
    private var password = ""
    private fun validarInformacion() {
        //Obtener los permisos
        email = binding.EtEmailCl.text.toString().trim()
        password = binding.EtPasswordCl.text.toString().trim()

        //Validar
        if (email.isEmpty()){
            binding.EtEmailCl.error = "Ingrese correo"
            binding.EtEmailCl.requestFocus()
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.EtEmailCl.error = "Correo no válido"
            binding.EtEmailCl.requestFocus()
        }
        else if (password.isEmpty()){
            binding.EtPasswordCl.error = "Ingrese contraseña"
            binding.EtPasswordCl.requestFocus()

        }else{
            loginCliente()
        }
    }

    private fun loginCliente() {
        progressDialog.setMessage("Iniciando sesión...")
        progressDialog.show()

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                progressDialog.dismiss()
                startActivity(Intent(this@Login_Cliente, MainActivityCliente::class.java))
                finishAffinity()
            }
            .addOnFailureListener{e->
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "No se ha podido iniciar sesión. Error: ${e.message}",
                    Toast.LENGTH_SHORT).show()
            }
    }
}