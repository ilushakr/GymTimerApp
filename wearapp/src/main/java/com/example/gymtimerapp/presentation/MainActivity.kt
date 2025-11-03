package com.example.gymtimerapp.presentation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeTextDefaults
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.ListHeader
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.example.gymtimerapp.R
import com.example.gymtimerapp.presentation.theme.GymTimerAppTheme
import com.example.shared.connectivity.data.WearableConnectionRepository
import com.example.shared.connectivity.data.WorkoutDataModel
import com.example.stopwatch.StopWatch
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable
import com.google.android.horologist.compose.layout.AppScaffold
import com.google.android.horologist.compose.layout.ColumnItemType
import com.google.android.horologist.compose.layout.ResponsiveTimeText
import com.google.android.horologist.compose.layout.rememberResponsiveColumnPadding
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.android.inject
import java.nio.charset.StandardCharsets


class MainActivity : ComponentActivity(), MessageClient.OnMessageReceivedListener,
    DataClient.OnDataChangedListener {


    private val wearableConnectionRepository by inject<WearableConnectionRepository>()

    private var onDataChanged by mutableStateOf("emp")

    override fun onDataChanged(dataEvents: DataEventBuffer) {
//        onDataChanged = dataEvents.toString()
        Log.d(TAG, "Received message: $dataEvents")
        for (event in dataEvents) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                // DataItem changed
                if (event.getDataItem().getUri().getPath() == PATH) {
                    val dataMapItem = DataMapItem.fromDataItem(event.getDataItem())
                    val receivedMessage = dataMapItem.getDataMap().getInt(KEY_MESSAGE)
                    onDataChanged = "Received: $receivedMessage"
                    Log.d(TAG, "Received message: " + receivedMessage)
                    // Process the received message here
                }
            }
        }
    }

    companion object {
        private const val TAG = "MyWearableListenerService"
        private const val PATH = "/my_data_path"
        private const val KEY_MESSAGE = "message_key"
    }


    private val TAG_MESSAGE_RECEIVED = "receive1"
    private val APP_OPEN_WEARABLE_PAYLOAD_PATH = "/APP_OPEN_WEARABLE_PAYLOAD"

    private var mobileDeviceConnected: Boolean = false


    // Payload string items
    private val wearableAppCheckPayloadReturnACK = "AppOpenWearableACK"

    private val MESSAGE_ITEM_RECEIVED_PATH: String = "/message-item-received"


    private var messageEvent: MessageEvent? = null
    private var mobileNodeUri: String? = null

    private var deviceconnectionStatusTv by mutableStateOf("deviceconnectionStatusTv")

    private var messagelogTextView by mutableStateOf("")

    // Implementation of one of the screens in the navigation
    @Composable
    fun MessageDetail(id: String) {
        // .. Screen level content goes here
        val scrollState = rememberTransformingLazyColumnState()

        val padding = rememberResponsiveColumnPadding(
            first = ColumnItemType.BodyText
        )

        ScreenScaffold(
            scrollState = scrollState,
            contentPadding = padding
        ) { scaffoldPaddingValues ->
            // Screen content goes here
            // [START_EXCLUDE]
            TransformingLazyColumn(
                state = scrollState,
                contentPadding = scaffoldPaddingValues
            ) {
                item {
                    Text(
                        text = id,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            // [END_EXCLUDE]
            // [END android_wear_navigation]
        }
    }

    @Composable
    fun MessageList(onMessageClick: (String) -> Unit) {
        val scrollState = rememberTransformingLazyColumnState()

        val padding = rememberResponsiveColumnPadding(
            first = ColumnItemType.ListHeader,
            last = ColumnItemType.Button
        )

        ScreenScaffold(scrollState = scrollState, contentPadding = padding) { contentPadding ->
            TransformingLazyColumn(
                state = scrollState,
                contentPadding = contentPadding
            ) {
                item {
                    ListHeader() {
                        Text(text = "message_list")
                    }
                }
                item {
                    Button(
                        onClick = { onMessageClick("message1") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Message 1")
                    }
                }
                item {
                    Button(
                        onClick = { onMessageClick("message2") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Message 2")
                    }
                }
            }
        }
    }

    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.setWorkout(intent.getParcelableExtra<WorkoutDataModel>(getString(R.string.workout_model_extra)))


        // TODO remove with Service with wakelock and foreground vibration
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        setContent {
            GymTimerAppTheme {
                val navController = rememberSwipeDismissableNavController()



//                MainScreen(onFinishWidgetClick = ::finishAffinity)

                AppScaffold(
                    timeText = {
                        ResponsiveTimeText(
                            timeTextStyle = TimeTextDefaults.timeTextStyle(
                                color = androidx.wear.compose.material3.MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                ) {
//                    SwipeDismissableNavHost(
//                        navController = navController,
//                        startDestination = "message_list"
//                    ) {
//                        composable("message_list") {
//                            MessageList(onMessageClick = { id ->
//                                navController.navigate("message_detail/$id")
//                            })
//                        }
//                        composable("message_detail/{id}") {
//                            MessageDetail(id = it.arguments?.getString("id")!!)
//                        }
//                    }

                    MainScreen(
                        Modifier.fillMaxSize(),
                        onFinishWidgetClick = ::finishAffinity
                    )
                }

//                ScalingLazyColumn(
////                    autoCentering = AutoCenteringParams(itemIndex = state.currentExerciseIndex),
//                    modifier = Modifier.fillMaxSize(),
////                    verticalArrangement = Arrangement.spacedBy(space = 16.dp, alignment = Alignment.Bottom)
//                ) {
//
//                    item {
//                        Text("on - ${wearableConnectionRepository.messageSharedFlow.collectAsStateWithLifecycle(WearableConnectionRepository.Message.Empty).value}")
//                    }
//
////                    item {
////                        Text("messagelogTextView - $messagelogTextView")
////                    }
////                    item {
////                        Button(
////                            onClick = {
////                                if(mobileDeviceConnected) {
////                                    val nodeId: String = messageEvent?.sourceNodeId!!
////                                    // Set the data of the message to be the bytes of the Uri.
////                                    val payload: ByteArray =
////                                        "messagecontentEditText".toByteArray()
////
////                                    // Send the rpc
////                                    // Instantiates clients without member variables, as clients are inexpensive to
////                                    // create. (They are cached and shared between GoogleApi instances.)
////                                    val sendMessageTask =
////                                        Wearable.getMessageClient(this@MainActivity)
////                                            .sendMessage(nodeId, MESSAGE_ITEM_RECEIVED_PATH, payload)
////
//////                                    binding.deviceconnectionStatusTv.visibility = View.GONE
////
////                                    sendMessageTask.addOnCompleteListener {
////                                        if (it.isSuccessful) {
////                                            Log.d("send1", "Message sent successfully")
////                                            val sbTemp = StringBuilder()
////                                            sbTemp.append("\n")
////                                            sbTemp.append("messagecontentEditText")
////                                            sbTemp.append(" (Sent to mobile)")
////                                            Log.d("receive1", " $sbTemp")
////                                            messagelogTextView = buildString {
////                                                append(messagelogTextView)
////                                                append(sbTemp)
////                                            }
////
////                                        } else {
////                                            Log.d("send1", "Message failed.")
////                                        }
////                                    }
////                                }
////                            }
////                        ) {
////                            Text("sendmessageButton")
////                        }
////                    }
//                }
//                AppScaffold(
//                    timeText = {
//                        ResponsiveTimeText(
//                            timeTextStyle = TimeTextDefaults.timeTextStyle(
//                                color = androidx.wear.compose.material3.MaterialTheme.colorScheme.primary
//                            )
//                        )
//                    }
//                ) {
//                    MainScreen(
//                        Modifier.fillMaxSize(),
//                        onFinishWidgetClick = {
//                            finishAffinity()
//                        }
//                    )
//                }
            }
        }
    }

    override fun onMessageReceived(p0: MessageEvent) {
        try {
            Log.d(TAG_MESSAGE_RECEIVED, "onMessageReceived event received")
            val s1 = String(p0.data, StandardCharsets.UTF_8)
            val messageEventPath: String = p0.path

            Log.d(
                TAG_MESSAGE_RECEIVED,
                "onMessageReceived() A message from watch was received:"
                        + p0.requestId
                        + " "
                        + messageEventPath
                        + " "
                        + s1
            )

            //Send back a message back to the source node
            //This acknowledges that the receiver activity is open
            if (messageEventPath.isNotEmpty() && messageEventPath == APP_OPEN_WEARABLE_PAYLOAD_PATH) {
                try {
                    // Get the node id of the node that created the data item from the host portion of
                    // the uri.
                    val nodeId: String = p0.sourceNodeId.toString()
                    // Set the data of the message to be the bytes of the Uri.
                    val returnPayloadAck = wearableAppCheckPayloadReturnACK
                    val payload: ByteArray = returnPayloadAck.toByteArray()

                    // Send the rpc
                    // Instantiates clients without member variables, as clients are inexpensive to
                    // create. (They are cached and shared between GoogleApi instances.)
                    val sendMessageTask =
                        Wearable.getMessageClient(this)
                            .sendMessage(nodeId, APP_OPEN_WEARABLE_PAYLOAD_PATH, payload)

                    Log.d(
                        TAG_MESSAGE_RECEIVED,
                        "Acknowledgement message successfully with payload : $returnPayloadAck"
                    )

                    messageEvent = p0
                    mobileNodeUri = p0.sourceNodeId

                    sendMessageTask.addOnCompleteListener {
                        if (it.isSuccessful) {
                            Log.d(TAG_MESSAGE_RECEIVED, "Message sent successfully")
//                            binding.messagelogTextView.visibility = View.VISIBLE

                            val sbTemp = StringBuilder()
                            sbTemp.append("\nMobile device connected.")
                            Log.d("receive1", " $sbTemp")
                            messagelogTextView = buildString {
                                append(messagelogTextView)
                                append(sbTemp)
                            }

                            mobileDeviceConnected = true

//                            binding.textInputLayout.visibility = View.VISIBLE
//                            binding.sendmessageButton.visibility = View.VISIBLE
//                            binding.deviceconnectionStatusTv.visibility = View.VISIBLE
                            deviceconnectionStatusTv = "Mobile device is connected"
                        } else {
                            Log.d(TAG_MESSAGE_RECEIVED, "Message failed.")
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }//emd of if
            else if (messageEventPath.isNotEmpty() && messageEventPath == MESSAGE_ITEM_RECEIVED_PATH) {
                try {
//                    binding.messagelogTextView.visibility = View.VISIBLE
//                    binding.textInputLayout.visibility = View.VISIBLE
//                    binding.sendmessageButton.visibility = View.VISIBLE
//                    binding.deviceconnectionStatusTv.visibility = View.GONE

                    val sbTemp = StringBuilder()
                    sbTemp.append("\n")
                    sbTemp.append(s1)
                    sbTemp.append(" - (Received from mobile)")
                    Log.d("receive1", " $sbTemp")
                    messagelogTextView = buildString {
                        append(messagelogTextView)
                        append(sbTemp)
                    }


                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val t = intent.getParcelableExtra<WorkoutDataModel>(getString(R.string.workout_model_extra))
        Log.d("sgkffdv", "onNewIntent -> ${t.toString()}")
    }

    override fun onResume() {
        super.onResume()
        Wearable.getDataClient(this).addListener(this)

        try {
//            Wearable.getDataClient(activityContext!!).addListener(this)
//            Wearable.getMessageClient(this).addListener(this)
//            Wearable.getCapabilityClient(activityContext!!)
//                .addListener(this, Uri.parse("wear://"), CapabilityClient.FILTER_REACHABLE)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        getKoin().get<StopWatch>().stopAndReset()
    }
}