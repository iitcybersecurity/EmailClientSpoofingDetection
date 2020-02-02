package iit.cnr.it.mailcheck;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.util.ArrayList;



public class MainActivity extends AppCompatActivity implements MailListener {
    //swipe element to refresh mails
    SwipeRefreshLayout mSwipeRefreshLayout;
    ProgressBar progressBar;

    //maximum number of mail to load
    int NUM_MAX_MAIL = 20;
    //total number of mail loaded
    int total_mail_number = 0;

    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;

    String address, password, imap_port, imap_host;
    MailManager mailObj;
    MailManagerNew mailObjNew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = (ProgressBar)findViewById(R.id.progressBarTable);
        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swifeRefresh);
        progressBar.setVisibility(View.VISIBLE);

        //get username and password
        Bundle extras = getIntent().getExtras();
        address = extras.getString("username");
        password = extras.getString("password");
        imap_port = extras.getString("port");
        imap_host = extras.getString("host");

        /*address = "secure.address18@gmail.com";
        password = "secure_mail_18";

        address = "giacomo.giorgi@iit.cnr.it";
        password = "scrozza_88";*/


        //Create mailManager object in order to manage the mail account
        mailObjNew = new MailManagerNew(address, password, imap_host, imap_port, NUM_MAX_MAIL, total_mail_number, MainActivity.this, getContentResolver());

        //Check email address connection
        //Integer[] mailManagerParam = { 0 };
        mailObjNew.execute();

        //get all the contacts in the phone book
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        }
    }

    @Override
    public void onRequestPermissionsResult(int s, String[] k, int[] f){
        //TODO
    }

    /**
     * Callback called from checkConnection function in the mailManager. It return the
     * status of the connection to the account email, if the connection is not estabilished
     * return to the login activity otherwise get received email from the account
     * @param status
     * @param message
     */
    @Override
    public void checkCompleted(boolean status, String message) {
        if(!status) {
            mailObj.cancel(true);
            System.out.println(message);
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.putExtra("error", message);
            startActivity(intent);
            finish();
        }else{
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    try {
                        mailObj = new MailManager(address, password, imap_host, imap_port, NUM_MAX_MAIL, total_mail_number, MainActivity.this, getContentResolver());
                        Integer[] mailManagerParam = {2};
                        mailObj.execute(mailManagerParam);
                    }catch (Exception e){
                        System.out.println(e.getMessage());
                    }
                }
            });
            try {
                mailObj = new MailManager(address, password, imap_host, imap_port, NUM_MAX_MAIL, total_mail_number, this, getContentResolver());
                Integer[] mailManagerParam = {1};
                mailObj.execute(mailManagerParam);
            }catch (Exception e){
                System.out.println(e.getMessage());
            }
        }
    }

    @Override
    public void mailDownloadCompleted(boolean status, String message) {
        if(!status) {
            mailObjNew.cancel(true);
            System.out.println(message);
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.putExtra("error", message);
            startActivity(intent);
            finish();
        }else{
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    try {
                        mailObjNew = new MailManagerNew(address, password, imap_host, imap_port, NUM_MAX_MAIL, total_mail_number, MainActivity.this, getContentResolver());
                        //Integer[] mailManagerParam = {2};
                        mailObjNew.execute();
                    }catch (Exception e){
                        System.out.println(e.getMessage());
                    }
                }
            });
            try {
                mailObjNew = new MailManagerNew(address, password, imap_host, imap_port, NUM_MAX_MAIL, total_mail_number, MainActivity.this, getContentResolver());
                //Integer[] mailManagerParam = {1};
                mailObj.execute();
            }catch (Exception e){
                System.out.println(e.getMessage());
            }
        }
    }


    private void setRow(String address, Mail msg, LinearLayout tableLayout, boolean valid, int spam, int position){

        ViewGroup rowMail = (ViewGroup) getLayoutInflater().inflate(R.layout.row_mail, null);
        LinearLayout layoutRow = (LinearLayout) rowMail.findViewById(R.id.row_layout);

        if(position%2==0)
            layoutRow.setBackgroundResource(R.drawable.border_row_mail_dark);

        if(!valid) {
            ImageView checkView = (ImageView)rowMail.findViewById(R.id.CheckView);
            checkView.setImageResource(R.drawable.ic_wrong);
        }

        if(spam == 1) {
            ImageView checkView = (ImageView)rowMail.findViewById(R.id.SpamView);
            checkView.setImageResource(R.drawable.ic_wrong);
        }

        if(spam == 0) {
            ImageView checkView = (ImageView)rowMail.findViewById(R.id.SpamView);
            checkView.setImageResource(R.drawable.ic_right);
        }

        if(spam == -1) {
            ImageView checkView = (ImageView)rowMail.findViewById(R.id.SpamView);
            checkView.setImageResource(R.drawable.ic_denied);
        }

        TextView addressView = (TextView)rowMail.findViewById(R.id.AddressView);
        addressView.setText(address);
        addressView.setTypeface(null, Typeface.BOLD);

        TextView dataView = (TextView)rowMail.findViewById(R.id.DataView);
        dataView.setText(Utils.gethour(msg.getDate()));

        TextView subjectView = (TextView)rowMail.findViewById(R.id.SubjectView);
        subjectView.setText(msg.getSubject());

        tableLayout.addView(rowMail);

    }


    @Override
    public void mailLoadCompleted(ArrayList<Mail> messages, int total_mails) {

        try{
            //write on the global variable the total mail read from the mail account
            total_mail_number = total_mails;
            final String[] last_data = {""};

            if (messages != null){
                for(int i = 0; i < messages.size(); i++){
                    final Mail msg = messages.get(i);
                    ArrayList<String> in = msg.getSender();
                    String subject = msg.getSubject();
                    for (final String address : in) {
                        final String date = Utils.getDate(msg.getDate());

                        final int finalI = i;
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                try {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    LinearLayout tableLayout = (LinearLayout) findViewById(R.id.mail_table);
                                    //print date emails
                                    if(!date.equals(last_data[0])){
                                        last_data[0] = date;
                                        ViewGroup rowData = (ViewGroup) getLayoutInflater().inflate(R.layout.row_data, null);
                                        TextView dataView = (TextView)rowData.findViewById(R.id.date);
                                        dataView.setText(date);
                                        dataView.setTypeface(null, Typeface.BOLD);
                                        tableLayout.addView(rowData);
                                    }

                                    //check email in contact list
                                    boolean inContactList = false;
                                    if(msg.getInPhoneBook())
                                        inContactList = true;

                                    int isSpam = 0;
                                    if(msg.getSpam() > 0.5)
                                        isSpam = 1;
                                    if(msg.getSpam() == -1)
                                        isSpam = -1;

                                    setRow(address.toString(), msg, tableLayout, inContactList, isSpam, finalI +1);

                                }catch (Exception e){
                                    System.out.println("Exception " + e.getMessage());
                                }
                            }
                        });

                    }
                }
            }
        } catch (Exception mex) {
            mex.printStackTrace();
            System.out.println(mex.getMessage());
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.putExtra("error", mex.getMessage());
            startActivity(intent);
            finish();
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
