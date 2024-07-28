package com.example.soilserverspring;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Base64;

import static com.example.soilserverspring.firebase.FirebaseService.addUserSample;

@RestController
@RequestMapping("/upload")
public class UploadController {

    public static String UPLOAD_DIRECTORY = System.getProperty("user.dir") + "/uploads";

    @PostMapping("/allMethods")
    public ResponseEntity<String> uploadImageAllMethods(@RequestBody Post post) throws IOException {
        System.out.println("time1 = " + String.valueOf(java.time.LocalDateTime.now()));

        Base64.Decoder decoder = Base64.getDecoder();
        byte[] image = decoder.decode(post.getBase64Image());
        String email = post.getEmail();

        Path fileNameAndPath = Paths.get(UPLOAD_DIRECTORY, email + "&image.jpeg");
        Files.write(fileNameAndPath, image);

        String[] answer = UploadService.imageUploadedAllMethods(fileNameAndPath.toString(), post.getMunsell());

        System.out.println("time2 = " + String.valueOf(java.time.LocalDateTime.now()));
        return getSoloPhotoStringResponse(post, answer);
    }

    @PostMapping("/allMethods/colorChecker")
    public ResponseEntity<String> uploadImageAllMethodsWithColorChecker(@RequestBody Post post) throws IOException {
        //System.out.println("time1 = " + String.valueOf(java.time.LocalDateTime.now()));
        String time = String.valueOf(java.time.LocalDateTime.now());

        Base64.Decoder decoder = Base64.getDecoder();
        String[] images = post.getBase64Image().split(";");
        String email = post.getEmail();

        byte[] colorChecker = decoder.decode(images[0]);
        byte[] image = decoder.decode(images[1]);

        Path fileNameAndPathColorChecker = Paths.get(UPLOAD_DIRECTORY, "colorChecker.jpeg");
        Files.write(fileNameAndPathColorChecker, colorChecker);
        Path fileNameAndPathImage = Paths.get(UPLOAD_DIRECTORY, email + "&image.jpeg");
        Files.write(fileNameAndPathImage, image);

        String[] answer = UploadService.imageUploadedAllMethodsWithColorChecker(
                fileNameAndPathColorChecker.toString(),
                fileNameAndPathImage.toString(), post.getMunsell());

        LocalDateTime time2 = java.time.LocalDateTime.now();

        // System.out.println("time2 = " + String.valueOf(java.time.LocalDateTime.now()));
        //System.out.println("res = " + (time - time2));
        return getSoloPhotoStringResponse(post, answer);
    }

    @PostMapping("/groupMethod")
    public ResponseEntity<String> uploadImageGroupMethod(@RequestBody Post post) throws IOException {
        System.out.println(String.valueOf(java.time.LocalDateTime.now()));
        Base64.Decoder decoder = Base64.getDecoder();
        double[] answer = new double[9];
        String email = post.getEmail();

        String color = "";
        String[] images = post.getBase64Image().split(";");
        System.out.println("amount of images = " + images.length);
        for (String s : images) {
            byte[] image = decoder.decode(s);
            Path fileNameAndPathImage = Paths.get(UPLOAD_DIRECTORY, email + "&image.jpeg");
            Files.write(fileNameAndPathImage, image);

            String[] str = UploadService.imageUploadedAllMethods(
                    fileNameAndPathImage.toString(),
                    post.getMunsell());

            for (int j = 0; j < str.length; j++) {
                if (post.getMunsell() && j == 3) {
                    color = str[j];
                } else {
                    answer[j] = Double.parseDouble(str[j]);
                }
            }
        }

        return getGroupPhotoStringResponse(post, answer, color, images);
    }

    @PostMapping("/groupMethod/colorChekcer")
    public ResponseEntity<String> uploadImageGroupMethodWithColorChecker(@RequestBody Post post) throws IOException {
        Base64.Decoder decoder = Base64.getDecoder();
        double[] answer = new double[9];
        String email = post.getEmail();

        String color = "";
        String[] images = post.getBase64Image().split(";");
        System.out.println("amount of images = " + images.length);
        for (int i = 0; i < images.length; i += 2) {

            byte[] colorChecker = decoder.decode(images[i]);
            byte[] image = decoder.decode(images[i + 1]);

            Path fileNameAndPathColorChecker = Paths.get(UPLOAD_DIRECTORY, email + "&colorChecker.jpeg");
            Files.write(fileNameAndPathColorChecker, colorChecker);
            Path fileNameAndPathImage = Paths.get(UPLOAD_DIRECTORY, email + "&image.jpeg");
            Files.write(fileNameAndPathImage, image);

            String[] str = UploadService.imageUploadedAllMethodsWithColorChecker(
                    fileNameAndPathImage.toString(),
                    fileNameAndPathColorChecker.toString(),
                    post.getMunsell());

            for (int j = 0; j < str.length; j++) {
                if (post.getMunsell() && j == 3) {
                    color = str[j];
                } else {
                    answer[j] = Double.parseDouble(str[j]);
                }
            }
        }

        return getGroupPhotoStringResponse(post, answer, color, images);
    }

    private ResponseEntity<String> getGroupPhotoStringResponse(
            Post post,
            double[] answer,
            String color,
            String[] images)
    {
        String[] answerString = new String[9];
        for (int j = 0; j < answer.length; j++) {
            if (post.getMunsell() && j == 3) {
                answerString[j] =  color;
            }
            answerString[j] = String.valueOf(Math.round(answer[j] / images.length * 100d) / 100d);
        }

        if (post.getMunsell()) {
            addUserSample(post, answerString, post.getBase64Image());
            return ResponseEntity.ok(String.format("min:%s;  max:%s; average:%s; H:%s;  V:%s; C:%s; H:%s;  S:%s; L:%s",
                    answerString[1], answerString[0], answerString[2], answerString[3], answerString[4],
                    answerString[5], answerString[6], answerString[7], answerString[8]));
        } else {
            addUserSample(post, answerString, post.getBase64Image());
            return ResponseEntity.ok(String.format("min:%s;  max:%s; average:%s;",
                    answerString[1], answerString[0], answerString[2]));
        }
    }

    private ResponseEntity<String> getSoloPhotoStringResponse(
            Post post,
            String[] answer)
    {

        if (post.getMunsell()) {
            addUserSample(post, answer, post.getBase64Image());
            return ResponseEntity.ok(String.format("min:%s;  max:%s; average:%s; H:%s;  V:%s; C:%s; H:%s;  S:%s; L:%s",
                    answer[1], answer[0], answer[2], answer[3], answer[4], answer[5], answer[6], answer[7], answer[8]));
        } else {
            addUserSample(post, answer, post.getBase64Image());
            return ResponseEntity.ok(String.format("min:%s;  max:%s; average:%s;",
                    answer[1], answer[0], answer[2]));
        }
    }

}
