package com.zethratech.getfile;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class HTTPHandler extends AbstractHandler {

    private List<IpInterface> interfaces = null;
    private File file = null;

    @Override
    public void handle(String target,
                       Request baseRequest,
                       HttpServletRequest request,
                       HttpServletResponse response)
            throws IOException, ServletException {

        System.out.println(target);
        switch (target) {
            case "/get":
                baseRequest.setHandled(true);
                if (request.getQueryString() == null) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }
                Map<String, String> params = queryToMap(request.getQueryString());
                String fileName = params.get("file");
                if (fileName == null || !fileName.equals(file.getName())) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }

                response.setContentType("application/octet-stream");
                response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
                response.getWriter().write(readFile(file.getAbsolutePath(), StandardCharsets.UTF_8));
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

    public void generateInterfaceList() throws SocketException {
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
    }

    private static Map<String, String> queryToMap(String query) {
        Map<String, String> result = new HashMap<String, String>();
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

    private static String readFile(String path, Charset encoding)
            throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
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
