package iit.cnr.it.mailcheck;

import java.util.ArrayList;
import java.util.HashMap;

import javax.mail.Message;

/**
 * Created by giacomo on 05/12/18.
 */

public interface ContactsListener {
    void readContactsCompleted(boolean status, String message, HashMap<String, ArrayList<String>> contactMap);


}
