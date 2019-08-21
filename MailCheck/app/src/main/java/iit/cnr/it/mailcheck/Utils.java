package iit.cnr.it.mailcheck;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by giacomo on 06/12/18.
 */

public class Utils {

    public static String getDate(Date date){

        String dateConverted = "";
        SimpleDateFormat formatter_from = new SimpleDateFormat("EEE MMM dd hh:mm:ss ZZ yyyy", Locale.US);
        SimpleDateFormat formatter_to = new SimpleDateFormat("dd MMM yyyy");

        try {
            Date newDate = formatter_from.parse(date.toString());
            dateConverted = formatter_to.format(newDate);

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return dateConverted;
    }

    public static String gethour(Date date){

        String hourConverted = "";
        SimpleDateFormat formatter_from = new SimpleDateFormat("EEE MMM dd hh:mm:ss ZZ yyyy", Locale.US);
        SimpleDateFormat formatter_to = new SimpleDateFormat("hh:mm:ss aaa");

        try {
            Date newDate = formatter_from.parse(date.toString());
            hourConverted = formatter_to.format(newDate);

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return hourConverted;
    }
}
