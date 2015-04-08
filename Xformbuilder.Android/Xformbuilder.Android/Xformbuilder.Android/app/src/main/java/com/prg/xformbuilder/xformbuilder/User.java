package com.prg.xformbuilder.xformbuilder;

import java.util.Date;

/**
 * Created by Profesor-PC on 6.4.2015.
 */
public class User {
    private String _userName,_firstName,_lastName,_company,_password;
    private int _userId,_parentId,_id;
    private Date _modifiedDate;

    public User (int id, String userName, String firstName, String lastName, String company, String password, int userId, int parentId,Date modifiedDate){
        _id=id;
        _company=company;
        _firstName=firstName;
        _lastName=lastName;
        _parentId=parentId;
        _userId=userId;
        _userName=userName;
        _password=password;
        _modifiedDate=modifiedDate;

    }

    public int getId() {return _id;}
    public String getUserName() {return _userName;}
    public String getFirstName() {return _firstName;}
    public String getLastName() {return _lastName;}
    public String getCompany() {return _company;}
    public String getPassword() {return _password;}
    public int getUserId() {return _userId;}
    public int getParentId() {return _parentId;}
    public Date getModifiedDate(){return _modifiedDate;}

}
