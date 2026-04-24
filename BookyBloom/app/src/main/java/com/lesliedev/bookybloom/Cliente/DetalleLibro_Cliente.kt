package com.lesliedev.bookybloom.Cliente

import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.lesliedev.bookybloom.Administrador.Constantes
import com.lesliedev.bookybloom.Administrador.MisFunciones
import com.lesliedev.bookybloom.LeerLibro
import com.lesliedev.bookybloom.Modelos.ModeloComentario
import com.lesliedev.bookybloom.R
import com.lesliedev.bookybloom.databinding.ActivityDetalleLibroClienteBinding
import com.lesliedev.bookybloom.databinding.DialogAgregarComentarioBinding
import java.io.File
import java.io.FileOutputStream

class DetalleLibro_Cliente : AppCompatActivity() {

    private lateinit var binding: ActivityDetalleLibroClienteBinding
    private var idLibro = ""

    private var tituloLibro = ""
    private var urlLibro = ""

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var progressDialog : ProgressDialog

    private var esFavorito = false

    private lateinit var comentarioArrayList: ArrayList<ModeloComentario>
    private lateinit var adaptadorComentario: AdaptadorComentario

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalleLibroClienteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        idLibro = intent.getStringExtra("idLibro")!!

        //vistas libro
        MisFunciones.incrementarVistas(idLibro)

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Por favor, espere un momento...")
        progressDialog.setCanceledOnTouchOutside(false)

        //instanciar
        firebaseAuth = FirebaseAuth.getInstance()

        //EVENTOS
        binding.IbRegresar.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.BtnLeerLibroC.setOnClickListener{
            val intent = Intent(this@DetalleLibro_Cliente, LeerLibro::class.java)
            intent.putExtra("idLibro", idLibro)
            startActivity(intent)
        }

        binding.BtnDescargarLibroC.setOnClickListener{
            descargarLibro()
        }

        binding.BtnFavoritosLibroC.setOnClickListener{
            if (esFavorito){
                MisFunciones.eliminarFavoritos(this@DetalleLibro_Cliente, idLibro)

            }else{
                agregarFavoritos()
            }
        }

        binding.IbAgregarComentario.setOnClickListener {
            dialogComentar()
        }

        comprobarFavorito()

        cargarDetalleLibro()

        listarComentarios()
    }


    //FUNCIONES
    private fun listarComentarios() {
        comentarioArrayList = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("Libros")
        ref.child(idLibro).child("Comentarios")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    comentarioArrayList.clear()
                    for (ds in snapshot.children){
                        val modelo = ds.getValue(ModeloComentario::class.java)
                        comentarioArrayList.add(modelo!!)
                    }
                    adaptadorComentario = AdaptadorComentario(this@DetalleLibro_Cliente, comentarioArrayList)
                    binding.RvComentarios.adapter = adaptadorComentario
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    private var comentario = ""

    private fun dialogComentar() {
        val add_coment_binding = DialogAgregarComentarioBinding.inflate(LayoutInflater.from(this))

        val builder = AlertDialog.Builder(this)
        builder.setView(add_coment_binding.root)

        val alertDialog = builder.create()
        alertDialog.show()
        // Cuando el usuario presione fuera del dialog, no se cerrará
        alertDialog.setCanceledOnTouchOutside(false)

        // Asignar eventos a las vistas
        add_coment_binding.IbClose.setOnClickListener {
            // Cuando el usuario presione en el ic de close, si se cerrará
            alertDialog.dismiss()
        }
        add_coment_binding.BtnComentar.setOnClickListener {
            // Almacenar en la variable comentario lo que se haya escrito en la vista.
            comentario = add_coment_binding.EtAgregarComentario.text.toString().trim()

            if(comentario.isEmpty()){
                Toast.makeText(applicationContext, "Añadir comentario", Toast.LENGTH_SHORT).show()
            }else{
                alertDialog.dismiss()
                agregarComentario()
            }
        }

    }

    private fun agregarComentario() {
        progressDialog.setMessage("Agregando comentario")
        progressDialog.show()

        val tiempo = "${System.currentTimeMillis()}"

        val hashMap = HashMap<String, Any>()
        hashMap["id"] = "$tiempo"
        hashMap["idLibro"] = "${idLibro}"
        hashMap["tiempo"] = "$tiempo"
        hashMap["comentario"] = "${comentario}"
        hashMap["uid"] = "${firebaseAuth.uid}"

        val ref = FirebaseDatabase.getInstance().getReference("Libros")
        ref.child(idLibro).child("Comentarios").child(tiempo)
            .setValue(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "Comentario publicado", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener{e->
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "${e.message}", Toast.LENGTH_SHORT).show()
            }

    }

    private fun comprobarFavorito() {
        val ref =FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child(firebaseAuth.uid!!).child("Favoritos").child(idLibro)
            .addValueEventListener(object :  ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    //existe o no
                    esFavorito = snapshot.exists()
                    //si existe cambia el icono de favorito o no favorito
                    if (esFavorito){
                        binding.BtnFavoritosLibroC.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            0,
                            R.drawable.ic_agregar_favorito,
                            0,
                            0
                        )

                    }else{
                        binding.BtnFavoritosLibroC.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            0,
                            R.drawable.ic_no_favorito,
                            0,
                            0
                        )
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }

    private fun agregarFavoritos(){
        val tiempo = System.currentTimeMillis()

        //Confirgurar los datos que enviaremos a DB
        val hashMap = HashMap<String, Any>()
        hashMap["id"] = idLibro
        hashMap["tiempo"] = tiempo

        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child(firebaseAuth.uid!!).child("Favoritos").child(idLibro)
            .setValue(hashMap)
            .addOnSuccessListener {
                Toast.makeText(applicationContext, "Agregado a tus favoritos",
                    Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener{e->
                Toast.makeText(applicationContext, "No se ha agregado a favoritos. Error: ${e.message} ",
                    Toast.LENGTH_SHORT).show()

            }
    }

    private fun descargarLibro() {
        progressDialog.setMessage("Descargando libro...")
        progressDialog.show()

        val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(urlLibro)
        storageReference.getBytes(Constantes.Maximo_bytes_pdf)
            .addOnSuccessListener {bytes->

                guardarLibroDispositivo(bytes)
            }
            .addOnFailureListener {e->
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "Oh no! Ha ocurrido un error. ${e.message}", Toast.LENGTH_SHORT).show()
            }

    }

    private fun guardarLibroDispositivo(bytes: ByteArray) {
        val nombreLibro_extension = "$tituloLibro"+System.currentTimeMillis()+".pdf"
        try {
            val carpeta = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            carpeta.mkdirs()

            val archivo_ruta = carpeta.path+"/"+nombreLibro_extension
            val out = FileOutputStream(archivo_ruta)
            out.write(bytes)
            out.close()

            Toast.makeText(applicationContext, "Bien! Tu libro se ha guardado con éxito.", Toast.LENGTH_SHORT).show()
            progressDialog.dismiss()

            incrementarNumDescargas()

        }catch (e:Exception){
            Toast.makeText(applicationContext, "Oh no! Ha ocurrido un error.", Toast.LENGTH_SHORT).show()
            progressDialog.dismiss()
        }
    }

    private fun cargarDetalleLibro() {
        //Referencia a la base de datos libros
        val ref = FirebaseDatabase.getInstance().getReference("Libros")
        ref.child(idLibro)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    //Obtener la informacion del libro mediante el id
                    val categoria = "${snapshot.child("categoria").value}"
                    val contadorDescargas = "${snapshot.child("contadorDescargas").value}"
                    val contadorVistas = "${snapshot.child("contadorVistas").value}"
                    val descripcion = "${snapshot.child("descripcion").value}"
                    val tiempo = "${snapshot.child("tiempo").value}"
                    tituloLibro = "${snapshot.child("titulo").value}"
                    urlLibro = "${snapshot.child("url").value}"

                    //Cambiar formato tiempo
                    val fecha = MisFunciones.formatoTiempo(tiempo.toLong())

                    //Cargar categoria libro
                    MisFunciones.CargarCategoria(categoria, binding.categoriaD)

                    //Cargar imagen del libro, contador de pag
                    MisFunciones.CargarPdfUrl("$urlLibro", "$tituloLibro", binding.VisualizadorPDF, binding.progressBar,
                        binding.paginasD)

                    //Cargar peso del libro
                    MisFunciones.CargarPesoPdf("$urlLibro", "$tituloLibro", binding.pesoD)

                    //Configurar informacion restante
                    binding.tituloLibroD.text = tituloLibro
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

    private  fun incrementarNumDescargas(){
        val ref = FirebaseDatabase.getInstance().getReference("Libros")
        ref.child(idLibro)
            .addListenerForSingleValueEvent(object  : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    var contDescargActual = "${snapshot.child("contadorDescargas").value}"

                    if (contDescargActual == "" || contDescargActual == "null"){
                        contDescargActual = "0"
                    }

                    //incremento
                    val nuevaDescarga = contDescargActual.toLong() + 1

                    val hashMap = HashMap<String, Any>()
                    hashMap["contadorDescargas"] = nuevaDescarga

                    val BDRef = FirebaseDatabase.getInstance().getReference("Libros")
                    BDRef.child(idLibro)
                        .updateChildren(hashMap)
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }

}