package com.telnyx.webrtc.composevoicesample.ui.login

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.telnyx.webrtc.composevoicesample.ui.shared.BaseViewModel
import com.telnyx.webrtc.composevoicesample.ui.shared.HeaderArea
import com.telnyx.webrtc.composevoicesample.ui.theme.ComposeVoiceSampleTheme
import com.telnyx.webrtc.sdk.CredentialConfig
import com.telnyx.webrtc.sdk.model.LogLevel

@Composable
fun LoginScreen(baseViewModel: BaseViewModel, navigationCallback: () -> Unit?) {
    val context = LocalContext.current

    LaunchedEffect(context) {
        connectAndObserve(baseViewModel, context) { navigationCallback() }
    }

    Surface(Modifier.fillMaxWidth(), color = MaterialTheme.colors.background) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(16.dp)
            ) {
                HeaderArea()
                LoginArea(baseViewModel)
            }
        }
    }
}


private fun connectAndObserve(
    baseViewModel: BaseViewModel,
    context: Context,
    navigationCallback: () -> Unit?
) {
    baseViewModel.initConnection(context)
    baseViewModel.observerResponses()
    baseViewModel.errorState.observeForever { errorMessage ->
        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
    }
    baseViewModel.loginState.observeForever { loginState ->
        if (loginState) {
            navigationCallback()
        }
    }
}

@Composable
fun LoginArea(baseViewModel: BaseViewModel) {
    var sipUsernameText by remember {
        mutableStateOf("")
    }
    var passwordText by remember {
        mutableStateOf("")
    }
    var callerIdText by remember {
        mutableStateOf("")
    }
    var callerIdNumText by remember {
        mutableStateOf("")
    }
    var clientStateText by remember {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier
            .padding(24.dp)
            .fillMaxWidth()
    ) {
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = sipUsernameText,
            onValueChange = { sipUsernameText = it },
            label = { Text("SIP Username") })
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = passwordText,
            onValueChange = { passwordText = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation()
        )
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = callerIdText,
            onValueChange = { callerIdText = it },
            label = { Text("Caller ID") })
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = callerIdNumText,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            onValueChange = { callerIdNumText = it },
            label = { Text("Caller ID number") })
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = clientStateText,
            onValueChange = { clientStateText = it },
            label = { Text("Client State") })

        Row(
            Modifier
                .padding(16.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Button(
                modifier = Modifier.fillMaxWidth(0.8f),
                onClick = {
                    baseViewModel.doLoginWithCredentials(
                        CredentialConfig(
                            sipUsernameText,
                            passwordText,
                            callerIdText,
                            callerIdNumText,
                            null,
                            null,
                            null,
                            LogLevel.ALL
                        )
                    )
                }) {
                Text(text = "Login")
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ComposeVoiceSampleTheme {
        LoginScreen(BaseViewModel()) {}
    }
}