package com.servicetitan.android.syncwire

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.squareup.wire.AnyMessage
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import sync.entities.CreateCounter
import sync.protocol.ClientMessage
import sync.protocol.ServerMessage

class MainActivity : AppCompatActivity() {

    private val client = GrpcProvider.provideSyncServiceClient()

    private lateinit var action: SendChannel<ClientMessage>
    private lateinit var dataSnapshot: ReceiveChannel<ServerMessage>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        attachToServer()
    }

    private fun attachToServer() {
        client.Synchronize().executeIn(GlobalScope).apply {
            action = first
            lifecycleScope.launch {
                second.consumeEach {
                    Log.d("HSING", "Received")
                }
            }
//            listenToServer()
            val createCounter = CreateCounter(54L, "Counter", 2)
            lifecycleScope.launch {
                action.send(
                    ClientMessage(
                        actionRequest = ClientMessage.ActionRequest(
                            "564564",
                            AnyMessage(
                                CreateCounter.ADAPTER.type?.qualifiedName.orEmpty(),
                                CreateCounter.ADAPTER.encodeByteString(createCounter)
                            )
                        )
                    )
                )
            }
        }
    }

    private fun listenToServer() {
        lifecycleScope.launch {
            dataSnapshot.consumeAsFlow().collect {
                Log.d("HSING", "Received")
            }
        }
    }
}