package com.lesliedev.bookybloom.Administrador

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.lesliedev.bookybloom.MainActivity
import com.lesliedev.bookybloom.R
import com.lesliedev.bookybloom.databinding.ActivityAgregarCategoriaBinding

class Agregar_Categoria : AppCompatActivity() {

    private lateinit var binding : ActivityAgregarCategoriaBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        //Acceso a las vistas de la actividad
        super.onCreate(savedInstanceState)
        binding = ActivityAgregarCategoriaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favor")
        progressDialog.setCanceledOnTouchOutside(false)

        //Eventos
        binding.IbRegresar.setOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }

        binding.AgregarCategoriaDB.setOnClickListener{
            ValidarDatos()
        }
    }

    private var categoria = ""

    private fun ValidarDatos() {
        categoria = binding.EtCategoria.text.toString().trim()
        if(categoria.isEmpty()){
            Toast.makeText(applicationContext, "Ingrese una categoría", Toast.LENGTH_SHORT).show()
        }else{
            Agregar_CategoriaDB()
        }
    }

    private fun Agregar_CategoriaDB() {
        progressDialog.setMessage("Agregando categoría...")
        progressDialog.show()

        val tiempo = System.currentTimeMillis()

        val hashMap = HashMap<String, Any>()
        hashMap["id"] = "$tiempo"
        hashMap["categoria"] = categoria
        hashMap["tiempo"] = tiempo
        hashMap["uid"] = "${firebaseAuth.uid}"

        //Craer referencia a la base de datos
        val ref = FirebaseDatabase.getInstance().getReference("Categorias")
        ref.child("$tiempo")
            .setValue(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "La categoría se agregó a la Base de datos", Toast.LENGTH_SHORT).show()
                binding.EtCategoria.setText("")
                startActivity(Intent(this@Agregar_Categoria, MainActivity::class.java))
                finishAffinity()

            }
            .addOnFailureListener{ e->
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "No se ha podido agregar la categoría. Erro: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}