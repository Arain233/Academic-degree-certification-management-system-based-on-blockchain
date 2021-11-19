package com.edusource.bc.fabric;
import com.edusource.bc.entity.stu_info;
import com.edusource.bc.http.HttpResult;
import com.edusource.bc.utils.DigestUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

@Service
public class Invoke {
    private String digest;
    private String id;

    @Autowired
    RestTemplate restTemplate;

    public String invoke(stu_info stu_info){
        digest = DigestUtils.DigestProducer(stu_info);
        id = stu_info.getId().toString();
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(URI.address+"invoke");
        List<NameValuePair> param = new ArrayList<>();
        param.add(new BasicNameValuePair("id",id));
        param.add(new BasicNameValuePair("digest",digest));
        UrlEncodedFormEntity formEntity = null;
        try {
            formEntity = new UrlEncodedFormEntity(param,"utf-8");
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }
        httpPost.setEntity(formEntity);
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpPost);
            if(response.getStatusLine().getStatusCode() == 200){
                String content = EntityUtils.toString(response.getEntity(),"utf-8");
                return HttpResult.ok(content).getMsg();
            }
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            try{
                response.close();
                httpClient.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        return HttpResult.error().getMsg();
    }
}
