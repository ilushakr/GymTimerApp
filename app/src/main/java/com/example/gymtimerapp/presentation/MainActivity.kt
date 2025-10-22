package com.example.gymtimerapp.presentation

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.navigation.ModalBottomSheetLayout
import androidx.compose.material.navigation.bottomSheet
import androidx.compose.material.navigation.rememberBottomSheetNavigator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.gymtimerapp.navigation.NavigationManager
import com.example.gymtimerapp.navigation.Screens
import com.example.gymtimerapp.presentation.deleteexercise.DeleteExerciseScreen
import com.example.gymtimerapp.presentation.mainscreen.MainScreen
import com.example.gymtimerapp.presentation.newexercise.NewExerciseScreenBottomSheet
import com.example.gymtimerapp.presentation.newworkout.NewWorkoutScreenBottomSheet
import com.example.gymtimerapp.presentation.savedexerciselist.SavedExerciseListScreen
import com.example.gymtimerapp.presentation.savedworkoutlist.SavedWorkoutListScreen
import com.example.gymtimerapp.wearableutils.WatchConnectionManager
import com.example.presistent.api.PersistentWorkoutManager
import com.example.shared.connectivity.data.HandheldConnectionRepository
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataItem
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject
import org.koin.androidx.compose.koinViewModel
import java.nio.charset.StandardCharsets
import kotlin.random.Random


private
const val TAG = "DataSender"

private
const val PATH = "/my_data_path"

private
const val KEY_MESSAGE = "message_key"

@SuppressLint("VisibleForTests")
class MainActivity : ComponentActivity(), MessageClient.OnMessageReceivedListener,
    DataClient.OnDataChangedListener {

    private val persistentWorkoutManager by inject<PersistentWorkoutManager>()

    private val connectionManager by lazy {
        WatchConnectionManager(this)
    }

    private fun checkConnection() {
        lifecycleScope.launch {
            val isConnected = connectionManager.isPhoneConnected()
            updateConnectionStatus(isConnected)
        }
    }

    private var updateConnectionStatus by mutableStateOf("updateConnectionStatus")

    private fun updateConnectionStatus(isConnected: Boolean) {
        val text = if (isConnected) {
            // Phone is connected
            "Connected to phone"
        } else {
            // Phone is disconnected
            "Phone disconnected"
        }
        updateConnectionStatus = text
    }

    private lateinit var dataClient: DataClient
    private var count = 0


    override fun onDataChanged(dataEvents: DataEventBuffer) {
        dataEvents.forEach { event ->
            // DataItem changed
            if (event.type == DataEvent.TYPE_CHANGED) {
                event.dataItem.also { item ->
                    if (item.uri.path?.compareTo("/count") == 0) {
                        DataMapItem.fromDataItem(item).dataMap.apply {
                            updateCount(getInt(COUNT_KEY))
                        }
                    }
                }
            } else if (event.type == DataEvent.TYPE_DELETED) {
                // DataItem deleted
            }
        }
    }

    private var updateCount by mutableStateOf(0)

    // Method to update the count
    private fun updateCount(int: Int) {
        updateCount = int
    }

    private val capabilityClient by lazy { Wearable.getCapabilityClient(this) }
    private val messageClient by lazy { Wearable.getMessageClient(this) }

    private val START_ACTIVITY_PATH = "/start-activity"
    private val WEAR_CAPABILITY = "wear"

    // This method starts the Wearable app on the connected Wear device.
    // Alternative to this implementation, Horologist offers a DataHelper API which allows to
    // start the main activity or a different activity of your choice from the Wearable app
    // see https://google.github.io/horologist/datalayer-helpers-guide/#launching-a-specific-activity-on-the-other-device
    // for details
    private fun startWearableActivity() {
        lifecycleScope.launch {
            try {
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
                        messageClient.sendMessage(
                            node.id,
                            START_ACTIVITY_PATH, byteArrayOf()
                        )
                            .await()
                    }
                }.awaitAll()

                Log.d(TAG, "Starting activity requests sent successfully")
            } catch (cancellationException: kotlinx.coroutines.CancellationException) {
                throw cancellationException
            } catch (exception: Exception) {
                Log.d(TAG, "Starting activity failed: $exception")
            }
        }
    }


    fun sendData(message: String) {
        val putDataMapReq = PutDataMapRequest.create(PATH)
        putDataMapReq.dataMap.putInt(KEY_MESSAGE, Random.nextInt())
        putDataMapReq.setUrgent() // For immediate synchronization

        val putDataReq = putDataMapReq.asPutDataRequest()

        Wearable.getDataClient(this).putDataItem(putDataReq)
            .addOnSuccessListener(OnSuccessListener { dataItem: DataItem? ->
                Log.d(
                    TAG,
                    "Data sent successfully: " + dataItem!!.getUri()
                )
            })
            .addOnFailureListener(OnFailureListener { e: java.lang.Exception? ->
                Log.e(
                    TAG,
                    "Failed to send data: " + e!!.message
                )
            })
    }

    @Composable
    private fun Content() {
        val viewModel = koinViewModel<MainViewModel>()

        val state by viewModel.connectionState.collectAsStateWithLifecycle()

        val bottomSheetNavigator = rememberBottomSheetNavigator()
        val navController = rememberNavController(bottomSheetNavigator)

        LaunchedEffect(viewModel) {
            viewModel.navigationEvent.collectLatest { event ->
                when (event) {
                    is NavigationManager.NavigationEvent.NavigateToScreen -> {
                        navController.navigate(event.route)
                    }

                    is NavigationManager.NavigationEvent.NavigateUp -> {
//                        navController.currentBackStackEntry?.savedStateHandle.set()
                        navController.popBackStack()
//                        navController.popBackStack()
                    }
                }
            }
        }

        Column(
            modifier = Modifier.fillMaxSize(),
        ) {

            AnimatedVisibility(
                visible = state == HandheldConnectionRepository.ConnectionState.Disconnected,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp, vertical = 8.dp)
                        .background(Color.Red, RoundedCornerShape(8.dp))
                        .padding(16.dp),
                    text = "Wearables is disconnected. Please check connection again",
                    color = Color.White
                )
            }

            ModalBottomSheetLayout(
                modifier = Modifier
                    .imePadding()
                    .fillMaxWidth(),
                bottomSheetNavigator = bottomSheetNavigator,
                sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            ) {
                NavHost(
                    navController = navController,
                    startDestination = viewModel.startDestination
                ) {
                    composable(Screens.MainScreen.route) {
                        MainScreen()
//                        MainScreenOld(
//                            onAddClick = { navController.navigate("my_bottom_sheet") },
//                            onCardClick = { navController.navigate("my_bottom_sheet") }
//                        )
                    }

                    composable(Screens.SavedWorkoutListScreen.route) {
                        SavedWorkoutListScreen()
//                        MainScreenOld(
//                            onAddClick = { navController.navigate("my_bottom_sheet") },
//                            onCardClick = { navController.navigate("my_bottom_sheet") }
//                        )
                    }

                    composable(Screens.SavedExerciseListScreen.route) {
                        SavedExerciseListScreen()
                    }

                    dialog(
                        route = Screens.DeleteExerciseScreen.route + "/{uuid}",
                        arguments = listOf(
                            navArgument("uuid") { type = NavType.StringType }
                        )
                    ) { entry ->
                        entry.arguments?.getString("uuid")?.let { uuid ->
                            DialogWrapper {
                                DeleteExerciseScreen(uuid)
                            }
                        }
                    }

                    bottomSheet(Screens.NewExerciseScreen.route) {
                        BottomSheetWrapper(
                            modifier = Modifier
                                .fillMaxWidth()
                                .imePadding()
                        ) {
                            NewExerciseScreenBottomSheet()
                        }
                    }

                    bottomSheet(Screens.NewWorkoutScreen.route) {
                        BottomSheetWrapper(
                            modifier = Modifier
                                .fillMaxWidth()
                                .imePadding()
                        ) {
                            NewWorkoutScreenBottomSheet()
                        }
                    }
                }
            }
        }

//                val navController = rememberNavController()
//
//                NavHost(
//                    modifier = Modifier.fillMaxSize(),
//                    navController = navController,
//                    startDestination = Screens.NewExerciseScreen.route
//                ) {
//                    composable(route = Screens.NewExerciseScreen.route) {
//                        NewExerciseScreen()
//                    }
////                    composable(
////                        route = Screens.WeatherScreen.route + "/{weatherArgs}",
////                        arguments = listOf(
////                            navArgument("weatherArgs") {
////                                type = NavType.StringType
////                            }
////                        )
////                    ) { entry ->
////                        entry.arguments?.getString("weatherArgs")?.let { args ->
////                            WeatherInfoScreen(
////                                viewModel = daggerViewModel {
////                                    component.getWeatherViewModel()
////                                },
////                                weatherArgs = args,
////                                onNavigate = { event ->
////                                    when (event) {
////                                        is UiEvent.PopBackStack -> navController.popBackStack()
////                                        else -> Unit
////                                    }
////                                }
////                            )
////                        }
////                    }
//                }
    }

    private var wearableDeviceConnected: Boolean = false
    private val wearableAppCheckPayload = "AppOpenWearable"

    private var wearableNodeUri: String? = null

    private var currentAckFromWearForAppOpenCheck: String? = null
    private val wearableAppCheckPayloadReturnACK = "AppOpenWearableACK"

    private var deviceconnectionStatusTv by mutableStateOf("deviceconnectionStatusTv")
    private var sendmessageButton by mutableStateOf(false)

    private var messagecontentEditText by mutableStateOf(TextFieldValue(""))

    private var messageEvent: MessageEvent? = null

    private var messagelogTextView by mutableStateOf("")

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Content()
//                val scope = rememberCoroutineScope()
//
//                val viewModel = koinViewModel<MainViewModel>()
//
//                Column(Modifier.fillMaxSize()) {
//
//
////                    Text("updateConnectionStatus - ${connectionManager.state.collectAsStateWithLifecycle().value}")
////
////                    Button(
////                        onClick = {
////                            checkConnection()
//////                            val capabilityClient = Wearable.getCapabilityClient(this@MainActivity)
//////                             val capabilityName = "phone_feature" // Define this in both apps
//////
//////                            scope.launch {
//////                                updateConnectionStatus = try {
//////                                    val capabilityInfo = capabilityClient
//////                                        .getCapability(capabilityName, CapabilityClient.FILTER_REACHABLE)
//////                                        .await()
//////                                    capabilityInfo.nodes.isNotEmpty()
//////                                } catch (e: Exception) {
//////                                    false
//////                                }.toString()
//////
//////                            }
////                        }
////                    ) {
////                        Text("updateConnectionStatus")
////                    }
////
////
////                    Text("deviceconnectionStatusTv - $deviceconnectionStatusTv")
////                    if (sendmessageButton) {
////                        Button(
////                            onClick = {
////                                if (wearableDeviceConnected) {
////                                    if (messagecontentEditText.text.isNotEmpty()) {
////
////                                        val nodeId: String = messageEvent?.sourceNodeId!!
////                                        // Set the data of the message to be the bytes of the Uri.
////                                        val payload: ByteArray =
////                                            messagecontentEditText.text.toString().toByteArray()
////
////                                        // Send the rpc
////                                        // Instantiates clients without member variables, as clients are inexpensive to
////                                        // create. (They are cached and shared between GoogleApi instances.)
////                                        val sendMessageTask =
////                                            Wearable.getMessageClient(this@MainActivity)
////                                                .sendMessage(
////                                                    nodeId,
////                                                    MESSAGE_ITEM_RECEIVED_PATH,
////                                                    payload
////                                                )
////
////                                        sendMessageTask.addOnCompleteListener {
////                                            if (it.isSuccessful) {
////                                                Log.d("send1", "Message sent successfully")
////                                                val sbTemp = StringBuilder()
////                                                sbTemp.append("\n")
////                                                sbTemp.append(messagecontentEditText.text.toString())
////                                                sbTemp.append(" (Sent to Wearable)")
////                                                Log.d("receive1", " $sbTemp")
////                                                messagelogTextView = buildString {
////                                                    append(messagelogTextView)
////                                                    append(sbTemp)
////                                                }
////
////                                            } else {
////                                                Log.d("send1", "Message failed.")
////                                            }
////                                        }
////                                    } else {
////                                        Toast.makeText(
////                                            this@MainActivity,
////                                            "Message content is empty. Please enter some message and proceed",
////                                            Toast.LENGTH_SHORT
////                                        ).show()
////                                    }
////                                }
////                            }
////                        ) {
////                            Text("sendmessageButton")
////                        }
////                    }
////
////                    Text("messagelogTextView - $messagelogTextView")
////
////                    TextField(
////                        messagecontentEditText,
////                        {
////                            messagecontentEditText = it
////                        }
////                    )
//
//                    val list = persistentWorkoutManager.exercisesFlow()
//                        .collectAsStateWithLifecycle(emptyList()).value
//                    LazyColumn {
//                        items(list) {
//                            Text(it.toString(), modifier = Modifier.padding(16.dp))
//                        }
//                    }
//
//                    var m = remember { true }
//                    Button(
//                        onClick = {
////                            sendData("new data")
////                            viewModel.connect()
//                            scope.launch(Dispatchers.IO) {
////                                if(m){
////                                    persistentWorkoutManager.save(viewModel.wednesdayWorkout.exerciseList.first())
////                                    m = false
////                                }else {
////                                    persistentWorkoutManager.save(viewModel.fridayWorkout.exerciseList.first())
////                                }
//                            }
////                            if (!wearableDeviceConnected) {
////                                scope.launch {
////                                    initialiseDevicePairing(this@MainActivity)
////                                }
////                            }
//                        }
//                    ) {
//                        Text("Check for connected wearables")
//                    }
//                }
            }

        }

    }

    @SuppressLint("SetTextI18n")
    private suspend fun initialiseDevicePairing(tempAct: Activity) = coroutineScope {
        //Coroutine
        launch(Dispatchers.Default) {
            var getNodesResBool: BooleanArray? = null

            try {
                getNodesResBool = getNodes(tempAct.applicationContext)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            //UI Thread
            withContext(Dispatchers.Main) {
                if (getNodesResBool!![0]) {
                    //if message Acknowlegement Received
                    if (getNodesResBool[1]) {
                        Toast.makeText(
                            this@MainActivity,
                            "Wearable device paired and app is open. Tap the \"Send Message to Wearable\" button to send the message to your wearable device.",
                            Toast.LENGTH_LONG
                        ).show()
                        deviceconnectionStatusTv = "Wearable device paired and app is open."
//                        binding.deviceconnectionStatusTv.visibility = View.VISIBLE
                        wearableDeviceConnected = true
                        sendmessageButton = true
                    } else {
                        Toast.makeText(
                            this@MainActivity,
                            "A wearable device is paired but the wearable app on your watch isn't open. Launch the wearable app and try again.",
                            Toast.LENGTH_LONG
                        ).show()
                        deviceconnectionStatusTv = "Wearable device paired but app isn't open."
                        wearableDeviceConnected = false
                        sendmessageButton = false
                    }
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "No wearable device paired. Pair a wearable device to your phone using the Wear OS app and try again.",
                        Toast.LENGTH_LONG
                    ).show()
                    deviceconnectionStatusTv = "Wearable device not paired and connected."
                    wearableDeviceConnected = false
                    sendmessageButton = false
                }
            }
        }
    }

    private fun getNodes(context: Context): BooleanArray {
        val nodeResults = HashSet<String>()
        val resBool = BooleanArray(2)
        resBool[0] = false //nodePresent
        resBool[1] = false //wearableReturnAckReceived
        val nodeListTask = Wearable.getNodeClient(context).connectedNodes
        try {
            // Block on a task and get the result synchronously (because this is on a background thread).
            val nodes = Tasks.await(nodeListTask)
            Log.e(TAG_GET_NODES, "Task fetched nodes")
            for (node in nodes) {
                Log.e(TAG_GET_NODES, "inside loop")
                nodeResults.add(node.id)
                try {
                    val nodeId = node.id
                    // Set the data of the message to be the bytes of the Uri.
                    val payload: ByteArray = wearableAppCheckPayload.toByteArray()
                    // Send the rpc
                    // Instantiates clients without member variables, as clients are inexpensive to
                    // create. (They are cached and shared between GoogleApi instances.)
                    val sendMessageTask =
                        Wearable.getMessageClient(context)
                            .sendMessage(nodeId, APP_OPEN_WEARABLE_PAYLOAD_PATH, payload)
                    try {
                        // Block on a task and get the result synchronously (because this is on a background thread).
                        val result = Tasks.await(sendMessageTask)
                        Log.d(TAG_GET_NODES, "send message result : $result")
                        resBool[0] = true

                        //Wait for 700 ms/0.7 sec for the acknowledgement message
                        //Wait 1
                        if (currentAckFromWearForAppOpenCheck != wearableAppCheckPayloadReturnACK) {
                            Thread.sleep(100)
                            Log.d(TAG_GET_NODES, "ACK thread sleep 1")
                        }
                        if (currentAckFromWearForAppOpenCheck == wearableAppCheckPayloadReturnACK) {
                            resBool[1] = true
                            return resBool
                        }
                        //Wait 2
                        if (currentAckFromWearForAppOpenCheck != wearableAppCheckPayloadReturnACK) {
                            Thread.sleep(250)
                            Log.d(TAG_GET_NODES, "ACK thread sleep 2")
                        }
                        if (currentAckFromWearForAppOpenCheck == wearableAppCheckPayloadReturnACK) {
                            resBool[1] = true
                            return resBool
                        }
                        //Wait 3
                        if (currentAckFromWearForAppOpenCheck != wearableAppCheckPayloadReturnACK) {
                            Thread.sleep(350)
                            Log.d(TAG_GET_NODES, "ACK thread sleep 5")
                        }
                        if (currentAckFromWearForAppOpenCheck == wearableAppCheckPayloadReturnACK) {
                            resBool[1] = true
                            return resBool
                        }
                        resBool[1] = false
                        Log.d(
                            TAG_GET_NODES,
                            "ACK thread timeout, no message received from the wearable "
                        )
                    } catch (exception: Exception) {
                        exception.printStackTrace()
                    }
                } catch (e1: Exception) {
                    Log.d(TAG_GET_NODES, "send message exception")
                    e1.printStackTrace()
                }
            } //end of for loop
        } catch (exception: Exception) {
            Log.e(TAG_GET_NODES, "Task failed: $exception")
            exception.printStackTrace()
        }
        return resBool
    }

    override fun onMessageReceived(p0: MessageEvent) {
        try {
            val s =
                String(p0.data, StandardCharsets.UTF_8)
            val messageEventPath: String = p0.path
            Log.d(
                TAG_MESSAGE_RECEIVED,
                "onMessageReceived() Received a message from watch:"
                        + p0.requestId
                        + " "
                        + messageEventPath
                        + " "
                        + s
            )
            if (messageEventPath == APP_OPEN_WEARABLE_PAYLOAD_PATH) {
                currentAckFromWearForAppOpenCheck = s
                Log.d(
                    TAG_MESSAGE_RECEIVED,
                    "Received acknowledgement message that app is open in wear"
                )

                val sbTemp = StringBuilder()
                sbTemp.append(messagelogTextView.toString())
                sbTemp.append("\nWearable device connected.")
                Log.d("receive1", " $sbTemp")
                messagelogTextView = sbTemp.toString()
//                textInputLayout.visibility = View.VISIBLE

//                binding.checkwearablesButton.visibility = View.GONE
                messageEvent = p0
                wearableNodeUri = p0.sourceNodeId
            } else if (messageEventPath.isNotEmpty() && messageEventPath == MESSAGE_ITEM_RECEIVED_PATH) {

                try {
//                    binding.messagelogTextView.visibility = View.VISIBLE
//                    binding.textInputLayout.visibility = View.VISIBLE
//                    binding.sendmessageButton.visibility = View.VISIBLE

                    val sbTemp = StringBuilder()
                    sbTemp.append("\n")
                    sbTemp.append(s)
                    sbTemp.append(" - (Received from wearable)")
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
            Log.d("receive1", "Handled")
        }
    }

    override fun onResume() {
        super.onResume()

        Wearable.getDataClient(this).addListener(this)

        try {
//            Wearable.getDataClient(activityContext!!).addListener(this)
            Wearable.getMessageClient(this).addListener(this)
//            Wearable.getCapabilityClient(activityContext!!)
//                .addListener(this, Uri.parse("wear://"), CapabilityClient.FILTER_REACHABLE)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        private const val TAG_GET_NODES: String = "getnodes1"
        private const val APP_OPEN_WEARABLE_PAYLOAD_PATH = "/APP_OPEN_WEARABLE_PAYLOAD"
        private const val MESSAGE_ITEM_RECEIVED_PATH: String = "/message-item-received"
        private const val TAG_MESSAGE_RECEIVED: String = "receive1"


        private const val COUNT_KEY = "com.example.key.count"
    }

}