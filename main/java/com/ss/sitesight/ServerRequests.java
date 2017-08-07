package com.ss.sitesight;

import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Created by AJ on 6/7/2017.
 */

public class ServerRequests {

    private String result;

    public ServerRequests() {}

    public void get(String url) {
        RequestGet req = new RequestGet();
        req.execute(url);
    }

    public void registerUser(User user) {
        RequestPostJSON req = new RequestPostJSON();
        req.execute("/api/v1/users/", user.getName(), user.getEmail(), user.getPassword());
    }

    public void authenticateUser(User user) {
        RequestPostBasicAuth req = new RequestPostBasicAuth();
        req.execute("/api/v1/authenticate/", user.getEmail(), user.getPassword());
    }

    public void put(String url) {

    }

    public void incrementPoints(int incrementAmount, OnTaskCompleted listener) {
        new RequestUpdatePoints(listener).execute(incrementAmount);
    }

    public void updateVisitedSites(String site_id){
        if(site_id != null){
            new RequestUpdateVisitedSites().execute(site_id);
        }
    }
    public void incrementPoints(String userID, int incrementAmount) {
        new RequestUpdatePointsByID().execute(userID, Integer.toString(incrementAmount));
    }


    public void putVisitedSite(String objectID) {
        new RequestUpdateVisitedSites().execute(objectID);
    }

    public void addMarker(String email, int x, int y, int radius, String imageLoc) {
        MarkerRequest req = new MarkerRequest();
        req.execute("/api/v1/newmarker/", email, Integer.toString(x), Integer.toString(y), Integer.toString(radius), imageLoc);
    }

    private class RequestGet extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            System.out.println("output is : " + result);

            try {
                //JSONObject obj = new JSONObject(result);
                JSONArray markers = new JSONArray(result);
                for (int i = 0; i < markers.length(); i++) {
                    JSONObject mark = markers.getJSONObject(i);
                    //System.out.print("email : " + mark.getString("email"));
                    System.out.print(" | x : " + mark.getInt("x"));
                    System.out.print(" | y : " + mark.getInt("y"));
                    System.out.println(" | radius : " + mark.getInt("radius"));
                }

            } catch(JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        protected String doInBackground(String... params) {
            try {
                final String url = Settings.SERVER_URL + params[0];  // server address
                URL serverUrl = new URL(url);
                HttpURLConnection con = (HttpURLConnection) serverUrl.openConnection();
                con.setRequestProperty("Cookie", User.COOKIE);
                con.setRequestMethod("GET");
                con.connect();  // Sending request to server

                BufferedReader bf = new BufferedReader(new InputStreamReader(con.getInputStream()));

                String value = bf.readLine();
                System.out.println("value = " + value);
                result = value;

            } catch (Exception e) {
                Log.e("MainActivity", e.getMessage(), e);
            }
            return result;
        }
    }

    private class RequestPostJSON extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                final String url = Settings.SERVER_URL + params[0];  // server address
                URL serverUrl = new URL(url);
                HttpURLConnection con = (HttpURLConnection) serverUrl.openConnection();
                con.setDoOutput(true);
                con.setDoInput(true);
                con.setRequestProperty("Accept", "application/json");
                con.setRequestProperty("Cookie", User.COOKIE);
                con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                con.setRequestMethod("POST");

                JSONObject user = new JSONObject();
                user.put("name", params[1]);
                user.put("email", params[2]);
                user.put("password", params[3]);

                OutputStream wr = con.getOutputStream();
                wr.write(user.toString().getBytes("UTF-8"));
                wr.flush();
                wr.close();

                //display what returns the POST request
                StringBuilder sb = new StringBuilder();
                int HttpResult = con.getResponseCode();
                if (HttpResult == HttpURLConnection.HTTP_CREATED) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    System.out.println("server response OK : " + sb.toString());
                } else {
                    System.out.println("server response BAD : " + con.getResponseMessage());
                }

            } catch (Exception e) {
                Log.e("MainActivity", e.getMessage(), e);
            }
            return result;
        }
    }

    private class RequestPostBasicAuth extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            System.out.println(s);
            super.onPostExecute(s);
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                final String url = Settings.SERVER_URL + params[0];  // server address
                URL serverUrl = new URL(url);
                HttpURLConnection con = (HttpURLConnection) serverUrl.openConnection();
                con.setDoOutput(true);
                con.setDoInput(true);

                String credentials = params[1] + ":" + params[2];
                String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                con.setRequestProperty("Authorization", "Basic " + base64EncodedCredentials);
                con.setRequestMethod("POST");

                con.connect();
                User.COOKIE = con.getHeaderField("Set-Cookie");

                //display what returns the POST request
                StringBuilder sb = new StringBuilder();
                int HttpResult = con.getResponseCode();
                if (HttpResult == HttpURLConnection.HTTP_OK) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    System.out.println("server response OK : " + sb.toString());
                } else {
                    System.out.println("server response BAD : " + con.getResponseMessage());
                }

            } catch (Exception e) {
                Log.e("MainActivity", e.getMessage(), e);
            }
            return result;
        }
    }

    private class MarkerRequest extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                final String url = Settings.SERVER_URL + params[0];  // server address
                URL serverUrl = new URL(url);
                HttpURLConnection con = (HttpURLConnection) serverUrl.openConnection();
                con.setDoOutput(true);
                con.setDoInput(true);
                con.setRequestProperty("Cookie", User.COOKIE);
                con.setRequestProperty("Accept", "application/json");
                con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                con.setRequestMethod("POST");

                JSONObject user = new JSONObject();
                user.put("email", params[1]);
                user.put("x", Integer.parseInt(params[2]));
                user.put("y", Integer.parseInt(params[3]));
                user.put("radius", Integer.parseInt(params[4]));
                user.put("imageLocation", params[5]);

                OutputStream wr = con.getOutputStream();
                wr.write(user.toString().getBytes("UTF-8"));
                wr.flush();
                wr.close();

                //display what returns the POST request
                StringBuilder sb = new StringBuilder();
                int HttpResult = con.getResponseCode();
                if (HttpResult == HttpURLConnection.HTTP_CREATED) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    System.out.println("server response OK : " + sb.toString());
                } else {
                    System.out.println("server response BAD : " + con.getResponseMessage());
                }

            } catch (Exception e) {
                Log.e("MainActivity", e.getMessage(), e);
            }
            return result;
        }
    }

    private class RequestUpdatePoints extends AsyncTask<Integer, Void, String> {
        private OnTaskCompleted listener;


        public RequestUpdatePoints(OnTaskCompleted listener){
            this.listener = listener;
        }
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            System.out.println("\t\t\tRESULT OF PUT : " + s);
            listener.onTaskCompleted();
        }

        @Override
        protected String doInBackground(Integer... params) {
            String res = "";
            try {
                final String url = Settings.SERVER_URL + "/api/v1/user/points";  // server address
                URL serverUrl = new URL(url);
                HttpURLConnection con = (HttpURLConnection) serverUrl.openConnection();
                con.setDoOutput(true);
                con.setDoInput(true);
                con.setRequestProperty("Accept", "application/json");
                con.setRequestProperty("Cookie", User.COOKIE);
                con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                con.setRequestMethod("PUT");

                System.out.println("\t\t\tINCREMENT POINTS BY: " + params[0]);
                JSONObject user = new JSONObject();
                user.put("points", params[0]);

                OutputStream wr = con.getOutputStream();
                wr.write(user.toString().getBytes("UTF-8"));
                wr.flush();
                wr.close();

                //display what returns the POST request
                StringBuilder sb = new StringBuilder();
                int HttpResult = con.getResponseCode();
                if (HttpResult == HttpURLConnection.HTTP_OK) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    System.out.println("server response OK : " + sb.toString());
                    res = "OK";
                } else {
                    System.out.println("server response BAD : " + con.getResponseMessage());
                    res = "BAD";
                }

            } catch (Exception e) {
                Log.e("MainActivity", e.getMessage(), e);
            }
            return res;
        }
    }

    private class RequestUpdatePointsByID extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            System.out.println("\t\t\tRESULT OF PUT : " + s);
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "";
            try {
                final String url = Settings.SERVER_URL + "/api/v1/user/givepoints";  // server address
                URL serverUrl = new URL(url);
                HttpURLConnection con = (HttpURLConnection) serverUrl.openConnection();
                con.setDoOutput(true);
                con.setDoInput(true);
                con.setRequestProperty("Accept", "application/json");
                con.setRequestProperty("Cookie", User.COOKIE);
                con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                con.setRequestMethod("PUT");

                JSONObject user = new JSONObject();
                user.put("userid", params[0]);
                user.put("points", Integer.parseInt(params[1]));

                OutputStream wr = con.getOutputStream();
                wr.write(user.toString().getBytes("UTF-8"));
                wr.flush();
                wr.close();

                //display what returns the POST request
                StringBuilder sb = new StringBuilder();
                int HttpResult = con.getResponseCode();
                if (HttpResult == HttpURLConnection.HTTP_OK) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    System.out.println("server response OK : " + sb.toString());
                    res = "OK";
                } else {
                    System.out.println("server response BAD : " + con.getResponseMessage());
                    res = "BAD";
                }

            } catch (Exception e) {
                Log.e("MainActivity", e.getMessage(), e);
            }
            return res;
        }
    }

    private class RequestUpdateVisitedSites extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            System.out.println("\t\t\tRESULT OF PUT : " + s);
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "";
            try {
                final String url = Settings.SERVER_URL + "/api/v1/markers/visited";  // server address
                URL serverUrl = new URL(url);
                HttpURLConnection con = (HttpURLConnection) serverUrl.openConnection();
                con.setDoOutput(true);
                con.setDoInput(true);
                con.setRequestProperty("Accept", "application/json");
                con.setRequestProperty("Cookie", User.COOKIE);
                con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                con.setRequestMethod("PUT");

                JSONObject user = new JSONObject();
                user.put("site_id", params[0]);

                OutputStream wr = con.getOutputStream();
                wr.write(user.toString().getBytes("UTF-8"));
                wr.flush();
                wr.close();

                //display what returns the POST request
                StringBuilder sb = new StringBuilder();
                int HttpResult = con.getResponseCode();
                if (HttpResult == HttpURLConnection.HTTP_OK) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    System.out.println("server response OK : " + sb.toString());
                    res = "OK";
                } else {
                    System.out.println("server response BAD : " + con.getResponseMessage());
                    res = "BAD";
                }

            } catch (Exception e) {
                Log.e("MainActivity", e.getMessage(), e);
            }
            return res;
        }
    }
}

