package com.example.aidlservice;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by LiuLei on 2017/11/28.
 */

public class NewMessage implements Parcelable {

    public String senderID;
    public String messageContent;
    public String receiverID;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(senderID);
        out.writeString(messageContent);
        out.writeString(receiverID);
    }

    public static final Creator<NewMessage> CREATOR = new Creator<NewMessage>() {

        @Override
        public NewMessage[] newArray(int size) {
            return new NewMessage[size];
        }

        @Override
        public NewMessage createFromParcel(Parcel in) {
            return new NewMessage(in);
        }
    };

    private NewMessage(Parcel in)
    {
        senderID = in.readString();
        messageContent = in.readString();
        receiverID = in.readString();
    }

    public NewMessage(String senderID, String messageContent, String receiverID)
    {
        this.senderID = senderID;
        this.messageContent = messageContent;
        this.receiverID = receiverID;
    }
}
