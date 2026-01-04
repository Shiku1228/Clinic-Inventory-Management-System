
package models;


import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Users {
    private final StringProperty mongoId;
    private final StringProperty userId;
    private final StringProperty name;
    private final StringProperty role;
    private final StringProperty contact;
    private final StringProperty email;
    private final StringProperty status;
    private final StringProperty avatarPath;
    
    public Users (String mongoId, String userId, String name, String role, String contact, String email, String status, String avatarPath){
        this.mongoId = new SimpleStringProperty (mongoId);
        this.userId = new SimpleStringProperty(userId);
        this.name = new SimpleStringProperty (name);
        this.role = new SimpleStringProperty (role);
        this.contact = new SimpleStringProperty(contact);
        this.email = new SimpleStringProperty(email);
        this.status = new SimpleStringProperty (status);
        this.avatarPath = new SimpleStringProperty(avatarPath);
    }
    
    //Getters
    public String getMongoId(){return mongoId.get();}
    public String getUserId(){return userId.get();}
    public String getName() {return name.get();}
    public String getRole() {return role.get();}
    public String getContact() {return contact.get();}
    public String getEmail() {return email.get();}
    public String getStatus() {return status.get();}
    public String getAvatarPath() { return avatarPath.get(); }
    
    //Setters
    public void setMongoId(String id) {this.mongoId.set(id);}
    public void setUserId(String id) {this.userId.set(id);}
    public void setName(String name) {this.name.set(name);}
    public void setRole(String role) {this.role.set(role);}
    public void setContact(String contact) {this.contact.set(contact);}
    public void setEmail(String email) {this.email.set(email);}
    public void setStatus(String status) {this.status.set(status);}
    public void setAvatarPath(String avatarPath) { this.avatarPath.set(avatarPath); }
    
    //Properties for Table View
    public StringProperty mongoIdProperty() {return mongoId;}
    public StringProperty userIdProperty() {return userId;}
    public StringProperty nameProperty() {return name;}
    public StringProperty roleProperty() {return role;}
    public StringProperty contactProperty() {return contact;}
    public StringProperty emailProperty() {return email;}
    public StringProperty statusProperty() {return status;}
    public StringProperty avatarPathProperty() { return avatarPath; }
}   
