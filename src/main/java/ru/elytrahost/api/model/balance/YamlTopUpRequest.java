package ru.elytrahost.api.model.balance;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class YamlTopUpRequest {
    public String requestURL;
    public HTTPMethod requestMethod;
    public List<String> requestHeaders;
    public String requestData;
    public ResponseType responseType;
    public List<String> responseValuePath;

    public List<String> doRequest() throws IOException {
        URL url = new URL(requestURL);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod(String.valueOf(requestMethod));

        if (requestData != null && !requestData.equals("")){
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

        }
    }

    public enum HTTPMethod {
        GET, POST, PUT
    }

    public enum ResponseType {
        JSON
    }
}
