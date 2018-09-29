package muheeb.com.colcom20;

/**
 * Created by muheeb on 16-Feb-17.
 */

public class HelpingClass
{
    String sender, reciever;

    public HelpingClass(String sender, String reciever)
    {
        this.sender  = sender;
        this.reciever = reciever;
    }

    public String getSender()
    {
        return sender;
    }

    public String getReciever()
    {
        return reciever;
    }
}
