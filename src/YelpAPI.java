import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class YelpAPI {

    private static final String API_KEY = "Q458ccQJboD9mFLYsgu955VJ9Ogix8riAHXDKRfyFKSSju_Vjar_3xVpay8DYO0I5oiOJAEI3GaHLmpAIwTISxWKBkJs57gQC9QAmFGLVJj3kgkWpUyFXT0zC6YxZXYx"; // Replace with your Yelp API Key
    private static final String BASE_URL = "https://api.yelp.com/v3/businesses/search?term=";

    public static String[] getRestaurantCoordinates(String restaurantName) throws Exception {
        String[] coordinates = new String[2]; // [latitude, longitude]
        URL url = new URL(BASE_URL + restaurantName + "&location=Los+Angeles");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "Bearer " + API_KEY);

        // Get the response
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

            coordinates[0] = responseBody.substring(startLatIndex, endLatIndex);
            coordinates[1] = responseBody.substring(startLongIndex, endLongIndex);
        }
        catch(Exception e) {
        	System.out.println(e);
        }

        

        return coordinates;
    }

    public static void main(String[] args) {
        try {
            String restaurantName = "Momota"; // Replace with any other restaurant name
            String[] coords = getRestaurantCoordinates(restaurantName);
            System.out.println("Latitude: " + coords[0]);
            System.out.println("Longitude: " + coords[1]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
