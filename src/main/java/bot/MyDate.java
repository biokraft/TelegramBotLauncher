package bot;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MyDate {
    private String currentDate;

    public MyDate() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 0);
        Date date = cal.getTime();
        SimpleDateFormat ft = new SimpleDateFormat("dd-MM-YYYY");
        currentDate = ft.format(date);
    }

    public String getCurrentDate() {
        return currentDate;
    }
}
