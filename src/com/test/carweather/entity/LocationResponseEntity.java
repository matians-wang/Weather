package com.test.carweather.entity;

/**
 * Created by b913 on 2017/4/6.
 */

public class LocationResponseEntity {
    /**
     * geo : {"status":"0","msg":"success","city_info":{"location":"101340101","level":"1","county":"台北市","city":"台北","province":"台湾","state":"中国"}}
     */

    private GeoBean geo;

    public GeoBean getGeo() {
        return geo;
    }

    public void setGeo(GeoBean geo) {
        this.geo = geo;
    }

    public static class GeoBean {
        /**
         * status : 0
         * msg : success
         * city_info : {"location":"101340101","level":"1","county":"台北市","city":"台北","province":"台湾","state":"中国"}
         */

        private String status;
        private String msg;
        private CityInfoBean city_info;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public CityInfoBean getCity_info() {
            return city_info;
        }

        public void setCity_info(CityInfoBean city_info) {
            this.city_info = city_info;
        }

        public static class CityInfoBean {
            /**
             * location : 101340101
             * level : 1
             * county : 台北市
             * city : 台北
             * province : 台湾
             * state : 中国
             */

            private String location;
            private String level;
            private String county;
            private String city;
            private String province;
            private String state;

            public String getLocation() {
                return location;
            }

            public void setLocation(String location) {
                this.location = location;
            }

            public String getLevel() {
                return level;
            }

            public void setLevel(String level) {
                this.level = level;
            }

            public String getCounty() {
                return county;
            }

            public void setCounty(String county) {
                this.county = county;
            }

            public String getCity() {
                return city;
            }

            public void setCity(String city) {
                this.city = city;
            }

            public String getProvince() {
                return province;
            }

            public void setProvince(String province) {
                this.province = province;
            }

            public String getState() {
                return state;
            }

            public void setState(String state) {
                this.state = state;
            }
        }
    }
}
