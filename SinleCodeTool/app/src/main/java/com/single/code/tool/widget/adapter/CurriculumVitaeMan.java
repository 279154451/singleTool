package com.single.code.tool.widget.adapter;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 企业通讯录Bean
 */
public class CurriculumVitaeMan {
    public static final int TYPE_GROUP = 1;
    public static final int TYPE_USER = 2;

    public static class CurriculumVitaeInfo implements Parcelable {
        public int id;
        public String no;
        public String name;
        public String parentNo;
        public String sId;
        public String pId;
        public int type;
        public String orgName;
        public String number;

        public CurriculumVitaeInfo() {

        }



        CurriculumVitaeInfo(Parcel p) {
            id = p.readInt();
            no = p.readString();
            name = p.readString();
            parentNo = p.readString();
            sId = p.readString();
            pId = p.readString();
            type = p.readInt();
            orgName = p.readString();
            number = p.readString();
        }


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel p, int flags) {
            p.writeInt(id);
            p.writeString(no);
            p.writeString(name);
            p.writeString(parentNo);
            p.writeString(sId);
            p.writeString(pId);
            p.writeInt(type);
            p.writeString(orgName);
            p.writeString(number);
        }

        public static final Creator<CurriculumVitaeInfo> CREATOR = new Creator<CurriculumVitaeInfo>() {

            @Override
            public CurriculumVitaeInfo createFromParcel(Parcel source) {
                return new CurriculumVitaeInfo(source);
            }

            @Override
            public CurriculumVitaeInfo[] newArray(int size) {
                return new CurriculumVitaeInfo[size];
            }
        };

        public String getNo() {
            return no;
        }

        public void setNo(String no) {
            this.no = no;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getParentNo() {
            return parentNo;
        }

        public void setParentNo(String parentNo) {
            this.parentNo = parentNo;
        }

        public String getsId() {
            return sId;
        }

        public void setsId(String sId) {
            this.sId = sId;
        }

        public String getpId() {
            return pId;
        }

        public void setpId(String pId) {
            this.pId = pId;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getOrgName() {
            return orgName;
        }

        public void setOrgName(String orgName) {
            this.orgName = orgName;
        }

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }
    }
}
