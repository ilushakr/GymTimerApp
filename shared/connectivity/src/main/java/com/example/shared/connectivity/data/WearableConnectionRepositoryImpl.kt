package com.example.shared.connectivity.data

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.gymtimer.GymTimer
import com.example.shared.connectivity.data.WorkoutDataModel.Companion.toDataModel
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ConnectionRepositoryImpl(context: Context) : MessageClient.OnMessageReceivedListener,
    HandheldConnectionRepository,
    WearableConnectionRepository {

    private val nodeClient = Wearable.getNodeClient(context)
    private val capabilityClient = Wearable.getCapabilityClient(context)
    private val messageClient = Wearable.getMessageClient(context).apply {
        addListener(this@ConnectionRepositoryImpl)
    }

    private val scope = CoroutineScope(Dispatchers.Default)

    override val connectionState = MutableStateFlow(
        HandheldConnectionRepository.ConnectionState.Disconnected
    )

    override val messageSharedFlow = MutableSharedFlow<WearableConnectionRepository.Message>()

    init {
        nodeClient
            .connectedNodes
            .addOnSuccessListener { nodes ->
                connectionState.value = when (nodes.any { it.isNearby }) {
                    true -> HandheldConnectionRepository.ConnectionState.Connected
                    false -> HandheldConnectionRepository.ConnectionState.Disconnected
                }
            }.addOnFailureListener {
                connectionState.value = HandheldConnectionRepository.ConnectionState.Disconnected
            }
    }

    override fun startWearableActivity(workoutModel: GymTimer.WorkoutModel) {

        scope.launch {
            try {
                val dataModel = workoutModel.toDataModel()

                val nodes = capabilityClient
                    .getCapability(WEAR_CAPABILITY, CapabilityClient.FILTER_REACHABLE)
                    .await()
                    .nodes

                // Send a message to all nodes in parallel
                // If you need an acknowledge for the start activity use case, you can alternatively use
                // [MessageClient.sendRequest](https://developers.google.com/android/reference/com/google/android/gms/wearable/MessageClient#sendRequest(java.lang.String,%20java.lang.String,%20byte[]))
                // See an implementation in Horologist DataHelper https://github.com/google/horologist/blob/release-0.5.x/datalayer/core/src/main/java/com/google/android/horologist/data/apphelper/DataLayerAppHelper.kt#L210
                nodes.map { node ->
                    async {
                        messageClient
                            .sendMessage(
                                node.id,
                                START_ACTIVITY_PATH,
                                dataModel.byteArrayPresentation
                            )
                            .await()
                    }
                }.awaitAll()

                Log.d(TAG, "Starting activity requests sent successfully")
            } catch (cancellationException: CancellationException) {
                throw cancellationException
            } catch (exception: Exception) {
                Log.d(TAG, "Starting activity failed: $exception")
            }
        }
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        when (messageEvent.path) {
            START_ACTIVITY_PATH -> {
               scope.launch {
                   val model = WearableConnectionRepository.Message.NewWorkoutModel(
                       WorkoutDataModel.fromByteArray(messageEvent.data).domainModelPresentation
                   )
                   messageSharedFlow.emit(model)
               }
            }
        }
    }

//    private suspend fun initialiseDevicePairing(tempAct: Activity) = coroutineScope {
//        //Coroutine
//        launch(Dispatchers.Default) {
//            var getNodesResBool: BooleanArray? = null
//
//            try {
//                getNodesResBool = getNodes(tempAct.applicationContext)
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//
//            //UI Thread
//            withContext(Dispatchers.Main) {
//                if (getNodesResBool!![0]) {
//                    //if message Acknowlegement Received
//                    if (getNodesResBool[1]) {
//                        deviceconnectionStatusTv = "Wearable device paired and app is open."
////                        binding.deviceconnectionStatusTv.visibility = View.VISIBLE
//                        wearableDeviceConnected = true
//                        sendmessageButton = true
//                    } else {
//                        deviceconnectionStatusTv = "Wearable device paired but app isn't open."
//                        wearableDeviceConnected = false
//                        sendmessageButton = false
//                    }
//                } else {
//                    Toast.makeText(
//                        this@MainActivity,
//                        "No wearable device paired. Pair a wearable device to your phone using the Wear OS app and try again.",
//                        Toast.LENGTH_LONG
//                    ).show()
//                    deviceconnectionStatusTv = "Wearable device not paired and connected."
//                    wearableDeviceConnected = false
//                    sendmessageButton = false
//                }
//            }
//        }
//    }
//
//    private fun getNodes(context: Context): BooleanArray {
//        val nodeResults = HashSet<String>()
//        val resBool = BooleanArray(2)
//        resBool[0] = false //nodePresent
//        resBool[1] = false //wearableReturnAckReceived
//        val nodeListTask = Wearable.getNodeClient(context).connectedNodes
//        try {
//            // Block on a task and get the result synchronously (because this is on a background thread).
//            val nodes = Tasks.await(nodeListTask)
//            for (node in nodes) {
//                nodeResults.add(node.id)
//                try {
//                    val nodeId = node.id
//                    // Set the data of the message to be the bytes of the Uri.
//                    val payload: ByteArray = "wearableAppCheckPayload".toByteArray()
//                    // Send the rpc
//                    // Instantiates clients without member variables, as clients are inexpensive to
//                    // create. (They are cached and shared between GoogleApi instances.)
//                    val sendMessageTask =
//                        Wearable.getMessageClient(context)
//                            .sendMessage(nodeId, "APP_OPEN_WEARABLE_PAYLOAD_PATH", payload)
//                    try {
//                        // Block on a task and get the result synchronously (because this is on a background thread).
//                        val result = Tasks.await(sendMessageTask)
//                        resBool[0] = true
//
//                        //Wait for 700 ms/0.7 sec for the acknowledgement message
//                        //Wait 1
//                        if (currentAckFromWearForAppOpenCheck != wearableAppCheckPayloadReturnACK) {
//                            Thread.sleep(100)
//                            Log.d(TAG_GET_NODES, "ACK thread sleep 1")
//                        }
//                        if (currentAckFromWearForAppOpenCheck == wearableAppCheckPayloadReturnACK) {
//                            resBool[1] = true
//                            return resBool
//                        }
//                        //Wait 2
//                        if (currentAckFromWearForAppOpenCheck != wearableAppCheckPayloadReturnACK) {
//                            Thread.sleep(250)
//                            Log.d(TAG_GET_NODES, "ACK thread sleep 2")
//                        }
//                        if (currentAckFromWearForAppOpenCheck == wearableAppCheckPayloadReturnACK) {
//                            resBool[1] = true
//                            return resBool
//                        }
//                        //Wait 3
//                        if (currentAckFromWearForAppOpenCheck != wearableAppCheckPayloadReturnACK) {
//                            Thread.sleep(350)
//                            Log.d(TAG_GET_NODES, "ACK thread sleep 5")
//                        }
//                        if (currentAckFromWearForAppOpenCheck == wearableAppCheckPayloadReturnACK) {
//                            resBool[1] = true
//                            return resBool
//                        }
//                        resBool[1] = false
//                        Log.d(
//                            TAG_GET_NODES,
//                            "ACK thread timeout, no message received from the wearable "
//                        )
//                    } catch (exception: Exception) {
//                        exception.printStackTrace()
//                    }
//                } catch (e1: Exception) {
//                    Log.d(TAG_GET_NODES, "send message exception")
//                    e1.printStackTrace()
//                }
//            } //end of for loop
//        } catch (exception: Exception) {
//            Log.e(TAG_GET_NODES, "Task failed: $exception")
//            exception.printStackTrace()
//        }
//        return resBool
//    }


    companion object {
        const val START_ACTIVITY_PATH = "/start-activity"
        private const val WEAR_CAPABILITY = "wear"

        private const val TAG = "WearableConnectionRepositoryImplTag"
    }
}