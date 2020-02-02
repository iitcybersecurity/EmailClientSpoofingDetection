package iit.cnr.it.mailcheck;

import android.content.ContentResolver;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

public class MailManagerNew extends AsyncTask<Void, Void, Void> implements ContactsListener, SpamListener {
    public String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    String address, password, imap_port, imap_host;
    int NUM_MAX_MAIL, total_mail_number, spamChecked;
    private MailListener listener;
    ArrayList<Mail> mailReceivedList;
    ContentResolver cr;
    MetadataCheck metadataCheck;

    public MailManagerNew(String address, String password, String imap_host, String imap_port, int num_max_mail, int total_mail_number, MailListener listener, ContentResolver cr){
        this.address = address;
        this.password = password;
        this.imap_host = imap_host;
        this.imap_port = imap_port;
        this.NUM_MAX_MAIL = num_max_mail;
        this.total_mail_number = total_mail_number;
        this.listener = listener;
        this.cr = cr;
        mailReceivedList = new ArrayList<>();
        this.spamChecked = 0;
    }

    private void getEmailsReceived() {
        String response = "";
        String requestURL = "http://192.168.1.238:9999/flaskAPI/login";
        DataOutputStream request;
        HttpURLConnection httpConn;
        // creates a unique boundary based on time stamp
        URL url = null;
        try {
            url = new URL(requestURL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -60);
            Date dateStart = cal.getTime();
            SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
            //dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            String dateStartFormatted = dateFormat.format(dateStart);
            LoginDto login = new LoginDto(this.address, this.password, Integer.parseInt(this.imap_port), this.imap_host, dateStartFormatted);
            Gson gson = new Gson();
            String parameter = gson.toJson(login);

            httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setUseCaches(false);
            httpConn.setDoOutput(true); // indicates POST method
            httpConn.setRequestMethod("POST");
            httpConn.setRequestProperty("accept", "application/json");
            httpConn.setRequestProperty("Content-Type", "application/json");
            request =  new DataOutputStream(httpConn.getOutputStream());
            request.writeBytes(parameter);
            request.flush();

            int status = httpConn.getResponseCode();
            if (status == HttpURLConnection.HTTP_OK) {
                InputStream responseStream = new
                        BufferedInputStream(httpConn.getInputStream());

                BufferedReader responseStreamReader =
                        new BufferedReader(new InputStreamReader(responseStream));

                String line = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((line = responseStreamReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                responseStreamReader.close();

                response = stringBuilder.toString();
                Log.d("Response", response);
                System.out.println(response);
                httpConn.disconnect();
                addOnMailList(response);
                //listener.mailDownloadCompleted(false, "Server returned OK status: " + status);
            } else {
                System.out.println("Server returned non-OK status: " + status);
                listener.mailDownloadCompleted(false, "Server returned non-OK status: " + status);
            }
        } catch (Exception e) {
            System.out.println("Exception " + e.getMessage());
            e.printStackTrace();
            listener.mailDownloadCompleted(false, "Exception " + e.getMessage());
        }

    }

    private void addOnMailList(String response) throws JSONException, ParseException {
        try {
            if (response != null) {
                JSONArray messages = new JSONArray(response);

                for(int i = 0; i < messages.length(); i++) {
                    JSONObject msg = messages.getJSONObject(i);
                    Mail mail = new Mail();
                    //get sender addresses
                    String sender = msg.getString("Sender");
                    ArrayList<String> sender_addresses = new ArrayList<>();
                    sender_addresses.add(sender);
                    //check email
                    if (metadataCheck != null && metadataCheck.checkInAddressBook(sender))
                        mail.setInPhoneBook(true);
                    String subject = msg.getString("Subject");
                    String body = msg.getString("Body");
                    mail.setDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(msg.getString("Timestamp")));
                    mail.setSubject(subject);
                    mail.setSender(sender_addresses);
                    mail.setBody(body);
                    mailReceivedList.add(mail);
                    /*SpamCheck spamObj = new SpamCheck(subject + "\n" + body, i, this);
                    spamObj.execute();*/
                    Log.d("Message " + i, msg.toString());
                }
            }

            listener.mailLoadCompleted(mailReceivedList, total_mail_number);
        }catch(Exception mex){
            throw mex;
        }

    }

    private void getContacts(){
        Contacts contactObj = new Contacts(cr, this);
        contactObj.execute();
    }

    @Override
    protected Void doInBackground(Void... params) {
        System.out.println("Do in background");
        getContacts();
        getEmailsReceived();
        return null;
    }

    @Override
    public void readContactsCompleted(boolean status, String message, HashMap<String, ArrayList<String>> contactMap) {
        if(status) {
            metadataCheck = new MetadataCheck(contactMap);
            //getFolders();
            getEmailsReceived();
        }
    }

    @Override
    public void SpamCheckCompleted(boolean status, String result, int spamIndex) {
        System.out.println("MAIL: " + spamIndex + " " + result);
        spamChecked ++;
        System.out.println("SPAM CHECKED: " + spamChecked + "/" + total_mail_number);
        System.out.println("SPAM RESULT: " + result);
        JSONObject obj = null;
        if(result.length()>0) {
            try {
                JSONArray jsonarray = new JSONArray(result);
                for (int i = 0; i < jsonarray.length(); i++) {
                    JSONObject jsonobject = jsonarray.getJSONObject(i);
                    double spamProb = Double.parseDouble(jsonobject.getString("spamProbability"));
                    System.out.println("SPAM RESULT: " + spamProb);
                    mailReceivedList.get(total_mail_number-spamIndex-1).setSpam(spamProb);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if(spamChecked == total_mail_number)
            listener.mailLoadCompleted(mailReceivedList, total_mail_number);

    }
}
