package com.example.application.http;

import android.content.Context;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class HttpsUtil{
    private static HttpsUtil httpsUtil;

    private HttpsUtil(){

    }
    public static HttpsUtil getInstance(){
        if (httpsUtil==null){
            httpsUtil=new HttpsUtil();
        }
        return httpsUtil;
    }
    public interface  OnRequestCallBack{
        void onSuccess(String s);
        void onFail(Exception e);
    }


    public void get(final String path, final OnRequestCallBack callBack){
        new Thread(new Runnable() {
            @Override
            public void run() {
                //联网操作
                try {
                    URL url = new URL(path);
                    //1.改成s
                    HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                    //2.SSLContext 初始化
                    SSLContext tls = SSLContext.getInstance("TLS");
                    //3.定义一个 TrustManagerFactory,让这个工厂生成TrustManager数组

                    String defaultType = KeyStore.getDefaultType();
                    KeyStore instance = KeyStore.getInstance(defaultType);
                    instance.load(null);
                    //instance.setCertificateEntry("srca", getX509Certificate(context));

                    String defaultAlgorithm = TrustManagerFactory.getDefaultAlgorithm();//得到默认算法
                    TrustManagerFactory trustMF = TrustManagerFactory.getInstance(defaultAlgorithm);
                    trustMF.init(instance);
                    //TrustManager[] trustManagers = trustMF.getTrustManagers();

                    TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {  //信任所以证书
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }

                        @Override
                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        }
                    }};



                    tls.init(null, trustAllCerts, new SecureRandom());
                    //3.ssl工厂
                    SSLSocketFactory factory = tls.getSocketFactory();
                    //4.添加一个主机名称校验器
                    conn.setHostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String hostname, SSLSession session) {
                            return true;
                            /*if (hostname.equals("120.26.172.16")) {
                                return true;
                            }else{
                                    return false;
                            }*/

                        }
                    });



                    conn.setSSLSocketFactory(factory);

                    conn.setRequestMethod("GET");
                    conn.setReadTimeout(5000);
                    conn.setConnectTimeout(5000);
                    conn.setDoInput(true);
                    //conn.setDoOutput(true);
                    conn.connect();
                    InputStream inputStream = conn.getInputStream();

                    StringBuilder sb=new StringBuilder();
                    int flag;
                    byte[] buf=new byte[1024];
                    while((flag=inputStream.read(buf))!=-1){
                        sb.append(new String(buf,0,flag));
                    }
                    String s = sb.toString();
                    //调用对方传入callback完成回调操作
                    callBack.onSuccess(s);
                } catch (Exception e) {
                    e.printStackTrace();
                    callBack.onFail(e);
                }
            }
        }).start();


    }

    public  static String  sendPost(String url, String parame, Map<String,Object> pmap) throws IOException, KeyManagementException, NoSuchAlgorithmException, NoSuchProviderException {
        // 请求结果
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";           //返回值
        URL realUrl;
        HttpsURLConnection conn;
        String method = "POST";
        //查询地址
        String queryString = url;
        //请求参数获取
        String postpar = "";
        //字符串请求参数
        SSLContext tls = SSLContext.getInstance("TLS");
        //3.定义一个 TrustManagerFactory,让这个工厂生成TrustManager数组
        if(parame!=null){
            postpar = parame;
        }
        // map格式的请求参数
        if(pmap!=null){
            StringBuffer mstr = new StringBuffer();
            for(String str:pmap.keySet()){
                String val = (String) pmap.get(str);
                try {
                    val= URLEncoder.encode(val,"UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                mstr.append(str+"="+val+"&");
            }
            // 最终参数
            postpar = mstr.toString();
            int lasts=postpar.lastIndexOf("&");
            postpar=postpar.substring(0, lasts);
        }
        if(method.toUpperCase().equals("GET")){
            queryString+="?"+postpar;
        }

        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {  //信任所以证书
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }

            @Override
            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }};

        tls.init(null, trustAllCerts, new SecureRandom());
        SSLSocketFactory  ssf= tls.getSocketFactory();

        try {
            realUrl= new URL(queryString);
            conn = (HttpsURLConnection)realUrl.openConnection();
            conn.setSSLSocketFactory(ssf);
            conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            conn.setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            if(method.toUpperCase().equals("POST")){
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setUseCaches(false);
                out = new PrintWriter(conn.getOutputStream());
                out.print(postpar);
                out.flush();
            }else{
                conn.connect();
            }
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(),"utf-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }


        }finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }

    //拿到自己的证书
    X509Certificate getX509Certificate(Context context) throws IOException, CertificateException {
        InputStream in = context.getAssets().open("srca.cer");
        CertificateFactory instance = CertificateFactory.getInstance("X.509");
        X509Certificate certificate = (X509Certificate) instance.generateCertificate(in);
        return certificate;
    }

}
