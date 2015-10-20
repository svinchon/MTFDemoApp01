package com.diy.helpers.v1;

//import com.sun.jersey.api.client.Client;
//import com.sun.jersey.api.client.ClientResponse;
//import com.sun.jersey.api.client.WebResource;
//import com.sun.jersey.api.client.config.DefaultClientConfig;
//import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
//import com.sun.jersey.api.json.JSONConfiguration;

//import javax.ws.rs.core.MediaType;

import android.util.Log;

import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.apache.http.NameValuePair;

import java.io.IOException;
import java.util.List;

public class RESTHelper {

    private String TAG = this.getClass().getName();

    private RESTHelperCallRequest req = new RESTHelperCallRequest();
    private RESTHelperCallResult resp = null;

    public RESTHelperCallRequest getReq() { return this.req; }

    public void setReq(RESTHelperCallRequest req) { this.req = req; }

    public void execute() {
        this.resp = new RESTHelperCallResult();
        try {
            if (req.isValid()) {
                 String url = req.getURL();
                String contentType = req.getContentType();
                String content = req.getContent();
                String requestBodyContentType = "text/plain";
                if ("json".equals(contentType)) {
                    requestBodyContentType = "application/json; charset=utf-8";
                }
                OkHttpClient client = new OkHttpClient();
                Request request;
                if (req.getRequestType().equals("POST")) {
                    RequestBody body = RequestBody.create(
                            MediaType.parse(requestBodyContentType),
                            content
                    );
                    request = new Request
                            .Builder()
                            .url(url)
                            .post(body)
                            .build();
                } else {
                    HttpUrl.Builder b = HttpUrl.parse(url).newBuilder();
                    List<NameValuePair> l = req.getParameters();
                    for (int i=0; i<l.size(); i++) {
                        b.addQueryParameter(l.get(i).getName(), l.get(i).getValue());
                    }
                    HttpUrl u = b.build();
                    request = new Request
                            .Builder()
                            .url(u)
                            .get()
                            .build();
                }
                Response response = client
                        .newCall(request)
                        .execute();
                this.resp.setContentType("unknown");
                if (!response.isSuccessful()) {
                    this.resp.setStatus("error");
                } else {
                    this.resp.setStatus("success");
                    String respContent = response.body().string();
                    this.resp.setContent(respContent);
                    if (JSONHelper.isJSONValid_json_org(respContent)) {
                        this.resp.setContentType("json");
                    }
                }
            }
        } catch (IOException e) {
            //e.printStackTrace();
            Log.e(TAG, "Error during http call: "+e.getMessage());
            this.resp.setStatus("error");
        }

//        DefaultClientConfig clientConfig = new DefaultClientConfig();
//        clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
//        Client client = Client.create(clientConfig);
//        String HTTPBasicSecurityLogin =  "";
//        String HTTPBasicSecurityPassword = "";
//        client.addFilter(new HTTPBasicAuthFilter(HTTPBasicSecurityLogin, HTTPBasicSecurityPassword));
//        WebResource webResource = client.resource(req.getURL());
//        webResource.accept("application/json").type("application/json");
//        //webResource.
//        ClientResponse response = webResource.post(
//                ClientResponse.class,
//                req.getContent()
//        );
//        this.resp = new RESTHelperCallResult();
//        this.resp.setContentType("unknown");
//        //Utils.Log(""+response.getType());
//        if (response.getStatus() != 200 & response.getStatus() != 201 & response.getStatus() != 404) {
//            this.resp.setStatus("error");
//        } else {
//            this.resp.setStatus("success");
//            String respContent = response
//                    //.type(MediaType.APPLICATION_XML)
//                    .getEntity(String.class);
//            this.resp.setContent(respContent);
//            if (JSONHelper.isJSONValid_json_org(respContent)) { this.resp.setContentType("json"); }
//        }
    }

    public RESTHelperCallResult getRESTCallResult() { return this.resp; }

}
