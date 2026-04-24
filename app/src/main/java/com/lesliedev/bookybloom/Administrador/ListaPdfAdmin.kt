package com.lesliedev.bookybloom.Administrador

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.lesliedev.bookybloom.Modelos.ModeloPdf
import com.lesliedev.bookybloom.databinding.ActivityListaPdfAdminBinding

class ListaPdfAdmin : AppCompatActivity() {
    private lateinit var binding: ActivityListaPdfAdminBinding

    private var idCategoria = ""
    private var tituloCategoria = ""

    private lateinit var pdfArrayList : ArrayList<ModeloPdf>
    private lateinit var adaptadorPdfAdmin : AdaptadorPdfAdmin

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListaPdfAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //obtenemos el intent que estamos pasando desde el adapatador
        val intent = intent
        idCategoria = intent.getStringExtra("idCategoria")!!
        tituloCategoria = intent.getStringExtra("tituloCategoria")!!

        //categoria del libro del layout
        binding.TxtCategoriaLibro.text = tituloCategoria

        //Regresar a la actividad anterior
        binding.IbRegresar.setOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }

        ListarLibros()

        binding.EtBuscarLibroAdmin.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(libro: CharSequence?, start: Int, before: Int, count: Int) {
                try {
                    adaptadorPdfAdmin.filter.filter(libro)

                }catch (e:Exception){

                }
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

    }

    //Funcion para visualizar los libros
    private fun ListarLibros() {
        pdfArrayList = ArrayList()

        //Crear referencia a la DB
        val ref = FirebaseDatabase.getInstance().getReference("Libros")
        //el id de la bd se compara con el id de la categoria que el admin ha seleccionado
        ref.orderByChild("categoria").equalTo(idCategoria)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    pdfArrayList.clear()
                    for(ds in snapshot.children){

                        //Cada informacion de cada libro lo adaptara al ModeloPdf
                        val modelo = ds.getValue(ModeloPdf::class.java)

                        //Agregar un item a la lista
                        if(modelo !=null){
                            pdfArrayList.add(modelo)
                        }
                    }
                    //Configurar el adaptador
                    adaptadorPdfAdmin = AdaptadorPdfAdmin(this@ListaPdfAdmin, pdfArrayList)
                    binding.RvLibrosAdmin.adapter = adaptadorPdfAdmin
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })

    }
}