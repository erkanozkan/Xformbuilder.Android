package com.prg.xformbuilder.xformbuilder.events;

import com.prg.xformbuilder.xformbuilder.common.UploadErrorCode;

/**
 * Created by KAPLAN 12 on 15.5.2015.
 */
public interface UploadFilesCompleteListener {
    void onCompleted(UploadErrorCode errorCode);
}
