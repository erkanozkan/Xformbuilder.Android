package com.prg.xformbuilder.xformbuilder;

/**
 * Created by KAPLAN 12 on 12.4.2015.
 */

public class SettingsModel{

  //  private int icon;
    private String title;
  //  private String counter; sayac eklemek istersek

    private boolean isGroupHeader = false;
    private boolean groupHeader;

    public SettingsModel(String title) {
        this(-1,title,null);
        isGroupHeader = true;
    }
    public SettingsModel(int icon, String title, String counter) {  //SettingsModel(int icon, String title, String counter)
        super();
      //  this.icon = icon;  icon için
        this.title = title;
       // this.counter = counter;
    }


    public boolean isGroupHeader() {
        return groupHeader;
    }

  /*  public int getIcon() {
        return icon;
    }*/

    public String getTitle() {
        return title;
    }

  /*  public String getCounter() {
        return counter;
    }*/
}