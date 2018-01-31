package com.pratham.prathamdigital.interfaces;

import org.apache.commons.net.ftp.FTPFile;

/**
 * Created by pefpr on 31/01/2018.
 */

public interface FolderClick {
    public void onFolderClicked(int position, String name);
    public void onDownload(int position, FTPFile name);
}
