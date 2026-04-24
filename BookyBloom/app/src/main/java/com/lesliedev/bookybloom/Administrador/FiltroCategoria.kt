package com.lesliedev.bookybloom.Administrador

import android.widget.Filter
import com.lesliedev.bookybloom.Modelos.ModeloCategoria

class FiltroCategoria : Filter {

    private var filtroLista : ArrayList<ModeloCategoria>
    private var adapatadorCategoria : AdaptadorCategoria

    constructor(filtroLista: ArrayList<ModeloCategoria>, adapatadorCategoria: AdaptadorCategoria) {
        this.filtroLista = filtroLista
        this.adapatadorCategoria = adapatadorCategoria
    }

    override fun performFiltering(categoria: CharSequence?): FilterResults {
        var categoria = categoria
        var resultados = FilterResults()

        //Comprobar que no sea nullo ni este vacio el dato categoria
        if(categoria != null && categoria.isNotEmpty()){
            categoria = categoria.toString().uppercase()
            val modeloFiltrado : ArrayList<ModeloCategoria> = ArrayList()

            for (i in 0 until  filtroLista.size){
                //Validar
                if(filtroLista[i].categoria.uppercase().contains(categoria)){
                    modeloFiltrado.add(filtroLista[i])
                }
                resultados.count = modeloFiltrado.size
                resultados.values = modeloFiltrado
            }
        }

        else{
            resultados.count = filtroLista.size
            resultados.values = filtroLista
        }
        return resultados
    }

    override fun publishResults(p0: CharSequence?, resultados: FilterResults) {
        adapatadorCategoria.categoriaArrayList = resultados.values as ArrayList<ModeloCategoria>
        //Notificar los cambios
        adapatadorCategoria.notifyDataSetChanged()
    }
}