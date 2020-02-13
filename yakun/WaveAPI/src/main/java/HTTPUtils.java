import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HTTPUtils {
    private static RequestConfig config;

    public HTTPUtils() {
        config = RequestConfig.custom()
                .setConnectionRequestTimeout(3000)
                .setConnectTimeout(3000)
                .setSocketTimeout(3000)
                .build();
    }

    /**
     * Custom timeout parameters
     *
     * @param connectionRequestTimeout int
     * @param connectTimeout           int
     * @param socketTimeout            int
     */
    public HTTPUtils(int connectionRequestTimeout, int connectTimeout, int socketTimeout) {
        config = RequestConfig.custom()
                .setConnectionRequestTimeout(connectionRequestTimeout)
                .setConnectTimeout(connectTimeout)
                .setSocketTimeout(socketTimeout)
                .build();
    }

    /**
     * HTTP Post method
     *
     * @param url      String
     * @param header   String
     * @param fileUUid String
     * @param filePath String
     * @return Response
     */
    public Response doPost(String url, String header, String fileUUid, String filePath) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();

        // add Post url
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(config);

        // add Post header
        if (header != null && !header.equals("")) {
            for (Map.Entry<String, String> entry : getRequestHeader(header).entrySet()) {
                httpPost.setHeader(entry.getKey(), entry.getValue());
            }
        }

        // request body
        StringBody uuid = new StringBody(fileUUid, ContentType.TEXT_PLAIN);
        FileBody file = new FileBody(new File(filePath));
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        builder.addPart("uuid", uuid);
        builder.addPart("file", file);
        HttpEntity entity = builder.build();
        httpPost.setEntity(entity);

        CloseableHttpResponse response = httpClient.execute(httpPost);
        return getResponse(response);
    }

    /**
     * HTTP Get method
     *
     * @param url    String
     * @param header String
     * @return Response
     */
    public Response doGet(String url, String header) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();

        // add Get url
        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(config);

        // add Get header
        if (header != null && !header.equals("")) {
            for (Map.Entry<String, String> entry : getRequestHeader(header).entrySet()) {
                httpGet.setHeader(entry.getKey(), entry.getValue());
            }
        }

        CloseableHttpResponse response = httpClient.execute(httpGet);
        return getResponse(response);
    }

    public Response doDelete(String url, String header) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();

        // add Delete url
        HttpDelete httpDelete = new HttpDelete(url);
        httpDelete.setConfig(config);

        // add Delete header
        if (header != null && !header.equals("")) {
            for (Map.Entry<String, String> entry : getRequestHeader(header).entrySet()) {
                httpDelete.setHeader(entry.getKey(), entry.getValue());
            }
        }

        CloseableHttpResponse response = httpClient.execute(httpDelete);
        return getResponse(response);
    }


    /**
     * Get Request Header
     * header format: [{"key1":"value1"},{"key2":"value2"},{"key3":"value3"}]
     *
     * @param header String
     * @return Map<String, String>
     */
    private Map<String, String> getRequestHeader(String header) {
        Map<String, String> headerMap = new HashMap<String, String>();
        JSONArray headerArray = JSONArray.parseArray(header);
        for (int i = 0; i < headerArray.size(); i++) {
            JSONObject headerObject = headerArray.getJSONObject(i);
            for (String key : headerObject.keySet()) {
                headerMap.put(key, headerObject.getString(key));
            }
        }
        return headerMap;
    }

    /**
     * Get Response
     *
     * @param response CloseableHttpResponse
     * @return Response
     */
    private Response getResponse(CloseableHttpResponse response) {
        Response res = null;
        try {
            String result;
            try {
                HttpEntity entity = response.getEntity();
                result = EntityUtils.toString(entity, Consts.UTF_8);
            } catch (IllegalArgumentException e) {
                result = "";
            }
            res = new Response();
            res.setResponseCode(response.getStatusLine().getStatusCode());
            res.setResponseHeader(getResponseHeader(response.getAllHeaders()));
            res.setResponseBody(result);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return res;
    }

    /**
     * Get Response Header
     *
     * @param headers Header[]
     * @return Map<String, String>
     */
    private Map<String, String> getResponseHeader(Header[] headers) {
        Map<String, String> headerMap = new HashMap<String, String>();
        for (Header header : headers) {
            headerMap.put(header.getName(), header.getValue());
        }
        return headerMap;
    }


    /**
     *
     */
    @Data
    public class Response {
        private int responseCode;
        private Map<String, String> responseHeader;
        private Object responseBody;
    }


    public static void main(String[] args) throws IOException {
        HTTPUtils httpUtils = new HTTPUtils();
        String url = "http://104.197.180.173/wave_factory/";
        String header = "";
        String fileUUid = "3511qf-c682-4198-aef8-3449f7e89630";
        String filePath = "output.wav";

//        // test Post function
//        Response postResponse = httpUtils.doPost(url, header, fileUUid, filePath);
//        if (postResponse.getResponseCode() == 201) {
//            System.out.println("Upload success!");
//        }

//        // test Get function
//        url = url + "?uuid=" + fileUUid;
//        Response getResponse = httpUtils.doGet(url, header);
//        if (getResponse.getResponseCode() == 200) {
//            System.out.println(getResponse.getResponseBody());
//        }

//        // test Delete function
//        url = url + "?uuid=" + fileUUid;
//        Response deleteResponse = httpUtils.doDelete(url, header);
//        if (deleteResponse.getResponseCode() == 204) {
//            System.out.println("Delete success!");
//        } else {
//            System.out.println("Delete failed!");
//        }
    }
}