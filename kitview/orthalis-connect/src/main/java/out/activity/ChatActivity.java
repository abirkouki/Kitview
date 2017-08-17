package out.activity;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import io.smooch.core.Smooch;
import io.smooch.ui.ConversationActivity;

/**
 * Created by orthalis on 01/08/2017.
 */

public class ChatActivity extends Application {

    String APP_TOKEN = "7drfjg3gy0t9tm9izomsx7x72";

    @Override
    public void onCreate() {
        super.onCreate();
        Smooch.init(this, APP_TOKEN);
        /*
        Context context = getApplicationContext();
        ConversationActivity.show(context);
        */
    }
}
