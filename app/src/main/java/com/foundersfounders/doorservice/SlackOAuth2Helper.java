package com.foundersfounders.doorservice;

import android.content.Context;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

public class SlackOAuth2Helper {

   private static final String AUTHORIZATION_SERVER_URL = "https://slack.com/oauth/authorize";
   private static final String TOKEN_SERVER_URL = "https://slack.com/api/oauth.access";

   private static final String CLIENT_ID = "4355184355.29511006788";
   private static final String CLIENT_SECRET = "<very secret>";

   private static final String OAUTH_CREDENTIALS_FILENAME = ".oauth-credentials";
   private static final String REQUIRED_SCOPE = "chat:write:user";

   public static final String CREDENTIALS_USER_ID = "SlackUID";

   public static AuthorizationCodeFlow getFlow(Context context) throws IOException {
      File dataStorage = new File(context.getFilesDir(), OAUTH_CREDENTIALS_FILENAME);

      return new AuthorizationCodeFlow.Builder(
            BearerToken.queryParameterAccessMethod(),
            new NetHttpTransport(),
            new JacksonFactory(),
            new GenericUrl(TOKEN_SERVER_URL),
            new ClientParametersAuthentication(CLIENT_ID, CLIENT_SECRET),
            CLIENT_ID,
            AUTHORIZATION_SERVER_URL)
            .setDataStoreFactory(new FileDataStoreFactory(dataStorage))
            .setScopes(Collections.singleton(REQUIRED_SCOPE)).build();
   }
}
