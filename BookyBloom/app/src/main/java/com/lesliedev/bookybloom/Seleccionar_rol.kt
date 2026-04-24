package com.lesliedev.bookybloom

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.lesliedev.bookybloom.Administrador.Registrar_Admin
import com.lesliedev.bookybloom.Cliente.Registro_Cliente
import com.lesliedev.bookybloom.databinding.ActivitySeleccionarRolBinding

class Seleccionar_rol : AppCompatActivity() {

    private lateinit var binding : ActivitySeleccionarRolBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySeleccionarRolBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.BtnRolAdministrador.setOnClickListener{
           //Toast.makeText(applicationContext, "Rol administrador", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this@Seleccionar_rol, Registrar_Admin::class.java))
        }

        binding.BtnRolCliente.setOnClickListener{
           startActivity(Intent(this@Seleccionar_rol, Registro_Cliente::class.java))
        }
    }
}