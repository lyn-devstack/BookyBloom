package com.lesliedev.bookybloom.Administrador

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AlertDialog.*
import androidx.recyclerview.widget.RecyclerView
import com.lesliedev.bookybloom.Modelos.ModeloPdf
import com.lesliedev.bookybloom.databinding.ItemLibroAdminBinding

class AdaptadorPdfAdmin : RecyclerView.Adapter<AdaptadorPdfAdmin.HolderPdfAdmin>, Filterable {

    private lateinit var binding : ItemLibroAdminBinding

    private var m_context : Context
    public var pdfArrayList : ArrayList<ModeloPdf>
    private var filtroLibro : ArrayList<ModeloPdf>
    private var filtro : FiltroPdfAmin?=null

    constructor(m_context: Context, pdfArrayList: ArrayList<ModeloPdf>) : super() {
        this.m_context = m_context
        this.pdfArrayList = pdfArrayList
        this.filtroLibro = pdfArrayList
    }


    inner class HolderPdfAdmin (itemView: View) : RecyclerView.ViewHolder(itemView){
        val VisualizadorPDF = binding.VisualizadorPDF
        val progressBar = binding.progressBar
        val Txt_titulo_libro_item = binding.TxtTituloLibroItem
        val Txt_descripcion_libro_item = binding.TxtDescripcionLibroItem
        val Txt_categoria_libro_admin = binding.TxtCategoriaLibroAdmin
        val Txt_peso_libro_admin = binding.TxtPesoLibroAdmin
        val Txt_fecha_libro_admin = binding.TxtFechaLibroAdmin
        val Ib_mas_opciones = binding.IbMasOpciones


    }

    //Inflar y enlazar el adapatador con el item_libro_admin
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderPdfAdmin {
        binding = ItemLibroAdminBinding.inflate(LayoutInflater.from(m_context), parent, false)
        return HolderPdfAdmin(binding.root)
    }

    //Obtener todos los items de la lista
    override fun getItemCount(): Int {
        return pdfArrayList.size
    }

    //Obtener y establecer la informacion y manejar eventos
    override fun onBindViewHolder(holder: HolderPdfAdmin, position: Int) {
        val modelo = pdfArrayList[position]
        val pdfId = modelo.id
        val categoriaId = modelo.categoria
        val titulo = modelo.titulo
        val descripcion = modelo.descripcion
        val pdfUrl = modelo.url
        val tiempo = modelo.tiempo

        val formatoTiempo = MisFunciones.formatoTiempo(tiempo)

        //establecer informacion dentro del item
        holder.Txt_titulo_libro_item.text = titulo
        holder.Txt_descripcion_libro_item.text = descripcion
        holder.Txt_fecha_libro_admin.text = formatoTiempo

        MisFunciones.CargarCategoria(categoriaId, holder.Txt_categoria_libro_admin)
        MisFunciones.CargarPdfUrl(pdfUrl, titulo, holder.VisualizadorPDF, holder.progressBar, null)
        MisFunciones.CargarPesoPdf(pdfUrl,titulo, holder.Txt_peso_libro_admin)

        //Eventos cuando el usuario presiona un elemento de la lista
        holder.Ib_mas_opciones.setOnClickListener{
            verOpciones(modelo, holder)
        }

        holder.itemView.setOnClickListener{
            //Dirige a la actividad detalleLibro
            val intent = Intent(m_context,DetalleLibro::class.java)

            //Pasar como parametro el id del libro para que sea recuperada
            intent.putExtra("idLibro", pdfId)
            m_context.startActivity(intent)
        }
    }

    private fun verOpciones(modelo: ModeloPdf, holder: AdaptadorPdfAdmin.HolderPdfAdmin) {
        val idLibro = modelo.id
        val urlLibro = modelo.url
        val tituloLibro = modelo.titulo

        val opciones = arrayOf("Actualizar","Eliminar")

        //Alert Dialog
        val builder = Builder(m_context)
        builder.setTitle("Seleccione una opción")
            .setItems(opciones){dialog, posicion->
                if(posicion == 0){
                    //Actualizar
                    val intent = Intent(m_context, ActualizarLibro::class.java)
                    intent.putExtra("idLibro", idLibro)
                    m_context.startActivity(intent)

                }else if(posicion == 1){
                    val opcionesBorrado = arrayOf("Confirmar", "Cancelar")
                    val builder = AlertDialog.Builder(m_context)
                    builder.setTitle("¿Confirma borrar el libro ${tituloLibro}?")
                        .setItems(opcionesBorrado){dialog, posicion->
                            if(posicion == 0){
                                MisFunciones.EliminarLibro(m_context, idLibro, urlLibro, tituloLibro)
                            }
                            else if(posicion == 1){
                                Toast.makeText(m_context, "Cancelado por el usuario", Toast.LENGTH_SHORT).show()
                            }
                        }
                        .show()
                }
            }
            .show()
    }

    override fun getFilter(): Filter {
        if(filtro == null){
            filtro = FiltroPdfAmin(filtroLibro, this)
        }
        return filtro as FiltroPdfAmin
    }
}