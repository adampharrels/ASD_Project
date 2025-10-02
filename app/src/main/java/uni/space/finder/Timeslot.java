package uni.space.finder;
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

    public Timeslot(int startYear, int startMonth, int startDay, int startHour, int startMinute, int durationHour, int durationMinute) {
        this.startYear = 0;
        this.startMonth = 0;
        this.startDay = 0;
        this.startHour = 0;
        this.startMinute = 0;
        this.durationHour = 0;
        this.durationMinute = 0;
    }

    public void constructDateTime() {
        this.currentDateTime = LocalDateTime.of(this.startYear, this.startMonth, this.startDay, this.startHour, this.startMinute);
    }
}
