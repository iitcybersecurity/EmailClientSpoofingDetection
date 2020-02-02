package iit.cnr.it.mailcheck;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class LoginDto {
    @SerializedName("email_address")
    @Expose
    private String EmailAddress;
    @SerializedName("password")
    @Expose
    private String Password;
    @SerializedName("email_port")
    @Expose
    private int EmailPort;
    @SerializedName("email_imap")
    @Expose
    private String EmailImap;
    @SerializedName("date_start")
    @Expose
    private String DateStart;

    public String getEmailAddress() {
        return EmailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.EmailAddress = emailAddress;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String passsword) {
        this.Password = passsword;
    }

    public int getEmailPort() {
        return EmailPort;
    }

    public void setEmailPort(int emailPort) {
        this.EmailPort = emailPort;
    }

    public String getEmailImap() {
        return EmailImap;
    }

    public void setEmailImap(String emailImap) {
        this.EmailImap = emailImap;
    }

    public String getDateStart() {
        return DateStart;
    }

    public void setDateStart(String dateStart) {
        this.DateStart = dateStart;
    }

    @Override
    public String toString() {
        return "{\"email_address\": " + EmailAddress + "," +
                "  \"password\": " + Password + "," +
                "  \"email_port\": " + EmailPort + "," +
                "  \"email_imap\": " + EmailImap + "," +
                "  \"date_start\": " + DateStart + "}";
         /*"{" +
                "EmailAddress='" + EmailAddress + '\'' +
                ", Password='" + Password + '\'' +
                ", EmailPort=" + EmailPort +
                ", EmailImap='" + EmailImap + '\'' +
                ", DateStart='" + DateStart + '\'' +
                '}';*/
    }

    LoginDto(String emailAddress, String password, int emailPort, String emailImap, String dateStart) {
        EmailAddress = emailAddress;
        Password = password;
        EmailPort = emailPort;
        EmailImap = emailImap;
        DateStart = dateStart;
    }
}
