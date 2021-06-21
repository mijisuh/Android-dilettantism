package ddwu.mobile.final_project;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;

public class CulartPlaceDetailXmlParser {

    private enum TagType { NONE, NAME, ADDRESS, OPENDATE, TEL, HOMEURL, IMAGE, LAT, LNG };

    private final static String ITEM_TAG = "placeInfo";
    private final static String NAME_TAG = "culName";
    private final static String ADDRESS_TAG = "culAddr";
    private final static String OPENDATE_TAG = "culOpenDay";
    private final static String TEL_TAG = "culTel";
    private final static String HOMEURL_TAG = "culHomeUrl";
    private final static String IMAGE_TAG = "culViewImg1";
    private final static String LAT_TAG = "gpsY";
    private final static String LNG_TAG = "gpsX";

    public CulartPlaceDetailXmlParser() {
    }

    public CulartPlaceDetailDTO parse(String xml) {
        CulartPlaceDetailDTO dto = null;

        CulartPlaceDetailXmlParser.TagType tagType = CulartPlaceDetailXmlParser.TagType.NONE;

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
                            dto = new CulartPlaceDetailDTO();
                        } else if (parser.getName().equals(NAME_TAG)) {
                            if (dto != null) tagType = CulartPlaceDetailXmlParser.TagType.NAME;
                        } else if (parser.getName().equals(ADDRESS_TAG)) {
                            if (dto != null) tagType = CulartPlaceDetailXmlParser.TagType.ADDRESS;
                        } else if (parser.getName().equals(OPENDATE_TAG)) {
                            if (dto != null) tagType = CulartPlaceDetailXmlParser.TagType.OPENDATE;
                        } else if (parser.getName().equals(TEL_TAG)) {
                            if (dto != null) tagType = CulartPlaceDetailXmlParser.TagType.TEL;
                        } else if (parser.getName().equals(HOMEURL_TAG)) {
                            if (dto != null) tagType = CulartPlaceDetailXmlParser.TagType.HOMEURL;
                        } else if (parser.getName().equals(IMAGE_TAG)) {
                            if (dto != null) tagType = CulartPlaceDetailXmlParser.TagType.IMAGE;
                        } else if (parser.getName().equals(LAT_TAG)) {
                            if (dto != null) tagType = CulartPlaceDetailXmlParser.TagType.LAT;
                        } else if (parser.getName().equals(LNG_TAG)) {
                            if (dto != null) tagType = CulartPlaceDetailXmlParser.TagType.LNG;
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        tagType = TagType.NONE;
                        break;
                    case XmlPullParser.TEXT:
                        switch(tagType) {
                            case NAME:
                                dto.setName(parser.getText());
                                break;
                            case ADDRESS:
                                dto.setAddress(parser.getText());
                                break;
                            case OPENDATE:
                                dto.setOpenDate(parser.getText());
                                break;
                            case TEL:
                                dto.setTel(parser.getText());
                                break;
                            case HOMEURL:
                                dto.setHomeUrl(parser.getText());
                                break;
                            case IMAGE:
                                dto.setImageLink(parser.getText());
                                break;
                            case LAT:
                                dto.setLatitude(Double.parseDouble(parser.getText()));
                                break;
                            case LNG:
                                dto.setLongitude(Double.parseDouble(parser.getText()));
                                break;
                        }
                        tagType = CulartPlaceDetailXmlParser.TagType.NONE;
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