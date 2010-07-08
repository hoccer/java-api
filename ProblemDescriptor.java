package com.artcom.y60.data;

public interface ProblemDescriptor {

    public static final String HOCCABILITY_INTRO     = "hoccability_intro";
    public static final String HOCCABILITY_HINT_JOIN = "hoccability_tip_join";

    public static class Problems {
        public static final String HOCCABILITY_BAD     = "hoccability_bad";
        public static final String HOCCABILITY_PERFECT = "hoccability_perfect";
    }

    public static class Suggestions {
        public static final String HOCCABILITY_TURN_WIFI_ON = "hoccability_improve_turn_wifi_on";
        public static final String HOCCABILITY_GO_OUTSIDE   = "hoccability_improve_go_outside";
    }

    public String getDescription(String id);
}
