package util.session;

/**
 * Created by orthalis on 06/06/2017.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;

public class SQLiteHandler extends SQLiteOpenHelper {

    private static final String TAG = SQLiteHandler.class.getSimpleName();

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "android_api";

    // Login table name
    private static final String TABLE_USER = "user";



    // Login Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NOM = "nom";
    private static final String KEY_UID = "uid";
    private static final String KEY_PRENOM = "prenom";

    //Params table name
    private static final String TABLE_PARAMS = "params";

    // Params Table Columns names
    private static final String KEY_DELTA = "delta";
    private static final String KEY_FAMILLE = "famille";

    // Notif table name
    private static final String TABLE_NOTIF = "notif";

    // Notif Table Columns names
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_DATE = "date";

    // Prochain Rdv table name
    //Cette table permet d'enregistrer le dernier message de prochain rdv pour qu'il soit dispo hors connexion
    private static final String TABLE_RDV = "rdv";
    // Prochain Rdv Columns names
    private static final String KEY_RDV = "rdv";
    private static final String KEY_DATE_MODIF= "date";
    private static final String KEY_LIBELLE= "libelle";
    private static final String KEY_DUREE= "duree";

    // Balance table name
    //Cette table permet d'enregistrer le dernier message de balance pour qu'il soit dispo hors connexion
    private static final String TABLE_BALANCE = "balance";
    // Balance Columns names
    private static final String KEY_BALANCE = "balance";

    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_NOM + " TEXT," + KEY_UID + " TEXT,"
                + KEY_PRENOM + " TEXT"+ ")";
        String CREATE_NOTIF_TABLE = "CREATE TABLE " + TABLE_NOTIF + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_MESSAGE + " TEXT," + KEY_DATE + " TEXT"
                + ")";
        String CREATE_RDV_TABLE = "CREATE TABLE " + TABLE_RDV + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_RDV + " TEXT," + KEY_DATE_MODIF + " TEXT," + KEY_LIBELLE + " TEXT,"+ KEY_DUREE + " TEXT"
                + ")";
        String CREATE_BALANCE_TABLE = "CREATE TABLE " + TABLE_BALANCE + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_BALANCE + " TEXT,"+ KEY_DATE_MODIF + " TEXT"
                + ")";
        String CREATE_PARAMS_TABLE = "CREATE TABLE " + TABLE_PARAMS + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_DELTA + " TEXT,"+ KEY_FAMILLE + " TEXT"
                + ")";

        db.execSQL(CREATE_LOGIN_TABLE);
        db.execSQL(CREATE_NOTIF_TABLE);
        db.execSQL(CREATE_RDV_TABLE);
        db.execSQL(CREATE_BALANCE_TABLE);
        db.execSQL(CREATE_PARAMS_TABLE);
        Log.d(TAG, "Database tables created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIF);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RDV);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BALANCE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PARAMS);
        onCreate(db);
    }

    /**
     * Storing user details in database
     * */
    public void addUser(String prenom, String nom, String uid) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NOM, nom);
        values.put(KEY_UID, uid);
        values.put(KEY_PRENOM, prenom);



        // Inserting Row
        long id = db.insert(TABLE_USER, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New user inserted into sqlite: " + id);
    }

    public void addNotif(String message, String date) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_MESSAGE, message);
        values.put(KEY_DATE, date);



        // Inserting Row
        long id = db.insert(TABLE_NOTIF, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New notif inserted into sqlite: " + id);
    }

    public void addRdv(String message, String date, String libelle, String duree) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_RDV, message);
        values.put(KEY_DATE_MODIF, date);
        values.put(KEY_LIBELLE, libelle);
        values.put(KEY_DUREE, duree);


        // Inserting Row

        deleteRdv();
        long id = db.insert(TABLE_RDV, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New rdv inserted into sqlite: " + id);
    }
    public void addBalance(String balance, String date) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_BALANCE, balance);
        values.put(KEY_DATE_MODIF, date);


        // Inserting Row

        deleteBalance();
        long id = db.insert(TABLE_BALANCE, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New balance inserted into sqlite: " + id);
    }

    public void addParams(String delta, String famille) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_DELTA, delta);
        values.put(KEY_FAMILLE, famille);


        // Inserting Row
        deleteParams();
        long id = db.insert(TABLE_PARAMS, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New params inserted into sqlite: " + id);
    }

    /**
     * Getting user data from database
     * */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + TABLE_USER;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user.put("nom", cursor.getString(1));
            user.put("uid", cursor.getString(2));
            user.put("prenom", cursor.getString(3));

        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching user from Sqlite: " + user.toString());

        return user;
    }
    /**
     * Getting notif data from database
     * */
    public String[][] getNotifDetails() {
        String[][] notif = new String[10][2];
        String selectQuery = "SELECT  * FROM " + TABLE_NOTIF;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        int i = 0;
        // Move to first row

        if (cursor.getCount() > 0) {
            cursor.moveToLast();
            do {
                notif[i][0] = (cursor.getString(1));
                notif[i][1] = (cursor.getString(2));
                i++;
                cursor.moveToPrevious();
            } while (!cursor.isBeforeFirst() && i < 10);
        }
        cursor.close();
        db.close();
        // return notif
        Log.d(TAG, "Fetching user from Sqlite: " + notif.toString());

        return notif;

    }

    public String[][] getRdvDetails() {
        String[][] rdv = new String[1][4];
        String selectQuery = "SELECT  * FROM " + TABLE_RDV;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);



        if (cursor.getCount() > 0) {
            cursor.moveToFirst();

            rdv[0][0] = (cursor.getString(1));
            rdv[0][1] = (cursor.getString(2));
            rdv[0][2] = (cursor.getString(3));
            rdv[0][3] = (cursor.getString(4));

        }
        cursor.close();
        db.close();
        // return notif
        Log.d(TAG, "Fetching user from Sqlite: " + rdv.toString());

        return rdv;
    }

    public String[][] getBalanceDetails() {
        String[][] balance = new String[1][2];
        String selectQuery = "SELECT  * FROM " + TABLE_BALANCE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);



        if (cursor.getCount() > 0) {
            cursor.moveToFirst();

            balance[0][0] = (cursor.getString(1));
            balance[0][1] = (cursor.getString(2));

        }
        cursor.close();
        db.close();
        // return notif
        Log.d(TAG, "Fetching user from Sqlite: " + balance.toString());

        return balance;
    }

    public String[][] getParamsDetails() {
        String[][] param = new String[1][2];
        String selectQuery = "SELECT  * FROM " + TABLE_PARAMS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);



        if (cursor.getCount() > 0) {
            cursor.moveToFirst();

            param[0][0] = (cursor.getString(1));
            param[0][1] = (cursor.getString(2));

        }
        cursor.close();
        db.close();


        Log.d(TAG, "Fetching user from Sqlite: " + param.toString());
        return param;
    }

    /**
     * Re crate database Delete all tables and create them again
     * */
    public void deleteUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_USER, null, null);
        db.close();

        Log.d(TAG, "Deleted all user info from sqlite");
    }
    public void deleteNotifs() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_NOTIF, null, null);
        db.close();

        Log.d(TAG, "Deleted all notification info from sqlite");
    }

    public void deleteRdv() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_RDV, null, null);
        //db.close();

        Log.d(TAG, "Deleted all rdv info from sqlite");
    }

    public void deleteBalance() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_BALANCE, null, null);
        //db.close();

        Log.d(TAG, "Deleted all balance info from sqlite");
    }

    public void deleteParams() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_PARAMS, null, null);
        //db.close();

        Log.d(TAG, "Deleted all params info from sqlite");
    }
}