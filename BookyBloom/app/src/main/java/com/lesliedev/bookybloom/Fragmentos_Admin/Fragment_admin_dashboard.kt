package com.lesliedev.bookybloom.Fragmentos_Admin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.lesliedev.bookybloom.Administrador.AdaptadorCategoria
import com.lesliedev.bookybloom.Administrador.Agregar_Categoria
import com.lesliedev.bookybloom.Administrador.Agregar_pdf
import com.lesliedev.bookybloom.Modelos.ModeloCategoria
import com.lesliedev.bookybloom.databinding.FragmentAdminDashboardBinding


class Fragment_admin_dashboard : Fragment() {

    private lateinit var binding  : FragmentAdminDashboardBinding
    private lateinit var mContext : Context
    private lateinit var categoriaArrayList : ArrayList<ModeloCategoria>
    private lateinit var adapatadorCategoria : AdaptadorCategoria

    override fun onAttach(context: Context) {
        mContext = context
        super.onAttach(context)
    }
//    Acceder a los identificadores del disign
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentAdminDashboardBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ListarCategorias()

        binding.BuscarCategoria.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(categoria: CharSequence?, start: Int, before: Int, count: Int) {
                //Llamara a esta funcion cuando el usuario escriba algo en el editText
                try{
                    adapatadorCategoria.filter.filter(categoria)
                }catch (e: Exception){

                }

            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        binding.BtnAgregarCategoria.setOnClickListener{
            //Redirigir a la actividad
            startActivity(Intent(mContext, Agregar_Categoria::class.java))
        }

        binding.AgregarPdf.setOnClickListener{
            startActivity(Intent(mContext, Agregar_pdf::class.java))
        }
    }

    private fun ListarCategorias() {
        categoriaArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Categorias").orderByChild("categoria")
        ref.addValueEventListener(object : ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {
                //Borrar la lista antes de comenzar a guardar datos en la lista
                categoriaArrayList.clear()
                for (ds in snapshot.children){

                    //obtener los datos como modelo
                    val modelo = ds.getValue(ModeloCategoria::class.java)

                    //Agregar la informacion al arrayList
                    categoriaArrayList.add(modelo!!)
                }
                adapatadorCategoria = AdaptadorCategoria(mContext, categoriaArrayList)
                //set el adaptador en el recycler view
                binding.categoriasRv.adapter = adapatadorCategoria
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

}