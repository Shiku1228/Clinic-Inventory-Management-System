package utils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import models.Notifications;

public class NotificationManager {
    
    private static final ObservableList<Notifications> activityFeed =
            FXCollections.observableArrayList();
    
    private NotificationManager() {}
    
    public static ObservableList<Notifications> getFeed(){
        return activityFeed;
    }
    
    public static void push(String message, String timeText, String type) { 
        activityFeed.add(0, new Notifications(message, timeText, type, null));
    }
}
