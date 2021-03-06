package sample;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Insets;
import java.awt.Desktop;
import java.util.zip.GZIPInputStream;


public class Main extends Application
{
    public static void main(String[] args)
    {
        launch(args);
    }

    private Text displayHoursOpenedText;

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(8);
        grid.setVgap(5);


        Button openLogDirectory = new Button("Open Folder");
        grid.add(openLogDirectory,0, 0);

        Text displayChosenDirectoryText = new Text();
        grid.add(displayChosenDirectoryText,1,0);

        Button findHoursOpenedButton = new Button("Find Hours Opened");
        grid.add(findHoursOpenedButton,0,1);

        displayHoursOpenedText = new Text("Hours Played: ");
        grid.add(displayHoursOpenedText,1,1);

        Button findServerHours = new Button("Find Hours On Servers");
        grid.add(findServerHours,0,2);

        Text displayServerHours = new Text("Hours Played: ");
        grid.add(displayServerHours,1,2);

        Button findMessagesSent = new Button("Find Messages Sent");
        grid.add(findMessagesSent,0,3);

        Text displayMessagesSent = new Text("Messages Sent: ");
        grid.add(displayMessagesSent,1,3);

        TextField minecraftUsernameField = new TextField();
        minecraftUsernameField.setPromptText("Minecraft Username");
        grid.add(minecraftUsernameField, 0, 4);

        Text info = new Text("Minecraft Log Analyzer - Version 0.1 - Made By Stephen5311 and Zodsmar");
        grid.add(info, 1,4);

        final DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Open Minecraft's Logs Folder");
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home") + "/Desktop"));
        /* hey 🅱️eter I wrote some code */
        ArrayList<String> sList = new ArrayList<String>();

        openLogDirectory.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                File directory = directoryChooser.showDialog(primaryStage);
                if (directory != null)
                {
                    displayChosenDirectoryText.setText(directory.getAbsolutePath());
                    //sb.delete(0, sb.length());
                    sList.clear();
                    File[] filesList = directory.listFiles();
                    int i = 0;
                    for (File file : filesList)
                    {
                        if (file.isFile() &&  file.getName().contains(".log.gz") || file.getName().contains(".log"))
                        {
                            //System.out.println(file.getName());
                            try
                            {
                                Reader r = readGZOrFile(file);
                                BufferedReader br = new BufferedReader(r);
                                StringBuffer sb = new StringBuffer();

                                String line;
                                while ((line = br.readLine()) != null)
                                {
                                    sb.append(line);
                                    sb.append("\n");
                                }
                                r.close();
                                sList.add(sb.toString());
                                //System.out.println(sb.toString());
                            }
                            catch (IOException e)
                            {
                                System.out.println(e);
                            }
                        }
                    }
                }
                else
                {
                    displayChosenDirectoryText.setText(null);
                }

            }
        });

        findHoursOpenedButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                DateFormat format = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);

                int realTotalTime = 0;
                for (String lines : sList) {
                    boolean first = true;
                    int last = -1;
                    int totalTime = 0;
                    for (String line : lines.split("\n")) {
                        if (line.length() < "[00:00:00]".length()) {
                            continue;
                        } else if (line.charAt(0) != '[') {
                            continue;
                        }

                        Calendar timestamp = Calendar.getInstance();
                        try {
                            timestamp.setTime(format.parse(line.substring(1, 1 + 6 + 2 + 1)));
                            //System.out.println(timestamp);
                        } catch (ParseException e) {
                            System.out.println(line);
                            continue;
                        }

                        if (first) {
                            first = false;
                        } else {
                            int tmp = calendarToSeconds(timestamp) - last;
                            if (tmp < 0) {
                                tmp += 24 * 60 * 60;
                            }
                            totalTime += tmp;
                            // this is new code ^
                        }
                        last = calendarToSeconds(timestamp);
                    }

                    //System.out.println(totalTime);
                    realTotalTime += totalTime;
                }
                displayHoursOpenedText.setText(String.format("Hours Played: %.2f hours", (realTotalTime / 3600.0)));
            }
        });


        primaryStage.setScene(new Scene(grid));
        primaryStage.show();
    }

    public static int calendarToSeconds(Calendar timestamp) {
        int ret = (timestamp.get(Calendar.HOUR) * 60 + timestamp.get(Calendar.MINUTE)) * 60 + timestamp.get(Calendar.SECOND);
        //System.out.println(ret);
        return ret;
    }
    
    public Reader readGZOrFile(File file)
    {
        Reader reader = null;
        try
        {
            if (file.getName().contains(".log.gz"))
            {
                InputStream fileStream = new FileInputStream(file);
                InputStream gzipStream = new GZIPInputStream(fileStream);
                reader = new InputStreamReader(gzipStream, "UTF-8");
               
            }
            else
            {
                reader = new FileReader(file);
            }
        }
        catch(IOException e)
        {
            System.out.println(e);
        }
        return reader;
    }
}
