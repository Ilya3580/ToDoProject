package WorkerAndReceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import androidx.lifecycle.MutableLiveData

class SomeReceiver(
    private var mutableDataInternet: MutableLiveData<Boolean>,
) : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        mutableDataInternet.value = checkInternet(context)
    }

    companion object{
        fun checkInternet(context: Context?) : Boolean{
            val cm = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activityNetwork = cm.activeNetworkInfo
            return activityNetwork?.isConnectedOrConnecting == true
        }
    }
}