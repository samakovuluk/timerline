import java.time.LocalDateTime;

public class Tim {

    private Long chat_id;

    private Long user_id;

    private String path;


    public Tim(Long chat_id, Long user_id, String path) {
        this.chat_id = chat_id;
        this.path = path;
        this.user_id = user_id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Long getChat_id() {
        return chat_id;
    }

    public void setChat_id(Long chat_id) {
        this.chat_id = chat_id;
    }

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }
}
