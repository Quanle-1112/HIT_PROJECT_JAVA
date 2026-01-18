package org.example.model.chapter;


import com.google.gson.annotations.SerializedName;
import java.util.List;

public class AllChapter {
    @SerializedName("server_name")
    private String serverName;

    @SerializedName("server_data")
    private List<ChapterInfo> serverData;

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public List<ChapterInfo> getServerData() {
        return serverData;
    }

    public void setServerData(List<ChapterInfo> serverData) {
        this.serverData = serverData;
    }
}