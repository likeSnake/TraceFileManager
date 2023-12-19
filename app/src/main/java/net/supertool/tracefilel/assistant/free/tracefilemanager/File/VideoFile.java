package net.supertool.tracefilel.assistant.free.tracefilemanager.File;


public class VideoFile {
    private boolean isSelect = false;
    private String path;
    private String size;
    private String time;
    private String name;
    private String videoDuration;
    private long videoLongSize;

    public VideoFile(String path, String size, String time, String name, String videoDuration, long videoLongSize) {
        this.path = path;
        this.size = size;
        this.time = time;
        this.name = name;
        this.videoDuration = videoDuration;
        this.videoLongSize = videoLongSize;
    }

    public long getVideoLongSize() {
        return videoLongSize;
    }

    public void setVideoLongSize(long videoLongSize) {
        this.videoLongSize = videoLongSize;
    }

    public String getVideoDuration() {
        return videoDuration;
    }

    public void setVideoDuration(String videoDuration) {
        this.videoDuration = videoDuration;
    }

    public boolean isSelect() {
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
