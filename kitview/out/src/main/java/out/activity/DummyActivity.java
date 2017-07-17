package out.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.kitview.out.mobile.R;

import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import util.helper.XmlParser;

public class DummyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dummy);


        String path = getApplicationContext().getFilesDir().getAbsolutePath() + File.separator + "Config"+ File.separator + "kitpatient.xml";
        //System.out.println(path);
        try {
            XmlParser xmlParser = new XmlParser(path);

            System.out.println(xmlParser.getAddress().toString());
            System.out.println(xmlParser.getName());
            System.out.println(Arrays.toString(xmlParser.getDoctors().toArray()));
            System.out.println(xmlParser.getOpeningHours().toString());
            System.out.println(xmlParser.getKitviewServer().toString());
            System.out.println(xmlParser.getText());
            System.out.println(xmlParser.getContact().toString());

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("IOException");
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            System.out.println("XmlPullParserException");
        }


    }
}
