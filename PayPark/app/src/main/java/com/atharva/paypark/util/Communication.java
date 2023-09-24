package com.atharva.paypark.util;

import android.os.StrictMode;
import android.util.Log;
import android.util.Pair;

import com.atharva.paypark.model.ParkingLogModel;
import com.atharva.paypark.model.UserModel;
import com.destro.Encoder;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class Communication {

    private static String serverAddress = "http://192.168.43.125:8080/mobile_server";

    public static void verifyLogin(String fid, String pass) {
        Log.d(null,"fid: " + fid + "\t pass: " + pass);
        pass = Encoder.encode(pass);
        Log.d(null,"Encoded pass: " + pass);
        List<Pair<String,String>> pairList = new ArrayList<>();
        pairList.add(new Pair<>("fid", fid));
        pairList.add(new Pair<>("pass", pass));
        Log.d(null, "params: " + pairList.toString());
        try {
            String param = getPostString(pairList);
            Log.d(null, "param string: " + param);
            URL url = new URL( serverAddress + "/login/verify");
            List<String> response = getResponse(url,param);
            if(!("****ERROR****".equals(response.get(0)))) {
                UserModel.getInstance().setLoginState(true);
                Log.d(null,"response: " + response.get(0));
                Parser.parseUserModel(response.get(0));
            } else {
                Log.d(null,"LOGIN: FALSE");
                UserModel.getInstance().setLoginState(false);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int registerUser(String pass){
        pass = Encoder.encode(pass);
        List<Pair<String,String>> pairList = new ArrayList<>();
        pairList.add(new Pair<>("fid", UserModel.getInstance().getFid()));
        pairList.add(new Pair<>("mobile", UserModel.getInstance().getMobile()));
        pairList.add(new Pair<>("balance", UserModel.getInstance().getBalance() + ""));
        int i=0;
        for ( String v : UserModel.getInstance().getVehicleNos() ) {
            pairList.add(new Pair<>("vehicle[" + i + "]", v));
            i++;
        }
        Log.d(null, "params: " + pairList.toString());
        pairList.add(new Pair<>("pass", pass));

        try {
            String param = getPostString(pairList);
            Log.d(null, "param string: " + param);
            URL url = new URL(serverAddress + "/register");
            List<String> response = getResponse(url,param);
            if("****SUCCESS****".equals(response.get(0))) {
                UserModel.getInstance().setLoginState(true);
                Log.d(null,"response: " + response.get(0));
            } else if("****ERROR****".equals(response.get(0))) {
                Log.d(null,"REGISTER: FALSE");
                Log.d(null,"REASON: " + response.get(1));
                UserModel.getInstance().setLoginState(false);
                switch (response.get(1)){
                    case "duplicate fid": return 1;
                    case "duplicate vehicles": return 2;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static List<ParkingLogModel> getParkingLog(String vehicle) {
        List<ParkingLogModel> parkingLogModels = new ArrayList<>();
        List<Pair<String,String>> pairList = new ArrayList<>();
        pairList.add(new Pair<>("vehicle", vehicle));
        try {
            String param = getPostString(pairList);
            URL url = new URL(serverAddress + "/session/parking_log");
            List<String> response = getResponse(url, param);
            if (response.size() > 0) {
                String[] strings = response.get(0).split(",");
                for (int i = 0; i < strings.length; i = i + 3) {
                    String entry = strings[i + 1];
                    String exit = strings[i + 2].substring(0, strings[i + 2].indexOf('}') - 1);
                    parkingLogModels.add(new ParkingLogModel(vehicle, entry, exit));
                }
            }
        } catch(IOException e){
            e.printStackTrace();
        }
        return parkingLogModels;
    }

    public static String getChecksum(List<Pair<String, String>> pairList) {
        String checksum = null;
        try {
            String param = getPostString(pairList);
            URL url = new URL(serverAddress + "/paytm/generateChecksum");
            List<String> response = getResponse(url,param);
            if(response.size()>0)
                checksum = response.get(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return checksum;
    }

    public static boolean verifyChecksum(List<Pair<String,String>> pairList){
        boolean correct = false;
        try {
            String param = getPostString(pairList);
            URL url = new URL(serverAddress + "/paytm/verifyChecksum");
            List<String> response = getResponse(url,param);
            correct = "true".equals(response.get(0));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return correct;
    }

    private static List<String> getResponse(URL url, String param) throws IOException {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        List<String> response = new ArrayList<>();
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("POST");
        urlConnection.setDoInput(true);
        urlConnection.setDoOutput(true);
        OutputStream os = urlConnection.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
        writer.write(param);
        writer.flush();
        writer.close();
        os.close();
        int responseCode=urlConnection.getResponseCode();
        try {
            if(responseCode==HttpURLConnection.HTTP_OK) {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                while(true) {
                    String x = br.readLine();
                    if(x==null)
                        break;
                    response.add(x);
                    Log.d("RESPONSE",x);
                }
            }
        } finally {
            urlConnection.disconnect();
        }
        return response;
    }

    private static String getPostString(List<Pair<String, String>> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for( Pair<String,String> pair : params){
            if (first)
                first = false;
            else
                result.append("&");
            Log.d("ENCODE" , pair.first + " -> " + pair.second);
            result.append(URLEncoder.encode(pair.first, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.second, "UTF-8"));
        }

        return result.toString();
    }

}