public class Email // storing all mail
{
    // provide encapsulation
    private String from, to, subject, message;

    // constructor
    public Email(String from, String to, String subject, String message)
    {
        this.from = from;
        this.to = to;
        this.subject = subject;
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

    public String getSubject()
    {
        return subject;
    }

    public String getMessage()
    {
        return message;
    }


}
