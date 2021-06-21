package ddwu.mobile.final_project;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;

public class CulartDetailXmlParser {

    private enum TagType {NONE, TITLE, STARTDATE, ENDDATE, PLACE, REALM, PRICE, CONTENTS, URL, TEL, IMGURL, PLACESEQ}

    private final static String ITEM_TAG = "perforInfo";
    private final static String TITLE_TAG = "title";
    private final static String STARTDATE_TAG = "startDate";
    private final static String ENDDATE_TAG = "endDate";
    private final static String PLACE_TAG = "place";
    private final static String REALM_TAG = "realmName";
    private final static String PRICE_TAG = "price";
    private final static String CONTENTS_TAG = "contents1";
    private final static String URL_TAG = "url";
    private final static String TEL_TAG = "phone";
    private final static String IMGURL_TAG = "imgUrl";
    private final static String PLACESEQ_TAG = "placeSeq";


    public CulartDetailXmlParser() {
    }

    public CulartDetailDTO parse(String xml) {
        CulartDetailDTO dto = null;
        CulartDetailXmlParser.TagType tagType = TagType.NONE;
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
                            dto = new CulartDetailDTO();
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
                        } else if (parser.getName().equals(PRICE_TAG)) {
                            if (dto != null) tagType = TagType.PRICE;
                        } else if (parser.getName().equals(CONTENTS_TAG)) {
                            if (dto != null) tagType = TagType.CONTENTS;
                        } else if (parser.getName().equals(URL_TAG)) {
                            if (dto != null) tagType = TagType.URL;
                        } else if (parser.getName().equals(TEL_TAG)) {
                            if (dto != null) tagType = TagType.TEL;
                        } else if (parser.getName().equals(IMGURL_TAG)) {
                            if (dto != null) tagType = TagType.IMGURL;
                        } else if (parser.getName().equals(PLACESEQ_TAG)) {
                            if (dto != null) tagType = TagType.PLACESEQ;
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        tagType = TagType.NONE;
                        break;
                    case XmlPullParser.TEXT:
                        switch (tagType) {
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
                            case PRICE:
                                dto.setPrice(parser.getText());
                                break;
                            case CONTENTS:
                                dto.setContents(parser.getText());
                                break;
                            case URL:
                                dto.setUrl(parser.getText());
                                break;
                            case TEL:
                                dto.setTel(parser.getText());
                                break;
                            case IMGURL:
                                dto.setImgUrl(parser.getText());
                                break;
                            case PLACESEQ:
                                dto.setPlaceSeq(parser.getText());
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
        return dto;
    }

}