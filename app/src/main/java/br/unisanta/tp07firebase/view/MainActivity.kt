package br.unisanta.tp07firebase.view

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import br.unisanta.tp07firebase.R
import br.unisanta.tp07firebase.databinding.ActivityMainBinding
import br.unisanta.tp07firebase.viewmodel.UsuarioViewModel
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: UsuarioViewModel

    // Contrato da documentação oficial do FirebaseUI
    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract(),
    ) { res ->
        this.onSignInResult(res)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[UsuarioViewModel::class.java]

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Observa o estado do utilizador pelo ViewModel (MVVM)
        viewModel.usuarioLogado.observe(this) { usuario ->
            if (usuario != null) {
                binding.tvNome.text = "Nome: ${usuario.name}"
                binding.tvEmail.text = "E-mail: ${usuario.email}"
                binding.tvUid.text = "UID: ${usuario.uid}"
            } else {
                abrirTelaLoginFirebaseUI()
            }
        }

        // Botão para sair e regressar ao ecrã de login
        binding.btnSair.setOnClickListener {
            viewModel.deslogar(this) {
                Toast.makeText(this, "Sessão encerrada!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun abrirTelaLoginFirebaseUI() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build()
        )

        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setTheme(R.style.Theme_FirebaseUI_Custom) // Corrige a barra cortada e aplica cor moderna
            .setLogo(R.mipmap.ic_launcher)             // Coloca o ícone no topo do Login
            .setIsSmartLockEnabled(false)
            .build()

        signInLauncher.launch(signInIntent)
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        if (result.resultCode == RESULT_OK) {
            viewModel.verificarUsuarioAtual()
            Toast.makeText(this, "Autenticado com sucesso!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Autenticação necessária.", Toast.LENGTH_SHORT).show()
        }
    }
}