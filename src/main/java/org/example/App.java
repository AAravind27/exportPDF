package org.example;

//import com.itextpdf.text.Document;
//import com.itextpdf.text.Image;
//import com.itextpdf.text.pdf.PdfWriter;
import com.microsoft.playwright.*;
import com.microsoft.playwright.impl.driver.Driver;
import com.microsoft.playwright.options.LoadState;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class App {
  public static void main( String[] args ) throws IOException {

//    String BROWSER_PATH = "C:\\Program Files\\Google\\Chrome\\Application\\Chrome.exe";
//    System.getenv().forEach((k, v) -> {
//      System.out.println(k + ":" + v);
//    });

    Playwright.CreateOptions options = new Playwright.CreateOptions();
    Map<String, String> env = new HashMap<>();
    env.put( "PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD", "true" );
    options.setEnv( env );
    installFirefox();

    try ( Playwright playwright = Playwright.create(options) ) {
      BrowserType chromium = playwright.firefox();
      Browser browser = chromium.launch(new BrowserType.LaunchOptions()
          .setHeadless(false));
//          .setExecutablePath(Paths.get(BROWSER_PATH)));
      BrowserContext context = browser.newContext();

      // Fetch the ECharts CDN script contents
      String echartsScriptUrl = "https://cdn.jsdelivr.net/npm/echarts@5.4.3/dist/echarts.min.js";
      String echartsScriptContents = new String(HttpClient.newHttpClient()
          .send(HttpRequest.newBuilder().uri(URI.create(echartsScriptUrl)).build(),
              HttpResponse.BodyHandlers.ofByteArray()).body());

      // Inject the ECharts script using addInitScript()
      context.addInitScript(echartsScriptContents);

      Page page = context.newPage();

      String filename="exportPDF/src/main/java/resources/echartsBase64.js";

      Path pathToFile = Paths.get(filename);
//       Read the JavaScript file
      String jsCode = new String(java.nio.file.Files.readAllBytes(pathToFile.toAbsolutePath()));

      jsCode = jsCode.replace( "@height", "700" );
      jsCode = jsCode.replace( "@width", "1200" );

      JSHandle result = page.evaluateHandle(jsCode);
      page.screenshot( new Page.ScreenshotOptions().setPath( Paths.get( "screenshot.png" ) ).setFullPage( true ) );

//      byte[] imageArray = decodeBase64ToImage( result.toString() );
//
//     // Convert the byte array to an image file
//      ByteArrayInputStream bis = new ByteArrayInputStream( imageArray );
//      BufferedImage image = ImageIO.read( bis );
//      ImageIO.write( image, "png", new File( "output.png" ) );
     // Optionally, convert the image to a PDF
//      convertImageToPDF( "output.png", "output.pdf" );

      // Close the browser
      result.dispose();
      context.close();
      browser.close();
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
//  public static void convertImageToPDF( String inputImagePath, String outputPdfPath ) {
//    try {
////      BufferedImage image = ImageIO.read( new File( inputImagePath ) );
//      Document document = new Document();
//      PdfWriter.getInstance( document, new FileOutputStream( outputPdfPath ) );
//      document.open();
//      Image pdfImage = com.itextpdf.text.Image.getInstance( inputImagePath );
//      document.add( pdfImage );
//      document.close();
//    } catch ( Exception e ) {
//      e.printStackTrace();
//    }
//  }
  private static byte[] decodeBase64ToImage( String base64String ) {
    return Base64.getDecoder().decode( base64String );
  }

  public static void installFirefox() {
    Driver driver = Driver.createAndInstall(Collections.emptyMap(), false);
    ProcessBuilder pb = driver.createProcessBuilder();
    pb.command().add("install");
    pb.command().add("firefox");
    pb.redirectError(ProcessBuilder.Redirect.INHERIT);
    pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);

    try {
      Process p = pb.start();

      // Wait for the process to complete and get the exit code
      int exitCode = p.waitFor();
      System.out.println("Process exited with code: " + exitCode);

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
