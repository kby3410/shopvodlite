package com.ayst.adplayer.template;

import android.util.Log;

import com.ayst.adplayer.data.BoxInfo;
import com.ayst.adplayer.data.ImageBoxData;
import com.ayst.adplayer.data.TextBoxData;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by shenhaibo on 2018/10/29.
 */

public class BoxInfoTypeAdapter extends TypeAdapter<BoxInfo> {

    @Override
    public void write(JsonWriter out, BoxInfo boxInfo) throws IOException {
        out.beginObject();
        out.name("id").value(boxInfo.getId());
        out.name("box_type").value(boxInfo.getType());
        out.name("pos_x").value(boxInfo.getPosX());
        out.name("pos_y").value(boxInfo.getPosY());
        out.name("width").value(boxInfo.getWidth());
        out.name("height").value(boxInfo.getHeight());
        if (null != boxInfo.getData()) {
            out.name("data");
            if (boxInfo.getType() == BoxType.IMAGE.ordinal()) {
                writeImageBoxData(out, (ImageBoxData) boxInfo.getData());
            } else if (boxInfo.getType() == BoxType.TEXT.ordinal()) {
                writeTextBoxData(out, (TextBoxData) boxInfo.getData());
            }
        } else {
            out.name("data").nullValue();
        }
        out.endObject();
    }

    @Override
    public BoxInfo read(JsonReader in) throws IOException {
        final BoxInfo boxInfo = new BoxInfo();

        in.beginObject();
        while (in.hasNext()) {
            switch (in.nextName()) {
                case "id":
                    boxInfo.setId(in.nextInt());
                    break;
                case "box_type":
                    boxInfo.setType(in.nextInt());
                    break;
                case "pos_x":
                    boxInfo.setPosX((float) in.nextDouble());
                    break;
                case "pos_y":
                    boxInfo.setPosY((float) in.nextDouble());
                    break;
                case "width":
                    boxInfo.setWidth((float) in.nextDouble());
                    break;
                case "height":
                    boxInfo.setHeight((float) in.nextDouble());
                    break;
                case "data":
                    if (boxInfo.getType() == BoxType.IMAGE.ordinal()) {
                        boxInfo.setData(readImageBoxData(in));
                    } else if (boxInfo.getType() == BoxType.TEXT.ordinal()) {
                        boxInfo.setData(readTextBoxData(in));
                    } else {
                        in.beginObject();
                        boxInfo.setData(null);
                        in.endObject();
                    }
                    break;
            }
        }
        in.endObject();

        return boxInfo;
    }

    private ImageBoxData readImageBoxData(JsonReader in) throws IOException {
        final ImageBoxData imageBoxData = new ImageBoxData();

        in.beginObject();
        while (in.hasNext()) {
            switch (in.nextName()) {
                case "images":
                    in.beginArray();
                    List<String> images = new ArrayList<>();
                    while (in.hasNext()) {
                        String filePath = in.nextString();
                        File file_image = new File(filePath);
                        
                        if(file_image.exists()) {
                            images.add(filePath);
                        }else{
                            Log.d("lily",filePath + "-----not exists-------" );
                        }
                    }
                    Log.d("lily", "-----images-------"  + images.size());
                    imageBoxData.setImages(images);
                    in.endArray();
                    break;
                case "interval":
                    imageBoxData.setInterval(in.nextInt());
                    break;
                case "effect":
                    imageBoxData.setEffect(in.nextString());
                    break;
            }
        }
        in.endObject();

        return imageBoxData;
    }

    private TextBoxData readTextBoxData(JsonReader in) throws IOException {
        final TextBoxData textBoxData = new TextBoxData();

        in.beginObject();
        while (in.hasNext()) {
            switch (in.nextName()) {
                case "size":
                    textBoxData.setSize(in.nextInt());
                    break;
                case "style":
                    textBoxData.setStyle(in.nextString());
                    break;
                case "text_color":
                    textBoxData.setTextColor(in.nextLong());
                    break;
                case "bg_color":
                    textBoxData.setBgColor(in.nextLong());
                    break;
                case "scroll_speed":
                    textBoxData.setScrollSpeed(in.nextInt());
                    break;
                case "text":
                    textBoxData.setText(in.nextString());
                    break;
            }
        }
        in.endObject();

        return textBoxData;
    }

    private void writeImageBoxData(JsonWriter out, ImageBoxData imageBoxData) throws IOException {
        out.beginObject();
        if (null != imageBoxData.getImages() && !imageBoxData.getImages().isEmpty()) {
            out.name("images");
            out.beginArray();
            for (String image : imageBoxData.getImages()) {
                out.value(image);
            }
            out.endArray();
        } else {
            out.name("images").nullValue();
        }

        out.name("interval").value(imageBoxData.getInterval());
        out.name("effect").value(imageBoxData.getEffect());
        out.endObject();
    }

    private void writeTextBoxData(JsonWriter out, TextBoxData textBoxData) throws IOException {
        out.beginObject();
        out.name("size").value(textBoxData.getSize());
        out.name("style").value(textBoxData.getStyle());
        out.name("text_color").value(textBoxData.getTextColor());
        out.name("bg_color").value(textBoxData.getBgColor());
        out.name("scroll_speed").value(textBoxData.getScrollSpeed());
        out.name("text").value(textBoxData.getText());
        out.endObject();
    }
}
