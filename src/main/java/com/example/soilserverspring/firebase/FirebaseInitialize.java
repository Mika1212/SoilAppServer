package com.example.soilserverspring.firebase;
import javax.annotation. PostConstruct;

import com.google.firebase.FirebaseOptions;
import org. springframework. stereotype. Service; import com.google.auth.oauth2.GoogleCredentials;
import com.google. firebase. FirebaseApp;

import java.io.FileInputStream;

@Service
public class FirebaseInitialize {
    @PostConstruct
    public void initialize() {
        try {
            FileInputStream serviceAccount =
                    new FileInputStream("./serviceAccount.json");
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl("https://soil-app-mobile.firebaseio.com")
                    .build();
            FirebaseApp.initializeApp(options);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}