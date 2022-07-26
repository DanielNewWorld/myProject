package ua.in.nets.model;

/**
 * Created by root on 15.12.15.
 */
public class UserClass {
    public String dbTicketsID;
    public String dbSubject;
    public String dbCreated;
    public String dbUpdated;
    public String dbDepartment;
    public String dbStatus;
    public String dbPriority;
    public int dbID;

    UserClass(String ticketsID, String subject, String created, String updates, String department, String status, String priority, int dbid) {
        this.dbTicketsID = ticketsID;
        this.dbSubject = subject;
        this.dbCreated = created;
        this.dbUpdated= updates;
        this.dbDepartment= department;
        this.dbStatus= status;
        this.dbPriority= priority;
        this.dbID = dbid;
    }
}
