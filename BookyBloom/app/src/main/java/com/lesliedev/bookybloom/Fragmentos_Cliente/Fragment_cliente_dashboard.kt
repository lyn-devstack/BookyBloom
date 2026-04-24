package com.lesliedev.bookybloom.Fragmentos_Cliente

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.lesliedev.bookybloom.Modelos.ModeloCategoria
import com.lesliedev.bookybloom.Cliente.AdaptadorCategoria_Cliente
import com.lesliedev.bookybloom.Cliente.TopDescargados
import com.lesliedev.bookybloom.Cliente.TopVistos
import com.lesliedev.bookybloom.databinding.FragmentClienteDashboardBinding


class Fragment_cliente_dashboard : Fragment() {

    private lateinit var binding: FragmentClienteDashboardBinding
    private lateinit var mContext : Context

    private lateinit var categoriasArrayList: ArrayList<ModeloCategoria>
    private lateinit var adaptadorCategoria: AdaptadorCategoria_Cliente

    //inicializar el context
    override fun onAttach(context: Context) {
        mContext = context
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentClienteDashboardBinding.inflate(LayoutInflater.from(context),container, false )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cargarCategorias()

        binding.BuscarCategoria.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(categoria: CharSequence?, start: Int, before: Int, count: Int) {

                try {
                    adaptadorCategoria.filter.filter(categoria)

                }catch (e:Exception){
                    Toast.makeText(mContext, "${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        binding.BtnMasVistos.setOnClickListener{
            startActivity(Intent(mContext, TopVistos::class.java))

        }

        binding.BtnMasDescargados.setOnClickListener {
            startActivity(Intent(mContext, TopDescargados::class.java))
        }
    }

    private fun cargarCategorias() {
        categoriasArrayList = ArrayList()

        //Referencia a la DB donde alojo las categorias
        val ref = FirebaseDatabase.getInstance().getReference("Categorias").orderByChild("categoria")
        ref.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                categoriasArrayList.clear()
                for (ds in snapshot.children){
                    //Obtener los datos y psarselos al modelo
                    val modelo = ds.getValue(ModeloCategoria::class.java)

                    //Agregar a la lista todas las categorias
                    categoriasArrayList.add(modelo!!)
                }
                adaptadorCategoria = AdaptadorCategoria_Cliente(mContext, categoriasArrayList)

                //Configurar esa informacion dentro del recyclerView
                binding.categoriaRv.adapter = adaptadorCategoria
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

}