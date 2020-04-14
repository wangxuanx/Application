package com.example.application.http;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

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

    //拿到自己的证书
    X509Certificate getX509Certificate(Context context) throws IOException, CertificateException {
        InputStream in = context.getAssets().open("srca.cer");
        CertificateFactory instance = CertificateFactory.getInstance("X.509");
        X509Certificate certificate = (X509Certificate) instance.generateCertificate(in);
        return certificate;
    }

}
