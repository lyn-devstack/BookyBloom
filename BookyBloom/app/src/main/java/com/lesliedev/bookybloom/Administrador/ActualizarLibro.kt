package com.lesliedev.bookybloom.Administrador

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.lesliedev.bookybloom.R
import com.lesliedev.bookybloom.databinding.ActivityActualizarLibroBinding

class ActualizarLibro : AppCompatActivity() {

    private lateinit var binding : ActivityActualizarLibroBinding

    private var idLibro = ""

    private lateinit var progressDialog: ProgressDialog

    //Titulos
    private lateinit var categoriaTituloArrayList :ArrayList<String>
    //Id de las categprias
    private lateinit var categoriaIdArrayList: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityActualizarLibroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        idLibro = intent.getStringExtra("idLibro")!!

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favor")
        progressDialog.setCanceledOnTouchOutside(false)

        cargarCategorias()
        cargarInformacion()

        binding.IbRegresar.setOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }

        binding.TvCategoriaLibro.setOnClickListener {
            dialogCategoria()
        }

        binding.BtnActualizarLibro.setOnClickListener{
            validarInformacion()
        }
    }

    //Funcion para cargar la informacion que tenia previamente antes de actualizarla
    private fun cargarInformacion() {
        val ref = FirebaseDatabase.getInstance().getReference("Libros")
        ref.child(idLibro)
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    //Obtener informacion en tiempo real del libro seleccionado
                    val titulo = snapshot.child("titulo").value.toString()
                    val descripcion = snapshot.child("descripcion").value.toString()
                    id_seleccionado = snapshot.child("categoria").value.toString()

                    //Modificamos en las vistas
                    binding.EtTituloLibro.setText(titulo)
                    binding.EtDescripcionLibro.setText(descripcion)

                    val refCategoria = FirebaseDatabase.getInstance().getReference("Categorias")
                    refCategoria.child(id_seleccionado)
                        .addListenerForSingleValueEvent(object : ValueEventListener{
                            override fun onDataChange(snapshot: DataSnapshot) {

                                //Obtener categoria
                                val categoria = snapshot.child("categoria").value

                                //Modificar en el TextView
                                binding.TvCategoriaLibro.text = categoria.toString()
                            }

                            override fun onCancelled(error: DatabaseError) {
                                TODO("Not yet implemented")
                            }
                        })
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }


    private var titulo = ""
    private var descripcion = ""
    private fun validarInformacion() {
        //obtener los datos ingresados
        titulo = binding.EtTituloLibro.text.toString().trim()
        descripcion = binding.EtDescripcionLibro.text.toString().trim()

        if (titulo.isEmpty()){
            Toast.makeText(this, "Ingrese título", Toast.LENGTH_SHORT).show()
        }
        else if (descripcion.isEmpty()){
            Toast.makeText(this, "Ingrese descripción", Toast.LENGTH_SHORT).show()
        }
        else if (id_seleccionado.isEmpty()){
            Toast.makeText(this, "Seleccione una categoria", Toast.LENGTH_SHORT).show()
        }else{
            actualizarInformacion()
        }
    }

    private fun actualizarInformacion() {
        progressDialog.setMessage("Actualizando información")
        progressDialog.show()
        val hashMap = HashMap<String, Any>()
        hashMap["titulo"] = "$titulo"
        hashMap["descripcion"] = "$descripcion"
        hashMap["categoria"] = "$id_seleccionado"

        //Actualizacion
        val ref = FirebaseDatabase.getInstance().getReference("Libros")
        ref.child(idLibro)
            .updateChildren(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this, "Actualización realizada exitosamente", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener{e->
                progressDialog.dismiss()
                Toast.makeText(this, "Fallo en la actualización. Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private var id_seleccionado = ""
    private var titulo_seleccionado = ""
    private fun dialogCategoria() {
        val categoriaArray = arrayOfNulls<String>(categoriaTituloArrayList.size)
        for (i in categoriaTituloArrayList.indices){
            categoriaArray[i] = categoriaTituloArrayList[i]
        }

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Seleccione una categoría")
            .setItems(categoriaArray){dialog, posicion->
                id_seleccionado = categoriaIdArrayList[posicion]
                titulo_seleccionado = categoriaTituloArrayList[posicion]

                binding.TvCategoriaLibro.text = titulo_seleccionado
            }
            .show()
    }

    private fun cargarCategorias() {
        categoriaTituloArrayList = ArrayList()
        categoriaIdArrayList = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("Categorias")
        ref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                categoriaTituloArrayList.clear()
                categoriaIdArrayList.clear()

                for(ds in snapshot.children){
                    val id = ""+ds.child("id").value
                    val categoria = ""+ds.child("categoria").value

                    categoriaTituloArrayList.add(categoria)
                    categoriaIdArrayList.add(id)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
}