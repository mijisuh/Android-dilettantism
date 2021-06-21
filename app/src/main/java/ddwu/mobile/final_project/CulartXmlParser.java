package ddwu.mobile.final_project;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;

public class CulartXmlParser {

    private enum TagType { NONE, SEQ, TITLE, STARTDATE, ENDDATE, PLACE, REALM, AREA, THUMBNAIL }

    private final static String ITEM_TAG = "perforList";
    private final static String SEQ_TAG = "seq";
    private final static String TITLE_TAG = "title";
    private final static String STARTDATE_TAG = "startDate";
    private final static String ENDDATE_TAG = "endDate";
    private final static String PLACE_TAG = "place";
    private final static String REALM_TAG = "realmName";
    private final static String AREA_TAG = "area";
    private final static String THUMBNAIL_TAG = "thumbnail";

    public CulartXmlParser() {
    }

    public ArrayList<CulartDTO> parse(String xml) {
        ArrayList<CulartDTO> resultList = new ArrayList();
        CulartDTO dto = null;

        CulartXmlParser.TagType tagType = TagType.NONE;

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
                            dto = new CulartDTO();
                        } else if (parser.getName().equals(SEQ_TAG)) {
                            if (dto != null) tagType = TagType.SEQ;
                        } else if (parser.getName().equals(TITLE_TAG)) {
                            if (dto != null) tagType = TagType.TITLE;
                        } else if (parser.getName().equals(STARTDATE_TAG)) {
                            if (dto != null) tagType = TagType.STARTDATE;
                        } else if (parser.getName().equals(ENDDATE_TAG)) {
                            if (dto != null) tagType = TagType.ENDDATE;
                        } else if (parser.getName().equals(PLACE_TAG)) {
                            if (dto != null) tagType = TagType.PLACE;
                        } else if (parser.getName().equals(REALM_TAG)) {
                            if (dto != null) tagType = TagType.REALM;
                        } else if (parser.getName().equals(AREA_TAG)) {
                            if (dto != null) tagType = TagType.AREA;
                        } else if (parser.getName().equals(THUMBNAIL_TAG)) {
                            if (dto != null) tagType = TagType.THUMBNAIL;
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        tagType = TagType.NONE;
                        if (parser.getName().equals(ITEM_TAG)) {
                            resultList.add(dto);
                            dto = null;
                        }
                        break;
                    case XmlPullParser.TEXT:
                        switch(tagType) {
                            case SEQ:
                                dto.setSeq(parser.getText());
                                break;
                            case TITLE:
                                dto.setTitle(parser.getText());
                                break;
                            case STARTDATE:
                                dto.setStartDate(parser.getText());
                                break;
                            case ENDDATE:
                                dto.setEndDate(parser.getText());
                                break;
                            case PLACE:
                                dto.setPlace(parser.getText());
                                break;
                            case REALM:
                                dto.setRealm(parser.getText());
                                break;
                            case AREA:
                                dto.setArea(parser.getText());
                                break;
                            case THUMBNAIL:
                                dto.setThumbnail(parser.getText());
                                break;
                        }
                        tagType = TagType.NONE;
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