package util.app;


import java.io.File;

/**
 * Created by orthalis on 06/06/2017.
 */

public class AppConfig{
    // Server user login url
    //TODO : chemin du fichier des scripts php
    public static String URL_LOGIN = "http://"+AppController.practiceKitviewServer.ip+File.separator+AppController.practiceKitviewServer.path+File.separator+"login.php";

    public static String URL_INSERT = "http://"+AppController.practiceKitviewServer.ip+File.separator+AppController.practiceKitviewServer.path+File.separator+"fcm_insert.php";

    public static String URL_RECUPERER_BALANCE = "http://"+AppController.practiceKitviewServer.ip+ File.separator+AppController.practiceKitviewServer.path+File.separator+"recuperer_balance.php";

    public static String URL_RECUPERER_PROCHAIN_RDV = "http://"+AppController.practiceKitviewServer.ip+ File.separator+AppController.practiceKitviewServer.path+File.separator+"recuperer_prochain_rdv.php";

    public static String URL_MODIF_SETTINGS = "http://"+AppController.practiceKitviewServer.ip+ File.separator+AppController.practiceKitviewServer.path+File.separator+"modif_settings.php";

    public static String URL_DELETE_USER = "http://"+AppController.practiceKitviewServer.ip+ File.separator+AppController.practiceKitviewServer.path+File.separator+"delete_user.php";


}