package org.example;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;
import com.microsoft.playwright.*;

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

    try ( Playwright playwright = Playwright.create() ) {
      Browser browser = playwright.firefox().launch( new BrowserType.LaunchOptions().setHeadless(true) );
      BrowserContext context = browser.newContext();

      // Fetch the ECharts CDN script contents
      String echartsScriptUrl = "https://cdn.jsdelivr.net/npm/echarts@5.4.3/dist/echarts.min.js";
      String echartsScriptContents = new String(HttpClient.newHttpClient()
          .send(HttpRequest.newBuilder().uri(URI.create(echartsScriptUrl)).build(),
              HttpResponse.BodyHandlers.ofByteArray()).body());

      // Inject the ECharts script using addInitScript()
      context.addInitScript(echartsScriptContents);

      Page page = context.newPage();

      // Wait for the page to load or any other necessary actions
//      page.waitForTimeout(10000);

      String filename="exportPDF/src/main/java/resources/echartsBase64.js";

      Path pathToFile = Paths.get(filename);
//       Read the JavaScript file
      String jsCode = new String(java.nio.file.Files.readAllBytes(pathToFile.toAbsolutePath()));

      jsCode = jsCode.replace( "@height", "700" );
      jsCode = jsCode.replace( "@width", "1200" );

      // Execute the JavaScript function with arguments
//      Object result = page.evaluate("(() => { " + jsCode + " return getChartAsBase64(); })()");
//      Object result = page.evaluate(jsCode);
      JSHandle result = page.evaluateHandle(jsCode);
//      page.evaluateHandle(jsCode);
//
//      System.out.println("Result: " + result);

//      Map<String, Object> Largs = new HashMap<>();
//      Largs.put("height", "Hello");
//      Largs.put("width", "World");
//      JSHandle result = page.evaluateHandle(
//          "({height, width}) => { " + jsCode + " return myFunction(height, width); }", Largs);
//      System.out.println("Result: " + result);

//      page.waitForTimeout(3000);
// Take a screenshot of the entire page
//      page.screenshot(new Page.ScreenshotOptions()
//          .setPath(Paths.get("screenshot.png")).setFullPage(true));


      byte[] imageArray = decodeBase64ToImage( result.toString() );

     // Convert the byte array to an image file
      ByteArrayInputStream bis = new ByteArrayInputStream( imageArray );
      BufferedImage image = ImageIO.read( bis );
      ImageIO.write( image, "png", new File( "output.png" ) );
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
  public static void convertImageToPDF( String inputImagePath, String outputPdfPath ) {
    try {
//      BufferedImage image = ImageIO.read( new File( inputImagePath ) );
      Document document = new Document();
      PdfWriter.getInstance( document, new FileOutputStream( outputPdfPath ) );
      document.open();
      Image pdfImage = com.itextpdf.text.Image.getInstance( inputImagePath );
      document.add( pdfImage );
      document.close();
    } catch ( Exception e ) {
      e.printStackTrace();
    }
  }
  private static byte[] decodeBase64ToImage( String base64String ) {
    return Base64.getDecoder().decode( base64String );
  }
}
