package com.atharva.paypark.util;


import com.atharva.paypark.model.UserModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Parser {

    static void parseUserModel(String s) {
        String[] strings = s.split(";");
        UserModel.getInstance().setFid(strings[0]);
        UserModel.getInstance().setMobile(strings[1]);
        UserModel.getInstance().setBalance(Integer.parseInt(strings[2]));
        String vehicleString = strings[3].substring(1,strings[3].length()-1);
        String[] vehicles = vehicleString.split(",");
        UserModel.getInstance().setVehicleNos(new ArrayList<>(Arrays.asList(vehicles)));
    }
}