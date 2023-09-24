package com.atharva.paypark.model;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class UserModel {
    private static final UserModel ourInstance = new UserModel();

    public static UserModel getInstance() {
        return ourInstance;
    }

    private UserModel() {
        loginState=false;
    }

    private String fid;
    private String mobile;
    private int balance;
    private List<String> vehicleNos;
    private boolean loginState;

    public boolean isLoginState() {
        return loginState;
    }

    public void setLoginState(boolean loginState) {
        this.loginState = loginState;
    }

    public String getFid() {
        return fid;
    }

    public void setFid(String fid) {
        this.fid = fid;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public List<String> getVehicleNos() {
        return vehicleNos;
    }

    public void setVehicleNos(List<String> vehicleNos) {
        this.vehicleNos = vehicleNos;
    }

    public void clearFields() {
        fid = "";
        mobile = "";
        balance = 0;
        vehicleNos = new ArrayList<>();
        loginState = false;
    }

    @NonNull
    @Override
    public String toString() {
        return "UserModel{" +
                "fid='" + fid + '\'' +
                ", mobile='" + mobile + '\'' +
                ", balance=" + balance +
                ", vehicleNos=" + vehicleNos +
                ", loginState=" + loginState +
                '}';
    }


    //    public void login(String fid, String pass) {
//        // get salt
//        // generate encrypted pass
//        // check if it is in database
//
//        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//
//        StrictMode.setThreadPolicy(policy);
//
//        try {
//            URL url = new URL("http://192.168.0.111:8080/spring/getSalt");
//            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
//            try {
//                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
////                readStream(in);
//                BufferedReader br = new BufferedReader(new InputStreamReader(in));
//                while(true) {
//                    String x = br.readLine();
//                    if(x==null)
//                        break;
//                    System.out.println(x);
//                }
//            } finally {
//                urlConnection.disconnect();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
}
