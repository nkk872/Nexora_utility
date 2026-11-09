/*
 * Copyright (c) 2012-2016 Arne Schwabe
 * This API parcelable is exempted from the OpenVPN for Android GPLv2 engine license.
 * The remoteExample API interface is distributed under Apache License 2.0.
 * See docs/THIRD_PARTY_NOTICES.md.
 */
package de.blinkt.openvpn.api;

import android.os.Parcel;
import android.os.Parcelable;

/** Parcelable profile metadata used by the OpenVPN for Android remote API. */
public class APIVpnProfile implements Parcelable {
    public final String mUUID;
    public final String mName;
    public final boolean mUserEditable;

    public APIVpnProfile(Parcel in) {
        mUUID = in.readString();
        mName = in.readString();
        mUserEditable = in.readInt() != 0;
    }

    public APIVpnProfile(String uuidString, String name, boolean userEditable, String profileCreator) {
        mUUID = uuidString;
        mName = name;
        mUserEditable = userEditable;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mUUID);
        dest.writeString(mName);
        dest.writeInt(mUserEditable ? 0 : 1);
    }

    public static final Creator<APIVpnProfile> CREATOR = new Creator<APIVpnProfile>() {
        @Override public APIVpnProfile createFromParcel(Parcel in) { return new APIVpnProfile(in); }
        @Override public APIVpnProfile[] newArray(int size) { return new APIVpnProfile[size]; }
    };
}
