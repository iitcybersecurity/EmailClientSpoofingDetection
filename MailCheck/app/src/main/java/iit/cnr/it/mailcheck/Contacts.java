package iit.cnr.it.mailcheck;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by giacomo on 04/12/18.
 */

public class Contacts extends AsyncTask<Void, Void, Void> {

    HashMap<String, ArrayList<String>> contactMap;
    ContentResolver cr;
    private ContactsListener listener;

    public Contacts(ContentResolver cr, ContactsListener listener){
        this.contactMap = new HashMap<>();
        this.cr = cr;
        this.listener = listener;
    }


    /**
     * Get address book from the mobile phone
     */
    private void getContactsName() {
        try {
            // Get the Cursor of all the contacts
            Cursor cursorName = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

            cursorName.moveToFirst();
            // Move the cursor to first. Also check whether the cursor is empty or not.
            if (cursorName.getCount() > 0) {
                // Iterate through the cursor

                while (cursorName.moveToNext()) {
                    String id = cursorName.getString(cursorName.getColumnIndex(ContactsContract.Contacts._ID));
                    String name = cursorName.getString(cursorName.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    Cursor emailCur = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", new String[]{id}, null);
                    ArrayList<String> email_addresses = new ArrayList<>();
                    while (emailCur.moveToNext()) {
                        String email = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                        email_addresses.add(email);
                    }
                    contactMap.put(name, email_addresses);
                    emailCur.close();
                }

            }
            // Close the curosor
            cursorName.close();
        }catch (Exception e){
            System.out.println("Exception " + e.getMessage());
            listener.readContactsCompleted(false, e.getMessage(),null);
        }

        listener.readContactsCompleted(true,"",contactMap);
    }


    @Override
    protected Void doInBackground(Void... params) {
        getContactsName();
        /*
        try {
            for (Map.Entry<String, ArrayList<String>> entry : contactMap.entrySet()) {
                String name = entry.getKey();
                ArrayList<String> emails = entry.getValue();
                System.out.println(name);
                for(String email : emails)
                    System.out.println(email);
            }
        }catch (Exception e){
            System.out.println("Exception " + e.getMessage());
        }
        */
        return null;
    }
}