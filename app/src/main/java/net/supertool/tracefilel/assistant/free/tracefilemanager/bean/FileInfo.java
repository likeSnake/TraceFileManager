package net.supertool.tracefilel.assistant.free.tracefilemanager.bean;

import net.supertool.tracefilel.assistant.free.tracefilemanager.File.ZipFile;

import java.util.ArrayList;

public class FileInfo {

    private String formattedSize;
    private ArrayList<ZipFile> bigFilePath;
    private String bigFileSize;

    public FileInfo() {
    }


    public String getFormattedSize() {
        return formattedSize;
    }

    public void setFormattedSize(String formattedSize) {
        this.formattedSize = formattedSize;
    }

    public ArrayList<ZipFile> getBigFilePath() {
        return bigFilePath;
    }

    public void setBigFilePath(ArrayList<ZipFile> bigFilePath) {
        this.bigFilePath = bigFilePath;
    }

    public String getBigFileSize() {
        return bigFileSize;
    }

    public void setBigFileSize(String bigFileSize) {
        this.bigFileSize = bigFileSize;
    }

    public FileInfo(String formattedSize, ArrayList<ZipFile> bigFilePath, String bigFileSize) {
        this.formattedSize = formattedSize;
        this.bigFilePath = bigFilePath;
        this.bigFileSize = bigFileSize;
    }
}
