package function;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.EventReminder;

import java.io.IOException;
import java.util.Arrays;

public class CreateEvent
{
    private String response;

    private final CreateEventData createEventData;

    // Refer to the Java quickstart on how to set up the environment:
// https://developers.google.com/calendar/quickstart/java
// Change the scope to CalendarScopes.CALENDAR and delete any stored
// credentials.
    public CreateEvent(Calendar calendar, DataMaster dataMaster) {
        createEventData = dataMaster.getCreateEventData();
        Event event = new Event()
            .setSummary(createEventData.getName());

        /*=====Start=====*/
        java.util.Calendar eventStartTime = createEventData.getStart();

        EventDateTime start = new EventDateTime()
                .setDateTime(new DateTime(eventStartTime.getTime()))
                .setTimeZone("Europe/Berlin");
        event.setStart(start);
        System.out.println("Start " + eventStartTime.get(java.util.Calendar.HOUR) + ":" + eventStartTime.get(java.util.Calendar.MINUTE));

        /*======End======*/
        java.util.Calendar eventEndTime = createEventData.getEnd();

        EventDateTime end = new EventDateTime()
                .setDateTime(new DateTime(eventEndTime.getTime()))
                .setTimeZone("Europe/Berlin");
        event.setEnd(end);

        System.out.println("END " + eventEndTime.get(java.util.Calendar.HOUR) + ":" + eventEndTime.get(java.util.Calendar.MINUTE));


        EventReminder[] reminderOverrides = new EventReminder[] {
                new EventReminder().setMethod("email").setMinutes(24 * 60),
                new EventReminder().setMethod("popup").setMinutes(10),
        };
        Event.Reminders reminders = new Event.Reminders()
                .setUseDefault(false)
                .setOverrides(Arrays.asList(reminderOverrides));
        event.setReminders(reminders);

        String calendarId = "primary";
        try {
            event = calendar.events().insert(calendarId, event).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.printf("Event created: %s\n", event.getHtmlLink());

        createResponse();
    }

    private void createResponse()
    {
        String startTimeString = "";
        startTimeString += (createEventData.getStart().get(java.util.Calendar.HOUR) + 2) + " Uhr";
        if (createEventData.getStart().get(java.util.Calendar.MINUTE) > 0)
            startTimeString += " " + createEventData.getStart().get(java.util.Calendar.MINUTE);


        String endTimeString = "";
        endTimeString += (createEventData.getEnd().get(java.util.Calendar.HOUR) + 2) + " Uhr ";
        if (createEventData.getEnd().get(java.util.Calendar.MINUTE) > 0)
            startTimeString += " " + createEventData.getEnd().get(java.util.Calendar.MINUTE);

        response = "Ich habe den Termin " + createEventData.getName() + " von " + startTimeString + " bis " + endTimeString + " geplant.";
    }

    public String getResponse() {
        return response;
    }
}
