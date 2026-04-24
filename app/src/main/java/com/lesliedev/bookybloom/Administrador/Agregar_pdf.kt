package com.lesliedev.bookybloom.Administrador

import android.Manifest
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.lesliedev.bookybloom.Modelos.ModeloCategoria
import com.lesliedev.bookybloom.databinding.ActivityAgregarPdfBinding
import java.util.ArrayList

class Agregar_pdf : AppCompatActivity() {

    private lateinit var binding : ActivityAgregarPdfBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog : ProgressDialog
    private lateinit var categoriaArrayList : ArrayList<ModeloCategoria>
    private var pdfUri : Uri?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAgregarPdfBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        CargarCategorias()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favor")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.IbRegresar.setOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }

        binding.AdjuntarPdfIb.setOnClickListener{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // Para Android 13 y superiores, solicita permisos específicos para medios
                SolicitudPermisoAccederArchivos.launch(Manifest.permission.READ_MEDIA_IMAGES)
            } else {
                // Para versiones anteriores a Android 13, solicita READ_EXTERNAL_STORAGE
                if (ContextCompat.checkSelfPermission(
                        this@Agregar_pdf,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    // Si el permiso ya fue concedido
                    ElegirPdf()
                } else {
                    // Solicitar el permiso
                    SolicitudPermisoAccederArchivos.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }

        }

        binding.TvCategoriaLibro.setOnClickListener{
            SeleccionarCategoria()
        }

        binding.BtnSubirLibro.setOnClickListener {
            ValidarInformacion()
        }
    }

    private var titulo = ""
    private var descripcion = ""
    private var categoria = ""
    private fun ValidarInformacion() {
        titulo = binding.EtTituloLibro.text.toString().trim()
        descripcion = binding.EtDescripcionLibro.text.toString().trim()
        categoria = binding.TvCategoriaLibro.text.toString().trim()

        if(titulo.isEmpty()){
            Toast.makeText(this, "Ingrese título", Toast.LENGTH_SHORT).show()
        }
        else if(descripcion.isEmpty()){
            Toast.makeText(this, "Ingrese descripción", Toast.LENGTH_SHORT).show()
        }
        else if (categoria.isEmpty()){
            Toast.makeText(this, "Selecciones categoría", Toast.LENGTH_SHORT).show()
        }
        else if(pdfUri == null){
            Toast.makeText(this, "Adjunte libro ", Toast.LENGTH_SHORT).show()
        }
        else{
            SubirPdfStore()
        }
    }

    private fun SubirPdfStore() {
        progressDialog.setMessage("Subiendo PDF")
        progressDialog.show()

        val tiempo = System.currentTimeMillis()

        //La carpeta se llamara Libros, tendra los libros cuyo id sera "tiempo"
        val ruta_libro = "Libros/$tiempo"
        val storageReference = FirebaseStorage.getInstance().getReference(ruta_libro)
        storageReference.putFile(pdfUri!!)
            .addOnSuccessListener {t->
                val uriTask : Task<Uri> = t.storage.downloadUrl
                while (!uriTask.isSuccessful);
                val UrlPdfSubido = "${uriTask.result}"

                //Llamar a la funcion que subira el pdf a la base de datos
                SubirPdfDB(UrlPdfSubido,tiempo)

            }
            .addOnFailureListener{e->
                progressDialog.dismiss()
                Toast.makeText(this, "No se ha podido subor el libro. Vuelva a intentarlo. Error: ${e.message}", Toast.LENGTH_SHORT).show()

            }
    }

    private fun SubirPdfDB(urlPdfSubido: String, tiempo: Long) {
        progressDialog.setMessage("Subiendo PDF a la Base de Datos...")
        val uid = firebaseAuth.uid

        val hashMap : HashMap<String, Any> = HashMap()
        hashMap["uid"] = "$uid"
        hashMap["id"] = "$tiempo"
        hashMap["titulo"] = titulo
        hashMap["descripcion"] = descripcion
        hashMap["categoria"] = id_categoria
        hashMap["url"] = urlPdfSubido
        hashMap["tiempo"] = tiempo
        hashMap["contadorVistas"] = 0
        hashMap["contadorDescargas"] = 0

        val ref = FirebaseDatabase.getInstance().getReference("Libros")
        ref.child("$tiempo")
            .setValue(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this, "El libro ha sido subido exitosamente.", Toast.LENGTH_SHORT).show()
                binding.EtTituloLibro.setText("")
                binding.EtDescripcionLibro.setText("")
                binding.TvCategoriaLibro.setText("")
                pdfUri = null
            }
            .addOnFailureListener{e->
                progressDialog.dismiss()
                Toast.makeText(this, "No se ha podido subor el libro. Vuelva a intentarlo. Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun CargarCategorias() {

        categoriaArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Categorias").orderByChild("categoria")
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                //limpiar la lista
                categoriaArrayList.clear()
                for(ds in snapshot.children){
                    val modelo = ds.getValue(ModeloCategoria::class.java)
                    categoriaArrayList.add(modelo!!)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private var id_categoria = ""
    private var titulo_categoria = ""

    private fun SeleccionarCategoria(){
        val categoriasArray = arrayOfNulls<String>(categoriaArrayList.size)
        for(i in categoriasArray.indices){
            categoriasArray[i] = categoriaArrayList[i].categoria
        }

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Seleccionar categoría")
            .setItems(categoriasArray){dialog, which->
                id_categoria = categoriaArrayList[which].id
                titulo_categoria = categoriaArrayList[which].categoria
                binding.TvCategoriaLibro.text = titulo_categoria
            }
            .show()
    }

    private fun ElegirPdf(){
        val intent = Intent()
        intent.type = "application/pdf"
        intent.action = Intent.ACTION_GET_CONTENT
        pdfActivityRL.launch(intent)
    }

    val pdfActivityRL = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback<ActivityResult>{resultado->
            if(resultado.resultCode == RESULT_OK){
                pdfUri = resultado.data!!.data
            }else{
                Toast.makeText(this, "Cancelado", Toast.LENGTH_SHORT).show()
            }
        }
    )

    private val SolicitudPermisoAccederArchivos =
        registerForActivityResult(ActivityResultContracts.RequestPermission()){ permiso ->
            if(permiso){
                //Permiso concedido
                ElegirPdf()
            }else{
                //No se dio permiso
                Toast.makeText(this, "El permiso para acceder al gestor de archivos no ha sido concedido.",
                    Toast.LENGTH_SHORT).show()
            }

        }
}