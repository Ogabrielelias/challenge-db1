package br.com.fiap.challange.screens

import android.annotation.SuppressLint
import android.text.TextUtils
import android.util.Patterns
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.navigation.NavController
import br.com.fiap.challange.Components.Input
import br.com.fiap.challange.R
import br.com.fiap.challange.database.repository.UserRepository
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen (navController: NavController) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        containerColor = Color.Transparent,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
                FormLogin(onSend = { status ->
                    var message = "Login efetuado!"
                    if(!status){
                        message = "E-mail e/ou Senha Inválido!"
                    } else{
                        navController.navigate("activity")
                    }
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message,
                            duration = SnackbarDuration.Short
                        )
                    }
                })
        }
    }
}

fun validateLoginInputs (email:String, senha:String) : Boolean {
    if(
        !TextUtils.isEmpty(email) &&
        Patterns.EMAIL_ADDRESS.matcher(email).matches() &&
        senha.length >= 8
        ) return true

    return false
}

@Composable
fun FormLogin(onSend: (status:Boolean) -> Unit){
    val context = LocalContext.current
    val userRepository = UserRepository(context)

    var error = remember { mutableStateOf(false) }

    val buttonDisabled = remember { mutableStateOf(true) }

    var emailValue = remember {
        mutableStateOf("")
    }

    var senhaValue = remember {
        mutableStateOf("")
    }

    Column (
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Input(
                label = "Email",
                value = emailValue.value,
                onChange = { value ->
                    emailValue.value = value
                    buttonDisabled.value = !validateLoginInputs(
                        value,
                        senhaValue.value
                    )
                    error.value = false
                },
                frontImage = R.drawable.mail,
                isError = error.value
            )

            Input(
                label = "Senha",
                value = senhaValue.value,
                onChange = { value ->
                    senhaValue.value = value
                    buttonDisabled.value = !validateLoginInputs(
                        emailValue.value,
                        value
                    )
                    error.value = false
                },
                type = "password",
                frontImage = R.drawable.lock,
                isError = error.value
            )
        }

        Button(
            onClick = { ->
                try{
                    val user = userRepository.getUserByLogin(
                        email = emailValue.value,
                        senha = senhaValue.value
                    )

                    if(user == null)  {
                        error.value = true
                    }else{
                        emailValue.value = ""
                        senhaValue.value = ""
                    }

                    onSend(user != null)
                }catch (err:Throwable) {
                   println(err)
                }
            },
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0XFF001CB0),
                disabledContainerColor = Color(0xFF1F2E7E),
                disabledContentColor = Color(0xFFC9C9C9)
            )
        ) {
            Text(
                "Entrar",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}