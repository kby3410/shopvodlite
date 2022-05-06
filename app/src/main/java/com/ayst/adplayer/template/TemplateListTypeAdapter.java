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

public class TemplateListTypeAdapter extends TypeAdapter<List<HomeTemplate>> {
    private TemplateTypeAdapter mTemplateTypeAdapter;

    public TemplateListTypeAdapter() {
        mTemplateTypeAdapter = new TemplateTypeAdapter();
    }

    @Override
    public void write(JsonWriter out, List<HomeTemplate> templates) throws IOException {
        out.beginObject();
        if (null != templates && !templates.isEmpty()) {
            out.beginArray();
            for (HomeTemplate template : templates) {
                mTemplateTypeAdapter.write(out, template);
            }
            out.endArray();
        }
        out.endObject();
    }

    @Override
    public List<HomeTemplate> read(JsonReader in) throws IOException {
        List<HomeTemplate> templates = new ArrayList<>();

        in.beginArray();
        while (in.hasNext()) {
            templates.add(mTemplateTypeAdapter.read(in));
        }
        in.endArray();

        return templates;
    }
}
