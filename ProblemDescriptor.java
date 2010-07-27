package com.artcom.y60.data;

public interface ProblemDescriptor {

    public static class Problems {
        public static final String HOCCABILITY_BAD  = "hoccability_bad";
        public static final String HOCCABILITY_OK   = "hoccability_ok";
        public static final String HOCCABILITY_GOOD = "hoccability_good";
        public static final String NETWORK_OFF      = "network_off";
    }

    public static class Suggestions {

        public static final String HOCCABILITY_0                     = "hoccability_0";

        public static final String HOCCABILITY_1_GPS_BAD_BSSIDS_GOOD = "hoccability_1_gps_bad_bssids_good";
        public static final String HOCCABILITY_1_GPS_OK_BSSIDS_BAD   = "hoccability_1_gps_ok_bssids_bad";

        public static final String HOCCABILITY_2_GPS_OK_BSSIDS_GOOD  = "hoccability_2_gps_ok_bssids_good";
        public static final String HOCCABILITY_2_GPS_GOOD_BSSIDS_BAD = "hoccability_2_gps_good_bssids_bad";

        public static final String HOCCABILITY_3                     = "hoccability_3";

        public static final String NETWORK_OFF_SUGGESTION            = "network_off_suggestion";

    }

    public String getDescription(String id);
}
