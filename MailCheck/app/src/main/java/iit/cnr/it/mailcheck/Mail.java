package iit.cnr.it.mailcheck;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by giacomo on 18/12/18.
 */

public class Mail {
    private String body;
    private String subject;
    private ArrayList<String> sender;
    private Date date;
    private double spam;
    private double sender_malicious;
    private boolean isIstitutional;
    private boolean isInPhoneBook;
    private ArrayList<String> senderSimilarity;

    public Mail(){
        body = "";
        subject = "";
        sender = new ArrayList<>();
        date = null;
        spam = -1;
        sender_malicious = -1;
        isIstitutional = false;
        isInPhoneBook = false;
        senderSimilarity = new ArrayList<>();
    }

    public void setBody(String body){
        this.body = body;
    }

    public void setSubject(String subject){
        this.subject = subject;
    }


    public void setSender(ArrayList<String> sender){
        this.sender = sender;
    }

    public void setDate(Date date){
        this.date = date;
    }

    public void setSpam(double spam){
        this.spam = spam;
    }

    public void setSender_malicious(double sender_malicious){
        this.sender_malicious = sender_malicious;
    }

    public void setIstitutional(boolean isIstitutional){
        this.isIstitutional = isIstitutional;
    }

    public void setInPhoneBook(boolean isInPhoneBook){
        this.isInPhoneBook = isInPhoneBook;
    }

    public void setSenderSimilarity(ArrayList<String> senderSimilarity){
        this.senderSimilarity = senderSimilarity;
    }


    public String getBody(){
        return body;
    }

    public String getSubject(){
        return subject;
    }


    public ArrayList<String> getSender(){
        return sender;
    }

    public Date getDate(){
        return date;
    }

    public double getSpam(){
        return spam;
    }

    public double getSender_malicious(){
        return sender_malicious;
    }

    public boolean getIstitutional(){
        return isIstitutional;
    }

    public boolean getInPhoneBook(){
        return isInPhoneBook;
    }

    public ArrayList<String> getSenderSimilarity(){
        return senderSimilarity;
    }



}
