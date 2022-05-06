package com.ayst.adplayer.data;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Administrator on 2018/3/28.
 */

public class PlayListInfo extends BaseInfo {
    private String title = "";
    private ArrayList<FileInfo> playlist = new ArrayList<>();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<FileInfo> getPlaylist() {
        return playlist;
    }

    public void setPlaylist(ArrayList<FileInfo> playlist) {
        this.playlist.addAll(playlist);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PlayListInfo that = (PlayListInfo) o;

        return this.id == that.id;
    }
}
