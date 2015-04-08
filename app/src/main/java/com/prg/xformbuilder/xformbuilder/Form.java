package com.prg.xformbuilder.xformbuilder;

/**
 * Created by Profesor-PC on 7.4.2015.
 */

public class Form {
    private String _formTitle,_userName,_mobileHtml,_modifiedDate;
    private int _parentId,_id,_formId;

    public Form (int id, String formTitle, int formId, int parentId,String userName,String mobileHtml,String modifiedDate){
        _id=id;
        _formTitle=formTitle;
        _formId=formId;
        _parentId=parentId;
        _userName=userName;
        _mobileHtml=mobileHtml;
        _modifiedDate=modifiedDate;
    }
    public int getId() {return _id;}
    public String getFormTitle() {return _formTitle;}
    public int getFormId(){return _formId;}
    public int getParentId(){return _parentId;}
    public String getUserName(){return _userName;}
    public String getMobileHtml(){return _mobileHtml;}
    public String getModifiedDate() {return _modifiedDate;}





}

