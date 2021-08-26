package function;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import com.google.common.hash.Hashing;
import com.google.common.io.Resources;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Main implements RequestHandler<Map<String, Object>, Object> {
    private static final String APPLICATION_NAME = "Google Calendar API Java Quickstart";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = System.getenv("CREDENTIALS_PATH");

    private static Calendar CALENDAR;

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    /**
     * handleRequest is the function called by AWS Lambda. It serves the same function as public static main() normally would
     * <p>
     * It sets up the calendar and hands down the inputMap to the DataMaster for processing
     *
     * @param inputMap the Json Object as Map that has been given to Lambda by Ley
     * @param context  context is important... for something...
     * @return a Json Object in form of a class structure that contains the response lex will give the user
     */
    @Override
    public Object handleRequest(Map<String, Object> inputMap, Context context) {
        System.out.println(inputMap);
        System.out.println();
        System.out.println();

        try {
            CALENDAR = setupCalendar();
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }

        System.out.println(inputMap);
        LexResponse lexResponse = new LexResponse().initiate();

        DataMaster dataMaster = new DataMaster(inputMap);

        String responseMessage = dataMaster.getResponse();

        lexResponse.getDialogAction().getMessage().setContent(responseMessage);

        switch (dataMaster.getIntent()) {
            case CREATE_EVENT:
                CreateEvent createEvent = new CreateEvent(CALENDAR, dataMaster);
                lexResponse.getDialogAction().getMessage().setContent(createEvent.getResponse());
                break;
            case DELETE_EVENT:
                //TODO...
                break;
            case EDIT_EVENT:
                //TODO...
                break;
            case LIST_EVENTS:
                ListEvents listEvents = new ListEvents(CALENDAR, dataMaster);
                lexResponse.getDialogAction().getMessage().setContent(listEvents.getResponse());
                break;
            default:
                break;
        }

        return lexResponse;
    }

    /**
     * Test the project when only building it into a jar and not uploading it to AWS lambda
     */
    public static void main(String... args) throws IOException, GeneralSecurityException {
        System.out.println("Hello World");


        CALENDAR = setupCalendar();
        listEvents();
    }

    /**
     * Creates an authorized Credential object.
     *
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {

        // Load client secrets.
        InputStream in = Main.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }

        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.

        URL storedCredential = Resources.getResource("StoredCredential");

        byte[] credentials = Resources.toByteArray(storedCredential);
        System.out.println(Hashing.sha256().hashBytes(credentials));
        Path OUTPUT_PATH = Path.of(TOKENS_DIRECTORY_PATH + "/StoredCredential");
        Files.createDirectories(Path.of(TOKENS_DIRECTORY_PATH));

        if (!Files.exists(OUTPUT_PATH)) {
            Files.createFile(OUTPUT_PATH);
        }
        Files.write(OUTPUT_PATH, credentials, StandardOpenOption.WRITE);
        System.out.println(Hashing.sha256().hashBytes(Files.readAllBytes(OUTPUT_PATH)));

        FileDataStoreFactory dataStoreFactory = new FileDataStoreFactory(new File(TOKENS_DIRECTORY_PATH));

        System.out.println(TOKENS_DIRECTORY_PATH);
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(dataStoreFactory)
                .setAccessType("offline")
                .build();

        System.out.println(flow.getScopes());
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    /**
     * sets up the calendar by asking the user for consent to access personal data.
     * Token is stored and used later
     *
     * @return the connected calendar
     */
    private static Calendar setupCalendar() throws GeneralSecurityException, IOException {
        // Build a new authorized API client calendar.
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        return new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    /**
     * lists the next 10 events from the connected google calendar
     * <p>
     * example from google documentation
     */
    private static void listEvents() throws IOException {
        // List the next 10 events from the primary calendar.
        DateTime now = new DateTime(System.currentTimeMillis());

        System.out.println(CALENDAR == null);

        Events events = CALENDAR.events().list("primary")
                .setMaxResults(10)
                .setTimeMin(now)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();
        List<Event> items = events.getItems();
        if (items.isEmpty()) {
            System.out.println("No upcoming events found.");
        } else {
            System.out.println("Upcoming events");
            for (Event event : items) {
                DateTime start = event.getStart().getDateTime();
                if (start == null) {
                    start = event.getStart().getDate();
                }
                System.out.printf("%s (%s)\n", event.getSummary(), start);
            }
        }
    }
}