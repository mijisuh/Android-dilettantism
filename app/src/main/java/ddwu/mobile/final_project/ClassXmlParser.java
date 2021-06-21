package ddwu.mobile.final_project;

import android.text.Html;
import android.text.Spanned;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;

public class ClassXmlParser {

    private enum TagType {NONE, TITLE, LINK, TUTOR, IMGURL}

    private final static String ITEM_TAG = "item";
    private final static String TITLE_TAG = "title";
    private final static String LINK_TAG = "link";
    private final static String TUTOR_TAG = "mallName";
    private final static String IMGURL_TAG = "image";

    public ClassXmlParser() {
    }

    public ArrayList<ClassDTO> parse(String xml) {
        ArrayList<ClassDTO> resultList = new ArrayList();
        ClassDTO dto = null;

        ClassXmlParser.TagType tagType = ClassXmlParser.TagType.NONE;
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(xml));

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.END_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        if (parser.getName().equals(ITEM_TAG)) {
                            dto = new ClassDTO();
                        } else if (parser.getName().equals(TITLE_TAG)) {
                            if (dto != null) tagType = ClassXmlParser.TagType.TITLE;
                        } else if (parser.getName().equals(LINK_TAG)) {
                            if (dto != null) tagType = ClassXmlParser.TagType.LINK;
                        } else if (parser.getName().equals(TUTOR_TAG)) {
                            if (dto != null) tagType = ClassXmlParser.TagType.TUTOR;
                        } else if (parser.getName().equals(IMGURL_TAG)) {
                            if (dto != null) tagType = ClassXmlParser.TagType.IMGURL;
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        tagType = ClassXmlParser.TagType.NONE;
                        if (parser.getName().equals(ITEM_TAG)) {
                            resultList.add(dto);
                            dto = null;
                        }
                        break;
                    case XmlPullParser.TEXT:
                        switch (tagType) {
                            case TITLE:
                                Spanned contents = Html.fromHtml(parser.getText());
                                dto.setTitle(contents.toString());
                                break;
                            case LINK:
                                dto.setLink(parser.getText());
                                break;
                            case TUTOR:
                                dto.setTutor(parser.getText());
                                break;
                            case IMGURL:
                                dto.setImg(parser.getText());
                                break;
                        }
                        tagType = ClassXmlParser.TagType.NONE;
                        break;
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultList;
    }
}
