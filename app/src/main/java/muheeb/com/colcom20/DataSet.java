package muheeb.com.colcom20;

/**
 * Created by muheeb on 16-Feb-17.
 */

public class DataSet
{

    String name,message;
    String date;
    Boolean isMine;

    public DataSet(String name, String message, String date, Boolean isMine) {
        this.name = name;
        this.isMine = isMine;
        this.message = message;
        this.date = date;
    }
    public DataSet()
    {

    }



    public Boolean isMine() {
        return isMine;
    }


    public String getMessage() {
        return message;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }
}

