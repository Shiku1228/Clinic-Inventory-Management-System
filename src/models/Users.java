
package models;


import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Users {
    private final StringProperty id;
    private final StringProperty name;
    private final StringProperty role;
    private final StringProperty status;
    private final StringProperty avatarUrl;
    
    public Users (String id, String name, String role, String status, String avatarUrl){
        this.id = new SimpleStringProperty(id);
        this.name = new SimpleStringProperty (name);
        this.role = new SimpleStringProperty (role);
        this.status = new SimpleStringProperty (status);
        this.avatarUrl = new SimpleStringProperty(avatarUrl);
    }
    
    //Getters
    public String getId(){return id.get();}
    public String getName() {return name.get();}
    public String getRole() {return role.get();}
    public String getStatus() {return status.get();}
    public String getAvatarUrl() { return avatarUrl.get(); }
    
    //Setters
    public void setId(String id) {this.id.set(id);}
    public void setName(String name) {this.name.set(name);}
    public void setRole(String role) {this.role.set(role);}
    public void setStatus(String status) {this.status.set(status);}
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl.set(avatarUrl); }
    
    //Properties for Table View
    public StringProperty idProperty() {return id;}
    public StringProperty nameProperty() {return name;}
    public StringProperty roleProperty() {return role;}
    public StringProperty statusProperty() {return status;}
    public StringProperty avatarUrlProperty() { return avatarUrl; }
}   
