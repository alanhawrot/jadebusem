package pl.edu.uj.jadebusem;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import pl.edu.uj.jadebusem.util.UtilityClass;


public class LoginActivity extends ActionBarActivity {

    public static final String LOGIN_USERNAME = "LOGIN_USERNAME";
    public static final String LOGIN_PASSWORD = "LOGIN_PASSWORD";

    public static final String LOGIN_URL = "http://jadebusem1.herokuapp.com/users/sign_in/";
    public static final String USERNAME = "USERNAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        if (savedInstanceState != null) {
            EditText loginUsernameEditText = (EditText) findViewById(R.id.loginUsernameEditText);
            EditText loginPasswordEditText = (EditText) findViewById(R.id.loginPasswordEditText);

            String username = savedInstanceState.getString(LOGIN_USERNAME, "");
            String password = savedInstanceState.getString(LOGIN_PASSWORD, "");

            loginUsernameEditText.setText(username);
            loginPasswordEditText.setText(password);
        }

        UtilityClass.setupUI(this, findViewById(R.id.loginParent));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        EditText loginUsernameEditText = (EditText) findViewById(R.id.loginUsernameEditText);
        EditText loginPasswordEditText = (EditText) findViewById(R.id.loginPasswordEditText);

        String username = loginUsernameEditText.getText().toString();
        String password = loginPasswordEditText.getText().toString();

        if (username != null) {
            outState.putString(LOGIN_USERNAME, username);
        }

        if (password != null) {
            outState.putString(LOGIN_PASSWORD, password);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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

    public void registerButtonClicked(View view) {
        Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
        startActivity(intent);
    }

    public void guestLoginButtonClicked(View view) {
        Intent intent = new Intent(getApplicationContext(), SchedulesActivity.class);
        startActivity(intent);
    }

    public void loginButtonClicked(View view) {
        EditText loginUsernameEditText = (EditText) findViewById(R.id.loginUsernameEditText);
        EditText loginPasswordEditText = (EditText) findViewById(R.id.loginPasswordEditText);

        String username = loginUsernameEditText.getText().toString();
        String password = loginPasswordEditText.getText().toString();

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            String[] params = new String[2];
            params[0] = username;
            params[1] = password;

            LoginTask loginTask = new LoginTask();
            loginTask.execute(params);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.network_problem_title));
            builder.setMessage(getString(R.string.network_problem_message));
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.create().show();
        }
    }

    enum LoginResults {
        SUCCESS, NETWORK_PROBLEM, LOGIN_ERROR
    }

    class LoginTask extends AsyncTask<String, Void, LoginResults> {

        @Override
        protected LoginResults doInBackground(String... params) {
            try {
                URL url = new URL(LOGIN_URL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setReadTimeout(50000);
                httpURLConnection.setConnectTimeout(50000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String body = URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(params[0], "UTF-8")
                        + "&" + URLEncoder.encode("password", "UTF-8") + "=" + params[1];
                writer.write(body);
                writer.flush();
                writer.close();
                outputStream.close();

                httpURLConnection.connect();

                BufferedReader reader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String response;
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                response = stringBuilder.toString();
                reader.close();

                if (response.contains("Please correct the errors below.") || response.contains("Please correct the " +
                        "error below.") || response.contains("Error, please check your email or password.")) {
                    return LoginResults.LOGIN_ERROR;
                }
            } catch (IOException e) {
                return LoginResults.NETWORK_PROBLEM;
            }
            return LoginResults.SUCCESS;
        }

        @Override
        protected void onPostExecute(LoginResults loginResult) {
            super.onPostExecute(loginResult);

            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            switch (loginResult) {
                case LOGIN_ERROR:
                    builder.setTitle(getString(R.string.login_error_title));
                    builder.setMessage(getString(R.string.login_error_message));
                    builder.create().show();
                    break;
                case NETWORK_PROBLEM:
                    builder.setTitle(getString(R.string.network_problem_title));
                    builder.setMessage(getString(R.string.network_problem_message));
                    builder.create().show();
                    break;
                case SUCCESS:
                    EditText loginUsernameEditText = (EditText) findViewById(R.id.loginUsernameEditText);
                    String username = loginUsernameEditText.getText().toString();

                    Intent intent = new Intent(getApplicationContext(), SchedulesActivity.class);
                    intent.putExtra(USERNAME, username);
                    startActivity(intent);
                    break;
                default:
                    break;
            }
        }
    }
}
