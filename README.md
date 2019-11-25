# HotelBooking
----------------------------------------------------------
L4 Hotel Chain Reservation System Instructions
----------------------------------------------------------

NOTE: Password for desk clerk is deskAdmin and password for supervisor is admin

1. Extract the downloaded zip and ensure that in the extracted folder you have all the source files and a folder called data (which should have sub-directories bookingInfo, dataAnalysis, hotel. 
2. In the hotels sub-directory you will find a l4Hotels.csv containing the information for each hotel and it's rooms.
  2a. If you want to add a hotel or room to the system edit this CSV file:
    To add a room to an existing hotel, find the hotel name and insert a new line (with hotel section blank) between that name and the next hotel name and fill in the room type, occupancy information as adult_occupancy+child_occupancy and then the rates for the various dates following the file headers and the format of the default rooms in the file
    To add a new hotel, simply beneath the current hotels and rooms, insert a new row with the hotel name in the hotel name section and then follow with its rooms
   2b. To remove a room or hotel:
          To remove a room, simply delete the whole line of that room
          To remove a hotel, delete the row with the hotel name and all rooms of that hotel
          
3. To run the system for the FIRST TIME:
  Open the command line with the current working directory in the root of the extracted folder 
  Type (without quotes) "javac *.java" to compile all the java files in the src folder
  
  See step 4 once compilation completed succesfully
4. To now run the system:
  Type in the command line "java L4System"
  
5. Using the application:
   a. On starting up the system you will be asked which hotel you want to run the system as, choose this hotel using the letters provided
   b. After choosing the hotel you will be asked if you want to login, change the hotel or quit(which terminates the program)
   c. If you choose login you can login as a Customer, DeskClerk or Supervisor
   d. A customer can make a reservation, cancel a reservation or view one
   e. DeskClerk and Supervisor can do the same as a customer but they can also check in and check out
   f. Supervisor can request data analysis, where all files are stored to the dataAnalysis sub-directory of the data folder.
   g. Note for deskClerk the password is: deskAdmin and the supervisor password is: admin
