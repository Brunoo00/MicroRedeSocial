package com.example.microredesocialbruno.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.microredesocialbruno.helper.Base64Converter
import com.example.microredesocialbruno.dao.UserDAO
import com.example.microredesocialbruno.databinding.ActivityProfileBinding
import com.example.microredesocialbruno.model.User
import com.google.firebase.auth.FirebaseAuth

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private val userDAO = UserDAO()
    private val firebaseAuth = FirebaseAuth.getInstance()

    private val galeria = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            binding.profilePicture.setImageURI(uri)
        } else {
            Toast.makeText(this, "Nenhuma foto selecionada", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Meu Perfil"

        val nomeCompletoRecebido = intent.getStringExtra("nomeCompleto")
        if (!nomeCompletoRecebido.isNullOrEmpty()) {
            binding.edtNomeCompleto.setText(nomeCompletoRecebido)
        } else {
            carregarPerfilExistente()
        }

        binding.btnAlterarFoto.setOnClickListener {
            galeria.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }

        binding.btnSalvar.setOnClickListener {
            salvarPerfil()
        }
    }

    private fun carregarPerfilExistente() {
        val email = firebaseAuth.currentUser?.email ?: return
        userDAO.buscarUsuario(
            email = email,
            onSucesso = { user ->
                binding.edtUsername.setText(user.username)
                binding.edtNomeCompleto.setText(user.nomeCompleto)
                if (user.fotoPerfil.isNotEmpty()) {
                    val bitmap = Base64Converter.stringToBitmap(user.fotoPerfil)
                    binding.profilePicture.setImageBitmap(bitmap)
                }
            },
            onErro = {}
        )
    }

    private fun salvarPerfil() {
        val username = binding.edtUsername.text.toString()
        val nomeCompleto = binding.edtNomeCompleto.text.toString()
        val email = firebaseAuth.currentUser?.email ?: ""
        val senhaAtual = binding.edtSenhaAtual.text.toString()
        val novaSenha = binding.edtNovaSenha.text.toString()
        val confirmarNovaSenha = binding.edtConfirmarNovaSenha.text.toString()

        if (username.isEmpty() || nomeCompleto.isEmpty()) {
            Toast.makeText(this, "Preencha nome e username", Toast.LENGTH_SHORT).show()
            return
        }

        // Converte foto apenas se for BitmapDrawable, senão salva string vazia
        val fotoString = try {
            Base64Converter.drawableToString(binding.profilePicture.drawable)
        } catch (e: Exception) {
            ""
        }

        val user = User(
            email = email,
            username = username,
            nomeCompleto = nomeCompleto,
            fotoPerfil = fotoString
        )

        if (senhaAtual.isNotEmpty() || novaSenha.isNotEmpty()) {
            if (senhaAtual.isEmpty() || novaSenha.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos de senha", Toast.LENGTH_SHORT).show()
                return
            }
            if (novaSenha != confirmarNovaSenha) {
                Toast.makeText(this, "As senhas não coincidem", Toast.LENGTH_SHORT).show()
                return
            }
            userDAO.alterarSenha(
                senhaAtual = senhaAtual,
                novaSenha = novaSenha,
                onSucesso = { salvarDadosPerfil(user) },
                onErro = {
                    Toast.makeText(this, "Senha atual incorreta", Toast.LENGTH_SHORT).show()
                }
            )
        } else {
            salvarDadosPerfil(user)
        }
    }

    private fun salvarDadosPerfil(user: User) {
        userDAO.salvarUsuario(
            user = user,
            onSucesso = {
                Toast.makeText(this, "Perfil salvo!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            },
            onErro = {
                Toast.makeText(this, "Erro ao salvar perfil", Toast.LENGTH_SHORT).show()
            }
        )
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}