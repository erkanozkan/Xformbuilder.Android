package com.prg.xformbuilder.xformbuilder;

/**
 * Created by Profesor-PC on 20.4.2015.
 */
public class PutDraftForm {

    private String  _draftJson;
    private int _id, _formId, _userId;


    public PutDraftForm(int id, int formId,  String drafJson,  int userId) {
        _id = id;
        _formId = formId;
        _draftJson = drafJson;
        _userId = userId;
    }
    public int getId() {
        return _id;
    }
    public int getFormId() {
        return _formId;
    }

    public String getDraftJson() {
        return _draftJson;
    }

    public int getUserId() {
        return _userId;
    }
}