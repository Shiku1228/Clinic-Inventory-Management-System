package models;

import java.time.LocalDateTime;

public class Notifications {
    
    private final String message;
    private final String timeText;
    private final String type;
    private final LocalDateTime timestamp;
    
    public Notifications(String message, String timeText, String type, LocalDateTime timestamp){
        this.message = message;
        this.timeText = timeText;
        this.type = type;
        this.timestamp = LocalDateTime.now();
    }
    
    //getter
    public String getMessage(){
        return message;
    }
    
    public String getTimeText(){
        return timeText;
    }
    
    public String getType(){
        return type;
    }
    
    public LocalDateTime getTimestamp(){
        return timestamp;
    }
}
