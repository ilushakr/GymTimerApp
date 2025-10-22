package com.example.gymtimerapp.wearableutils

import android.content.Context
import com.google.android.gms.wearable.Node
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.tasks.await

class WatchConnectionManager(private val context: Context) {

    val state = MutableStateFlow(false)

    private val nodeClient = Wearable.getNodeClient(context)

    init {
        nodeClient.connectedNodes.addOnSuccessListener { nodes ->
            val isConnected = nodes.any { it.isNearby
//                    && !it.isLocal
            }
            state.value = isConnected
        }.addOnFailureListener {
            state.value = false
        }
    }

    suspend fun isPhoneConnected(): Boolean {
        return try {
            val connectedNodes = nodeClient.connectedNodes.await()
            connectedNodes.any { node ->
                node.isNearby
//                        && !node.isLocal
            }
        } catch (e: Exception) {
            false
        }
    }

    // Get all connected nodes
    suspend fun getConnectedNodes(): List<Node> {
        return try {
            nodeClient.connectedNodes.await()
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Listen for connection changes
    fun listenForConnectionChanges(callback: (Boolean) -> Unit) {
        nodeClient.connectedNodes.addOnSuccessListener { nodes ->
            val isConnected = nodes.any { it.isNearby
//                    && !it.isLocal
            }
            callback(isConnected)
        }.addOnFailureListener {
            callback(false)
        }
    }
}