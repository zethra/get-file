package com.zethratech.getfile;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import net.glxn.qrgen.javase.QRCode;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Ben Goldberg
 */
public class GetFile {
    public static void main(String[] args) throws IOException {
        Map<String, File> files = new HashMap<>();

        Enumeration<NetworkInterface> n = NetworkInterface.getNetworkInterfaces();
        for (; n.hasMoreElements();)
        {
            NetworkInterface e = n.nextElement();

            Enumeration<InetAddress> a = e.getInetAddresses();
            for (; a.hasMoreElements();)
            {
                InetAddress addr = a.nextElement();
                System.out.println("  " + addr.getHostAddress());
            }
        }

        JLabel qrLable = new JLabel();
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());
        JButton selectButton = new JButton("Select Files");
        selectButton.setActionCommand("select");
        selectButton.addActionListener(actionListener -> {
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                files.put(selectedFile.getName(), selectedFile);
                System.out.println(selectedFile.getName());
                File file = QRCode.from("http://" + "" +":8080/get?file=" + selectedFile.getName()).file();
                BufferedImage qrCode = null;
                try {
                    qrCode = ImageIO.read(file);
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
                ImageIcon icon = new ImageIcon(qrCode);
                qrLable.setIcon(icon);
            }
        });
        controlPanel.add(selectButton);

        JFrame mainFrame = new JFrame("Get File");
        mainFrame.setSize(400, 400);
        mainFrame.setLayout(new GridLayout(2, 1));
        mainFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent) {
                System.exit(0);
            }
        });
        mainFrame.add(qrLable);
        mainFrame.add(controlPanel);
        mainFrame.setVisible(true);

        Server server = new Server(8080);
        server.setHandler(new AbstractHandler() {
            @Override
            public void handle(String target,
                               Request baseRequest,
                               HttpServletRequest request,
                               HttpServletResponse response)
                    throws IOException, ServletException {

                baseRequest.setHandled(true);
                if(request.getQueryString() == null){
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }
                Map<String, String> params = queryToMap(request.getQueryString());
                String fileName = params.get("file");
                if(fileName == null){
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }

                response.setContentType("application/octet-stream");
                response.setHeader("Content-Disposition", "attachment; filename=\"test\"");
                response.getWriter().write(readFile(files.get(fileName).getAbsolutePath(), StandardCharsets.UTF_8));
                response.setStatus(HttpServletResponse.SC_OK);
            }
        });
        try {
            server.start();
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
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

    static String readFile(String path, Charset encoding)
            throws IOException
    {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }
}
