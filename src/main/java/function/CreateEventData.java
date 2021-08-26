package function;

import java.util.Calendar;
import java.util.Date;

public class CreateEventData {
    private String name;
    private Calendar date;
    private Calendar start;
    private Calendar end;

    /**
     * stores the data necessary to create a calendar event
     * @param name
     * @param date
     */
    public CreateEventData(String name, Calendar date, Calendar start, Calendar end)
    {
        this.name = name;
        this.date = date;
        this.start = start;
        this.end = end;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public Calendar getStart() {
        return start;
    }

    public void setStart(Calendar start) {
        this.start = start;
    }

    public Calendar getEnd() {
        return end;
    }

    public void setEnd(Calendar end) {
        this.end = end;
    }
}
