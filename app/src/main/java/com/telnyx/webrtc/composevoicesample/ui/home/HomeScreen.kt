package com.telnyx.webrtc.composevoicesample.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.telnyx.webrtc.composevoicesample.ui.shared.BaseViewModel
import com.telnyx.webrtc.composevoicesample.ui.shared.HeaderArea
import com.telnyx.webrtc.composevoicesample.ui.theme.ComposeVoiceSampleTheme

@Composable
fun HomeScreen(baseViewModel: BaseViewModel, navigationCallback: () -> Unit) {
    Surface(Modifier.fillMaxWidth(), color = MaterialTheme.colors.background) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Column {
                HeaderArea()
                StateArea(baseViewModel)
                CallArea(baseViewModel)
            }
        }
    }
}

@Composable
fun StateArea(baseViewModel: BaseViewModel) {
    val callState = baseViewModel.callState.observeAsState().value
    val connectionState = baseViewModel.connectionState.observeAsState().value
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            Modifier.padding(16.dp, 16.dp, 16.dp, 0.dp),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(Modifier.padding(6.dp)) {
                Text(text = "Socket: ", fontWeight = FontWeight.Bold)
                Text(text = connectionState!!.name)
            }
            Row(Modifier.padding(6.dp)) {
                Text(text = "Call State: ", fontWeight = FontWeight.Bold)
                Text(text = callState!!.name)
            }
        }
    }
}

@Composable
fun CallArea(baseViewModel: BaseViewModel) {
    val callState = baseViewModel.callState.observeAsState()
    when (callState.value) {
        BaseViewModel.CallState.IDLE -> HomeInviteArea(baseViewModel)
        BaseViewModel.CallState.INCOMING -> IncomingCall(baseViewModel)
        BaseViewModel.CallState.ONGOING -> OngoingCall(baseViewModel)
    }
}

@Composable
fun HomeInviteArea(baseViewModel: BaseViewModel) {
    var callDestination by remember {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier
            .padding(24.dp)
            .fillMaxWidth()
    ) {
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = callDestination,
            onValueChange = { callDestination = it },
            label = { Text("Enter a destination to call...") })
        Row(
            Modifier
                .padding(16.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Button(
                modifier = Modifier.fillMaxWidth(0.8f),
                onClick = { baseViewModel.sendInvite(callDestination) }) {
                Text(text = "Call")
            }
        }
    }
}

@Composable
fun IncomingCall(baseViewModel: BaseViewModel) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
        ) {
            val callDetails = baseViewModel.incomingCallDetails
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Text(
                    modifier = Modifier.padding(8.dp),
                    text = "Incoming Call from ${callDetails.callerIdName}"
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color(0xFF1890ff),
                        contentColor = Color.White
                    ),
                    onClick = {
                        baseViewModel.acceptCall(
                            callDetails.callId,
                            callDetails.callerIdNumber
                        )
                        baseViewModel.callState.postValue(BaseViewModel.CallState.ONGOING)
                    }) {
                    Text(text = "Accept")

                }
                Button(
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color(0xFFff4d4f),
                        contentColor = Color.White
                    ),
                    onClick = {
                        baseViewModel.endCall(callDetails.callId)
                        baseViewModel.callState.postValue(BaseViewModel.CallState.IDLE)
                    }) {
                    Text(text = "Decline")
                }
            }
        }
    }
}

@Composable
fun OngoingCall(baseViewModel: BaseViewModel) {
    Row(
        Modifier
            .padding(16.dp)
            .fillMaxWidth(), horizontalArrangement = Arrangement.Center
    ) {
        Button(
            modifier = Modifier.fillMaxWidth(0.8f),
            onClick = {
                baseViewModel.endCall()
                baseViewModel.callState.postValue(BaseViewModel.CallState.IDLE)
            }) {
            Text(text = "End")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ComposeVoiceSampleTheme {
        HomeScreen(BaseViewModel()) {}
    }
}