package com.prg.xformbuilder.xformbuilder;


import java.util.Date;


public class DraftForm {
    private String _draftHtml, _draftJson;
    private int _id, _formId,_userId;
    private String _dateDraft;


    public DraftForm(int id, int formId, String draftHtml, String drafJson, String dateDraft,int userId) {
        _id = id;
        _formId = formId;
        _draftHtml = draftHtml;
        _draftJson = drafJson;
        _dateDraft=dateDraft;
        _userId=userId;
    }

    public int getId() {
        return _id;
    }

    public int getFormId() {
        return _formId;
    }

    public String getDraftHtml() {
        return _draftHtml;
    }

    public String getDraftJson() {
        return _draftJson;
    }
    public String getDateDraft(){return _dateDraft;}
    public int getUserId(){return _userId;}

}
