package derek.project;

import static java.lang.System.out;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

import org.springframework.core.io.ClassPathResource;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/*
 * Author: Derek Park
 * */
public class App {
	public static void main(String[] args) {
		String line = "";
		String identifier = ",";
		String timezone = "";
		String output = "";
		String filename = "data.csv";

		try (BufferedReader br = new BufferedReader(
				new FileReader(new ClassPathResource(filename).getFile().getAbsolutePath()))) {
			while ((line = br.readLine()) != null) {
				String[] arr = line.split(identifier);
				// out.println(Arrays.asList(arr).toString());

				for (int i = 0; i < arr.length; i++) {
					output += arr[i] + identifier;
				}

				timezone = getTimezone(arr[1], arr[2]);
				output += getTimezone(arr[1], arr[2]) + identifier + getLocalisedDatetime(arr[0], timezone);

				out.println(output);
				output = "";
			}
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
	}

	public static String getLocalisedDatetime(String datetime, String timezone) throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
		Date date = formatter.parse(datetime);
		String dTime = formatter.format(date);

		formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		formatter.setTimeZone(TimeZone.getTimeZone(timezone));
		dTime = formatter.format(date);

		return dTime;
	}

	public static String getTimezone(String lat, String lng) {
		String url = "http://api.geonames.org/timezoneJSON?lat=" + lat + "&lng=" + lng + "&username="; //api_key

		RestTemplate restTemplate = new RestTemplate();
		String json = restTemplate.getForObject(url, String.class);

		Gson gson = new Gson();
		Map<String, String> result = gson.fromJson(json, new TypeToken<Map<String, String>>() {
		}.getType());

		return result.get("timezoneId");
	}
}
