package com.lesliedev.bookybloom.Administrador

import android.app.Application
import android.app.ProgressDialog
import android.content.Context
import android.text.format.DateFormat
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.github.barteksc.pdfviewer.PDFView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import org.w3c.dom.Text
import java.util.Calendar
import java.util.Locale

class MisFunciones :Application() {

    override fun onCreate() {
        super.onCreate()
    }

    //Metodo para convertir el formato del tiempo
    companion object{
        fun formatoTiempo (tiempo: Long) : String{
            val calendario = Calendar.getInstance(Locale.ENGLISH)
            calendario.timeInMillis = tiempo

            //dd/MM/yyyy
            return DateFormat.format("dd/MM/yyyy", calendario).toString()
        }

        fun CargarPesoPdf(pdfUrl : String, pdfTitulo : String, peso : TextView){
            val ref = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl)
            ref.metadata
                .addOnSuccessListener { metadata->
                    val bytes = metadata.sizeBytes.toDouble()

                    val KB = bytes/1024
                    val MB = KB/1024

                    if(MB>1){
                        peso.text = "${String.format("%.2f", MB)} MB"
                    }
                    else if(KB>=1){
                        peso.text = "${String.format("%.2f", KB)} KB"

                    }else{
                        peso.text = "${String.format("%.2f", bytes)} Bytes"
                    }
                }
                .addOnFailureListener{e->

                }
        }

        //Cargar el pdf a traves de una URL
        fun CargarPdfUrl(pdfUrl : String, pdfTitulo : String, pdfView : PDFView, progressBar: ProgressBar,
                         paginaTv : TextView?){

            val ref = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl)
            ref.getBytes(Constantes.Maximo_bytes_pdf)
                .addOnSuccessListener {bytes->
                    pdfView.fromBytes(bytes)
                        .pages(0)
                        .spacing(0)
                        .swipeHorizontal(false)
                        .enableSwipe(false)
                        .onError{t->
                            progressBar.visibility = View.INVISIBLE
                        }
                        .onPageError{page, pageCount->
                            progressBar.visibility = View.INVISIBLE
                        }
                        .onLoad{ Pagina->
                            //Cuando entramos a esta funcion es porque el pdf ya ha sido cargado y se puede configurar

                            progressBar.visibility = View.INVISIBLE
                            if(paginaTv != null){
                                paginaTv.text = "$Pagina"
                            }
                        }
                        .load()
                }
                .addOnFailureListener {e->

                }
        }

        //Cargar categoria
        fun CargarCategoria(categoriaId : String, categoriaTv : TextView){

            val ref = FirebaseDatabase.getInstance().getReference("Categorias")
            ref.child(categoriaId)
                .addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val categoria = "${snapshot.child("categoria").value}"
                        categoriaTv.text = categoria
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                } )
        }

        fun EliminarLibro(context: Context, idLibro : String, urlLibro : String, tituloLibro: String){
            val progressDialog = ProgressDialog(context)
            progressDialog.setTitle("Espere por favor")
            progressDialog.setMessage("Eliminando $tituloLibro")
            progressDialog.setCanceledOnTouchOutside(false)
            progressDialog.show()

            val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(urlLibro)
            storageReference.delete()
                .addOnSuccessListener {
                    val ref = FirebaseDatabase.getInstance().getReference("Libros")
                    ref.child(idLibro)
                        .removeValue()
                        .addOnSuccessListener {
                            progressDialog.dismiss()
                            Toast.makeText(context, "El libro se ha eliminado exitosamente.", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener{e->
                            progressDialog.dismiss()
                            Toast.makeText(context, "No se ha podido borrar el libro. Error: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
                .addOnFailureListener{e->
                    progressDialog.dismiss()
                    Toast.makeText(context, "No se ha podido borrar el libro. Error: ${e.message}", Toast.LENGTH_SHORT).show()

                }

        }

        fun incrementarVistas(idLibro : String){
            val ref = FirebaseDatabase.getInstance().getReference("Libros")
            ref.child(idLibro)
                .addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var vistasActuales = "${snapshot.child("contadorVistas").value}"
                        if (vistasActuales == "" || vistasActuales == "null"){
                            vistasActuales = "0"
                        }

                        //incremento
                        val nuevaVista = vistasActuales.toLong() + 1

                        val hashMap = HashMap<String, Any>()
                        hashMap["contadorVistas"] = nuevaVista

                        val BDRef = FirebaseDatabase.getInstance().getReference("Libros")
                        BDRef.child(idLibro)
                            .updateChildren(hashMap)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })
        }

        fun eliminarFavoritos(context: Context, idLibro : String){
            val firebaseAuth = FirebaseAuth.getInstance()
            val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
            ref.child(firebaseAuth.uid!!).child("Favoritos").child(idLibro)
                .removeValue()
                .addOnSuccessListener {
                    Toast.makeText(context, "Se ha eliminado de favoritos",
                        Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {e->
                    Toast.makeText(context, "No se ha eliminado de favoritos. Error: ${e.message} ",
                        Toast.LENGTH_SHORT).show()
                }
        }
    }
}