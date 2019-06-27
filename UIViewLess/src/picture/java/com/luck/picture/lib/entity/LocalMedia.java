package com.luck.picture.lib.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;

/**
 * author：luck
 * project：PictureSelector
 * package：com.luck.picture.lib.entity
 * describe：for PictureSelector media entity.
 * email：893855882@qq.com
 * data：2017/5/24
 */

public class LocalMedia implements Parcelable {
    public static final Parcelable.Creator<LocalMedia> CREATOR = new Parcelable.Creator<LocalMedia>() {
        @Override
        public LocalMedia createFromParcel(Parcel source) {
            return new LocalMedia(source);
        }

        @Override
        public LocalMedia[] newArray(int size) {
            return new LocalMedia[size];
        }
    };
    public int position;
    private String path;
    private String compressPath;
    private String cutPath;
    private long duration;
    private boolean isChecked;
    private boolean isCut;
    private int num;
    /**
     * @see PictureConfig#TYPE_ALL
     * @see PictureConfig#TYPE_AUDIO
     * @see PictureConfig#TYPE_IMAGE
     * @see PictureConfig#TYPE_VIDEO
     */

    /**
     * 媒体选择时的类型
     */
    private int mimeType = PictureConfig.TYPE_IMAGE;
    /**
     * 媒体返回的类型
     */
    private String pictureType = "image/jpeg";
    private boolean compressed;

    //angcyo
    private int width;
    private int height;
    /**
     * 1558921509 秒
     */
    private long modifyTime;
    /**
     * 1558921509 秒
     */
    private long addTime;

    public LocalMedia() {

    }

    public LocalMedia(String path) {
        this.path = path;
    }

    /**
     * @param mimeType
     * @see PictureConfig#TYPE_ALL
     * @see PictureConfig#TYPE_AUDIO
     * @see PictureConfig#TYPE_IMAGE
     * @see PictureConfig#TYPE_VIDEO
     */
    public LocalMedia(String path, int mimeType) {
        this.path = path;
        this.mimeType = mimeType;
    }

    /**
     * @param pictureType image/jpeg
     */
    public LocalMedia(String path, long duration, int mimeType, String pictureType) {
        this.path = path;
        this.duration = duration;
        this.mimeType = mimeType;
        this.pictureType = pictureType;
    }

    public LocalMedia(String path, long duration, int mimeType, String pictureType, int width, int height) {
        this.path = path;
        this.duration = duration;
        this.mimeType = mimeType;
        this.pictureType = pictureType;
        this.width = width;
        this.height = height;
    }

    public LocalMedia(String path, long duration, int mimeType, String pictureType,
                      int width, int height, long modifyTime, long addTime) {
        this.path = path;
        this.duration = duration;
        this.mimeType = mimeType;
        this.pictureType = pictureType;
        this.width = width;
        this.height = height;
        this.modifyTime = modifyTime;
        this.addTime = addTime;
    }

    public LocalMedia(String path, long duration,
                      boolean isChecked, int position, int num, int mimeType) {
        this.path = path;
        this.duration = duration;
        this.isChecked = isChecked;
        this.position = position;
        this.num = num;
        this.mimeType = mimeType;
    }

    protected LocalMedia(Parcel in) {
        this.position = in.readInt();
        this.path = in.readString();
        this.compressPath = in.readString();
        this.cutPath = in.readString();
        this.duration = in.readLong();
        this.isChecked = in.readByte() != 0;
        this.isCut = in.readByte() != 0;
        this.num = in.readInt();
        this.mimeType = in.readInt();
        this.pictureType = in.readString();
        this.compressed = in.readByte() != 0;
        this.width = in.readInt();
        this.height = in.readInt();
        this.modifyTime = in.readLong();
        this.addTime = in.readLong();
    }

    public String getPictureType() {
        if (TextUtils.isEmpty(pictureType)) {
            pictureType = "image/jpeg";
        }
        return pictureType;
    }

    public void setPictureType(String pictureType) {
        this.pictureType = pictureType;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getCompressPath() {
        return compressPath;
    }

    public void setCompressPath(String compressPath) {
        this.compressPath = compressPath;
    }

    public String getCutPath() {
        return cutPath;
    }

    public void setCutPath(String cutPath) {
        this.cutPath = cutPath;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public boolean isCut() {
        return isCut;
    }

    public void setCut(boolean cut) {
        isCut = cut;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getMimeType() {
        return mimeType;
    }

    public void setMimeType(int mimeType) {
        this.mimeType = mimeType;
    }

    public String getSelectorType() {
        switch (mimeType) {
            case PictureConfig.TYPE_ALL:
                return "All";
            case PictureConfig.TYPE_IMAGE:
                return "Image";
            case PictureConfig.TYPE_VIDEO:
                return "Video";
            case PictureConfig.TYPE_AUDIO:
                return "Audio";
            default:
                return "";
        }
    }

    public boolean isImageType() {
        return mimeType == PictureMimeType.ofImage() ||
                getPictureType().toLowerCase().startsWith("image");
    }

    public boolean isVideoType() {
        return mimeType == PictureMimeType.ofVideo() ||
                getPictureType().toLowerCase().startsWith("video");
    }

    public boolean isAudioType() {
        return mimeType == PictureMimeType.ofAudio() ||
                getPictureType().toLowerCase().startsWith("audio");
    }

    public boolean isCompressed() {
        return compressed;
    }

    public void setCompressed(boolean compressed) {
        this.compressed = compressed;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * 返回最终的文件路径
     */
    public String getFilePath() {
        if (isCompressed()) {
            return getCompressPath();
        }
        if (isCut()) {
            return getCutPath();
        }
        return getPath();
    }

    public String getLoadUrl() {
        return getFilePath();
    }

    /**
     * 返回无有效的展示地址
     */
    public boolean isUrlEmpty() {
        return TextUtils.isEmpty(getLoadUrl());
    }

    public long getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(long modifyTime) {
        this.modifyTime = modifyTime;
    }

    public long getAddTime() {
        return addTime;
    }

    public void setAddTime(long addTime) {
        this.addTime = addTime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.position);
        dest.writeString(this.path);
        dest.writeString(this.compressPath);
        dest.writeString(this.cutPath);
        dest.writeLong(this.duration);
        dest.writeByte(this.isChecked ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isCut ? (byte) 1 : (byte) 0);
        dest.writeInt(this.num);
        dest.writeInt(this.mimeType);
        dest.writeString(this.pictureType);
        dest.writeByte(this.compressed ? (byte) 1 : (byte) 0);
        dest.writeInt(this.width);
        dest.writeInt(this.height);
        dest.writeLong(this.modifyTime);
        dest.writeLong(this.addTime);
    }
}
