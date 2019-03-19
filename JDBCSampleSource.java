import java.sql.*;
import java.util.Scanner;
import java.util.InputMismatchException;

/**
 *
 * @author Mimi Opkins with some tweaking from Dave Brown
 */
public class JDBCSampleSource {
    //  Database credentials
    static String USER;
    static String PASS;
    static String DBNAME;
    //This is the specification for the printout that I'm doing:
    //each % denotes the start of a new field.
    //The - denotes left justification.
    //The number indicates how wide to make the field.
    //The "s" denotes that it's a string.  All of our output in this test are
    //strings, but that won't always be the case.
    static final String displayFormat="%-5s%-15s%-15s%-15s\n";
    static final String oneDisplayFormat="%-15s\n";
    static final String elevendisplay="%-25s%-25s%-25s%-25s%-25s%-25s%-25s%-25s%-25s%-25s%-25s\n";


// JDBC driver name and database URL
    static final String JDBC_DRIVER = "org.apache.derby.jdbc.ClientDriver";
    static String DB_URL = "jdbc:derby://localhost:1527/";
//            + "testdb;user=";
/**
 * Takes the input string and outputs "N/A" if the string is empty or null.
 * @param input The string to be mapped.
 * @return  Either the input string or "N/A" as appropriate.
 */
    public static String dispNull (String input) {
        //because of short circuiting, if it's null, it never checks the length.
        if (input == null || input.length() == 0)
            return "N/A";
        else
            return input;
    }

    public static void main(String[] args) {
        //Prompt the user for the database name, and the credentials.
        //If your database has no credentials, you can update this code to
        //remove that from the connection string.
        Scanner in  = new Scanner(System.in);
        Scanner in2 = new Scanner(System.in);
        System.out.print("Name of the database (not the user account): ");
        DBNAME = in.nextLine();
        System.out.print("Database user name: ");
        USER = in.nextLine();
        System.out.print("Database password: ");
        PASS = in.nextLine();
        //Constructing the database URL connection string
        DB_URL = DB_URL + DBNAME + ";user="+ USER + ";password=" + PASS;
        Connection conn = null; //initialize the connection
        Statement stmt = null;  //initialize the statement that we're using
        ResultSet rs = null;
        int end = 0;
        try {
            //STEP 2: Register JDBC driver
            Class.forName("org.apache.derby.jdbc.ClientDriver");

            //STEP 3: Open a connection
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL);
                                    
            while(end == 0){
                System.out.println("---\tMENU\t---");
                System.out.println("1.\tSelect all writing groups");
                System.out.println("2.\tSelect specific group and all data"
                                 + " associated with group");
                System.out.println("3.\tSelect all publishers");
                System.out.println("4.\tSelect all data from a publisher");
                System.out.println("5.\tSelect all book titles");
                System.out.println("6.\tSelect all data for a single book");
                System.out.println("7.\tInsert a new book ");
                System.out.println("8.\tInsert a new publisher");
                System.out.println("9.\tRemove a single book");
                System.out.println("10.\tQuit");

                System.out.print("Enter a menu option: ");
                
                while(!in.hasNextInt()){
                    in.next();
                    System.out.print("Enter a VALID menu option (1-10): ");
                }               
                int userChoice = in.nextInt();

                
                if(userChoice == 1){
                    /**
                     * Displays all writing groups in database
                     */

                    System.out.println("Selecting all writing groups...");

                    stmt = conn.createStatement();
                    String sql;

                    sql = "SELECT group_name FROM writing_groups";
                    rs = stmt.executeQuery(sql);
                    if (!rs.next()){
                        System.out.println("Writing groups table in database empty");
                    } else {
                        System.out.printf(oneDisplayFormat, "Group Name");
                        do {
                            String group = rs.getString("group_name");
                            System.out.printf(oneDisplayFormat, dispNull(group));   
                        } while(rs.next());
                    }

                }
                if(userChoice == 2){
                    /**
                     * Has the user enter a specific group name
                     * and then displays any groups and their
                     * data if they are found in the database
                     */
                    System.out.println("Which group would you like to see: "); // asks users for a group name
                    String groupChoice = in2.nextLine();

                    PreparedStatement q=conn.prepareStatement("SELECT * FROM writing_groups " 
                                                            + "NATURAL JOIN books NATURAL JOIN publishers "
                                                            + "WHERE group_name=?");  

                    q.setString(1,groupChoice); 

                    rs = q.executeQuery();

                    if(!rs.next()){
                        System.out.println(groupChoice + " not found in database");
                    } else {                 
                        System.out.printf(elevendisplay, "Publisher Name", "Group Name", 
                        "Head Writer", "Year Formed", "Subject", "Book Title", "Year Published", 
                        "Number Pages", "Publisher Address", "Publisher Phone", "Publisher Email");
                        do {
                            String pubname = rs.getString("publisher_name");
                            String group = rs.getString("group_name");
                            String head = rs.getString("head_writer");
                            String year = rs.getString("year_formed");
                            String subject = rs.getString("subject");
                            String book_title = rs.getString("book_title");
                            String year_pub = rs.getString("year_published");
                            String num_page = rs.getString("number_pages");
                            String pub_add = rs.getString("publisher_address");
                            String pub_phone = rs.getString("publisher_phone");
                            String pub_email = rs.getString("publisher_email");
                            System.out.printf(elevendisplay,
                                            dispNull(pubname), dispNull(group), dispNull(head), 
                                            dispNull(year), dispNull(subject), dispNull(book_title),
                                            dispNull(year_pub), dispNull(num_page), dispNull(pub_add),
                                            dispNull(pub_phone), dispNull(pub_email));
                        } while(rs.next());
                    }
                }
                if(userChoice == 3){
                    /**
                     * Displays all publishers
                     */

                    System.out.println("Selecting all publishers...");
                    stmt = conn.createStatement();

                    String sql;
                    sql = "SELECT publisher_name FROM publishers";
                    rs = stmt.executeQuery(sql);

                    if(!rs.next()){
                        System.out.println("Publishers table empty in database");
                    } else {
                        System.out.printf(oneDisplayFormat, "Publisher Name");
                        do {
                            String pub_name = rs.getString("publisher_name");
                            System.out.printf(oneDisplayFormat,
                            dispNull(pub_name));                    
                        } while(rs.next());
                    }
                }
                if(userChoice == 4){
                    System.out.print("Which publisher would you like to see: ");
                    String publisherPicked = in2.nextLine();


                    PreparedStatement q=conn.prepareStatement("SELECT * FROM publishers "
                             + "NATURAL JOIN books NATURAL JOIN writing_groups WHERE publisher_name=?"); 

                    q.setString(1,publisherPicked);  
                    rs = q.executeQuery();

                    if (rs.next() == false){
                        System.out.println("The publisher "+ publisherPicked +" does not exist." );
                    } else {
                        System.out.printf(elevendisplay, "Book Title", "Pages", "Year Published", "Publisher","Publisher Adress","Publisher phone","Publisher Email","Group","Head Writer","Year formed", "Subject");
                        do {
                            String title = rs.getString("book_title");
                            String np = rs.getString("number_pages");
                            String yp = rs.getString("year_published");
                            String p = rs.getString("publisher_name");
                            String pa = rs.getString("publisher_address");
                            String pp = rs.getString("publisher_phone");
                            String pe = rs.getString("publisher_email");
                            String g = rs.getString("group_name");
                            String hw = rs.getString("head_writer");
                            String y = rs.getString("year_formed");
                            String s = rs.getString("subject");

                            System.out.printf(elevendisplay,
                            dispNull(title),dispNull(np),dispNull(yp),dispNull(p),dispNull(pa),dispNull(pp),dispNull(pe),dispNull(g),dispNull(hw),dispNull(y),dispNull(s));
                        } while(rs.next());
                    }
                }  
                if(userChoice == 5){
                    System.out.println("Selecting all book titles...");

                    stmt = conn.createStatement();
                    String sql;
                    sql = "SELECT book_title FROM books";
                    rs = stmt.executeQuery(sql);
                    System.out.printf(oneDisplayFormat, "Book Title");

                    while (rs.next()) {
                        String title = rs.getString("book_title");
                //Display values
                        System.out.printf(oneDisplayFormat,
                        dispNull(title));
                    } 
                }
                if(userChoice == 6){
                    System.out.println("For the book you would to see\nWhat is the title: ");
                    String bookPicked = in2.nextLine();
                    System.out.println("What is the name of the writing group: ");
                    String bookGroup = in2.nextLine();

                    PreparedStatement q=conn.prepareStatement("SELECT * FROM books NATURAL "
                            + "JOIN publishers NATURAL JOIN writing_groups WHERE book_title=? and group_name =?");  
                    q.setString(1,bookPicked);  
                    q.setString(2,bookGroup);
                    
                    rs = q.executeQuery();
                    if (!rs.next()){
                        System.out.println("The book "+ bookPicked +" with the group "+ bookGroup + " does not exist." );
                    } else {
                        System.out.printf(elevendisplay, "Publisher Name", "Group Name", 
                        "Head Writer", "Year Formed", "Subject", "Book Title", "Year Published", 
                        "Number Pages", "Publisher Address", "Publisher Phone", "Publisher Email");
                        do {
                            String pubname = rs.getString("publisher_name");
                            String group = rs.getString("group_name");
                            String head = rs.getString("head_writer");
                            String year = rs.getString("year_formed");
                            String subject = rs.getString("subject");
                            String book_title = rs.getString("book_title");
                            String year_pub = rs.getString("year_published");
                            String num_page = rs.getString("number_pages");
                            String pub_add = rs.getString("publisher_address");
                            String pub_phone = rs.getString("publisher_phone");
                            String pub_email = rs.getString("publisher_email");
                            System.out.printf(elevendisplay,
                                            dispNull(pubname), dispNull(group), dispNull(head), 
                                            dispNull(year), dispNull(subject), dispNull(book_title),
                                            dispNull(year_pub), dispNull(num_page), dispNull(pub_add),
                                            dispNull(pub_phone), dispNull(pub_email));
                        } while (rs.next());
                    }
                }
               if (userChoice == 7) // USER ADD NEW BOOK 
                {
                    String groupName, bookTitle, publisherName;

                    System.out.println("Please enter the Book's group name: ");
                    groupName = in2.nextLine();
                    System.out.println("Please enter the Book's title: ");
                    bookTitle = in2.nextLine();
                    System.out.println("Please enter the Book's publisher's name: ");
                    publisherName = in2.nextLine();
                    System.out.println("Please enter the year that the book was published: ");
                    
                    while(!in2.hasNextInt()){
                        in2.next();
                        System.out.println("Please only enter a number: ");
                    }
                    int yearPublished = in2.nextInt();

                    while (yearPublished > 2019 || yearPublished <= 0)
                    {
                        System.out.println("That is an invalid year try again ");
                        System.out.println("Please enter the year that the book was published: ");
                        yearPublished = in2.nextInt();
                    }

                    System.out.println("Please enter the number of pages the book has: ");
                    
                    while(!in2.hasNextInt()){
                        in2.next();
                        System.out.println("Please only enter a number: ");
                    }
                    int numberPages = in2.nextInt();
                    
                    String query;
                    query = "Insert into books (group_name, book_title, publisher_name, year_published, number_pages)"
                                + "values(?,?,?,?,?)";   
                    String query1 = "Select * from writing_groups where group_name = ? ";
                    String query2 = "Select * from publishers where publisher_name = ? ";

                    PreparedStatement findGroup = conn.prepareStatement(query1);
                    PreparedStatement findPublish = conn.prepareStatement(query2);

                    findGroup.setString(1, groupName);
                    findPublish.setString(1, publisherName);

                    ResultSet rs1 = findGroup.executeQuery();
                    ResultSet rs2 = findPublish.executeQuery();

                    while(!rs1.next()) // while the group doesnt exist
                    {
                        System.out.println("That writing group doesn't exist");
                        System.out.println("Reenter the Book's Group Name: ");
                        groupName = in2.nextLine();
                        findGroup.setString(1, groupName);
                        rs1 = findGroup.executeQuery();
                    }

                    while(!rs2.next()) // while the publisher doesnt exist
                    {
                        System.out.println("That publisher doesn't exist");
                        System.out.println("Reenter the Publisher Name: ");
                        publisherName = in2.nextLine();
                        findPublish.setString(1, publisherName);
                        rs2 = findPublish.executeQuery();
                    }
                    
                    try{
                            PreparedStatement newBook = conn.prepareStatement(query);
                            newBook.setString(1, groupName);
                            newBook.setString(2,bookTitle);
                            newBook.setString(3, publisherName);
                            newBook.setInt(4, yearPublished);
                            newBook.setInt(5,numberPages);
                            newBook.execute();
                        }catch(SQLIntegrityConstraintViolationException e){
                            System.out.println("This book already exists in our system");
                        }
                }
                if (userChoice == 8) // USER ADD NEW PUBLISH
                {
                    System.out.println("Enter the name of the new publisher: ");
                    String newPublisher = in2.nextLine();

                    System.out.println("Enter the name of the old publisher: ");
                    String oldPublisher = in2.nextLine();

                    String query = "Insert into publishers (publisher_name) values(?)"; 
                    String query2 = "Update books SET publisher_name = ? where publisher_name = ?";
                    String query3 = "Select * from publishers where publisher_name = ?";
                    String query4 = "Select * from publishers where publisher_name = ?";

                    PreparedStatement findOld = conn.prepareStatement(query3);
                    PreparedStatement findNew = conn.prepareStatement(query4);                    
                    PreparedStatement addPublisher = conn.prepareStatement(query);
                    PreparedStatement updatePublisher = conn.prepareStatement(query2);

                    findOld.setString(1, oldPublisher);
                    findNew.setString(1, newPublisher);

                    ResultSet rs1 = findOld.executeQuery();
                    ResultSet rs2 = findNew.executeQuery();

                    while (!rs1.next())
                    {
                        // While the old publisher doesn't exist have the user
                        // enter old publishers until one exists
                        System.out.println("The old publisher, " + oldPublisher+ ", does not exist in our database");
                        System.out.println("Enter the old publisher: ");
                        oldPublisher = in2.nextLine();
                        findOld.setString(1, oldPublisher);
                        rs1 = findOld.executeQuery();
                    }

                    while(rs2.next()) // if the new publisher already exists
                    {
                        System.out.println("The new publisher, " + newPublisher + ", already exists");
                        System.out.println("Enter the new publisher: ");
                        newPublisher = in2.nextLine();
                        findNew.setString(1, newPublisher);
                        rs2 = findNew.executeQuery();
                    }

                    // Adds and updates the new publisher
                    addPublisher.setString(1, newPublisher);
                    updatePublisher.setString(1, newPublisher);
                    updatePublisher.setString(2, oldPublisher);
                    addPublisher.execute();
                    updatePublisher.execute();
                    System.out.println("");
                    
                    // Display all new books where publisher is changed
                    stmt = conn.createStatement();
                    String sql;
                    
                    sql = "SELECT * FROM publishers NATURAL JOIN books NATURAL JOIN writing_groups WHERE publisher_name = '" + newPublisher + "'"; 
                    
                    rs = stmt.executeQuery(sql);
                    
                    if (!rs.next()){
                        System.out.println("No books found with " + newPublisher);
                    } else {
                        System.out.printf(elevendisplay, "Publisher Name", "Group Name", 
                        "Head Writer", "Year Formed", "Subject", "Book Title", "Year Published", 
                        "Number Pages", "Publisher Address", "Publisher Phone", "Publisher Email");
                        do {
                            String pubname = rs.getString("publisher_name");
                            String group = rs.getString("group_name");
                            String head = rs.getString("head_writer");
                            String year = rs.getString("year_formed");
                            String subject = rs.getString("subject");
                            String book_title = rs.getString("book_title");
                            String year_pub = rs.getString("year_published");
                            String num_page = rs.getString("number_pages");
                            String pub_add = rs.getString("publisher_address");
                            String pub_phone = rs.getString("publisher_phone");
                            String pub_email = rs.getString("publisher_email");
                            System.out.printf(elevendisplay,
                                            dispNull(pubname), dispNull(group), dispNull(head), 
                                            dispNull(year), dispNull(subject), dispNull(book_title),
                                            dispNull(year_pub), dispNull(num_page), dispNull(pub_add),
                                            dispNull(pub_phone), dispNull(pub_email));
                        } while (rs.next());
                    }
                    
                    //Closes both result sets
                    rs1.close();
                    rs2.close();
                }
                if (userChoice == 9) // USER REMOVE BOOK
                {
                    System.out.println("Enter the book you want to delete: ");
                    String bookDelete = in2.nextLine();

                    System.out.println("Enter the book's group name: ");
                    String groupDelete = in2.nextLine();

                    String query = "delete from books where book_title = ? and group_name = ?";
                    String query1 = "select * from books where book_title = ? and group_name = ?";

                    PreparedStatement findBook = conn.prepareStatement(query1);
                    PreparedStatement deleteBook = conn.prepareStatement(query);

                    findBook.setString(1, bookDelete);
                    findBook.setString(2, groupDelete);

                    ResultSet rs1 = findBook.executeQuery();

                    while(!rs1.next()) // That book doesn't exist with the info provided
                    {
                        System.out.println("The book you are requesting doesn't exist");
                        System.out.print("Reenter the Book title: ");
                        bookDelete = in2.nextLine();
                        System.out.print("Reenter the Book's group name: ");
                        groupDelete = in2.nextLine();
                        findBook.setString(1, bookDelete);
                        findBook.setString(2, groupDelete);
                        rs1 = findBook.executeQuery();
                    }
                    
                    System.out.println("Deleting " + bookDelete);
                    
                    deleteBook.setString(1,bookDelete);
                    deleteBook.setString(2, groupDelete);
                    deleteBook.execute();
                    
                    System.out.println("Selecting all book titles...");

                    stmt = conn.createStatement();
                    String sql;
                    sql = "SELECT book_title FROM books";
                    rs = stmt.executeQuery(sql);
                    System.out.printf(oneDisplayFormat, "Book Title");

                    while (rs.next()) {
                        String title = rs.getString("book_title");
                //Display values
                        System.out.printf(oneDisplayFormat,
                        dispNull(title));
                    }
                    
                    rs1.close();
                }
                if(userChoice == 10){
                    System.out.println("Quitting...");
                    rs.close();
                    stmt.close();
                    conn.close();
                    end = 1;
                }
                if(userChoice < 1 || userChoice > 10){
                    System.out.println("Invalid menu option");
                }
            }
        } catch (SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
        } catch (Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
        } finally {
            //finally block used to close resources
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException se2) {
            }// nothing we can do
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }//end finally try
        }//end try
        System.out.println("Goodbye!");
    }//end main
}//end FirstExample}

