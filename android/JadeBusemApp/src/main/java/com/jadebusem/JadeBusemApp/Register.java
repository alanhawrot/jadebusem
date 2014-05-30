package com.jadebusem.JadeBusemApp;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

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

import java.util.ArrayList;
import java.util.List;

public class Register extends Activity {
    private TextView email, password, repassword, text;

/*####################################################################################################
# onCreate()
# Inputs: savedInstanceState
# Return: None
# Initialize Register Activity
####################################################################################################*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        email = (TextView) findViewById(R.id.email);
        password = (TextView) findViewById(R.id.password);
        repassword = (TextView) findViewById(R.id.repassword);
        text = (TextView) findViewById(R.id.text);
    }

/*####################################################################################################
# register()
# Inputs: view
# Return: None
# Action of register button
####################################################################################################*/
    public void register(View view) {
        new TheTask().execute("http://jadebusem1.herokuapp.com/users/registration/");
    }

/*####################################################################################################
# getActivity()
# Inputs: None
# Return: Activity
# Return address of activity
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

            if (result.contains("Account created successfully")) {
                email.setError(null);
                password.setError(null);
                repassword.setError(null);
                Intent intent = new Intent(getActivity(), MainList.class);
                startActivity(intent);
            } else {
                if(result.contains("This e-mail is already in use")){
                    email.setError(result);
                    password.setError(null);
                    repassword.setError(null);
                }
                else {
                    email.setError(null);
                    password.setError(result);
                    repassword.setError(result);
                }
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
# Body of thread, POST request with register informations
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
                nameValuePairs.add(new BasicNameValuePair("password2", repassword.getText().toString()));
                nameValuePairs.add(new BasicNameValuePair("first_name", ""));
                nameValuePairs.add(new BasicNameValuePair("last_name", ""));
                nameValuePairs.add(new BasicNameValuePair("address", ""));
                nameValuePairs.add(new BasicNameValuePair("company_name", ""));

                method.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpclient.execute(method);
                HttpEntity entity = response.getEntity();
                if(entity != null){
                    String temp = EntityUtils.toString(entity);

                    if(temp.contains("This e-mail is already in use")){
                        return "This e-mail is already in use";
                    }
                    else if(temp.contains("Password must be at least six characters long")){
                        return "Password must be at least six characters long";
                    }
                    else if(temp.contains("Password must be at least six characters long")){
                        return "Password must be at least six characters long";
                    }
                    else if(temp.contains("Passwords do not match")){
                        return "Passwords do not match";
                    }
                    else
                    {
                        return "Account created successfully";
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
