package com.prg.xformbuilder.xformbuilder;

/**
 * Created by Profesor-PC on 7.4.2015.
 */

public class Form {
    private String _formTitle,_userName,_formGroupId,_formDescription,_language,_successMesage,_mobileHtml,_jsCode;
    private int _userId,_parentId,_id,_formId,_statusForm;
    private boolean _isPublic;
    public Form (int id, String formTitle, int formId, int parentId,String userName,String formGroupId,String formDescription,String language,boolean isPublic,String successMesage,int statusForm,String mobileHtml,String jsCode){
        _id=id;
        _formTitle=formTitle;
        _formId=formId;
        _parentId=parentId;
        _userName=userName;
        _formGroupId=formGroupId;
        _formDescription=formDescription;
        _language=language;
        _isPublic=isPublic;
        _successMesage=successMesage;
        _statusForm=statusForm;
        _mobileHtml=mobileHtml;
        _jsCode=jsCode;
    }
    public int getId() {return _id;}
    public String getFormTitle() {return _formTitle;}
    public int getFormId(){return _formId;}
    public int getParentId(){return _parentId;}
    public String getUserName(){return _userName;}
    public String getFormGroupId(){return _formGroupId;}
    public String getFormDescription(){return _formDescription;}
    public String getLanguage(){return _language;}
    public Boolean getIsPublic(){return _isPublic;}
    public String getSuccessMesage(){return _successMesage;}
    public int getStatusForm(){return _statusForm;}
    public String getMobileHtml(){return _mobileHtml;}
    public String getjsCode(){return _jsCode;}




}

