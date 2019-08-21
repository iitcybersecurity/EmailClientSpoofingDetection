package iit.cnr.it.mailcheck;

import android.os.AsyncTask;
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
import java.util.Map;

/**
 * Created by giacomo on 06/12/18.
 */

public class MetadataCheck {
    HashMap<String, ArrayList<String>> contactMap;

    public MetadataCheck(HashMap<String, ArrayList<String>> contactMap){
        this.contactMap = contactMap;
    }

    public boolean checkInAddressBook(String address){
        if(contactMap!=null) {
            for (Map.Entry<String, ArrayList<String>> entry : contactMap.entrySet()) {
                ArrayList<String> emails = entry.getValue();
                if(emails.contains(address))
                    return true;
            }
        }
        return false;
    }




    class RequestSpam extends AsyncTask<String, Void, String> {
        String response = "";

        protected String doInBackground(String... body) {
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
                request.writeBytes("&email_text=" + body[0]);
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
                return response;
            }
            return response;
        }

        protected void onPostExecute() {

        }
    }

    /**
     * Check if the mail is spam or not, basing on a spam email classifier
     * @return
     * @param body
     */
    public double spamCheck(String body){
        double spamProbability = 0;

        new RequestSpam().execute(body);

        return spamProbability;
    }

    /**
     * Check if the email address received belong to the list of email addrress already used in sent email
     * @param address
     * @return
     */
    public boolean checkInSent(String address){
        boolean inSent = false;
        return inSent;
    }

    /**
     * Check if the email address domain have been pwned
     * @param address
     * @return
     */
    public boolean checkDomainIntegrity(String address){
        boolean valid = false;
        return valid;
    }

    /**
     * Check if the email address domain belong to an istitutional domain
     * @param address
     * @return
     */
    public boolean checkInstitutionalDomain(String address){
        boolean isIstitutional = false;
        return isIstitutional;
    }

     /*
    private Map<String, Double> computeSimilarity2(List<String> contacts, String contactReceived, int preprocessing){
        double final_distance = 100;
        Map<String, Double> nearestContact;
        nearestContact = new HashMap<String, Double>();
        if(preprocessing == 2)
            contactReceived = contactReceived.split("@")[0];

        //Levenshtein l = new Levenshtein();
        //NormalizedLevenshtein l = new NormalizedLevenshtein();
        JaroWinkler l = new JaroWinkler();

        String nearest_contact = "";

        for (String contact_mail: contacts){
            if(preprocessing == 1)
                contact_mail = contact_mail.split("@")[0];

            double distance = l.distance(contactReceived, contact_mail);

            if(distance < final_distance) {
                final_distance = distance;
                nearest_contact = contact_mail;
            }
        }
        nearestContact.put(nearest_contact,final_distance);

        Object[] keys = nearestContact.keySet().toArray();
        double similarity = nearestContact.get(keys[0]);
        System.out.println(keys[0] + " " + similarity);
        return nearestContact;
    }

    private Map<String, Double> computeSimilarity(Map<String, String> contacts, String contactReceived, int preprocessing){
        double final_distance = 100;
        Map<String, Double> nearestContact;
        nearestContact = new HashMap<String, Double>();
        if(preprocessing == 2)
            contactReceived = contactReceived.split("@")[0];

        //Levenshtein l = new Levenshtein();
        //NormalizedLevenshtein l = new NormalizedLevenshtein();
        JaroWinkler l = new JaroWinkler();

        String nearest_contact = "";
        Set<String> keys = contacts.keySet();
        for(String key:keys) {

            if(preprocessing == 0) {
                String contact_mail = contacts.get(key);
                double distance = l.distance(contactReceived, contact_mail);
                System.out.println(contactReceived + " " + contact_mail);
                if(distance < final_distance) {
                    final_distance = distance;
                    nearest_contact = contact_mail;
                }
            }

        }

        nearestContact.put(nearest_contact,final_distance);

        Object[] key = nearestContact.keySet().toArray();
        double similarity = nearestContact.get(key[0]);
        System.out.println(key[0] + " " + similarity);
        return nearestContact;
    }

    public Object getElementByIndex(LinkedHashMap map, int index){
        return map.get( (map.keySet().toArray())[ index ] );
    }

    private double computeScore(String address) {

        double score = 100;
        System.out.println("----------" + address + "----------");
        String alias_mail = address.split("<")[0];
        String address_mail = address.split("<")[1].split(">")[0];
        //Similarity email address received, email addresses in contact list
        //Map<String, Double> nearestContact_mail_mail = computeSimilarity(contactsMail, address_mail, 0);
        //Similarity email address received, name profile in contact list
        //Map<String, Double> nearestContact_mail_name = computeSimilarity(contactsName, address_mail, 2);
        //Similarity username received, email addresses in contact list
        //Map<String, Double> nearestContact_name_mail = computeSimilarity(contactsMail, alias_mail, 1);
        //Similarity username received, name profile in contact list
        //Map<String, Double> nearestContact_name_name = computeSimilarity(contactsName, alias_mail, 0);

        //Similarity email address received, email addresses in contact list
        Map<String, Double> nearestContact_mail_mail = computeSimilarity(contacts, address_mail, 0);


        return score;
    }*/
}
