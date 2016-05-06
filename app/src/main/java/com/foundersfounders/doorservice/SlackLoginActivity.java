package com.foundersfounders.doorservice;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.http.GenericUrl;

import java.io.IOException;

public class SlackLoginActivity extends AppCompatActivity {

   @SuppressLint("SetJavaScriptEnabled")
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      WebView webview = new WebView(this);
      webview.getSettings().setJavaScriptEnabled(true);
      webview.setVisibility(View.VISIBLE);
      setContentView(webview);

      try {
         final AuthorizationCodeFlow flow = SlackOAuth2Helper.getFlow(this.getBaseContext());

         webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
               super.onPageStarted(view, url, favicon);
               Log.d("", "onPageFinished : " + url);
               if (url.startsWith(flow.getTokenServerEncodedUrl())) {
                  String code = new GenericUrl(url).getFirst("code").toString();

                  OAuth2AttemptLoginTask.attemptLogin(flow, code, SlackLoginActivity.this);
                  view.stopLoading();
               }
            }
         });

         webview.loadUrl(flow.newAuthorizationUrl().build());
      } catch (IOException e) {
         SlackLoginActivity.this.finish();
         Log.e("SlackLogin", e.getMessage(), e);
      }
   }

   public static class OAuth2AttemptLoginTask extends AsyncTask<Void, Void, TokenResponse> {

      private AuthorizationCodeFlow flow;
      private String code;
      private Activity thisActivity;
      private OAuth2AttemptLoginTask(AuthorizationCodeFlow flow, String code, Activity thisActivity) {
         this.flow = flow;
         this.code = code;
         this.thisActivity = thisActivity;
      }

      public static void attemptLogin(AuthorizationCodeFlow flow, String code, Activity thisActivity) {
         new OAuth2AttemptLoginTask(flow, code, thisActivity).execute((Void) null);
      }

      @Override
      protected TokenResponse doInBackground(Void... params) {
         try {
            return flow.newTokenRequest(code).execute();
         } catch (IOException e) {
            return null;
         }
      }

      @Override
      protected void onPostExecute(TokenResponse response) {
         try {
            flow.createAndStoreCredential(response, SlackOAuth2Helper.CREDENTIALS_USER_ID);
            thisActivity.finish();

            Toast.makeText(
                  thisActivity.getApplicationContext(),
                  "You can now open the Founders² Door!",
                  Toast.LENGTH_LONG
            ).show();

            NotificationHelper.buildNotification(
                  thisActivity,
                  (NotificationManager) thisActivity.getSystemService(Context.NOTIFICATION_SERVICE));
         } catch (IOException ignored) {
         }
      }
   }
}
