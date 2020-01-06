import java.io.File;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Database {

    private static Connection con;
    private static Statement stmt;

    public static void init() throws Exception {
        Class.forName("org.h2.Driver").newInstance();
        con = DriverManager.getConnection("jdbc:h2:"+"./database/data", "sa", "sa");
        stmt = con.createStatement();
        String sql =  "CREATE TABLE IF NOT EXISTS timer " +
                "(id BIGINT not NULL, " +
                " user_id BIGINT not null , " +
                " chat_id BIGINT  , " +
                " edited_at TIMESTAMP , " +
                " in_step INTEGER , " +
                " upload_file varchar(255), " +
                " timer_min INTEGER , " +
                " active BOOLEAN , " +
                " last_message text , " +
                " PRIMARY KEY ( user_id ))";
        stmt.executeUpdate(sql);
        System.out.println("Created table in given database...");
        showall();


    }

    public static Boolean isExistUserId(Long user_id) throws Exception{
        showall();
        String sql = "SELECT * FROM timer where user_id="+user_id;

        ResultSet set = null;

        try {
            set = stmt.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (set!=null){

            while (set.next()) {
                String id = set.getString("user_id");
                System.out.println(id);
                return true;
            }

        }


        return false;
    }

    public static void showall() throws SQLException {
        String sql = "SELECT * FROM timer";

        ResultSet set = null;


        try {
            set = stmt.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (set!=null){

            while (set.next()) {
                String id = set.getString("user_id");
                String in_step = set.getString("in_step");
                String edited_at = set.getString("edited_at");
                String upload_file = set.getString("upload_file");
                String timer_min = set.getString("timer_min");
                String active = set.getString("active");
                String last_message = set.getString("last_message");

                System.out.println(id + in_step + edited_at + upload_file + timer_min + active + last_message);
            }

        }
    }

    public static Boolean status(Long user_id) throws SQLException {


        String sql = "SELECT * FROM timer WHERE user_id="+user_id;

        ResultSet set = null;


        try {
            set = stmt.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (set!=null){

            while (set.next()) {

                String active = set.getString("active");

                if (active.equals("TRUE"))
                    return true;

            }

        }

        return false;

    }

    public static boolean run(Long user_id, Long chat_id) throws Exception {
        showall();
        if (isExistUserId(user_id)) {
            String sql = "update timer " +
                    "set active=true , " +
                    "edited_at = CURRENT_TIMESTAMP()," +
                    " chat_id="+ chat_id +
                    "where user_id ="+user_id;

            Boolean b = stmt.execute(sql);
            return false;
        }
        String sql = "insert into timer(id, user_id, in_step, active, chat_id)" +
                "values ("+user_id+","+user_id+", 1"+", true,"+chat_id+")";

        Boolean b = stmt.execute(sql);

        System.out.println(b);

        return true;
    }

    public static Boolean isExistFile(Long user_id) throws Exception {
        if (!isExistUserId(user_id)){
            return false;
        }

        String sql = "SELECT * FROM timer WHERE user_id="+user_id;

        ResultSet set = null;


        try {
            set = stmt.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (set!=null){

            while (set.next()) {

                String upload_file = set.getString("upload_file");
                System.out.println(upload_file);
                if (upload_file!=null)
                return true;

            }

        }

        return false;
    }

    public static String getTtl(Long user_id) throws Exception {

        if (!isExistUserId(user_id)){
            return "Your profile is not active, type command /run to start.";
        }

        String sql = "SELECT * FROM timer WHERE user_id="+user_id;

        ResultSet set = null;




        try {
            set = stmt.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (set!=null){

            while (set.next()) {

                String timer_min = set.getString("timer_min");
                System.out.println(timer_min);
                if (timer_min!=null)
                    return "Your timer is setted in "+timer_min+ " minutes.";

            }

        }

        return "You timer minute is null, to set timer please type \'/set X\'";

    }


    public static String set(Integer mn, Long user_id) throws Exception {
        if (!isExistUserId(user_id)){
            return "Your profile is not active, type command /run to start.";
        }

        String sql = "update timer " +
                "set timer_min="+ mn +
                ", edited_at = CURRENT_TIMESTAMP ()"+
                "where user_id ="+user_id;

        stmt.execute(sql);
        if (isExistFile(user_id))
            return "Now every "+mn+" minute you will get message.";
        else
            return "Now every "+mn+" minute you will get message. And you need to upload your txt file.";
    }


    public static String setFile(String path, Long user_id) throws Exception {
        if (!isExistUserId(user_id)) {
          return "Your profile is not active, /run to start.";
        }

        String sql = "update timer " +
                "set upload_file=\'"+path+"\'" +
                "where user_id ="+user_id;

        stmt.execute(sql);
        return "Your file is uploaded.";
    }

    public static String getLine(Long user_id) throws Exception {

        if (!isExistUserId(user_id)){
            return "Your profile is not active, type command /run to start.";
        }

        String sql = "SELECT * FROM timer WHERE user_id="+user_id;

        ResultSet set = null;




        try {
            set = stmt.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (set!=null){

            while (set.next()) {

                String upload_file = set.getString("upload_file");
                System.out.println(upload_file);
                if (upload_file!=null)
                    return FileLine.getRandomLine(upload_file);

            }

        }
        return "";

    }


    public static String getLastMessage(Long user_id) throws Exception {
        if (!isExistUserId(user_id)){
            return "";
        }
        showall();
        String sql = "SELECT * FROM timer WHERE user_id="+user_id;

        ResultSet set = null;


        try {
            set = stmt.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (set!=null){

            while (set.next()) {

                String last_message = set.getString("last_message");
                System.out.println(last_message);
                return last_message;

            }

        }

        return "";

    }

    public static String setLastMessage(String last_message, Long user_id) throws Exception {
        showall();
        if (!isExistUserId(user_id)){
            return "";
        }

        String sql = "update timer " +
                "set last_message=\'"+last_message+"\'" +
                "where user_id ="+user_id;
        System.out.println(sql);
        stmt.execute(sql);
        return "";
    }

    public static void setTime(Long user_id) throws SQLException {
        String sql = "update timer " +
                "set edited_at = CURRENT_TIMESTAMP ()"+
                "where user_id ="+user_id;

        stmt.execute(sql);

    }

    public static void setInActive(Long user_id) throws SQLException {
        String sql = "update timer " +
                "set active = false "+
                "where user_id ="+user_id;

        stmt.execute(sql);
    }

    public static List<Tim> gett() throws SQLException {

        String sql = "SELECT *, (EXTRACT(EPOCH FROM (CURRENT_TIMESTAMP() - t_a.edited_at))/60) as difmin FROM timer t_a " +
                "where timer_min <= EXTRACT(EPOCH FROM (CURRENT_TIMESTAMP() - t_a.edited_at))/60" +
                " and active=true";

        ResultSet set = null;

        List<Tim> tims = new ArrayList<>();
        try {
            set = stmt.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (set!=null){

            while (set.next()) {

                Long id = Long.valueOf(set.getString("user_id"));
                Long chat_id = Long.valueOf(set.getString("chat_id"));
                String path = set.getString("upload_file");

                tims.add(new Tim(chat_id, id, path));
            }

        }


        return tims;
    }





}
