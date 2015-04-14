package com.prg.xformbuilder.xformbuilder;

/**
 * Created by Profesor-PC on 13.4.2015.
 */


public class DraftList {


    private int draftImage;
    private String draftDate,formId,draftId;



    public DraftList(int _draftImage, String _draftDate , String _formId,String _draftId){
        this.formId = _formId;
        this.draftId = _draftId;
        this.draftDate = _draftDate;
        this.draftImage = _draftImage;
    }

    public String getFormId(){
        return formId;
    }

    public String getDraftDate(){
        return draftDate;
    }

    public String getDraftId(){return draftId;}

    public int getDraftImage(){
        return draftImage;
    }


}
