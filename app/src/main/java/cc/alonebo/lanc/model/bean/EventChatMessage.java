package cc.alonebo.lanc.model.bean;

/**
 * Created by alonebo on 17-5-20.
 */

public class EventChatMessage {
    private ChatMessageBean chatMessageBean;

    public ChatMessageBean getChatMessageBean() {
        return chatMessageBean;
    }

    public EventChatMessage setChatMessageBean(ChatMessageBean chatMessageBean) {
        this.chatMessageBean = chatMessageBean;
        return this;
    }
}
