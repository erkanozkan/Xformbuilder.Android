package com.prg.xformbuilder.xformbuilder;


/*
 String sqlDraftForm= ("CREATE TABLE IF NOT EXISTS  "+TABLE_DRAFTFORM + "(" +KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +KEY_FORMID
               + " TEXT," +KEY_DRAFHTML + " TEXT," +KEY_DRAFTJSON + " TEXT," +KEY_MOBILEHTML + " TEXT)");

               */
public class DraftForm {
    private String _draftHtml, _draftJson, _mobileHtml;
    private int _id, _formId;

    public DraftForm(int id, int formId, String draftHtml, String drafJson, String mobileHtml) {
        _id = id;
        _formId = formId;
        _draftHtml = draftHtml;
        _draftJson = drafJson;
        _mobileHtml = mobileHtml;
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
    public String getMobileHtml() {
        return _mobileHtml;
    }

}
