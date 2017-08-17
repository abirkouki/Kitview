package util.helper;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.orthalis.connect.R;

import util.app.AppController;

/**
 * Created by Administrateur on 03/08/2017.
 */

public class ActionBarHelper {


    public static void actionBarCustom(AppCompatActivity activity, boolean backArrow){
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(backArrow);
            actionBar.setLogo(R.drawable.logo98);
            actionBar.setDisplayUseLogoEnabled(true);
            actionBar.setTitle(AppController.practiceName);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
    }

    public static void actionBarOrthalis(AppCompatActivity activity){
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setLogo(R.drawable.logo98);
            actionBar.setDisplayUseLogoEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
    }

}
