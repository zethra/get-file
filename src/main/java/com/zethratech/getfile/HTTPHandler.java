package com.zethratech.getfile;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.util.*;

public class HTTPHandler extends AbstractHandler {

    private List<IpInterface> interfaces = null;
    private IpInterface defaultInterface = null;
    private File file = null;

    @Override
    public void handle(String target,
                       Request baseRequest,
                       HttpServletRequest request,
                       HttpServletResponse response)
            throws IOException, ServletException {

//        System.out.println(target);
        System.out.println(baseRequest.getUri());
        switch (target) {
            case "/get":
                baseRequest.setHandled(true);
                if (request.getQueryString() == null) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    System.err.println("No file name in request");
                    return;
                }
                Map<String, String> params = queryToMap(request.getQueryString());
                String fileName = URLDecoder.decode(params.get("file"), "ASCII");
                if (file == null) {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    System.err.println("No file selected");
                    return;
                }
                if (fileName == null || !fileName.equals(file.getName())) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    System.err.println("File name did not match selected file");
                    return;
                }

                response.setContentType("application/octet-stream");
                response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
                try (OutputStream os = response.getOutputStream()) {
                    Files.copy(file.toPath(), os);
                    os.flush();
                } catch (IOException e) {
                    System.err.println("Could not open file");
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }
                response.setStatus(HttpServletResponse.SC_OK);
                break;
        }
    }

    public void setFile(File file) {
        this.file = file;
    }

    public List<IpInterface> getInterfaces() {
        return interfaces;
    }

    public IpInterface getDefaultInterface() {
        return defaultInterface;
    }

    public void setDefaultInterface(IpInterface defaultInterface) {
        this.defaultInterface = defaultInterface;
    }

    public void generateInterfaceList() throws SocketException {
        interfaces = new ArrayList<>();
        Enumeration<NetworkInterface> n = NetworkInterface.getNetworkInterfaces();
        for (; n.hasMoreElements(); ) {
            NetworkInterface e = n.nextElement();
            Enumeration<InetAddress> a = e.getInetAddresses();
            for (; a.hasMoreElements(); ) {
                InetAddress addr = a.nextElement();
                if (validIP(addr.getHostAddress()) && !addr.getHostAddress().equals("127.0.0.1"))
                    interfaces.add(new IpInterface(e.getDisplayName(), addr.getHostAddress()));
            }
        }
        if (defaultInterface == null)
            defaultInterface = interfaces.get(0);
    }

    private static Map<String, String> queryToMap(String query) {
        Map<String, String> result = new HashMap<>();
        for (String param : query.split("&")) {
            String pair[] = param.split("=");
            if (pair.length > 1) {
                result.put(pair[0], pair[1]);
            } else {
                result.put(pair[0], "");
            }
        }
        return result;
    }

    private static boolean validIP(String ip) {
        try {
            if (ip == null || ip.isEmpty())
                return false;
            String[] parts = ip.split("\\.");
            if (parts.length != 4)
                return false;
            for (String s : parts) {
                int i = Integer.parseInt(s);
                if ((i < 0) || (i > 255))
                    return false;
            }
            return !ip.endsWith(".");
        } catch (NumberFormatException nfe) {
            return false;
        }
    }
}
