package com.example.microredesocialbruno.ui

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.microredesocialbruno.helper.Base64Converter
import com.example.microredesocialbruno.helper.LocalizacaoHelper
import com.example.microredesocialbruno.dao.PostDAO
import com.example.microredesocialbruno.databinding.ActivityCreatePostBinding
import com.google.firebase.auth.FirebaseAuth

class CreatePostActivity : AppCompatActivity(), LocalizacaoHelper.Callback {
    private lateinit var binding: ActivityCreatePostBinding
    private val postDAO = PostDAO()
    private val firebaseAuth = FirebaseAuth.getInstance()
    private lateinit var localizacaoHelper: LocalizacaoHelper
    private var cidadeAtual: String = ""
    private val LOCATION_PERMISSION_REQUEST_CODE = 1001

    private val galeria = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            binding.imgPost.setImageURI(uri)
        } else {
            Toast.makeText(this, "Nenhuma foto selecionada", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreatePostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Novo Post"

        localizacaoHelper = LocalizacaoHelper(applicationContext)

        solicitarLocalizacao()

        binding.btnAlterarFoto.setOnClickListener {
            galeria.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }

        binding.btnPublicar.setOnClickListener {
            publicarPost()
        }
    }

    private fun solicitarLocalizacao() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            &&
            ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            localizacaoHelper.obterLocalizacaoAtual(this)
        }
    }

    override fun onLocalizacaoRecebida(endereco: Address, latitude: Double, longitude: Double) {
        cidadeAtual = endereco.subAdminArea ?: endereco.locality ?: "Cidade desconhecida"
        runOnUiThread {
            binding.txtCidade.text = "📍 $cidadeAtual"
        }
    }

    override fun onErro(mensagem: String) {
        runOnUiThread {
            binding.txtCidade.text = "Localização indisponível"
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            solicitarLocalizacao()
        } else {
            Toast.makeText(this, "Permissão de localização negada", Toast.LENGTH_SHORT).show()
            binding.txtCidade.text = "Localização indisponível"
        }
    }

    private fun publicarPost() {
        val descricao = binding.edtDescricao.text.toString()
        val email = firebaseAuth.currentUser?.email ?: ""

        if (descricao.isEmpty()) {
            Toast.makeText(this, "Escreva uma descrição", Toast.LENGTH_SHORT).show()
            return
        }

        val fotoString = Base64Converter.drawableToString(binding.imgPost.drawable)

        postDAO.salvarPost(
            imageString = fotoString,
            descricao = descricao,
            autorEmail = email,
            cidade = cidadeAtual,
            onSucesso = {
                Toast.makeText(this, "Post publicado!", Toast.LENGTH_SHORT).show()
                finish()
            },
            onErro = {
                Toast.makeText(this, "Erro ao publicar post", Toast.LENGTH_SHORT).show()
            }
        )
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}