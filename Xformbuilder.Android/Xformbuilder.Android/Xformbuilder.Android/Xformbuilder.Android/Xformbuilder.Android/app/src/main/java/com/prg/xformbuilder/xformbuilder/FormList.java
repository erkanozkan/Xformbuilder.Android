package com.prg.xformbuilder.xformbuilder;

/**
 * Created by Profesor-PC on 7.4.2015.
 */

public class FormList {

    private int formId;
    private String formTitle;
    private String username;
    private int formImage;

    public FormList(int _formId, String _formTitle, String _username, int _formImage){
        this.formId = _formId;
        this.formTitle = _formTitle;
        this.username = _username;
        this.formImage = _formImage;
    }

    public int getFormId(){
        return formId;
    }

    public String getFormTitle(){
        return formTitle;
    }

    public String getUserName(){
        return username;
    }

    public int getFormImage(){
        return formImage;
    }
}