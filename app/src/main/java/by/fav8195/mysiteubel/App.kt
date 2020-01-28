package by.fav8195.mysiteubel

import android.app.Application;
//import com.onesignal.OneSignal

public class App : Application() {
    override fun onCreate() {
        super.onCreate()
        // OneSignal Initialization
/*
        OneSignal.startInit(this)
            .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
            .unsubscribeWhenNotificationsAreDisabled(true)
            .init()
*/

    }

}
