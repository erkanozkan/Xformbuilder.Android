package com.prg.xformbuilder.xformbuilder;

import android.graphics.Bitmap;

/**
 * Created by Profesor-PC on 7.4.2015.
 */

public class FormList {

    private int formId;
    private String formTitle;
    private String username;
    private Bitmap formImage;
    private String draftCount;
    private int imgPencil;

    public FormList(int _formId, String _formTitle, String _username, Bitmap _formImage,String _draftCount,int _imgPencil){
        this.formId = _formId;
        this.formTitle = _formTitle;
        this.username = _username;
        this.formImage = _formImage;
        this.draftCount = _draftCount;
        this.imgPencil = _imgPencil;
    }

    public int getFormId(){
        return formId;
    }

    public String getDraftCount(){
        return draftCount;
    }

    public int getImgPencil(){
        return imgPencil;
    }



    public String getFormTitle(){
        return formTitle;
    }

    public String getUserName(){
        return username;
    }

    public Bitmap getFormImage(){
        return formImage;
    }
 }