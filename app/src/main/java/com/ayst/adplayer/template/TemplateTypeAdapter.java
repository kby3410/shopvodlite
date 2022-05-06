package com.ayst.adplayer.template;

import com.ayst.adplayer.data.BoxInfo;
import com.ayst.adplayer.data.HomeTemplate;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by shenhaibo on 2018/10/29.
 */

public class TemplateTypeAdapter extends TypeAdapter<HomeTemplate> {
    private static int sIdCnt = 0;
    private BoxInfoTypeAdapter mBoxInfoTypeAdapter;

    public TemplateTypeAdapter() {
        mBoxInfoTypeAdapter = new BoxInfoTypeAdapter();
    }

    @Override
    public void write(JsonWriter out, HomeTemplate template) throws IOException {
        out.beginObject();
        out.name("id").value(template.getId());
        out.name("name").value(template.getName());
        out.name("type").value(template.getType());
        if (null != template.getItems() && !template.getItems().isEmpty()) {
            out.name("items");
            out.beginArray();
            for (BoxInfo boxInfo : template.getItems()) {
                mBoxInfoTypeAdapter.write(out, boxInfo);
            }
            out.endArray();
        } else {
            out.name("items").nullValue();
        }
        out.endObject();
    }

    @Override
    public HomeTemplate read(JsonReader in) throws IOException {
        final HomeTemplate template = new HomeTemplate();

        in.beginObject();
        while (in.hasNext()) {
            switch (in.nextName()) {
                case "id":
                    long id = in.nextLong();
                    if (0 == id) {
                        template.setId(System.currentTimeMillis() + sIdCnt++);
                    } else {
                        template.setId(id);
                    }
                    break;
                case "name":
                    template.setName(in.nextString());
                    break;
                case "type":
                    template.setType(in.nextInt());
                    break;
                case "items":
                    in.beginArray();
                    List<BoxInfo> boxs = new ArrayList<>();
                    while (in.hasNext()) {
                        boxs.add(mBoxInfoTypeAdapter.read(in));
                    }
                    template.setItems(boxs);
                    in.endArray();
                    break;
                default:
                    break;
            }
        }
        in.endObject();

        return template;
    }
}
