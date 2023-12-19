package net.supertool.tracefilel.assistant.free.tracefilemanager.File;

public class ImageFile {
    private boolean isSelect = false;
    private String path;
    private String size;
    private String time;
    private String name;
    private long imageLongSize;


    public ImageFile(String path, String size, String time, String name, long imageLongSize) {
        this.path = path;
        this.size = size;
        this.time = time;
        this.name = name;
        this.imageLongSize = imageLongSize;
    }

    public long getImageLongSize() {
        return imageLongSize;
    }

    public void setImageLongSize(long imageLongSize) {
        this.imageLongSize = imageLongSize;
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

    public boolean isSelect() {
        return isSelect;
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
