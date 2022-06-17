package edu.byu.cs240.familymapclient;

import org.junit.Test;

import ReqRes.LoginRequest;
import ReqRes.MultipleEventResult;
import ReqRes.MultiplePersonResult;
import ReqRes.RegisterRequest;

public class ServerProxyTest {
    @Test
    public void loginTest(){
        ServerProxy proxy = new ServerProxy("localhost", "8080");
        LoginRequest request = new LoginRequest("sheila", "parker");
        proxy.login(request);
    }

    @Test
    public void registerTest(){
        ServerProxy proxy = new ServerProxy("localhost", "8080");
        RegisterRequest request = new RegisterRequest("dormond", "123", "dormond@gmail.com", "dillon", "ormond", "m", "dillonID");
        proxy.register(request);
    }

    @Test
    public void peopleTest(){
        ServerProxy proxy = new ServerProxy("localhost", "8080");
        LoginRequest request = new LoginRequest("dillon123", "123");
        proxy.login(request);
        MultiplePersonResult result = proxy.getPeople();
        result.isSuccess();
    }

    @Test
    public void eventTest(){
        ServerProxy proxy = new ServerProxy("localhost", "8080");
        LoginRequest request = new LoginRequest("sheila", "parker");
        proxy.login(request);
        MultipleEventResult result = proxy.getEvents();
        result.isSuccess();
    }
}
