package handlers;

import ReqRes.SinglePersonResult;
import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.*;
import model.Person;
import model.User;
import service.SinglePersonService;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URI;
import java.sql.Connection;

public class SinglePersonHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        boolean success = false;
        SinglePersonResult GlobalResult = new SinglePersonResult();
        try {
            if (exchange.getRequestMethod().toLowerCase().equals("get")) {
                Headers reqHeaders = exchange.getRequestHeaders();
                if (reqHeaders.containsKey("Authorization")) {
                    URI reqHeader = exchange.getRequestURI();
                    String stringHeader = reqHeader.getPath();
                    String segments[] = stringHeader.split("/");
                    String id = segments[2];
                    String authToken = reqHeaders.getFirst("Authorization");

                    SinglePersonService service = new SinglePersonService();
                    SinglePersonResult result = service.findPersonByID(id, authToken);
                    GlobalResult = result;

                    if(result.isSuccess()) {
                        exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                        OutputStream resBody = exchange.getResponseBody();
                        Writer writerResBody = new OutputStreamWriter(exchange.getResponseBody());
                        Gson gson = new Gson();
                        gson.toJson(result, writerResBody);
                        writerResBody.close();
                        resBody.close();

                        exchange.getResponseBody().close();

                        success = true;
                    }

                }
            }
            if(!success){
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                Writer resBody = new OutputStreamWriter(exchange.getResponseBody());
                Gson gson = new Gson();
                gson.toJson(GlobalResult, resBody);
                resBody.close();
                exchange.getResponseBody().close();
            }
        } catch (DataAccessException e) {
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
            Writer resBody = new OutputStreamWriter(exchange.getResponseBody());
            Gson gson = new Gson();
            gson.toJson(GlobalResult, resBody);
            resBody.close();
            exchange.getResponseBody().close();
        }
    }
}
