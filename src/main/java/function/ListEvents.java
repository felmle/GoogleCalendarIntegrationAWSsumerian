package function;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

import java.io.IOException;
import java.util.List;

public class ListEvents {
    private String response = "";

    public ListEvents(Calendar calendar, DataMaster dataMaster) {
        if (dataMaster.getListEventDataNumber() <= 1) {
            response = "Dein nächster Termin ist ";
        } else {
            response = "Deine nächsten " + dataMaster.getListEventDataNumber() + " Termine sind ";
        }

        DateTime now = new DateTime(System.currentTimeMillis());

        System.out.println(calendar == null);

        Events events = null;
        try {
            events = calendar.events().list("primary")
                    .setMaxResults(dataMaster.getListEventDataNumber())
                    .setTimeMin(now)
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<Event> items = events.getItems();
        if (items.isEmpty()) {
            System.out.println("No upcoming events found.");
        } else {
            System.out.println("Upcoming events");
            for (Event event : items) {
                DateTime start = event.getStart().getDateTime();
                if (start == null) {
                    start = event.getStart().getDate();
                }
                DateTime end = event.getEnd().getDateTime();
                if (end == null) {
                    end = event.getEnd().getDate();
                }
                response += event.getSummary() + " von " + start + " bis " + end + ". ";
            }
        }
        System.out.println("response: " + response);
    }

    public String getResponse() {
        return response;
    }
}
