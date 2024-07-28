package com.example.soilserverspring.firebase;

import java.util.*;
import java.util.concurrent. ExecutionException;

import com.example.soilserverspring.Post;
import com.google.cloud.firestore.*;
import org.springframework.stereotype.Service;
import com.google.api.core.ApiFuture;
import com.google.firebase.cloud. FirestoreClient;
import org.json.JSONArray;

@Service
public class FirebaseService {

    public String saveUserDetails(String email) {
        System.out.println("Регистрация пользователя в базе данных, почта: " + email);
        Firestore dbFirestore = FirestoreClient.getFirestore();
        String time = String.valueOf(java.time.LocalDateTime.now());
        Map<String, String> line = new HashMap<>();
        line.put("created", time);

        ApiFuture<WriteResult> collectionsApiFuture = dbFirestore.collection("users").document(email)
                .create(line);
        System.out.println("Пользователь " + email + " создан");
        return collectionsApiFuture.toString();
    }

    public static String addUserSample(Post post, String[] answer, String image64) {
        String Cvalue = String.format("min:%s;  max:%s; average:%s", answer[1], answer[0], answer[2]);
        Sample sample = new Sample(Cvalue, image64, post.getColorChecker());

        if (answer.length > 3) {
            sample.setMunsellValue(String.format("H:%s;  V:%s; C:%s", answer[3], answer[4], answer[5]));
            sample.setHSL(String.format("H:%s;  S:%s; L:%s", answer[6], answer[7], answer[8]));
        }

        Firestore dbFirestore = FirestoreClient.getFirestore();
        String time = String.valueOf(java.time.LocalDateTime.now());
        dbFirestore.collection("users").document(post.getEmail()).collection("samples")
                .document(time).set(sample);
        return time;
    }

    public String deleteUserSample(String email, String sampleDate) {
        System.out.println("Удаление запроса с ID:\n" + sampleDate + "\nпользователя: " + email);

        Firestore dbFirestore = FirestoreClient.getFirestore();
        //System.out.println(sampleDate);
        ApiFuture<WriteResult> writeResult = dbFirestore.collection("users").document(email)
                .collection("samples").document(sampleDate).delete();

        return "Документ с ID "+sampleDate+" был удален";
    }

    public String getUserSamplesDetails(String name) throws InterruptedException, ExecutionException {
        Firestore dbFirestore = FirestoreClient.getFirestore();

        CollectionReference collectionReference = dbFirestore.collection("users").document(name).collection("samples");
        Iterable<DocumentReference> userSamples = collectionReference.listDocuments();

        List<DocumentReference> userSamplesReversed = new ArrayList<>();
        for (DocumentReference currentSample: userSamples) {
            userSamplesReversed.add(0, currentSample);
        }
        JSONArray list = new JSONArray();
        String jsonText;

        for (DocumentReference currentSample: userSamplesReversed) {
            ApiFuture<DocumentSnapshot> future = currentSample.get();
            DocumentSnapshot document = future.get();

            if (document.exists()) {
                Map<String, Object> pairChanged = new HashMap<>();
                Map<String, Object> documentData = document.getData();
                StringBuilder value = new StringBuilder();

                for (String param: Sample.paramNamesString) {
                    StringBuilder line = new StringBuilder(String.valueOf(documentData.get(param)));
                    if (Objects.equals(param, Sample.image64String)) {
                        String[] image64Arr = line.toString().split(";");
                        line = new StringBuilder(image64Arr[image64Arr.length - 1]);
                    }
                    if (Objects.equals(param, Sample.CvalueString) ||
                            (Objects.equals(param, Sample.HSLString) && line.length() > 4) ||
                            (Objects.equals(param, Sample.MunsellString)) && line.length() > 4) {
                        String[] cvalueArr = line.toString().split(";");
                        line = new StringBuilder();
                        for (String cvalueLine: cvalueArr) {
                            line.append(cvalueLine).append(",");
                        }
                        line.deleteCharAt(line.length() - 1);

                    }
                    value.append(line).append(";");
                }

                pairChanged.put(document.getId() + "Z", value);
                list.put(pairChanged);
            }
        }

        jsonText = list.toString();
        System.out.println("samples send");

        return jsonText;
    }

}
