package ddwu.mobile.final_project;

import java.io.Serializable;

public class CulartPlaceDTO implements Serializable {

    private long _id;
    private String seq;
    private String name;
    private String group;
    private String tel;

    public CulartPlaceDTO() {
    }

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public String getSeq() {
        return seq;
    }

    public void setSeq(String seq) {
        this.seq = seq;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

}