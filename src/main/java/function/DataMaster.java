package function;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class DataMaster {
    private String response;

    private final Intent intent;
    private Map<String, Object> map;

    private final MapEdit mapEdit = new MapEdit();

    private CreateEventData createEventData;
    private int listEventDataNumber = 1;

    /**
     * extracts the information from the given Map.
     * @param map the json-Object given by Lex
     */
    public DataMaster(Map<String, Object> map) {
        String intentMapString = map.get("currentIntent").toString();

        intentMapString = intentMapString.replaceAll(":", "-");
        intentMapString = intentMapString.replaceAll("=", ":");

        this.map = mapEdit.castStringToMap(intentMapString);
        intent = findIntent();

        switch (intent) {
            case CREATE_EVENT:
                try {
                    createEventData = getCreateEventContent();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
            case LIST_EVENTS:
                listEventDataNumber = getListEventContent();
                break;
            default:
                break;
        }
    }

    /**
     * @return the intent extracted from the map.
     * intentName is not a nested Object and can be directly extracted from the root object
     */
    private Intent findIntent() {
        switch(map.get("name").toString()) {
            case "CreateEvent":
            case "\"CreateEvent\"":
                return Intent.CREATE_EVENT;
            case "ListEvents":
            case "\"ListEvents\"":
                return Intent.LIST_EVENTS;
            default:
                return null;
        }
    }

    /**
     *
     * @return a CreateEventData object that has stored all the information necessary to create an event
     * @throws ParseException if the date is in an incorrect format
     */
    private CreateEventData getCreateEventContent() throws ParseException {
        String slotMapString = map.get("slots").toString();

        Map<String, Object> slotMap = mapEdit.castStringToMap(slotMapString);

        String name = slotMap.get("Name").toString();

        java.util.Calendar date = java.util.Calendar.getInstance();
        String dateString = slotMap.get("Date").toString();

        SimpleDateFormat sdf = new SimpleDateFormat("\"yyyy-MM-dd\"");
        date.setTime(sdf.parse(dateString));
        date.set(Calendar.ZONE_OFFSET, 2);

        System.out.println("DATE: " + date.get(Calendar.DATE) + "." + date.get(Calendar.MONTH));

        /*=========START TIME=========*/
        Calendar start = Calendar.getInstance();

        String startTimeString = slotMap.get("TimeStart").toString();
        SimpleDateFormat sdf1 = new SimpleDateFormat("\"HH-mm\"");
        start.setTime(sdf1.parse(startTimeString));

        start.set(Calendar.HOUR, start.get(Calendar.HOUR) - 2);

        start.set(Calendar.DATE, date.get(Calendar.DATE));
        start.set(Calendar.MONTH, date.get(Calendar.MONTH));
        start.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR));

        /*=========END TIME=========*/
        Calendar end = Calendar.getInstance();

        String endTimeString = slotMap.get("TimeEnd").toString();
        SimpleDateFormat sdf2 = new SimpleDateFormat("\"HH-mm\"");
        end.setTime(sdf2.parse(endTimeString));

        end.set(Calendar.HOUR, end.get(Calendar.HOUR) - 2);

        end.set(Calendar.DATE, date.get(Calendar.DATE));
        end.set(Calendar.MONTH, date.get(Calendar.MONTH));
        end.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR));

        System.out.println("(DataMaster)Start " + start.get(java.util.Calendar.HOUR) + ":" + start.get(java.util.Calendar.MINUTE));
        System.out.println("(DataMaster)End   " + end.get(java.util.Calendar.HOUR) + ":" + end.get(java.util.Calendar.MINUTE));

        return new CreateEventData(name, date, start, end);
    }

    /**
     * @return all the information needed for the ListEvents Intent. In this case a single integer
     * that determines how many of the next events get shown. If the slot in the map is null, it will return 0;
     */
    private int getListEventContent() {
        Map<String, Object> slotMap = mapEdit.castStringToMap(map.get("slots").toString());
        try {
            return Integer.parseInt(slotMap.get("Number").toString());
        } catch (Exception e)
        {
            return 1;
        }
    }

    public Intent getIntent() {
        return intent;
    }

    public CreateEventData getCreateEventData() {
        return createEventData;
    }

    public int getListEventDataNumber() {
        return listEventDataNumber;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
