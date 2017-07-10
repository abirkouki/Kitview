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
            return "Doctor{" +
                    "firstname='" + firstname + '\'' +
                    ", lastname='" + lastname + '\'' +
                    '}';
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

    public static class KitviewServer {
        public final String ip;
        public final String port;

        public KitviewServer(String ip, String port) {
            this.ip = ip;
            this.port = port;
        }

        @Override
        public String toString() {
            return "KitviewServer{" +
                    "ip='" + ip + '\'' +
                    ", port='" + port + '\'' +
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
    private KitviewServer kitviewServer;
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

    public KitviewServer getKitviewServer() {
        return kitviewServer;
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
            // looking for the tags
            if (tagName.equals("address")) {
                address = readAddress(parser);
            } else if(tagName.equals("name")) {
                name = readLeaf(parser, "name");
            } else if(tagName.equals("doctor")) {
                doctors.add(readDoctor(parser));
            } else if(tagName.equals("opening_hours")) {
                openingHours = readOpeningHours(parser);
            } else if(tagName.equals("kitview_server")) {
                kitviewServer = readKitviewServer(parser);
            } else if(tagName.equals("text")) {
                text = readLeaf(parser, "text");
            } else if(tagName.equals("contact")) {
                contact = readContact(parser);
            } else {
                skip(parser);
            }
        }
    }

    private String readLeaf(XmlPullParser parser, String tagName) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, tagName);
        String text = readLeafContent(parser);
        parser.require(XmlPullParser.END_TAG, ns, tagName);
        return text;
    }

    // For the tags title and summary, extracts their text values.
    private String readLeafContent(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
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
        parser.require(XmlPullParser.START_TAG, ns, "address");
        String geographic = "";
        double latitude = 0, longitude = 0;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String tagName = parser.getName();
            // looking for the tags
            if (tagName.equals("geographic")) {
                geographic = readLeaf(parser, "geographic");
            } else if(tagName.equals("latitude")) {
                latitude = Double.parseDouble(readLeaf(parser, "latitude"));
            } else if(tagName.equals("longitude")) {
                longitude = Double.parseDouble(readLeaf(parser, "longitude"));
            } else {
                skip(parser);
            }
        }
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
            if (tagName.equals("monday")) {
                monday = readLeaf(parser, "monday");
            } else if(tagName.equals("tuesday")) {
                tuesday = readLeaf(parser, "tuesday");
            } else if(tagName.equals("wednesday")) {
                wednesday = readLeaf(parser, "wednesday");
            } else if(tagName.equals("thursday")) {
                thursday = readLeaf(parser, "thursday");
            } else if(tagName.equals("friday")) {
                friday = readLeaf(parser, "friday");
            } else if(tagName.equals("saturday")) {
                saturday = readLeaf(parser, "saturday");
            } else if(tagName.equals("sunday")) {
                sunday = readLeaf(parser, "sunday");
            } else {
                skip(parser);
            }
        }
        return new OpeningHours(monday, tuesday, wednesday, thursday, friday, saturday, sunday);
    }

    private KitviewServer readKitviewServer(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "kitview_server");
        String ip = "", port = "";
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String tagName = parser.getName();
            // looking for the tags
            if (tagName.equals("ip")) {
                ip = readLeaf(parser, "ip");
            } else if(tagName.equals("port")) {
                port = readLeaf(parser, "port");
            } else {
                skip(parser);
            }
        }
        return new KitviewServer(ip,port);
    }

    private Contact readContact(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "contact");
        String email = "", tel = "", website = "";
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String tagName = parser.getName();
            // looking for the tags
            if (tagName.equals("email")) {
                email = readLeaf(parser, "email");
            } else if(tagName.equals("tel")) {
                tel = readLeaf(parser, "tel");
            } else if(tagName.equals("website")) {
                website = readLeaf(parser, "website");
            } else {
                skip(parser);
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
            if (tagName.equals("firstname")) {
                firstname = readLeaf(parser, "firstname");
            } else if(tagName.equals("lastname")) {
                lastname = readLeaf(parser, "lastname");
            } else {
                skip(parser);
            }
        }
        return new Doctor(firstname, lastname);
    }


}
