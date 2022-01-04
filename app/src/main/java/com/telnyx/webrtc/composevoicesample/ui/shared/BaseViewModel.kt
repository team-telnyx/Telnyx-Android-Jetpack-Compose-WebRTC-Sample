package com.telnyx.webrtc.composevoicesample.ui.shared

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.telnyx.webrtc.sdk.Call
import com.telnyx.webrtc.sdk.CredentialConfig
import com.telnyx.webrtc.sdk.TelnyxClient
import com.telnyx.webrtc.sdk.model.SocketMethod
import com.telnyx.webrtc.sdk.model.TxServerConfiguration
import com.telnyx.webrtc.sdk.verto.receive.InviteResponse
import com.telnyx.webrtc.sdk.verto.receive.ReceivedMessageBody
import com.telnyx.webrtc.sdk.verto.receive.SocketObserver
import com.telnyx.webrtc.sdk.verto.receive.SocketResponse
import timber.log.Timber
import java.util.*

class BaseViewModel : ViewModel() {

    enum class CallState {
        INCOMING,
        ONGOING,
        IDLE,
    }

    enum class ConnectionState {
        CONNECTED,
        DISCONNECTED
    }

    private var telnyxClient: TelnyxClient? = null

    private var currentCall: Call? = null
    private var previousCall: Call? = null

    private lateinit var storedCredentialConfig: CredentialConfig

    val clientReadyState = MutableLiveData(false)
    val loginState = MutableLiveData(false)
    val errorState: MutableLiveData<String> = MutableLiveData()

    lateinit var incomingCallDetails: InviteResponse
    val callState: MutableLiveData<CallState> = MutableLiveData(CallState.IDLE)
    val connectionState: MutableLiveData<ConnectionState> =
        MutableLiveData(ConnectionState.DISCONNECTED)

    fun initConnection(context: Context, providedServerConfig: TxServerConfiguration? = null) {
        telnyxClient = TelnyxClient(context)
        providedServerConfig?.let {
            telnyxClient?.connect(it)
        } ?: run {
            telnyxClient?.connect()
        }
    }

    fun doLoginWithCredentials(credentialConfig: CredentialConfig) {
        telnyxClient?.credentialLogin(credentialConfig)
        storedCredentialConfig = credentialConfig
    }

    fun sendInvite(
        destinationNumber: String,
    ) {
        telnyxClient?.call?.newInvite(
            storedCredentialConfig.sipCallerIDName ?: "User",
            storedCredentialConfig.sipCallerIDNumber ?: "000000",
            destinationNumber,
            "State"
        )
    }

    fun acceptCall(callId: UUID, destinationNumber: String) {
        telnyxClient?.call?.acceptCall(callId, destinationNumber)
    }

    fun endCall(callId: UUID? = null) {
        callId?.let {
            telnyxClient?.call?.endCall(callId)
        } ?: run {
            val clientCallId = telnyxClient?.call?.callId
            clientCallId?.let { telnyxClient?.call?.endCall(it) }
        }
        previousCall?.let {
            currentCall = it
        }
    }

    fun observerResponses() {
        telnyxClient?.getSocketResponse()
            ?.observeForever(object : SocketObserver<ReceivedMessageBody>() {
                override fun onConnectionEstablished() {
                    // Handle a successfully established connection
                    Timber.i("BaseViewModel :: Connection Established")
                    connectionState.value = ConnectionState.CONNECTED
                }

                override fun onMessageReceived(data: ReceivedMessageBody?) {
                    when (data?.method) {
                        SocketMethod.CLIENT_READY.methodName -> {
                            Timber.i("BaseViewModel :: Client Ready")
                            // Fires once client has correctly been setup and logged into, you can now make calls.
                            clientReadyState.value = true
                        }

                        SocketMethod.LOGIN.methodName -> {
                            Timber.i("BaseViewModel :: Logged in")
                            loginState.value = true
                        }

                        SocketMethod.INVITE.methodName -> {
                            Timber.i("BaseViewModel :: Invite")
                            // Handle an invitation Update UI or Navigate to new screen, etc.
                            // Then, through an answer button of some kind we can accept the call with:
                            val inviteResponse = data.result as InviteResponse
                            incomingCallDetails = inviteResponse
                            callState.value = CallState.INCOMING
                        }

                        SocketMethod.ANSWER.methodName -> {
                            Timber.i("BaseViewModel :: Answer")
                            callState.value = CallState.ONGOING
                        }

                        SocketMethod.BYE.methodName -> {
                            Timber.i("BaseViewModel :: Bye")
                            callState.value = CallState.IDLE
                        }
                    }
                }

                override fun onLoading() {
                    Timber.i("BaseViewModel :: Loading")
                }

                override fun onError(message: String?) {
                    Timber.i("BaseViewModel :: Error :: $message")
                    errorState.value = message
                }

            })
    }
}