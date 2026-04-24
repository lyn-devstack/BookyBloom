package com.lesliedev.bookybloom.Administrador

import android.widget.Filter
import com.lesliedev.bookybloom.Modelos.ModeloPdf

class FiltroPdfAmin : Filter{
    var filtroList : ArrayList<ModeloPdf>
    var adaptadorPdfAdmin : AdaptadorPdfAdmin

    constructor(filtroList: ArrayList<ModeloPdf>, adaptadorPdfAdmin: AdaptadorPdfAdmin) {
        this.filtroList = filtroList
        this.adaptadorPdfAdmin = adaptadorPdfAdmin
    }

    override fun performFiltering(libro: CharSequence?): FilterResults {
        var libro : CharSequence?= libro
        val resultados = FilterResults()
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

    override fun publishResults(constraint: CharSequence?, resultados: FilterResults) {
        adaptadorPdfAdmin.pdfArrayList = resultados.values as ArrayList<ModeloPdf>
        adaptadorPdfAdmin.notifyDataSetChanged()
    }
}