package com.prg.xformbuilder.xformbuilder;

/**
 * Created by KAPLAN 12 on 5.5.2015.
 */
public class LogError {
    private String _errorMessage,_methodName,_description,_date,_version,_userName;
    private int _id,_userId,_parentId;


    public LogError (int id,String  methodName ,String  description, String errorMessage,String  date ,String userName,String version,int userId,int parentId){
        _id=id;
        _errorMessage=errorMessage;
        _methodName=methodName;
        _description=description;
        _date=date;
        _userId = userId;
        _parentId = parentId;
        _version = version;
        _userName = userName;

    }

    public int getId() {return _id;}
    public String getErrorMessage() {return _errorMessage;}
    public String getMethodName() {return _methodName;}
    public String getDescription() {return _description;}
    public String getDate() {return _date;}

    public String getUserName() {return _userName;}
    public int getUserId() {return _userId;}
    public int getParentId() {return _parentId;}
    public String getVersion() {return _version;}



}
