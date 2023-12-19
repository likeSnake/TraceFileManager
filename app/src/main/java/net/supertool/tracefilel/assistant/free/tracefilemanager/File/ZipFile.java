package net.supertool.tracefilel.assistant.free.tracefilemanager.File;


public class ZipFile {
    private boolean isSelect = false;
    private String path;
    private String size;
    private String time;
    private String name;
    private long fileSize;


    public ZipFile(String path, String size, String time, String name, long fileSize) {
        this.path = path;
        this.size = size;
        this.time = time;
        this.name = name;
        this.fileSize = fileSize;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public boolean getSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
