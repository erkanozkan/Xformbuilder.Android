package com.prg.xformbuilder.xformbuilder;

/**
 * Created by Profesor-PC on 7.4.2015.
 */

public class Files {
     private String  _formId,_path,_elementId,_draftId;
      private  int _id;
    public Files (int id, String formId, String elementId,String  path,String draftId){
        _id=id;
          _formId=formId;
        _elementId=elementId;
        _draftId = draftId;
        _path=path;
    }
      public int getId() {return _id;}
      public String getFormId(){return _formId;}
      public String getElementId(){return _elementId;}
      public String getPath(){return _path;}
      public String getDraftId(){return _draftId;}
}