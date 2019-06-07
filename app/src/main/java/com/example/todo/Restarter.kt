package com.example.todo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class Restarter : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.i(Restarter::class.java.simpleName, "Service Stops! Oooooooooooooppppssssss!!!!")
        context.startService(Intent(context, MyService::class.java))
    }
}
