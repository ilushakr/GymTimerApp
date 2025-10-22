package com.example.gymtimerapp

import android.util.Log
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.WearableListenerService


class MyWearableListenerService : WearableListenerService() {
    override fun onDataChanged(dataEvents: DataEventBuffer) {
        for (event in dataEvents) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                // DataItem changed
                if (event.getDataItem().getUri().getPath() == PATH) {
                    val dataMapItem = DataMapItem.fromDataItem(event.getDataItem())
                    val receivedMessage = dataMapItem.getDataMap().getString(KEY_MESSAGE)
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
}