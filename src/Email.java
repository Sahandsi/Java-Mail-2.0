

import java.io.Serializable;

public class Email implements Serializable
{


    private String from, to, message ;
    private String attachment;
    private String emailSubject;
    private byte[] attachmentFile;
    private int userID;

    public Email(int userID, String from, String to,String emailSubject ,String message,byte[] attachmentFile, String attachment)

    {
        this.userID= userID;
        this.from = from;
        this.to = to;
        this.emailSubject = emailSubject;
        this.message = message;
        this.attachment = attachment;
        this.attachmentFile = attachmentFile;

    }



    public String getFrom()
    {
        return from;
    }

    public String getTo()
    {
        return to;
    }

    public String getemailSubject()
    {
        return emailSubject;
    }

    public String getMessage()
    {
        return message;
    }

    public String getAttachment()
    {
        return attachment;
    }

    public byte[] getAttachmentFiles()
    {
        return attachmentFile;
    }

    public int getUserID() { return userID; }




}


