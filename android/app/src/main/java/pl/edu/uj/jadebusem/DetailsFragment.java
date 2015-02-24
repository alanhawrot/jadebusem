package pl.edu.uj.jadebusem;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import pl.edu.uj.jadebusem.models.Departure;
import pl.edu.uj.jadebusem.models.Schedule;
import pl.edu.uj.jadebusem.models.Tracepoint;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetailsFragment extends Fragment {

    public static final String SCHEDULE = "SCHEDULE";

    public DetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_details, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initTabHost(view);

        Schedule schedule = (Schedule) getActivity().getIntent().getSerializableExtra(SCHEDULE);

        TextView detailsScheduleNameTextView = (TextView) view.findViewById(R.id.detailsScheduleNameTextView);
        detailsScheduleNameTextView.setText(schedule.getCompanyName());

        ListView tracepointsListView = (ListView) view.findViewById(R.id.detailsTracepointsListView);
        Collections.sort(schedule.getTracepoints(), new Comparator<Tracepoint>() {
            @Override
            public int compare(Tracepoint lhs, Tracepoint rhs) {
                if (lhs.getOrd() < rhs.getOrd()) {
                    return -1;
                } else if (lhs.getOrd() == rhs.getOrd()) {
                    return 0;
                }
                return 1;
            }
        });
        ArrayAdapter<Tracepoint> tracepointsArrayAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, schedule.getTracepoints());
        tracepointsListView.setAdapter(tracepointsArrayAdapter);

        ListView[] departuresListViews = new ListView[7];
        departuresListViews[0] = (ListView) view.findViewById(R.id.timeMonListView);
        departuresListViews[1] = (ListView) view.findViewById(R.id.timeTueListView);
        departuresListViews[2] = (ListView) view.findViewById(R.id.timeWedListView);
        departuresListViews[3] = (ListView) view.findViewById(R.id.timeThuListView);
        departuresListViews[4] = (ListView) view.findViewById(R.id.timeFriListView);
        departuresListViews[5] = (ListView) view.findViewById(R.id.timeSatListView);
        departuresListViews[6] = (ListView) view.findViewById(R.id.timeSunListView);

        Collections.sort(schedule.getDepartures(), new Comparator<Departure>() {
            @Override
            public int compare(Departure lhs, Departure rhs) {
                return lhs.getHour().compareTo(rhs.getHour());
            }
        });
        for (int i = 0; i < departuresListViews.length; i++) {
            List<Departure> departures = new ArrayList<>();

            for (Departure departure : schedule.getDepartures()) {
                if (departure.getDayInt() == i) {
                    departures.add(departure);
                }
            }

            ArrayAdapter<Departure> departuresArrayAdapter = new ArrayAdapter<>(getActivity(),
                    android.R.layout.simple_list_item_1, departures);
            departuresListViews[i].setAdapter(departuresArrayAdapter);
        }
    }

    private void initTabHost(View view) {
        TabHost tabHost = (TabHost) view.findViewById(R.id.tabHost);
        tabHost.setup();

        TabHost.TabSpec spec1 = tabHost.newTabSpec("M");
        spec1.setContent(R.id.MON);
        spec1.setIndicator(getActivity().getString(R.string.tab_mon));

        TabHost.TabSpec spec2 = tabHost.newTabSpec("T");
        spec2.setIndicator(getActivity().getString(R.string.tab_tue));
        spec2.setContent(R.id.TUE);

        TabHost.TabSpec spec3=tabHost.newTabSpec("W");
        spec3.setIndicator(getActivity().getString(R.string.tab_wed));
        spec3.setContent(R.id.WED);

        TabHost.TabSpec spec4=tabHost.newTabSpec("Th");
        spec4.setContent(R.id.THU);
        spec4.setIndicator(getActivity().getString(R.string.tab_thu));

        TabHost.TabSpec spec5=tabHost.newTabSpec("F");
        spec5.setIndicator(getActivity().getString(R.string.tab_fri));
        spec5.setContent(R.id.FRI);

        TabHost.TabSpec spec6=tabHost.newTabSpec("Sat");
        spec6.setIndicator(getActivity().getString(R.string.tab_sat));
        spec6.setContent(R.id.SAT);

        TabHost.TabSpec spec7=tabHost.newTabSpec("Sun");
        spec7.setIndicator(getActivity().getString(R.string.tab_sun));
        spec7.setContent(R.id.SUN);

        tabHost.addTab(spec1);
        tabHost.addTab(spec2);
        tabHost.addTab(spec3);
        tabHost.addTab(spec4);
        tabHost.addTab(spec5);
        tabHost.addTab(spec6);
        tabHost.addTab(spec7);
    }
}
