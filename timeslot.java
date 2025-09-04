import java.time.LocalDateTime;

public class Timeslot {

    LocalDateTime currentDateTime;
    int startYear;
    int startMonth;
    int startDay;
    int startHour;
    int startMinute;
    int durationHour;
    int durationMinute;

    public Timeslot() {
        startYear = 0;
        startMonth = 0;
        startDay = 0;
        startHour = 0;
        startMinute = 0;
        durationHour = 0;
        durationMinute = 0;
    }

    public void constructDateTime() {
        this.currentDateTime = LocalDateTime.of(this.startYear, this.startMonth, this.startDay, this.startHour, this.startMinte);
    }
}
