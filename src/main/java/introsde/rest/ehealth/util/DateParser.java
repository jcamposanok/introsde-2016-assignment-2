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
            this("");
        }

        public RequestParam(String dateString) {
            this.dateString = dateString;
        }

        public Date parseFromString() {
            return parseFromString(DEFAULT_FORMAT);
        }

        public Date parseFromString(String formatString) {
            Date parsed;
            try {
                SimpleDateFormat format = new SimpleDateFormat(formatString);
                if (dateString.length() > 0) {
                    parsed = format.parse(dateString);
                }
                else {
                    parsed = new Date();
                }
            }
            catch(ParseException pe) {
                throw new IllegalArgumentException();
            }
            return parsed;
        }

    }

}
