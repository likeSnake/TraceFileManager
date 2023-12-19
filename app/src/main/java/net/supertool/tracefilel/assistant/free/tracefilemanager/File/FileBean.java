package net.supertool.tracefilel.assistant.free.tracefilemanager.File;

public class FileBean {
    private boolean isSelect = false;
    private boolean isFolder;
    private String path;
    private String size;
    private String time;
    private String name;
    private Long fileSize;
    private int fileCount;

    public FileBean(boolean isFolder, String path, String size, String time, String name, Long fileSize, int fileCount) {
        this.isFolder = isFolder;
        this.path = path;
        this.size = size;
        this.time = time;
        this.name = name;
        this.fileSize = fileSize;
        this.fileCount = fileCount;
    }

    public int getFileCount() {
        return fileCount;
    }

    public void setFileCount(int fileCount) {
        this.fileCount = fileCount;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public boolean isFolder() {
        return isFolder;
    }

    public void setFolder(boolean folder) {
        isFolder = folder;
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

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }
}
