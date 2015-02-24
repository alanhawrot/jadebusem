package pl.edu.uj.jadebusem.util;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import pl.edu.uj.jadebusem.models.Departure;
import pl.edu.uj.jadebusem.models.Schedule;
import pl.edu.uj.jadebusem.models.Tracepoint;

/**
 * Created by alanhawrot on 09.02.15.
 */
public class SchedulesAdapter extends ArrayAdapter<Schedule> implements Filterable {

    private Filter filter;
    private List<Schedule> allSchedulesList = new ArrayList<>();

    public SchedulesAdapter(Context context, int resource) {
        super(context, resource);
    }

    @Override
    public void addAll(Collection<? extends Schedule> collection) {
        super.addAll(collection);
        allSchedulesList.addAll(collection);
    }

    @Override
    public void addAll(Schedule... items) {
        super.addAll(items);
        Collections.addAll(allSchedulesList, items);
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new ScheduleFilter();
        }
        return filter;
    }

    public void clear(boolean completelyClear) {
        super.clear();
        if (completelyClear) {
            allSchedulesList.clear();
        }
    }

    private class ScheduleFilter extends Filter {

        private boolean checkIfAnyTracepointContainsKeyword(List<Tracepoint> tracepoints, String keyword) {
            for (Tracepoint tracepoint : tracepoints) {
                if (tracepoint.getName().contains(keyword)) {
                    return true;
                }
            }
            return false;
        }

        private boolean checkIfAnyDepartureContainsKeyword(List<Departure> departures, String keyword) {
            for (Departure departure : departures) {
                if (departure.getHour().contains(keyword)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            List<Schedule> schedules = new ArrayList<>();

            for (Schedule schedule : allSchedulesList) {
                schedules.add(schedule);
            }

            if (constraint != null && constraint.toString().length() > 0) {
                String[] keywords = constraint.toString().split(" ");
                List<Schedule> filteredSchedules = new ArrayList<>(schedules);
                List<Schedule> helperList = new ArrayList<>();

                for (String keyword : keywords) {
                    for (Schedule s : filteredSchedules) {
                        if (s.getCompanyName().contains(keyword) || checkIfAnyTracepointContainsKeyword(s
                                .getTracepoints(), keyword) || checkIfAnyDepartureContainsKeyword(s.getDepartures(),
                                keyword)) {
                            helperList.add(s);
                        }
                    }
                    filteredSchedules.clear();
                    filteredSchedules.addAll(helperList);
                    helperList.clear();
                }

                filterResults.count = filteredSchedules.size();
                filterResults.values = filteredSchedules;
            } else {
                synchronized (this) {
                    filterResults.count = schedules.size();
                    filterResults.values = schedules;
                }
            }

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            List<Schedule> filteredSchedules = (List<Schedule>) results.values;
            clear(false);
            for (Schedule schedule : filteredSchedules) {
                add(schedule);
            }
            notifyDataSetChanged();
        }
    }

}
