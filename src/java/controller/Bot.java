/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import com.restfb.*;
import com.restfb.types.send.IdMessageRecipient;
import com.restfb.types.send.Message;
import com.restfb.types.send.SendResponse;
import com.restfb.types.webhook.WebhookEntry;
import com.restfb.types.webhook.WebhookObject;
import com.restfb.types.webhook.messaging.MessagingItem;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author hoandk
 */
public class Bot extends HttpServlet {

    private String AccessToken = "your Access token here";
    private String verifyToken = "Your verify token here(could be any thing)";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String hubToken = request.getParameter("hub.verify_token");
        String hubChallenge = request.getParameter("hub.challenge");
        //if verify token of Message API same your verify token
        if (verifyToken.equals(hubToken)) {
            response.getWriter().write(hubChallenge);
            response.getWriter().flush();
            response.getWriter().close();
        } else {
            response.getWriter().write("wrong verify token");
        }
    }

    @Override

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        StringBuffer sb = new StringBuffer();
        BufferedReader br = request.getReader();
        String line = "";
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        JsonMapper mapper = new DefaultJsonMapper();
        //convert from string to java object
        WebhookObject webhookObj = mapper.toJavaObject(sb.toString(), WebhookObject.class);
        for (WebhookEntry entry : webhookObj.getEntryList()) {
            if (entry.getMessaging() != null) {
                for (MessagingItem mItem : entry.getMessaging()) {
                    //if user send message to the bot
                    
                    if (mItem.getMessage() != null && mItem.getMessage().getText() != null) {
                        String senderId = mItem.getSender().getId();
                        IdMessageRecipient recipient = new IdMessageRecipient(senderId);
                        System.out.println("user send mess: " + mItem.getMessage().getText() + ", sender id: " + senderId);
                        sendMessage(recipient, new Message(MessageProcess(mItem.getMessage().getText())));
                        return;
                    }

                }
            }
        }
        sb.delete(0, sb.length());
    }

    //you may need that function if your language have accents
    public static String convert(String str) {
        str = str.replaceAll("à|á|ạ|ả|ã|â|ầ|ấ|ậ|ẩ|ẫ|ă|ằ|ắ|ặ|ẳ|ẵ", "a");
        str = str.replaceAll("è|é|ẹ|ẻ|ẽ|ê|ề|ế|ệ|ể|ễ", "e");
        str = str.replaceAll("ì|í|ị|ỉ|ĩ", "i");
        str = str.replaceAll("ò|ó|ọ|ỏ|õ|ô|ồ|ố|ộ|ổ|ỗ|ơ|ờ|ớ|ợ|ở|ỡ", "o");
        str = str.replaceAll("ù|ú|ụ|ủ|ũ|ư|ừ|ứ|ự|ử|ữ", "u");
        str = str.replaceAll("ỳ|ý|ỵ|ỷ|ỹ", "y");
        str = str.replaceAll("đ", "d");
        return str;
    }

    //process the message then then select the fit message to answer
    //txt is input of user, mess is output of the bot
    static String MessageProcess(String txt) {
        String mess = "";
        if (txt.contains("user say something here")) {
            mess = "the bot say something here";
        }
        return mess;
    }

    void sendMessage(IdMessageRecipient recipient, Message message) {
        // create a version 11.0 client
        FacebookClient pageClient = new DefaultFacebookClient(AccessToken, Version.VERSION_11_0);
        SendResponse resp = pageClient.publish("me/messages", SendResponse.class,
                Parameter.with("recipient", recipient), // the id or phone recipient
                Parameter.with("message", message)); // one of the messages from above

    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
