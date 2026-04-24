package com.lesliedev.bookybloom.Fragmentos_Admin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.lesliedev.bookybloom.Administrador.EditarPerfilAdmin
import com.lesliedev.bookybloom.Administrador.MisFunciones
import com.lesliedev.bookybloom.R
import com.lesliedev.bookybloom.Seleccionar_rol
import com.lesliedev.bookybloom.databinding.FragmentAdminCuentaBinding


class Fragment_admin_cuenta : Fragment() {

    private lateinit var binding : FragmentAdminCuentaBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var mContext : Context

    override fun onAttach(context: Context) {
        mContext = context
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentAdminCuentaBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()

        cargarInformacion()

        binding.EditarPerfilAdmin.setOnClickListener{
            //navegar entre actividades
            startActivity(Intent(mContext, EditarPerfilAdmin::class.java))
        }

        binding.CerrarSesionAdmin.setOnClickListener {
            //Cerrar sesion
            firebaseAuth.signOut()
            //Una vez cerramos la sesion
            startActivity(Intent(context, Seleccionar_rol::class.java))
            //Cerramos las actividades anteriores
            activity?.finishAffinity()
        }
    }

    private fun cargarInformacion() {
        //Referencia a la DB
        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child("${firebaseAuth.uid}")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    //Obtener datos de usuario actual
                    val nombre = "${snapshot.child("nombre").value}"
                    val apellidos = "${snapshot.child("apellidos").value}"
                    val email = "${snapshot.child("email").value}"
                    var t_registro = "${snapshot.child("tiempo_registro").value}"
                    val rol = "${snapshot.child("rol").value}"
                    val imagen = "${snapshot.child("imagen").value}"

                    //excepciones
                    if(t_registro == "null"){
                        t_registro = "0"
                    }

                    //Convertir a format fecha
                    val formato_fecha = MisFunciones.formatoTiempo(t_registro.toLong())

                    //Configurar info
                    binding.TxtNombreAdmin.text = nombre
                    binding.TxtApellidosAdmin.text = apellidos
                    binding.TxtEmailAdmin.text = email
                    binding.TxtTiempoRegistroAdmin.text = formato_fecha
                    binding.TxtRolAdmin.text = rol

                    //Configurar Imagen
                    try {
                        Glide.with(mContext)
                            .load(imagen)
                            .circleCrop() // Hace la imagen circular
                            .placeholder(R.drawable.ic_img_perfil)
                            .into(binding.imgPerfilAdmin)

                    }catch (e: Exception){
                        Toast.makeText(mContext, "${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

}