package com.test.carweather.entity;

import java.util.List;

/**
 * Created by b913 on 2017/4/6.
 */

public class AlarmResponseEntity {
    private List<AlarmsBean> alarms;

    public List<AlarmsBean> getAlarms() {
        return alarms;
    }

    public void setAlarms(List<AlarmsBean> alarms) {
        this.alarms = alarms;
    }

    public static class AlarmsBean {
        /**
         * province : 内蒙古自治区
         * city : 通辽市
         * region : 霍林郭勒市
         * warning_name : 大风
         * warning_class : 蓝色
         * warning_content : 内蒙古自治区通辽市霍林郭勒市气象局 2016 年 1 月 25 日 10 时 30分发布大风蓝色预警信号:预计 24 小时内我市平均风力可达 5-6 级，阵风 7 级，山区平均风力 6-7 级阵风 8 级。请注意防御!
         * publish_time : 2016-01-25 10:30
         * warning_code : 0501
         * warning_image : null
         * warning_id : 201601251012509243 大风蓝色
         */

        private String province;
        private String city;
        private String region;
        private String warning_name;
        private String warning_class;
        private String warning_content;
        private String publish_time;
        private String warning_code;
        private String warning_image;
        private String warning_id;

        public String getProvince() {
            return province;
        }

        public void setProvince(String province) {
            this.province = province;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getRegion() {
            return region;
        }

        public void setRegion(String region) {
            this.region = region;
        }

        public String getWarning_name() {
            return warning_name;
        }

        public void setWarning_name(String warning_name) {
            this.warning_name = warning_name;
        }

        public String getWarning_class() {
            return warning_class;
        }

        public void setWarning_class(String warning_class) {
            this.warning_class = warning_class;
        }

        public String getWarning_content() {
            return warning_content;
        }

        public void setWarning_content(String warning_content) {
            this.warning_content = warning_content;
        }

        public String getPublish_time() {
            return publish_time;
        }

        public void setPublish_time(String publish_time) {
            this.publish_time = publish_time;
        }

        public String getWarning_code() {
            return warning_code;
        }

        public void setWarning_code(String warning_code) {
            this.warning_code = warning_code;
        }

        public String getWarning_image() {
            return warning_image;
        }

        public void setWarning_image(String warning_image) {
            this.warning_image = warning_image;
        }

        public String getWarning_id() {
            return warning_id;
        }

        public void setWarning_id(String warning_id) {
            this.warning_id = warning_id;
        }
    }
}
