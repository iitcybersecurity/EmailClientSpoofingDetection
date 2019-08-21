package iit.cnr.it.mailcheck;
import android.app.ProgressDialog;
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
import android.widget.ProgressBar;
import android.widget.Toast;


public class LoginActivity extends AppCompatActivity {

    EditText usernameText;
    EditText passwordText;
    Button nextBtn;
    ImageView mail_check;
    ImageView password_check;
    boolean mail_valid = false;
    boolean password_valid = false;
    String errors = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        usernameText = (EditText)findViewById(R.id.email_input);
        usernameText.requestFocus();
        passwordText = (EditText)findViewById(R.id.password_input);
        nextBtn = (Button)findViewById(R.id.next_button);
        mail_check = (ImageView)findViewById(R.id.mail_check);
        password_check = (ImageView)findViewById(R.id.password_check);


        Bundle extras = getIntent().getExtras();
        if(getIntent().hasExtra("error")) {
            errors = extras.getString("error");
            Toast.makeText(LoginActivity.this, errors, Toast.LENGTH_LONG).show();
        }



        nextBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v)
            {

                if(mail_valid & password_valid) {
                    Intent intent = new Intent(LoginActivity.this, SettingsActivity.class);
                    intent.putExtra("username", usernameText.getText().toString());
                    intent.putExtra("password", passwordText.getText().toString());
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(LoginActivity.this, "Incorrect credentials!",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        usernameText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                if(isValidEmail(s)){
                    mail_check.setImageResource(R.drawable.ic_right);
                    mail_valid = true;
                    if(password_valid && mail_valid){
                        nextBtn.setBackgroundResource(R.drawable.next_button_green);
                    }
                }else {
                    mail_check.setImageResource(R.drawable.ic_wrong);
                    mail_valid = false;
                    nextBtn.setBackgroundResource(R.drawable.next_button_red);

                }
                mail_check.setVisibility(View.VISIBLE);
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

        passwordText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()!=0){
                    password_check.setImageResource(R.drawable.ic_right);
                    password_valid = true;
                    if(password_valid && mail_valid){
                        nextBtn.setBackgroundResource(R.drawable.next_button_green);
                    }
                }else {
                    password_check.setImageResource(R.drawable.ic_wrong);
                    password_valid = false;
                    nextBtn.setBackgroundResource(R.drawable.next_button_red);
                }
                password_check.setVisibility(View.VISIBLE);
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

    public final static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

}
