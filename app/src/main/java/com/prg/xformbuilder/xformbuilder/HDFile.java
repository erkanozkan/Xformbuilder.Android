package com.prg.xformbuilder.xformbuilder;

/**
 * Created by KAPLAN 12 on 15.5.2015.
 */
public class HDFile {

    private String Id;
    private String Name;
    private boolean Selected;
    private String FilePath;
    private String Size;
    private String Url;
    private String ElementId;
    private String GuId;
    private String FormId;
    private String UserId;

    private String FileId;


    public  String getFileId() {return FileId;}

    public void setFileId(String fileId){FileId=fileId;}



    public  String getUserId() {return UserId;}

    public void setUserId(String userId){UserId=userId;}


    public  String getFormId() {return FormId;}

    public void setFormId(String formId){FormId=formId;}



    public  String getGuId() {return GuId;}
    public void setGuId(String guid){GuId=guid;}



    public  String getElementId() {return ElementId;}

    public void setElementId(String elementId){ElementId=elementId;}


    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }



    public void setName(String name) {
        Name = name;
    }

    public boolean isSelected() {
        return Selected;
    }

    public void setSelected(boolean selected) {
        Selected = selected;
    }

    public String getFilePath() {
        return FilePath;
    }

    public void setFilePath(String filePath) {
        FilePath = filePath;
    }

    public String getSize() {
        return Size;
    }

    public void setSize(String size) {
        Size = size;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }
}
