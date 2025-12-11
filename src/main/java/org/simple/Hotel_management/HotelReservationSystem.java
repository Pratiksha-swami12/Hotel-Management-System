package org.simple.Hotel_management;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;



public class HotelReservationSystem {
private static final String url = "jdbc:mysql://localhost:3306/hotel_db";
private static final String username = "root";
private static final String password = "YourPassward";

public static void main(String[] args)throws ClassNotFoundException, SQLException {
	try {
		Class.forName("com.mysql.cj.jdbc.Driver");

	}
	catch(ClassNotFoundException e) {
		System.out.println(e.getMessage());
	}
	
	try {
		Connection connection =DriverManager.getConnection(url, username, password);
		while(true) {
			System.out.println();
			System.out.println("-- HOTEL MANAGEMENT SYSTEM ---");
			Scanner scanner=new Scanner(System.in);
			System.out.println("1. Reserve a room");
			System.out.println("2. View Reservations");
			System.out.println("3. Get Room Number");
			System.out.println("4. Update Reservations");
			System.out.println("5. Delete Reservations");
			System.out.println("0. Exit");
			System.out.println("Choose an option: ");
			int choice=scanner.nextInt();
			switch(choice) {
			case 1: 
				reserveRoom(connection, scanner);
				break;
			case 2:
				viewReservations(connection);
				break;
			case 3:
				getRoomNumber(connection,scanner);
				break;
			case 4:
				updateReservation(connection,scanner);
                break;
			case 5:
				deleteReservation(connection,scanner);
				break;
			case 0:
				exit();
				scanner.close();
				return;
				default:
					System.out.println("Invalid choice. Try again.");
	}
			
		}
		
		}catch(SQLException e) {
		System.out.println(e.getMessage());
	     } 
	catch(InterruptedException e) {
		throw new RuntimeException(e);
	}
	
}
//*************************************************************************
private static void reserveRoom(Connection connection,Scanner scanner) {
	try {
		scanner.nextLine();
		System.out.println("enter guest name: ");
		String guestName=scanner.nextLine();
		
		System.out.println("Enter room number: ");
		int roomNumber=scanner.nextInt();
		  if (!isRoomAvailable(connection, roomNumber)) {
	            System.out.println("❌ Room already booked. Try another room.");
	            return;
	        }
		System.out.println("Enter contact number: ");
		String contactNumber=scanner.next();
		
		String sql= "INSERT INTO reservations(guest_name,room_number,contact_number) VALUES (?, ?, ?)";
		
		try(PreparedStatement statement = connection.prepareStatement(sql)){
			statement.setString(1, guestName);
			statement.setInt(2, roomNumber);
			statement.setString(3, contactNumber);
			int rowInserted=statement.executeUpdate();
			if(rowInserted > 0) {
				System.out.println("Reservation successful");
			}
			else {
				System.out.println("Reservation failed");
			}
		}
			}catch(SQLException e) {
	e.printStackTrace();	
	}
	}
//***********************************************************************
private static boolean isRoomAvailable(Connection connection, int roomNumber) {
    String sql = "SELECT room_number FROM reservations WHERE room_number = ?";
    try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
        preparedStatement.setInt(1, roomNumber);
        ResultSet resultSet = preparedStatement.executeQuery();

        return !resultSet.next(); // if no result → room is free
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}
//*********************************************************************************
private static void getRoomNumber(Connection connection, Scanner scanner) {
    try {
        System.out.print("Enter reservation ID: ");
        int reservationId = scanner.nextInt();
        System.out.print("Enter guest name: ");
        String guestName = scanner.next();

        String sql = "SELECT room_number FROM reservations WHERE reservation_id=? AND guest_name=?";

        try (PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setInt(1, reservationId);
        	statement.setString(2, guestName);
        		
        ResultSet resultSet = statement.executeQuery(sql);

            if (resultSet.next()) {
                int roomNumber = resultSet.getInt("room_number");
               System.out.println("Room number: " + roomNumber);
            } else {
                System.out.println("Reservation not found for the given ID and guest name.");
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}
//******************************************************************************************
private static void updateReservation(Connection connection, Scanner scanner) {
    try {
        System.out.print("Enter reservation ID to update: ");
        int reservationId = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character

        if (!reservationExists(connection, reservationId)) {
            System.out.println("Reservation not found for the given ID.");
            return;
        }

        System.out.print("Enter new guest name: ");
        String newGuestName = scanner.nextLine();
        System.out.print("Enter new room number: ");
        int newRoomNumber = scanner.nextInt();
        if (!isRoomAvailable(connection, newRoomNumber)) {
            System.out.println("❌ Room already booked. Choose another room.");
            return;
        }
        System.out.print("Enter new contact number: ");
        String newContactNumber = scanner.next();

        String sql = "UPDATE reservations SET guest_name = ?, room_number = ?, contact_number = ? WHERE reservation_id = ? ";

        try (PreparedStatement statement=connection.prepareStatement(sql)) {
            statement.setString(1, newGuestName);
            statement.setInt(2, newRoomNumber);
            statement.setString(3, newContactNumber);
            statement.setInt(4, reservationId);
           int updated= statement.executeUpdate();
            

            if (updated > 0) {
                System.out.println("Reservation updated successfully!");
            } else {
                System.out.println("Reservation update failed.");
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}
//**********************************************************************************
private static void deleteReservation(Connection connection, Scanner scanner) {
    try {
        System.out.print("Enter reservation ID to delete: ");
        int reservationId = scanner.nextInt();

        if (!reservationExists(connection, reservationId)) {
            System.out.println("Reservation not found for the given ID.");
            return;
        }

        String sql = "DELETE FROM reservations WHERE reservation_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
           statement.setInt(1, reservationId);
           int deleted=statement.executeUpdate();
           if(deleted > 0) {
        	   System.out.println("Reservation deleted successfully");
           }
           else {
        	   System.out.println("Deletion failed");
           }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}
//**************************************************************************
private static boolean reservationExists(Connection connection, int reservationId) {
        String sql = "SELECT reservation_id FROM reservations WHERE reservation_id = ?";
try(PreparedStatement statement= connection.prepareStatement(sql)){
    	statement.setInt(1, reservationId);
    	ResultSet resultSet= statement.executeQuery();
    	return resultSet.next();
      
    } catch (SQLException e) {
        e.printStackTrace();
        return false; // Handle database errors as needed
    }
}
//************************************************************************************
public static void exit() throws InterruptedException {
    System.out.print("Exiting System");
    int i = 5;
    while(i!=0){
        System.out.print(".");
        Thread.sleep(1000);
        i--;
    }
    System.out.println();
    System.out.println("ThankYou For Using Hotel Reservation System!!!");
}
//*******************************************************************************************
private static void viewReservations(Connection connection) throws SQLException {
    String sql = "SELECT reservation_id, guest_name, room_number, contact_number, reversation_date FROM reservations";

    try (Statement statement = connection.createStatement();
         ResultSet resultSet = statement.executeQuery(sql)) {

        System.out.println("Current Reservations:");
        System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");
        System.out.println("| Reservation ID | Guest           | Room Number   | Contact Number      | Reservation Date        |");
        System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");

        while (resultSet.next()) {
            int reservationId = resultSet.getInt("reservation_id");
            String guestName = resultSet.getString("guest_name");
            int roomNumber = resultSet.getInt("room_number");
            String contactNumber = resultSet.getString("contact_number");
            String reservationDate = resultSet.getTimestamp("reversation_date").toString();

            // Format and display the reservation data in a table-like format
            System.out.printf("| %-14d | %-15s | %-13d | %-20s | %-19s   |\n",
                    reservationId, guestName, roomNumber, contactNumber, reservationDate);
        }

        System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");
    }
}


}
