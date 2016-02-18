package sayden.autoupdater;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import java.awt.Dimension;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;

import javax.swing.JFrame;
import javax.swing.JProgressBar;


public class ApplicationUpdater extends JFrame{
	
    /**
	 * 
	 */
	private static final long serialVersionUID = -851907197857929462L;
	
	String updateurl;
    JProgressBar progress;

    public ApplicationUpdater(String url){
        updateurl = url;
    }
    
    void downloadLatestVersion(){
        URL url;
        try {
            url = new URL(updateurl + "Sayden.jar");
            HttpURLConnection hConnection = (HttpURLConnection) url
                    .openConnection();
            HttpURLConnection.setFollowRedirects(true);
            if (HttpURLConnection.HTTP_OK == hConnection.getResponseCode()) {
                InputStream in = hConnection.getInputStream();
                BufferedOutputStream out = new BufferedOutputStream(
                        new FileOutputStream("Sayden.jar"));
                int filesize = hConnection.getContentLength();
                progress.setMaximum(filesize);
                byte[] buffer = new byte[4096];
                int numRead;
                long numWritten = 0;
                while ((numRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, numRead);
                    numWritten += numRead;
                    System.out.println((double)numWritten/(double)filesize);
                    progress.setValue((int) numWritten);
                }
                if(filesize!=numWritten)
                    System.out.println("Wrote "+numWritten+" bytes, should have been "+filesize);
                else
                    System.out.println("Downloaded successfully.");
                out.close();
                in.close();
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }
	
    public void setRevision(int version){
    	BufferedWriter bw = null;
    	
        try {
            File version_file = new File("src/version.txt");

            bw = new BufferedWriter(new FileWriter(version_file));
            bw.write(version + "");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
            	bw.close();
            } catch (Exception e) {
            }
    	}
    }
    
	public int latestRevision(){
        URL url;
        try {
            url = new URL(updateurl + "version.txt");
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

	public void checkForUpdates() {
		int latestRevision = latestRevision();
		
		if(latestRevision > currentRevision()){
			
			this.setPreferredSize(new Dimension(300, 80));
			this.setSize(new Dimension(300, 80));
			this.setTitle("Actualizador");
			progress = new JProgressBar(0,100);
			progress.setValue(0);
			progress.setStringPainted(true);
			this.add(progress);
			this.setLocationRelativeTo(null);
			this.setVisible(true);
			this.requestFocus(true);
			
			downloadLatestVersion();
			setRevision(latestRevision);
			
		    try {
		        @SuppressWarnings("unused")
				Process foo = Runtime.getRuntime().exec("java -jar Sayden.jar final");
		    } catch (IOException e) {
		        e.printStackTrace();
		    }
		    
		    System.exit(0);
		}
	}
}
