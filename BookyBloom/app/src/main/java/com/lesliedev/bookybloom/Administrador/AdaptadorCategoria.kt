package com.lesliedev.bookybloom.Administrador

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.firebase.database.FirebaseDatabase
import com.lesliedev.bookybloom.Modelos.ModeloCategoria
import com.lesliedev.bookybloom.databinding.ItemCategoriaAdminBinding

class AdaptadorCategoria : RecyclerView.Adapter<AdaptadorCategoria.HolderCategoria>, Filterable{

    private lateinit var binding : ItemCategoriaAdminBinding

    private val m_context : Context
    public var categoriaArrayList : ArrayList<ModeloCategoria>

    private var filtroLista : ArrayList<ModeloCategoria>
    private var filtro : FiltroCategoria? = null

    constructor(m_context: Context, categoriaArrayList: ArrayList<ModeloCategoria>) {
        this.m_context = m_context
        this.categoriaArrayList = categoriaArrayList
        this.filtroLista = categoriaArrayList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderCategoria {
        binding = ItemCategoriaAdminBinding.inflate(LayoutInflater.from(m_context), parent, false)
        return HolderCategoria(binding.root)
    }

    override fun getItemCount(): Int {
        return categoriaArrayList.size
    }

    override fun onBindViewHolder(holder: HolderCategoria, position: Int) {
        //Obtener y establecer la informacion de Firebase
        val modelo    = categoriaArrayList[position]
        val id        = modelo.id
        val categoria = modelo.categoria
        val tiempo    = modelo.tiempo
        val uid       = modelo.uid

        holder.categoriaTv.text = categoria
        holder.eliminarCategoriaIb.setOnClickListener{
            val builder = AlertDialog.Builder(m_context)
            builder.setTitle("Eliminar categoria")
                .setMessage("¿Confirma que quiere eliminar esta categoría?")
                .setPositiveButton("Confirmar"){a, d->
                    Toast.makeText(m_context, "Eliminando categoría", Toast.LENGTH_SHORT).show()
                    EliminarCategoria(modelo, holder)
                }
                .setNegativeButton("Cancelar"){a, d->
                    a.dismiss()
                }
            builder.show()
        }

        holder.itemView.setOnClickListener{
            val intent = Intent(m_context, ListaPdfAdmin::class.java)
            intent.putExtra("idCategoria", id)
            intent.putExtra("tituloCategoria", categoria)
            m_context.startActivity(intent)
        }
    }

    private fun EliminarCategoria(modelo: ModeloCategoria, holder: AdaptadorCategoria.HolderCategoria) {
        //Seleccionames el id del item seleccionado por el administrador
        val id = modelo.id
        //Busca ese id en la DB de Categorias
        val ref = FirebaseDatabase.getInstance().getReference("Categorias")
        //Si coinciden lo elimina
        ref.child(id).removeValue()
            .addOnSuccessListener {
                Toast.makeText(m_context, "Categoría eliminada", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener{e->
                Toast.makeText(m_context, "No se ha podido eliminar la categoría. Error: ${e.message}", Toast.LENGTH_SHORT).show()

            }
    }

    inner class HolderCategoria(itemView: MaterialCardView) : RecyclerView.ViewHolder(itemView){
        var categoriaTv : TextView = binding.ItemNombreCategoriaAdmin
        var eliminarCategoriaIb : ImageButton = binding.EliminarCategoria
    }

    override fun getFilter(): Filter {
        if (filtro == null){
            filtro = FiltroCategoria(filtroLista,this)
        }
        return filtro as FiltroCategoria
    }


}