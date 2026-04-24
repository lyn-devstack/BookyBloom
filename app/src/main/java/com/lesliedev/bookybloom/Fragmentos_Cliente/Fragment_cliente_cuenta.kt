package com.lesliedev.bookybloom.Fragmentos_Cliente

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
import com.lesliedev.bookybloom.Administrador.MisFunciones
import com.lesliedev.bookybloom.Cliente.EditarPerfilCliente
import com.lesliedev.bookybloom.R
import com.lesliedev.bookybloom.Seleccionar_rol
import com.lesliedev.bookybloom.databinding.ActivityMainClienteBinding
import com.lesliedev.bookybloom.databinding.FragmentClienteCuentaBinding

class Fragment_cliente_cuenta : Fragment() {

    private lateinit var binding: FragmentClienteCuentaBinding

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var mContext : Context

    override fun onAttach(context: Context){
        mContext = context
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentClienteCuentaBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()

        cargarInformacion()

        binding.EditarPerfilCliente.setOnClickListener {
            startActivity(Intent(mContext, EditarPerfilCliente::class.java))
        }

        binding.CerrarSesionCliente.setOnClickListener{
            firebaseAuth.signOut()
            startActivity(Intent(mContext, Seleccionar_rol::class.java))
            activity?.finishAffinity()
        }
    }

    private fun cargarInformacion() {
        //Referencia a la BD
        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child("${firebaseAuth.uid}")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    //Obtener los datos del usuario actual desde Firebase
                    val nombre = "${snapshot.child("nombre").value}"
                    val apellidos = "${snapshot.child("apellidos").value}"
                    val email = "${snapshot.child("email").value}"
                    var t_registro = "${snapshot.child("tiempo_registro").value}"
                    val edad = "${snapshot.child("edad").value}"
                    val rol = "${snapshot.child("rol").value}"
                    val imagen = "${snapshot.child("imagen").value}"

                    if (t_registro == "null"){
                        t_registro = "0"
                    }

                    val formatoFecha = MisFunciones.formatoTiempo(t_registro.toLong())

                    //configurar toda la informacion anterior dentro de las vistas
                    binding.TxtNombreCliente.text = nombre
                    binding.TxtApellidosCliente.text = apellidos
                    binding.TxtEmailCliente.text = email
                    binding.TxtTiempoRegistroCliente.text = formatoFecha
                    binding.TxtEdadCliente.text = edad
                    binding.TxtRolCliente.text = rol

                    //Configurar la imagen dentro de imageView
                    try {
                        Glide.with(mContext)
                            .load(imagen)
                            .placeholder(R.drawable.ic_img_perfil)
                            .into(binding.imgPerfilCliente)
                    }catch (e:Exception){
                        Toast.makeText(mContext, "${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }

}