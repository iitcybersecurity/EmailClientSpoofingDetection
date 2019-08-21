package iit.cnr.it.mailcheck;

import java.util.ArrayList;

import javax.mail.Message;

/**
 * Created by giacomo on 05/12/18.
 */

public interface MailListener {
    void checkCompleted(boolean status, String message);
    void mailLoadCompleted(ArrayList<Mail> messages, int total_mails);
}
