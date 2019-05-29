package com.bsep_sbz.PKI.config.ftp;

import org.apache.commons.net.ftp.FTPSClient;
import org.springframework.integration.ftp.session.DefaultFtpsSessionFactory;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSessionContext;
import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.Locale;


public class FtpsSessionFactory extends DefaultFtpsSessionFactory {

    @Override
    public FTPSClient createClientInstance() {
        return new SharedSSLFTPSClient();
    }

    private static final class SharedSSLFTPSClient extends FTPSClient {

        @Override
        protected void _prepareDataSocket_(final Socket socket) throws IOException {
            if (socket instanceof SSLSocket) {
                // Control socket is SSL
                final SSLSession session = ((SSLSocket) _socket_).getSession();
                final SSLSessionContext context = session.getSessionContext();
                context.setSessionCacheSize(0); // you might want to limit the cache
                try {
                    final Field sessionHostPortCache = context.getClass()
                            .getDeclaredField("sessionHostPortCache");
                    sessionHostPortCache.setAccessible(true);
                    final Object cache = sessionHostPortCache.get(context);

                    final Method method = cache.getClass().getDeclaredMethod("put", Object.class, Object.class);
                    method.setAccessible(true);

                    String key = String.format("%s:%s", socket.getInetAddress().getHostName(),
                            String.valueOf(socket.getPort())).toLowerCase(Locale.ROOT);
                    method.invoke(cache, key, session);

                    key = String.format("%s:%s", socket.getInetAddress().getHostAddress(),
                            String.valueOf(socket.getPort())).toLowerCase(Locale.ROOT);
                    method.invoke(cache, key, session);
                }
                catch (NoSuchFieldException e) {
                    // Not running in expected JRE
                    System.out.println("No field sessionHostPortCache in SSLSessionContext");
                    e.printStackTrace();
                }
                catch (Exception e) {
                    // Not running in expected JRE
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                }
            }

        }

    }
}
