package com.prg.xformbuilder.xformbuilder;

/**
 * Created by Profesor-PC on 7.4.2015.
 */

public class Form {
    private String _formTitle,_userName,_mobileHtml;
    private int _parentId,_id,_formId,_userId;
    private String  _formImage;

    public Form (int id, String formTitle, int formId, int parentId,String userName,String mobileHtml,int userId,String formImage){
        _id=id;
        _formTitle=formTitle;
        _formId=formId;
        _parentId=parentId;
        _userName=userName;
        _mobileHtml=mobileHtml;
        _userId=userId;
        _formImage=formImage;
    }
    public int getId() {return _id;}
    public String getFormTitle() {return _formTitle;}
    public int getFormId(){return _formId;}
    public int getParentId(){return _parentId;}
    public String getUserName(){return _userName;}
    public String getMobileHtml(){return _mobileHtml;}
    public int getUserId() {return _userId;}
    public String getFormImage(){return _formImage;}
}

