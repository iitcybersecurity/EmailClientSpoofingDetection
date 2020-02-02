package iit.cnr.it.mailcheck;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Store;

/**
 * Created by giacomo on 05/12/18.
 */

public class MailManager extends AsyncTask<Integer, Void, Void> implements ContactsListener, SpamListener{
    String address, password, imap_port, imap_host;
    int NUM_MAX_MAIL, total_mail_number, spamChecked;
    private MailListener listener;
    Store store;
    String inbox_folder = "INBOX";
    String sent_folder = "";
    ArrayList<Mail> mailReceivedList;
    ContentResolver cr;
    MetadataCheck metadataCheck;



    public MailManager(String address, String password, String imap_host, String imap_port, int num_max_mail, int total_mail_number, MailListener listener, ContentResolver cr){
        this.address = address;
        this.password = password;
        this.NUM_MAX_MAIL = num_max_mail;
        this.listener = listener;
        this.total_mail_number = total_mail_number;
        this.imap_host = imap_host;
        this.imap_port = imap_port;
        this.spamChecked = 0;
        mailReceivedList = new ArrayList<>();
        this.cr = cr;
    }

    private String getConnection(){
        Properties props = new Properties();

        props.setProperty("mail.imap.port", imap_port);
        props.setProperty("mail.store.protocol", "imaps");


        try {
            Session session = Session.getDefaultInstance(props, null);

            store = session.getStore("imaps");
            store.connect(imap_host, address, password);

        } catch (Exception mex) {
            mex.printStackTrace();
            return mex.getMessage();
        }
        return null;
    }

    private void checkConnection(){
        System.out.println("Check connection");
        String mex = getConnection();
        if(mex == null)
            listener.checkCompleted(true, "");
        else
            listener.checkCompleted(false, mex);
    }

    private Message[] getEmailsReceived(){
        getConnection();
        Folder inbox = null;
        Message[] messages = null;
        try {
            inbox = store.getFolder(inbox_folder);
            inbox.open(Folder.READ_ONLY);
            if (total_mail_number == 0) {
                total_mail_number = inbox.getMessageCount();
                if (inbox.getMessageCount() > NUM_MAX_MAIL)
                    messages = inbox.getMessages(inbox.getMessageCount() - NUM_MAX_MAIL, inbox.getMessageCount() - 1);
                else
                    messages = inbox.getMessages();
            } else {
                if (total_mail_number < inbox.getMessageCount()) {
                    messages = inbox.getMessages(total_mail_number + 1, inbox.getMessageCount());
                    total_mail_number = inbox.getMessageCount();
                }
            }

        } catch (MessagingException e) {
            e.printStackTrace();
        }
        addOnMailList(messages);
        //listener.mailLoadCompleted(messages, total_mail_number);
        return messages;
    }

    private void addOnMailList(Message[] messages) {
        try {
            if (messages != null) {
                for (int i = messages.length - 1; i >= 0; i--) {
                    Mail mail = new Mail();

                    final Message msg = messages[i];
                    //get sender addresses
                    javax.mail.Address[] addr_from = msg.getFrom();

                    String subject = msg.getSubject();

                    mail.setSubject(subject);

                    ArrayList<String> sender_addresses = new ArrayList<>();
                    for (final javax.mail.Address address : addr_from) {
                        mail.setDate(msg.getSentDate());
                        sender_addresses.add(address.toString());
                        //check email
                        if (metadataCheck.checkInAddressBook(address.toString().split("<")[1].split(">")[0]))
                            mail.setInPhoneBook(true);
                    }

                    mail.setSender(sender_addresses);
                    String body = "";
                    Object content = msg.getContent();
                    if (content instanceof String) {
                        body = (String) content;
                        System.out.println("CONTENT:" + body);
                    } else if (content instanceof Multipart) {
                        Multipart mp = (Multipart) content;
                        BodyPart bp = mp.getBodyPart(0);
                        body = (String) bp.getContent();
                        System.out.println("CONTENT:" + bp.getContent());
                    }
                    mail.setBody(body);
                    mailReceivedList.add(mail);
                    SpamCheck spamObj = new SpamCheck(subject + "\n" + body, i, this);
                    spamObj.execute();
                    }
                }
            }catch(Exception mex){

            }

        }



    private boolean openFolder(Folder folder, String folderToFind){
        try {
            folder.open(Folder.READ_ONLY);
            Message[] messages = null;

            if (folder.getMessageCount() > 0) {
                try {
                    messages = folder.getMessages(1, 1);
                    for(Message msg : messages){
                        javax.mail.Address[] in;
                        if(folderToFind.equals("sent")) {
                            in = msg.getFrom();
                            for (final javax.mail.Address addressFrom : in) {
                                System.out.println(addressFrom.toString());

                                System.out.println(addressFrom.toString().split("<")[1].split(">")[0]);
                                if (addressFrom.toString().split("<")[1].split(">")[0].equals(address))
                                    return true;
                            }
                        }
                        else {
                            in = msg.getAllRecipients();
                            for (final javax.mail.Address addressFrom : in) {
                                System.out.println(addressFrom.toString());
                                if (addressFrom.toString().equals(address))
                                    return true;
                            }
                        }
                    }
                }catch (Exception e){
                    System.out.println("impossible to read " + e.getMessage());
                }
            }
        }catch (Exception e){
            System.out.println("impossible to open");
            return false;
        }
        return false;
    }

    private String findFolder(String folderToFind){
        Folder[] f = new Folder[0];
        try {
            f = store.getDefaultFolder().list("*");

            for(Folder fd:f) {
                System.out.println(">> " + fd.getName());
                Folder folder = null;
                folder = store.getFolder(fd.getName());
                if(!openFolder(folder, folderToFind)){
                    f = store.getDefaultFolder().list(fd.getName() + "/*");
                    System.out.println(">> " + fd.getName() + "/*");
                    for(Folder fd_internal:f) {
                        folder = store.getFolder(fd.getName() + "/" + fd_internal.getName());
                        if(openFolder(folder, folderToFind))
                            return fd.getName() + "/" + fd_internal.getName();
                    }
                }
                else
                    return fd.getName();
            }
        } catch (MessagingException e) {
            e.printStackTrace();
            return "";
        }
        return "";
    }

    private void getFolders(){
        getConnection();
        String sentFolder = findFolder("sent");
        sent_folder = sentFolder;
    }

    private void getContacts(){
        Contacts contactObj = new Contacts(cr, this);
        contactObj.execute();
    }

    @Override
    protected Void doInBackground(Integer... params) {
        System.out.println("Do in background");

        int param1=params[0];
        //check connection to the imap server
        if(param1 == 0){
            checkConnection();
        }

        //get contacts and mails
        if(param1 == 1){
            getContacts();

        }

        //get only emails
        if(param1 == 2){
            getFolders();
            getEmailsReceived();
        }
        return null;
    }

    @Override
    public void readContactsCompleted(boolean status, String message, HashMap<String, ArrayList<String>> contactMap) {
        if(status) {
            metadataCheck = new MetadataCheck(contactMap);
            getFolders();
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