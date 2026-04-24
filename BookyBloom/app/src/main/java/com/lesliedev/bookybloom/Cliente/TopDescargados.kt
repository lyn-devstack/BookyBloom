package com.lesliedev.bookybloom.Cliente

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.lesliedev.bookybloom.Modelos.ModeloPdf
import com.lesliedev.bookybloom.databinding.ActivityTopDescargadosBinding

class TopDescargados : AppCompatActivity() {

    private lateinit var binding : ActivityTopDescargadosBinding
    private lateinit var pdfArrayList : ArrayList<ModeloPdf>
    private lateinit var adaptadorPdfCliente : AdaptadorPdfCliente

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTopDescargadosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        topDescargados()
    }

    private fun topDescargados() {
        pdfArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Libros")
        ref.orderByChild("contadorDescargas").limitToLast(5)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    pdfArrayList.clear()
                    for (ds in snapshot.children){
                        val modelo = ds.getValue(ModeloPdf::class.java)
                        pdfArrayList.add(modelo!!)
                    }
                    //configurar el adaptador
                    adaptadorPdfCliente = AdaptadorPdfCliente( this@TopDescargados, pdfArrayList)
                    binding.RvTopDescargados.adapter = adaptadorPdfCliente
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }
}