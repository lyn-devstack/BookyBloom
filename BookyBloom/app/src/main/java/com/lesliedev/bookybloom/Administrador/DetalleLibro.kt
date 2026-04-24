package com.lesliedev.bookybloom.Administrador

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.lesliedev.bookybloom.LeerLibro
import com.lesliedev.bookybloom.R
import com.lesliedev.bookybloom.databinding.ActivityDetalleLibroBinding

class DetalleLibro : AppCompatActivity() {

    private lateinit var binding : ActivityDetalleLibroBinding
    private var idLibro = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalleLibroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        idLibro = intent.getStringExtra("idLibro")!!

        binding.IbRegresar.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.BtnLeerLibro.setOnClickListener{
            val intent = Intent(this@DetalleLibro, LeerLibro::class.java)
            intent.putExtra("idLibro", idLibro)
            startActivity(intent)
        }

        cargarDetalleLibro()
    }

    private fun cargarDetalleLibro() {
        //Referencia a la base de datos libros
        val ref = FirebaseDatabase.getInstance().getReference("Libros")
        ref.child(idLibro)
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    //Obtener la informacion del libro mediante el id
                    val categoria = "${snapshot.child("categoria").value}"
                    val contadorDescargas = "${snapshot.child("contadorDescargas").value}"
                    val contadorVistas = "${snapshot.child("contadorVistas").value}"
                    val descripcion = "${snapshot.child("descripcion").value}"
                    val tiempo = "${snapshot.child("tiempo").value}"
                    val titulo = "${snapshot.child("titulo").value}"
                    val url = "${snapshot.child("url").value}"

                    //Cambiar formato tiempo
                    val fecha = MisFunciones.formatoTiempo(tiempo.toLong())

                    //Cargar categoria libro
                    MisFunciones.CargarCategoria(categoria, binding.categoriaD)

                    //Cargar imagen del libro, contador de pag
                    MisFunciones.CargarPdfUrl("$url", "$titulo", binding.VisualizadorPDF, binding.progressBar,
                        binding.paginasD)

                    //Cargar peso del libro
                    MisFunciones.CargarPesoPdf("$url", "$titulo", binding.pesoD)

                    //Configurar informacion restante
                    binding.tituloLibroD.text = titulo
                    binding.descripcionD.text = descripcion
                    binding.vistasD.text = contadorVistas
                    binding.descargasD.text = contadorDescargas
                    binding.fechaD.text = fecha
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }
}