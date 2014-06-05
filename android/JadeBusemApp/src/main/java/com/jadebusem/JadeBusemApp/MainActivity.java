package com.jadebusem.JadeBusemApp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import utils.User;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private EditText email;
    private EditText password;
    private TextView tv1, tv2;

/*####################################################################################################
# onCreate()
# Inputs: savedInstanceState
# Return: None
# Initialize Main Activity
####################################################################################################*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_page);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);

        tv1 = (TextView) this.findViewById(R.id.without_login);
        tv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User.EMAIL = "";
                User.LOGGED = false;
                Intent intent = new Intent(getActivity(), MainListActivity.class);
                startActivity(intent);
            }
        });

        tv2 = (TextView) this.findViewById(R.id.register);
        tv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Register.class);
                startActivity(intent);
            }
        });

    }

/*####################################################################################################
# onResume()
# Inputs: None
# Return: None
# Initialize Main Activity when Resume
####################################################################################################*/
    @Override
    public void onResume()
    {
        super.onResume();
        SharedPreferences resSettings = getSharedPreferences("BYLECO", MODE_PRIVATE);
        String Uname = resSettings.getString("USER_NAME", "");
        email.setText(Uname);
    }

/*####################################################################################################
# loginButton()
# Inputs: view
# Return: None
# Action of login button
####################################################################################################*/
    public void loginButton(View view) {
        new TheTask().execute("http://jadebusem1.herokuapp.com/users/sign_in/");
    }

/*####################################################################################################
# getActivity()
# Inputs: None
# Return: Activity
# Return address to this activity
####################################################################################################*/
    public Activity getActivity()
    {
        return this;
    }


    class TheTask extends AsyncTask<String,String,String>
    {

/*####################################################################################################
# onPostExecute()
# Inputs: result
# Return: None
# Action done after another thread end
####################################################################################################*/
        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            // update textview here
            if(result.contains("Logged successfully"))
            {
                email.setError(null);
                password.setError(null);
                SharedPreferences saveSettings = getSharedPreferences("BYLECO", MODE_PRIVATE);
                SharedPreferences.Editor editor = saveSettings.edit();
                editor.putString("USER_NAME", email.getText().toString());
                editor.commit();
                User.LOGGED = true;
                User.EMAIL = email.getText().toString();
                Intent intent = new Intent(getActivity(), MainListActivity.class);
                startActivity(intent);
            }
            else {
                email.setError(result);
                password.setError(result);
            }
        }

/*####################################################################################################
# onPreExecute()
# Inputs: None
# Return: None
# Action done before another thread start
####################################################################################################*/
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
        }

/*####################################################################################################
# doInBackground()
# Inputs: params
# Return: String
# Body of thread, POST request with login informations
####################################################################################################*/
        @Override
        protected String doInBackground(String... params) {
            try
            {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost method = new HttpPost(params[0]);
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("email", email.getText().toString()));
                nameValuePairs.add(new BasicNameValuePair("password", password.getText().toString()));
                method.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpclient.execute(method);
                HttpEntity entity = response.getEntity();
                if(entity != null){
                    String temp = EntityUtils.toString(entity);

                    System.out.println(temp);

                    if(temp.contains("Please correct the errors below.") || temp.contains("Please correct the error below.") || temp.contains("Error, please check your email or password.")){
                        return "Check your email or password";
                    }
                    else
                    {
                        return "Logged successfully";
                    }

                }
                else{
                    return "Please, try again later";
                }
            }
            catch(Exception e){
                return "Network problems";
            }

        }
    }

}