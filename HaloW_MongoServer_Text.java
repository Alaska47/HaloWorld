import com.mongodb.client.model.Filters;
//import com.mongodb.client.model.Filters.*;
import com.mongodb.client.*;
import com.mongodb.*;
import org.bson.*;
import javax.net.ssl.SSLSocketFactory;
import java.util.Arrays;
import java.lang.Math;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
//import javax.json.*;
import java.net.*;
//import com.google.android.gcm.server.Message;
//import com.google.android.gcm.server.MulticastResult;
//import com.google.android.gcm.server.Result;
//import com.google.android.gcm.server.Sender;
import com.twilio.sdk.*; 
import com.twilio.sdk.resource.factory.*; 
import com.twilio.sdk.resource.instance.*; 
import com.twilio.sdk.resource.list.*; 
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import java.util.List;

public class HaloW_MongoServer_Text
{
    public static final int portNumber = 1337; // l33t, git gud
    public static final String ACCOUNT_SID = "ACb***************************b0"; 
    public static final String AUTH_TOKEN = "77**************************8";   // CENSOR THESE FOR OPEN SOURCE CODE

    public static void main(String[] args) throws Exception {
        System.out.println("Initializing HaloW MongoServer...");
        System.out.println("Creating ServerSocket on port " + portNumber + " ...");
        
        System.out.println("Starting MongoClient...");
        MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
        MongoDatabase database = mongoClient.getDatabase("mydb");
        MongoCollection<Document> angelinfo = database.getCollection("angelinfo");
        MongoCollection<Document> angellocdata = database.getCollection("angelloc");
        MongoCollection<Document> reginfo = database.getCollection("reginfo");
        /*
         * DATABASE STORAGE FORMATS:
         * reginfo:
         * [phone], [name], [hashed password], [city]
         *
         * angelinfo
         * [phone], [name], [hashed password], [city], [range], [quals], [regId]
         *
         * angelloc
         * [phone], [curloc]
         *
         */
        System.out.println("Listening for Requests...");
        while(true) {
            ServerSocket serverSocket = new ServerSocket(portNumber);

            Socket socket = serverSocket.accept();
            OutputStream os = socket.getOutputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String req = br.readLine();
            if(req != null)
                System.out.println("received line: " + req);
            if(req != null && req.length() > 1) {
                String[] detailed = req.trim().split(",");
                if(detailed[0].equals("register")) {
                    System.out.println("Received [register] request:");
                    System.out.println("By index:");
                    for(int i = 0; i < detailed.length; i ++) {
                        System.out.println("[" + i + "]: " + detailed[i]);
                    }
                    System.out.println("Name: <<" + detailed[3] + ">>");
                    //System.out.println("req:" + req);
                    Document infodoc = new Document("phone",detailed[2]).append("name",detailed[3]).append("hashedpass",detailed[4]).append("city",detailed[5]);
                    if(detailed[1].equals("a")) { //register an angel
                        //TODO: separate qualifications into a list
                        //parse range as int

                        if(angelinfo.find(Filters.eq("phone",detailed[2])).first() != null) {
                            System.out.println("Error: Phone number already exists in Angel database, Registration Failed");
                        }
                        else {
                            infodoc = infodoc.append("range",detailed[6]).append("quals",detailed[7]).append("regId",detailed[8]);
                            angelinfo.insertOne(infodoc);
                            angellocdata.insertOne(new Document("phone",detailed[2]).append("curloc","None"));
                            System.out.println("Added to angelinfo");
                            System.out.println("New angel doc count: " + angelinfo.count());
                        }
                    } else {
                        if(reginfo.find(Filters.eq("phone",detailed[2])).first() != null) {
                            System.out.println("Error: Phone number already exists in Regular database, Registration Failed");
                        }
                        else {
                            reginfo.insertOne(infodoc);
                            System.out.println("Added to reginfo");
                            System.out.println("New reg doc count: " + reginfo.count());
                        }
                    }
                }
                else if(detailed[0].equals("updateloc")) {
                // format: updateloc , [phone#] , [location]
                    Document newLocDoc = new Document("phone",detailed[1]).append("curloc",detailed[2]);
                    angellocdata.findOneAndReplace(Filters.eq("phone",detailed[1]), newLocDoc);
                    System.out.println("Updated location of Angel w/ phone # " + detailed[1]);
                    System.out.println("New Location: " + detailed[2]);
                }
                else if(detailed[0].equals("report")) {
                // format: report, [phone], [location], [emergency type]
                    System.out.println("");
                    int emergType = Integer.parseInt(detailed[3]);
                    Double[] reportCoords = convertCoords(detailed[2]);
                    //String[] rlocation = detailed[1].split(":");
                    //double latitude = Double.parseDouble(rlocation[0]);
                    //double longitude = Double.parseDouble(rlocation[1]);
                    String citystr = "N/A";
                    System.out.println("Searching for Reporting User's City");
                    Document myDoc = reginfo.find(Filters.eq("phone",detailed[1])).first();
                    if(myDoc != null) {
                        citystr = (String) myDoc.get("city");
                        System.out.println("City identified as <<" + citystr + ">>");
                        ArrayList<Document> relevantAngels = new ArrayList<Document>();
                        System.out.println("Processing relevant Angels");
                        MongoCursor<Document> myIterator = angelinfo.find(Filters.eq("city",citystr)).iterator();
                        while(myIterator.hasNext()) {
                            relevantAngels.add(myIterator.next());
                        }
                        System.out.println("Found " + relevantAngels.size() + " Angels in the same city");
                        System.out.println("Identifying Angels that are within range");
                        ArrayList<Document> inRangeAngels = new ArrayList<Document>();
                        for(Document possAngel : relevantAngels) {
                            System.out.println(" - Trying Angel " + ((String) possAngel.get("name")) + " (" + ((String) possAngel.get("phone")) + ")");
                            String tPhone = (String) possAngel.get("phone");
                            Document thisCurLoc = angellocdata.find(Filters.eq("phone",tPhone)).first();
                            if(!((String) thisCurLoc.get("curloc")).equals("None")) {
                                Double[] thisCoords = convertCoords((String) thisCurLoc.get("curloc"));
                                double thisRange = Double.parseDouble((String) possAngel.get("range"));
                                System.out.println("    >  range=" + thisRange);
                                double actualDist = (getDist(reportCoords[0].doubleValue(), reportCoords[1].doubleValue(), thisCoords[0].doubleValue(), thisCoords[1].doubleValue()));
                                System.out.println("    >  dist=" + actualDist);
                                if(actualDist < thisRange) {
                                    System.out.println("     Checking if they have the quals ");
                                    if(((String) possAngel.get("quals")).charAt(1+emergType) == '1') {
                                        System.out.println("    Aw yiss");
                                        inRangeAngels.add(possAngel);
                                    }
                                }
                            }
                            else {
                                System.out.println("    nvm, Angel has no location listed");
                            }
                        }
                        System.out.println("Finished Checking, Identified " + inRangeAngels.size() + " Angels in Range");
                        for(Document aAngel : inRangeAngels) {
                            System.out.println("Sending text to Angel " + ((String) aAngel.get("name")) + " (phone # of " + ((String) aAngel.get("phone")) + ")");
                            String[] emergTypes = new String[] {"General Medical Care","Heimlich","Recovery Position","CPR","AED"};
                            sendSMS((String) aAngel.get("phone"), "There is an emergency happening nearby, the person needs [" + emergTypes[emergType] + "]");
                            sendSMS((String) aAngel.get("phone"), "http://maps.google.com/?q=" + reportCoords[0].doubleValue() + "," + reportCoords[1].doubleValue());
                            //System.out.println("Sending GCM Notification to...");
                            //String gcmid = (String) aAngel.get("regId");
                            //System.out.println("regId = " + gcmid);
                            //GCMsend(gcmid);//,"Somebody needs your help!, Coords=" + detailed[2]);
                        }
                    }
                }
            }
            socket.close();
            serverSocket.close();
        }
//        Document doc1 = new Document("name","Neil").append("GPA","4.0").append("Age","15");
//        Document doc2 = new Document("name","Aneesh").append("GPA","3.6").append("Age","16");
//        Document doc3 = new Document("name","Mihir").append("GPA","3.8").append("Age","16");
    }


    public static void sendSMS(String num, String msg) throws Exception {

        TwilioRestClient client = new TwilioRestClient(ACCOUNT_SID, AUTH_TOKEN); 
 
        // Build the parameters 
        List<NameValuePair> params = new ArrayList<NameValuePair>(); 
        params.add(new BasicNameValuePair("To", num)); 
        params.add(new BasicNameValuePair("From", "+12027437511"));  
        params.add(new BasicNameValuePair("Body", msg));   
 
        MessageFactory messageFactory = client.getAccount().getMessageFactory(); 
        Message message = messageFactory.create(params); 
        //System.out.println(message.getSid()); 
    }
    
    //public static void GCMsend(String to) throws Exception {
    //    Sender sender = new Sender("AIzaSyCCa0neANN-JJ95_TPXJ2G-SgzGh_PjInI");
    //    Message message = new Message.Builder().collapseKey("message").timeToLive(3).delayWhileIdle(true).addData("message","Somebody is Dying!@!@!").build();
    //    Result result = sender.send(message, to, 1);
    //    System.out.println("GCM Sent, result: " + result.toString());
    //}

/*    public static void GCMsend(String to,String msg){
        try {
            // Prepare JSON containing the GCM message content. What to send and where to send.
            JSONObject jGcmData = new JSONObject();
            JSONObject jData = new JSONObject();
            jData.put("message", msg);

            jGcmData.put("to", to);

            // What to send in GCM message.
            jGcmData.put("data", jData);

            // Create connection to send GCM Message request.
            URL url = new URL("https://android.googleapis.com/gcm/send");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Authorization", "key=" + API_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            // Send GCM message content.
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(jGcmData.toString().getBytes());

            // Read GCM response.
            InputStream inputStream = conn.getInputStream();
            String resp = IOUtils.toString(inputStream);
            System.out.println(resp);
            System.out.println("Check your device/emulator for notification or logcat for " +
                    "confirmation of the receipt of the GCM message.");
        } catch (IOException e) {
            System.out.println("Unable to send GCM message.");
            System.out.println("Please ensure that API_KEY has been replaced by the server " +
                    "API key, and that the device's registration token is correct (if specified).");
            e.printStackTrace();
        }
    }
*/


    
    public static Double[] convertCoords(String coordString) {
        String[] splitCoords = coordString.trim().split(":");
        double cLong = Double.parseDouble(splitCoords[0]);
        double cLat = Double.parseDouble(splitCoords[1]);
        return (new Double[] {new Double(cLong), new Double(cLat)});
    }
    
    
    // HELPER METHODS
    
    public static double getDist(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        //if (unit == 'K') {
        //dist = dist * 1.609344;
        //} else if (unit == 'N') {
        //  dist = dist * 0.8684;
        //}
        return (dist);
    }
        //}

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::  This function converts decimal degrees to radians             :*/
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    public static double deg2rad(double deg) {
      return (deg * Math.PI / 180.0);
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::  This function converts radians to decimal degrees             :*/
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    public static double rad2deg(double rad) {
      return (rad * 180.0 / Math.PI);
    }



}
