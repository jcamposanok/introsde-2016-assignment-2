package introsde.rest.ehealth.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public @interface DateParser {

    String DEFAULT_FORMAT  = "yyyy-MM-dd";
    String DEFAULT_START_DATE = "1970-01-01";
    String DEFAULT_END_DATE = "9999-01-01";

    class RequestParam {

        private final String dateString;

        public RequestParam() {
            this.dateString = DEFAULT_START_DATE;
        }

        public RequestParam(String dateString) {
            this.dateString = dateString;
        }

        public Date parseFromString() {
            Date parsed;
            try {
                SimpleDateFormat format = new SimpleDateFormat(DEFAULT_FORMAT);
                parsed = format.parse(dateString);
            }
            catch(ParseException pe) {
                throw new IllegalArgumentException();
            }
            return parsed;
        }

        public Date parseFromString(String formatString) {
            Date parsed;
            try {
                SimpleDateFormat format = new SimpleDateFormat(formatString);
                parsed = format.parse(dateString);
            }
            catch(ParseException pe) {
                throw new IllegalArgumentException();
            }
            return parsed;
        }

    }

}
