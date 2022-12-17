package my.uum;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.*;

/**
 * This class is for manipulating the Telegram bot through API and username.
 * It also connects to the SQLite database to retrieve the data.
 *
 * @author Foo Roon Yi
 */
public class MyBot extends TelegramLongPollingBot {

    Connection conn;
    PreparedStatement statement;

    /**
     * This constructor is used to connect to the SQLite database.
     * If successfully connected, the system will print out that connection has been established.
     */
    public MyBot(){
        try{
            // db parameters
            String url = "jdbc:sqlite:C:/Users/User/IdeaProjects/assignment-2-FooRoonYi/src/main/java/my/asg2_database.db";
            // create a connection to the database
            conn = DriverManager.getConnection(url);

            System.out.println("Connection to SQLite has been established.");
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * This method is used for getting the username of the telegram bot.
     *
     * @return username of Telegram bot
     */
    @Override
    public String getBotUsername() {
        // TODO
        return "s277088_A221_bot";
    }

    /**
     * This method is used for getting the API of the Telegram bot.
     *
     * @return telegram bot API
     */
    @Override
    public String getBotToken() {
        // TODO
        return "5848704897:AAE6MDOms-sx9tRJIy2rSYXO3viNKsmaMv8";
    }

    /**
     * This method is for the users to select the commands in the telegram bot.
     * The commands are start, booking, cancel and list.
     * If there is any wrong format of commands or info entered, then a message will be prompted to the users.
     *
     * @param update Message updated in telegram bot
     */
    @Override
    public void onUpdateReceived(Update update) {
        Message sms = update.getMessage();
        String[] data = sms.getText().split(" ");

        if(update.hasMessage() && update.getMessage().hasText()){
            switch (data[0]){
                case "/start":
                    sendMessage(sms, "Hi!  Welcome to s277088 _ A221 _ bot." +
                            "\nFeel free to chat with me to book a meeting room." +
                            "\n\nWe operate as usual from Monday to Friday, 8am - 6pm & Saturday 8am - 1pm for meeting room booking." +
                            "\n\nOnly staff have the privilege to book the meeting room." +
                            "\n\n1. Click /booking to proceed to the booking section." +
                            "\n2. Click /cancel to proceed to the cancel booking section." +
                            "\n3. Click /list to proceed to the display booking meeting room section.");
                    break;

                case "/booking":
                    sendMessage(sms, "Kindly inform you that it is required to book meeting room before use it. Let's go with appointment-making!" +
                            "\n\nCan I get some information from you to make a booking?" +
                            "\nName: <Foo Roon Yi = FooRoonYi>" +
                            "\nI/C number: <001234-56-7890>" +
                            "\nStaff ID: <111122>" +
                            "\nMobile number: <012-3456789>" +
                            "\nEmail: <janiceyi02@gmail.com>" +
                            "\nPurpose of Booking: <conference> / <brainstorming> / <training session>" +
                            "\nBooking Date: 12/12/2022" +
                            "\nBooking Time: 10:17 AM" +
                            "\nBooking Room: [small / medium / large]" +
                            "\n\n##Please do not use any spacing in all the information! However, you may leave a spacing for each type of information.##" +
                            "\n\n<Example for message reply to book a meeting room: >" +
                            "\nFooRoonYi 001234-56-7890 111122 012-3456789 janiceyi02@gmail.com conference 15/12/2022 10:17AM small");
                    break;

                case "/cancel":
                    sendMessage(sms, "To cancel booking, you have to enter your i/c number:" +
                            "\n\n##Follow the format here:##" +
                            "\ncancel 001234-56-7890");
                    cancelBooking(sms);
                    break;

                case "/list":
                    displayMeetingRoomBookings(sms);
                    break;

                default:
                    if (data.length == 9) {
                        bookMeetingRoom(sms);
                    } else if (data.length == 2){
                        cancelBooking(sms);
                    } else {
                        sendMessage(sms, "I can't understand you! Please enter the message with the right format.");
                    }
                    break;
            }
        }
    }

    /**
     * This method is for the telegram bot to send message to the users.
     *
     * @param message The message that will be sent to the users.
     * @param info The text that the telegram bot would like to send to the users.
     */
    void sendMessage(Message message, String info){
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setReplyToMessageId(message.getMessageId());
        sendMessage.setText(info);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is for the users to book a meeting room.
     * All information entered by the users will be saved in the array and then into the database.
     *
     * @param message The message that will be sent to the users.
     */
    void bookMeetingRoom(Message message){
        String[] bookingDetails = message.getText().split("\\s+");
        String name = bookingDetails[0];
        String ICNO = bookingDetails[1];
        String staff_id = bookingDetails[2];
        String Mobile_TelNo = bookingDetails[3];
        String email =bookingDetails[4];
        String purpose_booking = bookingDetails[5];
        String booking_date = bookingDetails[6];
        String booking_time = bookingDetails[7];
        String Room_Description = bookingDetails[8];

        try {
            statement = conn.prepareStatement("INSERT INTO tbl_booking (name, ICNO, staff_id, Mobile_TelNo, email, purpose_booking, booking_date, booking_time, Room_Description) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
            statement.setString(1, name);
            statement.setString(2, ICNO);
            statement.setString(3, staff_id);
            statement.setString(4, Mobile_TelNo);
            statement.setString(5, email);
            statement.setString(6, purpose_booking);
            statement.setString(7, booking_date);
            statement.setString(8, booking_time);
            statement.setString(9, Room_Description);
            statement.executeUpdate();

            sendMessage(message, "Thank you for your information. Your booking has been recorded. Feel free to check it through /list.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is for deleting or cancelling the booking of the meeting room.
     * The users can delete their booking with their IC numbers.
     *
     * @param message The message that will be sent to the users.
     */
    void cancelBooking(Message message){
        String ICNO = message.getText().split("\\s+")[1];

        try {
            // Delete the booking from the database
            statement = conn.prepareStatement("DELETE FROM tbl_booking WHERE ICNO = ?");
            statement.setString(1, ICNO);
            statement.executeUpdate();

            sendMessage(message, "Your booking has been cancelled. Feel free to check it through /list.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is for manipulating the booking list of the meeting rooms.
     * The info will be shown by selecting from the database.
     *
     * @param message The message that will be sent to the users.
     */
    void displayMeetingRoomBookings(Message message){
        try {
            // Retrieve the bookings from the database
            statement = conn.prepareStatement("SELECT tbl_room.Room_Id, tbl_booking.staff_id, tbl_booking.name, tbl_booking.Mobile_TelNo, tbl_booking.email, tbl_booking.purpose_booking, tbl_room.Room_Description, tbl_room.Maximum_Capacity, tbl_booking.booking_date, tbl_booking.booking_time FROM tbl_booking INNER JOIN tbl_room ON tbl_booking.Room_Description = tbl_room.Room_Description;");
            ResultSet rs = statement.executeQuery();

            // Construct the message
            String list = "This is the list of users booking for the meeting room: \n";
            while (rs.next()) {
                list += "\nRoom ID: " + rs.getString("Room_Id") + "\nBooking by: " + rs.getString("name") + "\nStaff ID: " + rs.getString("staff_id") + "\nPhone Number: " + rs.getString("Mobile_TelNo") + "\nEmail: " + rs.getString("email")  + "\nPurpose of Booking: " + rs.getString("purpose_booking") + "\nRoom Description: " + rs.getString("Room_Description") + "\nRoom Maximum Capacity: " + rs.getString("Maximum_Capacity") + "\nBooking Date: " + rs.getString("booking_date") + "\nBooking Time: " + rs.getString("booking_time") + "\n\n";
            }

            // Send the message
            sendMessage(message, list);
            sendMessage(message, "You can delete your booking via /cancel. " +
                    "\nYou may leave the session and back to the main menu by /start.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
