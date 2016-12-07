package com.example.yangxiaoguang.cordovademo;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangxiaoguang on 2016/12/6.
 * Http交互 采用 Android 21 版本中 apache http 原生jar
 */

public class HttpController {

    /**
     * get请求
     */
    public final static int REQ_METHOD_GET = 0;
    /**
     * post请求
     */
    public final static int REQ_METHOD_POST = 1;

    private HttpGet m_httpGet = null;
    private HttpPost m_httpPost = null;
    private HttpResponse m_httpResp = null;

    private List<NameValuePair> pairList;

    //可以自己定义
    private static final String USER_AGENT = "Android";
    private HttpClient m_httpClient;



    public HttpController()
    {
        m_httpClient = new DefaultHttpClient();
    }

    /**
     * 关闭http 链接
     */
    public void closeRequest() {
        if (m_httpGet != null)
            m_httpGet.abort();

        if (m_httpPost != null)
            m_httpPost.abort();
        m_httpResp = null;
        m_httpGet = null;
        m_httpPost = null;
    }


    /**
     * 打开一个http
     *
     * @param url
     * @param nReqMethod
     * @return
     */
    public boolean openRequest(String url, int nReqMethod) {
        closeRequest();
        if (nReqMethod == REQ_METHOD_GET) {
            m_httpGet = new HttpGet(url);
        } else if (nReqMethod == REQ_METHOD_POST) {
            m_httpPost = new HttpPost(url);
            pairList = new ArrayList<>();

        } else {
            return false;
        }

        return true;
    }

    /**
     * 添加头
     *
     * @param name
     * @param value
     */
    public void addHeader(String name, String value) {
        if (m_httpGet != null) {
            m_httpGet.addHeader(name, value);
        } else if (m_httpPost != null) {
            m_httpPost.addHeader(name, value);
        }
    }

    /**
     * 设置post数据
     * @param entity
     */
    public void setEntity(UrlEncodedFormEntity entity) {
        if (m_httpPost != null) {
            m_httpPost.setEntity(entity);
        }
    }

    /**
     * 添加post数据
     * @param Key
     * @param value
     */
    public void setPostValuesForKey(String Key, String value) {
        BasicNameValuePair basicNameValuePair = new BasicNameValuePair(Key, value);
        pairList.add(basicNameValuePair);


    }

    /**
     * 获得post数据对象
     * @return
     */
    public UrlEncodedFormEntity getPostData() {
        try {
            UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(pairList, HTTP.UTF_8);
            return urlEncodedFormEntity;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 发送http请求
     * @return
     */
    public Boolean sendRequest() {
        if (null == m_httpClient)
            return false;

        try {
            if (m_httpGet != null) {

                m_httpResp = m_httpClient.execute(m_httpGet);

                return true;
            } else if (m_httpPost != null) {
                m_httpResp = m_httpClient.execute(m_httpPost);
                return true;
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;

        }
        return false;
    }


    /**
     *  获得http 请求返回代码
     * @return
     */
    public int getRespCode() {
        if (m_httpResp != null)
            return m_httpResp.getStatusLine().getStatusCode();
        else
            return 0;
    }

    /**
     * 获得相应对象
     * @return
     */
    public HttpResponse getHttpResponse() {
        try {
            return m_httpResp;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    /**
     * 读取服务器返回数据
     *
     * @return
     */
    public byte[] getRespBodyData() {
        try {
            if (m_httpResp != null) {
                InputStream is = m_httpResp.getEntity().getContent();
                byte[] bytData = InputStreamToByte(is);
                is.close();
                return bytData;
            }
        } catch (IllegalStateException e) {

        } catch (IOException e) {

        }

        return null;
    }

    private byte[] InputStreamToByte(InputStream is) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int ch;
        byte[] buf = new byte[1024 * 4];
        byte data[] = null;

        try {
            while ((ch = is.read(buf)) != -1) {
                out.write(buf, 0, ch);
            }
            data = out.toByteArray();
            out.close();
        } catch (IOException e) {

        } finally {

        }

        return data;
    }

}
