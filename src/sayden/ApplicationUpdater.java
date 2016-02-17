package sayden;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ApplicationUpdater {
	
	private String github_url = "https://raw.githubusercontent.com/BBalderrabano/SimpleSayden/master/deploy";

	public int latestRevision(){
        URL url;
        
        try {
            url = new URL(github_url + "/version.txt");
            
            HttpURLConnection hConnection = (HttpURLConnection) url
                    .openConnection();
            
            HttpURLConnection.setFollowRedirects(true);
            
            if (HttpURLConnection.HTTP_OK == hConnection.getResponseCode()) {
                BufferedReader is = new BufferedReader(new
                        InputStreamReader(hConnection.getInputStream()));
                int rev = Integer.valueOf(is.readLine());
                return rev;
            }
            
        }catch(IOException e){
            e.printStackTrace();
        }
        
        return -1;
    }
	
	public int currentRevision(){
        BufferedReader is;
        try {
            is = new BufferedReader(
                    new InputStreamReader(ClassLoader.getSystemResource("version.txt").openStream()));
            int rev = Integer.valueOf(is.readLine());
            return rev;
        } catch(NullPointerException e){
        }catch (Exception e) {
            e.printStackTrace();
        }
        return 1<<31-1;
    }

	public void checkUpdate() {
		if(latestRevision() > currentRevision()){
			Updater up = new Updater("https://raw.githubusercontent.com/BBalderrabano/SimpleSayden/master/deploy/Sayden.jar");
	        up.downloadLatestVersion();
	        try {
	            @SuppressWarnings("unused")
				Process foo = Runtime.getRuntime().exec("java -jar Sayden.jar");
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        up.dispose();
		}
	}
}
