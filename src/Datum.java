//jsonschema2pojo (74 lines), 23 Sep. 2023, https://www.jsonschema2pojo.org/
import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Datum {
	//private List<Driver> driverThreads = new ArrayList<>();
	
	
	public Datum(String name, String address, Float lat1, Float long1,int drivers,  List<String> menu) {
	    this.name = name;
	    this.address = address;
	    this.latitude = lat1;
	    this.longitude = long1;
	    this.drivers = drivers;
	    this.menu = menu;
	}
@SerializedName("name")
@Expose
private String name;
@SerializedName("address")

private String address;
@SerializedName("latitude")
@Expose
private Float latitude;
@SerializedName("longitude")
@Expose
private Float longitude;
@SerializedName("drivers")
@Expose
private Integer drivers;
@SerializedName("menu")
@Expose
private List<String> menu;

public String getName() {
return name;
}

public void setName(String name) {
this.name = name;
}

public String getAddress() {
return address;
}

public void setAddress(String address) {
this.address = address;
}

public Float getLatitude() {
return latitude;
}

public void setLatitude(Float latitude) {
this.latitude = latitude;
}

public Float getLongitude() {
return longitude;
}

public void setLongitude(Float longitude) {
this.longitude = longitude;
}

public Integer getDrivers() {
return drivers;
}

public void setDrivers(Integer drivers) {
this.drivers = drivers;
}

public List<String> getMenu() {
return menu;
}

public void setMenu(List<String> menu) {
this.menu = menu;
}

//public void addDriver(Driver driver) {
//	driverThreads.add(driver);
//}


}

