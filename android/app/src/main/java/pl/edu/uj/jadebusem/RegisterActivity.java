package pl.edu.uj.jadebusem;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import pl.edu.uj.jadebusem.util.UtilityClass;


public class RegisterActivity extends ActionBarActivity {

    public static final String REGISTER_USERNAME = "email";
    public static final String REGISTER_PASSWORD = "password";
    public static final String REGISTER_REPASSWORD = "password2";
    public static final String REGISTER_FIRSTNAME = "first_name";
    public static final String REGISTER_SURNAME = "last_name";
    public static final String REGISTER_ADDRESS = "address";
    public static final String REGISTER_COMPANY_NAME = "company_name";

    public static final String EMAIL_REGEX = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    public static final String REGISTER_URL = "http://jadebusem1.herokuapp.com/users/registration/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        if (savedInstanceState != null) {
            EditText registerUsernameEditText = (EditText) findViewById(R.id.registerUsernameEditText);
            EditText registerPasswordEditText = (EditText) findViewById(R.id.registerPasswordEditText);
            EditText registerRepasswordEditText = (EditText) findViewById(R.id.registerRepasswordEditText);
            EditText registerFirstnameEditText = (EditText) findViewById(R.id.registerFirstnameEditText);
            EditText registerSurnameEditText = (EditText) findViewById(R.id.registerSurnameEditText);
            EditText registerAddressEditText = (EditText) findViewById(R.id.registerAddressEditText);
            EditText registerCompanyNameEditText = (EditText) findViewById(R.id.registerCompanyNameEditText);

            String username = savedInstanceState.getString(REGISTER_USERNAME, "");
            String password = savedInstanceState.getString(REGISTER_PASSWORD, "");
            String repassword = savedInstanceState.getString(REGISTER_REPASSWORD, "");
            String firstname = savedInstanceState.getString(REGISTER_FIRSTNAME, "");
            String surname = savedInstanceState.getString(REGISTER_SURNAME, "");
            String address = savedInstanceState.getString(REGISTER_ADDRESS, "");
            String companyName = savedInstanceState.getString(REGISTER_COMPANY_NAME, "");

            registerUsernameEditText.setText(username);
            registerPasswordEditText.setText(password);
            registerRepasswordEditText.setText(repassword);
            registerFirstnameEditText.setText(firstname);
            registerSurnameEditText.setText(surname);
            registerAddressEditText.setText(address);
            registerCompanyNameEditText.setText(companyName);
        }

        UtilityClass.setupUI(this, findViewById(R.id.registerParent));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        EditText registerUsernameEditText = (EditText) findViewById(R.id.registerUsernameEditText);
        EditText registerPasswordEditText = (EditText) findViewById(R.id.registerPasswordEditText);
        EditText registerRepasswordEditText = (EditText) findViewById(R.id.registerRepasswordEditText);
        EditText registerFirstnameEditText = (EditText) findViewById(R.id.registerFirstnameEditText);
        EditText registerSurnameEditText = (EditText) findViewById(R.id.registerSurnameEditText);
        EditText registerAddressEditText = (EditText) findViewById(R.id.registerAddressEditText);
        EditText registerCompanyNameEditText = (EditText) findViewById(R.id.registerCompanyNameEditText);

        String username = registerUsernameEditText.getText().toString();
        String password = registerPasswordEditText.getText().toString();
        String repassword = registerRepasswordEditText.getText().toString();
        String firstname = registerFirstnameEditText.getText().toString();
        String surname = registerSurnameEditText.getText().toString();
        String address = registerAddressEditText.getText().toString();
        String companyName = registerCompanyNameEditText.getText().toString();

        putInBundleIfNotNull(outState, REGISTER_USERNAME, username);
        putInBundleIfNotNull(outState, REGISTER_PASSWORD, password);
        putInBundleIfNotNull(outState, REGISTER_REPASSWORD, repassword);
        putInBundleIfNotNull(outState, REGISTER_FIRSTNAME, firstname);
        putInBundleIfNotNull(outState, REGISTER_SURNAME, surname);
        putInBundleIfNotNull(outState, REGISTER_ADDRESS, address);
        putInBundleIfNotNull(outState, REGISTER_COMPANY_NAME, companyName);
    }

    private void putInBundleIfNotNull(Bundle outState, String key, String value) {
        if (value != null) {
            outState.putString(key, value);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
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

    public void cancelButtonClicked(View view) {
        finish();
    }

    public void registerButtonClicked(View view) {
        EditText registerUsernameEditText = (EditText) findViewById(R.id.registerUsernameEditText);
        EditText registerPasswordEditText = (EditText) findViewById(R.id.registerPasswordEditText);
        EditText registerRepasswordEditText = (EditText) findViewById(R.id.registerRepasswordEditText);
        EditText registerFirstnameEditText = (EditText) findViewById(R.id.registerFirstnameEditText);
        EditText registerSurnameEditText = (EditText) findViewById(R.id.registerSurnameEditText);
        EditText registerAddressEditText = (EditText) findViewById(R.id.registerAddressEditText);
        EditText registerCompanyNameEditText = (EditText) findViewById(R.id.registerCompanyNameEditText);

        String username = registerUsernameEditText.getText().toString();
        String password = registerPasswordEditText.getText().toString();
        String repassword = registerRepasswordEditText.getText().toString();
        String firstname = registerFirstnameEditText.getText().toString();
        String surname = registerSurnameEditText.getText().toString();
        String address = registerAddressEditText.getText().toString();
        String companyName = registerCompanyNameEditText.getText().toString();

        boolean isDataValid = true;

        if (!username.matches(EMAIL_REGEX)) {
            registerUsernameEditText.setError(getString(R.string.register_email_not_valid));
            isDataValid = false;
        }

        if (password.length() < 6) {
            registerPasswordEditText.setError(getString(R.string.register_short_password));
            isDataValid = false;
        }

        if (!(password.compareTo(repassword) == 0)) {
            registerPasswordEditText.setError(getString(R.string.register_not_equal_passwords));
            registerRepasswordEditText.setError(getString(R.string.register_not_equal_passwords));
            isDataValid = false;
        }

        if (isDataValid) {
            ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isConnected()) {
                NameValuePair[] content = new NameValuePair[7];
                content[0] = new BasicNameValuePair(REGISTER_USERNAME, username);
                content[1] = new BasicNameValuePair(REGISTER_PASSWORD, password);
                content[2] = new BasicNameValuePair(REGISTER_REPASSWORD, repassword);
                content[3] = new BasicNameValuePair(REGISTER_FIRSTNAME, firstname);
                content[4] = new BasicNameValuePair(REGISTER_SURNAME, surname);
                content[5] = new BasicNameValuePair(REGISTER_ADDRESS, address);
                content[6] = new BasicNameValuePair(REGISTER_COMPANY_NAME, companyName);

                RegisterTask registerTask = new RegisterTask();
                registerTask.execute(content);
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
    }

    enum RegisterResults {
        SUCCESS, NETWORK_PROBLEMS, EMAIL_IN_USE
    }

    class RegisterTask extends AsyncTask<NameValuePair, Void, RegisterResults> {

        @Override
        protected RegisterResults doInBackground(NameValuePair... params) {
            try {
                URL url = new URL(REGISTER_URL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setReadTimeout(50000);
                httpURLConnection.setConnectTimeout(50000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                writer.write(getQuery(params));
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

                if (response.contains("This e-mail is already in use")) {
                    return RegisterResults.EMAIL_IN_USE;
                }
            } catch (IOException e) {
                return RegisterResults.NETWORK_PROBLEMS;
            }
            return RegisterResults.SUCCESS;
        }

        private String getQuery(NameValuePair[] params) throws UnsupportedEncodingException {
            StringBuilder result = new StringBuilder();
            boolean first = true;

            for (NameValuePair pair : params)
            {
                if (first) {
                    first = false;
                }
                else {
                    result.append("&");
                }

                result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
            }

            return result.toString();
        }

        @Override
        protected void onPostExecute(RegisterResults result) {
            super.onPostExecute(result);

            switch (result) {
                case SUCCESS:
                    finish();
                    break;
                case EMAIL_IN_USE:
                    EditText registerUsernameEditText = (EditText) findViewById(R.id.registerUsernameEditText);
                    registerUsernameEditText.setError(getString(R.string.register_email_in_use));
                    break;
                case NETWORK_PROBLEMS:
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    builder.setTitle(getString(R.string.network_problem_title));
                    builder.setMessage(getString(R.string.network_problem_message));
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.create().show();
                    break;
                default:
                    break;
            }
        }
    }

}
