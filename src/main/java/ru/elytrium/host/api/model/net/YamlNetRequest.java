package ru.elytrium.host.api.model.net;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import ru.elytrium.host.api.ElytraHostAPI;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class YamlNetRequest {
    private String requestURL;
    private HTTPMethod requestMethod;
    private List<String> requestHeaders;
    private String requestData;
    private ResponseType responseType;
    private List<String> responseValuePath;

    public List<String> doRequest(Map<String, String> replaceValues) throws YamlNetException {
        try {
            String requestURL = this.requestURL;
            String requestData = this.requestData;

            for (Map.Entry<String, String> entry : replaceValues.entrySet()) {
                requestURL = requestURL.replace(entry.getKey(), entry.getValue());
            }

            if (requestData != null) {
                for (Map.Entry<String, String> entry : replaceValues.entrySet()) {
                    requestData = requestData.replace(entry.getKey(), entry.getValue());
                }
            }

            URL url = new URL(requestURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod(String.valueOf(requestMethod));

            requestHeaders.forEach(head -> {
                String[] headerValues = head.split(": ");
                con.setRequestProperty(headerValues[0], headerValues[1]);
            });

            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);

            if (requestData != null) {
                con.setDoOutput(true);
                DataOutputStream out = new DataOutputStream(con.getOutputStream());
                out.writeBytes(requestData);
                out.flush();
                out.close();
            }

            int code = con.getResponseCode();

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();

            switch (responseType) {
                case JSON:
                    DocumentContext jsonContext = JsonPath.parse(content.toString());

                    return responseValuePath.stream()
                                            .map(jsonContext::read)
                                            .map(Object::toString)
                                            .collect(Collectors.toList());
                case NONE:
                    return null;
            }
        } catch (Exception e) {
            ElytraHostAPI.getLogger().fatal("Exception caught while procceding " + this);
            ElytraHostAPI.getLogger().fatal(e);
            throw new YamlNetException("Exception caught");
        }

        throw new YamlNetException("Unexpected EOF");
    }

    public String toString() {
        return "YamlTopUpRequest{requestURL=" + requestURL
                + ";requestData=" + requestData
                + ";requestMethod=" + requestMethod
                + ";requestHeaders=" + requestHeaders
                + ";responseType=" + responseType + "}";
    }

    public enum HTTPMethod {
        GET, POST, PUT, DELETE
    }

    public enum ResponseType {
        JSON, NONE
    }
}
