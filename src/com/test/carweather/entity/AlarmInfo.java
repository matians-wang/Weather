package com.test.carweather.entity;

import com.test.carweather.entity.AlarmResponseEntity.AlarmsBean;
import com.test.carweather.utils.CarWeatherUtils;

public class AlarmInfo {

    private String province;
    private String city;
    private String region;
    private String warningName;
    private String warningClass;
    private String warningContent;
    private String publishTime;
    private String warningCode;
    private String warningImage;
    private String warningId;

    private String warningFullName;
    private int iconId;
    private int color;

    public AlarmInfo(AlarmsBean alarmEntity) {
        setAlarmInfo(alarmEntity);
    }

    public void setAlarmInfo(AlarmsBean alarmEntity) {
        if (alarmEntity != null) {
            this.province = alarmEntity.getProvince();
            this.city = alarmEntity.getCity();
            this.region = alarmEntity.getRegion();
            this.warningName = alarmEntity.getWarning_name();
            this.warningClass = alarmEntity.getWarning_class();
            this.warningContent = alarmEntity.getWarning_content();
            this.publishTime = alarmEntity.getPublish_time();
            this.warningCode = alarmEntity.getWarning_code();
            this.warningImage = alarmEntity.getWarning_image();
            this.warningId = alarmEntity.getWarning_id();

            this.iconId = CarWeatherUtils.getAlarmIconId(warningClass);
            this.color = CarWeatherUtils.getAlarmColor(warningClass);
            StringBuilder sb = new StringBuilder();
            sb.append(warningName);
            sb.append(warningClass);
            sb.append("预警");
            this.warningFullName = sb.toString();
        }
    }

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

    public String getWarningName() {
        return warningName;
    }

    public void setWarningName(String warningName) {
        this.warningName = warningName;
    }

    public String getWarningClass() {
        return warningClass;
    }

    public void setWarningClass(String warningClass) {
        this.warningClass = warningClass;
    }

    public String getWarningContent() {
        return warningContent;
    }

    public void setWarningContent(String warningContent) {
        this.warningContent = warningContent;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }

    public String getWarningCode() {
        return warningCode;
    }

    public void setWarningCode(String warningCode) {
        this.warningCode = warningCode;
    }

    public String getWarningImage() {
        return warningImage;
    }

    public void setWarningImage(String warningImage) {
        this.warningImage = warningImage;
    }

    public String getWarningId() {
        return warningId;
    }

    public void setWarningId(String warningId) {
        this.warningId = warningId;
    }

    public String getWarningFullName() {
        return warningFullName;
    }

    public void setWarningFullName(String warningFullName) {
        this.warningFullName = warningFullName;
    }

    public int getIconId() {
        return iconId;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
