package com.test.carweather.entity;

/**
 * Created by b913 on 2017/4/6.
 */

public class AirResponseEntity {
    /**
     * air : {"pm":"35","aqi":"52","publish_time":"201601252300"}
     */

    private AirBean air;

    public AirBean getAir() {
        return air;
    }

    public void setAir(AirBean air) {
        this.air = air;
    }

    public static class AirBean {
        /**
         * pm : 35
         * aqi : 52
         * publish_time : 201601252300
         */

        private String pm;
        private String aqi;
        private String publish_time;

        public String getPm() {
            return pm;
        }

        public void setPm(String pm) {
            this.pm = pm;
        }

        public String getAqi() {
            return aqi;
        }

        public void setAqi(String aqi) {
            this.aqi = aqi;
        }

        public String getPublish_time() {
            return publish_time;
        }

        public void setPublish_time(String publish_time) {
            this.publish_time = publish_time;
        }
    }
}
