package fr.funlab.andgws.service;

import android.os.Parcel;
import android.os.Parcelable;

public class Data implements Parcelable {

	   private long requestsCount;
	   private int connectionsCount;
	   
    public static final Parcelable.Creator<Data> CREATOR = new Parcelable.Creator<Data>() {

        public Data createFromParcel(Parcel in) {
            return new Data(in);
        }
 
        public Data[] newArray(int size) {
            return new Data[size];
        }
    };
 
    public Data(long requestsCount, int connectionsCount) {
        super();
        this.requestsCount = requestsCount;
        this.connectionsCount = connectionsCount ;
    }
  
    private Data(Parcel in) {
        readFromParcel(in);
    }
 
    public long getRequestsCount() {
        return this.requestsCount;
    }
    public int getConnectionsCount() {
        return this.connectionsCount;
    }

    public void readFromParcel(Parcel in) {
        this.requestsCount = in.readLong();
        this.connectionsCount = in.readInt();
    }
  
	@Override
	public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.requestsCount);		
        dest.writeInt(this.connectionsCount);		
	}

	public int describeContents() {
        return 0;
    }

}
