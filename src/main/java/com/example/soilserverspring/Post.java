package com.example.soilserverspring;

public class Post {
        String base64Image;
        boolean colorChecker;
        boolean munsell;
        String email;

        public Post(String base64Image, String email, boolean munsell) {
                this.base64Image = base64Image;
                this.email = email;
                this.munsell = munsell;
        }

        public Post() { }

        public String getBase64Image() {
                return base64Image;
        }

        public boolean getMunsell() {
                return munsell;
        }

        public String getEmail() {
                return email;
        }

        public boolean getColorChecker() {
                return colorChecker;
        }

}