package iit.cnr.it.mailcheck;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by giacomo on 04/12/18.
 */

public class SpamCheck extends AsyncTask<Void, Void, Void> {


    String mail_content;
    SpamListener spamListener;
    int index_mail;

    public SpamCheck(String mail_content, int index_mail, SpamListener listener){
        this.mail_content = mail_content;
        this.spamListener = listener;
        this.index_mail = index_mail;
    }


    /**
     * Get address book from the mobile phone
     */
    private void checkSpam() {
        String response = "";
        String requestURL = "http://146.48.99.81:9999/spamClassifier/spam/classifier/upload/upload_string";
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
            httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setUseCaches(false);
            httpConn.setDoOutput(true); // indicates POST method
            httpConn.setRequestMethod("POST");
            httpConn.setRequestProperty("accept", "application/json");
            httpConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            request =  new DataOutputStream(httpConn.getOutputStream());
            request.writeBytes("&email_text=" + mail_content);
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
                System.out.println(response);
                httpConn.disconnect();
            } else {
                System.out.println("Server returned non-OK status: " + status);
            }
        } catch (Exception e) {
            System.out.println("Exception " + e.getMessage());
            e.printStackTrace();
            spamListener.SpamCheckCompleted(false, response, index_mail);
        }
        spamListener.SpamCheckCompleted(true, response, index_mail);
    }


    @Override
    protected Void doInBackground(Void... params) {
        checkSpam();
        return null;
    }
}