package com.test.carweather.entity;

import java.util.List;

public class SearchResponseEntity {

    public String resp_status;
    public String error_code;
    public String error_desc;
    public List<CityInfoEntity> resp_data;

}
