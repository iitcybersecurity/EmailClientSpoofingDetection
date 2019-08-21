package iit.cnr.it.mailcheck;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


public class SettingsActivity extends AppCompatActivity {

    EditText hostText;
    EditText portText;
    Button loginBtn;
    ImageView host_check;
    ImageView port_check;
    boolean host_valid = false;
    boolean port_valid = false;
    String username = "";
    String password = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        hostText = (EditText)findViewById(R.id.host_input);
        hostText.requestFocus();
        portText = (EditText)findViewById(R.id.imap_port_input);
        loginBtn = (Button)findViewById(R.id.login_button);
        host_check = (ImageView)findViewById(R.id.host_check);
        port_check = (ImageView)findViewById(R.id.port_check);


        Bundle extras = getIntent().getExtras();
        if(getIntent().hasExtra("username") && getIntent().hasExtra("password")) {
            username = extras.getString("username");
            password = extras.getString("password");

        }



        loginBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v)
            {

                if(host_valid & port_valid) {
                    Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                    intent.putExtra("username", username);
                    intent.putExtra("password", password);
                    intent.putExtra("host", hostText.getText().toString());
                    intent.putExtra("port", portText.getText().toString());
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(SettingsActivity.this, "Incorrect credentials!",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        hostText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()!=0){
                    host_check.setImageResource(R.drawable.ic_right);
                    host_valid = true;
                    if(port_valid && host_valid){
                        loginBtn.setBackgroundResource(R.drawable.login_button_green);
                    }
                }else {
                    host_check.setImageResource(R.drawable.ic_wrong);
                    host_valid = false;
                    loginBtn.setBackgroundResource(R.drawable.login_button_red);

                }
                host_check.setVisibility(View.VISIBLE);
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
            }
        });

        portText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()!=0){
                    port_check.setImageResource(R.drawable.ic_right);
                    port_valid = true;
                    if(port_valid && host_valid){
                        loginBtn.setBackgroundResource(R.drawable.login_button_green);
                    }
                }else {
                    port_check.setImageResource(R.drawable.ic_wrong);
                    port_valid = false;
                    loginBtn.setBackgroundResource(R.drawable.login_button_red);
                }
                port_check.setVisibility(View.VISIBLE);
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
            }
        });
    }


}
