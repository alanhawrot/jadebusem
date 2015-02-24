package pl.edu.uj.jadebusem;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Locale;

import pl.edu.uj.jadebusem.db.SchedulesDbManager;
import pl.edu.uj.jadebusem.models.Departure;
import pl.edu.uj.jadebusem.models.Schedule;
import pl.edu.uj.jadebusem.util.DeleteScheduleTask;
import pl.edu.uj.jadebusem.util.UploadScheduleTask;


public class DetailsActivity extends ActionBarActivity implements ActionBar.TabListener {

    private String username;
    private Schedule schedule;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }

        username = getIntent().getStringExtra(LoginActivity.USERNAME);
        schedule = (Schedule) getIntent().getSerializableExtra(EditScheduleActivity.SCHEDULE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.dialog_error_title));
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_edit) {
            if (username != null && schedule.getAuthor().compareTo(username) == 0)  {
                Intent intent = new Intent(this, EditScheduleActivity.class);
                intent.putExtra(LoginActivity.USERNAME, username);
                intent.putExtra(EditScheduleActivity.SCHEDULE, schedule);
                startActivity(intent);
            } else {
                builder.setMessage(getString(R.string.dialog_error_author_edit_message));
                builder.create().show();
            }
            return true;
        } else if (id == R.id.action_upload) {
            if (username != null && schedule.getAuthor().compareTo(username) == 0) {
                upload();
            } else {
                builder.setMessage(getString(R.string.dialog_error_author_upload_message));
                builder.create().show();
            }
            return true;
        } else if (id == R.id.action_delete) {
            if (username != null && schedule.getAuthor().compareTo(username) == 0) {
                AlertDialog.Builder deleteBuilder = new AlertDialog.Builder(this);
                deleteBuilder.setTitle(getString(R.string.dialog_delete_title));
                deleteBuilder.setMessage(getString(R.string.dialog_delete_message));
                deleteBuilder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        delete();
                    }
                });
                deleteBuilder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                deleteBuilder.create().show();
            } else {
                builder.setMessage(getString(R.string.dialog_error_author_delete_message));
                builder.create().show();
            }
            return true;
        } else if (id == R.id.action_sms) {
            sendSMS();
        }

        return super.onOptionsItemSelected(item);
    }

    private void sendSMS() {
        StringBuilder smsBodyBuilder = new StringBuilder();
        smsBodyBuilder.append(schedule.getCompanyName()).append("\n");

        smsBodyBuilder.append(getString(R.string.sms_tracepoints)).append("\n");
        for (int i = 0; i < schedule.getTracepoints().size(); i++) {
            smsBodyBuilder.append(i + 1).append(". ").append(schedule.getTracepoints().get(i).getName()).append("\n");
        }

        smsBodyBuilder.append(getString(R.string.sms_departure_times)).append("\n");
        for (int i = 0; i < 7; i++) {
            StringBuilder departureStringBuilder = new StringBuilder();

            for (Departure departure : schedule.getDepartures()) {
                if (departure.getDayInt() == i) {
                    departureStringBuilder.append(departure.getHour()).append("\n");
                }
            }

            if (departureStringBuilder.length() > 0) {
                switch (i) {
                    case 0:
                        smsBodyBuilder.append(getString(R.string.sms_monday)).append("\n").append(departureStringBuilder.toString());
                        break;
                    case 1:
                        smsBodyBuilder.append(getString(R.string.sms_tuesday)).append("\n").append(departureStringBuilder.toString());
                        break;
                    case 2:
                        smsBodyBuilder.append(getString(R.string.sms_wednesday)).append("\n").append(departureStringBuilder.toString());
                        break;
                    case 3:
                        smsBodyBuilder.append(getString(R.string.sms_thursday)).append("\n").append(departureStringBuilder.toString());
                        break;
                    case 4:
                        smsBodyBuilder.append(getString(R.string.sms_friday)).append("\n").append(departureStringBuilder.toString());
                        break;
                    case 5:
                        smsBodyBuilder.append(getString(R.string.sms_saturday)).append("\n").append(departureStringBuilder.toString());
                        break;
                    case 6:
                        smsBodyBuilder.append(getString(R.string.sms_sunday)).append("\n").append(departureStringBuilder.toString());
                        break;
                    default:
                        break;
                }
            }
        }

        Intent iSms = new Intent(Intent.ACTION_VIEW);
        iSms.putExtra("sms_body", smsBodyBuilder.toString());
        iSms.setType("vnd.android-dir/mms-sms");
        startActivity(iSms);
    }

    private void upload() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            Schedule[] params = { schedule };
            UploadScheduleTask uploadScheduleTask = new UploadScheduleTask(this, username, schedule);
            uploadScheduleTask.execute(params);
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

    private void delete() {
        if (schedule.getWeb_schedule_id() != 0) {
            ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isConnected()) {
                DeleteScheduleTask deleteScheduleTask = new DeleteScheduleTask(this, schedule);
                deleteScheduleTask.execute();
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
        } else {
            SchedulesDbManager schedulesDbManager = SchedulesDbManager.getInstance(this);
            schedulesDbManager.openWritableDatabase();
            schedulesDbManager.deleteSchedule(schedule);
            schedulesDbManager.closeDatabase();
            Toast.makeText(this, getString(R.string.toast_schedule_deleted), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private Fragment detailsFragment = new DetailsFragment();
        private Fragment mapFragment = new MapFragment();

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            switch (position) {
                case 0:
                    return detailsFragment;
                case 1:
                    return mapFragment;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
            }
            return null;
        }
    }

}
