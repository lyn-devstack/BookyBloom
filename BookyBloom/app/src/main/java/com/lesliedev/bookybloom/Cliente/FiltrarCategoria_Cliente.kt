package com.lesliedev.bookybloom.Cliente

import android.widget.Filter
import com.lesliedev.bookybloom.Modelos.ModeloCategoria

class FiltrarCategoria_Cliente : Filter{

    private var filtroLista : ArrayList<ModeloCategoria>
    private var adapatadorCategoriaCliente : AdaptadorCategoria_Cliente

    constructor(filtroLista: ArrayList<ModeloCategoria>, adapatadorCategoriaCliente: AdaptadorCategoria_Cliente){
        this.filtroLista = filtroLista
        this.adapatadorCategoriaCliente = adapatadorCategoriaCliente
    }

    override fun performFiltering(categoria: CharSequence?): Filter.FilterResults {
        var categoria = categoria
        var resultados = Filter.FilterResults()

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

    override fun publishResults(p0: CharSequence?, resultados: Filter.FilterResults) {
        adapatadorCategoriaCliente.categoriaArrayList = resultados.values as ArrayList<ModeloCategoria>
        //Notificar los cambios
        adapatadorCategoriaCliente.notifyDataSetChanged()
    }
}