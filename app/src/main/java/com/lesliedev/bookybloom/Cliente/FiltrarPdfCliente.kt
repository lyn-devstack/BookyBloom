package com.lesliedev.bookybloom.Cliente

import android.widget.Filter
import com.lesliedev.bookybloom.Modelos.ModeloPdf

class FiltrarPdfCliente : Filter {

    var filtroList : ArrayList<ModeloPdf>
    var adaptadorPdfCliente : AdaptadorPdfCliente

    constructor(filtroList: ArrayList<ModeloPdf>, adaptadorPdfCliente: AdaptadorPdfCliente) {
        this.filtroList = filtroList
        this.adaptadorPdfCliente = adaptadorPdfCliente
    }

    override fun performFiltering(libro: CharSequence?): Filter.FilterResults {
        var libro : CharSequence?= libro
        val resultados = Filter.FilterResults()
        if(libro!=null && libro.isNotEmpty()){
            libro = libro.toString().lowercase()
            val modeloFiltrado : ArrayList<ModeloPdf> = ArrayList()
            for(i in filtroList.indices){

                //Validar si coincide lo que estamos escribiendo con algun elemeneto de la lista
                if(filtroList[i].titulo.lowercase().contains(libro)){
                    modeloFiltrado.add(filtroList[i])
                }
            }
            resultados.count = modeloFiltrado.size
            resultados.values = modeloFiltrado
        }
        else{
            resultados.count = filtroList.size
            resultados.values = filtroList
        }
        return resultados
    }

    override fun publishResults(constraint: CharSequence?, resultados: Filter.FilterResults) {
        adaptadorPdfCliente.pdfArrayList = resultados.values as ArrayList<ModeloPdf>
        adaptadorPdfCliente.notifyDataSetChanged()
    }
}