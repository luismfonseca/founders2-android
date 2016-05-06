package com.foundersfounders.doorservice;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;

public class OpenDoorService extends IntentService {
   private static final String OPEN_DOOR = "com.foundersfounders.doorservice.action.OPEN_DOOR";
   private static final String SLACK_OPEN_DOOR_REQUEST = URLEncoder.encode("@door-service: Open the door, please.");

   public OpenDoorService() {
      super("OpenDoorService");
   }

   public static Intent buildIntentOpenDoor(Context context) {
      Intent intent = new Intent(context, OpenDoorService.class);
      intent.setAction(OPEN_DOOR);
      return intent;
   }

   @Override
   protected void onHandleIntent(Intent intent) {
      if (intent != null) {
         final String action = intent.getAction();
         if (OPEN_DOOR.equals(action)) {
            handleDoorOpen();
         }
      }
   }

   private void sendToast(final String message, final int duration) {
      Handler handler = new Handler(Looper.getMainLooper());
      handler.post(new Runnable() {
         @Override
         public void run() {
            Toast.makeText(getApplicationContext(), message, duration).show();
         }
      });
   }

   private void handleDoorOpen() {
      try {
         final AuthorizationCodeFlow flow = SlackOAuth2Helper.getFlow(getBaseContext());
         final Credential credential = flow.loadCredential(SlackOAuth2Helper.CREDENTIALS_USER_ID);

         if (credential == null || credential.getAccessToken() == null) {
            sendToast("Sign in to slack first.", Toast.LENGTH_SHORT);
            Intent slackLoginIntent =
                  new Intent(getBaseContext(), SlackLoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            getApplication().startActivity(slackLoginIntent);
         }
         else {
            final URL url = new URL(
               "https://slack.com/api/chat.postMessage?channel=C0NBQVA85&text=" + SLACK_OPEN_DOOR_REQUEST
                     + "&link_names=1&as_user=true&token=" + credential.getAccessToken());

            HttpRequest request = flow.getTransport().createRequestFactory().buildGetRequest(new GenericUrl(url));

            HttpResponse response = request.execute();
            sendToast("Sent door open request!", Toast.LENGTH_SHORT);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(NotificationHelper.NOTIFICATION_ID);
         }
      } catch (IOException ignored) {
      }
   }
}
