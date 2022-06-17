package edu.byu.cs240.familymapclient;

import android.provider.ContactsContract;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import ReqRes.LoginRequest;
import ReqRes.LoginResult;
import ReqRes.MultipleEventResult;
import ReqRes.MultiplePersonResult;
import ReqRes.RegisterRequest;
import ReqRes.RegisterResult;
import ReqRes.SinglePersonResult;
import model.Event;
import model.Person;

public class ServerProxy {
    String serverHost;
    String serverPort;
    public ServerProxy(String host, String port){
        serverHost = host;
        serverPort = port;
    }


    public RegisterResult register(RegisterRequest request){
        try{
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/user/register");
            HttpURLConnection http = (HttpURLConnection)url.openConnection();
            http.setRequestMethod("POST");
            http.setDoOutput(true);
            http.addRequestProperty("Accept", "application/json");
            http.connect();
            String username = request.getUsername();
            String pw = request.getPassword();
            String email = request.getEmail();
            String firstName = request.getFirstName();
            String lastName = request.getLastName();
            String gender = request.getGender();

            String reqData =
                    "{" +
                            "\"username\": \"" + username + "\",\n"  +
                            "\"password\": \"" + pw + "\", \n" +
                            "\"email\": \"" + email + "\", \n" +
                            "\"firstName\": \"" + firstName + "\", \n" +
                            "\"lastName\": \"" + lastName + "\", \n" +
                            "\"gender\": \"" + gender + "\"" +
                            "}";
            OutputStream reqBody = http.getOutputStream();
            writeString(reqData, reqBody);

            reqBody.close();
            if(http.getResponseCode() == HttpURLConnection.HTTP_OK){
                InputStream resBody = http.getInputStream();
                String respData = readString(resBody);
                Gson gson = new Gson();
                RegisterResult result = gson.fromJson(respData, RegisterResult.class);
                resBody.close();
                DataCache cache = DataCache.getInstance();
                cache.setAuthToken(result.getAuthToken());
                cache.setCurrUsername(result.getUsername());
                cache.setUserPersonID(result.getPersonID());
                System.out.println("Register successfull");
                return result;
            }
            else{
                System.out.println("ERROR: " + http.getResponseMessage());
                InputStream respBody = http.getErrorStream();
                String respData = readString(respBody);
                Gson gson = new Gson();
                RegisterResult result = gson.fromJson(respData, RegisterResult.class);
                respBody.close();
                System.out.println(respData);
                return result;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public LoginResult login(LoginRequest request){
        try{
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/user/login");
            HttpURLConnection http = (HttpURLConnection)url.openConnection();
            http.setRequestMethod("POST");
            http.setDoOutput(true);
            http.addRequestProperty("Accept", "application/json");
            http.connect();
            String username = request.getUsername();
            String pw = request.getPassword();
            Gson gson = new Gson();
            String reqData =
            "{" +
                    "\"username\": \"" + username + "\",\n"  +
                    "\"password\": \"" + pw + "\"" +
                    "}";

            OutputStream reqBody = http.getOutputStream();
            writeString(reqData, reqBody);

            reqBody.close();

            if(http.getResponseCode() == HttpURLConnection.HTTP_OK){
                InputStream resBody = http.getInputStream();
                String respData = readString(resBody);
                LoginResult result = gson.fromJson(respData, LoginResult.class);
                resBody.close();
                System.out.println("login successfull");
                DataCache cache = DataCache.getInstance();
                cache.setAuthToken(result.getAuthToken());
                cache.setCurrUsername(result.getUsername());
                cache.setUserPersonID(result.getPersonID());
                return result;
            }
            else{
                System.out.println("ERROR: " + http.getResponseMessage());
                InputStream respBody = http.getErrorStream();
                String respData = readString(respBody);
                LoginResult result = gson.fromJson(respData, LoginResult.class);
                respBody.close();
                System.out.println(respData);
                return result;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
    public MultiplePersonResult getPeople(){
        DataCache dataCache = DataCache.getInstance();
        String authToken = dataCache.getAuthToken();

        try{
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/person");
            HttpURLConnection http = (HttpURLConnection)url.openConnection();
            http.setRequestMethod("GET");
            http.setDoOutput(false);
            http.addRequestProperty("Authorization", authToken);
            http.addRequestProperty("Accept", "application/json");
            http.connect();

            if(http.getResponseCode() == HttpURLConnection.HTTP_OK){
                InputStream resBody = http.getInputStream();
                Gson gson = new Gson();
                String respData = readString(resBody);
                MultiplePersonResult result = gson.fromJson(respData, MultiplePersonResult.class);
                resBody.close();
                DataCache cache = DataCache.getInstance();
                Person[] personArray = result.getData();
                for(Person p : personArray){
                    cache.addToPersonsMap(p.getPersonID(), p);
                }
                System.out.println("people fetched successfully");
                return result;
            }
            else{
                System.out.println("ERROR: " + http.getResponseMessage());
                InputStream respBody = http.getErrorStream();
                String respData = readString(respBody);
                Gson gson = new Gson();
                MultiplePersonResult result = gson.fromJson(respData, MultiplePersonResult.class);
                respBody.close();
                System.out.println(respData);
                return result;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
    public SinglePersonResult getPerson(String ID){
        DataCache dataCache = DataCache.getInstance();
        String authToken = dataCache.getAuthToken();
        try{
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/person/" + ID );
            HttpURLConnection http = (HttpURLConnection)url.openConnection();
            http.setRequestMethod("GET");
            http.setDoOutput(false);
            http.addRequestProperty("Authorization", authToken);
            http.addRequestProperty("Accept", "application/json");
            http.connect();

            if(http.getResponseCode() == HttpURLConnection.HTTP_OK){
                InputStream resBody = http.getInputStream();
                Gson gson = new Gson();
                String respData = readString(resBody);
                SinglePersonResult result = gson.fromJson(respData, SinglePersonResult.class);
                resBody.close();
                return result;
            }
            else{
                System.out.println("ERROR: " + http.getResponseMessage());
                InputStream respBody = http.getErrorStream();
                String respData = readString(respBody);
                Gson gson = new Gson();
                SinglePersonResult result = gson.fromJson(respData, SinglePersonResult.class);
                respBody.close();
                return result;
            }
        } catch (MalformedURLException | ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public MultipleEventResult getEvents(){
        DataCache dataCache = DataCache.getInstance();
        String authToken = dataCache.getAuthToken();

        try{
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/event");
            HttpURLConnection http = (HttpURLConnection)url.openConnection();
            http.setRequestMethod("GET");
            http.setDoOutput(false);
            http.addRequestProperty("Authorization", authToken);
            http.addRequestProperty("Accept", "application/json");
            http.connect();

            if(http.getResponseCode() == HttpURLConnection.HTTP_OK){
                InputStream resBody = http.getInputStream();
                Gson gson = new Gson();
                String respData = readString(resBody);
                MultipleEventResult result = gson.fromJson(respData, MultipleEventResult.class);
                resBody.close();
                DataCache cache = DataCache.getInstance();
                Event[] events = result.getData();
                for(Event e : events){
                    cache.addEventToMap(e.getEventID(), e);
                }
                System.out.println("events fetched successfully");
                return result;
            }
            else{
                System.out.println("ERROR: " + http.getResponseMessage());
                InputStream respBody = http.getErrorStream();
                String respData = readString(respBody);
                Gson gson = new Gson();
                MultipleEventResult result = gson.fromJson(respData, MultipleEventResult.class);
                respBody.close();
                System.out.println(respData);
                return result;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
    private static void writeString(String str, OutputStream os) throws IOException {
        OutputStreamWriter sw = new OutputStreamWriter(os);
        sw.write(str);
        sw.flush();
    }
    private static String readString(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        InputStreamReader sr = new InputStreamReader(is);
        char[] buf = new char[1024];
        int len;
        while ((len = sr.read(buf)) > 0) {
            sb.append(buf, 0, len);
        }
        return sb.toString();
    }


}
