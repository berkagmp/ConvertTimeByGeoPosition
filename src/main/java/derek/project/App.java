package derek.project;

import static java.lang.System.out;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.function.BiFunction;

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
		String filename = "data.csv";
		StringBuilder output = new StringBuilder();
		
		BiFunction<String, String, String> f_timezone = (lat, lng) -> {
			String url = "http://api.geonames.org/timezoneJSON?lat=" + lat + "&lng=" + lng + "&username="; //api_key

			RestTemplate restTemplate = new RestTemplate();
			String json = restTemplate.getForObject(url, String.class);

			Gson gson = new Gson();
			Map<String, String> result = gson.fromJson(json, new TypeToken<Map<String, String>>() {
			}.getType());

			return result.get("timezoneId");
		};
		
		BiFunction<String, String, String> f_localTime = (str1, str2) -> {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			LocalDateTime currentDT = LocalDateTime.parse(str1, formatter);
			ZonedDateTime zonedDateTime = ZonedDateTime.of(currentDT, ZoneId.of("UTC"));
			
			formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
			
			zonedDateTime = zonedDateTime.withZoneSameInstant(ZoneId.of(str2));
			
			return formatter.format(zonedDateTime);
		};

		try (BufferedReader br = new BufferedReader(
				new FileReader(new ClassPathResource(filename).getFile().getAbsolutePath()))) {
			while ((line = br.readLine()) != null) {
				String[] arr = line.split(identifier);

				for (int i = 0; i < arr.length; i++) {
					output.append(arr[i])
							.append(identifier);
				}

				String timezone = f_timezone.apply(arr[1], arr[2]);
				output.append(timezone)
						.append(identifier)
						.append(f_localTime.apply(arr[0], timezone));
				
				out.println(output);
				output.setLength(0);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
