package ddwu.mobile.final_project;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;

public class CulartPlaceXmlParser {

    private enum TagType { NONE, SEQ, NAME, GROUP, TEL };

    private final static String ITEM_TAG = "placeList";
    private final static String SEQ_TAG = "seq";
    private final static String NAME_TAG = "culName";
    private final static String GROUP_TAG = "culGrpName";
    private final static String TEL_TAG = "culTel";

    public CulartPlaceXmlParser() {
    }

    public ArrayList<CulartPlaceDTO> parse(String xml) {
        ArrayList<CulartPlaceDTO> resultList = new ArrayList();
        CulartPlaceDTO dto = null;

        CulartPlaceXmlParser.TagType tagType = TagType.NONE;

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
                            dto = new CulartPlaceDTO();
                        } else if (parser.getName().equals(SEQ_TAG)) {
                            if (dto != null) tagType = TagType.SEQ;
                        } else if (parser.getName().equals(NAME_TAG)) {
                            if (dto != null) tagType = TagType.NAME;
                        } else if (parser.getName().equals(GROUP_TAG)) {
                            if (dto != null) tagType = TagType.GROUP;
                        } else if (parser.getName().equals(TEL_TAG)) {
                            if (dto != null) tagType = TagType.TEL;
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
                            case NAME:
                                dto.setName(parser.getText());
                                break;
                            case GROUP:
                                dto.setGroup(parser.getText());
                                break;
                            case TEL:
                                dto.setTel(parser.getText());
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