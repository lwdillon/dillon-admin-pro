package com.lw.fx.websocket;

import com.lw.dillon.admin.framework.common.util.json.JsonUtils;
import com.lw.dillon.admin.framework.websocket.core.message.JsonWebSocketMessage;
import com.lw.fx.store.AppStore;
import de.saxsys.mvvmfx.MvvmFX;
import io.datafx.core.concurrent.ProcessChain;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.enums.ReadyState;
import org.java_websocket.handshake.ServerHandshake;

import javax.net.ssl.*;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * sslweb插座端
 * 构建SSLWebSocket客户端，忽略证书
 *
 * @author wenli
 * @date 2023/06/01
 */
@Slf4j
public class SSLWebSocketClient extends WebSocketClient {

    private static SSLWebSocketClient instance;

    private static ScheduledExecutorService executorService;

    public static synchronized SSLWebSocketClient getInstance() {
        if (instance == null) {
            try {
                String url = System.getProperty("app.server.url") + "infra/ws";
                String urlStr = url.replace("http", "ws") + "?token=" + AppStore.getToken();
                instance = new SSLWebSocketClient(new URI(urlStr), new Draft_6455());
                executorService = new ScheduledThreadPoolExecutor(1,
                        new BasicThreadFactory.Builder().namingPattern("example-schedule-pool-%d").daemon(true).build());

            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }

        return instance;
    }


    @Override
    public void onOpen(ServerHandshake handshakedata) {
        log.info("[websocket] 连接成功");


    }

    @Override
    public void onMessage(String message) {
        log.info("[websocket] 收到消息={}", message);


        ProcessChain.create()
                .addSupplierInExecutor(() -> {
                    return JsonUtils.parseObject(message, JsonWebSocketMessage.class);
                })
                .addConsumerInPlatformThread(rel -> {

                    if (rel != null) {
                        MvvmFX.getNotificationCenter().publish("socketMessage", rel);
                    }
                })
                .onException(e -> {
                    e.printStackTrace();
                })
                .withFinal(() -> {
                })
                .run();

    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        log.info("[websocket-" + reason + " 退出连接");
    }

    @Override
    public void onError(Exception ex) {
        log.info("[websocket] 连接错误={}", ex.getMessage());
    }

    //构造方法
    public SSLWebSocketClient(URI serverURI, String message) {
        super(serverURI);
        if (serverURI.toString().contains("wss://")) {
            trustAllHosts(this);
            this.send(message);
        }
    }

    public SSLWebSocketClient(URI serverURI, Draft draft) {
        super(serverURI, draft);
        if (serverURI.toString().contains("wss://")) {
            trustAllHosts(this);
        }
    }


    /**
     * 忽略证书
     *
     * @paramclient
     */
    void trustAllHosts(SSLWebSocketClient client) {
        TrustManager[] trustAllCerts = new TrustManager[]{new X509ExtendedTrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] x509Certificates, String s, Socket socket) throws CertificateException {

            }

            @Override
            public void checkServerTrusted(X509Certificate[] x509Certificates, String s, Socket socket) throws CertificateException {

            }

            @Override
            public void checkClientTrusted(X509Certificate[] x509Certificates, String s, SSLEngine sslEngine) throws CertificateException {

            }

            @Override
            public void checkServerTrusted(X509Certificate[] x509Certificates, String s, SSLEngine sslEngine) throws CertificateException {

            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
//                return new java.security.cert.X509Certificate[]{};
//                System.out.println("getAcceptedIssuers");
                return null;
            }

            @Override
            public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                System.out.println("checkClientTrusted");
            }

            @Override
            public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                System.out.println("checkServerTrusted");
            }
        }};

        try {

            SSLContext ssl = SSLContext.getInstance("SSL");
            ssl.init(null, trustAllCerts, new java.security.SecureRandom());

            SSLSocketFactory socketFactory = ssl.getSocketFactory();
            this.setSocketFactory(socketFactory);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loginOut() {
        if (instance != null && instance.isOpen()) {
            instance.close();
            executorService.shutdown();
        }
        executorService = null;
        instance = null;
    }


    public void start() {
        executorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    if (instance.getReadyState() != ReadyState.OPEN) {
                        if (instance.getReadyState() == ReadyState.NOT_YET_CONNECTED) {
                            if (instance.isClosed()) {
                                log.info("连接关闭,正在重新连接中……");
                                instance.reconnect();
                            } else {
                                log.info("建立连接中……");
                                instance.connect();
                            }
                        } else if (instance.getReadyState() == ReadyState.CLOSED) {
                            log.info("连接关闭,正在重新连接中……");
                            instance.reconnect();
                        }
                    }
                } catch (Exception e) {
                    log.error("连接异常,正在重新连接中……");
                    instance.reconnect();
                }
            }
        }, 0, 6000, TimeUnit.MILLISECONDS);

    }

}
