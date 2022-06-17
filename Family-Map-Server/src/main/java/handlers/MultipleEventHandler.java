package handlers;

import ReqRes.MultipleEventResult;
import ReqRes.MultiplePersonResult;
import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.DataAccessException;
import service.MultipleEventService;
import service.MultiplePersonService;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;

public class MultipleEventHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        boolean success = false;
        MultipleEventResult GlobalResult = new MultipleEventResult();

        try{
            if (exchange.getRequestMethod().toLowerCase().equals("get")){
                Headers reqHeaders = exchange.getRequestHeaders();
                if (reqHeaders.containsKey("Authorization")){
                    String authToken = reqHeaders.getFirst("Authorization");
                    MultipleEventService service = new MultipleEventService();
                    MultipleEventResult result = service.returnAllEvents(authToken);
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
