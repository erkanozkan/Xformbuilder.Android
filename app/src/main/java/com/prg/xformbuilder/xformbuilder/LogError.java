package com.prg.xformbuilder.xformbuilder;

/**
 * Created by KAPLAN 12 on 5.5.2015.
 */
public class LogError {
    private String _error,_errorMessage;
    private int _id,_userId,_parentId;


    public LogError (int id, String error, String errorMessage,int userId,int parentId){
        _id=id;
        _error=error;
        _errorMessage=errorMessage;
        _userId=userId;
        _parentId=parentId;

    }

    public int getId() {return _id;}
    public String getError() {return _error;}
    public String getErrorMessage() {return _errorMessage;}
    public int getUserId() {return _userId;}
    public int getParentId() {return _parentId;}


}
