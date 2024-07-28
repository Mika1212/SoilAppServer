package com.example.soilserverspring.firebase;

import org.springframework.stereotype.Component;

@Component
public class Sample {

    public static String colorCheckerString = "colorChecker";
    public static String CvalueString = "cvalue";
    public static String HSLString = "hsl";
    public static String image64String = "image64";
    public static String MunsellString = "munsellValue";
    public static String[] paramNamesString = new String[] {"colorChecker", "cvalue", "hsl", "image64", "munsellValue"};


    private boolean colorChecker;
    private String Cvalue;
    private String HSL;
    private String Munsell;
    private String image64;


    public Sample() {
        this.colorChecker = true;
    }

    public Sample(String Munsell, String Cvalue, String HSL, String image64, boolean colorChecker) {
        this.Munsell = Munsell;
        this.Cvalue = Cvalue;
        this.HSL = HSL;
        this.colorChecker = colorChecker;
        this.image64 = image64;
    }

    public Sample(String Cvalue, String image64, boolean colorChecker) {
        this.Cvalue = Cvalue;
        this.image64 = image64;
        this.colorChecker = colorChecker;
    }

    public String getMunsellValue() {
        return Munsell;
    }
    public void setMunsellValue(String munsellValue) {
        this.Munsell = munsellValue;
    }
    public String getCvalue() {
        return Cvalue;
    }
    public void setCvalue(String cvalue) {
        this.Cvalue = cvalue;
    }
    public String getHSL() {
        return HSL;
    }
    public void setHSL(String HSL) {
        this.HSL = HSL;
    }
    public boolean getColorChecker() {
        return colorChecker;
    }
    public void setColorChecker(boolean colorChecker) {
        this.colorChecker = colorChecker;
    }
    public String getImage64() {
        return image64;
    }
    public void setImage64(String image64) {
        this.image64 = image64;
    }
}