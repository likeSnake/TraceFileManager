package net.supertool.tracefilel.assistant.free.tracefilemanager.File;

public class AudioFile {
    private boolean isSelect = false;
    private String path;
    private String size;
    private String time;
    private String name;
    private long audioLongSize;


    public AudioFile(String path, String size, String time, String name, long audioLongSize) {
        this.path = path;
        this.size = size;
        this.time = time;
        this.name = name;
        this.audioLongSize = audioLongSize;
    }

    public long getAudioLongSize() {
        return audioLongSize;
    }

    public void setAudioLongSize(long audioLongSize) {
        this.audioLongSize = audioLongSize;
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
