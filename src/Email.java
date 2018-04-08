import java.io.Serializable;

public class Email implements Serializable
{


    private String from, to, message ,attachment;

    public Email(String from, String to, String message , String attachment)

    {
        this.from = from;
        this.to = to;
        this.message = message;
        this.attachment =attachment;
    }

    public String getFrom()
    {
        return from;
    }

    public String getTo()
    {
        return to;
    }

    public String getMessage()
    {
        return message;
    }

    public String getattachment()
    {
        return attachment;
    }


}
