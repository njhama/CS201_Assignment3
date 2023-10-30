import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class YelpAPI {

    private static final String API_KEY = "Q458ccQJboD9mFLYsgu955VJ9Ogix8riAHXDKRfyFKSSju_Vjar_3xVpay8DYO0I5oiOJAEI3GaHLmpAIwTISxWKBkJs57gQC9QAmFGLVJj3kgkWpUyFXT0zC6YxZXYx"; // Replace with your Yelp API Key
    private static final String BASE_URL = "https://api.yelp.com/v3/businesses/search?term=";

    public static Coordinate getRestaurantCoordinates(String restaurantName, Coordinate home) throws Exception {
    	restaurantName = restaurantName.replace(" ", "+");
        URL url = new URL(BASE_URL + restaurantName + "&latitude=" + home.getLatitude() + "&longitude=" + home.getLongitude());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "Bearer " + API_KEY);

        Coordinate coordinate = null;

        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            connection.disconnect();

            // Extracting coordinates from the response using basic string manipulation
            String responseBody = content.toString();
            int startLatIndex = responseBody.indexOf("\"latitude\":") + 11;
            int endLatIndex = responseBody.indexOf(",", startLatIndex);
            int startLongIndex = responseBody.indexOf("\"longitude\":") + 12;
            int endLongIndex = responseBody.indexOf("}", startLongIndex);

            double latitude = Double.parseDouble(responseBody.substring(startLatIndex, endLatIndex));
            double longitude = Double.parseDouble(responseBody.substring(startLongIndex, endLongIndex));
            coordinate = new Coordinate(latitude, longitude);
        } catch (Exception e) {
            System.out.println(e);
        }

        return coordinate;
    }

}
