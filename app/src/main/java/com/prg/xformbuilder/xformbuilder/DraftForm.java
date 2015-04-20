package com.prg.xformbuilder.xformbuilder;


import java.util.Date;


public class DraftForm {
    private String _draftHtml, _draftJson;
    private int _id, _formId,_userId;
    private String _dateDraft,_isUploadable;

    private String _field1Title, _field1Value , _field2Title , _field2Value , _field3Title , _field3Value ;


    public DraftForm(int id, int formId, String draftHtml, String drafJson, String dateDraft,int userId,String field1Title,String field1Value ,String field2Title ,String field2Value ,String field3Title ,String field3Value,String isUploadable) {
        _id = id;
        _formId = formId;
        _draftHtml = draftHtml;
        _draftJson = drafJson;
        _dateDraft=dateDraft;
        _userId=userId;
        _field1Title = field1Title;
        _field1Value = field1Value;
        _field2Title=field2Title;
        _field2Value=field2Value;
        _field3Title = field3Title;
        _field3Value=field3Value;
        _isUploadable=isUploadable;
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

    public String getIsUploadable() {
        return _isUploadable;
    }

    public String getDateDraft(){return _dateDraft;}
    public int getUserId(){return _userId;}

    public String getField1Title() {
        return _field1Title;
    }
    public String getField2Title() {
        return _field1Title;
    }
    public String getField3Title() {
        return _field3Title;
    }
    public String getField1Value() {
        return _field1Value;
    }
    public String getField2Value() {
        return _field2Value;
    }
    public String getField3Value() {
        return _field3Value;
    }

}
