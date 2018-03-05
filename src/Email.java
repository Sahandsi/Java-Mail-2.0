import java.io.Serializable;

public class Email implements Serializable {

    // provide encapsulation
    private String from, to, message;

    // constructor
    public Email(String from, String to, String message)
    {
        this.from = from;
        this.to = to;
        this.message = message;
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



}
