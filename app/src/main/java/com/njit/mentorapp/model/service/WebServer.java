package com.njit.mentorapp.model.service;

public class WebServer
{
    // TODO (developer) LoginLink needs to be reverted back to 'login.php' as soon as timeout issue is resolved

    private static String ROOT = "https://web.njit.edu/~kas58/mentorDemo/";

    public static String getROOT() {
        return ROOT;
    }

    public static String getLoginLink() {
        return ROOT + "Model/scrap.php";
    }

    public static String getRegisterLink() {
        return ROOT + "Model/register.php";
    }

    public static String getQueryLink() {
        return ROOT + "Model/index.php";
    }

    public static String getAcademicsLink() {
        return ROOT + "academics/index.html";
    }

    public static String getSocialCapitalLink() {
        return ROOT + "social-capital/index.html";
    }

    public static String getWellBeingLink() {
        return ROOT + "wellbeing/index.html";
    }

    public static String getTermsAndConditionsLink() {
        return ROOT + "academics/terms.html";
    }

    public static String getPrivacyPolicyLink() {
        return ROOT + "academics/privacy.html";
    }

    public static String getAboutUsLink() {
        return ROOT + "academics/about_us.html";
    }

    public static String getContactUsLink() {
        return ROOT + "academics/contact.html";
    }

    public static String getHowToLink() {
        return ROOT + "academics/how_to.html";
    }
}