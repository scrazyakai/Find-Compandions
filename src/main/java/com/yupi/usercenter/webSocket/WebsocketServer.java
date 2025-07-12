package com.yupi.usercenter.webSocket;

import com.alibaba.fastjson.JSON;
import com.yupi.usercenter.model.dto.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.concurrent.ConcurrentHashMap;
@ServerEndpoint("/chat/{userId}")
@Slf4j
@Component
public class WebsocketServer{
    // 存储在线用户
    private static ConcurrentHashMap<String, Session> onlineUsers = new ConcurrentHashMap<>();

    private String userId;

    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId) {
        this.userId = userId;
        onlineUsers.put(userId, session);
        System.out.println("用户上线：" + userId);
    }

    @OnMessage
    public void onMessage(String messageJson, Session session) {
        Message message = JSON.parseObject(messageJson, Message.class);

        if ("group".equals(message.getType())) {
            // 群发消息
            onlineUsers.forEach((uid, s) -> {
                try {
                    s.getBasicRemote().sendText(JSON.toJSONString(message));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } else if ("private".equals(message.getType())) {
            // 私聊
            long toUserId = message.getToUserId();
            Session toSession = onlineUsers.get(toUserId);
            if (toSession != null && toSession.isOpen()) {
                try {
                    toSession.getBasicRemote().sendText(JSON.toJSONString(message));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @OnClose
    public void onClose() {
        onlineUsers.remove(this.userId);
        System.out.println("用户下线：" + this.userId);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("WebSocket 发生错误，用户: {}", this.userId, error);
    }
}
