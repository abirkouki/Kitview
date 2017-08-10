package util.helper;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Administrateur on 07/07/2017.
 */

public class XmlParser {

    public static class Address {
        public final String geographic;
        public final double latitude;
        public final double longitude;

        public Address(String geographic, double latitude, double longitude) {
            this.geographic = geographic;
            this.latitude = latitude;
            this.longitude = longitude;
        }

        @Override
        public String toString() {
            return "Address{" +
                    "geographic='" + geographic + '\'' +
                    ", latitude=" + latitude +
                    ", longitude=" + longitude +
                    '}';
        }
    }

    public static class Doctor {
        public final String firstname;
        public final String lastname;

        public Doctor(String firstname, String lastname) {
            this.firstname = firstname;
            this.lastname = lastname;
        }

        @Override
        public String toString() {
            return "Dr. " + firstname + " " + lastname;
        }
    }

    public static class OpeningHours {
        public final String monday;
        public final String tuesday;
        public final String wednesday;
        public final String thursday;
        public final String friday;
        public final String saturday;
        public final String sunday;

        public OpeningHours(String monday, String tuesday, String wednesday, String thursday, String friday, String saturday, String sunday) {
            this.monday = monday;
            this.tuesday = tuesday;
            this.wednesday = wednesday;
            this.thursday = thursday;
            this.friday = friday;
            this.saturday = saturday;
            this.sunday = sunday;
        }

        @Override
        public String toString() {
            return "OpeningHours{" +
                    "monday='" + monday + '\'' +
                    ", tuesday='" + tuesday + '\'' +
                    ", wednesday='" + wednesday + '\'' +
                    ", thursday='" + thursday + '\'' +
                    ", friday='" + friday + '\'' +
                    ", saturday='" + saturday + '\'' +
                    ", sunday='" + sunday + '\'' +
                    '}';
        }
    }

    public static class ConfigServer {
        public final String ip;
        public final String port;
        public final String orthalisAppPath;
        public final String chatSmooch;

        public ConfigServer(String ip, String port, String orthalisAppPath, String chatSmooch) {
            this.ip = ip;
            this.port = port;
            this.orthalisAppPath = orthalisAppPath;
            this.chatSmooch = chatSmooch;
        }

        @Override
        public String toString() {
            return "ConfigServer{" +
                    "ip='" + ip + '\'' +
                    ", port='" + port + '\'' +
                    ", orthalisAppPath='" + orthalisAppPath + '\'' +
                    ", chatSmooch='" + chatSmooch + '\'' +
                    '}';
        }
    }

    public static class Contact {
        public final String email;
        public final String tel;
        public final String website;

        public Contact(String email, String tel, String website) {
            this.email = email;
            this.tel = tel;
            this.website = website;
        }

        @Override
        public String toString() {
            return "Contact{" +
                    "email='" + email + '\'' +
                    ", tel='" + tel + '\'' +
                    ", website='" + website + '\'' +
                    '}';
        }
    }

    // We don't use namespaces
    private static final String ns = null;

    private Address address;
    private String name;
    private ArrayList<Doctor> doctors;
    private OpeningHours openingHours;
    private ConfigServer configServer;
    private String text;
    private Contact contact;

    public Address getAddress() {
        return address;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Doctor> getDoctors() {
        return doctors;
    }

    public OpeningHours getOpeningHours() {
        return openingHours;
    }

    public ConfigServer getConfigServer() {
        return configServer;
    }

    public String getText() {
        return text;
    }

    public Contact getContact() {
        return contact;
    }

    public XmlParser(String xmlFilePath) throws IOException, XmlPullParserException {
        InputStream xmlStream = new FileInputStream(xmlFilePath);
        doctors = new ArrayList<>();
        parse(xmlStream);
    }

    public void parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            readPractice(parser);
        } finally {
            in.close();
        }
    }

    private void readPractice(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "practice");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String tagName = parser.getName();
            switch (tagName) {
                case "address":
                    address = readAddress(parser);
                    break;
                case "name":
                    name = readLeaf(parser, "name");
                    break;
                case "doctor":
                    doctors.add(readDoctor(parser));
                    break;
                case "opening_hours":
                    openingHours = readOpeningHours(parser);
                    break;
                case "config_server":
                    configServer = readConfigServer(parser);
                    break;
                case "text":
                    text = readLeaf(parser, "text");
                    break;
                case "contact":
                    contact = readContact(parser);
                    break;
                default:
                    skip(parser);
                    break;
            }
        }
    }

    private String readLeaf(XmlPullParser parser, String tagName) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, tagName);
        String text = readLeafContent(parser);
        parser.require(XmlPullParser.END_TAG, ns, tagName);
        return text;
    }

    private String readLeafContent(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = null;
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

    private Address readAddress(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "address");//on veut le tag avec adresse
        String geographic = "";//initialisation
        double latitude = 0, longitude = 0;//initialisation
        while (parser.next() != XmlPullParser.END_TAG) {//tant qu'on n'est pas arrivé au tag de fin
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String tagName = parser.getName();//prend le tag sur lequel il arrive
            switch (tagName) {// cherche les tags
                case "geographic"://lit le contenu du tag 'geographic'
                    geographic = readLeaf(parser, "geographic");
                    break;
                case "latitude"://lit le contenu du tag 'latitude'
                    latitude = Double.parseDouble(readLeaf(parser, "latitude"));
                    break;
                case "longitude"://lit le contenu du tag 'longitude'
                    longitude = Double.parseDouble(readLeaf(parser, "longitude"));
                    break;
                default:
                    skip(parser);
                    break;
            }
        }//renvoie un objet Address
        return new Address(geographic,latitude,longitude);
    }

    private OpeningHours readOpeningHours(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "opening_hours");
        String monday = "", tuesday = "", wednesday = "", thursday = "", friday = "", saturday = "", sunday = "";
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String tagName = parser.getName();
            // looking for the tags
            switch (tagName) {
                case "monday":
                    monday = readLeaf(parser, "monday");
                    break;
                case "tuesday":
                    tuesday = readLeaf(parser, "tuesday");
                    break;
                case "wednesday":
                    wednesday = readLeaf(parser, "wednesday");
                    break;
                case "thursday":
                    thursday = readLeaf(parser, "thursday");
                    break;
                case "friday":
                    friday = readLeaf(parser, "friday");
                    break;
                case "saturday":
                    saturday = readLeaf(parser, "saturday");
                    break;
                case "sunday":
                    sunday = readLeaf(parser, "sunday");
                    break;
                default:
                    skip(parser);
                    break;
            }
        }
        return new OpeningHours(monday, tuesday, wednesday, thursday, friday, saturday, sunday);
    }

    private ConfigServer readConfigServer(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "config_server");
        String ip = "", port = "", orthalisAppPath = "", chatSmooch = "";
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String tagName = parser.getName();
            // looking for the tags
            switch (tagName) {
                case "ip_address":
                    ip = readLeaf(parser, "ip_address");
                    break;
                case "port":
                    port = readLeaf(parser, "port");
                    break;
                case "orthalis_app_path":
                    orthalisAppPath  = readLeaf(parser, "orthalis_app_path");
                    break;
                case "chat_smooch":
                    chatSmooch = readLeaf(parser, "chat_smooch");
                    break;
                default:
                    skip(parser);
                    break;
            }
        }
        return new ConfigServer(ip,port,orthalisAppPath,chatSmooch);
    }

    private Contact readContact(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "contact");
        String email = null, tel = null, website = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String tagName = parser.getName();
            // looking for the tags
            switch (tagName) {
                case "email":
                    email = readLeaf(parser, "email");
                    break;
                case "tel":
                    tel = readLeaf(parser, "tel");
                    break;
                case "website":
                    website = readLeaf(parser, "website");
                    break;
                default:
                    skip(parser);
                    break;
            }
        }
        return new Contact(email,tel,website);
    }

    private Doctor readDoctor(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "doctor");
        String firstname = "", lastname = "";
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String tagName = parser.getName();
            // looking for the tags
            switch (tagName) {
                case "firstname":
                    firstname = readLeaf(parser, "firstname");
                    break;
                case "lastname":
                    lastname = readLeaf(parser, "lastname");
                    break;
                default:
                    skip(parser);
                    break;
            }
        }
        return new Doctor(firstname, lastname);
    }


}
