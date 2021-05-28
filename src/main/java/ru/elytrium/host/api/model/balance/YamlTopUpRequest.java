package ru.elytrium.host.api.model.balance;

import com.google.gson.Gson;
import ru.elytrium.host.api.ElytraHostAPI;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class YamlTopUpRequest {
    public String requestURL;
    public HTTPMethod requestMethod;
    public List<String> requestHeaders;
    public String requestData;
    public ResponseType responseType;
    public List<String> responseValuePath;

    private final Gson gson = new Gson();

    public List<String> doRequest() throws TopUpException {
        try {
            URL url = new URL(requestURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod(String.valueOf(requestMethod));

            if (requestData != null && !requestData.equals("")) {
                con.setDoOutput(true);
                DataOutputStream out = new DataOutputStream(con.getOutputStream());
                out.writeBytes(requestData);
                out.flush();
                out.close();
            }

            requestHeaders.forEach(head -> {
                String[] headerValues = head.split(": ");
                con.setRequestProperty(headerValues[0], headerValues[1]);
            });

            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();

            switch (responseType) {
                case JSON:
                    HashMap map = gson.fromJson(content.toString(), HashMap.class);
                    List<String> answer = responseValuePath.stream()
                                                            .map(map::get)
                                                            .map(Object::toString)
                                                            .collect(Collectors.toList());
                    return answer;
            }
        } catch (Exception e) {
            ElytraHostAPI.getLogger().fatal("Exception caught while procceding " + this);
            ElytraHostAPI.getLogger().fatal(e);
            throw new TopUpException("Exception caught");
        }

        throw new TopUpException("Unexpected EOF");
    }

    public String toString() {
        return "YamlTopUpRequest{requestURL=" + requestURL
                + ";requestData" + requestData
                + ";requestMethod" + requestMethod
                + ";requestHeaders" + requestHeaders
                + ";responseType" + responseType + "}";
    }

    public enum HTTPMethod {
        GET, POST, PUT
    }

    public enum ResponseType {
        JSON
    }
}
